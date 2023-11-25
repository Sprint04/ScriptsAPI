import com.github.britooo.looca.api.core.Looca
import kotlin.concurrent.thread

fun main() {
    val bd = Repositorio()
    val looca = Looca()

    bd.iniciar()

    val mac = getMac()
    val token = bd.validarDispositivo(mac)
    if(token){
        sistema(bd, looca, mac)
    } else{
        cadastro(bd, looca, mac)
    }
}