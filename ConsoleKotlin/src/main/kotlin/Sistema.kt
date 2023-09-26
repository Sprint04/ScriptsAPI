class Sistema {

    var logins = mutableListOf<String>()
    var senhas = mutableListOf<String>()
    var user = mutableListOf<String>()
    var login:Boolean = false
    var userLog:String = ""
    var emailLog:String = ""
    var senhaLog:String = ""

    fun cadastrar(usuario:String, email:String, senha:String){

        val log = email.lowercase()

        val simbulo = "@"

        if (simbulo !in email){
            return println("\n\rO email digitado não é considerado um email.\n\r")
        } else if(senha.length < 6){
            return println("\n\rSenha muito curta, digite ao minimo 6 caracteres.\n\r")
        } else if(user.contains(usuario)) {
            return println("\n\rEsse usuario já existe.\n\r")
        } else {
            logins.add(log)
            senhas.add(senha)
            user.add(usuario)
            return println("\n\rCadastro realizado com sucesso\n\r")
        }
    }

    fun logado(){
        this.login = true
    }

    fun login(email:String, senha:String){

        val log = email.lowercase()

        var indice:Int = logins.indexOf(log)

        if(indice >= 0 && senhas[indice] == senha){
            println("""
                |
                |Logado com sucesso!
                |Seja bem vindo senhor ${user[indice]}!!
                |
            """.trimMargin())
            this.userLog = user[indice]
            this.emailLog = logins[indice]
            this.senhaLog = senhas[indice]
            this.logado()
        } else {
            return println("\n\rVocê não está cadastrado ou email/senha digitados incorretamente.\n\r")
        }
    }

    fun info(){
        if(this.login){
            return println("""
                    |
                    |Nome de Usuário: $userLog
                    |Id do Usuário: ${user.indexOf(userLog)}
                    |Email: $emailLog
                    |
                """.trimMargin())
        } else {
            println("\n\rVocê ainda não está logado!!\n\r")
        }
    }

    fun delet(usuario:Int){
        var i = 0
        (this.logins).removeAt(usuario)
        (this.senhas).removeAt(usuario)
        (this.user).removeAt(usuario)
        return println("\n\rLista Atualizada de Usuario:\n\r${this.user.forEach{ println("$i - $it"); i++}}")
    }

}