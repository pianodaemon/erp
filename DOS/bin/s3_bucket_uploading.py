import docker
import boto3
import gzip
from datetime import datetime

db_container = 'rdbms_dos'
database     = 'erp'
bucket_name  = 'medica-dumps'
s3_obj_name  = 'rrm-dump-' + datetime.now().strftime('%Y-%m-%d') + '.sql.gz'

# Getting the database dump
client = docker.from_env()

container = client.containers.get(db_container)
exec_code, output = container.exec_run('pg_dump -h localhost -U postgres --create {}'.format(database))

if exec_code != 0:
    raise Exception("PANIC!!!: " + exec_code)


s3 = boto3.resource('s3')

# Compressing and uploading
s3.Bucket(bucket_name).put_object(Key=s3_obj_name, Body=gzip.compress(output))
