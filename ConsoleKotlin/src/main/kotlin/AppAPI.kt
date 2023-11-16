import com.github.britooo.looca.api.core.Looca
import java.util.*

fun sistema(bd:Repositorio, looca: Looca, mac:String){

    val sistema = Sistema()
    val sn = Scanner(System.`in`)

    val comp:List<Computador> = bd.computador(mac)

    val pc = comp[0]
    println("Credenciais verificadas, Iniciando programa!")
    Thread.sleep(2000)

    val user:List<Usuario> = bd.Usuarios(pc)
    val plano = bd.verificarPlano(pc)


    while (true) {

        println(
            """Oque deseja fazer?
        |1) Login
        |2) Obter Informações do Usuário logado
        |3) Começar monitoramento
        |4) Exit
    """.trimMargin()
        )

        var acao = (sn.next()).toInt()
        val aMonitorar = Configuracao()

        when (acao) {
            1 -> {
                print("Digite seu email: ")
                var email = sn.next()

                print("Digite seu senha: ")
                var senha = sn.next()

                user.forEach{
                    if (it.email == email && it.senha == senha) {
                        sistema.login(it.idUsuario, it.nome, it.email, it.senha, it.cargo)
                    }
                }

            }
            2 -> {
                if (sistema.login){
                    sistema.info()
                } else{
                    println("Por favor, faça login antes de pedir dados do usuário logado.")
                }
            }
            3 -> {
                if (sistema.login) {
                    var config = true
                    while(config){
                        println("""
                            |O que deseja monitorar?
                            |1 - CPU (${if(aMonitorar.CPU) "Ativo" else "inativo"})
                            |2 - Memória (${if(aMonitorar.memoria) "Ativo" else "inativo"})
                            |3 - Disco (${if(aMonitorar.disco) "Ativo" else "inativo"})
                            |4 - USB (${if(aMonitorar.USB) "Ativo" else "inativo"})
                            |5 - Janelas (${if(aMonitorar.janelas) "Ativo" else "inativo"})
                            |6 - Rede (${if(aMonitorar.rede) "Ativo" else "inativo"})
                            |7 - Confirmar
                        """.trimMargin())
                        var i = sn.next().toInt()
                        when(i){
                            1 -> if(aMonitorar.CPU)aMonitorar.CPU = false else aMonitorar.CPU = true
                            2 -> if(aMonitorar.memoria) aMonitorar.memoria = false else aMonitorar.memoria = true
                            3 -> if(aMonitorar.disco) aMonitorar.disco = false else aMonitorar.disco = true
                            4 -> if(plano[0].permissao) if(aMonitorar.USB) aMonitorar.USB = false else aMonitorar.USB = true
                                    else println("\n\rSeu plano não comporta esse Monitoramento")
                            5 -> if(plano[1].permissao) if(aMonitorar.janelas) aMonitorar.janelas = false else aMonitorar.janelas = true
                                    else println("\n\rSeu plano não comporta esse Monitoramento")
                            6 -> if(plano[2].permissao) if(aMonitorar.rede) aMonitorar.rede = false else aMonitorar.rede = true
                                    else println("\n\rSeu plano não comporta esse Monitoramento")
                            7 -> config = false
                        }

                    }

                    bd.acessoLog(sistema,pc)

                    var fks: Int
                    var dados: Double
                    println("\n\rEstamos monitorando sua máquina.\n\r")
                    val (arquivo1, arquivo2) = ScriptPython.criarPython(aMonitorar.python(), pc.idDispositivo, if(aMonitorar.CPU) "s" else "n", if(aMonitorar.memoria) "s" else "n", if(aMonitorar.disco) "s" else "n")
                    ScriptPython.executarScript(arquivo1,arquivo2)
                    Runtime.getRuntime().addShutdownHook(Thread {
                        println("O monitoramento foi finalizado")
                        ScriptPython.pararScript()
                    })
                        while (true) {
                            println("Temos um total de:")
                            if (aMonitorar.USB) {
                                fks = 4
                                dados = (looca.dispositivosUsbGrupo.totalDispositvosUsbConectados).toDouble()
                                println("$dados usb conectados")
                                bd.monitoramento(dados, fks, pc)
                            }
                            if (aMonitorar.janelas) {
                                fks = 5
                                dados = (looca.grupoDeJanelas.totalJanelas).toDouble()
                                println("$dados Janelas abertas")
                                bd.monitoramento(dados, fks, pc)
                            }
                            if (aMonitorar.rede) {
                                fks = 6
                                dados = (looca.rede.grupoDeInterfaces.interfaces[1].bytesRecebidos / 1000000).toDouble()
                                println("Estamos Recebendo: $dados Megabytes de rede")
                                bd.monitoramento(dados, fks, pc)
                                fks = 7
                                dados = (looca.rede.grupoDeInterfaces.interfaces[1].bytesEnviados / 1000000).toDouble()
                                println("Estamos Enviando: $dados Megabytes de rede")
                                bd.monitoramento(dados, fks, pc)
                            }
                            println("\n\r")
                            Thread.sleep(5000)
                        }

                } else {
                    println("\n\rFaça login para começar o monitoramento da sua maquina.\n\r")
                }
            }

            4 -> break
        }

    }
}
