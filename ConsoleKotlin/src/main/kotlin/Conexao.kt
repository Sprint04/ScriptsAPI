import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.JdbcTemplate

object Conexao {
    var bd: JdbcTemplate? = null
        get() {
            if (field == null){
                val dataSource = BasicDataSource()
                dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
                val serverName = "localhost"
                val mydatabase = "trackware"
                dataSource.url = "jdbc:mysql://$serverName/$mydatabase"
                dataSource.username = "testes"
                dataSource.password = "12345678"
                val bd = JdbcTemplate(dataSource)
                field = bd
            }
            return field
        }
    fun insertComponentes(pc:Computador){
        bd!!.execute("""
            insert into componentes(fkTipoComponente,fkDispositivo,fkGatilhos) value
	            (1, ${pc.idDispositivo}, 1),
	            (2, ${pc.idDispositivo}, 2),
	            (3, ${pc.idDispositivo}, 3),
	            (4, ${pc.idDispositivo}, 4),
	            (5, ${pc.idDispositivo}, 5),
	            (6, ${pc.idDispositivo}, 6),
	            (7, ${pc.idDispositivo}, 6);
        """)
    }
}

