import java.io.File

object ScriptPython {

    var PythonExe: List<Process> = listOf()


    val host = SQLserver.serverName
    val user = SQLserver.username
    val passwd = SQLserver.password
    val database = SQLserver.mydatabase
    val host2 = Conexao.serverName
    val user2 = Conexao.username
    val passwd2 = Conexao.password
    val database2 = Conexao.mydatabase

    fun criarPython(python:String, maquina:Int, cpu:String, disk:String, memoria:String): Pair<String, String> {

            val pythonGeral = """
from mysql.connector import connect
import psutil
import pymssql
import time
import datetime
import platform
import smtplib
import sys

def mysql_connection(host, user, passwd, database=None):
    connection = connect(
        host = host,
        user = user,
        passwd = passwd,
        database = database
    )
    return connection
    
import pymssql

def monitoramento(dado, componente_janela, pc):
    try:
        conn = pymssql.connect(server='$host', user='$user', password='$passwd', database='$database')
        cursor = conn.cursor()
        query = "INSERT INTO monitoramento (dadoCapturado, fkComponente, fkDispositivo) VALUES (%s, %s, %s)"
        cursor.execute(query, (dado, componente_janela, pc))
        conn.commit()
        
    except pymssql.Error as ex:
        print(f"Erro ao conectar ao banco de dados pymssql: {ex}")
        try:
            connection = mysql_connection('$host2', '$user2', '$passwd2', '$database2')
            query = '''
                INSERT INTO monitoramento(dadoCapturado, fkComponente, fkDispositivo) VALUES
                    (
            '''
            dados = dado + ',' + componente_janela + ',' + pc + " )"
            cursor = connection.cursor()
            cursor.execute(query+dados)
            connection.commit()
        except mysql.connector.Error as err:
            print(f"Erro ao conectar ao banco de dados MySQL: {err}")

resposta = '$python'
if(resposta == "S" or resposta == "s"):
    maquina = '$maquina'
    cpuM = '$cpu'
    memoriaM = '$memoria'
    discoM = '$disk'

    if(cpuM == "S" or cpuM == "s"):
        cpuM = True
    else:
        cpuM = False
    if(memoriaM == "S" or memoriaM == "s"):
        memoriaM = True
    else:
        memoriaM = False
    if(discoM == "S" or discoM == "s"):
        discoM = True
    else:
        discoM = False
    
    while(True):
        
        cpu = psutil.cpu_percent()

        mem_used = psutil.virtual_memory()[2]

        disk = psutil.disk_usage('C:\\')[3]

        print("\n- CPU(%):",cpu, "\n- RAM(%):", mem_used, "\n- DISCO(%):", disk)
        
 # -----------------------------------------------------------------------------------------------------------------------------------
 
        info = psutil.disk_partitions()

        cpu = str(cpu)
        mem = str(mem_used)
        disk = str(disk)
        maquina = str(maquina)

        c = str(1)
        m = str(2)
        d = str(3)

        if(discoM == True):
            monitoramento(disk, d, maquina)
        if(cpuM == True):
            monitoramento(cpu, c, maquina)
        if(memoriaM == True):
            monitoramento(mem, m, maquina)

        time.sleep(5)

    """.trimIndent()

            val pythonAlerta = """
                from mysql.connector import connect
                import pymssql
                import psutil
                import time
                import datetime
                import platform
                import smtplib
                import email.message
                import sys

                def mysql_connection(host, user, passwd, database=None):
                    return connect(host=host, user=user, passwd=passwd, database=database)

                def read_query(query):
                    result = None
                    try:
                        conn = pymssql.connect(server='$host', user='$user', password='$passwd', database='$database')
                        cursor = conn.cursor()
                        if 'LIMIT 1' in query:
                            query = query.replace('LIMIT 1', '')
                            query = query.replace('SELECT', 'SELECT TOP 1')
                        cursor.execute(query)
                        result = cursor.fetchall()
                    except pymssql.Error as ex:
                        print(f"Erro ao conectar ao banco de dados pymssql: {ex}")
                        try:
                            connection = mysql_connection('$host2', '$user2', '$passwd2', '$database2')
                            cursor = connection.cursor()
                            cursor.execute(query)
                            result = cursor.fetchall()
                        except mysql.connector.Error as err:
                            print(f"Erro ao conectar ao banco de dados MySQL: {err}")
                    return result

                corpo = ""${'"'}
                    <!DOCTYPE html>
                    <html lang="pt-br">
                    <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Alerta da Trackware</title>
                    </head>
                    <body style="
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 0;">
                        <div style="
                        max-width: 90%;
                        margin: 0 auto;
                        padding: 2%;">
                            <div style="
                                background-color: #6b3e98;
                                color: #ffffff;
                                text-align: center;
                                padding: 3%;">
                                <img style="
                                max-width: 10%;
                                height: auto;
                                " src="https://i.imgur.com/NIBLx6a.png" alt="Logotipo da Trackware">
                                <h1>Alerta da Trackware</h1>
                            </div>
                            <div style="
                                background-color: #ffffff;
                                padding: 3%;
                                border-radius: 5px;
                                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
                                <p style="font-size: 18px; margin-bottom: 20px;">Prezado Cliente,</p>
                                <p style="font-size: 18px; margin-bottom: 20px;">Verificamos que seu dispositivo atingiu seu limite de uso de ""${'"'}
                                
                def enviar_email(corpo, componente):
                    dia = " as " + datetime.datetime.now().strftime('%d/%m/%Y %H:%M:%S')
                    end_corpo = dia + ""${'"'}</p>
                                <p style="font-size: 18px; margin-bottom: 20px;">Por favor, clique no retangulo abaixo para acessar nosso site e acompanhar o dispositivo.</p>
                                <a style="
                                display: inline-block;
                                background-color: #6b3e98;
                                color: #ffffff;
                                padding: 2% 4%;
                                text-decoration: none;
                                border-radius: 5px;" href="#">Acessar Trackware System</a>
                            </div>
                        </div>
                    </body>
                    </html>
                    ""${'"'}
                
                    msg = email.message.Message()
                    msg['Subject'] = "Alerta"
                    msg['From'] = 'nathanraoliveira@gmail.com'
                    msg['To'] = 'alertas-aaaak42km6rgih2za6nkswurre@trackware-workspace.slack.com'
                    password = 'empmruelvcjsbreg' 
                    msg.add_header('Content-Type', 'text/html; charset=UTF-8')  # Adicione 'charset=UTF-8' ao cabeçalho 'Content-Type'
                    msg.set_payload(corpo+end_corpo)
                
                    s = smtplib.SMTP('smtp.gmail.com: 587')
                    s.starttls()
                    s.login(msg['From'], password)
                    s.sendmail(msg['From'], [msg['To']], msg.as_string().encode('utf-8'))

                connection = mysql_connection('$host', '$user', '$passwd', '$database')
                maquina = str($maquina)

                componentes = ['CPU', 'Disco', 'Memoria', 'USB', 'JanelasAbertas', 'Rede(recebida)', 'Rede(enviada)']
                alertas = {componente: False for componente in componentes}

                while True:
                    for componente in componentes:
                        query = f"select dadoCapturado from monitoramento where fkDispositivo = {maquina} and fkComponente = (select idTipoComponente from tipoComponente where nome = '{componente}') order by idDado desc limit 1;"
                        dado = read_query(query)
                        if dado:
                            dado = float(dado[0][0])
                            alertas[componente] = True

                        limiteQuery = f"select limite from componentes join gatilhos on fkGatilhos = idGatilhos where fkDispositivo = {maquina} and fkTipoComponente = (select idTipoComponente from tipoComponente where nome = '{componente}');"
                        limite = read_query(limiteQuery)
                        if limite:
                            limite = float(limite[0][0])

                        if alertas[componente] and dado > limite:
                            print(f"Enviando Email({componente.lower()})")
                            enviar_email(corpo, componente)

                    time.sleep(5)

                connection.close()

            """.trimIndent()


            val nomeArquivoPython1 = "ScriptPython.py"
            File(nomeArquivoPython1).writeText(pythonGeral)

            Thread.sleep(2 * 1000L)

            val nomeArquivoPython2 = "AlertasSlack.py"
            File(nomeArquivoPython2).writeText(pythonAlerta)

            return Pair(nomeArquivoPython1, nomeArquivoPython2)

        }

        fun executarScript(arquivo1: String, arquivo2: String) {
            val pythonProcess1 = Runtime.getRuntime().exec("py $arquivo1")
            val pythonProcess2 = Runtime.getRuntime().exec("py $arquivo2")
            PythonExe = listOf(pythonProcess1, pythonProcess2)
        }

        fun pararScript() {
            for (process in PythonExe) {
               process.destroyForcibly()
            }
        }
    }