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
        conn = pymssql.connect(server='52.45.132.234', user='sa', password='#Gfsptech', database='trackware')
        cursor = conn.cursor()
        if 'LIMIT 1' in query:
            query = query.replace('LIMIT 1', '')
            query = query.replace('SELECT', 'SELECT TOP 1')
        cursor.execute(query)
        result = cursor.fetchall()
    except pymssql.Error as ex:
        print(f"Erro ao conectar ao banco de dados pymssql: {ex}")
        try:
            connection = mysql_connection('localhost', 'root', 'Trackware000', 'trackware')
            cursor = connection.cursor()
            cursor.execute(query)
            result = cursor.fetchall()
        except mysql.connector.Error as err:
            print(f"Erro ao conectar ao banco de dados MySQL: {err}")
    return result

corpo = """
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
                <p style="font-size: 18px; margin-bottom: 20px;">Verificamos que seu dispositivo atingiu seu limite de uso de """
                
def enviar_email(corpo, componente):
    dia = " as " + datetime.datetime.now().strftime('%d/%m/%Y %H:%M:%S')
    end_corpo = dia + """</p>
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
    """

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

connection = mysql_connection('52.45.132.234', 'sa', '#Gfsptech', 'trackware')
maquina = str(4)

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
