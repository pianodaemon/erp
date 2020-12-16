from dal.helper import run_stored_procedure


def edit_cot(usuario_id, identificador, select_tipo_cotizacion, id_cliente_o_prospecto,
    check_descripcion_larga, observaciones, tipo_cambio, moneda_id, fecha, agente_id,
    vigencia, incluye_iva, tc_usd, extra_data):

    grid_renglon_cot_str = renglones_cot_to_comp_type_arr_lit(extra_data)

    """Calls database function in order to create/update a quotation"""
    sql = """SELECT * FROM cot_edit(
        {}::integer,
        {}::integer,
        {}::integer,
        {}::integer,
        {}::boolean,
        '{}'::text,
        {}::double precision,
        {}::integer,
        '{}'::date,
        {}::integer,
        {}::smallint,
        {}::boolean,
        {}::double precision,
        {}::grid_renglon_cot[])
        AS msg""".format(
            usuario_id,
            identificador,
            select_tipo_cotizacion,
            id_cliente_o_prospecto,
            check_descripcion_larga,
            observaciones.replace("'", "''"),
            tipo_cambio,
            moneda_id,
            fecha,
            agente_id,
            vigencia,
            incluye_iva,
            tc_usd,
            grid_renglon_cot_str
        )

    rmsg = run_stored_procedure(sql)
    if rmsg[0] != '1':
        raise Exception(rmsg)

    return rmsg[0]


def renglones_cot_to_comp_type_arr_lit(extra_data):
    rens_str = "array["
    first = True
    
    for s in extra_data:
        if not first:
            rens_str += ", "
        
        rens_str += (
            "(" +
            str(s.removido) + ", " +
            str(s.idDetalle) + ", " +
            str(s.idProducto) + ", " +
            str(s.idPresentacion) + ", " +
            str(s.cantidad) + ", " +
            str(s.precio) + ", " +
            str(s.monedaGrId) + ", " +
            "'" + s.notr.replace("'", "''") + "', " +
            str(s.idImpProd) + ", " +
            str(s.valorImp) + ", " +
            str(s.unidadId) + ", " +
            str(s.statusAutorizacion) + ", " +
            str(s.precioAutorizado) + ", " +
            str(s.idUserAut) + ", " +
            str(s.requiereAutorizacion) + ", " +
            "'" + s.salvarRegistro.replace("'", "''") + "'" +
            ")"
        )
        first = False
    
    rens_str += "]"

    return rens_str
