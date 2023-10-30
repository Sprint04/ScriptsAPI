import com.github.britooo.looca.api.core.Looca

fun main() {
    val bd = Repositorio()
    val looca = Looca()

    bd.iniciar()

    val ip = looca.rede.parametros.hostName
    val token = bd.validarDispositivo(ip)
    if(token){
        sistema(bd, looca, ip)
    } else{
        cadastro(bd, looca, ip)
    }
}