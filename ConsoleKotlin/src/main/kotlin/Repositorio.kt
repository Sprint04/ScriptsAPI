import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.lang.RuntimeException

class Repositorio {
    lateinit var server: JdbcTemplate
    lateinit var bd: JdbcTemplate

    fun iniciar(){
        server = SLQserver.bd!!
        bd = Conexao.bd!!
    }
    fun validarDispositivo(ip:String):Boolean{
        lateinit var tk:List<Token>
        try {
            tk = server.query(
                """
        select chaveAtivacao as token from dispositivo 
            join empresa on fkempresa = empresa.idEmpresa
                join tokens on fkToken = idToken
                    where ip = '$ip'
        """,
                BeanPropertyRowMapper(Token::class.java)
            )
            if (tk.isNotEmpty()){

                return true
            }
            throw RuntimeException()
        }catch(exception:Exception) {
            tk = bd.query(
                """
        select chaveAtivacao as token from dispositivo 
            join empresa on fkempresa = empresa.idEmpresa
                join tokens on fkToken = idToken
                    where ip = '$ip'
        """,
                BeanPropertyRowMapper(Token::class.java)
            )
        }

        if (tk.isNotEmpty()){

            return true
        }
        return false

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
            val empresa: List<Empresa> = bd.query(
                """
            select idEmpresa as id, empresa.nome, chaveAtivacao as token, limite, fkPlano from empresa
                join tokens on fkToken = idToken
                    join plano on fkPlano = idPlano
                        where chaveAtivacao = '$token' 
                        """,
                BeanPropertyRowMapper(Empresa::class.java)
            )
            return empresa
        }
    }
    fun validacaoLimite(token:String):Int{
        try {
            val qtd = server.queryForObject(
                """
                select count(c.idDispositivo) as qtdComputadores from dispositivo as c 
	                join empresa as e on fkempresa = e.idEmpresa
		                join tokens as t on fkToken = idToken
		                    where chaveAtivacao = '$token';
                """, Int::class.java
            )
            return try{qtd.toInt()} catch(exception:Exception){0}
        } catch(exception:Exception){
            val qtd = bd.queryForObject(
                """
                select count(c.idDispositivo) as qtdComputadores from dispositivo as c 
	                join empresa as e on fkempresa = e.idEmpresa
		                join tokens as t on fkToken = idToken
		                    where chaveAtivacao = '$token';
                """, Int::class.java
            )
            return try{qtd.toInt()} catch(exception:Exception){0}
        }
    }
    fun cadastrarDispostivo(pc:Computador){
        try {
            val cadastro = server.update(
                """
            insert into dispositivo values
            (${pc.idDispositivo}, '${pc.sistemaOperacional}', '${pc.ip}', ${pc.fkempresa})
            """
            )
            if (cadastro == 1) {
                Thread.sleep(2500)
                return println("Cadastro Realizado!!\r\nReiniciando o programa.");
            }
        } catch (exception:Exception){
            val cadastro = bd.update(
                """
            insert into dispositivo values
            (${pc.idDispositivo}, '${pc.sistemaOperacional}', '${pc.ip}', ${pc.fkempresa})
            """
            )
            if (cadastro == 1) {
                Thread.sleep(2500)
                return println("Cadastro Realizado!!\r\nReiniciando o programa.");
            }
        }
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
    fun Usuarios(pc: Computador):List<Usuario>{
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
        } catch (exception:Exception){
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
        }
    }
    fun acessoLog(sistema:Sistema, pc: Computador){
        try {
            server.execute(
                """
            insert into acesso(fkUsuario, fkDispositivo, fkLog) value
            (${sistema.idUser}, ${pc.idDispositivo}, 1)
            """
            )
        } catch (exception:Exception) {
            bd.execute(
                """
            insert into acesso(fkUsuario, fkDispositivo, fkLog) value
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
            val plan: List<Permissao> = bd.query(
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
            return plan
        } catch(exception:Exception){
            val plan: List<Permissao> = bd.query(
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
            return plan
        }
    }
//    fun verificarPlano(pc: Computador):MutableList<Permissao2>{
//        val permissoes = mutableListOf<Permissao2>()
//        var i = 0
//        while (i < 3){
//            val permissao = Permissao2()
//            permissoes += permissao
//            i++
//        }
//        permissoes[0].nome = "USB"
//        permissoes[1].nome = "JanelasAbertas"
//        permissoes[2].nome = "Rede"
//
//        permissoes.forEach{
//            val permis = bd.queryForObject(
//                """
//                select permissao from monitorar
//	            join TipoComponente as tc on fkTipoComponente = idTipoComponente
//		            join plano on monitorar.fkPlano = idPlano
//			            join empresa on empresa.fkPlano = idPlano
//                            join dispositivo on dispositivo.fkEmpresa = idEmpresa
//                                where fkEmpresa = ${pc.fkempresa}
//                                    and tc.nome = '${it.nome}';
//                """, Int::class.java)
//            if (permis == 1) it.permissao = true
//        }
//        return permissoes
//    }
}
