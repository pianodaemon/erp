from misc.helperpg import run_stored_procedure

def edit_pedido(usuario_id,    agente_id,         cliente_id,      cliente_df_id,
                almacen_id,    moneda_id,         prov_credias_id, cfdi_met_pago_id,
                forma_pago_id, cfdi_uso_id,       pedido_id,       tasa_retencion_immex,
                tipo_cambio,   porcentaje_descto, descto_allowed,  enviar_obser_fac,
                flete_enabled, enviar_ruta,       observaciones,   motivo_descto,
                transporte,    fecha_compromiso,  lugar_entrega,   orden_compra,
                num_cuenta,    folio_cot,         grid_detalle):

    grid_detalle_str = convert_to_sql_array_literal(grid_detalle)

    """Calls database function in order to create/update a sales order"""
    sql = """
            SELECT * FROM pedido_edit(
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::integer,
                {}::double precision,
                {}::double precision,
                {}::double precision,
                {}::boolean,
                {}::boolean,
                {}::boolean,
                {}::boolean,
                '{}'::text,
                '{}'::text,
                '{}'::text,
                '{}'::character varying,
                '{}'::character varying,
                '{}'::character varying,
                '{}'::character varying,
                '{}'::character varying,
                {}::grid_renglon_pedido[]
            ) AS msg
            """.format(
                usuario_id,
                agente_id,
                cliente_id,
                cliente_df_id,
                almacen_id,
                moneda_id,
                prov_credias_id,
                cfdi_met_pago_id,
                forma_pago_id,
                cfdi_uso_id,
                pedido_id,
                tasa_retencion_immex,
                tipo_cambio,
                porcentaje_descto,
                descto_allowed,
                enviar_obser_fac,
                flete_enabled,
                enviar_ruta,
                observaciones.replace("'", "''"),
                motivo_descto.replace("'", "''"),
                transporte.replace("'", "''"),
                fecha_compromiso.replace("'", "''"),
                lugar_entrega.replace("'", "''"),
                orden_compra.replace("'", "''"),
                num_cuenta.replace("'", "''"),
                folio_cot.replace("'", "''"),
                grid_detalle_str
            )

    rmsg = run_stored_procedure(sql)
    return rmsg[0]


def cancel_pedido(pedido_id, usuario_id):

    """Calls database function in order to cancel a sales order"""
    sql = """
            SELECT * FROM pedido_cancel(
                {}::integer,
                {}::integer
            ) AS msg
            """.format(
                pedido_id,
                usuario_id
            )

    rmsg = run_stored_procedure(sql)
    return rmsg[0]


def convert_to_sql_array_literal(grid_detalle):
    rens_str = "array["
    first = True
    
    for s in grid_detalle:
        if not first:
            rens_str += ", "
        
        rens_str += (
            "("                    +
            str(s.id)              + ", " +
            str(s.toKeep)          + ", " +
            str(s.invProdId)       + ", " +
            str(s.presentacionId)  + ", " +
            str(s.cantidad)        + ", " +
            str(s.precioUnitario)  + ", " +
            str(s.gralImpId)       + ", " +
            str(s.valorImp)        + ", " +
            str(s.invProdUnidadId) + ", " +
            str(s.gralIepsId)      + ", " +
            str(s.valorIeps)       + ", " +
            str(s.descto)          + ", " +
            str(s.cotId)           + ", " +
            str(s.cotDetalleId)    + ", " +
            str(s.requiereAut)     + ", " +
            str(s.autorizado)      + ", " +
            str(s.precioAut)       + ", " +
            str(s.gralUsrIdAut)    + ", " +
            str(s.gralImptosRetId) + ", " +
            str(s.tasaRet)         +
            ")"
        )
        first = False
    
    rens_str += "]"

    return rens_str
