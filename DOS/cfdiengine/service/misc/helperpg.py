import psycopg2
import psycopg2.extras
from custom.profile import env_property


class HelperPg(object):
    """
    """

    @staticmethod
    def connect():
        """opens a connection to database"""

        # order here matters
        env_vars = ['MS_DBMS_DB', 'MS_DBMS_USER', 'MS_DBMS_HOST', 'MS_DBMS_PASS', 'MS_DBMS_PORT']
        t = tuple(map(env_property, env_vars))

        try:
            conn_str = "dbname={0} user={1} host={2} password={3} port={4}".format(*t)
            return psycopg2.connect(conn_str)
        except:
            raise Exception('It is not possible to connect with database')

    @staticmethod
    def onfly_update(sql):
        """updates database"""
        conn = None
        updated_rows = 0
        try:
            conn = HelperPg.connect()
            cur = conn.cursor()
            cur.execute(sql)
            updated_rows = cur.rowcount
            conn.commit()
            cur.close()
        except (Exception, psycopg2.DatabaseError) as error:
            raise Exception('Error updating database')
        finally:
            if conn is not None:
                conn.close()
        return updated_rows

    @staticmethod
    def query(conn, sql, commit=False):
        """carries an sql query out to database"""
        cur = conn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        cur.execute(sql)
        if commit:
            conn.commit()
        rows = cur.fetchall()
        cur.close()
        if len(rows) > 0:
            return rows

        # We should not have reached this point
        raise Exception('There is not data retrieved')

    @staticmethod
    def onfly_query(sql, commit=False):
        """exec a query with a temporary connection"""
        conn = HelperPg.connect()

        try:
            return HelperPg.query(conn, sql, commit)
        except:
            raise
        finally:
            conn.close()

    @staticmethod
    def store(conn, name, output_expected, *args):
        """calls an store procedure of database"""
        cur = conn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        cur.callproc(name, *args)
        rows = cur.fetchall()
        cur.close()

        if not output_expected:
            return

        if len(rows) > 0:
            return rows

        # We should not have reached this point
        raise Exception('There is not data retrieved')
