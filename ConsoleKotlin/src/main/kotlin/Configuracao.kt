class Configuracao {

    var CPU:Boolean = false
    var memoria:Boolean = false
    var disco:Boolean = false
    var USB:Boolean = false
    var janelas:Boolean = false
    var serviços:Boolean = false
    var rede:Boolean = false
    var processo:Boolean = false

    fun python():String{
        if(this.CPU || this.memoria || this.disco){
            return "s"
        }
        return "n"
    }

}