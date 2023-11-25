import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate

class Repositorio {
    lateinit var server: JdbcTemplate
    lateinit var bd: JdbcTemplate

    fun iniciar(){
        server = SQLserver.bd!!
        bd = Conexao.bd!!
    }
    fun validarDispositivo(ip: String): Boolean {
        return try {
            val token: Token? = server.queryForObject(
                """
            SELECT chaveAtivacao AS token FROM dispositivo 
            JOIN empresa ON fkempresa = empresa.idEmpresa
            JOIN tokens ON fkToken = idToken
            WHERE ip = '$ip'
            """,Token::class.java
            )
            token != null
        } catch (exception: Exception) {
            false
        }
    }

    fun empresa(token:String):List<Empresa>{
        try {
            val empresa: List<Empresa> = server.query(
                """
            select idEmpresa as id, empresa.nome, chaveAtivacao as token, limite, fkPlano from empresa
                join tokens on fkToken = idToken
                    join plano on fkPlano = idPlano
                        where chaveAtivacao = '$token' 
                        """,
                BeanPropertyRowMapper(Empresa::class.java)
            )
            return empresa
        }catch(exception:Exception) {

            val empresa = Empresa()
            return mutableListOf(empresa)
        }
    }
    fun validacaoLimite(token:String):Int{
            val qtd = server.queryForObject(
                """
                select count(c.idDispositivo) as qtdComputadores from dispositivo as c 
	                join empresa as e on fkempresa = e.idEmpresa
		                join tokens as t on fkToken = idToken
		                    where chaveAtivacao = '$token';
                """, Int::class.java
            )
            return try{qtd.toInt()} catch(exception:Exception){0}
    }
    fun cadastrarDispostivo(pc:Computador):Boolean{
        try {
            val cadastro = server.update(
                """
            insert into dispositivo(sistema_Operacional,IP,fkEmpresa) values
            ('${pc.sistemaOperacional}', '${pc.ip}', ${pc.fkempresa})
            """
            )
            bd.update(
                """
            insert into dispositivo(sistema_Operacional,IP,fkEmpresa) values
            ('${pc.sistemaOperacional}', '${pc.ip}', ${pc.fkempresa})
            """
            )
            if (cadastro == 1) {
                Thread.sleep(2500)
                return true
            }
        } catch (exception:Exception){
           return false
        }
        return false
    }
    fun computador(ip:String):List<Computador>{
        try {
            val comp: List<Computador> = server.query(
                """
            select * from dispositivo
                where ip = '$ip' 
        """,
                BeanPropertyRowMapper(Computador::class.java)
            )
            return comp
        } catch(exception:Exception){
            val comp: List<Computador> = bd.query(
                """
            select * from dispositivo
                where ip = '$ip' 
        """,
                BeanPropertyRowMapper(Computador::class.java)
            )
            return comp
        }
    }
    fun usuarios(pc: Computador):List<Usuario>{
        try {
            val user: List<Usuario> = bd.query(
                """
            select idUsuario, u.nome, email_Corporativo as email, senha, c.nome as cargo from usuario as u
	            join empresa on u.fkEmpresa = idEmpresa 
		            join dispositivo as d on d.fkEmpresa = idEmpresa
                        join cargo as c on fkCargo = idCargo
			                where u.fkEmpresa = ${pc.fkempresa};
        """,
                BeanPropertyRowMapper(Usuario::class.java)
            )
            return user
        } catch (exception: Exception) {
            val ADM = Usuario()
            ADM.email = "admuser000@permit.config"
            ADM.senha = "0000"
            ADM.cargo = "Gerenciador de Sistema para login offline"
            ADM.idUsuario = 0
            return mutableListOf(ADM)
        }
    }
    fun acessoLog(sistema:Sistema, pc: Computador){
        try {
            server.execute(
                """
            insert into acesso(fkUsuario, fkDispositivo, fkLog) values
            (${sistema.idUser}, ${pc.idDispositivo}, 1)
            """
            )
        } catch (exception:Exception) {
            bd.execute(
                """
            insert into acesso(fkUsuario, fkDispositivo, fkLog) values
            (${sistema.idUser}, ${pc.idDispositivo}, 1)
            """
            )
        }
    }
    fun monitoramento(dado:Double, componenteJanela:Int, pc:Computador){
        try{
            server.update(
                """
            insert into monitoramento(dadoCapturado,fkComponente,fkDispositivo) values
            ($dado, $componenteJanela, ${pc.idDispositivo})
            """
            )
        } catch(exception:Exception) {
            println("deu errado")
            println(exception)
            bd.update(
                """
            insert into monitoramento(dadoCapturado,fkComponente,fkDispositivo) values
            ($dado, $componenteJanela, ${pc.idDispositivo})
            """
            )
        }
    }
    fun verificarPlano(pc:Computador):List<Permissao>{
        try {
            return bd.query(
                """
            select tc.nome, permissao from monitorar
	            join TipoComponente as tc on fkTipoComponente = idTipoComponente
		            join plano on monitorar.fkPlano = idPlano
			            join empresa on empresa.fkPlano = idPlano
                            join dispositivo on dispositivo.fkEmpresa = idEmpresa
                                where fkEmpresa = ${pc.fkempresa}
                                    and tc.idTipoComponente > 3;

        """,
                BeanPropertyRowMapper(Permissao::class.java)
            )
        } catch (exception: Exception) {
            val permissao1 = Permissao()
            val permissao2 = Permissao()
            val permissao3 = Permissao()
            val permissao4 = Permissao()
            val permissao5 = Permissao()
            return mutableListOf(permissao1, permissao2, permissao3, permissao4, permissao5)
        }
    }
}
