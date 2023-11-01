import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.concurrent.thread

class Repositorio {
    lateinit var bd: JdbcTemplate

    fun iniciar(){
        bd = Conexao.bd!!
    }
    fun validarDispositivo(ip:String):Boolean{
        val tk:List<Token> = bd.query(
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
        return false

    }
    fun empresa(token:String):List<Empresa>{
        val empresa:List<Empresa> = bd.query(
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
    fun validacaoLimite(token:String):Int{
        val qtd = bd.queryForObject(
            """
                select count(c.idDispositivo) as qtdComputadores from dispositivo as c 
	                join empresa as e on fkempresa = e.idEmpresa
		                join tokens as t on fkToken = idToken
		                    where chaveAtivacao = '$token';
                """, Int::class.java)
        return qtd
    }
    fun cadastrarDispostivo(pc:Computador){
        val cadastro = bd.update(
            """
            insert into dispositivo values
            (${pc.idDispositivo}, '${pc.sistemaOperacional}', '${pc.ip}', ${pc.fkempresa})
            """
        )
        if(cadastro == 1){
            Thread.sleep(2500)
            return println("Cadastro Realizado!!\r\nReiniciando o programa.");
        }
    }
    fun computador(ip:String):List<Computador>{
        val comp:List<Computador> = bd.query("""
            select * from dispositivo
                where ip = '$ip' 
        """,
            BeanPropertyRowMapper(Computador::class.java))
        return comp
    }
    fun Usuarios(pc: Computador):List<Usuario>{
        val user:List<Usuario> = bd.query("""
            select idUsuario, u.nome, email_Corporativo as email, senha, c.nome as cargo from usuario as u
	            join empresa on u.fkEmpresa = idEmpresa 
		            join dispositivo as d on d.fkEmpresa = idEmpresa
                        join cargo as c on fkCargo = idCargo
			                where u.fkEmpresa = ${pc.fkempresa};
        """,
            BeanPropertyRowMapper(Usuario::class.java))
        return user
    }
    fun acessoLog(sistema:Sistema, pc: Computador){
        bd.execute("""
        insert into acesso(fkUsuario, fkDispositivo, fkLog) value
        (${sistema.idUser}, ${pc.idDispositivo}, 1)
        """)
    }
    fun monitoramento(dado:Double, componenteJanela:Int, pc:Computador){
        bd.update(
            """
            insert into monitoramento(dadoCapturado,fkComponente,fkDispositivo) values
            ($dado, $componenteJanela, ${pc.idDispositivo})
            """
        )
    }
    fun verificarPlano(pc:Computador):List<Permissao>{
        val plan:List<Permissao> = bd.query("""
            select tc.nome, permissao from monitorar
	            join TipoComponente as tc on fkTipoComponente = idTipoComponente
		            join plano on monitorar.fkPlano = idPlano
			            join empresa on empresa.fkPlano = idPlano
                            join dispositivo on dispositivo.fkEmpresa = idEmpresa
                                where fkEmpresa = ${pc.fkempresa}
                                    and tc.idTipoComponente > 3;

        """,
            BeanPropertyRowMapper(Permissao::class.java))
        return plan
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
    fun ocorrencia(dado:String ,pc:Computador){
        val AppAberto = bd.queryForObject(
            """
                select idProcesso from processosBloqueados where nome = '$dado' and fkEmpresa = ${pc.fkempresa}
                """, Int::class.java)
        bd.update(
            """
            insert into ocorrencias(fkProcesso,fkDispositivo) values
            ($AppAberto, ${pc.idDispositivo})
            """
        )
    }
}
