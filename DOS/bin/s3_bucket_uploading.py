#!/usr/bin/env python3

import sys
import docker
import boto3
import gzip
from datetime import datetime


class PgDumpCloud(object):

    _TRANSITIVE_CMD_FMT = """pg_dump -h localhost -U postgres --create {}"""
    _DUMP_FILENAME_FMT = """{}-dump-{}.sql.gz"""

    def __call__(self, dumper, placer):

        # Getting the database dump
        exec_code, data = dumper(self._tcmd)

        if exec_code != 0:
            raise Exception("PANIC!!!: " + exec_code)

        # Compressing and uploading
        placer(data, self._sthree_fname)

    @classmethod
    def bucketize(cls, db_container, db_instance, target_bucket, prefix):
        dumper = cls._gear_up_dump(db_container)
        placer = cls._placement(target_bucket)
        ic = cls()
        ic._sthree_fname = cls._DUMP_FILENAME_FMT.format(db_instance, datetime.now().strftime('%Y-%m-%d'))
        ic._tcmd = cls._TRANSITIVE_CMD_FMT.format(db_instance)
        return ic

    @classmethod
    def _placement(cls, target_bucket)
        sthree_res = boto3.resource('s3')
        return lambda data, fname: sthree_res.Bucket(target_bucket).put_object(Key=fname, Body=gzip.compress(data))

    @classmethod
    def _gear_up_dump(cls, db_container):
        client = docker.from_env()
        container = client.containers.get(db_container)
        return lambda tcmd: container.exec_run(tcmd)

if __name__ == "__main__":

    pgdc = PgDumpCloud.bucketize('rdbms_dos', 'erp', 'medica-dumps')

    try:
        pgdc()
    except KeyboardInterrupt:
        print('Exiting')
    except:
        sys.exit(1)
