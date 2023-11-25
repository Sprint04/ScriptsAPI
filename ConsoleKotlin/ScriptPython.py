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
        conn = pymssql.connect(server='52.45.132.234', user='sa', password='#Gfsptech', database='trackware')
        cursor = conn.cursor()
        query = "INSERT INTO monitoramento (dadoCapturado, fkComponente, fkDispositivo) VALUES (%s, %s, %s)"
        cursor.execute(query, (dado, componente_janela, pc))
        conn.commit()
        
    except pymssql.Error as ex:
        print(f"Erro ao conectar ao banco de dados pymssql: {ex}")
        try:
            connection = mysql_connection('localhost', 'testes', '12345678', 'trackware')
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

resposta = 's'
if(resposta == "S" or resposta == "s"):
    maquina = '1'
    cpuM = 's'
    memoriaM = 's'
    discoM = 's'

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
