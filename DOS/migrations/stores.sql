
CREATE FUNCTION public.poc_val_cusorder(
    usr_id integer,
    curr_val character varying,
    date_lim character varying,
    pay_met integer,
    account character varying,
    matrix text[]
) RETURNS text
    LANGUAGE plpgsql
    AS $_$

DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Validation of customer order >>
    -- >> Version: CDGB                >>
    -- >> Date: 20/Jul/2017            >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    valor_retorno text := '';
    total_existencia double precision;
    incluye_modulo_produccion boolean;

    -- Variable que indica si se debe controlar las existencias por presentaciones
    controlExisPres boolean := false;

    emp_id integer;
    suc_id integer;
    id_almacen integer;
    facpar record;
    mask_general character varying;
    match_cadena boolean;

    -- number of rows within matrix
    no_rows integer;

    -- row with cell values
    row_cells text[];

    -- a counter for loops iterations
    counter integer;

    -- Cantidad en la unidad de Venta, esto se utiliza cuando la unidad del producto es diferente a la de venta
    cantUnidadVenta double precision := 0;

    -- Cantidad de la existencia convertida a la unidad de venta, esto se utiliza cuando la unidad del producto es diferente a la de venta
    cantExisUnidadVenta double precision:=0;

    cant_reservada_anterior double precision:=0;
    cantPresReservAnterior double precision:=0;
    cantPresAsignado double precision:=0;
    cambiaUnidadMedida boolean:=false;

    --Equivalencia de la presentacion en la unidad del producto
    equivalenciaPres double precision:=0;

    -- Existencia actual de la presentacion
    exisActualPres double precision:=0;

    -- Id de la unidad de medida del producto
    idUnidadMedida integer := 0;
    tipo integer;

    -- Nombre de la unidad de medida del producto
    nombreUnidadMedida character varying := '';

    -- Densidad del producto
    densidadProd double precision := 0;

    -- Numero de decimales permitidos para la unidad
    noDecUnidad integer := 0;

    -- cell indexes as per column order inside matrix
    C_DELFLAG  integer := 1;
    C_DETID    integer := 2;
    C_PRODID   integer := 3;
    C_PRESENID integer := 4;
    C_QUANTITY integer := 6;
    C_NOTR     integer := 9;
    C_SELECT   integer := 10;
    C_UNIT     integer := 11;

BEGIN

    -- Se deducen variables con respecto al usuario
    -- ejecutando la validacion
    SELECT gral_suc.empresa_id,
        gral_usr_suc.gral_suc_id,
        inv_suc_alm.almacen_id
    FROM gral_usr_suc
    JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    JOIN inv_suc_alm ON inv_suc_alm.sucursal_id = gral_suc.id
    WHERE gral_usr_suc.gral_usr_id = usr_id
    INTO emp_id, suc_id, id_almacen;

    -- Obtener parametros para la facturacion
    SELECT * FROM fac_par WHERE gral_suc_id = suc_id INTO facpar;

    -- query para verificar si la Empresa actual incluye Modulo de Produccion
    SELECT incluye_produccion, control_exis_pres
    FROM gral_emp WHERE id = emp_id
    INTO incluye_modulo_produccion, controlExisPres;

    -- Tomar el id del almacen para ventas
    id_almacen := facpar.inv_alm_id;

    IF curr_val = '' THEN
        -- Es necesario ingresar el tipo de cambio
        valor_retorno := ''||valor_retorno||'tc:Es necesario ingresar el tipo de cambio___';
    END IF;

    IF date_lim = '' THEN
        -- Es necesario ingresar la Fecha de Compromiso
        valor_retorno := ''||valor_retorno||'fcompromiso:Es necesario ingresar la Fecha de Compromiso___';
    END IF;

    IF pay_met = 2 OR pay_met = 3 THEN
        IF account = '' THEN
            -- Es necesario ingresar los ultimos 4 digitos de la tarjeta
            valor_retorno := ''||valor_retorno||'nocuenta:Es necesario ingresar los ultimos 4 digitos de la tarjeta___';
        ELSE
            IF (SELECT account ~ '^([0-9]{4})+["NA"]{2}$') THEN
                -- Es necesario ingresar 4 digitos
                valor_retorno := ''||valor_retorno|| 'nocuenta:Es necesario ingresar 4 digitos.___';
            END IF;
        END IF;
    END IF;

    no_rows := array_length(matrix, 1);
    counter := 1;
    IF matrix[1] != 'sin datos' THEN
        FOR counter IN 1 .. no_rows LOOP
            SELECT INTO row_cells string_to_array(matrix[counter], '___');

            -- 0: eliminado
            -- 1: no esta eliminado
            IF row_cells[ C_DELFLAG ]::integer <> 0 THEN
                IF trim( row_cells[ C_QUANTITY ] ) = '' THEN
                    -- Es necesario ingresar la cantidad
                    valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':Es necesario ingresar la cantidad___';
                ELSE
                    IF (SELECT trim( row_cells[ C_QUANTITY ] ) ~ '^([0-9]+[.]?[0-9]*|[.][0-9]+)$') THEN
                        IF row_cells[ C_QUANTITY ]::double precision < 0.000001 THEN
                            -- La cantidad debe ser mayor que cero
                            valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':La cantidad debe ser mayor que cero___';
                        ELSE
                            --obtener el tipo de producto y el numero de Decimales Permitidos
                            SELECT inv_prod.tipo_de_producto_id AS tipo_producto, inv_prod.unidad_id,
                                inv_prod_unidades.titulo, inv_prod.densidad,
                                (CASE WHEN inv_prod_unidades.id IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec
                            FROM inv_prod LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id
                            WHERE inv_prod.id = row_cells[ C_PRODID ]::integer
                            INTO tipo, idUnidadMedida, nombreUnidadMedida, densidadProd, noDecUnidad;

                            --Tomamos la cantidad en la unidad de Venta seleccionada por el usuario
                            cantUnidadVenta := row_cells[ C_QUANTITY ]::double precision;

                            IF cambiaUnidadMedida THEN
                                IF idUnidadMedida::integer <> row_cells[ C_UNIT ]::integer THEN
                                    IF densidadProd IS NULL OR densidadProd=0 THEN
                                        densidadProd := 1;
                                    END IF;
                                    EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''KILO*'';' INTO match_cadena;
                                    IF match_cadena=true THEN
                                        --Convertir a kilos
                                        row_cells[ C_QUANTITY ] := row_cells[ C_QUANTITY ]::double precision * densidadProd;
                                    ELSE
                                        EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''LITRO*'';' INTO match_cadena;
                                        IF match_cadena=true THEN
                                            --Convertir a Litros
                                            row_cells[ C_QUANTITY ] := row_cells[ C_QUANTITY ]::double precision / densidadProd;
                                        END IF;
                                    END IF;
                                END IF;
                            END IF;

                            -- Redondear la Cantidad
                            row_cells[ C_QUANTITY ] := round(row_cells[ C_QUANTITY ]::numeric, noDecUnidad)::double precision;
                            cantUnidadVenta := round(cantUnidadVenta::numeric, noDecUnidad)::double precision;

                            -- Si el tipo de producto es diferente de 4, hay que validar existencias
                            -- tipo=4 Servicios
                            -- para el tipo servicios no se debe validar existencias
                            IF tipo <> 4 THEN
                                IF incluye_modulo_produccion = FALSE THEN
                                    -- Aqui entra si la Empresa NO INCLUYE Modulo de Produccion
                                    -- Se debe validar existencias de los productos tipo 1,2,5,6,7,8
                                    -- tipo = 1 Normal o Terminado
                                    -- tipo = 2 Subensable o Formulacion o Intermedio
                                    -- tipo = 5 Refacciones
                                    -- tipo = 6 Accesorios
                                    -- tipo = 7 Materia Prima
                                    -- tipo = 8 Prod. en Desarrollo
                                    IF tipo::integer = 1 OR tipo::integer = 2 OR tipo::integer = 5 OR tipo::integer = 6 OR tipo::integer = 7 OR tipo::integer = 8 THEN
                                        -- Llamada a proc que devuelve la existencia del producto.
                                        -- El tipo de busqueda de existencia es 1=Busqueda en el almacen de la Sucursal
                                        -- el valor false que se le esta pasando es para indicarle que en las existencias
                                        -- no incluya reservados, y que solo me devualva existencias disponibles
                                        SELECT inv_calculo_existencia_producto AS existencia
                                        FROM inv_calculo_existencia_producto(1, false, row_cells[ C_PRODID ]::integer, usr_id, id_almacen)
                                        INTO total_existencia;

                                        -- Asignanos el total de la venta
                                        cantExisUnidadVenta := total_existencia;
                                        IF cambiaUnidadMedida THEN
                                            IF idUnidadMedida::integer <> row_cells[ C_UNIT ]::integer THEN
                                                EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''KILO*'';' INTO match_cadena;
                                                IF match_cadena = true THEN
                                                    -- Convertir a litros la existencia para mostrar el warning
                                                    cantExisUnidadVenta := cantExisUnidadVenta::double precision / densidadProd::double precision;
                                                ELSE
                                                    EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''LITRO*'';' INTO match_cadena;
                                                    IF match_cadena = true THEN
                                                        -- Convertir a Kilos la existencia para mostrar el warning
                                                        cantExisUnidadVenta := cantExisUnidadVenta::double precision * densidadProd::double precision;
                                                    END IF;
                                                END IF;
                                            END IF;
                                        END IF;

                                        -- si es diferente de cero estamos en editar
                                        IF row_cells[ C_DETID ]::integer > 0 THEN
                                            -- Buscamos la cantidad reservada anterior
                                            SELECT reservado FROM poc_pedidos_detalle WHERE id = row_cells[ C_DETID ]::integer INTO cant_reservada_anterior;

                                            -- le sumamos a la existencia la cantidad reservada anterior para tener la existencia real
                                            total_existencia := total_existencia::double precision + cant_reservada_anterior::double precision;
                                        END IF;

                                        -- Redondear el total_existencia
                                        total_existencia := round(total_existencia::numeric, noDecUnidad)::double precision;

                                        IF facpar.permitir_req_com THEN
                                            -- AQUI ENTRA CUANDO LA CONFIGURACION PERMITE GENERAR REQUISICION DE COMPRA
                                            -- tipo = 7 Materia Prima
                                            IF tipo::integer = 7 THEN
                                                -- Seleccionado = 0 indica que no se ha marcado para enviar a produccion la cantidad que falta
                                                IF row_cells[ C_SELECT ]='0' THEN
                                                    IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                        valor_retorno := ''|| valor_retorno || 'backorder:cantidad' || row_cells[ C_NOTR ] ||
                                                            ':' || cantExisUnidadVenta || '___';
                                                    END IF;

                                                    IF total_existencia<=0 THEN
                                                        valor_retorno := ''|| valor_retorno || 'cantidad' || row_cells[ C_NOTR ] ||
                                                            ':Disponible=0,  Pedido=' || cantUnidadVenta ||
                                                            '. Seleccione la casilla para enviar una Requisici&oacute;n de Compra.___';
                                                    ELSE
                                                       	IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                            valor_retorno := '' || valor_retorno ||'cantidad'|| row_cells[ C_NOTR ] ||
                                                                ':Disponible=' || cantExisUnidadVenta ||',  Pedido='|| cantUnidadVenta ||
                                                                '. Seleccione la casilla para enviar una Requisici&oacute;n de Compra.___';
                                                        END IF;
                                                    END IF;
                                                END IF;
                                            END IF;

                                            -- Solo se debe validar existencias de productos tipo 5,6
                                            -- tipo = 5 Refacciones
                                            -- tipo=6 Accesorios
                                            IF tipo::integer = 1 OR tipo::integer = 2 OR tipo::integer = 5 OR tipo::integer = 6 OR tipo::integer = 8 THEN
                                                IF total_existencia<=0 THEN
                                                    valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':El producto tiene Existencia 0 en Almacen.___';
                                                ELSE
                                                    IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                        valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':Disponibles '||cantExisUnidadVenta||',  usted esta intentando vender '||cantUnidadVenta||'___';
                                                    END IF;
                                                END IF;
                                            END IF;
                                        ELSE
                                            -- AQUI ENTRA CUANDO LA CONFIGURACION NO PERMITE GENERAR REQUISICION DE COMPRA
                                            IF total_existencia<=0 THEN
                                                valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':El producto tiene Existencia 0 en Almacen.___';
                                            ELSE
                                                IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                    valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':Disponibles '||cantExisUnidadVenta||',  usted esta intentando vender '||cantUnidadVenta||'___';
                                                END IF;
                                            END IF;
                                        END IF;
                                    END IF;
                                ELSE
                                    -- Aqui entra si la Empresa SI INCLUYE Modulo de Produccion
                                    -- llamada a proc que devuelve la existencia del producto.
                                    -- El tipo de busqueda de existencia es 1=Busqueda en el almacen de la Sucursal
                                    -- el valor false que se le esta pasando es para indicarle que en las
                                    -- existencias no incluya reservados, y que solo me devualva existencias disponibles
                                    SELECT inv_calculo_existencia_producto AS existencia FROM inv_calculo_existencia_producto(1, false, row_cells[ C_PRODID ]::integer, row_cells[ C_PRODID ]::integer, id_almacen) INTO total_existencia; 

                                    -- Asignanos el total de la venta
                                    cantExisUnidadVenta := total_existencia;
                                    IF cambiaUnidadMedida THEN
                                        IF idUnidadMedida::integer <> row_cells[ C_UNIT ]::integer THEN
                                            EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''KILO*'';' INTO match_cadena;
                                            IF match_cadena = true THEN
                                                -- Convertir a litros la existencia para mostrar el warning
                                                cantExisUnidadVenta := cantExisUnidadVenta::double precision / densidadProd::double precision;
                                            ELSE
                                                EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''LITRO*'';' INTO match_cadena;
                                                IF match_cadena=true THEN
                                                    -- Convertir a Kilos la existencia para mostrar el warning
                                                    cantExisUnidadVenta := cantExisUnidadVenta::double precision * densidadProd::double precision;
                                                END IF;
                                            END IF;
                                        END IF;
                                    END IF;

                                    -- Si es diferente de cero estamos en editar
                                    IF row_cells[ C_DETID ]::integer > 0 THEN
                                        -- buscamos la cantidad reservada anterior
                                        SELECT reservado FROM poc_pedidos_detalle
                                        WHERE id = row_cells[ C_DETID ]::integer
                                        INTO cant_reservada_anterior;

                                        -- le sumamos a la existencia la cantidad reservada anterior para tener la existencia real
                                        total_existencia := total_existencia + cant_reservada_anterior;
                                    END IF;

                                    -- Redondear el total_existencia
                                    total_existencia := round(total_existencia::numeric, noDecUnidad)::double precision;

                                    -- tipo = 1 Normal o Terminado
                                    -- tipo = 2 Subensable o Formulacion o Intermedio
                                    -- tipo = 8 Prod. en Desarrollo
                                    IF tipo::integer = 1 OR tipo::integer = 2 OR tipo::integer = 8 THEN
                                        -- seleccionado=0 indica que no se ha marcado para enviar a produccion la cantidad que falta
                                        IF row_cells[ C_SELECT ] = '0' THEN
                                            IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                valor_retorno := ''||valor_retorno||'backorder:cantidad'||row_cells[ C_NOTR ]||':'||cantExisUnidadVenta||'___';
                                            END IF;

                                            IF total_existencia <= 0 THEN
                                                valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ] ||
                                                    ':Disponible=0,  Pedido='||cantUnidadVenta||'. Seleccione la casilla para enviar a producci&oacute;n.___';
                                            ELSE
                                                IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                    valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ] ||
                                                        ':Disponible='||cantExisUnidadVenta||',  Pedido='||cantUnidadVenta ||
                                                        '. Seleccione la casilla para enviar a producci&oacute;n.___';
                                                END IF;
                                            END IF;
                                        END IF;
                                    END IF;

                                    IF facpar.permitir_req_com THEN
                                        -- AQUI ENTRA CUANDO LA CONFIGURACION PERMITE GENERAR REQUISICION DE COMPRA
                                        -- tipo = 7 Materia Prima
                                        IF tipo::integer = 7 THEN
                                            -- seleccionado=0 indica que no se ha marcado para enviar a produccion la cantidad que falta
                                            IF row_cells[ C_SELECT ] = '0' THEN
                                                IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                    valor_retorno := ''||valor_retorno||'backorder:cantidad'||row_cells[ C_NOTR ]||':'||cantExisUnidadVenta||'___';
                                                END IF;

                                                IF total_existencia <= 0 THEN
                                                    valor_retorno := ''|| valor_retorno || 'cantidad' || row_cells[ C_NOTR ] ||
                                                        ':Disponible=0,  Pedido='||cantUnidadVenta ||
                                                        '. Seleccione la casilla para enviar una Requisici&oacute;n de Compra.___';
                                                ELSE

                                                IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                    valor_retorno := ''|| valor_retorno || 'cantidad' || row_cells[ C_NOTR ] ||
                                                        ':Disponible=' || cantExisUnidadVenta || ',  Pedido='||cantUnidadVenta ||
                                                        '. Seleccione la casilla para enviar una Requisici&oacute;n de Compra.___';
                                                END IF;
                                            END IF;
                                        END IF;
                                    END IF;

                                    -- Solo se debe validar existencias de productos tipo 5,6
                                    -- tipo = 5 Refacciones
                                    -- tipo = 6 Accesorios
                                    IF tipo::integer = 5 OR tipo::integer = 6 THEN
                                        IF total_existencia <= 0 THEN
                                            valor_retorno := ''|| valor_retorno || 'cantidad' || row_cells[ C_NOTR ] ||
                                                ':El producto tiene Existencia 0 en Almacen___';
                                        ELSE
                                            IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                valor_retorno := ''|| valor_retorno || 'presentacion' || row_cells[ C_NOTR ] ||
                                                    ':Disponibles '|| cantExisUnidadVenta ||',  usted esta intentando vender '||cantUnidadVenta||'___';
                                            END IF;
                                        END IF;
                                    END IF;
                                ELSE
                                    -- AQUI ENTRA CUANDO LA CONFIGURACION NO PERMITE GENERAR REQUISICION DE COMPRA
                                    -- Solo se debe validar existencias de productos tipo 5,6,7
                                    -- tipo = 5 Refacciones
                                    -- tipo = 6 Accesorios
                                    -- tipo = 7 Materia Prima
                                    IF tipo::integer = 5 OR tipo::integer = 6 OR tipo::integer = 7 THEN
                                        IF total_existencia <= 0 THEN
                                            valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||
                                                ':El producto tiene Existencia 0 en Almacen___';
                                        ELSE
                                            IF total_existencia < row_cells[ C_QUANTITY ]::double precision THEN
                                                valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ]||
                                                    ':Disponibles '||cantExisUnidadVenta||',  usted esta intentando vender '||cantUnidadVenta||'___';
                                            END IF;
                                        END IF;
                                    END IF;
                                END IF;
                            END IF;

                            -- verificar si hay que validar existencias de Presentaciones
                            IF controlExisPres = true THEN
                                -- Verificar si hay que validar las existencias de presentaciones desde el Pedido.
                                -- TRUE = Validar presentaciones desde el Pedido
                                -- FALSE = No validar presentaciones desde el Pedido
                                IF facpar.validar_pres_pedido = true THEN
                                    -- Buscar la existencia actual de la Presentacion
                                    SELECT (inicial::double precision + entradas::double precision - salidas::double precision -reservado::double precision) AS exi
                                    FROM inv_exi_pres WHERE inv_alm_id=id_almacen::integer AND inv_prod_id=row_cells[ C_PRODID ]::integer AND inv_prod_presentacion_id=row_cells[ C_PRESENID ]::integer 
                                    INTO exisActualPres;

                                    IF exisActualPres IS NULL THEN
                                        exisActualPres := 0;
                                    END IF;

                                    IF exisActualPres > 0 THEN
                                        -- Si es diferente de cero estamos en editar,por lo tanto hay que buscar la cantidad reservada anterior.
                                        IF row_cells[ C_DETID ]::integer > 0 THEN
                                            -- buscamos la cantidad reservada anterior
                                            SELECT (poc_pedidos_detalle.reservado::double precision / inv_prod_presentaciones.cantidad::double precision) AS cant_pres
                                            FROM poc_pedidos_detalle
                                            JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=poc_pedidos_detalle.presentacion_id
                                            WHERE poc_pedidos_detalle.id=row_cells[ C_DETID ]::integer
                                            INTO cantPresReservAnterior;

                                            -- redondear la Cantidad de la Presentacion reservada Anteriormente
                                            cantPresReservAnterior := round(cantPresReservAnterior::numeric, noDecUnidad)::double precision;

                                            -- sumar la cantidad reservada anterior para tener la existencia real
                                            exisActualPres = exisActualPres::double precision + cantPresReservAnterior::double precision;
                                        END IF;

					                    -- redondear la Existencia actual de Presentaciones
                                        exisActualPres := round(exisActualPres::numeric, noDecUnidad)::double precision;

                                        -- buscar la equivalencia de la Presentacion
                                        SELECT cantidad  FROM inv_prod_presentaciones WHERE id = row_cells[ C_PRESENID ]::integer INTO equivalenciaPres;

                                        -- convertir a su equivalencia en Presentacion, la cantidad de la partida actual del pedido
                                        cantPresAsignado := row_cells[ C_QUANTITY ]::double precision / equivalenciaPres::double precision;

                                        -- redondear la cantidad de Presentaciones Asignado en la partida
                                        cantPresAsignado := round(cantPresAsignado::numeric, noDecUnidad)::double precision;

                                        IF exisActualPres::double precision < cantPresAsignado::double precision THEN
                                                IF incluye_modulo_produccion=true OR facpar.permitir_req_com=TRUE THEN

                                                    -- Si incluye modulo de produccion ó la configuracion permite generar requisiciones cuando no hay exisencia
                                                    IF incluye_modulo_produccion THEN
                                                        IF tipo::integer = 1 OR tipo::integer = 2 OR tipo::integer = 8 THEN
                                                            IF row_cells[ C_SELECT ] = '0' THEN
                                                                valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ] ||
                                                                    ':Disponibles='||exisActualPres||',  Venta='||cantPresAsignado ||
                                                                    '. No hay existencia suficiente en esta presentacion.___';
                                                            END IF;
                                                        END IF;
                                                    END IF;

                                                    -- Si la configuracion permite generar requisiciones en automatico
                                                    IF facpar.permitir_req_com THEN
                                                        IF tipo::integer = 7 THEN
                                                            IF row_cells[ C_SELECT ] = '0' THEN
                                                                valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ] ||
                                                                    ':Disponibles='||exisActualPres||',  Venta='||cantPresAsignado ||
                                                                    '. No hay existencia suficiente en esta presentacion.___';
                                                            END IF;
                                                        END IF;
                                                    END IF;

                                                    IF tipo::integer = 5 OR tipo::integer = 6 THEN
                                                        valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ]||
                                                            ':Disponibles='||exisActualPres||',  Venta='||cantPresAsignado||
                                                            '. No hay existencia suficiente en esta presentacion.___';
                                                    END IF;
                                                ELSE
                                                    valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ]||
                                                        ':Disponibles='||exisActualPres||',  Venta='||cantPresAsignado||
                                                        '. No hay existencia suficiente en esta presentacion.___';
                                                END IF;
                                            END IF;
                                        ELSE
                                            IF incluye_modulo_produccion=true OR facpar.permitir_req_com=TRUE THEN
                                                -- Si incluye modulo de produccion ó la configuracion
                                                -- permite generar requisiciones cuando no hay exisencia

                                                IF incluye_modulo_produccion THEN 
                                                    IF tipo::integer = 1 OR tipo::integer = 2 OR tipo::integer = 8 THEN
                                                        IF row_cells[ C_SELECT ] = '0' THEN
                                                            valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ]||
                                                                ':No hay existencia en esta presentacion.___';
                                                        END IF;
                                                    END IF;
                                                END IF;

                                                IF facpar.permitir_req_com THEN
                                                    IF tipo::integer = 7 THEN
                                                        IF row_cells[ C_SELECT ] = '0' THEN
                                                            valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ]||
                                                                ':No hay existencia en esta presentacion.___';
                                                        END IF;
                                                    END IF;
                                                END IF;

                                                IF tipo::integer = 5 OR tipo::integer = 6 THEN
                                                    valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ] ||
                                                        ':No hay existencia en esta presentacion.___';
                                                END IF;
                                            ELSE
                                                valor_retorno := ''||valor_retorno||'presentacion'||row_cells[ C_NOTR ] ||
                                                    ':No hay existencia en esta presentacion.___';
                                            END IF;
                                        END IF;
                                    END IF;
                                END IF;
                            END IF;
                        END IF;
                    ELSE
                        -- Aqui entra porque el campo cantidad trae un valor no numerico
                        valor_retorno := ''||valor_retorno||'cantidad'||row_cells[ C_NOTR ]||':El valor para Cantidad es incorrecto, tiene mas de un punto('||row_cells[ C_QUANTITY ]||')___';
                    END IF;
                END IF;
            END IF;
        END LOOP;
    END IF;

    IF valor_retorno = '' THEN
        valor_retorno := 'true';
        RETURN valor_retorno;
    ELSE
        RETURN valor_retorno;
    END IF;

END;

$_$;



CREATE FUNCTION public.inv_calculo_existencia_producto(
    tipo_calculo integer,
    incluye_reservados boolean,
    id_prod integer,
    id_user integer,
    id_almacen integer
) RETURNS double precision
LANGUAGE plpgsql
AS $$

DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name: Calculador de existencia                                           >>
    -- >> Version: MAZINGER                                                        >>
    -- >> Date: 15/Dic/2020                                                        >>
    -- >>                                                                          >>
    -- >> Existen dos formas de calcular la existencia de el producto solicitado.  >>
    -- >> 1 - Con respecto a un almacen en especifico                              >>
    -- >> 2.- Con respecto a todos los almacenes de la empresa                     >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    cadena_sql text = '';
    subquery text = '';

    fila_almacenes record;

    id_almacenes character varying := '';
    primer_registro smallint = 0;
    ano_actual integer;
    mes_actual integer;
    emp_id integer;
    suc_id integer;
    incrementa int := 1;
    existencia double precision;
    descontar_reservados_transito character varying = '';

    espacio_tiempo_ejecucion timestamp with time zone = now();

BEGIN

    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;
    SELECT EXTRACT(MONTH FROM espacio_tiempo_ejecucion) INTO mes_actual;

    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
    FROM gral_usr_suc
    JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE gral_usr_suc.gral_usr_id=id_user
    INTO emp_id, suc_id;

    -- Busqueda de Existencia por un almacen en especifico
    IF tipo_calculo = 1 THEN
        id_almacenes := id_almacen;
    END IF;

    -- Busqueda de Existencia en Todos los almacenes de la Empresa
    IF tipo_calculo = 2 THEN

        -- Consulta para obtener todos los alacenes de la empresa
        cadena_sql := 'SELECT distinct inv_suc_alm.almacen_id
                       FROM gral_suc JOIN inv_suc_alm ON inv_suc_alm.sucursal_id=gral_suc.id
                       WHERE gral_suc.empresa_id='||emp_id||' ORDER BY inv_suc_alm.almacen_id;';

        -- Variable para saber si es el primer almacen en la cadena
        -- (Horrible approach but still working)
        primer_registro := 0;

        FOR fila_almacenes IN EXECUTE(cadena_sql) LOOP

            IF primer_registro = 0 THEN

                id_almacenes := id_almacenes||'';
            ELSE

                id_almacenes := id_almacenes||',';
            END IF;

            id_almacenes := id_almacenes||fila_almacenes.almacen_id;
            primer_registro := 1;

        END LOOP;

    END IF;


    -- Si el id del almacen es null,
    -- le asignamos un cero para que no genere error al ejecutar el query
    -- (Horrible approach but still working)
    IF id_almacenes IS NULL OR id_almacenes = '' THEN
        id_almacenes := '0';
    END IF;

    -- Descuenta reservados
    -- y transito en el calculo de las existencias
    IF incluye_reservados = FALSE THEN
        descontar_reservados_transito :=' - transito - reservado ';
    END IF;

    -- Reusar variable
    cadena_sql:='';

    -- Crear formula para calcular la existencia actual del producto
    cadena_sql:= 'SELECT (exi_inicial '||descontar_reservados_transito||' ';

    WHILE incrementa <= mes_actual LOOP
        cadena_sql:=cadena_sql ||' + entradas_'||incrementa||' - salidas_'||incrementa;
        incrementa:= incrementa + 1;
    END LOOP;

    cadena_sql:= cadena_sql||') AS exi FROM inv_exi WHERE inv_prod_id='||id_prod||' AND ano='||ano_actual||' AND inv_alm_id IN ('||id_almacenes||')';

    -- Obtiene existencia del producto
    subquery := 'SELECT sum(exi) as exi FROM ('||cadena_sql||') AS sbt;';

    EXECUTE subquery INTO existencia;

    IF existencia IS NULL OR existencia <= 0 THEN

        existencia := 0;
    END IF;

    RETURN existencia;

END;

$$;



