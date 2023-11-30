import com.github.britooo.looca.api.core.Looca
import java.util.*

fun cadastro(bd:Repositorio, looca:Looca, mac:String){

    val sn = Scanner(System.`in`)

    val pc = Computador()
    println("Dispostivivo não identificado,")
    println("Cadastre esse dispositivo:")
    println("Seu Token:")
    val token = sn.next()

    val empresa:List<Empresa> = bd.empresa(token)

    if (empresa.isNotEmpty()) {

        val qtd = bd.validacaoLimite(token)

        val l = empresa[0].limite

        val maxpc = l - 1

        when (qtd) {
            in 0..maxpc -> {
                println("Chave de Ativação Aceita!!\r\nCadastrando Dispostivo")
                Thread.sleep(2000)
                pc.idDispositivo = qtd + 1
                pc.sistemaOperacional = looca.sistema.sistemaOperacional
                pc.ip = mac
                println("Endereço mac obtido e sendo usado como identificador pessoal da maquina: ${pc.ip}")
                pc.fkempresa = empresa[0].id

                val cadastrado = bd.cadastrarDispostivo(pc)
                println(if(cadastrado)"Dispositivo Cadastrado no server" else "Falha ao cadastrar o dispositivo no server")
                val comp:List<Computador> = bd.computador(mac)
                val pc = comp[0]
                var cadastrado2 = false
                if(cadastrado) {
                    cadastrado2 = try {
                        SQLserver.insertComponentes(pc)
                        Conexao.criarTabelas()
                        bd.cadastrarDispostivoLocal(pc)
                        Conexao.insertComponentes(pc)
                        println("Cadastro Realizado!!\r\nReiniciando o programa.")
                        true
                    } catch (exception: Exception) {
                        println(exception)
                        true
                    }
                }
                println(if(cadastrado2)"Tudo certo!!" else "Deu erro ao cadastrar o banco local.")
                if(!cadastrado2){
                    println("Cadastro não realizado!!\r\nReiniciando o programa.")
                }
                Thread.sleep(2000)
                main()
            }

            l -> println(
                "Numero maximos de Computadores já atingido, por favor , " +
                        "contrate um plano melhor ou fale conosco se o numero maximo " +
                        "contratado ainda não foi atingido."
            )

        }


    } else {
        println("Essa chave de ativação não existe")
    }
}