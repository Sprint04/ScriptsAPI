from mysql.connector import connect
import psutil
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

        connection = mysql_connection('localhost', 'testes', '12345678', 'trackware')

        cpu = str(cpu)
        mem = str(mem_used)
        disk = str(disk)
        maquina = str(maquina)

        c = str(1)
        m = str(2)
        d = str(3)
        query = '''
            INSERT INTO monitoramento(dadoCapturado, fkComponente, fkDispositivo) VALUES
                (
        '''
        
        dados = disk + ',' + d + ',' + maquina + " )"
        dados2 = cpu + ',' + c + ',' + maquina + " )"
        dados3 = mem + ',' + m + ',' + maquina + " )"

        cursor = connection.cursor()
        if(discoM == True):
            cursor.execute(query+dados)
            connection.commit()
        if(cpuM == True):
            cursor.execute(query+dados2)
            connection.commit()
        if(memoriaM == True):
            cursor.execute(query+dados3)
            connection.commit()

        time.sleep(5)


    connection.close()