CREATE FUNCTION public.fac_save_xml(file_xml character varying, prefact_id integer, usr_id integer, creation_date character varying, no_id_emp character varying, serie character varying, _folio character varying, items_str character varying, traslados_str character varying, retenciones_str character varying, reg_fiscal character varying, pay_method character varying, exp_place character varying, purpose character varying, no_aprob character varying, ano_aprob character varying, rfc_custm character varying, rs_custm character varying, account_number character varying, total_tras double precision, subtotal_with_desc double precision, total double precision, refact boolean, folio_fiscal character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Save xml data in DB          >>
    -- >> Version: CDGB                >>
    -- >> Date: 20/Jul/2017            >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

DECLARE
    str_filas text[];
    --Total de elementos de arreglo
    total_filas integer;
    --Contador de filas o posiciones del arreglo
    cont_fila integer;

    valor_retorno character varying = '';
    ultimo_id integer:=0;
    ultimo_id_det integer:=0;
    id_tipo_consecutivo integer=0;
    prefijo_consecutivo character varying = '';
    nuevo_consecutivo bigint=0;
    nuevo_folio character varying = '';
    ultimo_id_proceso integer =0;
    tipo_de_documento integer =0;
    fila_fac_rem_doc record;

    app_selected integer;

    emp_id integer:=0;
    suc_id integer:=0;
    suc_id_consecutivo integer:=0; --sucursal de donde se tomara el consecutivo
    id_almacen integer;
    espacio_tiempo_ejecucion timestamp with time zone = now();
    ano_actual integer:=0;
    mes_actual integer:=0;
    factura_fila record;
    prefactura_fila record;
    prefactura_detalle record;
    factura_detalle record;
    tiene_pagos integer:=0;
    identificador_nuevo_movimiento integer;
    tipo_movimiento_id integer:=0;
    exis integer:=0;
    sql_insert text;
    sql_update text;
    sql_select text;
    sql_select2 character varying:='';
    cantidad_porcentaje double precision:=0;
    id_proceso integer;
    bandera_tipo_4 boolean;--bandera que identifica si el producto es tipo 4, true=tipo 4, false=No es tipo4
    serie_folio_fac character varying:='';
    tipo_cam double precision := 0;

    numero_dias_credito integer:=0;
    fecha_de_vencimiento timestamp with time zone;

    importe_del_descto_partida double precision := 0;
    importe_partida_con_descto double precision := 0;
    suma_descuento double precision := 0;
    suma_subtotal_con_descuento double precision := 0;

    importe_partida double precision := 0;
    importe_ieps_partida double precision := 0;
    impuesto_partida double precision := 0;
    monto_subtotal double precision := 0;
    suma_ieps double precision := 0;
    suma_total double precision := 0;
    monto_impuesto double precision := 0;
    total_retencion double precision := 0;
    retener_iva boolean := false;
    tasa_retencion double precision := 0;
    retencion_partida double precision := 0;
    suma_retencion_de_partidas double precision := 0;
    suma_retencion_de_partidas_globlal double precision:= 0;

    --Estas variables se utilizan en caso de que se facture un pedido en otra moneda
    suma_descuento_global double precision := 0;
    suma_subtotal_con_descuento_global double precision := 0;
    monto_subtotal_global double precision := 0;
    suma_ieps_global double precision := 0;
    monto_impuesto_global double precision := 0;
    total_retencion_global double precision := 0;
    suma_total_global double precision := 0;
    cant_original double precision := 0;

    total_factura double precision;
    id_moneda_factura integer:=0;
    suma_pagos double precision:=0;

    costo_promedio_actual double precision:=0;
    costo_referencia_actual double precision:=0;

    id_osal integer := 0;
    fila record;
    fila_detalle record;
    facpar record;--parametros de Facturacion

    id_df integer:=0;--id de la direccion fiscal
    result character varying:='';

    noDecUnidad integer:=0;--numero de decimales permitidos para la unidad
    exisActualPres double precision:=0;--existencia actual de la presentacion
    equivalenciaPres double precision:=0; --equivalencia de la presentacion en la unidad del producto
    cantPres double precision:=0; --Cantidad que se esta Intentando traspasar
    cantPresAsignado double precision:=0;
    cantPresReservAnterior double precision:=0;

    controlExisPres boolean; --Variable que indica  si se debe controlar Existencias por Presentacion
    partida_facturada boolean;--Variable que indica si la cantidad de la partida ya fue facturada en su totalidad
    actualizar_proceso boolean; --Indica si hay que actualizar el flujo del proceso. El proceso se debe actualizar cuando ya no quede partidas vivas
    id_pedido integer;--Id del Pedido que se esta facturando
    --Id de la unidad de medida del producto
    idUnidadMedida integer:=0;
    --Nombre de la unidad de medida del producto
    nombreUnidadMedida character varying:=0;
    --Densidad del producto
    densidadProd double precision:=0;
    --Cantidad en la unidad del producto
    cantUnidadProd double precision:=0;
    --Id de la unidad de Medida de la Venta
    idUnidadMedidaVenta integer:=0;
    --Cantidad en la unidad de Venta, esto se utiliza cuando la unidad del producto es diferente a la de venta
    cantUnidadVenta double precision:=0;
    --Cantidad de la existencia convertida a la unidad de venta, esto se utiliza cuando la unidad del producto es diferente a la de venta
    cantExisUnidadVenta double precision:=0;
    match_cadena boolean:=false;

BEGIN

    app_selected := 13;
	
    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;
    SELECT EXTRACT(MONTH FROM espacio_tiempo_ejecucion) INTO mes_actual;
	
    --obtener id de empresa, sucursal
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
    FROM gral_usr_suc 
    JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE gral_usr_suc.gral_usr_id = usr_id
    INTO emp_id, suc_id;
	
    --Obtener parametros para la facturacion
    SELECT * FROM fac_par WHERE gral_suc_id=suc_id INTO facpar;
	
    --tomar el id del almacen para ventas
    id_almacen := facpar.inv_alm_id;
	
    --éste consecutivo es para el folio de Remisión y folio para BackOrder(poc_ped_bo)
    suc_id_consecutivo := facpar.gral_suc_id_consecutivo;

	--query para verificar si la Empresa actual incluye Modulo de Produccion y control de Existencias por Presentacion
    SELECT control_exis_pres FROM gral_emp WHERE id=emp_id INTO controlExisPres;
	
    --Inicializar en cero
    id_pedido:=0;
			
    tipo_de_documento := 1; --Factura
			
    serie_folio_fac:= serie||_folio;
			
    --extraer datos de la Prefactura
    SELECT * FROM erp_prefacturas WHERE id=prefact_id INTO prefactura_fila;
			
    --Obtener el numero de dias de credito
    SELECT dias FROM cxc_clie_credias WHERE id=prefactura_fila.terminos_id INTO numero_dias_credito;
			
    --calcula la fecha de vencimiento a partir de la fecha de la factura
    SELECT (to_char(espacio_tiempo_ejecucion,'yyyy-mm-dd')::DATE + numero_dias_credito)::timestamp with time zone AS fecha_vencimiento INTO fecha_de_vencimiento;
			
    IF prefactura_fila.moneda_id=1 THEN 
        tipo_cam:=1;
    ELSE
        tipo_cam:=prefactura_fila.tipo_cambio;
    END IF;
			
    --Toma la fecha de la Facturación. Ésta fecha es la misma que se le asigno al xml
    espacio_tiempo_ejecucion := translate(creation_date,'T',' ')::timestamp with time zone;
			
    --crea registro en fac_cfds
    INSERT INTO fac_cfds(
        rfc_cliente,--rfc_custm,
        serie,--serie,
        folio_del_comprobante_fiscal,--folio,
        numero_de_aprobacion,--no_aprob,
        monto_de_la_operacion,--total,
        monto_del_impuesto,--total_tras,
        estado_del_comprobante,--'1',
        nombre_archivo,--file_xml,
        momento_expedicion,--creation_date,
        razon_social,--rs_custm,
        tipo_comprobante,--'I',
        proposito,--purpose,
        anoaprovacion, --ano_aprob,
        serie_folio, --serie_folio_fac,
        conceptos, --items_str,
        impuestos_trasladados, --traslados_str,
        impuestos_retenidos, --retenciones_str,
        regimen_fiscal, --reg_fiscal,
        metodo_pago, --pay_method,
        numero_cuenta, --account_number,
        lugar_expedicion,--exp_place,
        tipo_de_cambio,--tipo_cam,
        gral_mon_id,--prefactura_fila.moneda_id,
        id_user_crea,-- usr_id
        empresa_id,--emp_id,
        sucursal_id,--suc_id,
        proceso_id--prefactura_fila.proceso_id
    ) VALUES(rfc_custm, serie, _folio, no_aprob, total, total_tras, '1', file_xml, creation_date, rs_custm, 'I', purpose, ano_aprob, serie_folio_fac, items_str, traslados_str, retenciones_str, reg_fiscal, pay_method, account_number, exp_place, tipo_cam, prefactura_fila.moneda_id, usr_id, emp_id, suc_id, prefactura_fila.proceso_id);


    --crea registro en erp_h_facturas
    INSERT INTO erp_h_facturas(
        cliente_id,--prefactura_fila.cliente_id,
        cxc_agen_id,--prefactura_fila.empleado_id,
        serie_folio,--serie_folio_fac,
        monto_total,--prefactura_fila.fac_total,
        saldo_factura,--prefactura_fila.fac_total,
        moneda_id,--prefactura_fila.moneda_id,
        tipo_cambio,--tipo_cam,
        momento_facturacion,--espacio_tiempo_ejecucion,
        fecha_vencimiento,--fecha_de_vencimiento,
        subtotal,--prefactura_fila.fac_subtotal,
        monto_ieps, --prefactura_fila.fac_monto_ieps,
        impuesto,--prefactura_fila.fac_impuesto,
        retencion,--prefactura_fila.fac_monto_retencion,
        orden_compra,--prefactura_fila.orden_compra,
        id_usuario_creacion, --usr_id,
        empresa_id, --emp_id,
        sucursal_id--suc_id
    )VALUES(prefactura_fila.cliente_id, prefactura_fila.empleado_id, serie_folio_fac, total, total, prefactura_fila.moneda_id, tipo_cam, espacio_tiempo_ejecucion, fecha_de_vencimiento, prefactura_fila.fac_subtotal, prefactura_fila.fac_monto_ieps, prefactura_fila.fac_impuesto, prefactura_fila.fac_monto_retencion, prefactura_fila.orden_compra, usr_id, emp_id, suc_id);

    --Crea registros en la tabla fac_docs
    INSERT INTO fac_docs(
        serie_folio,--serie_folio_fac,
        folio_pedido,--prefactura_fila.folio_pedido,
        cxc_clie_id,--prefactura_fila.cliente_id,
        moneda_id,--prefactura_fila.moneda_id,
        subtotal,--prefactura_fila.fac_subtotal,
        monto_ieps,--prefactura_fila.fac_monto_ieps,
        impuesto,--prefactura_fila.fac_impuesto,
        monto_retencion,--prefactura_fila.fac_monto_retencion,
        total,--prefactura_fila.fac_total,
        tasa_retencion_immex,--prefactura_fila.tasa_retencion_immex,
        tipo_cambio,--tipo_cam,
        proceso_id,--prefactura_fila.proceso_id,
        cxc_agen_id,--prefactura_fila.empleado_id,
        terminos_id,--prefactura_fila.terminos_id,
        fecha_vencimiento,--fecha_de_vencimiento
        orden_compra,--prefactura_fila.orden_compra,
        observaciones, --prefactura_fila.observaciones,
        fac_metodos_pago_id, --prefactura_fila.fac_metodos_pago_id,
        no_cuenta, --prefactura_fila.no_cuenta,
        enviar_ruta,--prefactura_fila.enviar_ruta,
        inv_alm_id,--prefactura_fila.inv_alm_id
        cxc_clie_df_id,--prefactura_fila.cxc_clie_df_id,
        momento_creacion,--translate(creation_date,'T',' ')::timestamp with time zone,,
        gral_usr_id_creacion, --usr_id,
        ref_id, --no_id_emp 
        monto_descto, --prefactura_fila.fac_monto_descto
        motivo_descto, --prefactura_fila.motivo_descto,
        subtotal_sin_descto, --subtotal_with_desc 
        ctb_tmov_id --prefactura_fila.ctb_tmov_id 
    ) VALUES (serie_folio_fac, prefactura_fila.folio_pedido, prefactura_fila.cliente_id, prefactura_fila.moneda_id, prefactura_fila.fac_subtotal, prefactura_fila.fac_monto_ieps, prefactura_fila.fac_impuesto, prefactura_fila.fac_monto_retencion, prefactura_fila.fac_total, prefactura_fila.tasa_retencion_immex, tipo_cam, prefactura_fila.proceso_id, prefactura_fila.empleado_id, prefactura_fila.terminos_id, fecha_de_vencimiento, prefactura_fila.orden_compra, prefactura_fila.observaciones, prefactura_fila.fac_metodos_pago_id, prefactura_fila.no_cuenta, prefactura_fila.enviar_ruta, prefactura_fila.inv_alm_id, prefactura_fila.cxc_clie_df_id, translate(creation_date,'T',' ')::timestamp with time zone, usr_id, no_id_emp, prefactura_fila.fac_monto_descto, prefactura_fila.motivo_descto, subtotal_with_desc, prefactura_fila.ctb_tmov_id) RETURNING id INTO ultimo_id;


    --Guarda la cadena del xml timbrado
    INSERT INTO fac_cfdis(tipo, ref_id, doc, gral_emp_id, gral_suc_id, fecha_crea, gral_usr_id_crea) 
    VALUES (1,no_id_emp,folio_fiscal,emp_id,suc_id,translate(creation_date,'T',' ')::timestamp with time zone, usr_id);


    -- bandera que identifica si el producto es tipo 4
    -- si es tipo 4 no debe existir movimientos en inventario
    bandera_tipo_4=TRUE;
    tipo_movimiento_id:=5;--Salida por Venta
    id_tipo_consecutivo:=21; --Folio Orden de Salida
    id_almacen := prefactura_fila.inv_alm_id;--almacen de donde se hara la salida

    -- Bandera que indica si se debe actualizar el flujo del proceso.
    -- El proceso solo debe actualizarse cuando no quede ni una sola partida viva
    actualizar_proceso:=true;

    -- refact=false:No es refacturacion
    -- tipo_documento=1:Factura
    IF refact IS NOT true AND prefactura_fila.tipo_documento=1 THEN
        -- aqui entra para tomar el consecutivo del folio  la sucursal actual
        UPDATE gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
        WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;

        -- concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
        nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
				
        -- genera registro en tabla inv_osal(Orden de Salida)
        INSERT INTO inv_osal(folio,estatus,erp_proceso_id,inv_mov_tipo_id,tipo_documento,folio_documento,fecha_exp,gral_app_id,cxc_clie_id,inv_alm_id,subtotal,monto_iva,monto_retencion,monto_total,folio_pedido,orden_compra,moneda_id,tipo_cambio,momento_creacion,gral_usr_id_creacion, gral_emp_id, gral_suc_id, monto_ieps)
        VALUES(nuevo_folio,0,prefactura_fila.proceso_id,tipo_movimiento_id,tipo_de_documento,serie_folio_fac,espacio_tiempo_ejecucion,app_selected,prefactura_fila.cliente_id,id_almacen, prefactura_fila.fac_subtotal, prefactura_fila.fac_impuesto, prefactura_fila.fac_monto_retencion, prefactura_fila.fac_total, prefactura_fila.folio_pedido,prefactura_fila.orden_compra,prefactura_fila.moneda_id,tipo_cam,espacio_tiempo_ejecucion,usr_id, emp_id, suc_id, prefactura_fila.fac_monto_ieps) RETURNING id INTO id_osal;
				
        -- genera registro del movimiento
        INSERT INTO inv_mov(observacion,momento_creacion,gral_usr_id, gral_app_id,inv_mov_tipo_id, referencia, fecha_mov )
        VALUES (prefactura_fila.observaciones,espacio_tiempo_ejecucion,usr_id,app_selected, tipo_movimiento_id, serie_folio_fac, translate(creation_date,'T',' ')::timestamp with time zone) RETURNING id INTO identificador_nuevo_movimiento;

    END IF;

    -- obtiene lista de productos de la prefactura
    sql_select:='';
    sql_select := 'SELECT  erp_prefacturas_detalles.id AS id_det,
        erp_prefacturas_detalles.producto_id,
        erp_prefacturas_detalles.presentacion_id,
        erp_prefacturas_detalles.cantidad AS cant_pedido,
        erp_prefacturas_detalles.cant_facturado,
        erp_prefacturas_detalles.cant_facturar AS cantidad,
        erp_prefacturas_detalles.tipo_impuesto_id,
        erp_prefacturas_detalles.valor_imp,
        erp_prefacturas_detalles.precio_unitario,
        inv_prod.tipo_de_producto_id as tipo_producto,
        erp_prefacturas_detalles.costo_promedio,
        erp_prefacturas_detalles.costo_referencia, 
        erp_prefacturas_detalles.reservado,
        erp_prefacturas_detalles.reservado AS nuevo_reservado,
        (CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.cantidad END) AS cant_equiv,
        (CASE WHEN inv_prod_unidades.id IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec,
        inv_prod.unidad_id AS id_uni_prod,
        inv_prod.densidad AS densidad_prod,
        inv_prod_unidades.titulo AS nombre_unidad,
        erp_prefacturas_detalles.inv_prod_unidad_id,
        erp_prefacturas_detalles.gral_ieps_id,
        erp_prefacturas_detalles.valor_ieps,
        (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN 0 ELSE erp_prefacturas_detalles.descto END) AS descto,
        (CASE WHEN erp_prefacturas_detalles.fac_rem_det_id IS NULL THEN 0 ELSE erp_prefacturas_detalles.fac_rem_det_id END) AS fac_rem_det_id,
        erp_prefacturas_detalles.gral_imptos_ret_id,
        erp_prefacturas_detalles.tasa_ret  
        FROM erp_prefacturas_detalles 
        JOIN inv_prod ON inv_prod.id=erp_prefacturas_detalles.producto_id
        LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id
        LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=erp_prefacturas_detalles.presentacion_id 
        WHERE erp_prefacturas_detalles.cant_facturar>0 
        AND erp_prefacturas_detalles.prefacturas_id='||prefact_id||';';

    FOR prefactura_detalle IN EXECUTE(sql_select) LOOP
        -- Inicializar valores
        cantPresReservAnterior:=0;
        cantPresAsignado:=0;
        partida_facturada:=false;

        -- tipo_documento 3=Factura de remision
        IF prefactura_fila.tipo_documento::integer = 3 THEN 
            -- toma el costo promedio que viene de la prefactura
            costo_promedio_actual := prefactura_detalle.costo_promedio;
            costo_referencia_actual := prefactura_detalle.costo_referencia;
        ELSE
            -- Obtener costo promedio actual del producto. El costo promedio es en MN.
            SELECT * FROM inv_obtiene_costo_promedio_actual(prefactura_detalle.producto_id, espacio_tiempo_ejecucion) INTO costo_promedio_actual;

            -- Obtener el costo ultimo actual del producto. Este costo es convertido a pesos
            sql_select2 := 'SELECT (CASE WHEN gral_mon_id_'||mes_actual||'=1 THEN costo_ultimo_'||mes_actual||'  ELSE costo_ultimo_'||mes_actual||' * (CASE WHEN gral_mon_id_'||mes_actual||'=1 THEN 1 ELSE tipo_cambio_'||mes_actual||' END) END) AS costo_ultimo FROM inv_prod_cost_prom WHERE inv_prod_id='||prefactura_detalle.producto_id||' AND ano='||ano_actual||';';
            EXECUTE sql_select2 INTO costo_referencia_actual;
        END IF;
				
        -- Verificar que no tenga valor null
        IF costo_promedio_actual IS NULL OR costo_promedio_actual<=0 THEN costo_promedio_actual:=0; END IF;
        IF costo_referencia_actual IS NULL OR costo_referencia_actual<=0 THEN costo_referencia_actual:=0; END IF;

        cantUnidadProd:=0;
        idUnidadMedida:=prefactura_detalle.id_uni_prod;
        densidadProd:=prefactura_detalle.densidad_prod;
        nombreUnidadMedida:=prefactura_detalle.nombre_unidad;

        IF densidadProd IS NULL OR densidadProd=0 THEN densidadProd:=1; END IF;

        cantUnidadProd := prefactura_detalle.cantidad::double precision;

        IF facpar.cambiar_unidad_medida THEN
            IF idUnidadMedida::integer<>prefactura_detalle.inv_prod_unidad_id THEN
                EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''KILO*'';' INTO match_cadena;

                IF match_cadena=true THEN
                    -- Convertir a kilos
                    cantUnidadProd := cantUnidadProd::double precision * densidadProd;
                ELSE
                    EXECUTE 'select '''||nombreUnidadMedida||''' ~* ''LITRO*'';' INTO match_cadena;
                    IF match_cadena=true THEN 
                        -- Convertir a Litros
                        cantUnidadProd := cantUnidadProd::double precision / densidadProd;
                    END IF;
                END IF;

            END IF;
        END IF;

        -- Redondear cantidades
        prefactura_detalle.cant_pedido := round(prefactura_detalle.cant_pedido::numeric,prefactura_detalle.no_dec)::double precision;
        prefactura_detalle.cant_facturado := round(prefactura_detalle.cant_facturado::numeric,prefactura_detalle.no_dec)::double precision;
        prefactura_detalle.cantidad := round(prefactura_detalle.cantidad::numeric,prefactura_detalle.no_dec)::double precision;
        prefactura_detalle.reservado := round(prefactura_detalle.reservado::numeric,prefactura_detalle.no_dec)::double precision;
        prefactura_detalle.nuevo_reservado := round(prefactura_detalle.nuevo_reservado::numeric,prefactura_detalle.no_dec)::double precision;

        IF (cantUnidadProd::double precision <= prefactura_detalle.reservado::double precision) THEN
            -- Asignar la cantidad para descontar de reservado
            prefactura_detalle.reservado := cantUnidadProd::double precision;
        END IF;

        -- Calcular la nueva cantidad reservada
        prefactura_detalle.nuevo_reservado := prefactura_detalle.nuevo_reservado::double precision - prefactura_detalle.reservado::double precision;

        -- Redondaer la nueva cantidad reservada
        prefactura_detalle.nuevo_reservado := round(prefactura_detalle.nuevo_reservado::numeric,prefactura_detalle.no_dec)::double precision;

        -- crea registro en fac_docs_detalles
        INSERT INTO fac_docs_detalles(fac_doc_id,inv_prod_id,inv_prod_presentacion_id,gral_imptos_id,valor_imp,cantidad,precio_unitario,costo_promedio, costo_referencia, inv_prod_unidad_id, gral_ieps_id, valor_ieps, descto, gral_imptos_ret_id, tasa_ret) 
        VALUES (ultimo_id,prefactura_detalle.producto_id,prefactura_detalle.presentacion_id,prefactura_detalle.tipo_impuesto_id,prefactura_detalle.valor_imp,prefactura_detalle.cantidad,prefactura_detalle.precio_unitario, costo_promedio_actual, costo_referencia_actual, prefactura_detalle.inv_prod_unidad_id, prefactura_detalle.gral_ieps_id, prefactura_detalle.valor_ieps, prefactura_detalle.descto, prefactura_detalle.gral_imptos_ret_id, prefactura_detalle.tasa_ret) RETURNING id INTO ultimo_id_det;

        IF refact IS NOT true  AND prefactura_fila.tipo_documento::integer=1 THEN
            -- Si el tipo de producto es diferente de 4 el hay que descontar existencias y generar Movimientos
            -- tipo=4 Servicios
            -- para el tipo servicios NO debe generar movimientos NI descontar existencias
            IF prefactura_detalle.tipo_producto::integer<>4 THEN

                bandera_tipo_4=FALSE; -- indica que por lo menos un producto es diferente de tipo4, por lo tanto debe generarse movimientos

                -- tipo=1 Normal o Terminado
                -- tipo=2 Subensable o Formulacion o Intermedio
                -- tipo=5 Refacciones
                -- tipo=6 Accesorios
                -- tipo=7 Materia Prima
                -- tipo=8 Prod. en Desarrollo

                -- tipo=3 Kit
                -- tipo=4 Servicios
                -- IF prefactura_detalle.tipo_producto=1 OR prefactura_detalle.tipo_producto=2 OR prefactura_detalle.tipo_producto=5 OR prefactura_detalle.tipo_producto=6 OR prefactura_detalle.tipo_producto=7 OR prefactura_detalle.tipo_producto=8 THEN
                IF prefactura_detalle.tipo_producto::integer<>3 AND  prefactura_detalle.tipo_producto::integer<>4 THEN
                    -- Genera registro en detalles del movimiento
                    INSERT INTO inv_mov_detalle(producto_id, alm_origen_id, alm_destino_id, cantidad, inv_mov_id, costo, inv_prod_presentacion_id)
                    VALUES (prefactura_detalle.producto_id, id_almacen,0, cantUnidadProd, identificador_nuevo_movimiento, costo_promedio_actual, prefactura_detalle.presentacion_id);

                    -- Query para descontar producto de existencias y descontar existencia reservada porque ya se Facturó
                    sql_update := 'UPDATE inv_exi SET salidas_'||mes_actual||'=(salidas_'||mes_actual||'::double precision + '||cantUnidadProd||'::double precision), 
                        reservado=(reservado::double precision - '||prefactura_detalle.reservado||'::double precision), momento_salida_'||mes_actual||'='''||espacio_tiempo_ejecucion||'''
                        WHERE inv_alm_id='||id_almacen||'::integer AND inv_prod_id='||prefactura_detalle.producto_id||'::integer AND ano='||ano_actual||'::integer;';
                        EXECUTE sql_update;

                    IF FOUND THEN
	                -- RAISE EXCEPTION '%','FOUND'||FOUND;
                    ELSE
                        RAISE EXCEPTION '%','NOT FOUND:'||FOUND||'  No se pudo actualizar inv_exi';
                    END IF;

                    -- Crear registro en orden salida detalle
                    -- La cantidad se almacena en la unidad de venta
                    INSERT INTO inv_osal_detalle(inv_osal_id,inv_prod_id,inv_prod_presentacion_id,cantidad,precio_unitario, inv_prod_unidad_id, gral_ieps_id, valor_ieps)
                    VALUES (id_osal,prefactura_detalle.producto_id,prefactura_detalle.presentacion_id,prefactura_detalle.cantidad,prefactura_detalle.precio_unitario, prefactura_detalle.inv_prod_unidad_id, prefactura_detalle.gral_ieps_id, prefactura_detalle.valor_ieps);

                    -- Verificar si se está llevando el control de existencias por Presentaciones
                    IF controlExisPres=true THEN 
                        -- Si la configuracion indica que se validan Presentaciones desde el Pedido,entonces significa que hay reservados, por lo tanto hay que descontarlos
                        IF facpar.validar_pres_pedido=true THEN 
                            -- Convertir la cantidad reservada a su equivalente en presentaciones
                            cantPresReservAnterior := prefactura_detalle.reservado::double precision / prefactura_detalle.cant_equiv::double precision;

                            -- redondear la Cantidad de la Presentacion reservada Anteriormente
                            cantPresReservAnterior := round(cantPresReservAnterior::numeric,prefactura_detalle.no_dec)::double precision; 
                        END IF;

                        -- Convertir la cantidad de la partida a su equivalente a presentaciones
                        cantPresAsignado := cantUnidadProd::double precision / prefactura_detalle.cant_equiv::double precision;

                        -- Redondear la cantidad de Presentaciones asignado en la partida
                        cantPresAsignado := round(cantPresAsignado::numeric,prefactura_detalle.no_dec)::double precision;

                        -- Sumar salidas de inv_exi_pres
                        UPDATE inv_exi_pres SET 
                            salidas=(salidas::double precision + cantPresAsignado::double precision), reservado=(reservado::double precision - cantPresReservAnterior::double precision), 
                            momento_actualizacion=translate(creation_date,'T',' ')::timestamp with time zone, gral_usr_id_actualizacion=usr_id 
                        WHERE inv_alm_id=id_almacen AND inv_prod_id=prefactura_detalle.producto_id AND inv_prod_presentacion_id=prefactura_detalle.presentacion_id;
                        -- Termina sumar salidas
                    END IF;


                    -- :::::: Aqui inica calculos para el control de facturacion por partida  ::::::

                    -- Calcular la cantidad facturada
                    prefactura_detalle.cant_facturado:=prefactura_detalle.cant_facturado::double precision + prefactura_detalle.cantidad::double precision;

                    -- Redondear la cantidad facturada
                    prefactura_detalle.cant_facturado := round(prefactura_detalle.cant_facturado::numeric,prefactura_detalle.no_dec)::double precision;

                    IF prefactura_detalle.cant_pedido <= prefactura_detalle.cant_facturado THEN 
                        partida_facturada:=true;
                    ELSE
                        -- Si entro aqui quiere decir que por lo menos una partida esta quedando pendiente de facturar por completo.
                        actualizar_proceso:=false;
                    END IF;

                    -- Actualizar el registro de la partida
                    UPDATE erp_prefacturas_detalles SET cant_facturado=prefactura_detalle.cant_facturado, facturado=partida_facturada, cant_facturar=0, reservado=prefactura_detalle.nuevo_reservado 
                    WHERE id=prefactura_detalle.id_det;

                    -- Obtener el id del pedido que se esta facturando
                    SELECT id FROM poc_pedidos WHERE _folio=prefactura_fila.folio_pedido ORDER BY id DESC LIMIT 1 INTO id_pedido;

                    IF id_pedido IS NULL THEN id_pedido:=0; END IF;

                    IF id_pedido<>0 THEN 
                        -- Actualizar el registro detalle del Pedido
                        UPDATE poc_pedidos_detalle SET reservado=prefactura_detalle.nuevo_reservado 
                        WHERE poc_pedido_id=id_pedido AND inv_prod_id=prefactura_detalle.producto_id AND presentacion_id=prefactura_detalle.presentacion_id;
                    END IF;

                END IF; -- termina tipo producto 1, 2, 7

            ELSE
                IF prefactura_detalle.tipo_producto::integer=4 THEN
                    -- :::::::::: Aqui inica calculos para el control de facturacion por partida ::::::::

                    -- Calcular la cantidad facturada
                    prefactura_detalle.cant_facturado:=prefactura_detalle.cant_facturado::double precision + prefactura_detalle.cantidad::double precision;

                    -- Redondear la cantidad facturada
                    prefactura_detalle.cant_facturado := round(prefactura_detalle.cant_facturado::numeric,prefactura_detalle.no_dec)::double precision;

                    IF prefactura_detalle.cant_pedido <= prefactura_detalle.cant_facturado THEN 
                        partida_facturada:=true;
                    END IF;

                    -- Actualizar el registro de la partida
                    UPDATE erp_prefacturas_detalles SET 
                        cant_facturado=prefactura_detalle.cant_facturado, 
                        facturado=partida_facturada, 
                        cant_facturar=0 
                    WHERE id=prefactura_detalle.id_det;

                END IF;
            END IF;
            -- Termina verificacion diferente de tipo 4

        ELSE
            -- tipo_documento 3=Factura de remision
            IF prefactura_fila.tipo_documento::integer = 3 THEN 
                -- :::::::: Aqui inica calculos para el control de facturacion por partida ::::::::
                -- Calcular la cantidad facturada
                prefactura_detalle.cant_facturado:=prefactura_detalle.cant_facturado::double precision + prefactura_detalle.cantidad::double precision;

                -- Redondear la cantidad facturada
                prefactura_detalle.cant_facturado := round(prefactura_detalle.cant_facturado::numeric,prefactura_detalle.no_dec)::double precision;

                IF prefactura_detalle.cant_pedido <= prefactura_detalle.cant_facturado THEN 
                    partida_facturada:=true;
                ELSE
                    -- Si entro aqui quiere decir que por lo menos una partida esta quedando pendiente de facturar por completo.
                    actualizar_proceso:=false;
                END IF;

                -- Actualizar el registro de la partida
                UPDATE erp_prefacturas_detalles SET cant_facturado=prefactura_detalle.cant_facturado, facturado=partida_facturada, cant_facturar=0, reservado=0 
                WHERE id=prefactura_detalle.id_det;

                -- Crear registros para relacionar las partidas de la Remision con las partidas de las facturas.
                INSERT INTO fac_rem_doc_det(fac_doc_id, fac_doc_det_id,fac_rem_det_id)
                VALUES(ultimo_id, ultimo_id_det, prefactura_detalle.fac_rem_det_id);

            END IF;
        END IF; 
        -- termina if que verifica si es refacturacion

    END LOOP;
			
    -- si bandera tipo 4=true, significa el producto que se esta facturando son servicios;
    -- por lo tanto hay que eliminar el movimiento de inventario
    IF bandera_tipo_4=TRUE THEN
        -- refact=false:No es refacturacion
        -- tipo_documento=1:Factura
        IF refact IS NOT true AND prefactura_fila.tipo_documento=1 THEN
            DELETE FROM inv_mov WHERE id=identificador_nuevo_movimiento;
        END IF;
    END IF;

    IF (SELECT count(prefact_det.id) FROM erp_prefacturas_detalles AS prefact_det JOIN inv_prod ON inv_prod.id=prefact_det.producto_id WHERE prefact_det.prefacturas_id=prefact_id AND inv_prod.tipo_de_producto_id<>4 AND prefact_det.facturado=false )>=1 THEN
        actualizar_proceso:=false;
    END IF;
			
    -- Verificar si hay que actualizar el flujo del proceso
    IF actualizar_proceso THEN
        -- Actualiza el flujo del proceso a 3=Facturado
        UPDATE erp_proceso SET proceso_flujo_id=3 WHERE id=prefactura_fila.proceso_id;
    ELSE
        -- Actualiza el flujo del proceso a 7=FACTURA PARCIAL
        UPDATE erp_proceso SET proceso_flujo_id=7 WHERE id=prefactura_fila.proceso_id;
    END IF;


    -- tipo_documento 3=Factura de remision
    IF prefactura_fila.tipo_documento=3 THEN
        -- buscar numero de remision que se incluyeron en esta factura
        sql_select:='SELECT DISTINCT fac_rem_id FROM fac_rems_docs WHERE erp_proceso_id = '||prefactura_fila.proceso_id;

        FOR fila_fac_rem_doc IN EXECUTE(sql_select) LOOP
            IF (SELECT count(fac_rems_docs.id) as exis FROM fac_rems_docs JOIN erp_prefacturas ON erp_prefacturas.proceso_id = fac_rems_docs.erp_proceso_id JOIN erp_prefacturas_detalles ON erp_prefacturas_detalles.prefacturas_id = erp_prefacturas.id WHERE (erp_prefacturas_detalles.cantidad::double precision - erp_prefacturas_detalles.cant_facturado::double precision)>0 AND fac_rems_docs.fac_rem_id = fila_fac_rem_doc.fac_rem_id)<=0 THEN

                -- Asignar facturado a cada remision
                UPDATE fac_rems SET facturado=TRUE WHERE id=fila_fac_rem_doc.fac_rem_id;
            END IF;

        END LOOP;

    END IF;
			
    -- Una vez terminado el Proceso se asignan ceros a estos campos
    UPDATE erp_prefacturas SET fac_subtotal=0, fac_impuesto=0, fac_monto_retencion=0, fac_total=0, fac_monto_ieps=0, fac_monto_descto=0 
    WHERE id=prefact_id;
			
    -- Actualiza el consecutivo del folio de la factura en la tabla fac_cfds_conf_folios. La actualización es por Empresa-sucursal
    UPDATE fac_cfds_conf_folios SET folio_actual=(folio_actual+1) WHERE id=(SELECT fac_cfds_conf_folios.id FROM fac_cfds_conf JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id WHERE fac_cfds_conf_folios.proposito='FAC' AND fac_cfds_conf.empresa_id=emp_id AND fac_cfds_conf.gral_suc_id=suc_id);
			
    valor_retorno := '1:'||ultimo_id;--retorna el id de fac_docs
	
    RETURN valor_retorno; 

END;$$;


--
-- Name: fac_val_cancel(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.fac_val_cancel(_fac_id integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Factura Cancel Validation >>
    -- >> Version: CDGB             >>
    -- >> Date: 9/Dic/2018          >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    rv record;

    -- dump of errors
    rmsg character varying;

    serie_folio_fac character varying;
    tiene_pagos integer;

BEGIN

    SELECT serie_folio FROM fac_docs
    WHERE id = _fac_id
    INTO serie_folio_fac;

    SELECT count(serie_folio)
    FROM erp_pagos_detalles
    WHERE cancelacion = FALSE
    AND serie_folio = serie_folio_fac
    INTO tiene_pagos;

    IF tiene_pagos = 0 THEN
        SELECT count(serie_folio_factura) FROM fac_nota_credito
        WHERE serie_folio != ''
        AND cancelado = FALSE
        AND serie_folio_factura = serie_folio_fac
        INTO tiene_pagos;

        IF tiene_pagos > 0 THEN
            rmsg := 'La factura '||serie_folio_fac||', tiene notas de credito aplicadas';
        END IF;
    ELSE
        rmsg := 'La factura '||serie_folio_fac||', tiene pagos aplicados';
    END IF;

    IF rmsg != '' THEN
        rv := ( -1::integer, rmsg::text );
    ELSE
        rv := ( 0::integer, ''::text );
    END IF;

    RETURN rv;

END;
$$;


--
-- Name: fac_exec_cancel(integer, integer, text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.fac_exec_cancel(_usr_id integer, _fact_id integer, _reason text, _mode integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Factura Cancel Execution  >>
    -- >> Version: CDGB             >>
    -- >> Date: 9/Dic/2018          >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    rv record;

    -- dump of errors
    rmsg character varying := '';

    espacio_tiempo_ejecucion timestamp with time zone = now();

    ano_actual integer := 0;
    mes_actual integer := 0;

    emp_id integer := 0;
    suc_id integer := 0;
    almacen_id integer := 0;
    last_prefact_id integer := 0;

    tipo_movimiento_id integer := 0;
    identificador_nuevo_movimiento integer := 0;
    identificador_nueva_odev integer;

    prefijo_consecutivo character varying;
    nuevo_consecutivo bigint;
    nuevo_folio character varying;

    --Densidad del producto
    densidadProd double precision := 0;

    cantPresAsignado double precision := 0;
    exis integer := 0;

    -- Bandera que identifica si el producto es tipo 4
    -- Si es tipo 4 no debe existir movimientos en inventario
    conjunto_servicios boolean := TRUE;

    -- Variable que indica  si se debe controlar Existencias por Presentacion
    controlExisPres boolean;

    match_cadena boolean := FALSE;

    -- Pivot variable to be used with queries
    q_pivot text;

    -- Parametros de Facturacion
    facpar record;

    factura_detalle record;
    factura_fila record;
    lote_detalle record;
    osal_fila record;

BEGIN

    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;
    SELECT EXTRACT(MONTH FROM espacio_tiempo_ejecucion) INTO mes_actual;

    -- Obtener id de empresa, sucursal
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
    FROM gral_usr_suc
    JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE gral_usr_suc.gral_usr_id = _usr_id
    INTO emp_id, suc_id;

    -- Obtener parametros para la facturacion
    SELECT * FROM fac_par WHERE gral_suc_id = suc_id INTO facpar;

    -- Consulta para verificar si la Empresa utiliza control de Existencias por Presentacion
    SELECT control_exis_pres FROM gral_emp WHERE id = emp_id INTO controlExisPres;

    -- Obtiene todos los datos de la factura
    SELECT fac_docs.*
    FROM fac_docs
    WHERE fac_docs.id = _fact_id LIMIT 1
    INTO factura_fila;

    -- Cancela registro en fac_docs
    UPDATE fac_docs SET cancelado = TRUE,
    fac_docs_tipo_cancelacion_id = _mode,
    motivo_cancelacion= _reason,
    ctb_tmov_id_cancelacion = 0, -- Always Zero hardcode (It was just a poor try for a contable approach)
    momento_cancelacion = espacio_tiempo_ejecucion,
    gral_usr_id_cancelacion = _usr_id
    WHERE id = factura_fila.id RETURNING inv_alm_id INTO almacen_id;

    UPDATE fac_cfdis SET cancelado = TRUE,
    fecha_cancela = espacio_tiempo_ejecucion,
    gral_usr_id_cancela = _usr_id
	WHERE ref_id = factura_fila.ref_id AND gral_emp_id = emp_id;

    -- Cambia estado del comprobante a 0
    UPDATE fac_cfds SET estado_del_comprobante = '0',
    fac_docs_tipo_cancelacion_id = _mode,
    momento_cancelacion = espacio_tiempo_ejecucion,
    motivo_cancelacion = _reason,
    id_user_cancela = _usr_id
    WHERE fac_cfds.proceso_id = factura_fila.proceso_id AND
    fac_cfds.serie_folio = factura_fila.serie_folio;

    -- Cancela registro en h_facturas
    UPDATE erp_h_facturas SET cancelacion = TRUE,
    fac_docs_tipo_cancelacion_id = _mode,
    momento_cancelacion = espacio_tiempo_ejecucion,
    id_usuario_cancelacion = _usr_id
    WHERE serie_folio ILIKE factura_fila.serie_folio;

    IF _mode = 1 THEN

        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        -- INICIA DEVOLUCIONES A EL INVENTARIO
        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

        -- Obtiene lista de productos de la factura
        q_pivot =' SELECT fac_docs_detalles.inv_prod_id,
            (fac_docs_detalles.cantidad::double precision - cantidad_devolucion::double precision) AS cantidad,
            inv_prod.tipo_de_producto_id as tipo_producto,
            fac_docs_detalles.inv_prod_presentacion_id AS presentacion_id,
            (CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.cantidad END) AS cant_equiv,
            (CASE WHEN inv_prod_unidades.id IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec,
            inv_prod.unidad_id AS id_uni_prod,
            inv_prod.densidad AS densidad_prod,
            inv_prod_unidades.titulo AS nombre_unidad,
            fac_docs_detalles.inv_prod_unidad_id
            FROM fac_docs_detalles
            JOIN inv_prod ON inv_prod.id=fac_docs_detalles.inv_prod_id
            LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id
            LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=fac_docs_detalles.inv_prod_presentacion_id
            WHERE fac_docs_detalles.fac_doc_id = ' || _fact_id;


        -- Genera registro para el movimiento de cancelacion
        INSERT INTO inv_mov(
            observacion,
            momento_creacion,
            gral_usr_id,
            gral_app_id,
            inv_mov_tipo_id,
            referencia,
            fecha_mov
        )
        VALUES(
            _reason,
            espacio_tiempo_ejecucion,
            _usr_id,
            36,                        -- Hardcode (app id for cancel)
            2,                         -- Devolucion por cancelacion
            factura_fila.serie_folio,
            espacio_tiempo_ejecucion
        ) RETURNING id INTO identificador_nuevo_movimiento;

        FOR factura_detalle IN EXECUTE( q_pivot ) LOOP

            IF facpar.cambiar_unidad_medida THEN

                IF factura_detalle.id_uni_prod::integer <> factura_detalle.inv_prod_unidad_id THEN

                    densidadProd := factura_detalle.densidad_prod;

                    IF densidadProd IS NULL OR densidadProd = 0 THEN
                        densidadProd := 1;
                    END IF;

                    EXECUTE 'select ''' || factura_detalle.nombre_unidad || ''' ~* ''KILO*'';' INTO match_cadena;

                    IF match_cadena = true THEN

                        -- Convertir a kilos
                        factura_detalle.cantidad := factura_detalle.cantidad::double precision * densidadProd;

                    ELSE

                        EXECUTE 'select ''' || factura_detalle.nombre_unidad || ''' ~* ''LITRO*'';' INTO match_cadena;

                        IF match_cadena = true THEN
                            -- Convertir a Litros
                            factura_detalle.cantidad := factura_detalle.cantidad::double precision / densidadProd;
                        END IF;

                    END IF;
                END IF;

            END IF;

            -- Si el tipo de producto es diferente de 4, hay que devolver existencias y generar Movimientos
            -- tipo = 4 " Servicios: para el tipo servicios debe generar movimientos ni devolver existencias "
            IF factura_detalle.tipo_producto <> 4 THEN

                -- Indica que por lo menos un producto es diferente de tipo 4, por lo tanto deberan generarse movimientos
                conjunto_servicios = FALSE;

                IF  factura_detalle.tipo_producto = 1 OR    -- Normal o Terminado
                    factura_detalle.tipo_producto = 2 OR    -- Subensable o Formulacion o Intermedio
                    factura_detalle.tipo_producto = 5 OR    -- Refacciones
                    factura_detalle.tipo_producto = 6 OR    -- Accesorios
                    factura_detalle.tipo_producto = 7 OR    -- Materia Prima
                    factura_detalle.tipo_producto = 8 THEN  -- Prod. en Desarrollo

                    --Redondear la cantidad
                    factura_detalle.cantidad := round( factura_detalle.cantidad::numeric,factura_detalle.no_dec )::double precision;

                    -- Genera registro en detalles del movimiento
                    INSERT INTO inv_mov_detalle(
                        producto_id,
                        alm_origen_id,
                        alm_destino_id,
                        cantidad,
                        inv_mov_id,
                        inv_prod_presentacion_id
                    )
                    VALUES(
                        factura_detalle.inv_prod_id,
                        0,
                        almacen_id,
                        factura_detalle.cantidad,
                        identificador_nuevo_movimiento,
                        factura_detalle.presentacion_id
                    );

                    -- Consulta para verificar existencia del producto en almacen (sobre el año en curso)
                    q_pivot := ' SELECT count(id) FROM inv_exi
                        WHERE inv_prod_id = ' || factura_detalle.inv_prod_id ||
                        ' AND inv_alm_id = ' || almacen_id ||
                        ' AND ano = ' || ano_actual || ';' ;
                    EXECUTE q_pivot INTO exis;

                    IF exis > 0 THEN
                        q_pivot := 'UPDATE inv_exi SET entradas_'||mes_actual||'=(entradas_'||mes_actual||' + '||factura_detalle.cantidad||'::double precision),momento_entrada_'||mes_actual||'='''||espacio_tiempo_ejecucion||'''
                            WHERE inv_alm_id='|| almacen_id ||' AND inv_prod_id='||factura_detalle.inv_prod_id||' AND ano='||ano_actual||';';
                        EXECUTE q_pivot;
                    ELSE
                        q_pivot := 'INSERT INTO inv_exi (inv_prod_id,inv_alm_id, ano, entradas_'||mes_actual||',momento_entrada_'||mes_actual||',exi_inicial) '||
                            'VALUES('||factura_detalle.inv_prod_id||','|| almacen_id ||','||ano_actual||','||factura_detalle.cantidad||','''|| espacio_tiempo_ejecucion ||''',0)';
                        EXECUTE q_pivot;
                    END IF;

                    -- Verificar si se está llevando el control de existencias por Presentaciones
                    IF controlExisPres = TRUE THEN

                        -- Convertir la cantidad de la partida a su equivalente a presentaciones
                        cantPresAsignado := factura_detalle.cantidad::double precision / factura_detalle.cant_equiv::double precision;

                        -- Redondear la cantidad de Presentaciones asignado en la partida
                        cantPresAsignado := round( cantPresAsignado::numeric,factura_detalle.no_dec )::double precision;

                        -- Consulta para verificar existencia del producto en el almacen y en el año actual
                        q_pivot := 'SELECT count(id) FROM inv_exi_pres WHERE inv_prod_id='||factura_detalle.inv_prod_id||' AND inv_alm_id='|| almacen_id ||' AND inv_prod_presentacion_id = '||factura_detalle.presentacion_id||';';
                        EXECUTE q_pivot INTO exis;

                        -- Sumar entradas de inv_exi_pres
                        IF exis > 0 THEN
                            UPDATE inv_exi_pres SET entradas=(entradas::double precision + cantPresAsignado::double precision),
                            momento_actualizacion=espacio_tiempo_ejecucion, gral_usr_id_actualizacion = _usr_id
                            WHERE inv_alm_id = almacen_id
                            AND inv_prod_id = factura_detalle.inv_prod_id
                            AND inv_prod_presentacion_id = factura_detalle.presentacion_id;
                        ELSE
                            INSERT INTO inv_exi_pres(
                                inv_alm_id,
                                inv_prod_id,
                                inv_prod_presentacion_id,
                                inicial,
                                momento_creacion,
                                gral_usr_id_creacion,
                                entradas
                            )
                            VALUES(
                                almacen_id,
                                factura_detalle.inv_prod_id,
                                factura_detalle.presentacion_id,
                                0,
                                espacio_tiempo_ejecucion,
                                _usr_id,
                                cantPresAsignado::double precision
                            );
                        END IF;

                    END IF;

                END IF;

            END IF;

        END LOOP;

        IF conjunto_servicios = TRUE THEN
            --la factura es de un producto tipo 4, por lo tanto se elimina el movimiento generado anteriormente
            DELETE FROM inv_mov WHERE id = identificador_nuevo_movimiento;
        END IF;

        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        -- TERMINA DEVOLUCIONES A EL INVENTARIO
        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        -- INICIA CANCELACION DE ORDEN DE SALIDA
        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

        -- Obtener datos de la orden de salida que genero esta factura
        SELECT * FROM inv_osal
        WHERE inv_osal.folio_documento = factura_fila.serie_folio
        AND inv_osal.cxc_clie_id = factura_fila.cxc_clie_id
        AND inv_osal.tipo_documento = 1
        INTO osal_fila;

        UPDATE inv_osal SET cancelacion = true,
        momento_cancelacion = espacio_tiempo_ejecucion,
        motivo_cancelacion = _reason,
        gral_usr_id_actualizacion = _usr_id
        WHERE id = osal_fila.id;

        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        -- TERMINA CANCELACION DE ORDEN DE SALIDA
        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        -- INICIA GENERACION DE ORDEN DE DEVOLUCION
        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

        -- Estatus 0 = No se ha tocado por el personal de Almacen,
        -- Estatus 1 = Ya se ha ingresado cantidades, lotes, pedimentos y fechas de caducidad pero aun no se ha descontado del lote
        -- Estatus 2 = Confirmado( ya se le dio salida )

        -- Solo se puede generar orden de devolucion cuando el estatus es mayor a Uno
        IF osal_fila.estatus::integer >= 1 THEN

            -- Aqui entra para tomar el consecutivo del folio  la sucursal actual
            UPDATE gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
            WHERE gral_emp_id = emp_id
            AND gral_suc_id = suc_id
            AND gral_cons_tipo_id = 26  -- Folio Orden de Devolucion
            RETURNING prefijo, consecutivo INTO prefijo_consecutivo, nuevo_consecutivo;

            -- Concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio
            nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

            INSERT INTO inv_odev(
                folio,
                inv_mov_tipo_id,
                tipo_documento,
                folio_documento,
                folio_ncto,
                fecha_exp,
                cxc_clie_id,
                inv_alm_id,
                moneda_id,
                erp_proceso_id,
                momento_creacion,
                gral_usr_id_creacion,
                gral_emp_id,
                gral_suc_id,
                cancelacion
            ) VALUES (
                nuevo_folio,
                tipo_movimiento_id,
                osal_fila.tipo_documento,
                osal_fila.folio_documento,
                '',                        -- Este dato se va vacio porque es cancelacion de factura, esto no genera nota de credito.
                osal_fila.fecha_exp,
                factura_fila.cxc_clie_id,
                osal_fila.inv_alm_id,
                factura_fila.moneda_id,
                factura_fila.proceso_id,
                espacio_tiempo_ejecucion,
                _usr_id,
                emp_id,
                suc_id,
                false
            ) RETURNING id INTO identificador_nueva_odev;


            -- Obtiene los productos de la Orden de Salida
            q_pivot := 'SELECT inv_osal_detalle.id,
                inv_osal_detalle.inv_prod_id,
                inv_osal_detalle.inv_prod_presentacion_id,
                (inv_osal_detalle.cantidad::double precision - cant_dev::double precision) AS cantidad_devolucion
                FROM inv_osal_detalle WHERE inv_osal_detalle.inv_osal_id = ' || osal_fila.id || ';' ;


            FOR factura_detalle IN EXECUTE( q_pivot ) LOOP

                -- Registrar cantidades que se devolvieron a inv_osal_detalle
                UPDATE inv_osal_detalle SET cant_dev=( cant_dev + factura_detalle.cantidad_devolucion::double precision )
                WHERE id = factura_detalle.id;

                -- Obtiene lista de lotes de la Salida
                q_pivot := 'SELECT inv_lote_detalle.inv_lote_id AS id_lote,
                    inv_lote_detalle.inv_osal_detalle_id,
                    inv_lote_detalle.cantidad_sal AS cant_fac,
                    (inv_lote_detalle.cantidad_sal::double precision - cantidad_dev::double precision) AS devolucion,
                    inv_osal_detalle.inv_prod_unidad_id AS id_uni_prod_venta
                    FROM inv_osal_detalle
                    JOIN inv_lote_detalle ON inv_lote_detalle.inv_osal_detalle_id=inv_osal_detalle.id
                    JOIN inv_lote ON inv_lote.id=inv_lote_detalle.inv_lote_id
                    WHERE inv_osal_detalle.id = ' || factura_detalle.id || ' AND inv_lote.inv_prod_id = ' || factura_detalle.inv_prod_id || ';';

                FOR lote_detalle IN EXECUTE( q_pivot ) LOOP

                    lote_detalle.devolucion := factura_detalle.cantidad_devolucion::double precision;

                    -- Crear registro en inv_odev_detalle
                    INSERT INTO inv_odev_detalle(
                        inv_odev_id,
                        inv_osal_detalle_id,
                        inv_lote_id,
                        cant_fac_lote,
                        cant_dev_lote,
                        inv_prod_unidad_id
                    ) VALUES(
                        identificador_nueva_odev,
                        lote_detalle.inv_osal_detalle_id,
                        lote_detalle.id_lote,
                        lote_detalle.cant_fac,
                        lote_detalle.devolucion,
                        lote_detalle.id_uni_prod_venta
                    );

                END LOOP;

            END LOOP;

        END IF;

        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        -- TERMINA GENERACION DE ORDEN DE DEVOLUCION
        -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    ELSIF _mode = 2 THEN

        --Aqui entra cuando se refactura
        --actualiza campo refacturacion y regresa el id del proceso 
        UPDATE erp_prefacturas SET refacturar = TRUE
        WHERE proceso_id = factura_fila.proceso_id
        RETURNING id INTO last_prefact_id;

        --obtiene lista de productos de la factura
        q_pivot := 'SELECT  fac_docs_detalles.inv_prod_id,
            fac_docs_detalles.inv_prod_presentacion_id AS presentacion_id,
            fac_docs_detalles.cantidad,
            0::double precision AS nueva_cant_fac,
            inv_prod.tipo_de_producto_id AS tipo_producto,
            (CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.cantidad END) AS cant_equiv,
            (CASE WHEN inv_prod_unidades.id IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec 
            FROM fac_docs_detalles
            JOIN inv_prod ON inv_prod.id=fac_docs_detalles.inv_prod_id
            LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id
            LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=fac_docs_detalles.inv_prod_presentacion_id 
            WHERE fac_docs_detalles.fac_doc_id=' || _fact_id;

        FOR factura_detalle IN EXECUTE(q_pivot) LOOP

            -- Si el tipo de producto es diferente de 4, hay que devolver la cantidad a la prefactura
            -- tipo = 4 " Servicios
            IF factura_detalle.tipo_producto <> 4 THEN
                IF  factura_detalle.tipo_producto = 1 OR    -- Normal o Terminado
                    factura_detalle.tipo_producto = 2 OR    -- Subensable o Formulacion o Intermedio
                    factura_detalle.tipo_producto = 5 OR    -- Refacciones
                    factura_detalle.tipo_producto = 6 OR    -- Accesorios
                    factura_detalle.tipo_producto = 7 OR    -- Materia Prima
                    factura_detalle.tipo_producto = 8 THEN  -- Prod. en Desarrollo

                    --Actualizar partida de Prefacturas detalles
                    UPDATE erp_prefacturas_detalles
                    SET cant_facturado = factura_detalle.nueva_cant_fac, facturado = FALSE
                    WHERE prefacturas_id = last_prefact_id
                    AND producto_id = factura_detalle.inv_prod_id
                    AND presentacion_id = factura_detalle.presentacion_id;

                END IF;
            END IF;

        END LOOP;

        UPDATE erp_proceso SET proceso_flujo_id = 2 WHERE id=factura_fila.proceso_id;

    ELSE
        RAISE EXCEPTION '%', 'Tipo de cancelacion no soportada';
    END IF;

    IF rmsg != '' THEN
        rv := ( -1::integer, rmsg::text );
    ELSE
        rv := ( 0::integer, ''::text );
    END IF;

    RETURN rv;

END;
$$;



--
-- Name: ncr_exec_cancel(integer, integer, text, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.ncr_exec_cancel(_usr_id integer, _ncr_id integer, _reason text, _mode integer) RETURNS record
    LANGUAGE plpgsql
    AS $$

DECLARE

    rv record;

    -- dump of errors
    rmsg character varying;

    ncr_row record;

    total_factura double precision;
    suma_pagos double precision;
    suma_notas_credito double precision;
    id_moneda_factura integer;

    emp_id integer;
    suc_id integer;
    nuevacantidad_monto_pago double precision := 0;
    nuevo_saldo_factura double precision := 0;
    espacio_tiempo_ejecucion timestamp with time zone = now();

BEGIN

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Nota credito Cancel Execution  >>
    -- >> Version: CDGB                  >>
    -- >> Date: 21/Dic/2018              >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    -- obtener id de empresa, sucursal
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
    FROM gral_usr_suc 
    JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE gral_usr_suc.gral_usr_id = _usr_id
    INTO emp_id, suc_id;

    UPDATE fac_nota_credito
    SET cancelado = true,
        motivo_cancelacion = _reason,
        ctb_tmov_id_cancelacion = 0, -- Always Zero hardcode (It was just a poor try for a contable approach)
        momento_cancelacion = espacio_tiempo_ejecucion,
        gral_usr_id_cancelacion = _usr_id 
    WHERE id = _ncr_id;

    SELECT * FROM fac_nota_credito
    WHERE id = _ncr_id
    INTO ncr_row;
    
    UPDATE fac_cfdis
    SET cancelado = TRUE,
        fecha_cancela = espacio_tiempo_ejecucion,
        gral_usr_id_cancela = _usr_id 
    WHERE ref_id = ncr_row.ref_id
    AND gral_emp_id = emp_id;

    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    -- INICIA ACTUALIZACION erp_h_facturas
    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    SELECT monto_total, moneda_id
    FROM erp_h_facturas
    WHERE serie_folio = ncr_row.serie_folio_factura
    INTO total_factura, id_moneda_factura;

    -- sacar suma total de pagos para esta factura
    SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END
    FROM ( SELECT sum(cantidad)
           FROM erp_pagos_detalles
           WHERE serie_folio = ncr_row.serie_folio_factura
           AND cancelacion = FALSE ) AS sbt
    INTO suma_pagos;
			
    -- sacar suma total de notas de credito para esta factura
    -- cuando la moneda de la factura es USD hay que convertir todas las Notas de Credito a Dolar
    IF id_moneda_factura = 2 THEN

        SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END
        FROM (
            SELECT sum(total_nota) FROM (
                SELECT round(( (CASE WHEN moneda_id=1 THEN total/tipo_cambio ELSE total END))::numeric,2)::double precision AS total_nota
                FROM fac_nota_credito
                WHERE serie_folio != ''
                AND serie_folio_factura = ncr_row.serie_folio_factura
                AND cancelado = FALSE
            ) AS sbt 
        ) AS subtabla
        INTO suma_notas_credito;

    ELSE

        -- cuando la Factura es en pesos NO HAY necesidad de convertir,
        -- porque a las facturas en USD no se le aplica notas de credito
        -- de Otra MONEDA, solo pesos
        SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END
        FROM (
            SELECT sum(total)
            FROM fac_nota_credito
            WHERE serie_folio_factura = ncr_row.serie_folio_factura
            AND cancelado = FALSE ) AS subtabla
        INTO suma_notas_credito;

    END IF;
			
    nuevacantidad_monto_pago := round((suma_pagos)::numeric,4)::double precision;

    nuevo_saldo_factura := round((total_factura - suma_pagos - suma_notas_credito)::numeric,2)::double precision;
			
    -- actualiza cantidades cada vez que se realice un pago
    UPDATE erp_h_facturas
    SET total_pagos = nuevacantidad_monto_pago,
        total_notas_creditos = suma_notas_credito,
        saldo_factura = nuevo_saldo_factura,
        pagado = false,
        momento_actualizacion = espacio_tiempo_ejecucion 
    WHERE serie_folio = ncr_row.serie_folio_factura;

    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    -- TERMINA ACTUALIZACION erp_h_facturas
    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    IF rmsg != '' THEN
        rv := ( -1::integer, rmsg::text );
    ELSE
        rv := ( 0::integer, ''::text );
    END IF;

    RETURN rv;


END;
$$;



--
-- Name: ncr_save_xml(integer, character varying, character varying, character varying, boolean, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.ncr_save_xml(_ncr_id integer, _file_xml character varying, _serie character varying, _folio character varying, _saldado boolean, _usr_id integer) RETURNS record
    LANGUAGE plpgsql
    AS $$

DECLARE

    rv record;

    -- dump of errors
    rmsg character varying;

    refid character varying;
    serie_folio_fac character varying;
    serie_folio_nota_credito  character varying;
    concepto_nota_credito character varying;

    total_factura double precision := 0;
    suma_pagos double precision := 0;
    suma_notas_credito double precision := 0;
    nuevo_saldo_factura double precision := 0;
    nuevacantidad_monto_pago double precision := 0;

    id_moneda_factura integer;
    emp_id integer;
    suc_id integer;
    ncr_conf_folios_id integer;

    fecha_nota_credito timestamp with time zone;
    espacio_tiempo_ejecucion timestamp with time zone = now();

BEGIN

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Save xml data in DBMS     >>
    -- >> Version: CDGB             >>
    -- >> Date: 20/Dic/2018         >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    --obtener id de empresa, sucursal
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
    FROM gral_usr_suc 
    JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE gral_usr_suc.gral_usr_id = _usr_id
    INTO emp_id, suc_id;

    serie_folio_fac := _serie || _folio;
    refid = regexp_replace( _file_xml, '.[xX][mM][lL]', '' );

    UPDATE fac_nota_credito
    SET serie_folio = serie_folio_fac,
        momento_expedicion = espacio_tiempo_ejecucion,
        gral_usr_id_expedicion = _usr_id, ref_id = refid 
    WHERE id = _ncr_id
    RETURNING serie_folio_factura, momento_expedicion,
        concepto, serie_folio
    INTO serie_folio_fac, fecha_nota_credito, concepto_nota_credito,
    serie_folio_nota_credito;

    -- Guarda la cadena del xml timbrado
    INSERT INTO fac_cfdis(
       tipo,
       ref_id,
       doc,
       gral_emp_id,
       gral_suc_id,
       fecha_crea,
       gral_usr_id_crea
    ) VALUES (
       2,
       refid,
       'THIS FIELD IS DEPRECATED'::text,
       emp_id,
       suc_id,
       espacio_tiempo_ejecucion,
       _usr_id
    );

    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    -- INICIA ACTUALIZACION erp_h_facturas
    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    SELECT monto_total, moneda_id
    from erp_h_facturas
    where serie_folio = serie_folio_fac
    INTO total_factura, id_moneda_factura;

    -- sacar suma total de pagos para esta factura
    SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END from( SELECT sum(cantidad) FROM erp_pagos_detalles WHERE serie_folio=serie_folio_fac AND cancelacion=FALSE ) AS sbt INTO suma_pagos;
			
    -- sacar suma total de notas de credito para esta factura
    -- cuando la moneda de la factura es USD hay que convertir todas las Notas de Credito a Dolar
    IF id_moneda_factura = 2 THEN
        SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END FROM (
            SELECT sum(total_nota) FROM (
                SELECT round(( (CASE WHEN moneda_id=1 THEN total/tipo_cambio ELSE total END))::numeric,2)::double precision AS total_nota FROM fac_nota_credito WHERE serie_folio!='' AND serie_folio_factura=serie_folio_fac
            ) AS sbt
        ) AS subtabla INTO suma_notas_credito;
    ELSE
        -- cuando la Factura es en pesos NO HAY necesidad de convertir, porque a las facturas en USD no se le aplica notas de credito de Otra MONEDA, solo pesos
        SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END FROM (
            SELECT sum(total) FROM fac_nota_credito WHERE serie_folio_factura=serie_folio_fac AND cancelado = FALSE
        ) AS subtabla INTO suma_notas_credito;
    END IF;
			
    nuevacantidad_monto_pago := round((suma_pagos)::numeric,4)::double precision;
			
    -- SI saldado=true, entonces se asigna un cero al saldo de la factura
    IF _saldado THEN 
        nuevo_saldo_factura := 0;
    ELSE
        nuevo_saldo_factura := round((total_factura-suma_pagos-suma_notas_credito)::numeric,2)::double precision;
    END IF;
			
    -- actualiza cantidades cada vez que se realice un pago
    UPDATE erp_h_facturas
    SET total_pagos = nuevacantidad_monto_pago,
        total_notas_creditos = suma_notas_credito,
        saldo_factura = nuevo_saldo_factura,
        pagado = _saldado,
        momento_actualizacion = espacio_tiempo_ejecucion 
    WHERE serie_folio = serie_folio_fac;

    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    -- TERMINA ACTUALIZACION erp_h_facturas
    -- ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    select fac_cfds_conf_folios.id
    from fac_cfds_conf
    JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id
    where fac_cfds_conf_folios.proposito = 'NCR'
    AND fac_cfds_conf.empresa_id = emp_id
    AND fac_cfds_conf.gral_suc_id = suc_id
    INTO ncr_conf_folios_id;
    
    UPDATE fac_cfds_conf_folios SET folio_actual = (folio_actual + 1) where id = ncr_conf_folios_id;

    IF rmsg != '' THEN
        rv := ( -1::integer, rmsg::text );
    ELSE
        rv := ( 0::integer, ''::text );
    END IF;

    RETURN rv;

END;$$;



CREATE TYPE grid_renglon_cot AS (
    removido integer,
    id_detalle integer,
    id_producto integer,
    id_presentacion integer,
    cantidad double precision,
    precio double precision,
    moneda_grid integer,
    notr character varying,
    id_imp_prod integer,
    valor_imp double precision,
    unidad_id integer,
    status_autorizacion boolean,
    precio_autorizado double precision,
    id_user_aut integer,
    requiere_autorizacion boolean,
    salvar_registro character varying
);

CREATE OR REPLACE FUNCTION public.cot_edit(
    _usuario_id integer,
    _identificador integer,
    _select_tipo_cotizacion integer,
    _id_cliente_o_prospecto integer,
    _check_descripcion_larga boolean,
    _observaciones text,
    _tipo_cambio double precision,
    _moneda_id integer,
    _fecha date,
    _agente_id integer,
    _vigencia smallint,
    _incluye_iva boolean,
    _tc_usd double precision,
    _extra_data grid_renglon_cot[])
  RETURNS character varying AS
$BODY$
DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name:     Creación/Edición de Cotización                                  >>
    -- >> Version:  RRM                                                             >>
    -- >> Date:     10/Dic/2020                                                     >>
    -- >>                                                                           >>
    -- >> Si _identificador = 0 entonces se trata de una Creación de Cotización.    >>
    -- >> Si _identificador > 0 entonces se trata de una Edición de Cotización.     >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    -- Estas  variables se utilizan en la mayoria de los catalogos
    requiere_autorizacion boolean;
    valor_retorno character varying = '0';
    emp_id integer := 0;
    suc_id integer := 0;

    -- Sucursal de donde se tomara el consecutivo
    suc_id_consecutivo integer = 0;

    id_tipo_consecutivo integer = 0;
    ultimo_id integer := 0;

    -- Total de elementos de arreglo
    total_filas integer;

    -- Contador de filas o posiciones del arreglo
    cont_fila integer;

    -- Parametros de Facturacion
    facpar record;

    -- Variable para cotizaciones
    ultimo_id_proceso integer = 0;
    prefijo_consecutivo character varying = '';
    nuevo_consecutivo bigint = 0;
    nuevo_folio character varying = '';

    importe_partida double precision = 0;
    impuesto_partida double precision = 0;
    monto_subtotal double precision = 0;
    monto_total double precision = 0;
    monto_impuesto double precision = 0;

    -- Lo simultaneo pasara completamente en el mismo espacio tiempo
    espacio_tiempo_ejecucion timestamp with time zone = now();

BEGIN

    -- Obtiene empresa_id y sucursal_id
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
        FROM gral_usr_suc
        JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
        WHERE gral_usr_suc.gral_usr_id = _usuario_id
        INTO emp_id, suc_id;

    -- Obtener parametros para la facturacion
    SELECT *
        FROM fac_par
        WHERE gral_suc_id = suc_id
        INTO facpar;

    -- Este consecutivo es para el folio del Pedido
    -- y folio para BackOrder( poc_ped_bo )
    suc_id_consecutivo := facpar.gral_suc_id_consecutivo;


    -- Crea cotizacion
    IF _identificador = 0 THEN

        -- Crea registro en tabla erp_proceso
        -- y retorna el id del registro creado.
        -- El flujo del proceso es 1 = Cotizacion
        INSERT INTO erp_proceso (
            proceso_flujo_id,
            empresa_id,
            sucursal_id
        ) VALUES (
            4,
            emp_id,
            suc_id
        ) RETURNING id into ultimo_id_proceso;

        -- Consecutivo de cotizaciones a clientes
        id_tipo_consecutivo := 5;

        -- Aqui entra para tomar el consecutivo del pedido de la sucursal actual
        UPDATE gral_cons SET
            consecutivo = (
                SELECT sbt.consecutivo + 1
                FROM gral_cons AS sbt
                WHERE sbt.id=gral_cons.id
            )
        WHERE gral_emp_id = emp_id
              AND gral_suc_id = suc_id
              AND gral_cons_tipo_id = id_tipo_consecutivo
        RETURNING prefijo, consecutivo INTO prefijo_consecutivo, nuevo_consecutivo;

        -- Concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio
        nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

        -- Crear registro en la tabla poc_cot y retorna el id del registro creado
        INSERT INTO poc_cot (
            folio,
            tipo,
            observaciones,
            incluye_img_desc,
            tipo_cambio,
            gral_mon_id,
            fecha,
            cxc_agen_id,
            dias_vigencia,
            incluye_iva,
            tc_usd,
            proceso_id,
            gral_usr_id_creacion,
            momento_creacion,
            borrado_logico
        ) VALUES (
            nuevo_folio,
            _select_tipo_cotizacion,
            _observaciones,
            _check_descripcion_larga,
            _tipo_cambio,
            _moneda_id,
            _fecha,
            _agente_id,
            _vigencia,
            _incluye_iva,
            _tc_usd,
            ultimo_id_proceso,
            _usuario_id,
            espacio_tiempo_ejecucion,
            false
        ) RETURNING id INTO ultimo_id;

        -- Tipo
        -- 1 = Cliente,
        -- 2 = Prospecto
        IF _select_tipo_cotizacion = 1 THEN

            -- Crear registro para relacionar la Cotizacion con el Cliente
            INSERT INTO poc_cot_clie (
                poc_cot_id,
                cxc_clie_id
            ) VALUES (
                ultimo_id,
                _id_cliente_o_prospecto
            );

        ELSE

           -- Crear registro para relacionar la Cotizacion con el Prospecto
           INSERT INTO poc_cot_prospecto (
                 poc_cot_id,
                 crm_prospecto_id
           ) VALUES (
                 ultimo_id,
                 _id_cliente_o_prospecto
           );

        END IF;

        -- Obtiene total de elementos del arreglo
        total_filas := array_length(_extra_data,1);
        cont_fila   := 1;

        FOR cont_fila IN 1 .. total_filas LOOP

            -- str_filas[1] removido
            -- 1: no esta eliminado
            -- 0: eliminado
            IF _extra_data[cont_fila].removido != 0 THEN
                    --str_filas[2]    id_detalle
                    --str_filas[3]    id_producto
                    --str_filas[4]    id_presentacion
                    --str_filas[5]    cantidad
                    --str_filas[6]    precio
                    --str_filas[7]    moneda_grid
                    --str_filas[8]    notr
                    --str_filas[9]    id_imp_prod
                    --str_filas[10]    valor_imp
                    --str_filas[11]    unidad_id
                    --str_filas[12]    status_autorizacion
                    --str_filas[13]    precio_autorizado
                    --str_filas[14]    id_user_aut
                    --str_filas[15]    requiere_autorizacion
                    --str_filas[16]    salvar_registro

                IF _extra_data[cont_fila].status_autorizacion then

                    -- Si esta autorizado por default le asignamos true al campo requiere_autorizacion
                    requiere_autorizacion := true;

                ELSE

                    requiere_autorizacion := _extra_data[cont_fila].requiere_autorizacion;

                END IF;

                -- Crea registros para tabla poc_pedidos_detalle
                INSERT INTO poc_cot_detalle(
                    poc_cot_id,
                    inv_prod_id,
                    inv_presentacion_id,
                    cantidad,
                    precio_unitario,
                    gral_mon_id,
                    gral_impto_id,
                    valor_imp,
                    inv_prod_unidad_id,
                    requiere_aut,
                    autorizado,
                    precio_aut,
                    gral_usr_id_aut
                ) VALUES (
                    ultimo_id,
                    _extra_data[cont_fila].id_producto,
                    _extra_data[cont_fila].id_presentacion,
                    _extra_data[cont_fila].cantidad,
                    _extra_data[cont_fila].precio,
                    _extra_data[cont_fila].moneda_grid,
                    _extra_data[cont_fila].id_imp_prod,
                    _extra_data[cont_fila].valor_imp,
                    _extra_data[cont_fila].unidad_id,
                    requiere_autorizacion,
                    _extra_data[cont_fila].status_autorizacion,
                    _extra_data[cont_fila].precio_autorizado,
                    _extra_data[cont_fila].id_user_aut
                );

                importe_partida := _extra_data[cont_fila].cantidad * _extra_data[cont_fila].precio;

                IF _moneda_id <> _extra_data[cont_fila].moneda_grid THEN

                    IF _moneda_id=1 AND _extra_data[cont_fila].moneda_grid <> 1 THEN

                        importe_partida := importe_partida::double precision * _tipo_cambio;

                    ELSE

                        IF _moneda_id <> 1 AND _extra_data[cont_fila].moneda_grid = 1 THEN

                            importe_partida :=  importe_partida::double precision / _tipo_cambio;

                        END IF;

                    END IF;

                END IF;

                -- Redondear el importe de la partida a 4 digitos
                importe_partida := round(importe_partida::double precision::numeric,4)::double precision;
                impuesto_partida := round((importe_partida::double precision * _extra_data[cont_fila].valor_imp)::numeric,4)::double precision;
                monto_subtotal := round((monto_subtotal + importe_partida)::numeric,4)::double precision;
                monto_impuesto := round((monto_impuesto + impuesto_partida)::numeric,4)::double precision;

            END IF;

        END LOOP;

        -- Calcula el monto del pedido
        -- Monto_total:= monto_subtotal + monto_impuesto - total_retencion;
        monto_total := monto_subtotal + monto_impuesto;

        -- Actualiza campos subtotal, impuesto, retencion, total de tabla poc_cot
        UPDATE poc_cot SET
            subtotal = monto_subtotal,
            impuesto = monto_impuesto,
            total = monto_total
        WHERE id = ultimo_id;

        valor_retorno := '1';

    END IF;


    -- Actualiza cotizacion
    IF _identificador > 0 THEN

        UPDATE poc_cot SET
            observaciones = _observaciones,
            incluye_img_desc = _check_descripcion_larga,
            tipo_cambio = _tipo_cambio,
            gral_mon_id = _moneda_id,
            fecha = _fecha,
            cxc_agen_id = _agente_id,
            dias_vigencia = _vigencia,
            incluye_iva = _incluye_iva,
            tc_usd = _tc_usd,
            gral_usr_id_actualizacion = _usuario_id,
            momento_actualizacion = espacio_tiempo_ejecucion
        WHERE id = _identificador;

        -- Obtiene total de elementos del arreglo
        total_filas:= array_length( _extra_data, 1 );
        cont_fila := 1;

        FOR cont_fila IN 1 .. total_filas LOOP

            -- 1: no esta eliminado
            -- 0: eliminado
            IF _extra_data[cont_fila].removido != 0 THEN

                IF _extra_data[cont_fila].status_autorizacion THEN

                    -- Si esta autorizado por default le asignamos true al campo requiere_autorizacion
                    requiere_autorizacion := true;

                ELSE

                    requiere_autorizacion := _extra_data[cont_fila].requiere_autorizacion;

                END IF;

                IF _extra_data[cont_fila].id_detalle = 0 THEN

                    -- Crea registros para tabla poc_pedidos_detalle porque es nueva partida
                    INSERT INTO poc_cot_detalle (
                        poc_cot_id,
                        inv_prod_id,
                        inv_presentacion_id,
                        cantidad,
                        precio_unitario,
                        gral_mon_id,
                        gral_impto_id,
                        valor_imp,
                        inv_prod_unidad_id,
                        requiere_aut,
                        autorizado,
                        precio_aut,
                        gral_usr_id_aut
                    ) VALUES (
                        _identificador,
                        _extra_data[cont_fila].id_producto,
                        _extra_data[cont_fila].id_presentacion,
                        _extra_data[cont_fila].cantidad,
                        _extra_data[cont_fila].precio,
                        _extra_data[cont_fila].moneda_grid,
                        _extra_data[cont_fila].id_imp_prod,
                        _extra_data[cont_fila].valor_imp,
                        _extra_data[cont_fila].unidad_id,
                        requiere_autorizacion,
                        _extra_data[cont_fila].status_autorizacion,
                        _extra_data[cont_fila].precio_autorizado,
                        _extra_data[cont_fila].id_user_aut
                    );

                ELSE

                    -- Actualizar registro ya existente
                    UPDATE poc_cot_detalle SET
                        cantidad = _extra_data[cont_fila].cantidad,
                        precio_unitario = _extra_data[cont_fila].precio,
                        gral_mon_id = _extra_data[cont_fila].moneda_grid,
                        gral_impto_id = _extra_data[cont_fila].id_imp_prod,
                        valor_imp = _extra_data[cont_fila].valor_imp,
                        inv_prod_unidad_id = _extra_data[cont_fila].unidad_id,
                        requiere_aut = requiere_autorizacion,
                        autorizado = _extra_data[cont_fila].status_autorizacion,
                        precio_aut = _extra_data[cont_fila].precio_autorizado,
                        gral_usr_id_aut = _extra_data[cont_fila].id_user_aut
                    WHERE id = _extra_data[cont_fila].id_detalle;

                END IF;

                importe_partida := _extra_data[cont_fila].cantidad * _extra_data[cont_fila].precio;

                IF _moneda_id <> _extra_data[cont_fila].moneda_grid THEN

                    IF _moneda_id = 1 AND _extra_data[cont_fila].moneda_grid <> 1 THEN

                        importe_partida := importe_partida::double precision * _tipo_cambio;

                    ELSE

                        IF _moneda_id <> 1 AND _extra_data[cont_fila].moneda_grid = 1 THEN

                            importe_partida :=  importe_partida::double precision / _tipo_cambio;

                        END IF;

                    END IF;

                END IF;

                -- Redondear el importe de la partida a 4 digitos
                importe_partida := round(importe_partida::double precision::numeric,4)::double precision;
                impuesto_partida := round((importe_partida * _extra_data[cont_fila].valor_imp)::numeric,4)::double precision;

                monto_subtotal := round((monto_subtotal + importe_partida)::numeric,4)::double precision;
                monto_impuesto := round((monto_impuesto + impuesto_partida)::numeric,4)::double precision;

            ELSE

                IF _extra_data[cont_fila].removido = 0 THEN

                    -- Eliminar registro
                    DELETE FROM poc_cot_detalle
                    WHERE id = _extra_data[cont_fila].id_detalle;

                END IF;

            END IF;

        END LOOP;

        monto_total := monto_subtotal + monto_impuesto;

        UPDATE poc_cot SET
            subtotal = monto_subtotal,
            impuesto = monto_impuesto,
            total = monto_total
        WHERE id = _identificador;

        valor_retorno := '1';

    END IF;

    RETURN valor_retorno;

END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


CREATE TYPE grid_renglon_pedido AS (
    id                  integer,
    to_keep             integer,
    inv_prod_id         integer,
    presentacion_id     integer,
    cantidad            double precision,
    precio_unitario     double precision,
    gral_imp_id         integer,
    valor_imp           double precision,
    inv_prod_unidad_id  integer,
    gral_ieps_id        integer,
    valor_ieps          double precision,
    descto              double precision,
    cot_id              integer,
    cot_detalle_id      integer,
    requiere_aut        boolean,
    autorizado          boolean,
    precio_aut          double precision,
    gral_usr_id_aut     integer,
    gral_imptos_ret_id  integer,
    tasa_ret            double precision
);

CREATE FUNCTION public.pedido_edit(
    _usuario_id           integer,
    _agente_id            integer,
    _cliente_id           integer,
    _cliente_df_id        integer,
    _almacen_id           integer,
    _moneda_id            integer,
    _prov_credias_id      integer,
    _cfdi_met_pago_id     integer,
    _forma_pago_id        integer,
    _cfdi_uso_id          integer,
    _pedido_id            integer,
    _tasa_retencion_immex double precision,
    _tipo_cambio          double precision,
    _porcentaje_descto    double precision,
    _descto_allowed       boolean,
    _enviar_obser_fac     boolean,
    _flete_enabled        boolean,
    _enviar_ruta          boolean,
    _observaciones        text,
    _motivo_descto        text,
    _transporte           text,
    _fecha_compromiso     character varying,
    _lugar_entrega        character varying,
    _orden_compra         character varying,
    _num_cuenta           character varying,
    _folio_cot            character varying,
    _grid_detalle         grid_renglon_pedido[]
) RETURNS character varying
AS $$
DECLARE
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name:     Creación/Edición de Pedido de ventas                            >>
    -- >> Version:  CDGB                                                            >>
    -- >> Date:     8/Ene/2021                                                      >>
    -- >>                                                                           >>
    -- >> Si _pedido_id = 0 entonces se trata de una Creación de Pedido de ventas.  >>
    -- >> Si _pedido_id > 0 entonces se trata de una Edición de Pedido de ventas.   >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    --estas variables se utilizan en la mayoria de los catalogos
    valor_retorno character varying := '0';
    emp_id integer := 0;
    suc_id integer := 0;
    id_tipo_consecutivo integer := 0;
    ultimo_id integer := 0;
    ultimo_id_det integer := 0;
    espacio_tiempo_ejecucion timestamp with time zone := now();
    ano_actual integer := 0;
    sql_select character varying := '';

    detalle grid_renglon_pedido;
    --total de elementos de arreglo
    no_rows integer := 0;
    --contador de filas o posiciones del arreglo
    counter integer := 0;

    --variable para pedidos
    --parametros de Facturacion
    facpar record;
    ultimo_id_proceso integer := 0;
    id_proceso integer := 0;
    id_proceso_flujo integer := 0;
    prefijo_consecutivo character varying := '';
    nuevo_consecutivo bigint := 0;
    nuevo_folio character varying := '';
    incluye_modulo_produccion boolean := FALSE;
    tipo_prod integer := 0;
    id_producto integer := 0;
    total_existencia double precision := 0;
    cant_reservada_anterior double precision := 0;
    cant_reservar_nuevo double precision := 0;
    generar_backorder boolean := FALSE;

    importe_del_descto_partida double precision := 0;
    importe_partida_con_descto double precision := 0;
    suma_descuento double precision := 0;
    suma_subtotal_con_descuento double precision := 0;

    importe_partida double precision := 0;
    impuesto_partida double precision := 0;
    monto_subtotal double precision := 0;
    monto_total double precision := 0;
    monto_impuesto double precision := 0;
    total_retencion double precision := 0;
    importe_ieps_partida double precision := 0;
    suma_ieps double precision := 0;
    retener_iva boolean := FALSE;
    tasa_retencion double precision := 0;
    retencion_partida double precision := 0;
    suma_retencion_de_partidas double precision := 0;

    --variables autorizacion de pedidos
    pedido record;
    fila record;

    --numero de decimales permitidos para la unidad
    noDecUnidad integer := 0;
    --equivalencia de la presentacion en la unidad del producto
    equivalenciaPres double precision := 0;
    cantPresAsignado double precision := 0;
    cantPresReservAnterior double precision := 0;
    --Variable que indica  si se debe controlar Existencias por Presentacion
    controlExisPres boolean := FALSE;

    --Id de la unidad de medida del producto
    idUnidadMedida integer := 0;
    --Nombre de la unidad de medida del producto
    nombreUnidadMedida character varying := '0';
    --Cantidad en la unidad de Venta, esto se utiliza cuando la unidad del producto es diferente a la de venta
    cantUnidadVenta double precision := 0;

    --Variable que indica si una partida generó requisicion
    generar_requisicion  boolean := FALSE;

BEGIN
    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;

    SELECT gral_suc.empresa_id,
           gral_usr_suc.gral_suc_id
    FROM   gral_usr_suc
    JOIN   gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE  gral_usr_suc.gral_usr_id = _usuario_id
    INTO   emp_id,
           suc_id;

    --Obtener parametros para la facturacion
    SELECT *
    FROM   fac_par
    WHERE  gral_suc_id = suc_id
    INTO   facpar;

    -- query para verificar si la Empresa actual incluye Modulo de Produccion y control de Existencias por Presentacion
    SELECT incluye_produccion,
           control_exis_pres
    FROM   gral_emp
    WHERE  id = emp_id
    INTO   incluye_modulo_produccion,
           controlExisPres;

    -------------------------------------------------- NEW pedido -----------------------------------------------------
    IF _pedido_id = 0 THEN

        --crea registro en tabla erp_proceso y retorna el id del registro creado. El flujo del proceso es 4 = Pedido
        INSERT INTO erp_proceso(
            proceso_flujo_id,
            empresa_id,
            sucursal_id
        ) VALUES (
            4,
            emp_id,
            suc_id
        ) RETURNING id into ultimo_id_proceso;

        --consecutivo de pedidos
        id_tipo_consecutivo := 7;
            
        -- aqui entra para tomar el consecutivo del pedido de la sucursal actual
        UPDATE gral_cons
        SET consecutivo = (
                SELECT sbt.consecutivo + 1
                FROM gral_cons AS sbt
                WHERE sbt.id = gral_cons.id
            )
        WHERE gral_emp_id       = emp_id
          AND gral_suc_id       = suc_id
          AND gral_cons_tipo_id = id_tipo_consecutivo
        RETURNING prefijo,
                  consecutivo
        INTO prefijo_consecutivo,
             nuevo_consecutivo;

        -- concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio del pedido
        nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

        -- crear registro en la tabla poc_pedidos y retorna el id del registro creado
        INSERT INTO poc_pedidos(
            folio,                  -- nuevo_folio,
            cxc_clie_id,            -- _cliente_id,
            moneda_id,              -- _moneda_id,
            observaciones,          -- _observaciones,
            tipo_cambio,            -- _tipo_cambio,
            cxc_agen_id,            -- _agente_id,
            cxp_prov_credias_id,    -- _prov_credias_id,
            orden_compra,           -- _orden_compra,
            proceso_id,             -- ultimo_id_proceso,
            fecha_compromiso,       -- _fecha_compromiso::date,
            lugar_entrega,          -- _lugar_entrega,
            transporte,             -- _transporte,
            tasa_retencion_immex,   -- _tasa_retencion_immex,
            fac_metodos_pago_id,    -- _forma_pago_id,
            no_cuenta,              -- _num_cuenta,
            enviar_ruta,            -- _enviar_ruta,
            inv_alm_id,             -- _almacen_id::smallint
            cxc_clie_df_id,         -- _cliente_df_id,
            enviar_obser_fac,       -- _enviar_obser_fac,
            flete,                  -- _flete_enabled,
            subtotal,               -- 0,
            impuesto,               -- 0,
            monto_retencion,        -- 0,
            total,                  -- 0,
            borrado_logico,         -- FALSE,
            cancelado,              -- FALSE,
            momento_creacion,       -- espacio_tiempo_ejecucion,
            gral_usr_id_creacion,   -- _usuario_id
            motivo_descto,          -- _motivo_descto
            porcentaje_descto,      -- _porcentaje_descto
            folio_cot,              -- _folio_cot
            cfdi_usos_id,           -- _cfdi_uso_id
            cfdi_metodo_id          -- _cfdi_met_pago_id            
        ) VALUES (
            nuevo_folio, 
            _cliente_id, 
            _moneda_id, 
            _observaciones, 
            _tipo_cambio, 
            _agente_id, 
            _prov_credias_id, 
            _orden_compra, 
            ultimo_id_proceso, 
            _fecha_compromiso::date, 
            _lugar_entrega, 
            _transporte, 
            _tasa_retencion_immex, 
            _forma_pago_id, 
            _num_cuenta, 
            _enviar_ruta, 
            _almacen_id::smallint, 
            _cliente_df_id, 
            _enviar_obser_fac, 
            _flete_enabled, 
            0, 
            0, 
            0, 
            0, 
            FALSE, 
            FALSE, 
            espacio_tiempo_ejecucion, 
            _usuario_id, 
            _motivo_descto, 
            _porcentaje_descto, 
            _folio_cot,
            _cfdi_uso_id,
            _cfdi_met_pago_id
        ) RETURNING id INTO ultimo_id;
            
        --obtiene total de elementos del arreglo
        no_rows := array_length(_grid_detalle, 1);
        counter := 1;
        FOR counter IN 1 .. no_rows LOOP
            generar_requisicion := FALSE;
            retencion_partida   := 0;
            
            detalle := _grid_detalle[counter];

            -- 1: se conserva, 0: se elimina
            IF detalle.to_keep <> 0 THEN
                
                cantPresAsignado := 0;
                equivalenciaPres := 0;
                noDecUnidad := 0;
                --Id de la unidad de medida del producto
                idUnidadMedida := 0;
                --Nombre de la unidad de medida del producto
                nombreUnidadMedida := '';
                --Cantidad en la unidad de Venta, esto se utiliza cuando la unidad del producto es diferente a la de venta
                cantUnidadVenta := 0;
                
                --Obtener datos del Producto
                SELECT inv_prod.tipo_de_producto_id AS tipo_producto,
                       inv_prod.unidad_id,
                       inv_prod_unidades.titulo,
                       (CASE
                           WHEN inv_prod_unidades.id IS NULL THEN 0
                           ELSE inv_prod_unidades.decimales
                        END) AS no_dec
                FROM inv_prod
                LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id
                WHERE inv_prod.id = detalle.inv_prod_id 
                INTO  tipo_prod,
                      idUnidadMedida,
                      nombreUnidadMedida,
                      noDecUnidad;
                
                IF noDecUnidad IS NULL THEN noDecUnidad := 0; END IF;

                --Tomamos la cantidad en la unidad de Venta seleccionada por el usuario
                cantUnidadVenta := detalle.cantidad;

                --Redondear la cantidad de la Partida
                detalle.cantidad := round(detalle.cantidad::numeric, noDecUnidad)::double precision;
                cantUnidadVenta  := round(cantUnidadVenta::numeric, noDecUnidad)::double precision; 
                
                --Si el tipo de producto es diferente de 4, hay que RESERVAR existencias
                --tipo = 4 Servicios
                --para el tipo servicios no se debe reservar existencias
                IF tipo_prod<>4 THEN 
                
                    IF incluye_modulo_produccion = FALSE THEN 
                        --Aqui entra si la Empresa NO INCLUYE Modulo de Produccion
                        
                        --reservar toda cantidad la cantidad del pedido
                        cant_reservar_nuevo := detalle.cantidad;
                        generar_backorder := FALSE;
                    ELSE
                        --RAISE EXCEPTION '%','tipo_prod=' || tipo_prod;
                        
                        --Solo para productos formulados
                        IF tipo_prod = 1 OR tipo_prod = 2 OR tipo_prod = 8 THEN
                            --llamada a proc que devuelve la existencia del producto. 
                            --El tipo de busqueda de existencia es 1 = Busqueda en el almacen de la Sucursal
                            --el valor FALSE que se le esta pasando es para indicarle que en las existencias no incluya reservados, y que solo me devualva existencias disponibles
                            SELECT inv_calculo_existencia_producto AS existencia
                            FROM   inv_calculo_existencia_producto(1, FALSE, detalle.inv_prod_id, _usuario_id, _almacen_id)
                            INTO   total_existencia; 
                            
                            --Redondear la existencia del producto
                            total_existencia := round(total_existencia::numeric, noDecUnidad)::double precision;
                            
                            IF total_existencia < detalle.cantidad THEN
                                IF total_existencia <=0 THEN 
                                    --reservar cero
                                    cant_reservar_nuevo = 0;
                                ELSE
                                    --tomar la existencia para reservar
                                    cant_reservar_nuevo := total_existencia;
                                END IF;
                                
                                generar_backorder := TRUE;
                            ELSE
                                --Reservar toda la cantidad del  pedido
                                cant_reservar_nuevo := detalle.cantidad;
                                
                                generar_backorder := FALSE;
                            END IF;
                        END IF;
                    END IF;
                    
                    
                    /*
                    "1";"Prod. Terminado";FALSE
                    "2";"Prod. Intermedio";FALSE
                    "3";"Kit";FALSE
                    "4";"Servicios";FALSE
                    "5";"Refacciones";FALSE
                    "6";"Accesorios";FALSE
                    "7";"Materia Prima";FALSE
                    "8";"Prod. en Desarrollo";FALSE
                    */
                    IF facpar.permitir_req_com THEN 
                        --7 = Materia Prima - Hay que generar una requisicion de compra.
                        IF tipo_prod = 7 THEN 
                            --llamada a proc que devuelve la existencia del producto. 
                            --El tipo de busqueda de existencia es 1 = Busqueda en el almacen de la Sucursal
                            --el valor FALSE que se le esta pasando es para indicarle que en las existencias no incluya reservados, y que solo me devualva existencias disponibles
                            SELECT inv_calculo_existencia_producto AS existencia
                            FROM   inv_calculo_existencia_producto(1, FALSE, detalle.inv_prod_id, _usuario_id, _almacen_id)
                            INTO   total_existencia; 
                            
                            --Redondear la existencia del producto
                            total_existencia := round(total_existencia::numeric, noDecUnidad)::double precision;
                            
                            IF total_existencia < detalle.cantidad THEN
                                IF total_existencia <=0 THEN 
                                    --reservar cero
                                    cant_reservar_nuevo = 0;
                                ELSE
                                    --tomar la existencia para reservar
                                    cant_reservar_nuevo := total_existencia;
                                END IF;
                                
                                generar_requisicion := TRUE;
                            ELSE
                                --Reservar toda la cantidad del  pedido
                                cant_reservar_nuevo := detalle.cantidad;
                                
                                generar_requisicion := FALSE;
                            END IF;
                        END IF;
                    ELSE
                        if tipo_prod = 7 then  
                            --Reservar toda cantidad la cantidad del pedido ya que no incluye
                            cant_reservar_nuevo := detalle.cantidad;
                            generar_backorder := FALSE;
                            generar_requisicion := FALSE;
                        end if;
                    END IF;
                    
                    --RAISE EXCEPTION '%','permitir_req_com=' || facpar.permitir_req_com || '    tipo_prod=' || tipo_prod || '    cant_reservar_nuevo=' || cant_reservar_nuevo;
                    
                    --Redondear la cantidad de a Reservar
                    cant_reservar_nuevo := round(cant_reservar_nuevo::numeric, noDecUnidad)::double precision;
                    
                    --Reservar cantidad para el  pedido
                    UPDATE inv_exi
                    SET    reservado = (reservado::double precision + cant_reservar_nuevo::double precision)
                    WHERE  inv_prod_id = detalle.inv_prod_id
                      AND  inv_alm_id  = _almacen_id
                      AND  ano         = ano_actual;
                    
                    ------inicia reservar existencias en presentaciones--------------------------
                    --Verificar si hay que validar existencias de Presentaciones
                    IF controlExisPres = TRUE THEN 
                        --Verificar si hay que validar las existencias de presentaciones desde el Pedido.
                        --TRUE = Validar presentaciones desde el Pedido
                        --FALSE = No validar presentaciones desde el Pedido
                        IF facpar.validar_pres_pedido = TRUE THEN 
                            --buscar la equivalencia de la Presentacion
                            SELECT cantidad
                            FROM inv_prod_presentaciones
                            WHERE id = detalle.presentacion_id 
                            INTO equivalenciaPres;
                            
                            IF equivalenciaPres IS NULL THEN equivalenciaPres := 0; END IF;
                            
                            --Convertir a su equivalencia en Presentacion, la cantidad de la partida actual del pedido
                            cantPresAsignado := cant_reservar_nuevo::double precision / equivalenciaPres::double precision;
                            
                            --Redondear la cantidad de Presentaciones Asignado en la partida
                            cantPresAsignado := round(cantPresAsignado::numeric, noDecUnidad)::double precision; 
                            
                            --Reservar existencia en inv_exi_pres
                            UPDATE inv_exi_pres
                            SET   reservado = (reservado::double precision + cantPresAsignado::double precision)
                            WHERE inv_alm_id               = _almacen_id
                              AND inv_prod_id              = detalle.inv_prod_id
                              AND inv_prod_presentacion_id = detalle.presentacion_id;
                            
                        END IF;
                    END IF;
                    ------termina reservar existencias de Presentaciones------------------------------------
                    
                ELSE
                    generar_backorder := FALSE;
                    generar_requisicion := FALSE;
                    cant_reservar_nuevo := 0;
                END IF;--termina IF tipo 4

                --Tasa ieps
                IF detalle.valor_ieps>0 THEN 
                    detalle.valor_ieps := detalle.valor_ieps/100;
                END IF;

                --Tasa retencion
                IF detalle.tasa_ret>0 THEN 
                    detalle.tasa_ret := detalle.tasa_ret/100;
                END IF;
                
                --Crea registros para tabla poc_pedidos_detalle
                INSERT INTO poc_pedidos_detalle(
                    poc_pedido_id,
                    inv_prod_id,
                    presentacion_id,
                    gral_imp_id,
                    cantidad,
                    precio_unitario,
                    valor_imp,
                    reservado,
                    backorder,
                    inv_prod_unidad_id,
                    gral_ieps_id,
                    valor_ieps,
                    descto,
                    requisicion,
                    requiere_aut,
                    autorizado,
                    precio_aut,
                    gral_usr_id_aut,
                    gral_imptos_ret_id,
                    tasa_ret
                ) VALUES (
                    ultimo_id,
                    detalle.inv_prod_id,
                    detalle.presentacion_id,
                    detalle.gral_imp_id,
                    cantUnidadVenta::double precision,
                    detalle.precio_unitario,
                    detalle.valor_imp,
                    cant_reservar_nuevo,
                    generar_backorder,
                    detalle.inv_prod_unidad_id,
                    detalle.gral_ieps_id,
                    detalle.valor_ieps,
                    detalle.descto,
                    generar_requisicion,
                    detalle.requiere_aut,
                    detalle.autorizado,
                    detalle.precio_aut,
                    detalle.gral_usr_id_aut,
                    detalle.gral_imptos_ret_id,
                    detalle.tasa_ret
                ) RETURNING id INTO ultimo_id_det;
                
                --Calcula el Importe de la Partida
                importe_partida := round((cantUnidadVenta::double precision * detalle.precio_unitario)::numeric, 4)::double precision;
                
                --Calcula el IEPS de la partida
                importe_ieps_partida := round((importe_partida::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                
                --Calcula el IVA de la Partida
                impuesto_partida := (importe_partida::double precision + importe_ieps_partida::double precision) * detalle.valor_imp;
                
                --detalle.gral_imptos_ret_id        retencion_id
                --detalle.tasa_ret    retencion_tasa
                
                --Calcular el importe de la retencion de la partida si existe la tasa de retencion
                if detalle.tasa_ret>0 then 
                    retencion_partida := round((importe_partida::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                end if;
                
                
                --Cargar tabla que relaciona el pedido con la cotizacion
                IF detalle.cot_detalle_id > 0 THEN 
                    INSERT INTO poc_ped_cot(
                        poc_ped_id,
                        poc_cot_id,
                        poc_ped_det_id,
                        poc_cot_det_id
                    ) VALUES (
                        ultimo_id,
                        detalle.cot_id,
                        ultimo_id_det,
                        detalle.cot_detalle_id
                    );
                END IF;
                
                IF _descto_allowed THEN
                    IF detalle.descto > 0 THEN
                        --$pu_con_descto.val(parseFloat(parseFloat($campoPrecioU.val()) - (parseFloat($campoPrecioU.val()) * (parseFloat($vdescto.val())/100))).toFixed(4));
                        importe_del_descto_partida = round((importe_partida * (detalle.descto/100))::numeric, 4)::double precision;

                        importe_partida_con_descto = round((importe_partida - importe_del_descto_partida)::numeric, 4)::double precision;
                        
                        --Recalcular el IEPS de la partida tomando el importe_partida_con_descto
                        importe_ieps_partida := round((importe_partida_con_descto::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                        
                        --Recalcular el IVA de la Partida tomando el importe_partida_con_descto
                        impuesto_partida := (importe_partida_con_descto::double precision + importe_ieps_partida::double precision) * detalle.valor_imp;
                        
                        --Reclacular el nuevo el importe de la retencion de la partida si existe la tasa de retencion
                        if detalle.tasa_ret>0 then 
                            retencion_partida := round((importe_partida::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                        end if;
                    END IF;
                END IF;
                
                suma_descuento = suma_descuento + importe_del_descto_partida::double precision;
                suma_subtotal_con_descuento = suma_subtotal_con_descuento + importe_partida_con_descto::double precision;
                
                monto_subtotal := monto_subtotal + importe_partida::double precision;
                suma_ieps := suma_ieps + importe_ieps_partida::double precision; 
                monto_impuesto := monto_impuesto + impuesto_partida::double precision;
                suma_retencion_de_partidas := suma_retencion_de_partidas + retencion_partida::double precision;
            END IF;
        END LOOP;
        
        --Verificar si hay que retener iva para este cliente
        SELECT empresa_immex,
               (CASE
                   WHEN tasa_ret_immex IS NULL THEN 0
                   ELSE tasa_ret_immex::double precision / 100
                END)
        FROM cxc_clie
        WHERE id = _cliente_id
        INTO retener_iva,
             tasa_retencion;
        
        IF _descto_allowed AND suma_descuento>0 THEN
            IF retener_iva = TRUE THEN 
                total_retencion := suma_subtotal_con_descuento::double precision * tasa_retencion;
            ELSE
                total_retencion :=0;
            END IF;
            
            if suma_retencion_de_partidas > 0 then 
                total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
            end if;
            
            --Calcula el monto del pedido
            monto_total := suma_subtotal_con_descuento::double precision + suma_ieps::double precision + monto_impuesto::double precision - total_retencion::double precision;
            
            --Actualiza campos subtotal, impuesto, retencion, total de tabla poc_pedidos
            UPDATE poc_pedidos
            SET    subtotal        = suma_subtotal_con_descuento,
                   monto_descto    = suma_descuento,
                   monto_ieps      = suma_ieps,
                   impuesto        = monto_impuesto,
                   monto_retencion = total_retencion,
                   total           = monto_total
            WHERE  id = ultimo_id;
        ELSE
            IF retener_iva = TRUE THEN
                total_retencion := monto_subtotal::double precision * tasa_retencion;
            ELSE
                total_retencion := 0;
            END IF;

            if suma_retencion_de_partidas > 0 then 
                total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
            end if;
            
            --Calcula el monto del pedido
            monto_total := monto_subtotal::double precision + suma_ieps::double precision + monto_impuesto::double precision - total_retencion::double precision;
            
            --Actualiza campos subtotal, impuesto, retencion, total de tabla poc_pedidos
            UPDATE poc_pedidos
            SET    subtotal        = monto_subtotal,
                   monto_ieps      = suma_ieps,
                   impuesto        = monto_impuesto,
                   monto_retencion = total_retencion,
                   total           = monto_total
            WHERE  id = ultimo_id;
        END IF;
        
        valor_retorno := '1';
    END IF; -- termina accion NEW pedido

    -------------------------------------------------- EDIT pedido -----------------------------------------------------
    IF _pedido_id > 0 THEN
        
        --obtener el id del proceso para este pedido
        SELECT proceso_id
        FROM   poc_pedidos
        WHERE  id = _pedido_id
        INTO   id_proceso;
        
        --obtener el id del flujo del proceso
        SELECT proceso_flujo_id
        FROM   erp_proceso
        WHERE  id = id_proceso
        INTO   id_proceso_flujo;
        
        IF id_proceso_flujo = 4 THEN 
            
            UPDATE poc_pedidos 
            SET cxc_clie_id               = _cliente_id,
                moneda_id                 = _moneda_id,
                observaciones             = _observaciones,
                tipo_cambio               = _tipo_cambio,
                cxc_agen_id               = _agente_id,
                cxp_prov_credias_id       = _prov_credias_id,
                orden_compra              = _orden_compra, 
                fecha_compromiso          = _fecha_compromiso::date,
                lugar_entrega             = _lugar_entrega, 
                transporte                = _transporte, 
                tasa_retencion_immex      = _tasa_retencion_immex, 
                fac_metodos_pago_id       = _forma_pago_id, 
                no_cuenta                 = _num_cuenta, 
                enviar_ruta               = _enviar_ruta, 
                inv_alm_id                = _almacen_id::smallint, 
                cxc_clie_df_id            = _cliente_df_id, 
                enviar_obser_fac          = _enviar_obser_fac, 
                flete                     = _flete_enabled, 
                momento_actualizacion     = espacio_tiempo_ejecucion, 
                gral_usr_id_actualizacion = _usuario_id, 
                motivo_descto             = _motivo_descto, 
                porcentaje_descto         = _porcentaje_descto,
                cfdi_usos_id              = _cfdi_uso_id,
                cfdi_metodo_id            = _cfdi_met_pago_id
            WHERE id = _pedido_id;
            
            no_rows := array_length(_grid_detalle, 1);
            counter := 1;

            FOR counter IN 1 .. no_rows LOOP
                generar_requisicion := FALSE;
                generar_backorder := FALSE;
                retencion_partida := 0;
                
                detalle := _grid_detalle[counter];
                
                -- 1: se conserva, 0: se elimina
                IF detalle.to_keep <> 0 THEN
                    cant_reservada_anterior := 0;
                    cant_reservar_nuevo     := 0;
                    cantPresAsignado        := 0;
                    equivalenciaPres        := 0;
                    noDecUnidad             := 0;
                    cantPresReservAnterior  := 0;
                    idUnidadMedida          := 0;
                    nombreUnidadMedida      := '';
                    cantUnidadVenta         := 0;
                
                    --Obtener datos del Producto
                    SELECT inv_prod.tipo_de_producto_id AS tipo_producto,
                           inv_prod.unidad_id,
                           inv_prod_unidades.titulo,
                           (CASE
                               WHEN inv_prod_unidades.id IS NULL THEN 0
                               ELSE inv_prod_unidades.decimales
                            END) AS no_dec
                    FROM inv_prod
                    LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id
                    WHERE inv_prod.id = detalle.inv_prod_id 
                    INTO tipo_prod,
                         idUnidadMedida,
                         nombreUnidadMedida,
                         noDecUnidad;
                    
                    IF noDecUnidad IS NULL THEN noDecUnidad := 0; END IF;

                    --Tomamos la cantidad en la unidad de Venta seleccionada por el usuario
                    cantUnidadVenta := detalle.cantidad;

                    --Redondear la cantidad de la Partida
                    detalle.cantidad := round(detalle.cantidad::numeric, noDecUnidad)::double precision;
                    cantUnidadVenta  := round(cantUnidadVenta::numeric, noDecUnidad)::double precision; 
                    
                    --Si el tipo de producto es diferente de 4, hay que RESERVAR existencias
                    --tipo = 4 Servicios
                    --para el tipo servicios no se debe reservar existencias
                    IF tipo_prod::integer<>4 THEN 

                        --Solo Para productos formulados
                        IF tipo_prod = 5 OR tipo_prod = 6 THEN 
                            --Reservar toda cantidad de la partida del pedido
                            cant_reservar_nuevo := detalle.cantidad;
                            generar_backorder := FALSE;
                        end if;
                                
                        
                        IF incluye_modulo_produccion = FALSE THEN
                            --Aqui entra si la Empresa NO INCLUYE Modulo de Produccion
                            
                            --Solo Para productos formulados
                            IF tipo_prod = 1 OR tipo_prod = 2 OR tipo_prod = 8 THEN
                                --si es diferente de cero estamos en editar
                                IF detalle.id > 0 THEN 
                                    --Buscamos la cantidad reservada anterior
                                    SELECT inv_prod_id,
                                           reservado
                                    FROM   poc_pedidos_detalle
                                    WHERE  id = detalle.id
                                    INTO   id_producto,
                                           cant_reservada_anterior;
                                    
                                    --redondear la cantidad de Presentaciones reservada anteriormente
                                    cant_reservada_anterior := round(cant_reservada_anterior::numeric, noDecUnidad)::double precision;
                                    
                                    --restar la cantidad reservada anterior
                                    UPDATE inv_exi
                                    SET    reservado = (reservado::double precision - cant_reservada_anterior::double precision)
                                    WHERE  inv_prod_id = id_producto
                                      AND  inv_alm_id  = _almacen_id
                                      AND  ano         = ano_actual;
                                END IF;
                                
                                --Reservar toda cantidad de la partida del pedido
                                cant_reservar_nuevo := detalle.cantidad;
                                generar_backorder := FALSE;
                                
                            END IF;
                        ELSE
                            --Solo Para productos formulados
                            IF tipo_prod = 1 OR tipo_prod = 2 OR tipo_prod = 8 THEN
                                --llamada a proc que devuelve la existencia del producto. 
                                --El tipo de busqueda de existencia es 1 = Busqueda en el almacen de la Sucursal
                                --el valor FALSE que se le esta pasando es para indicarle que en las existencias no incluya reservados, y que solo me devualva existencias disponibles
                                SELECT inv_calculo_existencia_producto AS existencia
                                FROM   inv_calculo_existencia_producto(1, FALSE, detalle.inv_prod_id, _usuario_id, _almacen_id)
                                INTO   total_existencia; 
                                
                                --Si es diferente de cero estamos en editar
                                IF detalle.id > 0 THEN 
                                    --buscamos la cantidad reservada anterior
                                    SELECT inv_prod_id,
                                           reservado
                                    FROM   poc_pedidos_detalle
                                    WHERE id = detalle.id
                                    INTO  id_producto,
                                          cant_reservada_anterior;

                                    --redondear la cantidad de Presentaciones reservada anteriormente
                                    cant_reservada_anterior := round(cant_reservada_anterior::numeric, noDecUnidad)::double precision;
                                    
                                    --restar la cantidad reservada anterior
                                    UPDATE inv_exi
                                    SET    reservado = (reservado::double precision - cant_reservada_anterior::double precision)
                                    WHERE  inv_prod_id = id_producto
                                      AND  inv_alm_id  = _almacen_id
                                      AND  ano         = ano_actual;
                                    
                                    --le sumamos a la existencia la cantidad reservada anterior para tener la existencia real
                                    total_existencia := total_existencia + cant_reservada_anterior;
                                END IF;
                                
                                IF total_existencia < detalle.cantidad THEN
                                    IF total_existencia <=0 THEN 
                                        cant_reservar_nuevo = 0;--reservar cero
                                    ELSE
                                        cant_reservar_nuevo := total_existencia;--tomar la existencia para reservar
                                    END IF;
                                    
                                    generar_backorder := TRUE;
                                ELSE
                                    cant_reservar_nuevo := detalle.cantidad;--reservar toda la cantidad del  pedido
                                    generar_backorder := FALSE;
                                END IF;
                            END IF;
                        END IF;


                        IF facpar.permitir_req_com THEN 
                            --7 = Materia Prima - Hay que generar una requisicion de compra.
                            IF tipo_prod = 7 THEN 
                                --llamada a proc que devuelve la existencia del producto. 
                                --El tipo de busqueda de existencia es 1 = Busqueda en el almacen de la Sucursal
                                --el valor FALSE que se le esta pasando es para indicarle que en las existencias no incluya reservados, y que solo me devualva existencias disponibles
                                SELECT inv_calculo_existencia_producto AS existencia
                                FROM   inv_calculo_existencia_producto(1, FALSE, detalle.inv_prod_id, _usuario_id, _almacen_id)
                                INTO   total_existencia; 
                                
                                --si es diferente de cero estamos en editar
                                IF detalle.id > 0 THEN 
                                    --Buscamos la cantidad reservada anterior
                                    SELECT inv_prod_id,
                                           reservado
                                    FROM   poc_pedidos_detalle
                                    WHERE  id = detalle.id
                                    INTO   id_producto,
                                           cant_reservada_anterior;

                                    --Redondear la cantidad de Presentaciones reservada anteriormente
                                    cant_reservada_anterior := round(cant_reservada_anterior::numeric, noDecUnidad)::double precision;
                                    
                                    --Restar la cantidad reservada anterior
                                    UPDATE inv_exi
                                    SET    reservado = (reservado::double precision - cant_reservada_anterior::double precision)
                                    WHERE  inv_prod_id = id_producto
                                      AND  inv_alm_id  = _almacen_id
                                      AND  ano         = ano_actual;
                                    
                                    --Le sumamos a la existencia la cantidad reservada anterior para tener la existencia real
                                    total_existencia := total_existencia + cant_reservada_anterior;
                                END IF;
                                
                                IF total_existencia < detalle.cantidad THEN
                                    IF total_existencia <=0 THEN 
                                        --Reservar cero
                                        cant_reservar_nuevo = 0;
                                    ELSE
                                        --Tomar la existencia para reservar
                                        cant_reservar_nuevo := total_existencia;
                                    END IF;
                                    
                                    generar_requisicion := TRUE;
                                ELSE
                                    --Reservar toda la cantidad del  pedido
                                    cant_reservar_nuevo := detalle.cantidad;
                                    generar_requisicion := FALSE;
                                END IF;
                            END IF;
                        ELSE
                            if tipo_prod = 7 then  
                                --llamada a proc que devuelve la existencia del producto. 
                                --El tipo de busqueda de existencia es 1 = Busqueda en el almacen de la Sucursal
                                --el valor FALSE que se le esta pasando es para indicarle que en las existencias no incluya reservados, y que solo me devualva existencias disponibles
                                SELECT inv_calculo_existencia_producto AS existencia
                                FROM   inv_calculo_existencia_producto(1, FALSE, detalle.inv_prod_id, _usuario_id, _almacen_id)
                                INTO   total_existencia; 
                                
                                --si es diferente de cero estamos en editar
                                IF detalle.id > 0 THEN 
                                    --Buscamos la cantidad reservada anterior
                                    SELECT inv_prod_id,
                                           reservado
                                    FROM   poc_pedidos_detalle
                                    WHERE  id = detalle.id
                                    INTO   id_producto,
                                           cant_reservada_anterior;

                                    --Redondear la cantidad de Presentaciones reservada anteriormente
                                    cant_reservada_anterior := round(cant_reservada_anterior::numeric, noDecUnidad)::double precision;
                                    
                                    --Restar la cantidad reservada anterior
                                    UPDATE inv_exi
                                    SET    reservado = (reservado::double precision - cant_reservada_anterior::double precision)
                                    WHERE  inv_prod_id = id_producto
                                      AND  inv_alm_id  = _almacen_id
                                      AND  ano         = ano_actual;
                                    
                                    --Le sumamos a la existencia la cantidad reservada anterior para tener la existencia real
                                    total_existencia := total_existencia + cant_reservada_anterior;
                                END IF;
                                
                                IF total_existencia < detalle.cantidad THEN
                                    IF total_existencia <=0 THEN 
                                        --Reservar cero
                                        cant_reservar_nuevo = 0;
                                    ELSE
                                        --Tomar la existencia para reservar
                                        cant_reservar_nuevo := total_existencia;
                                    END IF;
                                    
                                    generar_requisicion := FALSE;
                                ELSE
                                    --Reservar toda la cantidad del  pedido
                                    cant_reservar_nuevo := detalle.cantidad;
                                    generar_requisicion := FALSE;
                                END IF;
                            end if;
                        END IF;
                        
                        --Redondear la nueva cantidad a reservar
                        cant_reservar_nuevo := round(cant_reservar_nuevo::numeric, noDecUnidad)::double precision;
                        
                        --Reservar cantidad para el  pedido
                        UPDATE inv_exi
                        SET   reservado = (reservado::double precision + cant_reservar_nuevo::double precision)
                        WHERE inv_prod_id = detalle.inv_prod_id
                          AND inv_alm_id  = _almacen_id
                          AND ano         = ano_actual;
                        
                        ------inicia reservar existencias en presentaciones--------------------------
                        --verificar si hay que validar existencias de Presentaciones
                        IF controlExisPres = TRUE THEN 
                            --Verificar si hay que validar las existencias de presentaciones desde el Pedido.
                            --TRUE = Validar presentaciones desde el Pedido
                            --FALSE = No validar presentaciones desde el Pedido
                            IF facpar.validar_pres_pedido = TRUE THEN 
                                --buscar la equivalencia de la Presentacion
                                SELECT cantidad
                                FROM   inv_prod_presentaciones
                                WHERE  id = detalle.presentacion_id 
                                INTO   equivalenciaPres;
                                
                                IF equivalenciaPres IS NULL THEN equivalenciaPres := 0; END IF;
                                
                                --si es diferente de cero estamos en editar
                                IF detalle.id > 0 THEN 
                                    cantPresReservAnterior := cant_reservada_anterior::double precision / equivalenciaPres::double precision;

                                    --redondear la cantidad de Presentaciones Reservada anteriormente
                                    cantPresReservAnterior := round(cantPresReservAnterior::numeric, noDecUnidad)::double precision; 
                                    
                                    --Quitar la Cantidad Reservada anteriormente
                                    UPDATE inv_exi_pres
                                    SET    reservado = (reservado::double precision - cantPresReservAnterior::double precision)
                                    WHERE  inv_alm_id               = _almacen_id
                                      AND  inv_prod_id              = detalle.inv_prod_id
                                      AND  inv_prod_presentacion_id = detalle.presentacion_id;
                                END IF;
                                
                                
                                --convertir a su equivalencia en Presentacion, la cantidad de la partida actual del pedido
                                cantPresAsignado := cant_reservar_nuevo::double precision / equivalenciaPres::double precision;
                                
                                --redondear la cantidad de Presentaciones Asignado en la partida
                                cantPresAsignado := round(cantPresAsignado::numeric, noDecUnidad)::double precision; 
                                
                                --Reservar existencia en inv_exi_pres
                                UPDATE inv_exi_pres
                                SET    reservado = (reservado::double precision + cantPresAsignado::double precision)
                                WHERE  inv_alm_id               = _almacen_id
                                  AND  inv_prod_id              = detalle.inv_prod_id
                                  AND  inv_prod_presentacion_id = detalle.presentacion_id;
                                
                            END IF;
                        END IF;
                        ------termina reservar existencias de Presentaciones------------------------------------
                    ELSE
                        generar_backorder := FALSE;
                        cant_reservar_nuevo = 0;
                    END IF;--termina if tipo_prod!=4
                    
                    --Dividir entre 100 la tasa del IEPS
                    IF detalle.valor_ieps>0 THEN 
                        detalle.valor_ieps := detalle.valor_ieps/100;
                    END IF;

                    --Tasa retencion
                    IF detalle.tasa_ret>0 THEN 
                        detalle.tasa_ret := detalle.tasa_ret/100;
                    END IF;

                    --requiere_aut = detalle.requiere_aut, autorizado = detalle.autorizado, precio_aut = detalle.precio_aut, gral_usr_id_aut = detalle.gral_usr_id_aut 
                    --requiere_aut, autorizado, precio_aut, gral_usr_id_aut
                    --detalle.requiere_aut, detalle.autorizado, detalle.precio_aut, detalle.gral_usr_id_aut 
                    
                    -- detalle.id = 0 Es registro Nuevo
                    -- detalle.id > 0 El registro ya existe, solo hay que actualizar
                    IF detalle.id = 0 THEN
                        --Crea registro nuevo en tabla poc_pedidos_detalle
                        INSERT INTO poc_pedidos_detalle(
                            poc_pedido_id,
                            inv_prod_id,
                            presentacion_id,
                            gral_imp_id,
                            cantidad,
                            precio_unitario,
                            valor_imp,
                            reservado,
                            backorder,
                            inv_prod_unidad_id,
                            gral_ieps_id,
                            valor_ieps,
                            descto,
                            requisicion,
                            requiere_aut,
                            autorizado,
                            precio_aut,
                            gral_usr_id_aut,
                            gral_imptos_ret_id,
                            tasa_ret
                        ) VALUES (
                            _pedido_id,
                            detalle.inv_prod_id,
                            detalle.presentacion_id,
                            detalle.gral_imp_id,
                            cantUnidadVenta::double precision,
                            detalle.precio_unitario,
                            detalle.valor_imp,
                            cant_reservar_nuevo,
                            generar_backorder,
                            detalle.inv_prod_unidad_id,
                            detalle.gral_ieps_id,
                            detalle.valor_ieps,
                            detalle.descto,
                            generar_requisicion,
                            detalle.requiere_aut,
                            detalle.autorizado,
                            detalle.precio_aut,
                            detalle.gral_usr_id_aut,
                            detalle.gral_imptos_ret_id,
                            detalle.tasa_ret
                        );
                    ELSE
                        --Actualiza registro
                        UPDATE poc_pedidos_detalle
                        SET poc_pedido_id       = _pedido_id,
                            inv_prod_id         = detalle.inv_prod_id,
                            presentacion_id     = detalle.presentacion_id,
                            gral_imp_id         = detalle.gral_imp_id,
                            cantidad            = cantUnidadVenta::double precision,
                            precio_unitario     = detalle.precio_unitario,
                            valor_imp           = detalle.valor_imp,
                            reservado           = cant_reservar_nuevo,
                            backorder           = generar_backorder,
                            inv_prod_unidad_id  = detalle.inv_prod_unidad_id,
                            valor_ieps          = detalle.valor_ieps,
                            descto              = detalle.descto,
                            requisicion         = generar_requisicion,
                            requiere_aut        = detalle.requiere_aut,
                            autorizado          = detalle.autorizado,
                            precio_aut          = detalle.precio_aut,
                            gral_usr_id_aut     = detalle.gral_usr_id_aut,
                            gral_imptos_ret_id  = detalle.gral_imptos_ret_id,
                            tasa_ret            = detalle.tasa_ret
                        WHERE id            = detalle.id
                          AND poc_pedido_id = _pedido_id;
                    END IF;
                    
                    --Calcular el Importe de la partida y redondealo a 4 digitos
                    importe_partida := round((cantUnidadVenta::double precision * detalle.precio_unitario)::numeric, 4)::double precision;
                    
                    --Calcula el IEPS de la partida y redondear a 4 digitos
                    importe_ieps_partida := round((importe_partida::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                    
                    --Calcula el IVA de la Partida
                    impuesto_partida := (importe_partida::double precision + importe_ieps_partida::double precision) * detalle.valor_imp;

                    --Calcular el importe de la retencion de la partida si existe la tasa de retencion
                    if detalle.tasa_ret>0 then 
                        retencion_partida := round((importe_partida::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                    end if;
                    
                    IF _descto_allowed THEN
                        IF detalle.descto>0 THEN
                            importe_del_descto_partida = round((importe_partida * (detalle.descto/100))::numeric, 4)::double precision;
                            
                            importe_partida_con_descto = round((importe_partida - importe_del_descto_partida)::numeric, 4)::double precision;
                            
                            --Recalcular el IEPS de la partida tomando el importe_partida_con_descto
                            importe_ieps_partida := round((importe_partida_con_descto::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                            
                            --Recalcular el IVA de la Partida tomando el importe_partida_con_descto
                            impuesto_partida := (importe_partida_con_descto::double precision + importe_ieps_partida::double precision) * detalle.valor_imp;

                            --Reclacular el nuevo el importe de la retencion de la partida si existe la tasa de retencion
                            if detalle.tasa_ret>0 then 
                                retencion_partida := round((importe_partida_con_descto::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                            end if;
                        END IF;
                    END IF;
                    
                    suma_descuento              := suma_descuento + importe_del_descto_partida::double precision;
                    suma_subtotal_con_descuento := suma_subtotal_con_descuento + importe_partida_con_descto::double precision;
                    
                    monto_subtotal             := monto_subtotal + importe_partida::double precision;
                    suma_ieps                  := suma_ieps + importe_ieps_partida::double precision; 
                    monto_impuesto             := monto_impuesto + impuesto_partida::double precision;
                    suma_retencion_de_partidas := suma_retencion_de_partidas + retencion_partida::double precision;

                ELSE                    
                    --Extraer datos del registro eliminado
                    sql_select := 'SELECT * FROM poc_pedidos_detalle WHERE id = ' || detalle.id || '::integer AND poc_pedido_id = ' || _pedido_id;
                    
                    --Regresar existencias reservadas
                    FOR fila IN EXECUTE (sql_select) LOOP
                        UPDATE inv_exi
                        SET    reservado = (reservado::double precision - fila.reservado::double precision)
                        WHERE  inv_prod_id = fila.inv_prod_id
                          AND  inv_alm_id  = _almacen_id
                          AND  ano         = ano_actual;
                    END LOOP;
                    
                    --Elimina registro que se elimino en el grid del navegador
                    DELETE FROM poc_pedidos_detalle
                    WHERE id            = detalle.id
                      AND poc_pedido_id = _pedido_id;
                    
                    --Eliminar el registro de la tabla que relaciona la Cotizacion con el Pedido
                    DELETE FROM poc_ped_cot
                    WHERE poc_ped_det_id = detalle.id
                      AND poc_ped_id     = _pedido_id;
                END IF;
            END LOOP;
            
            --Verificar si hay que retener iva para este cliente
            SELECT empresa_immex,
                   (CASE
                       WHEN tasa_ret_immex IS NULL THEN 0
                       ELSE tasa_ret_immex/100
                    END)
            FROM   cxc_clie
            WHERE  id = _cliente_id
            INTO   retener_iva,
                   tasa_retencion;
            
            --RAISE EXCEPTION '%','desct: ' || _descto_allowed || '        suma_descuento:' || suma_descuento;
            IF _descto_allowed AND suma_descuento>0 THEN
                IF retener_iva = TRUE THEN
                    total_retencion := suma_subtotal_con_descuento::double precision * tasa_retencion;
                ELSE 
                    total_retencion := 0;
                END IF;
                
                if suma_retencion_de_partidas > 0 then 
                    total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
                end if;
                
                ---RAISE EXCEPTION '%','suma_subtotal_con_descuento:' || suma_subtotal_con_descuento || '        suma_ieps:' || suma_ieps || '        monto_impuesto:' || monto_impuesto;
                --Calcula el monto del pedido
                monto_total := suma_subtotal_con_descuento::double precision + suma_ieps::double precision + monto_impuesto::double precision - total_retencion::double precision;
                
                --Actualiza campos subtotal, impuesto, retencion, total de tabla poc_pedidos
                UPDATE poc_pedidos
                SET subtotal        = suma_subtotal_con_descuento,
                    monto_descto    = suma_descuento,
                    monto_ieps      = suma_ieps,
                    impuesto        = monto_impuesto,
                    monto_retencion = total_retencion,
                    total           = monto_total
                WHERE id = _pedido_id;
            ELSE 
                IF retener_iva = TRUE THEN
                    total_retencion := monto_subtotal * tasa_retencion;
                ELSE
                    total_retencion := 0;
                END IF;
                
                if suma_retencion_de_partidas > 0 then 
                    total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
                end if;
                
                --Calcula el monto Total del pedido
                monto_total := monto_subtotal::double precision + suma_ieps::double precision + monto_impuesto::double precision - total_retencion::double precision;
                
                --Actualiza campos subtotal, impuesto, retencion, total de tabla poc_pedidos
                UPDATE poc_pedidos
                SET subtotal        = monto_subtotal,
                    monto_ieps      = suma_ieps,
                    impuesto        = monto_impuesto,
                    monto_retencion = total_retencion,
                    total           = monto_total
                WHERE id = _pedido_id;
            END IF;
            
            valor_retorno := '1';
        ELSE
            IF id_proceso_flujo = 2 THEN 
                valor_retorno := 'El pedido no pudo ser Actualizado, ya fue autorizado. Se encuentra en proceso de Facturacion.';
            END IF;
            
            IF id_proceso_flujo = 3 THEN 
                valor_retorno := 'El pedido no pudo ser Actualizado, ya fue Facturado.';
            END IF;
        END IF;
    END IF; -- termina edit pedido

    RETURN valor_retorno;

END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION pedido_cancel( _pedido_id integer, _usuario_id integer )
RETURNS character varying
AS $$

DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name:     Cancelación de Pedido de ventas                                >>
    -- >> Version:  MAZINGER                                                       >>
    -- >> Date:     13/Ene/2021                                                    >>
    -- >>                                                                          >>
    -- >> Si el pedido ya fue autorizado o facturado entonces no podrá cancelarse  >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    valor_retorno character varying := '0';
    id_proceso integer := 0;
    id_proceso_flujo integer := 0;
    warehouse_id integer := 0;
    sql_select character varying := '';
    cant_pres_reserv_anterior double precision := 0;
    equivalencia_pres double precision := 0;
    no_dec_unidad integer := 0;
    tipo_prod integer := 0;
    espacio_tiempo_ejecucion timestamp with time zone = now();
    ano_actual integer := 0;
    emp_id integer := 0;
    suc_id integer := 0;
    var_control_exis_pres boolean := FALSE;
    fila record;
    facpar record;

BEGIN
    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;

    SELECT gral_suc.empresa_id,
           gral_usr_suc.gral_suc_id
    FROM   gral_usr_suc
    JOIN   gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
    WHERE  gral_usr_suc.gral_usr_id = _usuario_id
    INTO   emp_id,
           suc_id;

    -- Obtener parametros para la facturacion
    SELECT *
    FROM   fac_par
    WHERE  gral_suc_id = suc_id
    INTO   facpar;

    -- query para verificar si la Empresa actual tiene control de Existencias por Presentacion
    SELECT control_exis_pres
    FROM   gral_emp
    WHERE  id = emp_id
    INTO   var_control_exis_pres;
    
    -- Obtener el id del proceso para este pedido
    SELECT proceso_id
    FROM   poc_pedidos
    WHERE  id = _pedido_id
    INTO   id_proceso;
    
    -- Obtener el id del flujo del proceso
    SELECT proceso_flujo_id
    FROM   erp_proceso
    WHERE  id = id_proceso
    INTO   id_proceso_flujo;

    IF id_proceso_flujo = 4 THEN

        UPDATE poc_pedidos
        SET cancelado               = TRUE,
            momento_cancelacion     = espacio_tiempo_ejecucion,
            gral_usr_id_cancelacion = _usuario_id
        WHERE id = _pedido_id
        RETURNING inv_alm_id
        INTO warehouse_id;
        
        --extraer datos del detalle del pedido
        sql_select := 'SELECT * FROM poc_pedidos_detalle WHERE poc_pedido_id = ' || _pedido_id;
        
        --crea devolver existencias reservadas
        FOR fila IN EXECUTE (sql_select) LOOP

            cant_pres_reserv_anterior := 0;
            no_dec_unidad             := 0;
            equivalencia_pres         := 0;
            
            --obtener el tipo de producto y el numero de Decimales Permitidos
            SELECT inv_prod.tipo_de_producto_id AS tipo_producto,
                   (CASE
                       WHEN inv_prod_unidades.id IS NULL THEN 0
                       ELSE inv_prod_unidades.decimales
                    END) AS no_dec
            FROM inv_prod
            LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id
            WHERE inv_prod.id = fila.inv_prod_id 
            INTO  tipo_prod,
                  no_dec_unidad;
            
            IF no_dec_unidad IS NULL THEN
                no_dec_unidad := 0;
            END IF;
            
            --Redondear la cantidad reservada
            fila.reservado := round(fila.reservado::numeric, no_dec_unidad)::double precision;
            
            --Quitar reservado de la tabla inv_exi
            UPDATE inv_exi
            SET    reservado   = (reservado::double precision - fila.reservado::double precision)
            WHERE  inv_prod_id = fila.inv_prod_id
              AND  inv_alm_id  = warehouse_id
              AND  ano         = ano_actual;
            
            ------Inicia quitar existencias reservadas en inv_exi_pres--------------------------
            --Verificar si la configuracion indica que se esta controlando existencias por presentaciones
            IF var_control_exis_pres = TRUE THEN 
                --Verificar si hay que validar las existencias de presentaciones desde el Pedido.
                --TRUE = Validar presentaciones desde el Pedido
                --FALSE = No validar presentaciones desde el Pedido
                IF facpar.validar_pres_pedido = TRUE THEN 
                    --buscar la equivalencia de la Presentacion
                    SELECT cantidad
                    FROM   inv_prod_presentaciones
                    WHERE  id = fila.presentacion_id::integer 
                    INTO   equivalencia_pres;
                    
                    IF equivalencia_pres IS NULL THEN
                        equivalencia_pres := 0;
                    END IF;
                    
                    --convertir a Presentaciones la cantidad Reservada
                    cant_pres_reserv_anterior := fila.reservado::double precision / equivalencia_pres::double precision;
                    
                    --redondear la cantidad de Presentaciones Reservada anteriormente
                    cant_pres_reserv_anterior := round(cant_pres_reserv_anterior::numeric, no_dec_unidad)::double precision; 
                    
                    --Quitar la Cantidad Reservada anteriormente
                    UPDATE inv_exi_pres
                    SET    reservado = (reservado::double precision - cant_pres_reserv_anterior::double precision)
                    WHERE  inv_alm_id               = warehouse_id
                      AND  inv_prod_id              = fila.inv_prod_id::integer
                      AND  inv_prod_presentacion_id = fila.presentacion_id::integer;
                END IF;
            END IF;
            
        END LOOP;
        
        valor_retorno := '1';
    ELSE
        IF id_proceso_flujo = 2 THEN
            valor_retorno := 'El pedido ya fue Autorizado, se encuentra en Facturacion. No se puede Cancelar.';
        END IF;
        
        IF id_proceso_flujo = 3 THEN
            valor_retorno := 'El pedido ya fue Facturado. No se puede Cancelar.';
        END IF;
    END IF;

    RETURN valor_retorno;

END;
$$ LANGUAGE plpgsql;


CREATE FUNCTION pedido_auth(
    _pedido_id  integer,
    _usuario_id integer
) RETURNS character varying
AS $$

DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name:     Autorización de Pedido de ventas                               >>
    -- >> Version:  MAZINGER                                                       >>
    -- >> Date:     15/Ene/2021                                                    >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    espacio_tiempo_ejecucion timestamp with time zone = now();
    id_almacen integer                    := 0;
    obser_prefactura text                 := '';
    ultimo_id integer                     := 0;
    sql_select character varying          := '';
    emp_id integer                        := 0;
    suc_id integer                        := 0;
    tipo_prod integer                     := 0;
    --Id de la unidad de medida del producto
    idUnidadMedida integer                := 0;
    --Nombre de la unidad de medida del producto
    nombreUnidadMedida character varying  := 0;
    --Densidad del producto
    densidadProd double precision         := 0;
    --numero de decimales permitidos para la unidad
    noDecUnidad integer                   := 0;
    match_cadena boolean                  := FALSE;
    --Variable para controlar la creacion de un registro en la tabla header de requisiciones cuando la configuracion lo permita
    header_requisicion_generada boolean   := FALSE;
    id_tipo_consecutivo integer           := 0;
    prefijo_consecutivo character varying := '';
    nuevo_consecutivo bigint              := 0;
    nuevo_folio character varying         := '';
    ultimo_id2 integer                    := 0;
    cantidad_produccion double precision  := 0;
    valor_retorno character varying       := 0;
    pedido record;
    facpar record;
    fila record;

BEGIN
    --actualiza el pedido con datos del usuario que autoriza
    UPDATE poc_pedidos
       SET momento_autorizacion = espacio_tiempo_ejecucion,
           gral_usr_id_autoriza = _usuario_id
     WHERE id = _pedido_id;
    
    --extraer datos del pedido
    SELECT *
      FROM poc_pedidos
     WHERE id = _pedido_id
      INTO pedido;
    
    id_almacen := pedido.inv_alm_id;

    SELECT gral_suc.empresa_id,
           gral_usr_suc.gral_suc_id
      FROM gral_usr_suc 
      JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
     WHERE gral_usr_suc.gral_usr_id = _usuario_id
      INTO emp_id,
           suc_id;
    
    --Obtener parametros para la facturacion
    SELECT *
      FROM fac_par
     WHERE gral_suc_id = suc_id
      INTO facpar;
    
    IF pedido.cancelado = FALSE THEN
        
        --Actualiza el flujo del proceso a 2=Prefactura
        UPDATE erp_proceso
           SET proceso_flujo_id = 2
         WHERE id = pedido.proceso_id;
        
        IF pedido.lugar_entrega != '' AND pedido.lugar_entrega IS NOT NULL THEN
            obser_prefactura := 'LUGAR DE ENTREGA: ' || pedido.lugar_entrega;
        ELSE
            obser_prefactura := '';
        END IF;
        
        --si enviar_obser_fac=true, hay que enviar las observaciones del pedido a las observaciones de la prefactura
        IF pedido.enviar_obser_fac = TRUE THEN
            --verificamos que las observaciones del pedido no venga vacio
            IF pedido.observaciones != '' AND pedido.observaciones IS NOT NULL THEN

                IF obser_prefactura != '' THEN
                    --si obser_prefactura no viene vacio, le agregamos un salto de linea
                    obser_prefactura := obser_prefactura || E'\n';
                END IF;
                
                obser_prefactura := obser_prefactura || pedido.observaciones;
            END IF;

        END IF;
        
        --Crear registro en la tabla erp_prefacturas y retorna el id del registro creado
        INSERT INTO  erp_prefacturas(
            proceso_id,             --pedido.proceso_id,
            folio_pedido,           --pedido.folio,
            cliente_id,             --pedido.cxc_clie_id,
            moneda_id,              --pedido.moneda_id,
            --observaciones,        --pedido.observaciones,
            observaciones,          --obser_prefactura,
            subtotal,               --pedido.subtotal,
            impuesto,               --pedido.impuesto,
            monto_retencion,        --pedido.monto_retencion,
            total,                  --pedido.total,
            tasa_retencion_immex,   --pedido.tasa_retencion_immex,
            tipo_cambio,            --pedido.tipo_cambio,
            empleado_id,            --pedido.cxc_agen_id,
            terminos_id,            --pedido.cxp_prov_credias_id
            orden_compra,           --pedido.orden_compra,
            fac_metodos_pago_id,    --pedido.fac_metodos_pago_id,
            no_cuenta,              --pedido.no_cuenta,
            enviar_ruta,            --pedido.enviar_ruta,
            inv_alm_id,             --pedido.inv_alm_id,
            cxc_clie_df_id,         --pedido.cxc_clie_df_id,
            refacturar,             --false,
            id_usuario_creacion,    --_usuario_id,
            momento_creacion,       --espacio_tiempo_ejecucion
            monto_ieps,             --pedido.monto_ieps
            monto_descto,           --pedido.monto_descto
            motivo_descto,          --pedido.motivo_descto
            cfdi_usos_id,
            cfdi_metodo_id
        ) VALUES (
            pedido.proceso_id,
            pedido.folio,
            pedido.cxc_clie_id,
            pedido.moneda_id,
            --pedido.observaciones,
            obser_prefactura,
            pedido.subtotal,
            pedido.impuesto,
            pedido.monto_retencion,
            pedido.total,
            pedido.tasa_retencion_immex,
            pedido.tipo_cambio,
            pedido.cxc_agen_id,
            pedido.cxp_prov_credias_id,
            pedido.orden_compra,
            pedido.fac_metodos_pago_id,
            pedido.no_cuenta,
            pedido.enviar_ruta,
            pedido.inv_alm_id,
            pedido.cxc_clie_df_id,
            FALSE,
            _usuario_id,
            espacio_tiempo_ejecucion,
            pedido.monto_ieps,
            pedido.monto_descto,
            pedido.motivo_descto,
            pedido.cfdi_usos_id,
            pedido.cfdi_metodo_id
        ) RETURNING id into ultimo_id;
        
        --Extraer datos del detalle del pedido
        sql_select := 'SELECT *, 0::integer as depto_id, 0::integer as empleado_id FROM poc_pedidos_detalle WHERE poc_pedido_id = ' || _pedido_id;
        
        --RAISE EXCEPTION '%','sql_select: '||sql_select;
        
        --crea registros para tabla erp_prefacturas_detalles
        FOR fila IN EXECUTE (sql_select) LOOP
            
            INSERT INTO erp_prefacturas_detalles(
                prefacturas_id,
                producto_id,
                presentacion_id,
                tipo_impuesto_id,
                valor_imp,
                cantidad,
                precio_unitario,
                reservado,
                inv_prod_unidad_id,
                gral_ieps_id,
                valor_ieps,
                descto,
                gral_imptos_ret_id,
                tasa_ret
            ) VALUES (
                ultimo_id,
                fila.inv_prod_id,
                fila.presentacion_id,
                fila.gral_imp_id,
                fila.valor_imp,
                fila.cantidad,
                fila.precio_unitario,
                fila.reservado,
                fila.inv_prod_unidad_id,
                fila.gral_ieps_id,
                fila.valor_ieps,
                fila.descto,
                fila.gral_imptos_ret_id,
                fila.tasa_ret
            );
            
            IF facpar.cambiar_unidad_medida THEN
                --Obtener datos del Producto
                SELECT inv_prod.tipo_de_producto_id AS tipo_producto,
                       inv_prod.unidad_id,
                       inv_prod_unidades.titulo,
                       inv_prod.densidad,
                       (CASE WHEN inv_prod_unidades.id IS NULL
                            THEN 0
                            ELSE inv_prod_unidades.decimales
                        END) AS no_dec
                  FROM inv_prod LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id
                 WHERE inv_prod.id = fila.inv_prod_id
                  INTO tipo_prod,
                       idUnidadMedida,
                       nombreUnidadMedida,
                       densidadProd,
                       noDecUnidad;
                
                IF noDecUnidad IS NULL THEN
                    noDecUnidad := 0;
                END IF;
                
                IF idUnidadMedida::integer <> fila.inv_prod_unidad_id THEN

                    IF densidadProd IS NULL OR densidadProd = 0 THEN
                        densidadProd := 1;
                    END IF;
                    
                    EXECUTE 'select ''' || nombreUnidadMedida || ''' ~* ''KILO*'';'
                       INTO match_cadena;

                    IF match_cadena = TRUE THEN
                        --Convertir a kilos
                        fila.cantidad := fila.cantidad::double precision * densidadProd;
                        fila.cantidad := round(fila.cantidad::numeric, noDecUnidad)::double precision;
                    ELSE
                        EXECUTE 'select ''' || nombreUnidadMedida || ''' ~* ''LITRO*'';'
                           INTO match_cadena;

                        IF match_cadena = TRUE THEN 
                            --Convertir a Litros
                            fila.cantidad := fila.cantidad::double precision / densidadProd;
                            fila.cantidad := round(fila.cantidad::numeric, noDecUnidad)::double precision;
                        END IF;
                    END IF;
                END IF;
            END IF;

            --Aqui debe entrar cuando la partida va a Requisicion de Compra
            IF fila.backorder = FALSE AND fila.requisicion = TRUE THEN 

                IF header_requisicion_generada = FALSE THEN
                    id_tipo_consecutivo := 32;--consecutivo de Requisicion
                    
                    --Aqui entra para tomar el consecutivo de la Requisicion de la sucursal actual
                    UPDATE gral_cons
                       SET consecutivo = (
                           SELECT sbt.consecutivo + 1
                             FROM gral_cons AS sbt
                            WHERE sbt.id = gral_cons.id
                           )
                     WHERE gral_emp_id       = emp_id
                       AND gral_suc_id       = suc_id
                       AND gral_cons_tipo_id = id_tipo_consecutivo
                    RETURNING prefijo,
                              consecutivo
                         INTO prefijo_consecutivo,
                              nuevo_consecutivo;
                    
                    --Concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio
                    nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

                    --Obtener id del empleado y departamento la que pertenece el usuario
                    --select gral_empleados.id, gral_empleados.gral_depto_id from gral_usr join gral_empleados on gral_empleados.id=gral_usr.gral_empleados_id into fila.empleado_id, fila.depto_id;
                    
                    SELECT gral_empleados.id,
                           gral_empleados.gral_depto_id
                      FROM gral_empleados
                     WHERE gral_empleados.id = pedido.cxc_agen_id 
                      INTO fila.empleado_id,
                           fila.depto_id;
                    
                    IF fila.empleado_id IS NULL THEN
                        fila.empleado_id := 0;
                    END IF;

                    IF fila.depto_id IS NULL THEN
                        fila.depto_id:=0;
                    END IF;
                    
                    --Tipo 1=Requisiciones creadas manualmente, 2=Requisiciones generadas desde un pedido.
                    INSERT INTO com_oc_req(
                        folio,
                        fecha_compromiso,
                        observaciones,
                        cancelado,
                        borrado_logico,
                        gral_emp_id,
                        gral_suc_id,
                        momento_creacion,
                        gral_usr_id_creacion,
                        gral_empleado_id,
                        gral_depto_id,
                        folio_pedido,
                        tipo
                    ) VALUES (
                        nuevo_folio,
                        pedido.fecha_compromiso,
                        pedido.observaciones,
                        FALSE,
                        FALSE,
                        emp_id,
                        suc_id,
                        espacio_tiempo_ejecucion,
                        _usuario_id,
                        fila.empleado_id,
                        fila.depto_id,
                        pedido.folio,
                        2
                    ) RETURNING id INTO ultimo_id2;
                    
                    --Cambiar bandera para indicar que ya se generó el header de la tabla de requisiciones
                    header_requisicion_generada := true;
                END IF;
                
                --Aqui se calcula la cantidad que se debe enviar a la requisicion de compra
                cantidad_produccion := fila.cantidad - fila.reservado;
                
                --Genera registro en la tabla detalle de la requisicion
                INSERT INTO com_oc_req_detalle(
                    com_oc_req_id,
                    inv_prod_id,
                    presentacion_id,
                    cantidad
                ) VALUES (
                    ultimo_id2,
                    fila.inv_prod_id,
                    fila.presentacion_id,
                    cantidad_produccion
                );
                
            END IF;
            
            
            --Aqui debe entrar solo cuando la partida va a backorder de produccion
            IF fila.backorder = TRUE AND fila.requisicion = FALSE THEN 
                --Folio backorder
                id_tipo_consecutivo := 24;
                
                --aqui entra para tomar el consecutivo del pedido de la sucursal actual
                UPDATE gral_cons
                   SET consecutivo = (
                        SELECT sbt.consecutivo + 1
                          FROM gral_cons AS sbt
                         WHERE sbt.id = gral_cons.id
                       )
                 WHERE gral_emp_id       = emp_id
                   AND gral_suc_id       = suc_id
                   AND gral_cons_tipo_id = id_tipo_consecutivo
                RETURNING prefijo,
                          consecutivo
                     INTO prefijo_consecutivo,
                          nuevo_consecutivo;
                --suc_id_consecutivo
                
                --concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio del pedido
                nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
                
                cantidad_produccion := fila.cantidad - fila.reservado;
                
                INSERT INTO poc_ped_bo(
                    folio,
                    poc_ped_detalle_id,
                    inv_prod_id,
                    cantidad,
                    inv_alm_id,
                    inv_mov_tipo_id,
                    cxc_clie_id,
                    orden_compra,
                    observaciones,
                    autorizado,
                    momento_autorizacion,
                    momento_creacion,
                    gral_usr_id_autoriza,
                    gral_usr_id_creacion,
                    gral_emp_id,
                    gral_suc_id
                ) VALUES (
                    nuevo_folio,
                    fila.id,
                    fila.inv_prod_id,
                    cantidad_produccion,
                    id_almacen,
                    0,
                    pedido.cxc_clie_id,
                    pedido.orden_compra,
                    pedido.observaciones,
                    TRUE,
                    espacio_tiempo_ejecucion,
                    espacio_tiempo_ejecucion,
                    _usuario_id,
                    _usuario_id,
                    emp_id,
                    suc_id
                );
            END IF;
            
        END LOOP;
        
        valor_retorno := '1';

    ELSE
        valor_retorno := 'El pedido fue CANCELADO en un proceso anterior. No se puede Autorizar.';
    END IF;

    RETURN valor_retorno;

END;
$$ LANGUAGE plpgsql;


CREATE TYPE grid_renglon_prefactura AS (
    to_keep integer,                    --str_filas[1]  eliminado
    id integer,                         --str_filas[2]  iddetalle
    producto_id integer,                --str_filas[3]  idproducto
    presentacion_id integer,            --str_filas[4]  id_presentacion
    tipo_impuesto_id integer,           --str_filas[5]  id_impuesto
    cantidad double precision,          --str_filas[6]  cantidad
    precio_unitario double precision,   --str_filas[7]  costo
    valor_imp double precision,         --str_filas[8]  valor_impuesto
    remision_id integer,                --str_filas[9]  id_remision
    costo_promedio double precision,    --str_filas[10] costo_promedio
    inv_prod_unidad_id integer,         --str_filas[11] idUnidad
    gral_ieps_id integer,               --str_filas[12] id_ieps
    valor_ieps double precision,        --str_filas[13] tasa_ieps
    descto double precision,            --str_filas[14] vdescto
    gral_imptos_ret_id integer,         --str_filas[15] retencion_id
    tasa_ret double precision           --str_filas[16] retencion_tasa    
);

CREATE FUNCTION prefactura_edit(
    _usuario_id integer,                --str_data[3]
    _prefactura_id integer,             --str_data[4]  id_prefactura
    _cliente_id integer,                --str_data[5]  id_cliente
    _moneda_id integer,                 --str_data[6]  id_moneda
    _observaciones text,                --str_data[7]  observaciones
    _tipo_cambio double precision,      --str_data[8]  tipo_cambio_vista
    _vendedor_id integer,               --str_data[9]  id_vendedor
    _condiciones_id integer,            --str_data[10] id_condiciones
    _orden_compra character varying,    --str_data[11] orden_compra
    _refacturar boolean,                --str_data[12] refacturar
    _metodo_pago_id integer,            --str_data[13] id_metodo_pago
    _no_cuenta character varying,       --str_data[14] no_cuenta
    _tipo_documento smallint,           --str_data[15] select_tipo_documento
    _moneda_original_id integer,        --str_data[18] id_moneda_original
    _adenda1 character varying,         --str_data[20]
    _adenda2 character varying,         --str_data[21]
    _adenda3 character varying,         --str_data[22]
    _adenda4 character varying,         --str_data[23]
    _adenda5 character varying,         --str_data[24]
    _adenda6 character varying,         --str_data[25]
    _adenda7 character varying,         --str_data[26]
    _adenda8 character varying,         --str_data[27]
    _permitir_descto boolean,           --str_data[29]
    _grid_detalle grid_renglon_prefactura[]
)
RETURNS character varying
AS $$

DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name:     Prefactura                                                     >>
    -- >> Version:  MAZINGER                                                       >>
    -- >> Date:     20/Ene/2021                                                    >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    detalle grid_renglon_prefactura;
    --Total de elementos de arreglo
    total_filas integer;
    --Contador de filas o posiciones del arreglo
    cont_fila integer;
    
    valor_retorno character varying       := '';
    ultimo_id integer                     := 0;
    ultimo_id_det integer                 := 0 ;
    id_tipo_consecutivo integer           := 0;
    prefijo_consecutivo character varying := '';
    nuevo_consecutivo bigint              := 0;
    nuevo_folio character varying         := '';
    ultimo_id_proceso integer             := 0;

    tipo_de_documento integer := 0;
    fila_fac_rem_doc record;
    
    -- Prefacturas(Facturacion):
    app_selected integer        := 13;
    usuario_ejecutor integer    := 0;
    emp_id integer              := 0;
    suc_id integer              := 0;
    --sucursal de donde se tomara el consecutivo:
    suc_id_consecutivo integer  := 0;
    id_almacen integer;
    espacio_tiempo_ejecucion timestamp with time zone = now();
    ano_actual integer          := 0;
    mes_actual integer          := 0;
    prefactura_fila record;
    prefactura_detalle record;
    identificador_nuevo_movimiento integer;
    tipo_movimiento_id integer  := 0;
    exis integer                := 0;
    sql_update text;
    sql_select text;
    sql_select2 character varying     := '';
    --bandera que identifica si el producto es tipo 4, true=tipo 4, false=No es tipo4
    bandera_tipo_4 boolean;
    serie_folio_fac character varying := '';
    refact character varying          := '';
    tipo_cam double precision         := 0;
    
    numero_dias_credito integer       := 0;
    fecha_de_vencimiento timestamp with time zone;
    
    importe_del_descto_partida double precision  := 0;
    importe_partida_con_descto double precision  := 0;
    suma_descuento double precision              := 0;
    suma_subtotal_con_descuento double precision := 0;
    
    importe_partida double precision                    := 0;
    importe_ieps_partida double precision               := 0;
    impuesto_partida double precision                   := 0;
    monto_subtotal double precision                     := 0;
    suma_ieps double precision                          := 0;
    suma_total double precision                         := 0;
    monto_impuesto double precision                     := 0;
    total_retencion double precision                    := 0;
    retener_iva boolean                                 := false;
    tasa_retencion double precision                     := 0;
    retencion_partida double precision                  := 0;
    suma_retencion_de_partidas double precision         := 0;
    suma_retencion_de_partidas_globlal double precision := 0;
    
    --Estas variables se utilizan en caso de que se facture un pedido en otra moneda
    suma_descuento_global double precision              := 0;
    suma_subtotal_con_descuento_global double precision := 0;
    monto_subtotal_global double precision              := 0;
    suma_ieps_global double precision                   := 0;
    monto_impuesto_global double precision              := 0;
    total_retencion_global double precision             := 0;
    suma_total_global double precision                  := 0;
    cant_original double precision                      := 0;
    
    costo_promedio_actual double precision   := 0;
    costo_referencia_actual double precision := 0;
    
    id_osal integer                    := 0;
    nuevo_folio_osal character varying := '';
    facpar record;--parametros de Facturacion
    
    cantPresAsignado double precision       := 0;
    cantPresReservAnterior double precision := 0;
    
    --Variable que indica  si se debe controlar Existencias por Presentacion
    controlExisPres boolean;
    --Variable que indica si la cantidad de la partida ya fue facturada en su totalidad
    partida_facturada boolean;
    --Indica si hay que actualizar el flujo del proceso. El proceso se debe actualizar cuando ya no quede partidas vivas
    actualizar_proceso boolean;
    --Id del Pedido que se esta facturando
    id_pedido integer;
    --Id de la unidad de medida del producto
    idUnidadMedida integer               := 0;
    --Nombre de la unidad de medida del producto
    nombreUnidadMedida character varying := 0;
    --Densidad del producto
    densidadProd double precision        := 0;
    --Cantidad en la unidad del producto
    cantUnidadProd double precision      := 0;
    match_cadena boolean                 := false;
    
    --Numero de Adenda
    idAdenda integer                  := 0;
    moneda_iso_4217 character varying := '';
    adenda8 character varying := '';

BEGIN
    -- usuario que utiliza el aplicativo
    usuario_ejecutor := _usuario_id;
    
    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;
    SELECT EXTRACT(MONTH FROM espacio_tiempo_ejecucion) INTO mes_actual;
    
    --obtener id de empresa, sucursal
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
      FROM gral_usr_suc 
      JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
     WHERE gral_usr_suc.gral_usr_id = usuario_ejecutor
      INTO emp_id, suc_id;
    
    --Obtener parametros para la facturacion
    SELECT *
      FROM fac_par
     WHERE gral_suc_id = suc_id
      INTO facpar;
    
    --tomar el id del almacen para ventas
    id_almacen := facpar.inv_alm_id;
    
    --éste consecutivo es para el folio de Remisión y folio para BackOrder(poc_ped_bo)
    suc_id_consecutivo := facpar.gral_suc_id_consecutivo;

    --query para verificar si la Empresa actual incluye Modulo de Produccion y control de Existencias por Presentacion
    SELECT control_exis_pres
      FROM gral_emp
     WHERE id = emp_id
      INTO controlExisPres;
    
    --Inicializar en cero
    id_pedido := 0;


    --Aqui entra antes de generar Remision y Factura
    IF _prefactura_id > 0 THEN
        /*
        Aquí se actualizan los datos de la PREFACTURA, esto es antes de facturar, terminando este proceso se genera la FACTURA
        Solo se actualizan los datos del header de la prefactura, lo datos del grid se deja  como viene del pedido
        */
        --str_data[3]    id_usuario
        --str_data[4]    id_prefactura
        --str_data[5]    id_cliente
        --str_data[6]    id_moneda
        --str_data[7]    observaciones
        --str_data[8]    tipo_cambio_vista
        --str_data[9]    id_vendedor
        --str_data[10]   id_condiciones
        --str_data[11]   orden_compra
        --str_data[12]   refacturar
        --str_data[13]   id_metodo_pago
        --str_data[14]   no_cuenta
        --str_data[15]   select_tipo_documento
        --str_data[16]   folio_pedido
        --str_data[17]   select_almacen
        --str_data[18]   id_moneda_original
        
        --Actualizar tabla erp_prefacturas
        UPDATE erp_prefacturas
           SET moneda_id                = _moneda_id,
               observaciones            = _observaciones,
               tipo_cambio              = _tipo_cambio,
               empleado_id              = _vendedor_id,
               terminos_id              = _condiciones_id,
               orden_compra             = _orden_compra,
               refacturar               = _refacturar,
               fac_metodos_pago_id      = _metodo_pago_id,
               no_cuenta                = _no_cuenta,
               tipo_documento           = _tipo_documento,
               id_moneda_pedido         = _moneda_original_id,
               id_usuario_actualizacion = usuario_ejecutor,
               momento_actualizacion    = espacio_tiempo_ejecucion
         WHERE id = _prefactura_id;

        suma_descuento              := 0;
        suma_subtotal_con_descuento := 0;
        suma_retencion_de_partidas  := 0;
        
        --Verificar si la moneda del Pedido es diferente a la moneda de la Prefactura
        IF _moneda_id <> _moneda_original_id THEN
            --eliminar los registros de erp_prefacturas_detalles
            --delete from erp_prefacturas_detalles where prefacturas_id=_prefactura_id;

            --Inicializar variables
            monto_subtotal_global              := 0;
            monto_impuesto_global              := 0;
            total_retencion_global             := 0;
            suma_total_global                  := 0;
            suma_ieps_global                   := 0;
            suma_descuento_global              := 0;
            suma_subtotal_con_descuento_global := 0;
            
            --si es diferente hay que actualizar los registros de prefacturas detalles
            --Esto es para que se conserve la Moneda seleccionada al momento de Realizar la Facturacion
            total_filas := array_length(_grid_detalle, 1);--obtiene total de elementos del arreglo
            cont_fila   := 1;

            FOR cont_fila IN 1 .. total_filas LOOP

                detalle := _grid_detalle[cont_fila];

                retencion_partida := 0;
                
                --1: se conserva, 0: se elimina
                IF detalle.to_keep <> 0 THEN
                    --str_filas[2]    iddetalle
                    --str_filas[3]    idproducto
                    --str_filas[4]    id_presentacion
                    --str_filas[5]    id_impuesto
                    --str_filas[6]    cantidad
                    --str_filas[7]    costo
                    --str_filas[8]    valor_impuesto
                    --str_filas[9]    id_remision
                    --str_filas[10]    costo_promedio
                    --str_filas[11]    idUnidad
                    --str_filas[12]    id_ieps
                    --str_filas[13]    tasa_ieps
                    --str_filas[14]    vdescto
                    --str_filas[15]    retencion_id
                    --str_filas[16]    retencion_tasa
                    
                    SELECT *
                      FROM inv_obtiene_costo_promedio_actual(detalle.producto_id, espacio_tiempo_ejecucion)
                      INTO costo_promedio_actual;
                    
                    --Inicializar
                    cant_original := 0;

                    --Tasa del ieps de la partida
                    IF detalle.valor_ieps > 0 THEN 
                        detalle.valor_ieps := detalle.valor_ieps / 100;
                    END IF;
                    
                    --Tasa retencion de la partida
                    IF detalle.tasa_ret > 0 THEN 
                        detalle.tasa_ret := detalle.tasa_ret / 100;
                    END IF;
                    
                    --Actualizar los registros en erp_prefacturas_detalles
                    UPDATE erp_prefacturas_detalles
                       SET cant_facturar   = detalle.cantidad,
                           valor_imp       = detalle.valor_imp,
                           precio_unitario = detalle.precio_unitario 
                     WHERE id = detalle.id 
                    RETURNING cantidad
                         INTO cant_original;
                    
                    IF cant_original IS NULL THEN
                        cant_original := 0;
                    END IF;
                    
                    --crear registros en erp_prefacturas_detalles
                    --INSERT INTO erp_prefacturas_detalles(prefacturas_id, producto_id, presentacion_id, tipo_impuesto_id, cant_facturar, precio_unitario, valor_imp, costo_promedio)
                    --VALUES(_prefactura_id, detalle.producto_id, detalle.presentacion_id, detalle.tipo_impuesto_id, detalle.cantidad, detalle.precio_unitario, detalle.valor_imp, costo_promedio_actual);
                    
                    --Inicializar variables para rautilizar en calculo de totales de lo que se va a facturar
                    importe_partida             := 0;
                    impuesto_partida            := 0;
                    importe_ieps_partida        := 0;
                    suma_descuento              := 0;
                    suma_subtotal_con_descuento := 0;
                    
                    --Calcular y Redondear el importe de la partida
                    importe_partida := round((detalle.cantidad * detalle.precio_unitario)::numeric, 4)::double precision;
                    
                    --Calcular y redondear el IEPS de la partida
                    importe_ieps_partida := round((importe_partida::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                    
                    --Calcula el IVA de la Partida
                    impuesto_partida := round(((importe_partida::double precision + importe_ieps_partida::double precision) * detalle.valor_imp)::numeric, 4)::double precision;

                    --Calcular el importe de la retencion de la partida si existe la tasa de retencion
                    IF detalle.tasa_ret > 0 THEN 
                        retencion_partida := round((importe_partida::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                    END IF;
                    
                    IF _permitir_descto THEN
                        IF detalle.descto > 0 THEN
                            importe_del_descto_partida := round((importe_partida * (detalle.descto / 100))::numeric, 4)::double precision;

                            importe_partida_con_descto := round((importe_partida - importe_del_descto_partida)::numeric, 4)::double precision;
                            
                            --Recalcular el IEPS de la partida tomando el importe_partida_con_descto
                            importe_ieps_partida := round((importe_partida_con_descto::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                            
                            --Recalcular el IVA de la Partida tomando el importe_partida_con_descto
                            impuesto_partida := round(((importe_partida_con_descto::double precision + importe_ieps_partida::double precision) * detalle.valor_imp)::numeric, 4)::double precision;

                            --Reclacular el nuevo el importe de la retencion de la partida si existe la tasa de retencion
                            IF detalle.tasa_ret > 0 THEN 
                                retencion_partida := round((importe_partida_con_descto::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                            END IF;
                        END IF;
                    END IF;
                    
                    suma_descuento              := suma_descuento + importe_del_descto_partida::double precision;
                    suma_subtotal_con_descuento := suma_subtotal_con_descuento + importe_partida_con_descto::double precision;

                    monto_subtotal             := monto_subtotal + importe_partida::double precision;
                    suma_ieps                  := suma_ieps + importe_ieps_partida::double precision; 
                    monto_impuesto             := monto_impuesto + impuesto_partida::double precision;
                    suma_retencion_de_partidas := suma_retencion_de_partidas + retencion_partida::double precision;
                    
                    
                    --Inicializar variables para reutilizar en calculo de totales General
                    importe_partida      := 0;
                    impuesto_partida     := 0;
                    importe_ieps_partida := 0;
                    retencion_partida    := 0;
                    
                    importe_partida := round((cant_original::double precision * detalle.precio_unitario)::numeric, 4)::double precision;
                    
                    --Calcular y redondear el IEPS de la partida
                    importe_ieps_partida := round((importe_partida::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                    
                    --Calcula el IVA de la Partida
                    impuesto_partida := (importe_partida::double precision + importe_ieps_partida::double precision) * detalle.valor_imp;
                    
                    --Calcular el importe de la retencion de la partida si existe la tasa de retencion
                    IF detalle.tasa_ret > 0 THEN 
                        retencion_partida := round((importe_partida::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                    END IF;
                    
                    IF _permitir_descto THEN
                        IF detalle.descto > 0 THEN
                            importe_del_descto_partida := round((importe_partida * (detalle.descto / 100))::numeric, 4)::double precision;

                            importe_partida_con_descto := round((importe_partida - importe_del_descto_partida)::numeric, 4)::double precision;
                            
                            --Recalcular el IEPS de la partida tomando el importe_partida_con_descto
                            importe_ieps_partida := round((importe_partida_con_descto::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                            
                            --Recalcular el IVA de la Partida tomando el importe_partida_con_descto
                            impuesto_partida := round(((importe_partida_con_descto::double precision + importe_ieps_partida::double precision) * detalle.valor_imp)::numeric, 4)::double precision;

                            --Calcular el importe de la retencion de la partida si existe la tasa de retencion
                            IF detalle.tasa_ret > 0 THEN 
                                retencion_partida := round((importe_partida_con_descto::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                            END IF;
                        END IF;
                    END IF;
                    
                    suma_descuento_global              := suma_descuento_global + importe_del_descto_partida::double precision;
                    suma_subtotal_con_descuento_global := suma_subtotal_con_descuento_global + importe_partida_con_descto::double precision;
                    monto_subtotal_global              := monto_subtotal_global + importe_partida::double precision;
                    suma_ieps_global                   := suma_ieps_global + importe_ieps_partida::double precision; 
                    monto_impuesto_global              := monto_impuesto_global + impuesto_partida::double precision;
                    suma_retencion_de_partidas_globlal := suma_retencion_de_partidas_globlal + retencion_partida::double precision;
                END IF;
            END LOOP;
            
            --verificar si hay que retener iva para este cliente
            SELECT empresa_immex,
                   (CASE WHEN tasa_ret_immex IS NULL
                       THEN 0
                       ELSE tasa_ret_immex / 100
                    END)
              FROM cxc_clie
             WHERE id = _cliente_id
              INTO retener_iva,
                   tasa_retencion;

            IF _permitir_descto AND suma_descuento > 0 THEN

                IF retener_iva = true THEN 
                    total_retencion        := suma_subtotal_con_descuento * tasa_retencion;
                    total_retencion_global := monto_subtotal_global * tasa_retencion;
                ELSE
                    total_retencion        := 0;
                    total_retencion_global := 0;
                END IF;
                
                IF suma_retencion_de_partidas > 0 THEN 
                    total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
                END IF;
                
                IF suma_retencion_de_partidas_globlal > 0 THEN 
                    total_retencion_global := round((total_retencion_global + suma_retencion_de_partidas_globlal)::numeric, 4)::double precision;
                END IF;
                
                --Calcula el total de lo que se esta facturando
                suma_total := suma_subtotal_con_descuento + suma_ieps + monto_impuesto - total_retencion::double precision;
                
                --Calcula el total global de la Prefactura
                suma_total_global := suma_subtotal_con_descuento_global + suma_ieps_global + monto_impuesto_global - total_retencion_global::double precision;
                
                --Actualiza campos subtotal, monto_ieps, impuesto, retencion, total de tabla erp_prefacturas
                UPDATE erp_prefacturas
                   SET subtotal             = suma_subtotal_con_descuento_global,
                       monto_ieps           = suma_ieps_global,
                       impuesto             = monto_impuesto_global,
                       monto_retencion      = total_retencion_global,
                       total                = suma_total_global,
                       monto_descto         = suma_descuento_global,
                       fac_subtotal         = suma_subtotal_con_descuento,
                       fac_monto_ieps       = suma_ieps,
                       fac_impuesto         = monto_impuesto,
                       fac_monto_retencion  = total_retencion,
                       fac_total            = suma_total,
                       tasa_retencion_immex = tasa_retencion,
                       fac_monto_descto     = suma_descuento 
                 WHERE id = _prefactura_id;
            ELSE
                IF retener_iva = true THEN
                    total_retencion        := monto_subtotal * tasa_retencion;
                    total_retencion_global := monto_subtotal_global * tasa_retencion;
                ELSE
                    total_retencion        := 0;
                    total_retencion_global := 0;
                END IF;
                
                IF suma_retencion_de_partidas > 0 THEN 
                    total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
                END IF;
                
                IF suma_retencion_de_partidas_globlal > 0 THEN 
                    total_retencion_global := round((total_retencion_global + suma_retencion_de_partidas_globlal)::numeric, 4)::double precision;
                END IF;
                
                --Calcula el total de lo que se esta facturando
                suma_total := monto_subtotal + suma_ieps + monto_impuesto - total_retencion::double precision;
                
                --Calcula el total global de la Prefactura
                suma_total_global := monto_subtotal_global + suma_ieps_global + monto_impuesto_global - total_retencion_global::double precision;
                
                --Actualiza campos subtotal, monto_ieps, impuesto, retencion, total de tabla erp_prefacturas
                UPDATE erp_prefacturas
                   SET subtotal             = monto_subtotal_global,
                       monto_ieps           = suma_ieps_global,
                       monto_descto         = 0,
                       impuesto             = monto_impuesto_global,
                       monto_retencion      = total_retencion_global,
                       total                = suma_total_global,
                       fac_subtotal         = monto_subtotal,
                       fac_monto_ieps       = suma_ieps,
                       fac_impuesto         = monto_impuesto,
                       fac_monto_retencion  = total_retencion,
                       fac_total            = suma_total,
                       tasa_retencion_immex = tasa_retencion,
                       fac_monto_descto     = 0 
                 WHERE id = _prefactura_id;
            END IF;
            
        ELSE
            suma_retencion_de_partidas         := 0;
            suma_retencion_de_partidas_globlal := 0;
            
            --Si la moneda de la prefactura es igual a la Moneda del pedido, entonces solo debemos actualizar las cantidades a facturar
            total_filas := array_length(_grid_detalle, 1);--obtiene total de elementos del arreglo
            cont_fila   := 1;

            FOR cont_fila IN 1 .. total_filas LOOP

                detalle := _grid_detalle[cont_fila];
                
                --1: se conserva, 0: se elimina
                IF detalle.to_keep <> 0 THEN
                    --str_filas[2]    iddetalle
                    --str_filas[3]    idproducto
                    --str_filas[4]    id_presentacion
                    --str_filas[5]    id_impuesto
                    --str_filas[6]    cantidad
                    --str_filas[7]    costo
                    --str_filas[8]    valor_impuesto
                    --str_filas[9]    id_remision
                    --str_filas[10]    costo_promedio
                    
                    --str_filas[12]    id_ieps
                    --str_filas[13]    tasa_ieps
                    --str_filas[14]    vdescto
                    --str_filas[15]    retencion_id
                    --str_filas[16]    retencion_tasa

                    importe_partida            := 0;
                    importe_ieps_partida       := 0;
                    impuesto_partida           := 0;
                    importe_del_descto_partida := 0;
                    importe_partida_con_descto := 0;
                    retencion_partida          := 0;

                    --Tasa de IEPS
                    IF detalle.valor_ieps > 0 THEN 
                        detalle.valor_ieps := detalle.valor_ieps / 100;
                    END IF;

                    --Tasa retencion de IVA de la partida
                    IF detalle.tasa_ret > 0 THEN 
                        detalle.tasa_ret := detalle.tasa_ret / 100;
                    END IF;
                    
                    SELECT *
                      FROM inv_obtiene_costo_promedio_actual(detalle.producto_id, espacio_tiempo_ejecucion)
                      INTO costo_promedio_actual;
                    
                    --Actualizar los registros en erp_prefacturas_detalles
                    UPDATE erp_prefacturas_detalles
                       SET cant_facturar = detalle.cantidad
                     WHERE id = detalle.id;
                    
                    --Calcular y Redondear el importe de la partida
                    importe_partida := round((detalle.cantidad * detalle.precio_unitario)::numeric, 4)::double precision;
                    
                    --Calcular y redondear el IEPS de la partida
                    importe_ieps_partida := round((importe_partida::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                    
                    --Calcula el IVA de la Partida
                    impuesto_partida := (importe_partida::double precision + importe_ieps_partida::double precision) * detalle.valor_imp;
                    
                    --Calcular el importe de la retencion de la partida si existe la tasa de retencion
                    IF detalle.tasa_ret > 0 THEN 
                        retencion_partida := round((importe_partida::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                    END IF;
                    
                    IF _permitir_descto THEN
                        IF detalle.descto > 0 THEN
                            importe_del_descto_partida := round((importe_partida * (detalle.descto / 100))::numeric, 4)::double precision;

                            importe_partida_con_descto := round((importe_partida - importe_del_descto_partida)::numeric, 4)::double precision;
                            
                            --Recalcular el IEPS de la partida tomando el importe_partida_con_descto
                            importe_ieps_partida := round((importe_partida_con_descto::double precision * detalle.valor_ieps)::numeric, 4)::double precision;
                            
                            --Recalcular el IVA de la Partida tomando el importe_partida_con_descto
                            impuesto_partida := round(((importe_partida_con_descto::double precision + importe_ieps_partida::double precision) * detalle.valor_imp)::numeric, 4)::double precision;

                            --Reclacular el nuevo el importe de la retencion de la partida si existe la tasa de retencion
                            IF detalle.tasa_ret > 0 THEN 
                                retencion_partida := round((importe_partida_con_descto::double precision * detalle.tasa_ret)::numeric, 4)::double precision;
                            END IF;
                        END IF;
                    END IF;

                    suma_descuento              := suma_descuento + importe_del_descto_partida::double precision;
                    suma_subtotal_con_descuento := suma_subtotal_con_descuento + importe_partida_con_descto::double precision;
                    monto_subtotal              := monto_subtotal + importe_partida::double precision;
                    suma_ieps                   := suma_ieps + importe_ieps_partida::double precision; 
                    monto_impuesto              := monto_impuesto + impuesto_partida::double precision;
                    suma_retencion_de_partidas  := suma_retencion_de_partidas + retencion_partida::double precision;
                END IF;
            END LOOP;
            
            --verificar si hay que retener iva para este cliente
            SELECT empresa_immex,
                   (CASE WHEN tasa_ret_immex IS NULL
                       THEN 0
                       ELSE tasa_ret_immex / 100
                    END)
              FROM cxc_clie
             WHERE id = _cliente_id
              INTO retener_iva,
                   tasa_retencion;

            IF _permitir_descto AND suma_descuento > 0 THEN
                IF retener_iva = true THEN
                    total_retencion := suma_subtotal_con_descuento * tasa_retencion;
                ELSE
                    total_retencion := 0;
                END IF;

                IF suma_retencion_de_partidas > 0 THEN 
                    total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
                END IF;
                
                --Calcula el monto de la prefactura
                suma_total := suma_subtotal_con_descuento::double precision + suma_ieps::double precision + monto_impuesto::double precision - total_retencion::double precision;
                
                --Actualiza campos subtotal, impuesto, retencion, total de tabla erp_prefacturas
                UPDATE erp_prefacturas
                   SET fac_subtotal         = suma_subtotal_con_descuento,
                       fac_monto_ieps       = suma_ieps,
                       fac_impuesto         = monto_impuesto,
                       fac_monto_retencion  = total_retencion,
                       fac_total            = suma_total,
                       tasa_retencion_immex = tasa_retencion,
                       fac_monto_descto     = suma_descuento  
                 WHERE id = _prefactura_id;
            ELSE
                IF retener_iva = true THEN
                    total_retencion := monto_subtotal * tasa_retencion;
                ELSE
                    total_retencion := 0;
                END IF;

                IF suma_retencion_de_partidas > 0 THEN 
                    total_retencion := round((total_retencion + suma_retencion_de_partidas)::numeric, 4)::double precision;
                END IF;
                
                --Calcula el monto de la prefactura
                suma_total := monto_subtotal::double precision + suma_ieps::double precision + monto_impuesto::double precision - total_retencion::double precision;
                
                --Actualiza campos subtotal, impuesto, retencion, total de tabla erp_prefacturas
                UPDATE erp_prefacturas
                   SET fac_subtotal         = monto_subtotal,
                       fac_monto_ieps       = suma_ieps,
                       fac_impuesto         = monto_impuesto,
                       fac_monto_retencion  = total_retencion,
                       fac_total            = suma_total,
                       tasa_retencion_immex = tasa_retencion
                 WHERE id = _prefactura_id;
            END IF;

            

            
            
            /*
            Verificar si se está llevando control de existencias por Presentaciones. 
            Si no se lleva control de presentaciones, por ningun motivo podrá ser cambiada la presentacion, por lo tanto no es necesario actualizar
            */
            IF controlExisPres = true THEN

                IF facpar.validar_pres_pedido = false THEN 
                    total_filas := array_length(_grid_detalle, 1);--obtiene total de elementos del arreglo
                    cont_fila := 1;

                    FOR cont_fila IN 1 .. total_filas LOOP

                        detalle := _grid_detalle[cont_fila];
                        
                        --1: se conserva, 0: se elimina
                        IF detalle.to_keep <> 0 THEN

                            --Aquí se actualiza la presentación porque puede que haya cambiado antes de facturar o remisionar
                            UPDATE erp_prefacturas_detalles
                               SET presentacion_id = detalle.presentacion_id 
                             WHERE id = detalle.id;

                        END IF;

                    END LOOP;

                END IF;

            END IF;

        END IF;
        
        
        
        --RAISE EXCEPTION '%','facpar.incluye_adenda: '||facpar.incluye_adenda;
        
        --:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        --Inicia gaurdar datos para la Adenda
        --Primero se verifica si en los parametros indica que se debe incluir la Adenda
        IF facpar.incluye_adenda THEN
            --Verificar que exista un id de cliente valido
            IF _cliente_id > 1 THEN
                --Buscar el numero de Adenda asignado al cliente.
                SELECT cxc_clie_tipo_adenda_id
                  FROM cxc_clie
                 WHERE id = _cliente_id
                  INTO idAdenda;
                
                --Varificar si tiene adenda asignada
                IF idAdenda > 0 THEN 
                    --Verificar el numero de adenda
                    IF idAdenda = 1 THEN 
                        --Si el numero de Adenda es 1, entonces solo debe se debe validar datos cuando el tipo de documento es igual a 3.
                        --Tipo Documento 3=Factura de Remision
                        IF _tipo_documento = 3 THEN 
                            --RAISE EXCEPTION '%','str_data[27]: '||str_data[27];
                            
                            --Buscar la codificacion de la moneda por si el usuario la cambio al momento de actualizar
                            SELECT iso_4217_anterior::character varying
                              FROM gral_mon
                             WHERE id = _moneda_id
                              INTO moneda_iso_4217;

                            adenda8 := moneda_iso_4217;
                            
                            --Verificar si ya hay un registro de la Adenda y que no este ligado a una factura, es decir no ha sido facturado
                            IF (SELECT count(id)
                                  FROM fac_docs_adenda
                                 WHERE prefactura_id = _prefactura_id
                                   AND fac_docs_id = 0) > 0 THEN 

                                --Actualizar datos de la adenda porque ya existe un registro
                                UPDATE fac_docs_adenda
                                   SET valor1 = _adenda1,
                                       valor2 = _adenda2,
                                       valor3 = _adenda3,
                                       valor4 = _adenda4,
                                       valor5 = _adenda5,
                                       valor6 = _adenda6,
                                       valor7 = _adenda7,
                                       valor8 = adenda8 
                                 WHERE prefactura_id = _prefactura_id
                                   AND fac_docs_id   = 0;
                            ELSE
                                --Crear el registro porque no existe
                                INSERT INTO fac_docs_adenda(
                                    prefactura_id,
                                    fac_docs_id,
                                    cxc_clie_adenda_tipo_id,
                                    valor1,
                                    valor2,
                                    valor3,
                                    valor4,
                                    valor5,
                                    valor6,
                                    valor7,
                                    valor8
                                ) VALUES (
                                    _prefactura_id,
                                    0,
                                    idAdenda,
                                    _adenda1,
                                    _adenda2,
                                    _adenda3,
                                    _adenda4,
                                    _adenda5,
                                    _adenda6,
                                    _adenda7,
                                    adenda8
                                );
                            END IF;
                        END IF;
                    END IF;
                    --Termina Addenda FEMSA-QUIMIPRODUCTOS
                    
                    
                    --Addenda SUN CHEMICAL
                    IF idAdenda = 2 THEN
                        
                        --Tipo Documento 1=Factura, 3=Factura de Remision
                        IF _tipo_documento = 1 OR _tipo_documento = 3 THEN 
                            --Verificar si ya hay un registro de la Adenda y que no este ligado a una factura, es decir no ha sido facturado
                            IF (SELECT count(id)
                                  FROM fac_docs_adenda
                                 WHERE prefactura_id = _prefactura_id
                                   AND fac_docs_id = 0) > 0 THEN 

                                --Actualizar datos de la adenda porque ya existe un registro
                                UPDATE fac_docs_adenda
                                   SET valor1 = _orden_compra 
                                 WHERE prefactura_id = _prefactura_id
                                   AND fac_docs_id   = 0;
                            ELSE
                                --Crear el registro porque no existe
                                INSERT INTO fac_docs_adenda(
                                    prefactura_id,
                                    fac_docs_id,
                                    cxc_clie_adenda_tipo_id,
                                    valor1
                                ) VALUES (
                                    _prefactura_id,
                                    0,
                                    idAdenda,
                                    _orden_compra
                                );
                            END IF;
                        END IF;
                    END IF;
                    --Termina Addenda SUN CHEMICAL
                END IF;
            END IF;
        END IF;
        --Termina Guardar datos Adenda
        --:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        
        
        
        
        
        --RAISE EXCEPTION '%','tipo_documento: '||str_data[15];
        --::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        --:::::: AQUI ENTRA PARA GENERAR UNA REMISION:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        --::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        IF _tipo_documento = 2 THEN

            id_tipo_consecutivo := 10;--Folio Remision de Clientes
            
            --aqui entra para tomar el consecutivo del folio de la remision de la sucursal actual
            UPDATE gral_cons
               SET consecutivo = (
                        SELECT sbt.consecutivo + 1
                          FROM gral_cons AS sbt
                         WHERE sbt.id = gral_cons.id
                   )
             WHERE gral_emp_id       = emp_id
               AND gral_suc_id       = suc_id
               AND gral_cons_tipo_id = id_tipo_consecutivo
            RETURNING prefijo,
                      consecutivo
                 INTO prefijo_consecutivo,
                      nuevo_consecutivo;
            --suc_id_consecutivo
            
            --concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio del pedido
            nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
            
            --extraer datos de la Prefactura
            SELECT *
              FROM erp_prefacturas
             WHERE id = _prefactura_id
              INTO prefactura_fila;
            
            IF prefactura_fila.moneda_id = 1 THEN
                --IF prefactura_fila.moneda_id != prefactura_fila.id_moneda_pedido THEN
                --    tipo_cam:=prefactura_fila.tipo_cambio;
                --ELSE
                tipo_cam := 1;
                --END IF;
            ELSE
                tipo_cam := prefactura_fila.tipo_cambio;
            END IF;
            
            INSERT INTO fac_rems(
                folio,                  --nuevo_folio,
                folio_pedido,           --prefactura_fila.folio_pedido,
                cxc_clie_id,            --prefactura_fila.cliente_id,
                moneda_id,              --prefactura_fila.moneda_id,
                subtotal,               --prefactura_fila.fac_subtotal,
                monto_ieps,             --prefactura_fila.fac_monto_ieps,
                impuesto,               --prefactura_fila.fac_impuesto,
                monto_retencion,        --prefactura_fila.fac_monto_retencion,
                total,                  --prefactura_fila.fac_total,
                tasa_retencion_immex,   --prefactura_fila.tasa_retencion_immex,
                tipo_cambio,            --tipo_cam,
                fac_metodos_pago_id,    --prefactura_fila.fac_metodos_pago_id,
                no_cuenta,              --prefactura_fila.no_cuenta,
                proceso_id,             --prefactura_fila.proceso_id,
                cxc_agen_id,            --prefactura_fila.empleado_id,
                cxc_clie_credias_id,    --prefactura_fila.terminos_id,
                orden_compra,           --prefactura_fila.orden_compra,
                observaciones,          --prefactura_fila.observaciones,
                inv_alm_id,             --prefactura_fila.inv_alm_id,
                cxc_clie_df_id,         --prefactura_fila.cxc_clie_df_id,
                momento_creacion,       --espacio_tiempo_ejecucion,
                gral_usr_id_creacion,   --usuario_ejecutor
                monto_descto,           --prefactura_fila.fac_monto_descto 
                motivo_descto           --prefactura_fila.motivo_descto 
            ) VALUES (
                nuevo_folio,
                prefactura_fila.folio_pedido,
                prefactura_fila.cliente_id,
                prefactura_fila.moneda_id,
                prefactura_fila.fac_subtotal,
                prefactura_fila.fac_monto_ieps,
                prefactura_fila.fac_impuesto,
                prefactura_fila.fac_monto_retencion,
                prefactura_fila.fac_total,
                prefactura_fila.tasa_retencion_immex,
                tipo_cam,
                prefactura_fila.fac_metodos_pago_id,
                prefactura_fila.no_cuenta,
                prefactura_fila.proceso_id,
                prefactura_fila.empleado_id,
                prefactura_fila.terminos_id,
                prefactura_fila.orden_compra,
                prefactura_fila.observaciones,
                prefactura_fila.inv_alm_id,
                prefactura_fila.cxc_clie_df_id,
                espacio_tiempo_ejecucion,
                usuario_ejecutor,
                prefactura_fila.fac_monto_descto,
                prefactura_fila.motivo_descto
            )
            RETURNING id INTO ultimo_id;
            
            tipo_movimiento_id  := 5;--Salida por Venta
            id_tipo_consecutivo := 21; --Folio Orden de Salida
            id_almacen          := prefactura_fila.inv_alm_id;
            
            --aqui entra para tomar el consecutivo del folio  la sucursal actual
            UPDATE gral_cons
               SET consecutivo = (
                      SELECT sbt.consecutivo + 1
                        FROM gral_cons AS sbt
                       WHERE sbt.id = gral_cons.id
                   )
             WHERE gral_emp_id       = emp_id
               AND gral_suc_id       = suc_id
               AND gral_cons_tipo_id = id_tipo_consecutivo
            RETURNING prefijo,
                      consecutivo
                 INTO prefijo_consecutivo,
                      nuevo_consecutivo;
            --suc_id_consecutivo
            
            --Concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
            nuevo_folio_osal := prefijo_consecutivo || nuevo_consecutivo::character varying;
            
            --Crea registro en tabla inv_osal(Orden de Salida)
            INSERT INTO inv_osal(
                folio,
                estatus,
                erp_proceso_id,
                inv_mov_tipo_id,
                tipo_documento,
                folio_documento,
                fecha_exp,
                gral_app_id,
                cxc_clie_id,
                inv_alm_id,
                subtotal,
                monto_iva,
                monto_retencion,
                monto_total,
                folio_pedido,
                orden_compra,
                moneda_id,
                tipo_cambio,
                momento_creacion,
                gral_usr_id_creacion,
                gral_emp_id,
                gral_suc_id,
                monto_ieps
            ) VALUES (
                nuevo_folio_osal,
                0,
                prefactura_fila.proceso_id,
                tipo_movimiento_id,
                _tipo_documento,
                nuevo_folio,
                espacio_tiempo_ejecucion,
                app_selected,
                prefactura_fila.cliente_id,
                id_almacen,
                prefactura_fila.subtotal,
                prefactura_fila.impuesto,
                prefactura_fila.monto_retencion,
                prefactura_fila.total,
                prefactura_fila.folio_pedido,
                prefactura_fila.orden_compra,
                prefactura_fila.moneda_id,
                tipo_cam,
                espacio_tiempo_ejecucion,
                usuario_ejecutor,
                emp_id,
                suc_id,
                prefactura_fila.monto_ieps
            )
            RETURNING id INTO id_osal;
            
            --Crea registro del movimiento
            INSERT INTO inv_mov(
                observacion,
                momento_creacion,
                gral_usr_id,
                gral_app_id,
                inv_mov_tipo_id,
                referencia,
                fecha_mov
            ) VALUES (
                prefactura_fila.observaciones,
                espacio_tiempo_ejecucion,
                usuario_ejecutor,
                app_selected,
                tipo_movimiento_id,
                nuevo_folio,
                espacio_tiempo_ejecucion
            )
            RETURNING id INTO identificador_nuevo_movimiento;
            
            --bandera que identifica si el producto es tipo 4
            --si es tipo 4 no debe existir movimientos en inventario
            bandera_tipo_4 := TRUE;

            --Bandera que indica si se debe actualizar el flujo del proceso.
            --El proceso solo debe actualizarse cuando no quede ni una sola partida viva
            actualizar_proceso := true;
            
            --Extraer datos de erp_prefacturas_detalles
            sql_select := '
            SELECT  erp_prefacturas_detalles.id AS id_det,
                    erp_prefacturas_detalles.producto_id,
                    erp_prefacturas_detalles.presentacion_id,
                    erp_prefacturas_detalles.cantidad AS cant_pedido,
                    erp_prefacturas_detalles.cant_facturado,
                    erp_prefacturas_detalles.cant_facturar AS cantidad,
                    erp_prefacturas_detalles.tipo_impuesto_id,
                    erp_prefacturas_detalles.valor_imp,
                    erp_prefacturas_detalles.precio_unitario,
                    inv_prod.tipo_de_producto_id AS tipo_producto,
                    erp_prefacturas_detalles.costo_promedio,
                    erp_prefacturas_detalles.reservado,
                    erp_prefacturas_detalles.reservado AS nuevo_reservado,
                    0::double precision AS descontar_reservado,
                    (CASE WHEN inv_prod_presentaciones.id IS NULL
                        THEN 0
                        ELSE inv_prod_presentaciones.cantidad
                        END) AS cant_equiv,
                    (CASE WHEN inv_prod_unidades.id IS NULL
                        THEN 0
                        ELSE inv_prod_unidades.decimales
                        END) AS no_dec,
                    inv_prod.unidad_id AS id_uni_prod,
                    inv_prod.densidad AS densidad_prod,
                    inv_prod_unidades.titulo AS nombre_unidad,
                    erp_prefacturas_detalles.inv_prod_unidad_id,
                    erp_prefacturas_detalles.gral_ieps_id, 
                    erp_prefacturas_detalles.valor_ieps,
                    (CASE WHEN erp_prefacturas_detalles.descto IS NULL
                        THEN 0
                        ELSE erp_prefacturas_detalles.descto
                        END) AS descto,
                    erp_prefacturas_detalles.gral_imptos_ret_id, 
                    erp_prefacturas_detalles.tasa_ret 
              FROM erp_prefacturas_detalles 
              JOIN inv_prod                     ON inv_prod.id                = erp_prefacturas_detalles.producto_id
              LEFT JOIN inv_prod_unidades       ON inv_prod_unidades.id       = inv_prod.unidad_id
              LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id = erp_prefacturas_detalles.presentacion_id 
             WHERE erp_prefacturas_detalles.cant_facturar  > 0 
               AND erp_prefacturas_detalles.prefacturas_id = ' || _prefactura_id || ';';
            
            FOR prefactura_detalle IN EXECUTE (sql_select) LOOP
                --Inicializar valores
                cantPresReservAnterior := 0;
                cantPresAsignado       := 0;
                partida_facturada      := false;
                cantUnidadProd         := 0;
                
                idUnidadMedida     := prefactura_detalle.id_uni_prod;
                densidadProd       := prefactura_detalle.densidad_prod;
                nombreUnidadMedida := prefactura_detalle.nombre_unidad;
                
                IF densidadProd IS NULL OR densidadProd = 0 THEN
                    densidadProd := 1;
                END IF;
                
                cantUnidadProd := prefactura_detalle.cantidad::double precision;
                
                IF facpar.cambiar_unidad_medida THEN
                    IF idUnidadMedida::integer <> prefactura_detalle.inv_prod_unidad_id THEN

                        EXECUTE 'SELECT ''' || nombreUnidadMedida || ''' ~* ''KILO*'';'
                           INTO match_cadena;
                        
                        IF match_cadena = true THEN
                            --Convertir a kilos
                            cantUnidadProd := cantUnidadProd::double precision * densidadProd;
                        ELSE
                            EXECUTE 'SELECT ''' || nombreUnidadMedida || ''' ~* ''LITRO*'';'
                               INTO match_cadena;
                            
                            IF match_cadena = true THEN 
                                --Convertir a Litros
                                cantUnidadProd := cantUnidadProd::double precision / densidadProd;
                            END IF;
                        END IF;
                    END IF;
                END IF;
                
                prefactura_detalle.cant_pedido     := round(prefactura_detalle.cant_pedido::numeric,prefactura_detalle.no_dec)::double precision;
                prefactura_detalle.cant_facturado  := round(prefactura_detalle.cant_facturado::numeric,prefactura_detalle.no_dec)::double precision;
                prefactura_detalle.cantidad        := round(prefactura_detalle.cantidad::numeric,prefactura_detalle.no_dec)::double precision;
                prefactura_detalle.reservado       := round(prefactura_detalle.reservado::numeric,prefactura_detalle.no_dec)::double precision;
                prefactura_detalle.nuevo_reservado := round(prefactura_detalle.nuevo_reservado::numeric,prefactura_detalle.no_dec)::double precision;
                cantUnidadProd := round(cantUnidadProd::numeric, prefactura_detalle.no_dec)::double precision;
                
                IF (cantUnidadProd::double precision <= prefactura_detalle.reservado::double precision) THEN 
                    --Asignar la cantidad para descontar de reservado
                    prefactura_detalle.reservado := cantUnidadProd::double precision;
                END IF;
                
                --Calcular la nueva cantidad reservada
                prefactura_detalle.nuevo_reservado := prefactura_detalle.nuevo_reservado::double precision - prefactura_detalle.reservado::double precision;
                
                --Redondaer la nueva cantidad reservada
                prefactura_detalle.nuevo_reservado := round(prefactura_detalle.nuevo_reservado::numeric, prefactura_detalle.no_dec)::double precision;
                
                --Obtener costo promedio actual del producto
                SELECT *
                    FROM inv_obtiene_costo_promedio_actual(prefactura_detalle.producto_id, espacio_tiempo_ejecucion)
                    INTO costo_promedio_actual;
                
                --Verificar que no tenga valor null
                IF costo_promedio_actual IS NULL OR costo_promedio_actual <= 0 THEN
                    costo_promedio_actual := 0;
                END IF;
                
                --Obtener el costo ultimo actual del producto. Este costo es convertido a pesos
                sql_select2 := 'SELECT (CASE WHEN gral_mon_id_' || mes_actual || ' = 1 
                                            THEN costo_ultimo_' || mes_actual || ' 
                                            ELSE costo_ultimo_' || mes_actual || ' *
                                                (CASE WHEN gral_mon_id_' || mes_actual || ' = 1 
                                                    THEN 1 
                                                    ELSE tipo_cambio_' || mes_actual || ' 
                                                 END)
                                        END) AS costo_ultimo
                                  FROM inv_prod_cost_prom 
                                 WHERE inv_prod_id = ' || prefactura_detalle.producto_id || ' 
                                   AND ano         = ' || ano_actual || ';';

                EXECUTE sql_select2 INTO costo_referencia_actual;
                --RAISE EXCEPTION '%',cadena_sql;
                
                --Verificar que no tenga valor null
                IF costo_referencia_actual IS NULL OR costo_referencia_actual <= 0 THEN
                    costo_referencia_actual := 0;
                END IF;
                
                --Crea registros para tabla fac_rems_detalles
                INSERT INTO fac_rems_detalles(
                    fac_rems_id,
                    inv_prod_id,
                    inv_prod_presentacion_id,
                    gral_imp_id,
                    valor_imp,
                    cantidad,
                    precio_unitario,
                    costo_promedio,
                    costo_referencia,
                    inv_prod_unidad_id,
                    gral_ieps_id,
                    valor_ieps,
                    descto,
                    gral_imptos_ret_id,
                    tasa_ret
                ) VALUES (
                    ultimo_id,
                    prefactura_detalle.producto_id,
                    prefactura_detalle.presentacion_id,
                    prefactura_detalle.tipo_impuesto_id,
                    prefactura_detalle.valor_imp,
                    prefactura_detalle.cantidad,
                    prefactura_detalle.precio_unitario,
                    costo_promedio_actual,
                    costo_referencia_actual,
                    prefactura_detalle.inv_prod_unidad_id,
                    prefactura_detalle.gral_ieps_id,
                    prefactura_detalle.valor_ieps,
                    prefactura_detalle.descto,
                    prefactura_detalle.gral_imptos_ret_id,
                    prefactura_detalle.tasa_ret
                );
                
                --RAISE EXCEPTION '%','prefactura_detalle.tipo_producto: '||prefactura_detalle.tipo_producto;
                --Si el tipo de producto es diferente de 4, hay que descontar existencias y generar Movimientos
                --tipo=4 Servicios
                --para el tipo servicios NO debe generar movimientos NI descontar existencias
                IF prefactura_detalle.tipo_producto::integer <> 4 THEN

                    --indica que por lo menos un producto es diferente de tipo4, por lo tanto debe generarse movimientos
                    bandera_tipo_4 := FALSE;
                    
                    --tipo=1 Normal o Terminado
                    --tipo=2 Subensable o Formulacion o Intermedio
                    --tipo=5 Refacciones
                    --tipo=6 Accesorios
                    --tipo=7 Materia Prima
                    --tipo=8 Prod. en Desarrollo
                    IF  prefactura_detalle.tipo_producto = 1 OR
                        prefactura_detalle.tipo_producto = 2 OR
                        prefactura_detalle.tipo_producto = 5 OR
                        prefactura_detalle.tipo_producto = 6 OR
                        prefactura_detalle.tipo_producto = 7 OR
                        prefactura_detalle.tipo_producto = 8 THEN
                        --genera registro en detalles del movimiento
                        INSERT INTO inv_mov_detalle(
                            producto_id,
                            alm_origen_id,
                            alm_destino_id,
                            cantidad,
                            inv_mov_id,
                            costo,
                            inv_prod_presentacion_id
                        ) VALUES (
                            prefactura_detalle.producto_id,
                            id_almacen,
                            0,
                            cantUnidadProd,
                            identificador_nuevo_movimiento,
                            costo_promedio_actual,
                            prefactura_detalle.presentacion_id
                        );
                        
                        --query para descontar producto de existencias y descontar existencia reservada porque ya se Remisionó
                        sql_update := 'UPDATE inv_exi
                                          SET salidas_'        || mes_actual || ' = (salidas_' || mes_actual || ' + ' || cantUnidadProd || '), 
                                              reservado                           = (reservado::double precision - ' || prefactura_detalle.reservado || '::double precision),
                                              momento_salida_' || mes_actual || ' = ''' || espacio_tiempo_ejecucion || '''
                                        WHERE inv_alm_id  = ' || id_almacen                     || '::integer 
                                          AND inv_prod_id = ' || prefactura_detalle.producto_id || '::integer
                                          AND ano         = ' || ano_actual                     || '::integer;';

                        EXECUTE sql_update;
                        
                        --Crear registro en orden salida detalle, se crea el registo con la cantidad en unidad de venta
                        INSERT INTO inv_osal_detalle(
                            inv_osal_id,
                            inv_prod_id,
                            inv_prod_presentacion_id,
                            cantidad,
                            precio_unitario,
                            inv_prod_unidad_id,
                            gral_ieps_id,
                            valor_ieps
                        ) VALUES (
                            id_osal,
                            prefactura_detalle.producto_id,
                            prefactura_detalle.presentacion_id,
                            prefactura_detalle.cantidad,
                            prefactura_detalle.precio_unitario,
                            prefactura_detalle.inv_prod_unidad_id,
                            prefactura_detalle.gral_ieps_id,
                            prefactura_detalle.valor_ieps
                        );
                        
                        --Verificar si se está llevando el control de existencias por Presentaciones
                        IF controlExisPres = true THEN 
                            --Si la configuracion indica que se validan Presentaciones desde el Pedido,entonces significa que hay reservados, por lo tanto hay que descontarlos
                            IF facpar.validar_pres_pedido = true THEN 
                                --Convertir la cantidad reservada a su equivalente en presentaciones
                                cantPresReservAnterior := prefactura_detalle.reservado::double precision / prefactura_detalle.cant_equiv::double precision;
                                
                                --redondear la Cantidad de la Presentacion reservada Anteriormente
                                cantPresReservAnterior := round(cantPresReservAnterior::numeric, prefactura_detalle.no_dec)::double precision; 
                            END IF;
                            
                            --Convertir la cantidad de la partida a su equivalente a presentaciones
                            cantPresAsignado := cantUnidadProd::double precision / prefactura_detalle.cant_equiv::double precision;
                            
                            --Redondear la cantidad de Presentaciones asignado en la partida
                            cantPresAsignado := round(cantPresAsignado::numeric, prefactura_detalle.no_dec)::double precision;
                            
                            --Sumar salidas de inv_exi_pres
                            UPDATE inv_exi_pres
                               SET salidas                   = (salidas::double precision + cantPresAsignado::double precision), 
                                   reservado                 = (reservado::double precision - cantPresReservAnterior::double precision), 
                                   momento_actualizacion     = espacio_tiempo_ejecucion, 
                                   gral_usr_id_actualizacion = usuario_ejecutor 
                             WHERE inv_alm_id               = prefactura_fila.inv_alm_id 
                               AND inv_prod_id              = prefactura_detalle.producto_id 
                               AND inv_prod_presentacion_id = prefactura_detalle.presentacion_id;
                            --Termina sumar salidas
                        END IF;
                        
                        --::Aqui inica calculos para el control de facturacion por partida::::::::
                        --Calcular la cantidad facturada
                        prefactura_detalle.cant_facturado := prefactura_detalle.cant_facturado::double precision + prefactura_detalle.cantidad::double precision;
                        
                        --Redondear la cantidad facturada
                        prefactura_detalle.cant_facturado := round(prefactura_detalle.cant_facturado::numeric, prefactura_detalle.no_dec)::double precision;
                        
                        IF prefactura_detalle.cant_pedido <= prefactura_detalle.cant_facturado THEN 
                            partida_facturada  := true;
                        ELSE
                            --Si entro aqui quiere decir que por lo menos una partida esta quedando pendiente de facturar por completo.
                            actualizar_proceso := false;
                        END IF;
                        
                        --Actualizar el registro de la partida
                        UPDATE erp_prefacturas_detalles
                           SET cant_facturado = prefactura_detalle.cant_facturado,
                               facturado      = partida_facturada,
                               cant_facturar  = 0,
                               reservado      = prefactura_detalle.nuevo_reservado 
                         WHERE id = prefactura_detalle.id_det;
                        
                        
                        --Obtener el id del pedido que se esta facturando
                        SELECT id
                          FROM poc_pedidos
                         WHERE folio = prefactura_fila.folio_pedido
                         ORDER BY id DESC
                         LIMIT 1
                          INTO id_pedido;
                        
                        IF id_pedido IS NULL THEN
                            id_pedido := 0;
                        END IF;
                        
                        IF id_pedido <> 0 THEN 
                            --Actualizar el registro detalle del Pedido
                            UPDATE poc_pedidos_detalle
                               SET reservado = prefactura_detalle.nuevo_reservado 
                             WHERE poc_pedido_id   = id_pedido
                               AND inv_prod_id     = prefactura_detalle.producto_id
                               AND presentacion_id = prefactura_detalle.presentacion_id;
                        END IF;
                        
                    END IF;--termina tipo producto 1, 2, 7
                ELSE
                    
                    IF prefactura_detalle.tipo_producto::integer = 4 THEN
                        --Aquí solo entre cuando es un Servicio
                        --::Aqui inica calculos para el control de remision por partida::::::::
                        --Calcular la cantidad facturada
                        prefactura_detalle.cant_facturado := prefactura_detalle.cant_facturado::double precision + prefactura_detalle.cantidad::double precision;
                        
                        --Redondear la cantidad facturada
                        prefactura_detalle.cant_facturado := round(prefactura_detalle.cant_facturado::numeric, prefactura_detalle.no_dec)::double precision;
                        
                        IF prefactura_detalle.cant_pedido <= prefactura_detalle.cant_facturado THEN 
                            partida_facturada := true;
                        END IF;
                        
                        --Actualizar el registro de la partida
                        UPDATE erp_prefacturas_detalles
                           SET cant_facturado = prefactura_detalle.cant_facturado,
                               facturado      = partida_facturada,
                               cant_facturar  = 0,
                               reservado      = 0 
                         WHERE id = prefactura_detalle.id_det;
                    END IF;

                END IF;
                
            END LOOP;
            
            --si bandera tipo 4=true, significa el producto que se esta facturando son servicios;
            --por lo tanto hay que eliminar el movimiento de inventario
            IF bandera_tipo_4 = TRUE THEN 
                DELETE FROM inv_mov
                 WHERE id = identificador_nuevo_movimiento;
            END IF;
            
            IF (SELECT count(prefact_det.id)
                  FROM erp_prefacturas_detalles AS prefact_det
                  JOIN inv_prod ON inv_prod.id = prefact_det.producto_id
                 WHERE prefact_det.prefacturas_id      = _prefactura_id
                   AND inv_prod.tipo_de_producto_id   <> 4
                   AND prefact_det.facturado = false) >= 1 THEN

                actualizar_proceso := false;
            END IF;
            
            --Verificar si hay que actualizar el flujo del proceso
            IF actualizar_proceso THEN 
                --Actualiza el flujo del proceso a 5=Remision
                UPDATE erp_proceso
                   SET proceso_flujo_id = 5
                 WHERE id = prefactura_fila.proceso_id;
            ELSE
                --Actualiza el flujo del proceso a 8=REMISION PARCIAL
                UPDATE erp_proceso
                   SET proceso_flujo_id = 8
                 WHERE id = prefactura_fila.proceso_id;
            END IF;
            
            --Una vez terminado el Proceso se asignan ceros a estos campos
            UPDATE erp_prefacturas
               SET fac_subtotal        = 0,
                   fac_monto_ieps      = 0,
                   fac_impuesto        = 0,
                   fac_monto_retencion = 0,
                   fac_total           = 0,
                   fac_monto_descto    = 0
             WHERE id = _prefactura_id;
            
        END IF;
        --::::::TERMINA GENERACION DE REMISION:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        
        valor_retorno := '1:' || nuevo_folio;

    END IF; --termina edit prefactura

    RETURN valor_retorno;

END;
$$ LANGUAGE plpgsql;


CREATE TYPE grid_renglon_pago AS (
    serie_folio character varying,
    saldado boolean,
    cantidad double precision,
    tipo_cambio double precision
);

CREATE FUNCTION public.pago_register(
    _usuario_id integer,                            --str_data[3]
    _cliente_id integer,                            --str_data[4]
    _moneda integer,                                --str_data[7]
    _fecha_hora timestamp with time zone,           --str_data[8]
    _banco integer,                                 --str_data[9]
    _observaciones text,                            --str_data[10]
    _forma_pago character varying,                  --str_data[11]
    _cheque character varying,                      --str_data[12]
    _referencia character varying,                  --str_data[13]
    _tarjeta character varying,                     --str_data[14]
    _monto_pago double precision,                   --str_data[16]
    _fecha_deposito timestamp with time zone,       --str_data[17]
    _ficha_movimiento_deposito character varying,   --str_data[18]
    _ficha_cuenta_deposito integer,                 --str_data[19]
    _ficha_banco_kemikal integer,                   --str_data[20]
    _tipo_cambio double precision,                  --str_data[21]
    _anticipo_gastado double precision,             --str_data[22]
    _no_transaccion_anticipo bigint,                --str_data[23]
    _saldo_a_favor double precision,                --str_data[24]
    _grid_detalle grid_renglon_pago[]

) RETURNS character varying LANGUAGE plpgsql AS $$

DECLARE

    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    -- >> Name:     Registro de pagos                                              >>
    -- >> Version:  MAZINGER                                                       >>
    -- >> Date:     11/feb/2021                                                    >>
    -- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    --Numero de transaccion pago CXC
    id_tipo_consecutivo integer := 11;
    prefijo_consecutivo character varying := '';
    nuevo_consecutivo bigint := 0;
    folio_transaccion bigint := 0;
    id_forma_pago integer := 0;
    id_anticipo integer := 0;
    monto_anticipo_actual double precision := 0;
    rowCount integer := 0;
    ultimo_id integer := 0;
    grid_rows integer := 0;
    detalle grid_renglon_pago;
    id_moneda_factura integer := 0;
    sql_pagos text := '';
    fila record;
    total_factura double precision := 0;
    monto_pagos double precision := 0;
    suma_pagos double precision := 0;
    suma_notas_credito double precision := 0;
    nuevacantidad_monto_pago double precision := 0;
    nuevo_saldo_factura double precision := 0;
    saldo_anticipo double precision := 0;
    emp_id integer := 0;
    suc_id integer := 0;
    valor_retorno character varying := '';

BEGIN

    --obtiene empresa_id, sucursal_id y sucursal_id
    SELECT gral_suc.empresa_id,
           gral_usr_suc.gral_suc_id
      FROM gral_usr_suc
      JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
      JOIN inv_suc_alm ON inv_suc_alm.sucursal_id = gral_suc.id
     WHERE gral_usr_suc.gral_usr_id = _usuario_id
      INTO emp_id,
           suc_id;

    --aqui entra para tomar el consecutivo del folio  la sucursal actual
    UPDATE gral_cons
       SET consecutivo = (
              SELECT sbt.consecutivo + 1
                FROM gral_cons AS sbt
               WHERE sbt.id = gral_cons.id
           )
     WHERE gral_emp_id = emp_id
       AND gral_suc_id = suc_id
       AND gral_cons_tipo_id = id_tipo_consecutivo
 RETURNING prefijo,
           consecutivo
      INTO prefijo_consecutivo,
           nuevo_consecutivo;

    --concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio
    --nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

    --nuevo folio transaccion
    folio_transaccion := nuevo_consecutivo::bigint;

    --obtiene id de la forma de pago
    SELECT id
      FROM erp_pagos_formas
     WHERE titulo ILIKE _forma_pago
     LIMIT 1
      INTO id_forma_pago;

    id_anticipo := 0;
    monto_anticipo_actual := 0;
    --obtener id del anticipo
    SELECT COUNT(id)
      FROM cxc_ant
     WHERE numero_transaccion = _no_transaccion_anticipo
       AND cliente_id = _cliente_id
       AND borrado_logico = false
      INTO rowCount;

    IF rowCount > 0 THEN
        SELECT id,
               anticipo_actual
          FROM cxc_ant
         WHERE numero_transaccion = _no_transaccion_anticipo
           AND cliente_id = _cliente_id
         LIMIT 1
          INTO id_anticipo,
               monto_anticipo_actual;
    END IF;

    INSERT INTO erp_pagos (
        numero_transaccion,
        momento_creacion,
        forma_pago_id,
        numero_cheque,
        referencia,
        numero_tarjeta,
        observaciones,
        id_usuario_pago,
        banco_id,
        moneda_id,
        monto_pago,
        cliente_id,
        fecha_deposito,
        numerocuenta_id,
        movimiento,
        bancokemikal_id,
        tipo_cambio,
        anticipo_id,
        empresa_id,
        sucursal_id
    ) VALUES (
        folio_transaccion,
        now(),
        id_forma_pago,
        _cheque,
        _referencia,
        _tarjeta,
        _observaciones,
        _usuario_id,
        _banco,
        _moneda,
        _monto_pago,
        _cliente_id,
        _fecha_deposito,
        _ficha_cuenta_deposito,
        _ficha_movimiento_deposito,
        _ficha_banco_kemikal,
        _tipo_cambio,
        id_anticipo,
        emp_id,
        suc_id
    ) RETURNING id INTO ultimo_id;

    --str_data[3]     id_usuario
    --str_data[4]     cliente_id
    --str_data[5]     deuda_pesos
    --str_data[6]     deuda_usd
    --str_data[7]     moneda
    --str_data[8]     fecha+" "+hora+":"+minutos+":"+segundos
    --str_data[9]     banco
    --str_data[10]    observaciones
    --str_data[11]    forma_pago
    --str_data[12]    cheque
    --str_data[13]    referencia
    --str_data[14]    tarjeta
    --str_data[15]    anticipo
    --str_data[16]    monto_pago
    --str_data[17]    fecha_deposito
    --str_data[18]    ficha_movimiento_deposito
    --str_data[19]    ficha_cuenta_deposito
    --str_data[20]    ficha_banco_kemikal
    --str_data[21]    tipo_cambio
    --str_data[22]    anticipo_gastado
    --str_data[23]    no_transaccion_anticipo
    --str_data[24]    saldo_a_favor

    --RAISE EXCEPTION '%','Si llega aqui: id_pago: '||ultimo_id_pago;
    --------------------saldando facturas--------------------------------
    --SELECT INTO item string_to_array(''||valores||'','&');

    SELECT array_length(_grid_detalle, 1)
      INTO grid_rows;

    FOR idx IN 1 .. grid_rows LOOP

        detalle := _grid_detalle[idx];
        --RAISE EXCEPTION '%', iterar[1];
        --iterar[1]    factura_vista
        --iterar[2]    saldado
        --iterar[3]    saldo
        --iterar[4]    tipocambio(este tipo de cambio no se utiliza, el tipo de cambio esta en pagos)

        SELECT moneda_id
          FROM fac_docs
         WHERE serie_folio = detalle.serie_folio
          INTO id_moneda_factura;

        INSERT INTO erp_pagos_detalles (
            pago_id,
            serie_folio,
            cantidad,
            momento_pago,
            fac_moneda_id
        ) VALUES (
            ultimo_id,
            detalle.serie_folio,
            detalle.cantidad,
            _fecha_hora,
            id_moneda_factura
        );

        IF detalle.saldado = true THEN
            UPDATE erp_h_facturas
               SET pagado = true
             WHERE serie_folio = detalle.serie_folio;

            UPDATE fac_cfds
               SET pagado = true
             WHERE serie_folio ilike detalle.serie_folio;
            --UPDATE erp_notacargos SET pagado=true WHERE serie_folio ilike detalle.serie_folio;
        END IF;

    END LOOP;

    sql_pagos := 'SELECT serie_folio,
                         cantidad
                    FROM erp_pagos_detalles
                   WHERE pago_id = ' || ultimo_id;
    --RAISE EXCEPTION '%','Si llega aqui: sql-pagos: '||string_pagos;

    FOR fila IN EXECUTE(sql_pagos) LOOP

        EXECUTE 'SELECT monto_total,
                        total_pagos
                   FROM erp_h_facturas
                  WHERE serie_folio ilike ''' || fila.serie_folio || ''''
           INTO total_factura,
                monto_pagos;

        --sacar suma total de pagos para esta factura
        SELECT CASE WHEN sum IS NULL
                   THEN 0
                   ELSE sum
               END
          FROM (SELECT sum(cantidad)
                  FROM erp_pagos_detalles
                 WHERE serie_folio = fila.serie_folio
                   AND cancelacion = FALSE) AS sbt
          INTO suma_pagos;

        --sacar suma total de notas de credito para esta factura
        --SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END FROM (SELECT sum(total) FROM fac_nota_credito WHERE serie_folio_factura=fila.serie_folio AND cancelado=FALSE) AS subtabla INTO suma_notas_credito;
        SELECT total_notas_creditos
          FROM erp_h_facturas
         WHERE serie_folio = fila.serie_folio
          INTO suma_notas_credito;

        nuevacantidad_monto_pago := round((suma_pagos)::numeric, 4)::double precision;
        nuevo_saldo_factura := round((total_factura - suma_pagos - suma_notas_credito)::numeric, 4)::double precision;

        --actualiza cantidades cada vez que se realice un pago
        UPDATE erp_h_facturas
           SET total_pagos = nuevacantidad_monto_pago,
               total_notas_creditos = suma_notas_credito,
               saldo_factura = nuevo_saldo_factura,
               momento_actualizacion = now(),
               fecha_ultimo_pago = _fecha_deposito::date
         WHERE serie_folio = fila.serie_folio;

    END LOOP;

    --Inicia guardar saldos a favor y actualizar anticipos
    IF id_anticipo <> 0 THEN
        --aqui entra porque el pago es de un anticipo y actualiza cantidades del anticipo
        IF monto_anticipo_actual >= _anticipo_gastado THEN

            saldo_anticipo := monto_anticipo_actual - _anticipo_gastado;
            IF saldo_anticipo <= 0 THEN
                UPDATE cxc_ant
                   SET anticipo_actual = 0,
                       borrado_logico = true,
                       id_usuario_actualizacion = _usuario_id,
                       momento_baja = now()
                 WHERE cliente_id = _cliente_id
                   AND id = id_anticipo;
            ELSE
                UPDATE cxc_ant
                   SET anticipo_actual = saldo_anticipo,
                       id_usuario_baja = _usuario_id,
                       momento_actualizacion = now()
                 WHERE cliente_id = _cliente_id
                   AND id = id_anticipo;
            END IF;

        END IF;
    ELSE
        --Aqui entra porque el pago no es de un anticipo
        IF _saldo_a_favor > 0 THEN
            /*
            INSERT INTO erp_pagosxaplicar(
                cliente_id,
                moneda_id,
                monto_inicial,
                monto_actual,
                momento_creacion,
                id_usuario_creacion,
                empresa_id,
                sucursal_id)
            VALUES(
                _cliente_id,
                _moneda,
                _saldo_a_favor,
                _saldo_a_favor,
                now(),_usuario_id,
                emp_id,
                suc_id
            );
            */
            --Aquí se genera una nuevo numero de transaccion para el anticipo des saldo a favor
            --aqui entra para tomar el consecutivo del folio  la sucursal actual
            UPDATE gral_cons
               SET consecutivo = (
                     SELECT sbt.consecutivo + 1
                       FROM gral_cons AS sbt
                      WHERE sbt.id = gral_cons.id
                   )
             WHERE gral_emp_id = emp_id
               AND gral_suc_id = suc_id
               AND gral_cons_tipo_id = id_tipo_consecutivo
         RETURNING prefijo,
                   consecutivo
              INTO prefijo_consecutivo,
                   nuevo_consecutivo;
                                --nuevo folio transaccion
            folio_transaccion := nuevo_consecutivo::bigint;

            INSERT INTO cxc_ant(
                numero_transaccion,     --folio_transaccion,
                cliente_id,             --str_data[4]::integer,
                moneda_id,              --_moneda,
                anticipo_inicial,       --_saldo_a_favor,
                anticipo_actual,        --_saldo_a_favor,
                fecha_anticipo_usuario, --now(),
                observaciones,          --'ANTICIPO GENERADO DESDE UN PAGO COMO SALDO A FAVOR DEL CLIENTE'
                momento_creacion,       --now(),
                id_usuario_creacion,    --_usuario_id,
                empresa_id,             --emp_id,
                sucursal_id             --suc_id
            ) VALUES (
                folio_transaccion,
                _cliente_id,
                _moneda,
                _saldo_a_favor,
                _saldo_a_favor,
                now(),
                'ANTICIPO GENERADO DESDE UN PAGO COMO SALDO A FAVOR DEL CLIENTE',
                now(),
                _usuario_id,
                emp_id,
                suc_id
            );
        END IF;

    END IF;

    valor_retorno := folio_transaccion::character varying || '___' || ultimo_id::character varying;
    RETURN valor_retorno;

END;
$$;


CREATE TYPE grid_renglon_traspaso AS (
    inv_prod_id integer,
    cantidad_tras double precision,
    inv_prod_presentacion_id integer
);

CREATE OR REPLACE FUNCTION inv_crear_traspaso(
    _usuario_id integer,    --str_data[3]
    _suc_orig_id integer,   --str_data[5]    select_suc_origen
    _alm_orig_id integer,   --str_data[6]    select_alm_origen
    _suc_dest_id integer,   --str_data[7]    select_suc_destino
    _alm_dest_id integer,   --str_data[8]    select_alm_destino
    _observaciones text,    --str_data[9]    observaciones
    _fecha_traspaso date,   --str_data[10]   fecha_traspaso
    _grid_detalle grid_renglon_traspaso[]
) RETURNS character varying
  LANGUAGE 'plpgsql' AS $$

--###################################
--# Wrtten by: Edwin Plauchu        #
--# mailto: pianodaemon@gmail.com   #
--# 12 / marzo / 2012               #
--###################################

-- #### Premisas de ejecucion de este procedimiento almacenado:
-- * Cuando se genera un Traspaso se genera una SALIDA en el almacen origen
-- y una ENTRADA en el almacen destino
-- * Cuando se realiza un ajuste se generara ya sea una salida (Si la cantidad de existencia disminuye)
-- o una ENTRADA (Si la cantidad de existencia aumenta)... sobre el almacen.
-- * El campo existencia de la tabla de existencias de inventario se actualizara cada que se actualice
-- la tabla de movimientos de inventario

DECLARE
    app_selected integer := 10;
    emp_id integer := 0;
    suc_id integer := 0;

    valor_retorno character varying := '';
    ultimo_id integer := 0;
    ultimo_id2 integer := 0;

    espacio_tiempo_ejecucion timestamp with time zone := now();
    identificador_nuevo_movimiento integer := 0;
    identificador_nuevo_movimiento_entrada integer := 0;
    tipo_movimiento_id integer := 0;

    exis integer := 0;
    ano_actual integer := 0;
    mes_actual integer := 0;

    sql_insert character varying := '';
    sql_update character varying := '';
    sql_select text := '';

    precio_unitario  double precision := 0.0;

    --Variables para generar consecutivos
    id_tipo_consecutivo integer := 0;
    prefijo_consecutivo character varying := '';
    nuevo_consecutivo bigint := 0;

    --Traspasos
    --numero de decimales permitidos para la unidad
    noDecUnidad integer := 0;
    --equivalencia de la presentacion en la unidad del producto
    equivalenciaPres double precision := 0.0;
    --Cantidad que se esta Intentando traspasar
    cantPres double precision := 0.0;

    folio1 character varying := '';
    folio2 character varying := '';

    --Variable que indica si se debe controlar las existencias por presentaciones
    controlExisPres boolean := false;

    affected_rows integer := 0;
    detalle grid_renglon_traspaso;
    idx integer := 0;

BEGIN

    --obtiene empresa_id, sucursal_id
    SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id
      FROM gral_usr_suc
      JOIN gral_suc    ON gral_suc.id             = gral_usr_suc.gral_suc_id
      JOIN inv_suc_alm ON inv_suc_alm.sucursal_id = gral_suc.id
     WHERE gral_usr_suc.gral_usr_id = _usuario_id
      INTO emp_id, suc_id;

    SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion)  INTO ano_actual;
    SELECT EXTRACT(MONTH FROM espacio_tiempo_ejecucion) INTO mes_actual;

    --Obtiene parametros de la empresa
    SELECT control_exis_pres
      FROM gral_emp
     WHERE id = emp_id
      INTO controlExisPres;

    --Aplicativo Traspasos

    --str_data[4]    identificador
    --str_data[5]    select_suc_origen
    --str_data[6]    select_alm_origen
    --str_data[7]    select_suc_destino
    --str_data[8]    select_alm_destino
    --str_data[9]    observaciones
    --str_data[10]   fecha_traspaso

    -- Folio Traspaso
    id_tipo_consecutivo := 29;

    -- aqui entra para tomar el consecutivo del folio la sucursal actual
    affected_rows := 0;

    UPDATE gral_cons
       SET consecutivo = (
               SELECT sbt.consecutivo + 1
                 FROM gral_cons AS sbt
                WHERE sbt.id = gral_cons.id
           )
     WHERE gral_emp_id       = emp_id
       AND gral_suc_id       = suc_id
       AND gral_cons_tipo_id = id_tipo_consecutivo
 RETURNING prefijo, consecutivo
      INTO prefijo_consecutivo, nuevo_consecutivo;

    GET DIAGNOSTICS affected_rows := ROW_COUNT;
    IF affected_rows = 0 THEN
        valor_retorno := 'Traspaso (nuevo): No fue posible obtener el folio Traspaso (emp_id=' || emp_id || ', suc_id=' || suc_id || ', id_tipo_consecutivo=' || id_tipo_consecutivo || ')';
        RAISE;
    END IF;

    -- concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio del traspaso
    folio1 := prefijo_consecutivo || nuevo_consecutivo::character varying;

    INSERT INTO inv_tras (
        folio,              --folio1,
        fecha_traspaso,     --str_data[10]::date,
        gral_suc_id_origen, --str_data[5]::integer,
        inv_alm_id_origen,  --str_data[6]::integer,
        gral_suc_id_destino,--str_data[7]::integer,
        inv_alm_id_destino, --str_data[8]::integer,
        observaciones,      --str_data[9],
        momento_creacion,   --espacio_tiempo_ejecucion,
        gral_emp_id,        --emp_id,
        gral_suc_id,        --suc_id,
        gral_usr_id_creacion--_usuario_id
    ) VALUES (
        folio1,
        _fecha_traspaso,
        _suc_orig_id,
        _alm_orig_id,
        _suc_dest_id,
        _alm_dest_id,
        _observaciones,
        espacio_tiempo_ejecucion,
        emp_id,
        suc_id,
        _usuario_id
    ) RETURNING id INTO ultimo_id;


    --********************************************************************************************************
    --GENERA REGISTRO PARA ORDEN DE TRASPASO(inv_otras)
    --Folio Orden de Traspaso
    id_tipo_consecutivo := 30;

    --aqui entra para tomar el consecutivo del folio la sucursal actual
    affected_rows := 0;

    UPDATE gral_cons
       SET consecutivo = (
               SELECT sbt.consecutivo + 1
                 FROM gral_cons AS sbt
                WHERE sbt.id = gral_cons.id
           )
     WHERE gral_emp_id       = emp_id
       AND gral_suc_id       = suc_id
       AND gral_cons_tipo_id = id_tipo_consecutivo
 RETURNING prefijo, consecutivo
      INTO prefijo_consecutivo, nuevo_consecutivo;

    GET DIAGNOSTICS affected_rows := ROW_COUNT;
    IF affected_rows = 0 THEN
        valor_retorno := 'Traspaso (nuevo): No fue posible obtener el folio Orden de Traspaso (emp_id=' || emp_id || ', suc_id=' || suc_id || ', id_tipo_consecutivo=' || id_tipo_consecutivo || ')';
        RAISE;
    END IF;

    --concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio dela Orden de Traspaso
    folio2 := prefijo_consecutivo || nuevo_consecutivo::character varying;

    INSERT INTO inv_otras (
        folio,
        fecha,
        gral_suc_id_origen,
        inv_alm_id_origen,
        gral_suc_id_destino,
        inv_alm_id_destino,
        momento_creacion,
        gral_emp_id,
        gral_suc_id,
        gral_usr_id_creacion,
        inv_tras_id
    ) VALUES (
        folio2,
        _fecha_traspaso,
        _suc_orig_id,
        _alm_orig_id,
        _suc_dest_id,
        _alm_dest_id,
        espacio_tiempo_ejecucion,
        emp_id,
        suc_id,
        _usuario_id,
        ultimo_id
    ) RETURNING id INTO ultimo_id2;

    --********************************************************************************************************

    tipo_movimiento_id := 7;--SALIDA POR TRASPASO

    --genera registro del movimiento
    INSERT INTO inv_mov (
        referencia,         --folio1,
        inv_mov_tipo_id,    --tipo_movimiento_id,
        fecha_mov,          --_fecha_traspaso,
        observacion,        --_observaciones,
        momento_creacion,   --espacio_tiempo_ejecucion,
        gral_usr_id,        --_usuario_id,
        gral_app_id         --app_selected
    ) VALUES (
        folio1,
        tipo_movimiento_id,
        _fecha_traspaso,
        _observaciones,
        espacio_tiempo_ejecucion,
        _usuario_id,
        app_selected
    ) RETURNING id INTO identificador_nuevo_movimiento;


    tipo_movimiento_id := 3;--ENTRADA TRASPASO

    --genera registro del movimiento
    INSERT INTO inv_mov (
        referencia,         --folio1,
        inv_mov_tipo_id,    --tipo_movimiento_id,
        fecha_mov,          --_fecha_traspaso,
        observacion,        --_observaciones,
        momento_creacion,   --espacio_tiempo_ejecucion,
        gral_usr_id,        --_usuario_id,
        gral_app_id         --app_selected
    ) VALUES (
        folio1,
        tipo_movimiento_id,
        _fecha_traspaso,
        _observaciones,
        espacio_tiempo_ejecucion,
        _usuario_id,
        app_selected
    ) RETURNING id INTO identificador_nuevo_movimiento_entrada;


    FOR idx IN 1 .. array_length(_grid_detalle, 1) LOOP

        detalle := _grid_detalle[idx];

        --str_filas[1]    idproducto[i]
        --str_filas[2]    cant_traspaso[i]
        --str_filas[3]    no_tr[i]
        --str_filas[4]    select_pres

        --buscar el numero de decimales de la unidad del producto
        SELECT inv_prod_unidades.decimales
          FROM inv_prod
          JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id
         WHERE inv_prod.id = detalle.inv_prod_id
          INTO noDecUnidad;

        IF noDecUnidad IS NULL THEN
            noDecUnidad := 0;
        END IF;

        --redondear la Cantidad de la Presentacion
        detalle.cantidad_tras := round(detalle.cantidad_tras::numeric, noDecUnidad)::double precision;

        --crear registro en detalles de traspaso
        INSERT INTO inv_tras_det (
            inv_tras_id,
            inv_prod_id,
            cantidad_tras,
            inv_prod_presentacion_id
        ) VALUES (
            ultimo_id,
            detalle.inv_prod_id,
            detalle.cantidad_tras,
            detalle.inv_prod_presentacion_id
        );

        --crea registro para detalles Orden de Traspaso
        INSERT INTO inv_otras_det (
            inv_otras_id,
            inv_prod_id,
            cantidad_tras,
            inv_prod_presentacion_id
        ) VALUES (
            ultimo_id2,
            detalle.inv_prod_id,
            detalle.cantidad_tras,
            detalle.inv_prod_presentacion_id
        );

        --obtener el costo promedio actual del producto
        SELECT *
          FROM inv_obtiene_costo_promedio_actual(detalle.inv_prod_id, espacio_tiempo_ejecucion)
          INTO precio_unitario;

        --***** RESGISTRAR SALIDA *********************
        --genera registro en detalles del movimiento
        INSERT INTO inv_mov_detalle (
            inv_mov_id,
            alm_origen_id,
            alm_destino_id,
            producto_id,
            cantidad,
            costo,
            inv_prod_presentacion_id
        ) VALUES (
            identificador_nuevo_movimiento,
            _alm_orig_id,
            _alm_dest_id,
            detalle.inv_prod_id,
            detalle.cantidad_tras,
            precio_unitario,
            detalle.inv_prod_presentacion_id
        );

        --query para descontar producto de existencias
        sql_update := 'UPDATE inv_exi
                          SET salidas_'        || mes_actual  || ' = (salidas_' || mes_actual || ' + ' || detalle.cantidad_tras || '),
                              momento_salida_' || mes_actual  || ' = ''' || espacio_tiempo_ejecucion || '''
                        WHERE inv_alm_id  = ' || _alm_orig_id  || '::integer
                          AND inv_prod_id = ' || detalle.inv_prod_id || '
                          AND ano         = ' || ano_actual;
        EXECUTE sql_update;

        ----------------------------------------------------------------------------------------
        --***** INICIA DESCONTAR EXISTENCIA DE PRESENTACIONES ******************************
        --revisar si la configuracion incluye control de Existencias por Presentacion
        IF controlExisPres = TRUE THEN
            --inicializar valor a cero
            equivalenciaPres := 0;

            --buscar la equivalencia de la Presentacion Origen en la Unidad del Producto
            SELECT cantidad AS equiv_pres
              FROM inv_prod_presentaciones
             WHERE id = detalle.inv_prod_presentacion_id
              INTO equivalenciaPres;

            IF equivalenciaPres IS NULL THEN
                equivalenciaPres := 0;
            END IF;

            --Convertir la Cantidad de Unidades a su equivalencia en Cantidad de Presentaciones
            cantPres := round(detalle.cantidad_tras::numeric, noDecUnidad)::double precision / equivalenciaPres::double precision;

            --redondear la Cantidad de la Presentacion
            cantPres := round(cantPres::numeric, noDecUnidad)::double precision;

            --Descontar existencia de las presentaciones
            UPDATE inv_exi_pres
               SET salidas                   = (salidas::double precision + cantPres::double precision),
                   momento_actualizacion     = espacio_tiempo_ejecucion,
                   gral_usr_id_actualizacion = _usuario_id
             WHERE inv_alm_id               = _alm_orig_id
               AND inv_prod_id              = detalle.inv_prod_id
               AND inv_prod_presentacion_id = detalle.inv_prod_presentacion_id;
        END IF;
        --termina descontar existencia de presentaciones
        ---------------------------------------------------------------------------------------------------------------------

        --***** RESGISTRAR ENTRADA *********************
        --genera registro en detalles del movimiento
        INSERT INTO inv_mov_detalle (
            inv_mov_id,
            alm_origen_id,
            alm_destino_id,
            producto_id,
            cantidad,
            costo,
            inv_prod_presentacion_id
        ) VALUES (
            identificador_nuevo_movimiento_entrada,
            _alm_orig_id,
            _alm_dest_id,
            detalle.inv_prod_id,
            detalle.cantidad_tras,
            precio_unitario,
            detalle.inv_prod_presentacion_id
        );

        --reiniciamos el valor de la variable exis a cero
        exis := 0;

        sql_select := '';
        --query para verificar existencia del producto en el almacen y en el año actual
        sql_select := 'SELECT count(id)
                         FROM inv_exi
                        WHERE inv_prod_id = ' || detalle.inv_prod_id || '
                          AND inv_alm_id  = ' || _alm_dest_id  || '
                          AND ano         = ' || ano_actual;

        EXECUTE sql_select
           INTO exis;

        --RAISE EXCEPTION '%' ,'sql_select: '||sql_select;
        IF exis > 0 THEN
            --aqui entra para ACTUALIZAR registro en inv_exi
            sql_update := 'UPDATE inv_exi
                              SET entradas_'        || mes_actual || ' = (entradas_' || mes_actual || ' + ' || detalle.cantidad_tras || '::double precision),
                                  costo_ultimo_'    || mes_actual || ' = '   || precio_unitario || '::double precision,
                                  momento_entrada_' || mes_actual || ' = ''' || espacio_tiempo_ejecucion || '''
                            WHERE inv_alm_id  = ' || _alm_dest_id  || '
                              AND inv_prod_id = ' || detalle.inv_prod_id || '
                              AND ano         = ' || ano_actual;
            EXECUTE sql_update;
        ELSE
            --aqui entra para CREAR registro en inv_exi
            sql_insert := 'INSERT INTO inv_exi (
                                inv_prod_id,
                                inv_alm_id,
                                ano,
                                entradas_'        || mes_actual || ',
                                momento_entrada_' || mes_actual || ',
                                exi_inicial,
                                costo_ultimo_'    || mes_actual ||
                          ') VALUES (' ||
                                detalle.inv_prod_id || ', '     ||
                                _alm_dest_id  || ', '     ||
                                ano_actual   || ', '     ||
                                detalle.cantidad_tras || ', '''   ||
                                espacio_tiempo_ejecucion ||
                                ''', 0, '       ||
                                precio_unitario ||
                          ')';
            EXECUTE sql_insert;
        END IF;


        -------------------------------------------------------------------------------------
        --***** SUMAR EXISTENCIA DE PRESENTACIONES EN EL ALMACEN DESTINO******
        --revisar si la configuracion incluye control de Existencias por Presentacion
        IF controlExisPres = TRUE THEN
            --inicializar a cero
            exis := 0;

            --buscar Registro de la Presentacion en el Almacen Destino
            SELECT count(id) AS exis
              FROM inv_exi_pres
             WHERE inv_alm_id               = _alm_dest_id
               AND inv_prod_id              = detalle.inv_prod_id
               AND inv_prod_presentacion_id = detalle.inv_prod_presentacion_id
              INTO exis;

            IF exis > 0 THEN
                --Sumar existencia de la presentacion
                UPDATE inv_exi_pres
                   SET entradas                  = (entradas::double precision + cantPres::double precision),
                       momento_actualizacion     = espacio_tiempo_ejecucion,
                       gral_usr_id_actualizacion = _usuario_id
                 WHERE inv_alm_id               = _alm_dest_id
                   AND inv_prod_id              = detalle.inv_prod_id
                   AND inv_prod_presentacion_id = detalle.inv_prod_presentacion_id;
            ELSE
                --aqui entra para CREAR registro en inv_exi
                INSERT INTO inv_exi_pres (
                    inv_alm_id,
                    inv_prod_id,
                    inv_prod_presentacion_id,
                    entradas,
                    momento_creacion,
                    gral_usr_id_creacion
                ) VALUES (
                    _alm_dest_id,
                    detalle.inv_prod_id,
                    detalle.inv_prod_presentacion_id,
                    cantPres::double precision,
                    espacio_tiempo_ejecucion,
                    _usuario_id
                );
            END IF;

        END IF;
        --**termina actualizar existencia de Presentaciones
        -----------------------------------------------------------------------------------------
    END LOOP;

    valor_retorno := '1';
    RETURN valor_retorno;

    EXCEPTION
        WHEN others THEN
            RETURN valor_retorno;

END;
$$;