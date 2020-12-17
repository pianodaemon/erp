from dal.helper import run_stored_procedure

def val_cust_order(usr_id, curr_val, date_lim, pay_met, account, matrix):

    matrix_str = renglones_to_arr_lit(matrix)

    """Calls database function in order to validate a customer order"""
    sql = """SELECT * FROM poc_val_cusorder(
        {}::integer,
        '{}'::character varying,
        '{}'::character varying,
        {}::integer,
        '{}'::character varying,
        {}::text[])
        AS msg""".format(
            usr_id,
            curr_val,
            date_lim,
            pay_met,
            account,
            matrix_str
        )

    rmsg = run_stored_procedure(sql)
    if rmsg[0] != '1':
        raise Exception(rmsg[0])

    return rmsg[0]


def renglones_to_arr_lit(matrix):
    rens_str = "array["
    first = True
    
    for s in matrix:
        if not first:
            rens_str += ", "
        
        rens_str += (
            "'" + s.replace("'", "''") + "'"
        )
        first = False
    
    rens_str += "]"

    return rens_str
