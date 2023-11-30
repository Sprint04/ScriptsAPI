import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.JdbcTemplate

object Conexao {
    var serverName = ""
    var mydatabase = "trackware"
    var username = "root"
    var password = "Trackware000"
    var bd: JdbcTemplate? = null
        get() {
            if (field == null){
                val dataSource = BasicDataSource()
                dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
                val serverName = this.serverName
                val mydatabase = this.mydatabase
                dataSource.url = "jdbc:mysql://$serverName/$mydatabase"
                dataSource.username = this.username
                dataSource.password = this.password
                val bd = JdbcTemplate(dataSource)
                field = bd
            }
            return field
        }
    fun insertComponentes(pc:Computador){
        bd!!.execute("""
            INSERT INTO tipoComponente(nome, descricao, fkUnidadeMedida) VALUES
                ('CPU', "Unidade Central de Processamento (CPU) responsável pelo processamento principal do sistema.", 1),
                ('Memória', "Memória de acesso aleatório (RAM) que armazena dados temporariamente para processamento.", 1),
                ('Disco', "Disco de armazenamento permanente (HDD/SSD) usado para armazenar dados permanentes no sistema.", 1),
                ('USB', "Porta de conexão universal utilizada para conectar dispositivos externos ao sistema.", 4),
                ('JanelasAbertas', "Quantidade de janelas de aplicativos abertas no sistema no momento da verificação.", 2),
                ('Rede(recebida)', "Dados recebidos pela interface de rede do dispositivo.", 3),
                ('Rede(enviada)', "Dados enviados pela interface de rede do dispositivo.", 3)
        """)
        bd!!.execute("""
            insert into componentes(fkTipoComponente,fkDispositivo) values
	            (1, ${pc.idDispositivo}),
	            (2, ${pc.idDispositivo}),
	            (3, ${pc.idDispositivo}),
	            (4, ${pc.idDispositivo}),
	            (5, ${pc.idDispositivo}),
	            (6, ${pc.idDispositivo}),
	            (7, ${pc.idDispositivo})
        """.trimIndent())
    }
    fun criarTabelas() {

        try {
            bd!!.execute(
                """
        CREATE TABLE IF NOT EXISTS tipoComponente(
	        idTipoComponente int primary key auto_increment,
	        nome varchar(45),
            descricao varchar(200),
            fkUnidadeMedida int
            )   
        """.trimIndent())
            bd!!.execute("""
                
        CREATE TABLE IF NOT EXISTS dispositivo(
	        idDispositivo INT PRIMARY KEY AUTO_INCREMENT,
	        sistema_Operacional VARCHAR (45),
            IP VARCHAR (50),
            fkEmpresa INT
        )
            """.trimIndent())
            bd!!.execute("""
        CREATE TABLE IF NOT EXISTS componentes(
            idComponente INT PRIMARY KEY AUTO_INCREMENT,
            fkTipoComponente int,
            foreign key (fkTipoComponente) references tipoComponente(idTipoComponente),
            fkDispositivo int,
            capacidade float
        )
        """.trimIndent())
            bd!!.execute("""
        CREATE TABLE IF NOT EXISTS monitoramento(
            idDado INT PRIMARY KEY AUTO_INCREMENT,
            dadoCapturado FLOAT,
            dtHora DATETIME default current_timestamp,
            fkComponente INT,
            FOREIGN KEY (fkComponente) REFERENCES componentes (idComponente),
            fkDispositivo int
        )
            """.trimIndent())
        } catch (exception:Exception){
            println(exception)
        }
    }
}

