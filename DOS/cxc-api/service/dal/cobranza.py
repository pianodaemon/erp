import logging
from misc.helperpg import run_stored_procedure

def registrar_pago(usuario_id,  cliente_id,       moneda,     fecha_hora,  banco,         observaciones,
                   forma_pago,  cheque,           referencia, tarjeta,     monto_pago,    fecha_deposito,
                   ficha_movimiento_deposito,     ficha_cuenta_deposito,   ficha_banco_kemikal,
                   tipo_cambio, anticipo_gastado, no_transaccion_anticipo, saldo_a_favor, grid_detalle):

    grid_renglon_pago_str = convert_to_sql_array_literal(grid_detalle)

    """Llamado a funcion Postgres para registrar un pago"""
    sql = """SELECT * FROM pago_register(
        {}::integer,
        {}::integer,
        {}::integer,
        '{}'::timestamp with time zone,
        {}::integer,
        '{}'::text,
        '{}'::character varying,
        '{}'::character varying,
        '{}'::character varying,
        '{}'::character varying,
        {}::double precision,
        '{}'::timestamp with time zone,
        '{}'::character varying,
        {}::integer,
        {}::integer,
        {}::double precision,
        {}::double precision,
        {}::bigint,
        {}::double precision,
        {}::grid_renglon_pago[])
        AS msg""".format(
            usuario_id,
            cliente_id,
            moneda,
            fecha_hora.replace("'", "''"),
            banco,
            observaciones.replace("'", "''"),
            forma_pago.replace("'", "''"),
            cheque.replace("'", "''"),
            referencia.replace("'", "''"),
            tarjeta.replace("'", "''"),
            monto_pago,
            fecha_deposito.replace("'", "''"),
            ficha_movimiento_deposito.replace("'", "''"),
            ficha_cuenta_deposito,
            ficha_banco_kemikal,
            tipo_cambio,
            anticipo_gastado,
            no_transaccion_anticipo,
            saldo_a_favor,
            grid_renglon_pago_str
        )

    try:
        rmsg = run_stored_procedure(sql)

    except Exception as err:
        logging.error(err)
        raise

    logging.info('SUCCESS: ' + rmsg[0])
    return rmsg[0]


def convert_to_sql_array_literal(grid_detalle):
    rens_str = "array["
    first = True

    for s in grid_detalle:
        if not first:
            rens_str += ", "

        rens_str += (
            "(" +
            "'" + s.serieFolio.replace("'", "''") + "', " +
            str(s.saldado) + ", " +
            str(s.cantidad) + ", " +
            str(s.tipoCambio) +
            ")"
        )
        first = False

    rens_str += "]"

    return rens_str
