import com.github.britooo.looca.api.core.Looca
import kotlin.concurrent.thread

fun main() {
    val bd = Repositorio()
    val looca = Looca()

    if(looca.sistema.sistemaOperacional.contains("Windows")){
            Conexao.serverName = "localhost"
    } else{
        Conexao.serverName = "172.17.0.2"
    }

    bd.iniciar()

    val mac = getMac()
    val token = bd.validarDispositivo(mac)
    if(token){
        sistema(bd, looca, mac)
    } else{
        cadastro(bd, looca, mac)
    }
}