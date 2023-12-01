from mysql.connector import connect
import pymssql
import psutil
import time
import datetime
import platform
import smtplib
import sys
import requests
import json
import mysql.connector

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

maquina = str(4)

componentes = ['CPU', 'Disco', 'Memoria', 'USB', 'JanelasAbertas', 'Rede(recebida)', 'Rede(enviada)']
alertas = {componente: False for componente in componentes}
webhook = "https://hooks.slack.com/services/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"

while True:
    for componente in componentes:
        query = f"select top 1 dadoCapturado from monitoramento where fkDispositivo = {maquina} and fkComponente = (select idTipoComponente from tipoComponente where nome = '{componente}') order by idDado desc;"
        dado = read_query(query)
        if dado:
            dado = float(dado[0][0])
            alertas[componente] = True

        limiteQuery = f"select limite from componentes join gatilhos on fkGatilhos = idGatilhos where fkDispositivo = {maquina} and fkTipoComponente = (select idTipoComponente from tipoComponente where nome = '{componente}');"
        limite = read_query(limiteQuery)
        if limite:
            limite = float(limite[0][0])

        if alertas[componente] and dado > limite:
            print(f"Enviando Email({componente.lower()} - Computador Nathan)")
            alerta = {"text":f"Atenção!{componente} está excedendo o limite que você definiu no computador Computador Nathan - 54:6C:EB:7C:55:5C"}
            requests.post(webhook, data=json.dumps(alerta))
    
    time.sleep(5)

connection.close()
