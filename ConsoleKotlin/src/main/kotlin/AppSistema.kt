import com.github.britooo.looca.api.core.Looca
import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import java.util.*


fun main() {

    val sn = Scanner(System.`in`)
    val sistema = Sistema()
    val looca = Looca()
    val dataSource = BasicDataSource()

    // classe do Driver do banco
    dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
    val serverName = "localhost"
    val mydatabase = "teste"
    dataSource.url = "jdbc:mysql://$serverName/$mydatabase"

    dataSource.username = "testes"
    dataSource.password = "12345678"

    val bd = JdbcTemplate(dataSource)

    val ip = looca.rede.parametros.hostName

    val tk:List<Token> = bd.query(
        """
                            select token from computador 
                                join empresa on fkempresa = empresa.id
                                    where ip = '$ip' 
                        """,
        BeanPropertyRowMapper(Token::class.java)
    )

    if (tk.isEmpty()) {
        val pc = Computador()
        println("Cadastre o seu computador:")
        println("Seu Token:")
        val token = sn.next()

        val empresa:List<Empresa> = bd.query(
            """
            select * from empresa where token = '$token' 
                        """,
            BeanPropertyRowMapper(Empresa::class.java)
        )

        if (empresa.isNotEmpty()) {

            val qtd:List<QtdComputadores> = bd.query(
                """
                            select count(c.id) as qtdComputadores from computador as c 
                                join empresa as e on fkempresa = e.id 
                                    where token = '$token' 
                        """,
                BeanPropertyRowMapper(QtdComputadores::class.java)
            )

            val l:List<Limite> = bd.query(
                """
                            select limite from empresa 
                                where token = '$token' 
                        """,
                BeanPropertyRowMapper(Limite::class.java)
            )

            val maxpc = l[0].limite - 1

            when (qtd[0].qtdComputadores) {
                in 0..maxpc -> {
                    println("Token Aceito!!\r\nComeçando Cadastro")
                    Thread.sleep(2000)
                    pc.id = qtd[0].qtdComputadores + 1
                    pc.sistemaOperacional = looca.sistema.sistemaOperacional
                    pc.ip = ip
                    pc.fkempresa = empresa[0].id

                    bd.execute(
                        """
                            insert into computador values
                             (${pc.id}, '${pc.sistemaOperacional}', '${pc.ip}', ${pc.fkempresa})
                        """
                    ).toString().toInt()
                    Thread.sleep(5000)
                    println("Cadastro Realizado!!\r\nPor favor, Reinicie o programa.")

                }

                l[0].limite -> println(
                    "Numero maximos de Computadores já atingido, por favor algum, " +
                            "contrate um plano melhor ou fale conosco se o numero maximo contratado ainda não foi atingido."
                )

            }


        } else {
            println("Esse token não existe")
        }

    } else {

        val comp:List<Computador> = bd.query("""
            select * from computador
                where ip = '$ip' 
        """,
            BeanPropertyRowMapper(Computador::class.java))

        val pc = comp[0]
        println("Credenciais verificadas, Iniciando programa!")
        Thread.sleep(2000)
        print("Defina a senha mestra por favor:")
        val senhaMaster = sn.next()

        while (true) {

            println(
                """Oque deseja fazer?
        |1) Login
        |2) Cadastro
        |3) Obter Informações do Usuario
        |4) Deletar Usuario
        |5) Começar monitoramento
        |6) Exit
    """.trimMargin()
            )

            var acao = (sn.next()).toInt()

            when (acao) {
                1 -> {
                    print("Digite seu email: ")
                    var email = sn.next()

                    print("Digite seu senha: ")
                    var senha = sn.next()

                    sistema.login(email, senha)

                }

                2 -> {
                    print("\n\rPor favor digite a senha mestra:")
                    var SM = sn.next()

                    if (SM == senhaMaster) {
                        print("Digite seu email: ")
                        var email = sn.next()

                        print("Digite seu senha: ")
                        var senha = sn.next()

                        print("Digite seu nome de usuario: ")
                        var usuario = sn.next()

                        sistema.cadastrar(usuario, email, senha)
                    } else {
                        println("\n\rSenha incorreta, cancelando operação!\n\r")
                    }

                }

                3 -> {
                    sistema.info()
                }

                4 -> {
                    print("\n\rPor favor digite a senha mestra:")
                    var SM = sn.next()

                    if (SM == senhaMaster) {
                        print("\n\rQue usuário gostaria de deletar?\n\r-1 - Cancelar\n\r")
                        var i = 0
                        sistema.user.forEach {
                            println("$i - $it")
                            i++
                        }
                        var del = (sn.next()).toInt()
                        if (del == -1) {
                            println("\n\rOperação cancelada!\n\r")
                        } else if (del > (sistema.user.size - 1)) {
                            println("\n\rEsse Usuario não existe!!\n\r")
                        } else {
                            sistema.delet(del)
                        }
                    } else {
                        println("\n\rSenha incorreta, cancelando operação!\n\r")
                    }
                }

                5 -> {
                    if (sistema.login) {
                        println("\n\rEstámos monitorando sua maquina.\n\r")

                        while (true) {
                            val usb = looca.dispositivosUsbGrupo.totalDispositvosUsb
                            val janela = looca.grupoDeJanelas.totalJanelas
                            val servicos = looca.grupoDeServicos.totalServicosAtivos
                            val bEnviados = looca.rede.grupoDeInterfaces.interfaces[1].bytesEnviados
                            val bRecebidos = looca.rede.grupoDeInterfaces.interfaces[1].bytesRecebidos

                            println(
                                """
                            
                            Temos um total de:
                            $usb usb conectados
                            $janela Janelas abertas
                            $servicos Serviços ativos.
                            
                            Estamos enviando: $bEnviados bytes
                            e recebendo: $bRecebidos bytes
                            
                        """.trimIndent()
                            )

                            bd.update(
                                """
                            insert into teste values
                            (null, $janela, ${pc.id})
                        """
                            )

                            Thread.sleep(10000)
                        }

                    } else {
                        println("\n\rFaça login para começar o monitoramento da sua maquina.\n\r")
                    }
                }

                6 -> break
            }

        }
    }
}