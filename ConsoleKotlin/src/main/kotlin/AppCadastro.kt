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
                pc.fkempresa = empresa[0].id

                val cadastrado = bd.cadastrarDispostivo(pc)
                val comp:List<Computador> = bd.computador(mac)
                val pc = comp[0]
                var cadastrado2 = false
                if(cadastrado) {
                    try {
                        SQLserver.insertComponentes(pc)
                        Conexao.insertComponentes(pc)
                        println("Cadastro Realizado!!\r\nReiniciando o programa.")
                        cadastrado2 = true
                    } catch (exception: Exception) {
                        cadastrado2 = true
                    }
                }
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