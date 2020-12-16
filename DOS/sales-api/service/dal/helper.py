from misc.helperpg import pgslack_connected, pgslack_exec, pgslack_update
from misc.helperpg import EmptySetError, ServerError

@pgslack_connected
def run_stored_procedure(conn, sql):
    """Runs a stored procedure with rich answer"""

    r = pgslack_exec(conn, sql)

    # For this case we are just expecting one row
    if len(r) != 1:
        return -1, "Unexpected result regarding execution of stored procedure"

    return r.pop()


@pgslack_connected
def exec_steady(conn, sql):
    return pgslack_exec(conn, sql)


@pgslack_connected
def update_steady(conn, sql):
    return pgslack_update(conn, sql)



def get_ignored_audit_structs(ignored_audit_set, prefix):
    s = ''
    l = []
    while True:
        try:
            aud = str(ignored_audit_set.pop())
            l.append(aud)
            s += ' and ' + prefix + 'auditoria_id <> ' + aud
        except:
            break
    return s, l

def get_direction_str_condition(division_id):
    return 'and direccion_id = ' + str(division_id) if division_id else ''


def get_ignored_audits():
    ignored_audit_set = set()  
    sql = '''
        select count(auditoria_id) as conteo, auditoria_id
        from auditoria_anios_cuenta_pub
        group by auditoria_id
        order by conteo desc, auditoria_id;
    '''
    try:
        rows = exec_steady(sql)
    except EmptySetError:
        rows = []
    except Exception:
        raise ServerError('Hay un problema con el servidor de base de datos')

    for row in rows:
        if row[0] > 1:
            ignored_audit_set.add(row[1])
        else:
            break
    
    sql = '''
        select count(auditoria_id) as conteo, auditoria_id
        from auditoria_dependencias
        group by auditoria_id
        order by conteo desc, auditoria_id;
    '''
    try:
        rows = exec_steady(sql)
    except EmptySetError:
        rows = []

    for row in rows:
        if row[0] > 1:
            ignored_audit_set.add(row[1])
        else:
            break
    return ignored_audit_set
