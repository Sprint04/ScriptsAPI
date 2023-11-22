import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.JdbcTemplate

object SLQserver {
    var serverName = "52.45.132.234"
    var mydatabase = "trackware"
    var username = "sa"
    var password = "#Gfsptech"
    var bd: JdbcTemplate? = null
        get() {
            if (field == null){
                val dataSource = BasicDataSource()
                dataSource.driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                val serverName = this.serverName
                val mydatabase = this.mydatabase
                dataSource.url = "jdbc:sqlserver://$serverName;database=$mydatabase;encrypt=false;trustServerCertificate=false"
                dataSource.username = this.username
                dataSource.password = this.password
                val bd = JdbcTemplate(dataSource)
                field = bd
            }
            return field
        }
    fun insertComponentes(pc:Computador){
        bd!!.execute("""
        insert into componentes(fkTipoComponente,fkDispositivo,fkGatilhos) values
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