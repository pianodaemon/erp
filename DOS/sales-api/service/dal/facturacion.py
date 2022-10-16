import json
import psycopg2
from misc.helperpg import run_stored_procedure
from dal.factura import FactRepr
from misc.error import FatalError
from .helperpg import HelperPg

def edit_prefactura(usuario_id,     prefactura_id,      cliente_id,      moneda_id,
                    observaciones,  tipo_cambio,        vendedor_id,     condiciones_id,
                    orden_compra,   refacturar,         metodo_pago_id,  no_cuenta,
                    tipo_documento, moneda_original_id, adenda1,         adenda2,
                    adenda3,        adenda4,            adenda5,         adenda6,
                    adenda7,        adenda8,            permitir_descto, grid_detalle):

    grid_detalle_str = convert_to_sql_array_literal(grid_detalle)

    """Calls database function in order to update a Prefactura"""
    sql = """
            SELECT * FROM prefactura_edit(
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
               '{}'::text,
                {}::double precision,
                {}::integer,
                {}::integer,
               '{}'::character varying,
                {}::boolean,
                {}::integer,
               '{}'::character varying,
                {}::smallint,
                {}::integer,
               '{}'::character varying,
               '{}'::character varying,
               '{}'::character varying,
               '{}'::character varying,
               '{}'::character varying,
               '{}'::character varying,
               '{}'::character varying,
               '{}'::character varying,
                {}::boolean,
                {}::grid_renglon_prefactura[]
            ) AS msg
            """.format(
                usuario_id,
                prefactura_id,
                cliente_id,
                moneda_id,
                observaciones.replace("'", "''"),
                tipo_cambio,
                vendedor_id,
                condiciones_id,
                orden_compra.replace("'", "''"),
                refacturar,
                metodo_pago_id,
                no_cuenta.replace("'", "''"),
                tipo_documento,
                moneda_original_id,
                adenda1.replace("'", "''"),
                adenda2.replace("'", "''"),
                adenda3.replace("'", "''"),
                adenda4.replace("'", "''"),
                adenda5.replace("'", "''"),
                adenda6.replace("'", "''"),
                adenda7.replace("'", "''"),
                adenda8.replace("'", "''"),
                permitir_descto,
                grid_detalle_str
            )

    rmsg = run_stored_procedure(sql)
    json_repr = ''

    if rmsg[0] == '1:':
        dat = __create(__open_dbms_conn(), usr_id=usuario_id, prefact_id=prefactura_id)
        convert_decimal_type(dat)
        json_repr = json.dumps(dat)

    return (rmsg[0], json_repr)


def convert_to_sql_array_literal(grid_detalle):
    rens_str = "array["
    first = True
    
    for s in grid_detalle:
        if not first:
            rens_str += ", "
        
        rens_str += (
            "("                    +
            str(s.toKeep)          + ", " +
            str(s.id)              + ", " +
            str(s.productoId)      + ", " +
            str(s.presentacionId)  + ", " +
            str(s.tipoImpuestoId)  + ", " +
            str(s.cantidad)        + ", " +
            str(s.precioUnitario)  + ", " +
            str(s.valorImp)        + ", " +
            str(s.remisionId)      + ", " +
            str(s.costoPromedio)   + ", " +
            str(s.invProdUnidadId) + ", " +
            str(s.gralIepsId)      + ", " +
            str(s.valorIeps)       + ", " +
            str(s.descto)          + ", " +
            str(s.gralImptosRetId) + ", " +
            str(s.tasaRet)         +
            ")"
        )
        first = False
    
    rens_str += "]"

    return rens_str


def __create(conn, **kwargs):
    """runs data acquisition"""
    dat = None

    try:
        fac = FactRepr()
        dat = fac.data_acq(conn, **kwargs)
        return dat
    except FatalError:
        raise
    finally:
        conn.close()


def __open_dbms_conn():
    """opens a connection to postgresql"""
    try:
        return HelperPg.connect()
    except psycopg2.Error as e:
        raise FatalError("dbms was not connected")
    except KeyError as e:
        raise FatalError("slack pgsql configuration")


def convert_decimal_type(dat):
    for i in dat['conceptos']:
        i['precio_unitario'] = float(i['precio_unitario'])
