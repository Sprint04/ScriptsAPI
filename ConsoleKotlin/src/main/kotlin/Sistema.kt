class Sistema {

    var idUser:Int = 0
    var nome:String = ""
    var login:Boolean = false
    var email:String = ""
    var senha:String = ""
    var cargo:String = ""

    fun logado(){
        this.login = true
    }

    fun login(idUsuario:Int ,usuario:String,email:String, senha:String, cargo:String){

        this.idUser = idUsuario
        this.nome = usuario
        this.email = email
        this.senha = senha
        this.cargo = cargo
        this.logado()

            println("""
                |
                |Logado com sucesso!
                |Seja bem vindo senhor ${this.nome}!!
                |
            """.trimMargin())
        }

    fun info(){
        println("""
    |
    |Id do Usuário: $idUser
    |Nome de Usuário: $nome
    |Email: $email
    |Cargo: $cargo
    |
    """.trimMargin())
    }

}