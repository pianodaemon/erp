
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
    _tiempo_entrega_id integer,
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
            borrado_logico,
            tiempo_entrega_id
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
            false,
            _tiempo_entrega_id
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
            momento_actualizacion = espacio_tiempo_ejecucion,
            tiempo_entrega_id = _tiempo_entrega_id
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


CREATE OR REPLACE FUNCTION public.gral_adm_catalogos(
	campos_data text,
	extra_data text[])
    RETURNS character varying
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
	
	--###################################
	--# Wrtten by: Noe Martinez    	    #
	--# mailto: gpmarsan@gmail.com	    #
	--# 12 / marzo / 2012               #
	--###################################

	--estas  variables se utilizan en la mayoria de los catalogos
	str_data text[];
	str_percep text[];
	str_deduc text[];
	
	app_selected integer;
	command_selected text;
	valor_retorno character varying;
	usuario_id integer;
	emp_id integer;
	suc_id integer;
	ultimo_id integer;
	espacio_tiempo_ejecucion timestamp with time zone = now();
	ano_actual integer;
	mes_actual integer;
	id_almacen integer=0;
	exis integer=0;
	
	id_tipo_consecutivo integer=0;
	prefijo_consecutivo character varying = '';
	nuevo_consecutivo bigint=0;
	nuevo_folio character varying = '';
	incluye_modulo_produccion boolean;
	incluye_modulo_contabilidad boolean;
	incluye_modulo_envasado boolean;
	controlExisPres boolean = false;--Variable que indica si se debe controlar las existencias por presentaciones
	incluye_nomina boolean:=false;
	
	--seran eliminadas
	nombre_consecutivo character varying = '';
	cadena_extra character varying = '';
	ultimo_cosecutivo_proveedor character varying;
	folio_proveedor character varying;
	
	--variables para catalogo de clientes
	str_filas text[];
	total_filas integer;--total de elementos de arreglo
	cont_fila integer;--contador de filas o posiciones del arreglo
	factura_en boolean;
	descarga_xml boolean;
	numero_control_client character varying;
	ultimo_cosecutivo_cliente character varying;
	
	--variables para catalogo de productos
	ultimo_cosecutivo_producto character varying;
	nuevo_sku character varying;
	tipo_producto integer;
	id_producto integer;
	str_pres text[];
	tot_filas integer;--total de elementos de arreglo de id de presentaciones
	meta_imp character varying='';
	
	--variable para prefacturas
	ultimo_id_proceso integer;
	
	--variable para pagos
	folio_transaccion bigint;
	ultimo_cosecutivo_transaccion character varying;
	id_forma_pago integer;
	id_anticipo integer;
	monto_anticipo_actual double precision;
	saldo_anticipo double precision;
	rowCount integer;
	item text[];
	iterar text[];
	veces int:=0;
	incrementa int:=1;
	sql_pagos text; 
	fila record;
	fila2 record;
	suma_pagos double precision=0;
	suma_notas_credito double precision=0;
	
	total_factura double precision;
	monto_pagos double precision;
	nuevacantidad_monto_pago double precision;
	nuevo_saldo_factura double precision;
	suma_pagos_efectuados  double precision;
	
	id_pago integer:=0;
	monto_cancelado double precision:=0;
	id_pagos_detalles integer;
	nuevacantidad_monto_cancelados double precision:=0;
	total_monto_cancelados double precision;
	serie_folio_cancel character varying:='';
	id_moneda_factura integer:=0;
	id_moneda_anticipo integer:=0; 
	tipo_cambio_pago double precision:=0;
	
	ultimo_id_usr integer=0;
	eliminar_registro boolean=true;
	valor1 double precision:=0;

	cont_alias integer := 0;
	cont_alias_idx integer := 0;
BEGIN
	--convertir cadena en arreglo
	SELECT INTO str_data string_to_array(''||campos_data||'','___');
	
	--aplicativo seleccionado
	app_selected := str_data[1]::integer;
	
	command_selected := str_data[2];--new, edit, delete. Para aplicativo 14 pagos: pago, anticipo, cancelacion
	
	-- usuario que utiliza el aplicativo
	usuario_id := str_data[3]::integer;
	
	/*
	--obtiene empresa_id y sucursal_id
	SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id FROM gral_usr_suc 	JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
	WHERE gral_usr_suc.gral_usr_id = usuario_id
	INTO emp_id, suc_id;
	*/
	
	--obtiene empresa_id, sucursal_id y sucursal_id
  	SELECT gral_suc.empresa_id, gral_usr_suc.gral_suc_id,inv_suc_alm.almacen_id FROM gral_usr_suc 
	JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id
	JOIN inv_suc_alm ON inv_suc_alm.sucursal_id = gral_suc.id
	WHERE gral_usr_suc.gral_usr_id=usuario_id
	INTO emp_id, suc_id, id_almacen;
	
	
	SELECT EXTRACT(YEAR FROM espacio_tiempo_ejecucion) INTO ano_actual;
	SELECT EXTRACT(MONTH FROM espacio_tiempo_ejecucion) INTO mes_actual;
	
	valor_retorno:='0';
	
	--Query para verificar si la empresa actual incluye Control de Existencias por Presentacion
	SELECT control_exis_pres,nomina,incluye_contabilidad FROM gral_emp WHERE id=emp_id 
	INTO controlExisPres, incluye_nomina,incluye_modulo_contabilidad;
	
	-- Catalogo de Almacenes
	IF app_selected = 1 THEN
		IF command_selected = 'new' THEN
			INSERT INTO inv_alm(
				titulo,--str_data[5]
				calle,--str_data[6]
				numero,--str_data[7]
				colonia,--str_data[8]
				codigo_postal,--str_data[9]
				gral_pais_id,--str_data[10]::integer
				gral_edo_id,--str_data[11]::integer
				gral_mun_id,--str_data[12]::integer
				tel_1,--str_data[13]
				tel_2,--str_data[14]
				tel_1_ext,--str_data[15]
				tel_2_ext,--str_data[16]
				responsable,--str_data[17]
				responsable_puesto,--str_data[18]
				responsable_email,--str_data[19]
				almacen_tipo_id,--str_data[20]::integer
				compras,--str_data[21]::boolean
				consignacion,--str_data[22]::boolean
				explosion_mat,--str_data[23]::boolean
				garantias,--str_data[24]::boolean
				reabastecimiento,--str_data[25]::boolean
				recepcion_mat,--str_data[26]::boolean
				reporteo,--str_data[27]::boolean
				traspaso,--str_data[28]::boolean
				ventas,--str_data[29]::boolean
				borrado_logico,--false
				momento_creacion--now()
			) VALUES(str_data[5],str_data[6],str_data[7],str_data[8],str_data[9],str_data[10]::integer,str_data[11]::integer,str_data[12]::integer,str_data[13],str_data[14],str_data[15],str_data[16],str_data[17],str_data[18],str_data[19],str_data[20]::integer,str_data[21]::boolean,str_data[22]::boolean,str_data[23]::boolean,str_data[24]::boolean,str_data[25]::boolean,str_data[26]::boolean,str_data[27]::boolean,str_data[28]::boolean,str_data[29]::boolean,false,now()) 
			RETURNING id INTO ultimo_id;
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			
			IF extra_data[1] != 'sin datos' THEN
				--RAISE EXCEPTION '%' ,extra_data[cont_fila]::integer;
				FOR cont_fila IN 1 .. total_filas LOOP
					INSERT INTO inv_suc_alm(almacen_id,sucursal_id) VALUES(ultimo_id, extra_data[cont_fila]::integer);
				END LOOP;
			END IF;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			
			UPDATE inv_alm SET 
				titulo=str_data[5],
				calle=str_data[6],
				numero=str_data[7],
				colonia=str_data[8],
				codigo_postal=str_data[9],
				gral_pais_id=str_data[10]::integer,
				gral_edo_id=str_data[11]::integer,
				gral_mun_id=str_data[12]::integer,
				tel_1=str_data[13],
				tel_2=str_data[14],
				tel_1_ext=str_data[15],
				tel_2_ext=str_data[16],
				responsable=str_data[17],
				responsable_puesto=str_data[18],
				responsable_email=str_data[19],
				almacen_tipo_id=str_data[20]::integer,
				compras=str_data[21]::boolean,
				consignacion=str_data[22]::boolean,
				explosion_mat=str_data[23]::boolean,
				garantias=str_data[24]::boolean,
				reabastecimiento=str_data[25]::boolean,
				recepcion_mat=str_data[26]::boolean,
				reporteo=str_data[27]::boolean,
				traspaso=str_data[28]::boolean,
				ventas=str_data[29]::boolean,
				momento_actualizacion=now()
			WHERE id = str_data[4]::integer;

			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			IF extra_data[1] != 'sin datos' THEN
				DELETE FROM inv_suc_alm WHERE almacen_id=str_data[4]::integer;
				FOR cont_fila IN 1 .. total_filas LOOP
					INSERT INTO inv_suc_alm(almacen_id,sucursal_id) VALUES(str_data[4]::integer, extra_data[cont_fila]::integer);
				END LOOP;
			END IF;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_alm SET borrado_logico=true, momento_baja=now() WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de Almacenes

	
	-- Catalogo de Proveedores
	IF app_selected = 2 THEN
		IF command_selected = 'new' THEN

			id_tipo_consecutivo:=2;--Folio de proveedor
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

			INSERT INTO cxp_prov(
				folio,--nuevo_folio
				rfc,--str_data[6]
				curp,--str_data[7]
				razon_social,--str_data[8]
				clave_comercial,--str_data[9]
				calle,--str_data[10]
				numero,--str_data[11]
				colonia,--str_data[12]
				cp,--str_data[13]
				entre_calles,--str_data[14]
				pais_id,--str_data[15]::integer
				estado_id,--str_data[16]::integer
				municipio_id,--str_data[17]::integer
				localidad_alternativa,--str_data[18]
				telefono1,--str_data[19]
				extension1,--str_data[20]
				fax,--str_data[21]
				telefono2,--str_data[22]
				extension2,--str_data[23]
				correo_electronico,--str_data[24]
				web_site,--str_data[25]
				impuesto,--str_data[26]::integer
				cxp_prov_zona_id,--str_data[27]::integer
				grupo_id,--str_data[28]::integer
				proveedortipo_id,--str_data[29]::integer
				clasif_1,--str_data[30]::integer
				clasif_2,--str_data[31]::integer
				clasif_3,--str/controllers/facturas/startup.agnux_data[32]::integer
				moneda_id,--str_data[33]::integer
				tiempo_entrega_id,--str_data[34]::integer
				estatus,--str_data[35]::boolean
				limite_credito,--str_data[36]::double precision
				dias_credito_id,--str_data[37]::integer
				descuento,--str_data[38]::double precision
				credito_a_partir,--str_data[39]::integer
				cxp_prov_tipo_embarque_id,--str_data[40]::integer
				flete_pagado,--str_data[41]::boolean
				condiciones,--str_data[42]
				observaciones,--str_data[43]
				vent_contacto,--str_data[44]
				vent_puesto,--str_data[45]
				vent_calle,--str_data[46]
				vent_numero,--/controllers/facturas/startup.agnuxstr_data[47]
				vent_colonia,--str_data[48]
				vent_cp,--str_data[49]
				vent_entre_calles,--str_data[50]
				vent_pais_id,--str_data[51]::integer
				vent_estado_id,--str_data[52]::integer
				vent_municipio_id,--str_data[53]::integer
				vent_telefono1,--str_data[54]
				vent_extension1,--str_data[55]
				vent_fax,--str_data[56]
				vent_telefono2,--str_data[57]
				vent_extension2,--str_data[58]
				vent_email,--str_data[59]
				cob_contacto,--str_data[60]
				cob_puesto,--str_data[61]
				cob_calle,--str_data[62]
				cob_numero,--str_data[63]
				cob_colonia,--str_data[64]
				cob_cp,--str_data[65]
				cob_entre_calles,--str_data[66]
				cob_pais_id,--str_data[67]::integer
				cob_estado_id,--str_data[68]::integer
				cob_municipio_id,--str_data[69]::integer
				cob_telefono1,--str_data[70]
				cob_extension1,--str_data[71]
				cob_fax,--str_data[72]
				cob_telefono2,--str_data[73]
				cob_extension2,--str_data[74]
				cob_email,--str_data[75]
				comentarios,--str_data[76]
				ctb_cta_id_pasivo,--str_data[77]::integer,
				ctb_cta_id_egreso,--str_data[78]::integer,
				ctb_cta_id_ietu,--str_data[79]::integer,
				ctb_cta_id_comple,--str_data[80]::integer,
				ctb_cta_id_pasivo_comple,--str_data[81]::integer,
				transportista,--str_data[82]::boolean,
				empresa_id,--emp_id
				sucursal_id,--suc_id
				borrado_logico,--false,
				momento_creacion,--now()
				id_usuario_creacion--usuario_id
			)
			VALUES(nuevo_folio,str_data[6],str_data[7],str_data[8],str_data[9],str_data[10],str_data[11],str_data[12],str_data[13],str_data[14],str_data[15]::integer,str_data[16]::integer,str_data[17]::integer,str_data[18],str_data[19],str_data[20],str_data[21],str_data[22],str_data[23],str_data[24],str_data[25],str_data[26]::integer,str_data[27]::integer,str_data[28]::integer,str_data[29]::integer,str_data[30]::integer,str_data[31]::integer,str_data[32]::integer,str_data[33]::integer,str_data[34]::integer,str_data[35]::boolean,str_data[36]::double precision,str_data[37]::integer,str_data[38]::double precision,str_data[39]::integer,str_data[40]::integer,str_data[41]::boolean,str_data[42],str_data[43],str_data[44],str_data[45],str_data[46],str_data[47],str_data[48],str_data[49],str_data[50],str_data[51]::integer,str_data[52]::integer,str_data[53]::integer,str_data[54],str_data[55],str_data[56],str_data[57],str_data[58],str_data[59],str_data[60],str_data[61],str_data[62],str_data[63],str_data[64],str_data[65],str_data[66],str_data[67]::integer,str_data[68]::integer,str_data[69]::integer,str_data[70],str_data[71],str_data[72],str_data[73],str_data[74],str_data[75],str_data[76], str_data[77]::integer, str_data[78]::integer, str_data[79]::integer, str_data[80]::integer, str_data[81]::integer, str_data[82]::boolean, emp_id, suc_id, false, now(), usuario_id);
			
			/*
			INSERT INTO cxp_prov(
				folio,--nuevo_folio
				rfc,--str_data[6]
				curp,--str_data[7]
				razon_social,--str_data[8]
				clave_comercial,--str_data[9]
				calle,--str_data[10]
				numero,--str_data[11]
				colonia,--str_data[12]
				cp,--str_data[13]
				entre_calles,--str_data[14]
				pais_id,--str_data[15]::integer
				estado_id,--str_data[16]::integer
				municipio_id,--str_data[17]::integer
				localidad_alternativa,--str_data[18]
				telefono1,--str_data[19]
				extension1,--str_data[20]
				fax,--str_data[21]
				telefono2,--str_data[22]
				extension2,--str_data[23]
				correo_electronico,--str_data[24]
				web_site,--str_data[25]
				impuesto,--str_data[26]::integer
				cxp_prov_zona_id,--str_data[27]::integer
				grupo_id,--str_data[28]::integer
				proveedortipo_id,--str_data[29]::integer
				clasif_1,--str_data[30]::integer
				clasif_2,--str_data[31]::integer
				clasif_3,--str/controllers/facturas/startup.agnux_data[32]::integer
				moneda_id,--str_data[33]::integer
				tiempo_entrega_id,--str_data[34]::integer
				estatus,--str_data[35]::boolean
				limite_credito,--str_data[36]::double precision
				dias_credito_id,--str_data[37]::integer
				descuento,--str_data[38]::double precision
				credito_a_partir,--str_data[39]::integer
				cxp_prov_tipo_embarque_id,--str_data[40]::integer
				flete_pagado,--str_data[41]::boolean
				condiciones,--str_data[42]
				observaciones,--str_data[43]
				vent_contacto,--str_data[44]
				vent_puesto,--str_data[45]
				vent_calle,--str_data[46]
				vent_numero,--/controllers/facturas/startup.agnuxstr_data[47]
				vent_colonia,--str_data[48]
				vent_cp,--str_data[49]
				vent_entre_calles,--str_data[50]
				vent_pais_id,--str_data[51]::integer
				vent_estado_id,--str_data[52]::integer
				vent_municipio_id,--str_data[53]::integer
				vent_telefono1,--str_data[54]
				vent_extension1,--str_data[55]
				vent_fax,--str_data[56]
				vent_telefono2,--str_data[57]
				vent_extension2,--str_data[58]
				vent_email,--str_data[59]
				cob_contacto,--str_data[60]
				cob_puesto,--str_data[61]
				cob_calle,--str_data[62]
				cob_numero,--str_data[63]
				cob_colonia,--str_data[64]
				cob_cp,--str_data[65]
				cob_entre_calles,--str_data[66]
				cob_pais_id,--str_data[67]::integer
				cob_estado_id,--str_data[68]::integer
				cob_municipio_id,--str_data[69]::integer
				cob_telefono1,--str_data[70]
				cob_extension1,--str_data[71]
				cob_fax,--str_data[72]
				cob_telefono2,--str_data[73]
				cob_extension2,--str_data[74]
				cob_email,--str_data[75]
				comentarios,--str_data[76]
				ctb_cta_id_pasivo,--str_data[77]::integer,
				transportista,--str_data[78]::boolean,
				empresa_id,--emp_id
				sucursal_id,--suc_id
				borrado_logico,--false,
				momento_creacion,--now()
				id_usuario_creacion--usuario_id
			)
			VALUES(nuevo_folio,str_data[6],str_data[7],str_data[8],str_data[9],str_data[10],str_data[11],str_data[12],str_data[13],str_data[14],str_data[15]::integer,str_data[16]::integer,str_data[17]::integer,str_data[18],str_data[19],str_data[20],str_data[21],str_data[22],str_data[23],str_data[24],str_data[25],str_data[26]::integer,str_data[27]::integer,str_data[28]::integer,str_data[29]::integer,str_data[30]::integer,str_data[31]::integer,str_data[32]::integer,str_data[33]::integer,str_data[34]::integer,str_data[35]::boolean,str_data[36]::double precision,str_data[37]::integer,str_data[38]::double precision,str_data[39]::integer,str_data[40]::integer,str_data[41]::boolean,str_data[42],str_data[43],str_data[44],str_data[45],str_data[46],str_data[47],str_data[48],str_data[49],str_data[50],str_data[51]::integer,str_data[52]::integer,str_data[53]::integer,str_data[54],str_data[55],str_data[56],str_data[57],str_data[58],str_data[59],str_data[60],str_data[61],str_data[62],str_data[63],str_data[64],str_data[65],str_data[66],str_data[67]::integer,str_data[68]::integer,str_data[69]::integer,str_data[70],str_data[71],str_data[72],str_data[73],str_data[74],str_data[75],str_data[76], str_data[77]::integer,str_data[78]::boolean, emp_id, suc_id, false, now(), usuario_id);
			*/
			valor_retorno := '1';
		END IF;

		
		IF command_selected = 'edit' THEN 
			/*
			UPDATE cxp_prov SET rfc=str_data[6],curp=str_data[7],razon_social=str_data[8],clave_comercial=str_data[9],calle=str_data[10],numero=str_data[11],colonia=str_data[12],cp=str_data[13],entre_calles=str_data[14],pais_id=str_data[15]::integer,estado_id=str_data[16]::integer,municipio_id=str_data[17]::integer,localidad_alternativa=str_data[18],telefono1=str_data[19],extension1=str_data[20],fax=str_data[21],telefono2=str_data[22],extension2=str_data[23],correo_electronico=str_data[24],web_site=str_data[25],impuesto=str_data[26]::integer,cxp_prov_zona_id=str_data[27]::integer,grupo_id=str_data[28]::integer,proveedortipo_id=str_data[29]::integer,clasif_1=str_data[30]::integer,clasif_2=str_data[31]::integer,clasif_3=str_data[32]::integer,moneda_id=str_data[33]::integer,tiempo_entrega_id=str_data[34]::integer,estatus=str_data[35]::boolean,limite_credito=str_data[36]::double precision,dias_credito_id=str_data[37]::integer,
				descuento=str_data[38]::double precision,credito_a_partir=str_data[39]::integer,cxp_prov_tipo_embarque_id=str_data[40]::integer,flete_pagado=str_data[41]::boolean,condiciones=str_data[42],observaciones=str_data[43],vent_contacto=str_data[44],vent_puesto=str_data[45],vent_calle=str_data[46],vent_numero=str_data[47],vent_colonia=str_data[48],vent_cp=str_data[49],vent_entre_calles=str_data[50],vent_pais_id=str_data[51]::integer,vent_estado_id=str_data[52]::integer,vent_municipio_id=str_data[53]::integer,vent_telefono1=str_data[54],vent_extension1=str_data[55],vent_fax=str_data[56],vent_telefono2=str_data[57],vent_extension2=str_data[58],vent_email=str_data[59],cob_contacto=str_data[60],cob_puesto=str_data[61],cob_calle=str_data[62],cob_numero=str_data[63],cob_colonia=str_data[64],cob_cp=str_data[65],cob_entre_calles=str_data[66],cob_pais_id=str_data[67]::integer,cob_estado_id=str_data[68]::integer,cob_municipio_id=str_data[69]::integer,
				cob_telefono1=str_data[70],cob_extension1=str_data[71],cob_fax=str_data[72],cob_telefono2=str_data[73],cob_extension2=str_data[74],cob_email=str_data[75],comentarios=str_data[76], ctb_cta_id_pasivo=str_data[77]::integer,transportista=str_data[78]::boolean, momento_actualizacion=now(),id_usuario_actualizacion=usuario_id
			WHERE id = str_data[4]::integer;
			*/
			UPDATE cxp_prov SET rfc=str_data[6],curp=str_data[7],razon_social=str_data[8],clave_comercial=str_data[9],calle=str_data[10],numero=str_data[11],colonia=str_data[12],cp=str_data[13],entre_calles=str_data[14],pais_id=str_data[15]::integer,estado_id=str_data[16]::integer,municipio_id=str_data[17]::integer,localidad_alternativa=str_data[18],telefono1=str_data[19],extension1=str_data[20],fax=str_data[21],telefono2=str_data[22],extension2=str_data[23],correo_electronico=str_data[24],web_site=str_data[25],impuesto=str_data[26]::integer,cxp_prov_zona_id=str_data[27]::integer,grupo_id=str_data[28]::integer,proveedortipo_id=str_data[29]::integer,clasif_1=str_data[30]::integer,clasif_2=str_data[31]::integer,clasif_3=str_data[32]::integer,moneda_id=str_data[33]::integer,tiempo_entrega_id=str_data[34]::integer,estatus=str_data[35]::boolean,limite_credito=str_data[36]::double precision,dias_credito_id=str_data[37]::integer,
				descuento=str_data[38]::double precision,credito_a_partir=str_data[39]::integer,cxp_prov_tipo_embarque_id=str_data[40]::integer,flete_pagado=str_data[41]::boolean,condiciones=str_data[42],observaciones=str_data[43],vent_contacto=str_data[44],vent_puesto=str_data[45],vent_calle=str_data[46],vent_numero=str_data[47],vent_colonia=str_data[48],vent_cp=str_data[49],vent_entre_calles=str_data[50],vent_pais_id=str_data[51]::integer,vent_estado_id=str_data[52]::integer,vent_municipio_id=str_data[53]::integer,vent_telefono1=str_data[54],vent_extension1=str_data[55],vent_fax=str_data[56],vent_telefono2=str_data[57],vent_extension2=str_data[58],vent_email=str_data[59],cob_contacto=str_data[60],cob_puesto=str_data[61],cob_calle=str_data[62],cob_numero=str_data[63],cob_colonia=str_data[64],cob_cp=str_data[65],cob_entre_calles=str_data[66],cob_pais_id=str_data[67]::integer,cob_estado_id=str_data[68]::integer,cob_municipio_id=str_data[69]::integer,
				cob_telefono1=str_data[70],cob_extension1=str_data[71],cob_fax=str_data[72],cob_telefono2=str_data[73],cob_extension2=str_data[74],cob_email=str_data[75],comentarios=str_data[76], ctb_cta_id_pasivo=str_data[77]::integer, ctb_cta_id_egreso=str_data[78]::integer, ctb_cta_id_ietu=str_data[79]::integer, ctb_cta_id_comple=str_data[80]::integer, ctb_cta_id_pasivo_comple=str_data[81]::integer, transportista=str_data[82]::boolean, momento_actualizacion=now(),id_usuario_actualizacion=usuario_id
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE cxp_prov SET borrado_logico=true, momento_baja=now(), id_usuario_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina catalogo de proveedores
	

	--Catalogo de Empleados
	IF app_selected = 4 THEN
		IF command_selected = 'new' THEN

			id_tipo_consecutivo:=15;--Consecutivo de clave empleado
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			--RAISE EXCEPTION '%','datos: '||extra_data;
			--RAISE EXCEPTION '%','nombre_consecutivo: '||nombre_consecutivo;
			--RAISE EXCEPTION '%','cadena_extra: '||cadena_extra;
			--RAISE EXCEPTION '%','numero_control_client: '||numero_control_client;

			IF trim(str_data[13])='' THEN
				str_data[13]:='2014-01-01';
			END IF;

			IF trim(str_data[61])='' THEN
				str_data[61]:='0';
			END IF;

			IF trim(str_data[62])='' THEN
				str_data[62]:='0';
			END IF;
			
			INSERT INTO gral_empleados(
				clave,--nuevo_folio,                       
				nombre_pila,--=str_data[5],
				apellido_paterno,--=str_data[6],
				apellido_materno,--=str_data[7],
				imss,--=str_data[8],
				infonavit,--=str_data[9],
				curp,--=str_data[10],
				rfc,--=str_data[11],
				fecha_nacimiento,--=str_data[12]::date,
				fecha_ingreso,--=str_data[13]::date,
				gral_escolaridad_id,--=str_data[14]::integer,
				gral_sexo_id,--=str_data[15]::integer,
				gral_civil_id,--=str_data[16]::integer,
				gral_religion_id,--=str_data[17]::integer,
				gral_sangretipo_id,--=str_data[30]::integer,
				gral_puesto_id,--=str_data[33]::integer,
				gral_categ_id,--=str_data[35]::integer,
				gral_suc_id_empleado,--=str_data[34]::integer,
				telefono,--=str_data[18],
				telefono_movil,--=str_data[19],
				correo_personal,--=str_data[20],
				gral_pais_id,--=str_data[21]::integer,
				gral_edo_id,--=str_data[22]::integer,
				gral_mun_id,--=str_data[23]::integer,
				calle,--=str_data[24],
				numero,--=str_data[25],
				colonia,--=str_data[26],
				cp,--=str_data[27],
				contacto_emergencia,--=str_data[28],
				telefono_emergencia,--=str_data[29],
				enfermedades,--=str_data[31],
				alergias,--=str_data[32],
				comentarios,--=str_data[36],
				comision_agen,--=str_data[41],
				region_id_agen,--=str_data[48],
				comision2_agen,--=str_data[42],
				comision3_agen,--=str_data[43],
				comision4_agen,--=str_data[44],
				dias_tope_comision,--=str_data[45],
				dias_tope_comision2,--=str_data[46],
				dias_tope_comision3,--=str_data[47],
				tipo_comision,--str_data[49]::integer,
				monto_tope_comision,--=str_data[50],
				monto_tope_comision2,--=str_data[51],
				monto_tope_comision3,--=str_data[52],
				correo_empresa,--str_data[53],
				no_int,--str_data[54],
				nom_regimen_contratacion_id,--str_data[55]::integer,
				nom_tipo_contrato_id,--str_data[56]::integer,
				nom_tipo_jornada_id,--str_data[57]::integer,
				nom_periodicidad_pago_id,--str_data[58]::integer,
				tes_ban_id,--str_data[59]::integer,
				nom_riesgo_puesto_id,--str_data[60]::integer,
				salario_base,--str_data[61]::double precision,
				salario_integrado,--str_data[62]::double precision,
				registro_patronal,--str_data[63],
				clabe, --str_data[64],
				genera_nomina, --str_data[67]::boolean,
				gral_depto_id, --str_data[68]::integer,
				momento_creacion,--now()
				gral_usr_id_creacion,
				gral_emp_id,
				gralsuc_id
				)VALUES (
				--Información: data_string: 4___new___1___0___[3]ADMIN___[4]SANTOS___[5]CAMPOS___[6]12345678901___[7]12345678901___[8]MASN831210MK7___[9]MASN831210MK7___[10]2012-08-09___[11]2012-08-15___3___2___2___7___1234567891_________2___19___986___AV.JUAREZ___12___MARIA LUISA___64988___EZEQUIEL CARDENAS___1234567891___2_________4
					nuevo_folio,                       
					str_data[5],
					str_data[6],
					str_data[7],
					str_data[8],
					str_data[9],
					str_data[10],
					str_data[11],
					str_data[12]::date,
					str_data[13]::date,
					str_data[14]::integer,
					str_data[15]::integer,
					str_data[16]::integer,
					str_data[17]::integer,
					str_data[30]::integer,
					str_data[33]::integer,
					str_data[35]::integer,
					str_data[34]::integer,
					str_data[18],
					str_data[19],
					str_data[20],
					str_data[21]::integer,
					str_data[22]::integer,
					str_data[23]::integer,
					str_data[24],
					str_data[25],
					str_data[26],
					str_data[27],
					str_data[28],
					str_data[29],
					str_data[31],
					str_data[32],
					str_data[36],
					str_data[41]::double precision,
					str_data[48]::integer,
					str_data[42]::double precision,
					str_data[43]::double precision,
					str_data[44]::double precision,
					str_data[45]::double precision,
					str_data[46]::double precision,
					str_data[47]::double precision,
					str_data[49]::integer,
					str_data[50]::double precision,
					str_data[51]::double precision,
					str_data[52]::double precision,
					str_data[53],
					str_data[54],
					str_data[55]::integer,
					str_data[56]::integer,
					str_data[57]::integer,
					str_data[58]::integer,
					str_data[59]::integer,
					str_data[60]::integer,
					str_data[61]::double precision,
					str_data[62]::double precision,
					str_data[63],
					str_data[64],
					str_data[67]::boolean,
					str_data[68]::integer,
					now(),
					usuario_id::integer,
					emp_id::integer, 
					suc_id::integer 
				)RETURNING id INTO ultimo_id;
				

			IF trim(str_data[37])<>'' THEN 
				--Si existe el nombre del usuario hay que crear el registro.
				
				--Crea el usuario
				INSERT INTO gral_usr(username,password,enabled,gral_empleados_id)VALUES(str_data[37],str_data[38],str_data[40]::boolean,ultimo_id::integer)
				RETURNING id INTO ultimo_id_usr;
				
				--Asigna sucursal al usuario
				INSERT INTO gral_usr_suc(gral_usr_id,gral_suc_id) VALUES(ultimo_id_usr,str_data[34]::integer);
				
				total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
				cont_fila:=1;
				
				IF extra_data[1]<>'sin datos' THEN
					FOR cont_fila IN 1 .. total_filas LOOP
						SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
						--Aqui se vuelven a crear los registros para asignar roles al usuario
						INSERT INTO gral_usr_rol(gral_usr_id,gral_rol_id) VALUES(ultimo_id_usr,extra_data[cont_fila]::integer);
					END LOOP;
				END IF;
			END IF;

			
			--Verificar si incluye NOMINA
			IF incluye_nomina THEN 
				--str_data[65] Percepciones
				IF str_data[65] is not null AND str_data[65]!='' THEN
					--Convertir en arreglo la cadena de Percepciones
					SELECT INTO str_percep string_to_array(str_data[65],',');
					
					FOR iter_y IN array_lower(str_percep,1) .. array_upper(str_percep,1) LOOP
						INSERT INTO gral_empleado_percep(gral_empleado_id,nom_percep_id) VALUES (ultimo_id,str_percep[iter_y]::integer);
					END LOOP;
				END IF;

				--str_data[66] deducciones
				IF str_data[66] is not null AND str_data[66]!='' THEN
					--Convertir en arreglo la cadena de Percepciones
					SELECT INTO str_deduc string_to_array(str_data[66],',');
					
					FOR iter_y IN array_lower(str_deduc,1) .. array_upper(str_deduc,1) LOOP
						INSERT INTO gral_empleado_deduc(gral_empleado_id,nom_deduc_id) VALUES (ultimo_id,str_deduc[iter_y]::integer);
					END LOOP;
				END IF;
			END IF;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			--SELECT INTO str_data string_to_array(''||campos_data||'','___');
			--RAISE EXCEPTION '%',str_data[1];
			--RAISE EXCEPTION '%',identificador;
			UPDATE gral_empleados SET 
				nombre_pila=str_data[5],
				apellido_paterno=str_data[6],
				apellido_materno=str_data[7],
				imss=str_data[8],
				infonavit=str_data[9],
				curp=str_data[10],
				rfc=str_data[11],
				fecha_nacimiento=str_data[12]::date,
				fecha_ingreso=str_data[13]::date,
				gral_escolaridad_id=str_data[14]::integer,
				gral_sexo_id=str_data[15]::integer,
				gral_civil_id=str_data[16]::integer,
				gral_religion_id=str_data[17]::integer,
				gral_sangretipo_id=str_data[30]::integer,
				gral_puesto_id=str_data[33]::integer,
				gral_categ_id=str_data[35]::integer,
				gral_suc_id_empleado=str_data[34]::integer,
				telefono=str_data[18],
				telefono_movil=str_data[19],
				correo_personal=str_data[20],
				gral_pais_id=str_data[21]::integer,
				gral_edo_id=str_data[22]::integer,
				gral_mun_id=str_data[23]::integer,
				calle=str_data[24],
				numero=str_data[25],
				colonia=str_data[26],
				cp=str_data[27],
				contacto_emergencia=str_data[28],
				telefono_emergencia=str_data[29],
				enfermedades=str_data[31],
				alergias=str_data[32],
				comentarios=str_data[36],
				comision_agen=str_data[41]::double precision,
				region_id_agen=str_data[48]::integer,
				comision2_agen=str_data[42]::double precision,
				comision3_agen=str_data[43]::double precision,
				comision4_agen=str_data[44]::double precision,
				dias_tope_comision=str_data[45]::double precision,
				dias_tope_comision2=str_data[46]::double precision,
				dias_tope_comision3=str_data[47]::double precision,
				tipo_comision=str_data[49]::integer,
				monto_tope_comision=str_data[50]::double precision,
				monto_tope_comision2=str_data[51]::double precision,
				monto_tope_comision3=str_data[52]::double precision,
				correo_empresa=str_data[53],
				no_int=str_data[54],
				nom_regimen_contratacion_id=str_data[55]::integer,
				nom_tipo_contrato_id=str_data[56]::integer,
				nom_tipo_jornada_id=str_data[57]::integer,
				nom_periodicidad_pago_id=str_data[58]::integer,
				tes_ban_id=str_data[59]::integer,
				nom_riesgo_puesto_id=str_data[60]::integer,
				salario_base=str_data[61]::double precision,
				salario_integrado=str_data[62]::double precision,
				registro_patronal=str_data[63],
				clabe=str_data[64],
				genera_nomina=str_data[67]::boolean,
				gral_depto_id=str_data[68]::integer,
				momento_actualizacion=now()::timestamp with time zone,
				gral_usr_id_actualizacion=usuario_id
			WHERE id=str_data[4]::integer;
			
			
			IF trim(str_data[37])<>'' THEN 
				IF (SELECT count(id) FROM gral_usr WHERE gral_empleados_id=str_data[4]::integer)<=0 THEN 
					--Crea el usuario
					INSERT INTO gral_usr(username,password,enabled,gral_empleados_id)VALUES(str_data[37],str_data[38],str_data[40]::boolean,str_data[4]::integer)
					RETURNING id INTO ultimo_id_usr;
				ELSE
					UPDATE gral_usr SET username=str_data[37], password=str_data[38], enabled=str_data[40]::boolean
					WHERE gral_empleados_id=str_data[4]::integer RETURNING id INTO ultimo_id_usr;
				END IF;
				
				--Buscar el registro en gral_usr_suc
				SELECT count(id) FROM gral_usr_suc WHERE gral_usr_id=ultimo_id_usr INTO exis;
				
				IF exis > 0 THEN
					--Actualizar la sucursal del usuario
					UPDATE gral_usr_suc SET gral_suc_id=str_data[34]::integer WHERE gral_usr_id=ultimo_id_usr;
				ELSE 
					--Crear registro
					INSERT INTO gral_usr_suc(gral_usr_id, gral_suc_id)VALUES(ultimo_id_usr, str_data[34]::integer);
				END IF;
			ELSE
				IF (SELECT count(id) FROM gral_usr WHERE gral_empleados_id=str_data[4]::integer AND enabled=true)>=0 THEN 
					UPDATE gral_usr SET enabled=false WHERE gral_empleados_id=str_data[4]::integer AND enabled=true;
				END IF;
			END IF;
			
			
			--Elimina todos los roles asignados actualmente
			delete from gral_usr_rol where gral_usr_id=ultimo_id_usr;

			IF trim(str_data[37])<>'' THEN 
				total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
				cont_fila:=1;

				--RAISE EXCEPTION '%','extra_data[1]: '||extra_data[1];
				
				IF extra_data[1]<>'sin_datos' THEN
					FOR cont_fila IN 1 .. total_filas LOOP
						SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
						--Aqui se vuelven a crear los registros
						INSERT INTO gral_usr_rol(gral_usr_id,gral_rol_id  )
						VALUES(ultimo_id_usr,extra_data[cont_fila]::integer);
					END LOOP;
				END IF;
			END IF;

			--Verificar si incluye NOMINA
			IF incluye_nomina THEN 
				--Elimina las Percepciones asignadas actualmente
				delete from gral_empleado_percep where gral_empleado_id=str_data[4]::integer;
				
				--str_data[65] Percepciones
				IF str_data[65] is not null AND str_data[65]!='' THEN
					--Convertir en arreglo la cadena de Percepciones
					SELECT INTO str_percep string_to_array(str_data[65],',');
					--Aqui se vuelven a crear registros de las percepciones asignadas
					FOR iter_y IN array_lower(str_percep,1) .. array_upper(str_percep,1) LOOP
						INSERT INTO gral_empleado_percep(gral_empleado_id,nom_percep_id) VALUES (str_data[4]::integer,str_percep[iter_y]::integer);
					END LOOP;
				END IF;

				--Elimina las Deducciones asignadas actualmente
				delete from gral_empleado_deduc where gral_empleado_id=str_data[4]::integer;
				
				--str_data[66] deducciones
				IF str_data[66] is not null AND str_data[66]!='' THEN
					--Convertir en arreglo la cadena de Percepciones
					SELECT INTO str_deduc string_to_array(str_data[66],',');

					--Aqui se vuelven a crear registros de las Deducciones asignadas
					FOR iter_y IN array_lower(str_deduc,1) .. array_upper(str_deduc,1) LOOP
						INSERT INTO gral_empleado_deduc(gral_empleado_id,nom_deduc_id) VALUES (str_data[4]::integer,str_deduc[iter_y]::integer);
					END LOOP;
				END IF;
			END IF;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE gral_empleados SET borrado_logico=true,  momento_baja=now(), gral_usr_id_baja=usuario_id::integer 
			WHERE id=str_data[4]::integer;
			
			--Deshabilitar usuario y cambiar el nombre del username, esto para evitar conservar el nombre del usuario.
			--No es posible eliminar el registro porque se utliza como llave foranea en varias tablas
			UPDATE gral_usr SET username='01010101010101010101010101010101010101010101010101', enabled=false WHERE gral_empleados_id=str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;

	END IF;--termina catalogo de empleados
	
	
	
	
	-- Catalogo de Clientes
	IF app_selected = 5 THEN
		IF command_selected = 'new' THEN

			id_tipo_consecutivo:=1;--Folio de proveedor
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			--RAISE EXCEPTION '%','emp_id: '||emp_id;
			--RAISE EXCEPTION '%','nombre_consecutivo: '||nombre_consecutivo;
			--RAISE EXCEPTION '%','cadena_extra: '||cadena_extra;
			--RAISE EXCEPTION '%','numero_control_client: '||numero_control_client;
			
			INSERT INTO cxc_clie(
					numero_control,--nuevo_folio
					rfc,--str_data[6]
					curp,--str_data[7]
					razon_social,--str_data[8]
					clave_comercial,--str_data[9]
					calle,--str_data[10]
					numero,--str_data[11]
					entre_calles,--str_data[12]
					numero_exterior,--str_data[13]
					colonia,--str_data[14]
					cp,--str_data[15]
					pais_id,--str_data[16]::integer
					estado_id,--str_data[17]::integer
					municipio_id,--str_data[18]::integer
					localidad_alternativa,--str_data[19]
					telefono1,--str_data[20]
					extension1,--str_data[21]
					fax,--str_data[22]
					telefono2,--str_data[23]
					extension2,--str_data[24]
					email,--str_data[25]
					cxc_agen_id,--str_data[26]::integer
					contacto,--str_data[27]
					zona_id,--str_data[28]::integer
					cxc_clie_grupo_id,--str_data[29]::integer
					clienttipo_id,--str_data[30]::integer
					clasif_1,--str_data[31]::integer
					clasif_2,--str_data[32]::integer
					clasif_3,--str_data[33]::integer
					moneda,--str_data[34]::integer
					filial,--str_data[35]::boolean
					estatus,--str_data[36]::boolean
					gral_imp_id,--str_data[37]::integer
					limite_credito,--str_data[38]::double precision
					dias_credito_id,--str_data[39]::integer
					credito_suspendido,--str_data[40]::boolean
					credito_a_partir,--str_data[41]::integer
					cxp_prov_tipo_embarque_id,--str_data[42]::integer
					dias_caducidad_cotizacion,--str_data[43]::integer
					condiciones,--str_data[44]
					observaciones,--str_data[45]
					contacto_compras_nombre,--str_data[46]
					contacto_compras_puesto,--str_data[47]
					contacto_compras_calle,--str_data[48]
					contacto_compras_numero,--str_data[49]
					contacto_compras_colonia,--str_data[50]
					contacto_compras_cp,--str_data[51]
					contacto_compras_entre_calles,--str_data[52]
					contacto_compras_pais_id,--str_data[53]::integer
					contacto_compras_estado_id,--str_data[54]::integer
					contacto_compras_municipio_id,--str_data[55]::integer
					contacto_compras_telefono1,--str_data[56]
					contacto_compras_extension1,--str_data[57]
					contacto_compras_fax,--str_data[58]
					contacto_compras_telefono2,--str_data[59]
					contacto_compras_extension2,--str_data[60]
					contacto_compras_email,--str_data[61]
					contacto_pagos_nombre,--str_data[62]
					contacto_pagos_puesto,--str_data[63]
					contacto_pagos_calle,--str_data[64]
					contacto_pagos_numero,--str_data[65]
					contacto_pagos_colonia,--str_data[66]
					contacto_pagos_cp,--str_data[67]
					contacto_pagos_entre_calles,--str_data[68]
					contacto_pagos_pais_id,--str_data[69]::integer
					contacto_pagos_estado_id,--str_data[70]::integer
					contacto_pagos_municipio_id,--str_data[71]::integer
					contacto_pagos_telefono1,--str_data[72]
					contacto_pagos_extension1,--str_data[73]
					contacto_pagos_fax,--str_data[74]
					contacto_pagos_telefono2,--str_data[75]
					contacto_pagos_extension2,--str_data[76]
					contacto_pagos_email,--str_data[77]
					empresa_immex,--str_data[78]::boolean,
					tasa_ret_immex,--str_data[79]::double precision,
					dia_revision,--str_data[80]::smallint,
					dia_pago,--str_data[81]::smallint,
					cta_pago_mn,--str_data[82],
					cta_pago_usd,--str_data[83],
					ctb_cta_id_activo,--str_data[84]::integer,
					ctb_cta_id_ingreso,--str_data[85]::integer,
					ctb_cta_id_ietu,--str_data[86]::integer,
					ctb_cta_id_comple,--str_data[87]::integer,
					ctb_cta_id_activo_comple,--str_data[88]::integer,
					lista_precio,--str_data[89]::integer,
					fac_metodos_pago_id,--str_data[90]::integer,
					empresa_id,--emp_id
					sucursal_id,--suc_id
					borrado_logico,--false
					momento_creacion,--now()
					id_usuario_creacion--usuario_id
				)VALUES (
					nuevo_folio,
					str_data[6],
					str_data[7],
					str_data[8],
					str_data[9],
					str_data[10],
					str_data[11],
					str_data[12],
					str_data[13],
					str_data[14],
					str_data[15],
					str_data[16]::integer,
					str_data[17]::integer,
					str_data[18]::integer,
					str_data[19],
					str_data[20],
					str_data[21],
					str_data[22],
					str_data[23],
					str_data[24],
					str_data[25],
					str_data[26]::integer,
					str_data[27],
					str_data[28]::integer,
					str_data[29]::integer,
					str_data[30]::integer,
					str_data[31]::integer,
					str_data[32]::integer,
					str_data[33]::integer,
					str_data[34]::integer,
					str_data[35]::boolean,
					str_data[36]::boolean,
					str_data[37]::integer,
					str_data[38]::double precision,
					str_data[39]::integer,
					str_data[40]::boolean,
					str_data[41]::integer,
					str_data[42]::integer,
					str_data[43]::integer,
					str_data[44],
					str_data[45],
					str_data[46],
					str_data[47],
					str_data[48],
					str_data[49],
					str_data[50],
					str_data[51],
					str_data[52],
					str_data[53]::integer,
					str_data[54]::integer,
					str_data[55]::integer,
					str_data[56],
					str_data[57],
					str_data[58],
					str_data[59],
					str_data[60],
					str_data[61],
					str_data[62],
					str_data[63],
					str_data[64],
					str_data[65],
					str_data[66],
					str_data[67],
					str_data[68],
					str_data[69]::integer,
					str_data[70]::integer,
					str_data[71]::integer,
					str_data[72],
					str_data[73],
					str_data[74],
					str_data[75],
					str_data[76],
					str_data[77],
					str_data[78]::boolean,
					str_data[79]::double precision,
					str_data[80]::smallint,
					str_data[81]::smallint,
					str_data[82],
					str_data[83],
					str_data[84]::integer,
					str_data[85]::integer,
					str_data[86]::integer,
					str_data[87]::integer,
					str_data[88]::integer,
					str_data[89]::integer,
					str_data[90]::integer,
					emp_id,
					suc_id,
					false,
					now(),
					usuario_id
				)RETURNING id INTO ultimo_id;
			
				
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			
			IF extra_data[1] != 'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
					--str_filas[1] calle
					--str_filas[2] numero
					--str_filas[3] colonia
					--str_filas[4] idpais
					--str_filas[5] identidad
					--str_filas[6] idlocalidad
					--str_filas[7] codigop
					--str_filas[8] localternativa
					--str_filas[9] telefono
					--str_filas[10] numfax
					
					INSERT INTO erp_clients_consignacions(cliente_id, calle, numero, colonia, pais_id, estado_id, municipio_id, cp, localidad_alternativa, telefono, fax, momento_creacion)
					VALUES(ultimo_id, str_filas[1], str_filas[2], str_filas[3], str_filas[4]::integer, str_filas[5]::integer, str_filas[6]::integer, str_filas[7], str_filas[8], str_filas[9], str_filas[10], now());
					
				END LOOP;
				
			END IF;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			--SELECT INTO str_data string_to_array(''||campos_data||'','___');
			--RAISE EXCEPTION '%',str_data[1];
			--RAISE EXCEPTION '%',identificador;
			UPDATE cxc_clie SET 
					rfc=str_data[6],
					curp=str_data[7],
					razon_social=str_data[8],
					clave_comercial=str_data[9],
					calle=str_data[10],
					numero=str_data[11],
					entre_calles=str_data[12],
					numero_exterior=str_data[13],
					colonia=str_data[14],
					cp=str_data[15],
					pais_id=str_data[16]::integer,
					estado_id=str_data[17]::integer,
					municipio_id=str_data[18]::integer,
					localidad_alternativa=str_data[19],
					telefono1=str_data[20],
					extension1=str_data[21],
					fax=str_data[22],
					telefono2=str_data[23],
					extension2=str_data[24],
					email=str_data[25],
					cxc_agen_id=str_data[26]::integer,
					contacto=str_data[27],
					zona_id=str_data[28]::integer,
					cxc_clie_grupo_id=str_data[29]::integer,
					clienttipo_id=str_data[30]::integer,
					clasif_1=str_data[31]::integer,
					clasif_2=str_data[32]::integer,
					clasif_3=str_data[33]::integer,
					moneda=str_data[34]::integer,
					filial=str_data[35]::boolean,
					estatus=str_data[36]::boolean,
					gral_imp_id=str_data[37]::integer,
					limite_credito=str_data[38]::double precision,
					dias_credito_id=str_data[39]::integer,
					credito_suspendido=str_data[40]::boolean,
					credito_a_partir=str_data[41]::integer,
					cxp_prov_tipo_embarque_id=str_data[42]::integer,
					dias_caducidad_cotizacion=str_data[43]::integer,
					condiciones=str_data[44],
					observaciones=str_data[45],
					contacto_compras_nombre=str_data[46],
					contacto_compras_puesto=str_data[47],
					contacto_compras_calle=str_data[48],
					contacto_compras_numero=str_data[49],
					contacto_compras_colonia=str_data[50],
					contacto_compras_cp=str_data[51],
					contacto_compras_entre_calles=str_data[52],
					contacto_compras_pais_id=str_data[53]::integer,
					contacto_compras_estado_id=str_data[54]::integer,
					contacto_compras_municipio_id=str_data[55]::integer,
					contacto_compras_telefono1=str_data[56],
					contacto_compras_extension1=str_data[57],
					contacto_compras_fax=str_data[58],
					contacto_compras_telefono2=str_data[59],
					contacto_compras_extension2=str_data[60],
					contacto_compras_email=str_data[61],
					contacto_pagos_nombre=str_data[62],
					contacto_pagos_puesto=str_data[63],
					contacto_pagos_calle=str_data[64],
					contacto_pagos_numero=str_data[65],
					contacto_pagos_colonia=str_data[66],
					contacto_pagos_cp=str_data[67],
					contacto_pagos_entre_calles=str_data[68],
					contacto_pagos_pais_id=str_data[69]::integer,
					contacto_pagos_estado_id=str_data[70]::integer,
					contacto_pagos_municipio_id=str_data[71]::integer,
					contacto_pagos_telefono1=str_data[72],
					contacto_pagos_extension1=str_data[73],
					contacto_pagos_fax=str_data[74],
					contacto_pagos_telefono2=str_data[75],
					contacto_pagos_extension2=str_data[76],
					contacto_pagos_email=str_data[77],
					empresa_immex=str_data[78]::boolean,
					tasa_ret_immex=str_data[79]::double precision,
					dia_revision=str_data[80]::smallint,
					dia_pago=str_data[81]::smallint,
					cta_pago_mn=str_data[82],
					cta_pago_usd=str_data[83],
					ctb_cta_id_activo=str_data[84]::integer,
					ctb_cta_id_ingreso=str_data[85]::integer,
					ctb_cta_id_ietu=str_data[86]::integer,
					ctb_cta_id_comple=str_data[87]::integer,
					ctb_cta_id_activo_comple=str_data[88]::integer,
					lista_precio=str_data[89]::integer,
					fac_metodos_pago_id=str_data[90]::integer,
					momento_actualizacion = now(),
					id_usuario_actualizacion = usuario_id,
					empresa_id=emp_id,
					sucursal_id=suc_id
			WHERE id=str_data[4]::integer;
			
			--eliminar direcciones de este cliente en la tabla clients_consignacions
			DELETE FROM erp_clients_consignacions WHERE cliente_id = str_data[4]::integer;
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			
			IF extra_data[1] != 'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
					--aqui se vuelven a crear los registros
					INSERT INTO erp_clients_consignacions(cliente_id,calle,numero,colonia,pais_id,estado_id,municipio_id,cp,localidad_alternativa,telefono,fax,momento_creacion)
					VALUES(str_data[3]::integer,str_filas[1],str_filas[2],str_filas[3],str_filas[4]::integer,str_filas[5]::integer,str_filas[6]::integer,str_filas[7],str_filas[8],str_filas[9],str_filas[10],now());
					
				END LOOP;
				
			END IF;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE cxc_clie SET borrado_logico=true, momento_baja=now(),id_usuario_baja = str_data[3]::integer WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;

	END IF;--termina catalogo de clientes
	
	
	
	-- Catalogo de Productos
	IF app_selected = 8 THEN
		
		IF str_data[15]::integer=0 THEN
			meta_imp:='exento';
		END IF;
		IF str_data[15]::integer=1 THEN
			meta_imp:='iva_1';
		END IF;
		IF str_data[15]::integer=2 THEN
			meta_imp:='tasa_cero';
		END IF;
		
		--query para verificar si la Empresa actual incluye Modulo de Produccion, Modulo de Contabilidad y Modulo de Envasado
		SELECT incluye_produccion, incluye_contabilidad, encluye_envasado FROM gral_emp WHERE id=emp_id INTO incluye_modulo_produccion, incluye_modulo_contabilidad, incluye_modulo_envasado;
		
		IF command_selected = 'new' THEN
			id_tipo_consecutivo:=3;--Folio de pproducto

			--alter para catalogo productos
			--ALTER TABLE inv_prod ADD COLUMN archivo_pdf character varying DEFAULT '';
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			--UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			--WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			--nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			nuevo_folio := str_data[31];
			tipo_producto:=str_data[18]::integer;
			INSERT INTO inv_prod(	
				sku,--nuevo_folio
				descripcion,--str_data[5]
				codigo_barras,--str_data[6]
				tentrega,--str_data[7]::integer,
				inv_clas_id,--str_data[8]::integer
				inv_stock_clasif_id,--str_data[9]::integer
				estatus,--str_data[10]::boolean
				inv_prod_familia_id,--str_data[11]::integer
				subfamilia_id,--str_data[12]::integer
				inv_prod_grupo_id,--str_data[13]::integer
				ieps,--str_data[14]::integer
				--meta_impuesto,--meta_imp
				gral_impto_id,--str_data[15]::integer,
				inv_prod_linea_id,--str_data[16]::integer
				inv_mar_id,--str_data[17]::integer
				tipo_de_producto_id,--str_data[18]::integer
				inv_seccion_id,--str_data[19]::integer
				unidad_id,--str_data[20]::integer
				requiere_numero_lote,--str_data[21]::boolean
				requiere_nom,--str_data[22]::boolean
				requiere_numero_serie,--str_data[23]::boolean
				requiere_pedimento,--str_data[24]::boolean
				permitir_stock,--str_data[25]::boolean
				venta_moneda_extranjera,--str_data[26]::boolean
				compra_moneda_extranjera,--str_data[27]::boolean
				cxp_prov_id,--str_data[29]::integer
				densidad,--str_data[30]::double precision
				valor_maximo,--str_data[32]::double precision
				valor_minimo,--str_data[33]::double precision
				punto_reorden,--str_data[34]::double precision
				ctb_cta_id_gasto, --str_data[35]::integer,
				ctb_cta_id_costo_venta, --str_data[36]::integer,
				ctb_cta_id_venta, --str_data[37]::integer,
				borrado_logico,--false
				momento_creacion,--now()
				id_usuario_creacion,--usuario_id
				empresa_id,--emp_id
				sucursal_id,--suc_id
				descripcion_corta,--str_data[40]
				descripcion_larga,--str_data[41]
				archivo_img,--str_data[38]
				archivo_pdf,--str_data[39]
				inv_prod_presentacion_id,--str_data[42]::integer
				flete,--str_data[43]::boolean,
				no_clie,--str_data[44]
				gral_mon_id,--str_data[45]::integer
				gral_imptos_ret_id, --str_data[46]::integer
                cfdi_prodserv_id --str_data[47]::integer
			) values(
				nuevo_folio,
				str_data[5],
				str_data[6],
				str_data[7]::integer,
				str_data[8]::integer,
				str_data[9]::integer,
				str_data[10]::boolean,
				str_data[11]::integer,
				str_data[12]::integer,
				str_data[13]::integer,
				str_data[14]::integer,
				--meta_imp,
				str_data[15]::integer,
				str_data[16]::integer,
				str_data[17]::integer,
				str_data[18]::integer,
				str_data[19]::integer,
				str_data[20]::integer,
				str_data[21]::boolean,
				str_data[22]::boolean,
				str_data[23]::boolean,
				str_data[24]::boolean,
				str_data[25]::boolean,
				str_data[26]::boolean,
				str_data[27]::boolean,
				str_data[29]::integer,
				str_data[30]::double precision,
				str_data[32]::double precision,
				str_data[33]::double precision,
				str_data[34]::double precision,
				str_data[35]::integer,
				str_data[36]::integer,
				str_data[37]::integer,
				false,
				now(),
				usuario_id,
				emp_id,
				suc_id,
				str_data[40],
				str_data[41],
				str_data[38],
				str_data[39],
				str_data[42]::integer,
				str_data[43]::boolean,
				str_data[44],
				str_data[45]::integer,
				str_data[46]::integer,
                get_id_cfdi_claveprodserv(str_data[47])::integer
			)RETURNING id INTO id_producto;
			
			--convertir en arreglo los id de presentaciones de producto
			SELECT INTO str_pres string_to_array(str_data[28],',');
			
			--obtiene numero de elementos del arreglo str_pres
			tot_filas:= array_length(str_pres,1);
			
			
			--Si el tiopo de producto es diferente de 3 y 4, hay que guardar presentaciones
			--tipo=3 Kit
			--tipo=4 Servicios
			--IF str_data[18]::integer!=3 AND str_data[18]::integer!=4 THEN
				
				FOR cont_fila_pres IN 1 .. tot_filas LOOP
					--Crea registros de presentaciones  en tabla inv_prod_pres_x_prod
					INSERT INTO inv_prod_pres_x_prod(producto_id,presentacion_id) VALUES (id_producto,str_pres[cont_fila_pres]::integer);
					
					--Crea registro por cada presentacion en la tabla de precios 
					INSERT INTO inv_pre (gral_emp_id, inv_prod_id, inv_prod_presentacion_id, momento_creacion,borrado_logico,precio_1, precio_2, precio_3, precio_4, precio_5, precio_6, precio_7, precio_8, precio_9, precio_10, gral_mon_id_pre1, gral_mon_id_pre2, gral_mon_id_pre3, gral_mon_id_pre4, gral_mon_id_pre5, gral_mon_id_pre6, gral_mon_id_pre7, gral_mon_id_pre8, gral_mon_id_pre9, gral_mon_id_pre10, descuento_1,descuento_2,descuento_3,descuento_4,descuento_5,descuento_6,descuento_7,descuento_8,descuento_9,descuento_10,default_precio_1,default_precio_2,default_precio_3,default_precio_4,default_precio_5,default_precio_6,default_precio_7,default_precio_8,default_precio_9,default_precio_10,operacion_precio_1,operacion_precio_2,operacion_precio_3,operacion_precio_4,operacion_precio_5,operacion_precio_6,operacion_precio_7,operacion_precio_8,operacion_precio_9,operacion_precio_10,calculo_precio_1,calculo_precio_2,calculo_precio_3,calculo_precio_4,calculo_precio_5,calculo_precio_6,calculo_precio_7,calculo_precio_8,calculo_precio_9,calculo_precio_10,redondeo_precio_1,redondeo_precio_2,redondeo_precio_3,redondeo_precio_4,redondeo_precio_5,redondeo_precio_6,redondeo_precio_7,redondeo_precio_8,redondeo_precio_9,redondeo_precio_10) 
					VALUES(emp_id, id_producto,str_pres[cont_fila_pres]::integer, now(), false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,0,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,0,0,0,0,  1,1,1,1,1,1,1,1,1,1 ,1,1,1,1,1,1,1,1,1,1,  0,0,0,0,0,0,0,0,0,0);
				END LOOP;
			--END IF;
			
			IF incluye_modulo_produccion=TRUE THEN 
				--para Producto 3=Kit
				IF tipo_producto=3 THEN
					total_filas:= array_length(extra_data,1);
					cont_fila:=1;
					IF extra_data[1]<>'sin datos' THEN
						FOR cont_fila IN 1 .. total_filas LOOP
							SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
							--str_filas[1] eliminado
							IF str_filas[1]::integer != 0 THEN--1: no esta eliminado, 0:eliminado
								--ya no se valida nada
								--str_filas[1]	producto_ingrediente_id
								--str_filas[2] 	porcentaje
								--INSERT INTO inv_prod_formulaciones(producto_formulacion_id,producto_ingrediente_id,porcentaje) VALUES (id_producto,str_filas[1]::integer,str_filas[2]::double precision);
								INSERT INTO inv_kit(producto_kit_id,producto_elemento_id,cantidad) 
								VALUES (id_producto,str_filas[1]::integer,str_filas[2]::double precision);
							END IF;
						END LOOP;
					END IF;
				END IF;	
			ELSE
				--para Producto 1=TERMINADO, 2=INTERMEDIO, 3=KIT, 8=DESARROLLO
				IF tipo_producto=1 OR tipo_producto=2 OR tipo_producto=3 OR tipo_producto=8 THEN 
					total_filas:= array_length(extra_data,1);
					cont_fila:=1;
					IF extra_data[1]<>'sin datos' THEN
						FOR cont_fila IN 1 .. total_filas LOOP
							SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
							--str_filas[1] eliminado
							IF str_filas[1]::integer != 0 THEN--1: no esta eliminado, 0:eliminado
								--ya no se valida nada
								--str_filas[1]	producto_ingrediente_id
								--str_filas[2] 	porcentaje
								--INSERT INTO inv_prod_formulaciones(producto_formulacion_id,producto_ingrediente_id,porcentaje) VALUES (id_producto,str_filas[1]::integer,str_filas[2]::double precision);
								INSERT INTO inv_kit(producto_kit_id,producto_elemento_id,cantidad) VALUES(id_producto,str_filas[1]::integer,str_filas[2]::double precision);
							END IF;
						END LOOP;
					END IF;
				END IF;
			END IF;
			
			
			--Si el tipo de producto es DIFERENTE DE 3=Kit Y 4=Servicios
			IF str_data[18]::integer<>3 AND str_data[18]::integer<>4 THEN
				--Genera registro en la tabla inv_exi
				FOR fila2 IN EXECUTE('SELECT distinct inv_suc_alm.almacen_id FROM gral_suc JOIN inv_suc_alm ON inv_suc_alm.sucursal_id=gral_suc.id WHERE gral_suc.empresa_id='||emp_id||' ORDER BY inv_suc_alm.almacen_id') LOOP
					INSERT INTO inv_exi(inv_prod_id, inv_alm_id, ano, exi_inicial, transito) VALUES(id_producto, fila2.almacen_id, ano_actual, 0, 0);
				END LOOP;
				
				if str_data[45]::integer=1 then 
					--Si es MN, el tipo de cambio es 1
					valor1:=1;
				else
					--Buscar el tipo de cambio del día
					SELECT valor AS tipo_cambio FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=str_data[45]::integer ORDER BY momento_creacion DESC LIMIT 1 into valor1;
					if valor1 is null then valor1:=1; end if;
				end if;
				
				--Genera registro en la tabla 
				INSERT INTO inv_prod_cost_prom(inv_prod_id, ano,gral_mon_id_1,gral_mon_id_2,gral_mon_id_3,gral_mon_id_4,gral_mon_id_5,gral_mon_id_6,gral_mon_id_7,gral_mon_id_8,gral_mon_id_9,gral_mon_id_10,gral_mon_id_11,gral_mon_id_12,tipo_cambio_1,tipo_cambio_2,tipo_cambio_3,tipo_cambio_4,tipo_cambio_5,tipo_cambio_6,tipo_cambio_7,tipo_cambio_8,tipo_cambio_9,tipo_cambio_10,tipo_cambio_11,tipo_cambio_12) 
				VALUES(id_producto, ano_actual,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,str_data[45]::integer,valor1,valor1,valor1,valor1,valor1,valor1,valor1,valor1,valor1,valor1,valor1,valor1);
			END IF;

			-- Guardar aliases (creacion de producto)
			cont_alias := 1;
			FOR cont_alias_idx IN 48 .. 57 LOOP
				IF str_data[cont_alias_idx] <> '' THEN
					INSERT INTO inv_prod_alias (producto_id, alias_id, descripcion) VALUES (id_producto, cont_alias, str_data[cont_alias_idx]);
				END IF;
				cont_alias := cont_alias + 1;
			END LOOP;

			valor_retorno := '1';
		END IF;--termina nuevo producto
		
		
		IF command_selected = 'edit' THEN
			nuevo_folio := str_data[31];
			tipo_producto:=str_data[18]::integer;
			UPDATE inv_prod SET 
				sku=nuevo_folio,--nuevo_folio, 
				descripcion=str_data[5],
				codigo_barras=str_data[6],
				tentrega=str_data[7]::integer,
				inv_clas_id=str_data[8]::integer,
				inv_stock_clasif_id=str_data[9]::integer,
				estatus=str_data[10]::boolean,
				inv_prod_familia_id=str_data[11]::integer,
				subfamilia_id=str_data[12]::integer,
				inv_prod_grupo_id=str_data[13]::integer,
				ieps=str_data[14]::integer,
				--meta_impuesto=meta_imp,
				gral_impto_id=str_data[15]::integer,
				inv_prod_linea_id=str_data[16]::integer,
				inv_mar_id=str_data[17]::integer,
				tipo_de_producto_id=str_data[18]::integer,
				inv_seccion_id=str_data[19]::integer,
				unidad_id=str_data[20]::integer,
				requiere_numero_lote=str_data[21]::boolean,
				requiere_nom=str_data[22]::boolean,
				requiere_numero_serie=str_data[23]::boolean,
				requiere_pedimento=str_data[24]::boolean,
				permitir_stock=str_data[25]::boolean,
				venta_moneda_extranjera=str_data[26]::boolean,
				compra_moneda_extranjera=str_data[27]::boolean,
				cxp_prov_id=str_data[29]::integer,
				densidad=str_data[30]::double precision,
				valor_maximo=str_data[32]::double precision,
				valor_minimo=str_data[33]::double precision,
				punto_reorden=str_data[34]::double precision,
				ctb_cta_id_gasto=str_data[35]::integer,
				ctb_cta_id_costo_venta=str_data[36]::integer,
				ctb_cta_id_venta=str_data[37]::integer,
				momento_actualizacion=now(),
				id_usuario_actualizacion=usuario_id,
				descripcion_corta=str_data[40],
				descripcion_larga=str_data[41],
				archivo_img=str_data[38],
				archivo_pdf=str_data[39],
				inv_prod_presentacion_id=str_data[42]::integer,
				flete=str_data[43]::boolean,
				no_clie=str_data[44],
				gral_mon_id=str_data[45]::integer,
				gral_imptos_ret_id=str_data[46]::integer,
                cfdi_prodserv_id=get_id_cfdi_claveprodserv(str_data[47])::integer
			WHERE id=str_data[4]::integer;
			
			--convertir en arreglo los id de presentaciones de producto
			SELECT INTO str_pres string_to_array(str_data[28],',');
			
			--obtiene numero de elementos del arreglo str_pres
			tot_filas:= array_length(str_pres,1);
			
			--elimina los registros de las presentaciones del producto
			DELETE FROM inv_prod_pres_x_prod WHERE producto_id=str_data[4]::integer;
			
			--Si el tiopo de producto es diferente de 3 y 4, hay que guardar presentaciones
			--tipo=3 Kit
			--tipo=4 Servicios
			--IF str_data[18]::integer!=3 AND str_data[18]::integer!=4 THEN
				--aqui se vuelven a crear los registros de las presentaciones del producto
				FOR cont_fila_pres IN 1 .. tot_filas LOOP
					INSERT INTO inv_prod_pres_x_prod(producto_id,presentacion_id) VALUES (str_data[4]::integer,str_pres[cont_fila_pres]::integer);
				END LOOP;
			--END IF;
			
			FOR fila IN EXECUTE('SELECT id, inv_prod_id, inv_prod_presentacion_id FROM inv_prod_costos WHERE inv_prod_id='||str_data[4]::integer||' AND ano=EXTRACT(YEAR FROM now())') LOOP
				exis:=0;
				SELECT count(id) FROM inv_prod_pres_x_prod WHERE producto_id=fila.inv_prod_id AND presentacion_id=fila.inv_prod_presentacion_id INTO exis;
				IF exis<=0 THEN 
					DELETE FROM inv_prod_costos WHERE id=fila.id;
				END IF;
			END LOOP;
			
			FOR fila IN EXECUTE('SELECT id, inv_prod_id, inv_prod_presentacion_id FROM inv_pre WHERE inv_prod_id='||str_data[4]::integer||' AND gral_emp_id='||emp_id) LOOP
				exis:=0;
				SELECT count(id) FROM inv_prod_pres_x_prod WHERE producto_id=fila.inv_prod_id AND presentacion_id=fila.inv_prod_presentacion_id INTO exis;
				IF exis<=0 THEN 
					DELETE FROM inv_pre WHERE id=fila.id;
				END IF;
			END LOOP;
			
			IF controlExisPres THEN 
				FOR fila IN EXECUTE('SELECT id, inv_prod_id, inv_prod_presentacion_id FROM inv_exi_pres WHERE inv_prod_id='||str_data[4]::integer) LOOP
					exis:=0;
					SELECT count(id) FROM inv_prod_pres_x_prod WHERE producto_id=fila.inv_prod_id AND presentacion_id=fila.inv_prod_presentacion_id INTO exis;
					IF exis<=0 THEN 
						DELETE FROM inv_exi_pres WHERE id=fila.id;
					END IF;
				END LOOP;

				FOR fila IN EXECUTE('select id, inv_prod_id, inv_prod_presentacion_id from env_conf where inv_prod_id='||str_data[4]::integer) LOOP
					exis:=0;
					SELECT count(id) FROM inv_prod_pres_x_prod WHERE producto_id=fila.inv_prod_id AND presentacion_id=fila.inv_prod_presentacion_id INTO exis;
					IF exis<=0 THEN 
						DELETE FROM env_conf WHERE id=fila.id;
						DELETE FROM env_conf_det WHERE env_conf_id=fila.id;
					END IF;
				END LOOP;
			END IF;
			
				
			IF incluye_modulo_produccion=TRUE THEN 
				--para Producto 3=Kit
				IF tipo_producto=3 THEN
					--elimina los prod ingredientes de la tabla inv_kit
					DELETE FROM inv_kit  WHERE producto_kit_id = str_data[4]::integer;
					
					total_filas:= array_length(extra_data,1);
					cont_fila:=1;
					IF extra_data[1] != 'sin datos' THEN
						FOR cont_fila IN 1 .. total_filas LOOP
							SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
							--str_filas[1] eliminado
							IF str_filas[1]::integer != 0 THEN--1: no esta eliminado, 0:eliminado
								--ya no se valida nada
								--str_filas[1]	producto_elemento_id
								--str_filas[2] 	cantidad
								INSERT INTO inv_kit(producto_kit_id,producto_elemento_id,cantidad) VALUES (str_data[4]::integer,str_filas[1]::integer,str_filas[2]::double precision);
							END IF;
						END LOOP;
					END IF;
				END IF;	
			ELSE
				
				--Para Producto 1=TERMINADO, 2=INTERMEDIO, 3=KIT, 8=DESARROLLO
				IF tipo_producto=1 OR tipo_producto=2 OR tipo_producto=3 OR tipo_producto=8 THEN 
					--elimina los prod ingredientes de la tabla inv_kit
					DELETE FROM inv_kit  WHERE producto_kit_id=str_data[4]::integer;

					--RAISE EXCEPTION '%','extra_data: '||extra_data;
					
					total_filas:= array_length(extra_data,1);
					cont_fila:=1;
					IF extra_data[1] != 'sin datos' THEN
						FOR cont_fila IN 1 .. total_filas LOOP
							SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
							--str_filas[1] eliminado
							IF str_filas[1]::integer<>0 THEN--1: no esta eliminado, 0:eliminado
								--str_filas[1]	producto_elemento_id
								--str_filas[2] 	cantidad
								INSERT INTO inv_kit(producto_kit_id,producto_elemento_id,cantidad) VALUES (str_data[4]::integer,str_filas[1]::integer,str_filas[2]::double precision);
							END IF;
						END LOOP;
					END IF;
				END IF;	
			END IF;

			-- Guardar aliases (edicion de producto)
			DELETE FROM inv_prod_alias WHERE producto_id = str_data[4]::integer;

			cont_alias := 1;
			FOR cont_alias_idx IN 48 .. 57 LOOP
				IF str_data[cont_alias_idx] <> '' THEN
					INSERT INTO inv_prod_alias (producto_id, alias_id, descripcion) VALUES (str_data[4]::integer, cont_alias, str_data[cont_alias_idx]);
				END IF;
				cont_alias := cont_alias + 1;
			END LOOP;

			valor_retorno := '1';
		END IF;--termina edit producto
		
		
		
		IF command_selected = 'delete' THEN
			valor_retorno := '1';
			
			IF incluye_modulo_produccion=TRUE THEN
				--aqui buscamos si el producto es formulado
				SELECT count(inv_prod_id) FROM pro_estruc  WHERE inv_prod_id=str_data[4]::integer AND borrado_logico=FALSE INTO exis;
				IF exis > 0 THEN
					valor_retorno := '01';
				ELSE
					exis:=0; --inicializar variable
					--aqui buscamos si el producto forma parte de una formula
					SELECT count(pro_estruc_det.inv_prod_id) FROM pro_estruc_det JOIN pro_estruc ON pro_estruc.id=pro_estruc_det.pro_estruc_id WHERE pro_estruc_det.inv_prod_id=str_data[4]::integer  AND pro_estruc.borrado_logico=FALSE 
					INTO exis;
					
					IF exis > 0 THEN
						valor_retorno := '02';
					END IF;
				END IF;
				
				IF valor_retorno='1' THEN 
					--si el valor retorno sigue igual a 1, entonces tambien buscamos en la tabla de kits
					SELECT count(producto_elemento_id) FROM inv_kit WHERE producto_elemento_id=str_data[4]::integer INTO exis;
					IF exis > 0 THEN
						valor_retorno := '03';
					END IF;
				END IF;
			ELSE
				SELECT count(producto_elemento_id) FROM inv_kit WHERE producto_elemento_id=str_data[4]::integer INTO exis;
				IF exis > 0 THEN
					valor_retorno := '04';
				END IF;
			END IF;
			
			
			IF incluye_modulo_envasado=TRUE THEN 
				--verificamos que el producto no forme parte de una configuracion de envase
				exis:=0;
				SELECT count(env_conf_det.inv_prod_id) FROM env_conf JOIN  env_conf_det ON env_conf_det.env_conf_id=env_conf.id WHERE env_conf_det.inv_prod_id=str_data[4]::integer AND env_conf.borrado_logico=FALSE 
				INTO exis;
				IF exis > 0 THEN
					valor_retorno := '05';
				END IF;
			END IF;

			IF (select sum((inv_exi.exi_inicial - inv_exi.transito - inv_exi.reservado  + inv_exi.entradas_1 - inv_exi.salidas_1 + inv_exi.entradas_2 - inv_exi.salidas_2 + inv_exi.entradas_3 - inv_exi.salidas_3 + inv_exi.entradas_4 - inv_exi.salidas_4 + inv_exi.entradas_5 - inv_exi.salidas_5 + inv_exi.entradas_6 - inv_exi.salidas_6 + inv_exi.entradas_7 - inv_exi.salidas_7 + inv_exi.entradas_8 - inv_exi.salidas_8 + inv_exi.entradas_9 - inv_exi.salidas_9 + inv_exi.entradas_10 - inv_exi.salidas_10 + inv_exi.entradas_11 - inv_exi.salidas_11 + inv_exi.entradas_12 - inv_exi.salidas_12)) AS existencia FROM inv_exi WHERE inv_prod_id=str_data[4]::integer)>0.0001 THEN 
				--No se puede eliminar porque hay existencia en uno o mas almacenes
				valor_retorno := '06';
			END IF;
			
			
			--Si valor retorno es igual a 1, entonces procedemos a eliminar el producto
			IF valor_retorno='1' THEN 
				UPDATE inv_prod SET borrado_logico=true, momento_baja=now(), id_usuario_baja = usuario_id
				WHERE id = str_data[4]::integer;
				
				--elimina los registros de las formulacion del producto
				DELETE FROM inv_kit  WHERE producto_kit_id = str_data[4]::integer;
				
				--elimina los registros de las presentaciones del producto
				DELETE FROM inv_prod_pres_x_prod WHERE producto_id=str_data[4]::integer;
				
				DELETE FROM inv_prod_costos WHERE inv_prod_id=str_data[4]::integer AND ano=EXTRACT(YEAR FROM now());

				DELETE FROM inv_pre WHERE inv_prod_id=str_data[4]::integer AND gral_emp_id=emp_id;

				DELETE FROM inv_exi_pres WHERE inv_prod_id=str_data[4]::integer;
			END IF;
			
			--valor_retorno := '1';
		END IF;
	END IF;
	--termina catalogo de productos
	
	
	
	
	-- prefacturas
	IF app_selected = 13 THEN
		IF command_selected = 'new' THEN
			
			--str_data[3]	id_usuario
			--str_data[4]	id_prefactura
			--str_data[5] 	id_cliente
			--str_data[6]	moneda
			--str_data[7]	observaciones
			--str_data[8]	subtotal
			--str_data[9]	impuesto
			--str_data[10]	total
			--str_data[11]	tipo_cambio
			--str_data[12]	id_vendedor
			--str_data[13]	id_condiciones
			--str_data[14]	orden_compra
			--str_data[15]	refacturar
			--str_data[16]	id_metodo_pago
			--str_data[17]	no_cuenta
			--str_data[19]	tipo_documento
			
			
			--crea registro en tabla erp_proceso y retorna el id del registro creado
			INSERT INTO  erp_proceso(proceso_flujo_id,empresa_id,sucursal_id)VALUES(2, emp_id, suc_id) RETURNING id into ultimo_id_proceso;
			
			--crear registro en la tabla cotizacions y retorna el id del registro creado
			 INSERT INTO  erp_prefacturas(
				  cliente_id,
				  moneda_id,
				  observaciones,
				  subtotal,
				  impuesto,
				  total,
				  proceso_id,
				  tipo_cambio,
				  empleado_id,
				  terminos_id,
				  orden_compra,
				  refacturar,
				  fac_metodos_pago_id,
				  no_tarjeta,
				  id_usuario_creacion,
				  momento_creacion
			)
			VALUES(
				  str_data[5]::integer,
				  str_data[6]::integer,
				  str_data[7],
				  str_data[8]::double precision,
				  str_data[9]::double precision,
				  str_data[10]::double precision,
				  ultimo_id_proceso,
				  str_data[11]::double precision,
				  str_data[12]::integer,
				  str_data[13]::integer,
				  str_data[14],
				  str_data[15]::boolean,
				  str_data[16]::integer,
				  str_data[17],
				  str_data[3]::integer,
				  now()
			) RETURNING id into ultimo_id;
			
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			FOR cont_fila IN 1 .. total_filas LOOP
				SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
				
				--str_filas[1] eliminado
				IF str_filas[1]::integer != 0 THEN--1: no esta eliminado, 0:eliminado
					--str_filas[2]	iddetalle
					--str_filas[3]	idproducto
					--str_filas[4]	id_presentacion
					--str_filas[5]	id_impuesto
					--str_filas[6]	cantidad
					--str_filas[7]	costo
					
					--crea registros para tabla invfisico-detalles
					INSERT INTO erp_prefacturas_detalles(
						  producto_id,
						  presentacion_id,
						  tipo_impuesto_id,
						  cantidad,
						  precio_unitario,
						  prefacturas_id,
						  momento_creacion
					)VALUES(
						  str_filas[3]::integer,
						  str_filas[4]::integer,
						  str_filas[5]::integer,
						  str_filas[6]::double precision,
						  str_filas[7]::double precision,
						  ultimo_id,
						  now()
					);
					
					IF str_data[16]='true' THEN
						
						
					END IF;
					
				END IF;
			END LOOP;
			valor_retorno := '1';
		END IF;--termina nueva prefactura
		
		
		
		IF command_selected = 'edit' THEN
			--str_data[3]	id_usuario
			--str_data[4]	id_prefactura
			--str_data[5] 	id_cliente
			--str_data[6]	moneda
			--str_data[7]	observaciones
			--str_data[8]	subtotal
			--str_data[9]	impuesto
			--str_data[10]	total
			--str_data[11]	tipo_cambio
			--str_data[12]	id_vendedor
			--str_data[13]	id_condiciones
			--str_data[14]	orden_compra
			--str_data[15]	refacturar
			--str_data[16]	id_metodo_pago
			--str_data[17]	no_cuenta
			--str_data[19]	tipo_documento
			UPDATE erp_prefacturas SET 
				cliente_id = str_data[5]::integer, 
				moneda_id = str_data[6]::integer, 
				observaciones = str_data[7], 
				subtotal = str_data[8]::double precision, 
				impuesto = str_data[9]::double precision, 
				total = str_data[10]::double precision, 
				tipo_cambio = str_data[11]::double precision,
				empleado_id=str_data[12]::integer,
				terminos_id=str_data[13]::integer,
				orden_compra=str_data[14],
				refacturar=str_data[15]::boolean, 
				fac_metodos_pago_id=str_data[16]::integer,
				no_tarjeta=str_data[17],
				id_usuario_actualizacion = str_data[3]::integer,
				momento_actualizacion = now()
			WHERE id = str_data[4]::integer;
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			FOR cont_fila IN 1 .. total_filas LOOP
				SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
				
				--str_filas[1] 0:eliminado, 1:no eliminado
				IF str_filas[1]::integer != 0 THEN--1: no esta eliminado, 0:eliminado
					--str_filas[2]	iddetalle
					--str_filas[3]	idproducto
					--str_filas[4]	id_presentacion
					--str_filas[5]	id_impuesto
					--str_filas[6]	cantidad
					--str_filas[7]	costo
					
					--verifica si trae un id. Este id es el id del registro en la tabla cotizacions_detalles
					IF str_filas[2] !='0' THEN
						--RAISE EXCEPTION '%','No es nuevo';
						--actualiza registros en la tabla invfisico-detalles
						UPDATE erp_prefacturas_detalles SET cantidad = str_filas[6]::double precision,precio_unitario = str_filas[7]::double precision,tipo_impuesto_id = str_filas[5]::integer
						WHERE  id = str_filas[2]::integer  AND prefacturas_id = str_data[4]::integer;
					ELSE
						--RAISE EXCEPTION '%','Este si es nuevo es nuevo: '||str_filas[2];
						--crea nuevos registros
						INSERT INTO erp_prefacturas_detalles(prefacturas_id,producto_id,presentacion_id,tipo_impuesto_id,cantidad,precio_unitario,momento_creacion)
						VALUES(str_data[4]::integer,str_filas[3]::integer,str_filas[4]::integer,str_filas[5]::integer,str_filas[6]::double precision,str_filas[7]::double precision,now());
					END IF;
				ELSE
					--elimina registro que se elimino en el grid
					DELETE FROM erp_prefacturas_detalles where id = str_filas[2]::integer  AND prefacturas_id = str_data[4]::integer;
				END IF;
			END LOOP;
			valor_retorno := '1';
		END IF;--termina edit prefactura
		
		IF command_selected = 'delete' THEN
			UPDATE erp_prefacturas SET borrado_logico = true, momento_baja = now(),id_usuario_baja = str_data[3]::integer
			where id = str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
		
	END IF;--termina prefacturas
	
	
	-- pagos
	IF app_selected = 14 THEN

		id_tipo_consecutivo:=11;--Numero de transaccion pago CXC
		
		--aqui entra para tomar el consecutivo del folio  la sucursal actual
		UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
		WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
		
		--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
		--nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

		
		--nuevo folio transaccion
		folio_transaccion := nuevo_consecutivo::bigint;
		
		
		IF command_selected = 'pago' THEN
			--obtiene id de la forma de pago
			SELECT id FROM erp_pagos_formas WHERE titulo ILIKE str_data[11] LIMIT 1 into id_forma_pago;
			
			id_anticipo := 0;
			monto_anticipo_actual:=0;
			--obtener id del anticipo
			SELECT COUNT(id) FROM cxc_ant WHERE numero_transaccion = str_data[23]::bigint AND cliente_id=str_data[4]::integer and borrado_logico=false INTO rowCount;
			IF rowCount > 0 THEN
				SELECT id, anticipo_actual FROM cxc_ant WHERE numero_transaccion = str_data[23]::bigint AND cliente_id = str_data[4]::integer   LIMIT 1 
				INTO id_anticipo, monto_anticipo_actual;
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
				)
			VALUES (folio_transaccion,
					now(),
					id_forma_pago,
					str_data[12],
					str_data[13],
					str_data[14],
					str_data[10],
					str_data[3]::integer,
					str_data[9]::integer,
					str_data[7]::integer,
					str_data[16]::double precision,
					str_data[4]::integer,
					str_data[17]::timestamp with time zone,
					str_data[19]::integer,
					str_data[18],
					str_data[20]::integer,
					str_data[21]::double precision,
					id_anticipo,
					emp_id, 
					suc_id
				) RETURNING id into ultimo_id;
				
			--str_data[3]	id_usuario
			--str_data[4]	cliente_id
			--str_data[5]	deuda_pesos
			--str_data[6]	deuda_usd
			--str_data[7]	moneda
			--str_data[8]	fecha+" "+hora+":"+minutos+":"+segundos
			--str_data[9]	banco
			--str_data[10]	observaciones
			--str_data[11]	forma_pago
			--str_data[12]	cheque
			--str_data[13]	referencia
			--str_data[14]	tarjeta
			--str_data[15]	antipo
			--str_data[16]	monto_pago
			--str_data[17]	fecha_deposito
			--str_data[18]	ficha_movimiento_deposito
			--str_data[19]	ficha_cuenta_deposito
			--str_data[20]	ficha_banco_kemikal
			--str_data[21]	tipo_cambio
			--str_data[22]	anticipo_gastado
			--str_data[23]	no_transaccion_anticipo
			--str_data[24]	saldo_a_favor
			
			--RAISE EXCEPTION '%','Si llega aqui: id_pago: '||ultimo_id_pago;
			--------------------saldando facturas--------------------------------
			--SELECT INTO item string_to_array(''||valores||'','&');
			
			SELECT INTO veces array_upper(extra_data,1);
			WHILE incrementa <= veces LOOP
				SELECT INTO iterar string_to_array(extra_data[incrementa],'___');
				--RAISE EXCEPTION '%', iterar[1];
				--iterar[1]	factura_vista
				--iterar[2]	saldado
				--iterar[3]	saldo
				--iterar[4]	tipocambio(este tipo de cambio no se utiliza, el tipo de cambio esta en pagos)
				
				SELECT moneda_id FROM fac_docs WHERE serie_folio=iterar[1] INTO id_moneda_factura;
				
				INSERT INTO erp_pagos_detalles (pago_id,serie_folio,cantidad,momento_pago,fac_moneda_id) 
				VALUES (ultimo_id,iterar[1],iterar[3]::double precision,str_data[8]::timestamp with time zone, id_moneda_factura);
				
				IF iterar[2]::boolean = true THEN
					UPDATE erp_h_facturas SET pagado=true WHERE serie_folio = iterar[1]::character varying;
					UPDATE fac_cfds SET pagado=true WHERE serie_folio ilike iterar[1]::character varying;
					--UPDATE erp_notacargos SET pagado=true WHERE serie_folio ilike iterar[1]::character varying;
				END IF;
				incrementa:= 1 + incrementa;
			END LOOP;

			sql_pagos:='SELECT serie_folio,cantidad FROM erp_pagos_detalles WHERE pago_id = '||ultimo_id;
			--RAISE EXCEPTION '%','Si llega aqui: sql-pagos: '||string_pagos;
			
			FOR fila IN EXECUTE(sql_pagos) LOOP
				EXECUTE 'SELECT monto_total,total_pagos	from  erp_h_facturas where serie_folio ilike '''||fila.serie_folio||'''' 
				INTO total_factura,monto_pagos;

				--sacar suma total de pagos para esta factura
				SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END  from(	SELECT sum(cantidad) FROM erp_pagos_detalles WHERE serie_folio=fila.serie_folio AND cancelacion=FALSE) AS sbt  INTO suma_pagos;

				--sacar suma total de notas de credito para esta factura
				--SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END FROM (SELECT sum(total) FROM fac_nota_credito WHERE serie_folio_factura=fila.serie_folio AND cancelado=FALSE) AS subtabla INTO suma_notas_credito;
				SELECT total_notas_creditos FROM erp_h_facturas WHERE serie_folio=fila.serie_folio INTO suma_notas_credito;
				
				nuevacantidad_monto_pago:=round((suma_pagos)::numeric,4)::double precision;
				nuevo_saldo_factura:=round((total_factura-suma_pagos-suma_notas_credito)::numeric,4)::double precision;
				
				--actualiza cantidades cada vez que se realice un pago
				UPDATE erp_h_facturas SET 
					total_pagos=nuevacantidad_monto_pago, 
					total_notas_creditos=suma_notas_credito,
					saldo_factura=nuevo_saldo_factura, 
					momento_actualizacion=now(),
					fecha_ultimo_pago=str_data[17]::date
				WHERE serie_folio=fila.serie_folio;
			END LOOP;
			
			--Inicia guardar saldos a favor y actualizar anticipos
			IF id_anticipo !=0 THEN
				--aqui entra porque el pago es de un anticipo y actualiza cantidades del anticipo
				IF monto_anticipo_actual >= str_data[22]::double precision THEN
					saldo_anticipo := monto_anticipo_actual - str_data[22]::double precision;
					IF saldo_anticipo <=0 THEN
					    UPDATE cxc_ant SET anticipo_actual=0, borrado_logico = true, id_usuario_actualizacion = str_data[3]::integer, momento_baja = now()
					    WHERE cliente_id = str_data[4]::integer AND id = id_anticipo;
					ELSE
					    UPDATE cxc_ant SET anticipo_actual = saldo_anticipo, id_usuario_baja = str_data[3]::integer, momento_actualizacion = now()
					    WHERE cliente_id = str_data[4]::integer AND id = id_anticipo;
					END IF;
				END IF;
			ELSE
				--Aqui entra porque el pago no es de un anticipo
				IF str_data[24]::double precision > 0 THEN
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
						str_data[4]::integer,
						str_data[7]::integer,
						str_data[24]::double precision,
						str_data[24]::double precision,
						now(),str_data[3]::integer,
						emp_id,
						suc_id	
					);
					*/
					--Aquí se genera una nuevo numero de transaccion para el anticipo des saldo a favor
					--aqui entra para tomar el consecutivo del folio  la sucursal actual
					UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
					WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
										--nuevo folio transaccion
					folio_transaccion := nuevo_consecutivo::bigint;

					INSERT INTO cxc_ant(
						numero_transaccion,--folio_transaccion,
						cliente_id,--str_data[4]::integer,
						moneda_id,--str_data[7]::integer,
						anticipo_inicial,--str_data[24]::double precision,
						anticipo_actual,--str_data[24]::double precision,
						fecha_anticipo_usuario,--now(),
						observaciones,--'ANTICIPO GENERADO DESDE UN PAGO COMO SALDO A FAVOR DEL CLIENTE'
						momento_creacion,--now(),
						id_usuario_creacion,--str_data[3]::integer,
						empresa_id,--emp_id,
						sucursal_id--suc_id
					)
					VALUES(
						folio_transaccion,
						str_data[4]::integer,
						str_data[7]::integer,
						str_data[24]::double precision,
						str_data[24]::double precision,
						now(),
						'ANTICIPO GENERADO DESDE UN PAGO COMO SALDO A FAVOR DEL CLIENTE',
						now(),
						str_data[3]::integer,
						emp_id,
						suc_id					
					);
				END IF;
			
			END IF;
			
			valor_retorno = folio_transaccion::character varying||'___'||ultimo_id::character varying;
		END IF;--termina registro de pago
		
		
		
		--registro de anticipo
		IF command_selected = 'anticipo' THEN
			
			SELECT INTO str_data string_to_array(''||campos_data||'','___');	
			--str_data[3] 	id_usuario
			--str_data[4] 	fecha_anticipo
			--str_data[5] 	monto_anticipo
			--str_data[6] 	id_moneda
                        --str_data[7]   id_cliente
                        --str_data[8]   observaciones
                                
			INSERT INTO cxc_ant(
				numero_transaccion,
				cliente_id,
				moneda_id,
				anticipo_inicial,
				anticipo_actual,
				fecha_anticipo_usuario,
				observaciones,
				momento_creacion,
				id_usuario_creacion,
				empresa_id,
				sucursal_id
			)
			VALUES(
				folio_transaccion,
				str_data[7]::integer,
				str_data[6]::integer,
				str_data[5]::double precision,
				str_data[5]::double precision,
				str_data[4]::timestamp with time zone,
				str_data[8],
				now(),
				str_data[3]::integer,
				emp_id,
				suc_id
			);
			valor_retorno := folio_transaccion::character varying;			
		END IF;--termina registro de anticipo
		
		
		
		--cancelacion de pagos
		IF command_selected = 'cancelacion' THEN
			--str_data[3]	id_usuario
			--str_data[4]	cancelar_por
			--str_data[5]	observaciones_canc
			--str_data[6]	numero_trans
			--str_data[7]	fecha_cancelacion

			--folio_transaccion:=str_data[6]::bigint;
			
			--TIPO CANCELACION POR FACTURAS
			IF str_data[4]='1' THEN
				id_anticipo:=0;
				--obtiene id del pago y id de anticipo
				SELECT id,anticipo_id,tipo_cambio FROM erp_pagos WHERE numero_transaccion=str_data[6]::bigint ORDER BY id DESC LIMIT 1 INTO id_pago,id_anticipo, tipo_cambio_pago;
				--RAISE EXCEPTION '%','monto cancelado: SELECT id,anticipo_id FROM erp_pagos WHERE numero_transaccion ILIKE '||str_data[3];
				total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
				cont_fila:=1;
				FOR cont_fila IN 1 .. total_filas LOOP
					--arreglo[cont_fila] serie_folio
					--RAISE EXCEPTION '%','SERIE_FOLIO: '||extra_data[cont_fila]||'   id_pago:'||id_pago;
					
					--actualiza tabla erp_pagos_detalles
					UPDATE erp_pagos_detalles SET cancelacion = true, momento_cancelacion = str_data[7]::timestamp with time zone 
					WHERE pago_id = id_pago AND serie_folio ilike extra_data[cont_fila] RETURNING id,cantidad into id_pagos_detalles,monto_cancelado;
					
					--verificar monto_cancelado
					--RAISE EXCEPTION '%','monto cancelado: '||monto_cancelado;
					
					SELECT monto_total,total_pagos_cancelados,moneda_id from  erp_h_facturas where serie_folio=extra_data[cont_fila]
					INTO total_factura,total_monto_cancelados,id_moneda_factura;
					
					SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END  from(	SELECT sum(cantidad) FROM erp_pagos_detalles WHERE serie_folio=extra_data[cont_fila] AND cancelacion=FALSE ) AS sbt  INTO suma_pagos;

					--sacar total de notas de credito
					SELECT total_notas_creditos FROM erp_h_facturas WHERE serie_folio=extra_data[cont_fila] INTO suma_notas_credito;
					
					nuevacantidad_monto_cancelados:=round((total_monto_cancelados + monto_cancelado)::numeric,4)::double precision;
					--nuevo_saldo_factura:=round((total_factura - suma_pagos)::numeric,4)::double precision;
					nuevo_saldo_factura:=round((total_factura-suma_pagos-suma_notas_credito)::numeric,4)::double precision;

					
					--RAISE EXCEPTION '%','TotFact='||total_factura||' SaldoFact='||saldo_factura||' TotalCan='||total_monto_cancelados||' NuevoTotCan='||nuevacantidad_monto_cancelados;
					
					--actualiza cantidades cada vez que se realice una cancelacion
					UPDATE erp_h_facturas SET 
						total_pagos = suma_pagos,
						total_notas_creditos=suma_notas_credito,
						total_pagos_cancelados = nuevacantidad_monto_cancelados, 
						saldo_factura=nuevo_saldo_factura,
						pagado = false
					where serie_folio ilike extra_data[cont_fila]
					RETURNING moneda_id into id_moneda_factura;
					
					UPDATE fac_cfds SET pagado=false WHERE serie_folio=extra_data[cont_fila];
					
					INSERT INTO  erp_pagos_cancelacion_detalles(pagos_detalles_id,numero_transaccion,momento_creacion_usuario,id_usuario_creacion,observaciones,momento_creacion)
					VALUES( id_pagos_detalles,folio_transaccion, str_data[7]::timestamp with time zone, str_data[3]::integer, str_data[5], now());
					
					
					--actualiza anticipos al cancelar un pago que fue originado de un anticipo
					IF id_anticipo !=0 THEN
						SELECT anticipo_actual,moneda_id FROM cxc_ant WHERE id = id_anticipo INTO monto_anticipo_actual, id_moneda_anticipo;
						
						IF id_moneda_factura = 1 THEN 
							IF id_moneda_anticipo = 2 THEN 
								saldo_anticipo:=monto_anticipo_actual + (monto_cancelado / tipo_cambio_pago);
							END IF;
							IF id_moneda_anticipo = 1 THEN 
								saldo_anticipo:=monto_anticipo_actual + monto_cancelado;
							END IF;
						END IF;
						IF id_moneda_factura = 2 THEN 
							IF id_moneda_anticipo = 2 THEN 
								saldo_anticipo:=monto_anticipo_actual + monto_cancelado;
							END IF;
							IF id_moneda_anticipo = 1 THEN 
								saldo_anticipo:=monto_anticipo_actual + (monto_cancelado * tipo_cambio_pago);
							END IF;
						END IF;
						
						--RAISE EXCEPTION '%','saldo_anticipo: '||saldo_anticipo;
						UPDATE cxc_ant SET anticipo_actual = saldo_anticipo,
									id_usuario_actualizacion = str_data[3]::integer, 
									momento_actualizacion = now(),
									borrado_logico = false,
									momento_baja = null
						WHERE id = id_anticipo;
					END IF;
					
				END LOOP;
				valor_retorno:=folio_transaccion::character varying;
				--RAISE EXCEPTION '%','valor_retorno: '||valor_retorno;
			END IF;--TERMINA TIPO CANCELACION POR FACTURAS
			
			
			
			
			--TIPO CANCELACION POR NUMERO DE TRANSACCION
			IF str_data[4]='2' THEN
				--obtiene id del pago
				
				total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
				cont_fila:=1;
				FOR cont_fila IN 1 .. total_filas LOOP
					id_anticipo:=0;
					--extra_data[cont_fila] numero de transaccion
					SELECT id,anticipo_id,tipo_cambio FROM erp_pagos WHERE numero_transaccion = extra_data[cont_fila]::bigint LIMIT 1 INTO id_pago,id_anticipo,tipo_cambio_pago;
					--RAISE EXCEPTION '%','id_pago: '||id_pago;
					
					--cancela todas las facturas pagadas con esta transaccion
					UPDATE erp_pagos_detalles SET cancelacion = true, momento_cancelacion = str_data[7]::timestamp with time zone 
					WHERE pago_id = id_pago;
					
					
					sql_pagos:='SELECT id,serie_folio, cantidad FROM erp_pagos_detalles WHERE  erp_pagos_detalles.pago_id = '||id_pago;
					
					FOR fila IN EXECUTE (sql_pagos) LOOP
						SELECT monto_total,total_pagos_cancelados,moneda_id from  erp_h_facturas where serie_folio=fila.serie_folio
						INTO total_factura,total_monto_cancelados,id_moneda_factura;
						
						--RAISE EXCEPTION '%','total_factura '||total_factura;
						--RAISE EXCEPTION '%','total_monto_cancelados: '||total_monto_cancelados;
						
						SELECT CASE WHEN sum IS NULL THEN 0 ELSE sum END  from(	SELECT sum(cantidad) FROM erp_pagos_detalles WHERE serie_folio = fila.serie_folio AND cancelacion=FALSE ) AS sbt  INTO suma_pagos;
						--RAISE EXCEPTION '%','suma_pagos '||suma_pagos;

						SELECT total_notas_creditos FROM erp_h_facturas WHERE serie_folio=fila.serie_folio INTO suma_notas_credito;
						
						nuevacantidad_monto_cancelados:=round((total_monto_cancelados + fila.cantidad)::numeric,4)::double precision;
						--nuevo_saldo_factura:=round((total_factura - suma_pagos)::numeric,4)::double precision;
						nuevo_saldo_factura:=round((total_factura-suma_pagos-suma_notas_credito)::numeric,4)::double precision;
						
						
						--actualiza cantidades cada vez que se realice una cancelacion
						UPDATE erp_h_facturas SET 
							total_pagos = suma_pagos,
							total_notas_creditos=suma_notas_credito,
							total_pagos_cancelados = nuevacantidad_monto_cancelados, 
							saldo_factura=nuevo_saldo_factura,
							pagado = false
						where serie_folio ilike fila.serie_folio
						RETURNING moneda_id into id_moneda_factura;
						
						UPDATE fac_cfds SET pagado=false WHERE serie_folio ilike fila.serie_folio;
						
						
						INSERT INTO  erp_pagos_cancelacion_detalles(pagos_detalles_id,numero_transaccion,momento_creacion_usuario,id_usuario_creacion,observaciones,momento_creacion)
						VALUES( fila.id, folio_transaccion, str_data[7]::timestamp with time zone, str_data[3]::integer, str_data[5], now());
						
						--actualiza anticipos al cancelar un pago que fue originado de un anticipo
						IF id_anticipo !=0 THEN
							--SELECT anticipo_actual FROM cxc_ant WHERE id = id_anticipo INTO monto_anticipo_actual;
							SELECT anticipo_actual,moneda_id FROM cxc_ant WHERE id = id_anticipo INTO monto_anticipo_actual, id_moneda_anticipo;
							
							IF id_moneda_factura = 1 THEN 
								IF id_moneda_anticipo = 2 THEN 
									saldo_anticipo:=monto_anticipo_actual + (fila.cantidad / tipo_cambio_pago);
								END IF;
								IF id_moneda_anticipo = 1 THEN 
									saldo_anticipo:=monto_anticipo_actual + fila.cantidad;
								END IF;
							END IF;
							IF id_moneda_factura = 2 THEN 
								IF id_moneda_anticipo = 2 THEN 
									saldo_anticipo:=monto_anticipo_actual + fila.cantidad;
								END IF;
								IF id_moneda_anticipo = 1 THEN 
									saldo_anticipo:=monto_anticipo_actual + (fila.cantidad * tipo_cambio_pago);
								END IF;
							END IF;
							
							--RAISE EXCEPTION '%','moneda_factura:'||id_moneda_factura||'  moneda_anticipo:'||id_moneda_anticipo||'  monto_anticipo_actual:'||monto_anticipo_actual||'  monto_cancelado:'||fila.cantidad||'  saldo_anticipo:'||saldo_anticipo; 
							
							UPDATE cxc_ant SET anticipo_actual = saldo_anticipo,
										id_usuario_actualizacion = str_data[3]::integer, 
										momento_actualizacion = now(),
										borrado_logico = false,
										momento_baja = null
							WHERE id = id_anticipo;
						END IF;
						
					END LOOP;
					--RAISE EXCEPTION '%','TotFact='||total_factura||' SaldoFact='||saldo_factura||' TotalCan='||total_monto_cancelados||' NuevoTotCan='||nuevacantidad_monto_cancelados;
					
					--valor_retorno:= valor_retorno||extra_data[cont_fila]||',';
					
					--RAISE EXCEPTION '%','valor_retorno: '||valor_retorno;
					
				END LOOP;
				valor_retorno:= folio_transaccion::character varying;
				
			END IF;--TERMINA TIPO CANCELACION POR NUMERO DE TRANSACCION
			
		END IF;--termina termina cancelacion de pagos
		
	END IF;--termina pagos
	
	
	
	
	-- Catalogo de centros de costo
	IF app_selected = 15 THEN
		IF command_selected = 'new' THEN
			--str_data[4] 	id
			--str_data[5] 	titulo
			--str_data[6] 	descripcion
			INSERT INTO ctb_cc (titulo,descripcion, momento_creacion, id_usuario_creacion,empresa_id,borrado_logico)
			VALUES (str_data[5],str_data[6], now(),usuario_id, emp_id,false);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE ctb_cc SET titulo=str_data[5],descripcion=str_data[6],id_usuario_actualizacion=usuario_id,momento_actualizacion=now(),borrado_logico=false
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE ctb_cc SET borrado_logico=true, momento_baja=now(), id_usuario_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina catalogo de centros de costo
	
	
	-- Catalogo de centros de Tipos de Poliza
	IF app_selected = 16 THEN
		IF command_selected = 'new' THEN
			--str_data[4] 	id
			--str_data[5] 	tipo
			--str_data[6] 	descripcion
			--str_data[7] 	grupo
			INSERT INTO ctb_tpol (titulo, ctb_tpol_grupo_id, tipo, empresa_id, borrado_logico, momento_creacion, id_usuario_creacion)
			VALUES (str_data[6], str_data[7]::integer, str_data[5]::integer, emp_id, false,now(),usuario_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE ctb_tpol SET titulo=str_data[6],ctb_tpol_grupo_id=str_data[7]::integer, tipo=str_data[5]::integer,id_usuario_actualizacion=usuario_id,momento_actualizacion=now()
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE ctb_tpol SET borrado_logico=true, momento_baja=now(), id_usuario_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina catalogo de Tipos de poliza
	
	
	
	-- Catalogo de conceptos contables
	IF app_selected = 17 THEN
		IF command_selected = 'new' THEN
			--str_data[4] 	id
			--str_data[5] 	titulo
			--str_data[6] 	descripcion
			INSERT INTO ctb_con (titulo,descripcion, momento_creacion, id_usuario_creacion,empresa_id,borrado_logico)
			VALUES (str_data[5],str_data[6], now(),usuario_id, emp_id,false);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE ctb_con SET titulo=str_data[5],descripcion=str_data[6],id_usuario_actualizacion=usuario_id,momento_actualizacion=now(),borrado_logico=false
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE ctb_con SET borrado_logico=true, momento_baja=now(), id_usuario_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina catalogo de conceptos contables
	
	
	-- Catalogo de Clasificacion de Cuentas(Cuentas de Mayor)
	IF app_selected = 18 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	ctamayor
			--str_data[6]	clasificacion
			--str_data[7]	des_espanol
			--str_data[8]	des_ingles
			--str_data[9]	des_otro
			INSERT INTO ctb_may (ctb_may_clase_id,clasificacion,descripcion,descripcion_ing,descripcion_otr,borrado_logico,	empresa_id,momento_creacion,id_usuario_creacion)
			VALUES (str_data[5]::integer,str_data[6]::smallint, str_data[7], str_data[8], str_data[9], false,emp_id,now(),usuario_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE ctb_may SET ctb_may_clase_id=str_data[5]::integer,clasificacion=str_data[6]::smallint, descripcion=str_data[7],descripcion_ing=str_data[8],descripcion_otr=str_data[9],id_usuario_actualizacion=usuario_id,momento_actualizacion=now(),borrado_logico=false
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE ctb_may SET borrado_logico=true, momento_baja=now(), id_usuario_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina catalogo de Clasificacion de Cuentas(Cuentas de Mayor)
	
	
	
	-- Catalogo de Catalogo de Agentes
	IF app_selected = 19 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	nombre_agente
			--str_data[6]	usuario_agente
			--str_data[7]	comision
			--str_data[8]	region

			INSERT INTO cxc_agen(nombre, comision, gral_reg_id,gral_usr_id,borrado_logico,momento_creacion,gral_usr_id_creacion)
			VALUES(str_data[5],str_data[7]::double precision,str_data[8]::integer,str_data[6]::integer,false,now(),usuario_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE cxc_agen SET nombre=str_data[5],
						comision=str_data[7]::double precision,
						gral_reg_id=str_data[8]::integer,
						gral_usr_id=str_data[6]::integer,
						gral_usr_id_actualizacion=usuario_id,
						momento_actualizacion=now()
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE cxc_agen SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de Agentes
	
	
	-- Catalogo de Catalogo Clientes Clasificacion 1
	IF app_selected = 20 THEN
		IF command_selected = 'new' THEN
			--str_data[4] 	id
			--str_data[5] 	titulo
			INSERT INTO cxc_clie_clas1 (titulo) VALUES (str_data[5]);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE cxc_clie_clas1 SET titulo=str_data[5] WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			DELETE FROM cxc_clie_clas1 WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Clientes Clasificacion 1
	

	-- Catalogo de Catalogo Clientes Clasificacion 2
	IF app_selected = 21 THEN
		IF command_selected = 'new' THEN
			--str_data[4] 	id
			--str_data[5] 	titulo
			INSERT INTO cxc_clie_clas2 (titulo) VALUES (str_data[5]);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE cxc_clie_clas2 SET titulo=str_data[5] WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			DELETE FROM cxc_clie_clas2 WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Clientes Clasificacion 2
	
	
	-- Catalogo de Catalogo Clientes Clasificacion 3
	IF app_selected = 22 THEN
		IF command_selected = 'new' THEN
			--str_data[4] 	id
			--str_data[5] 	titulo
			INSERT INTO cxc_clie_clas3 (titulo) VALUES (str_data[5]);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE cxc_clie_clas3 SET titulo=str_data[5] WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			DELETE FROM cxc_clie_clas3 WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Clientes Clasificacion 3

	
	
	-- Catalogo de Zonas de  Clientes
        IF app_selected = 23 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxc_clie_zonas (titulo,borrado_logico) VALUES (str_data[5],false);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxc_clie_zonas SET titulo=str_data[5] WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        --DELETE FROM cxc_clie_zonas WHERE id=str_data[4]::integer;
                        UPDATE cxc_clie_zonas
                        SET borrado_logico = true
                        WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo zonas de  Clientes
        

        -- Catalogo de  Grupos de  Clientes
        IF app_selected = 24 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxc_clie_grupos (titulo,borrado_logico) VALUES (str_data[5],false);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxc_clie_grupos SET titulo=str_data[5] WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        --DELETE FROM cxc_clie_grupos WHERE id=str_data[4]::integer;
                        UPDATE cxc_clie_grupos
                        SET borrado_logico = true
                        WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Grupos de  Clientes

	
	
	-- Catalogo de Catalogo Proveedores Clasificacion 1
        IF app_selected = 25 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxp_prov_clas1 (titulo, borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
                        VALUES (str_data[5], false, now(),usuario_id, emp_id, suc_id );
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_prov_clas1 SET titulo=str_data[5], momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
			UPDATE cxp_prov_clas1 SET borrado_logico=true, momento_baja=now(),gral_usr_id_baja=usuario_id 
			WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Proveedores Clasificacion 1
	
	
        -- Catalogo de Catalogo Proveedores Clasificacion 2
        IF app_selected = 26 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxp_prov_clas2 (titulo, borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
                        VALUES (str_data[5], false, now(),usuario_id, emp_id, suc_id);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_prov_clas1 SET titulo=str_data[5], momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
			UPDATE cxp_prov_clas1 SET borrado_logico=true, momento_baja=now(),gral_usr_id_baja=usuario_id 
			WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Proveedores Clasificacion 2
	
	
	
        -- Catalogo de Catalogo Proveedores Clasificacion 3
        IF app_selected = 27 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxp_prov_clas3 (titulo, borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
                        VALUES (str_data[5], false, now(), usuario_id, emp_id, suc_id);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_prov_clas1 SET titulo=str_data[5], momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
			UPDATE cxp_prov_clas1 SET borrado_logico=true, momento_baja=now(),gral_usr_id_baja=usuario_id 
			WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Proveedores Clasificacion 3

	-- Catalogo de Zonas de  Proveedores
        IF app_selected = 28 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxp_prov_zonas (titulo) VALUES (str_data[5]);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_prov_zonas SET titulo=str_data[5] WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        DELETE FROM cxp_prov_zonas WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo zonas de  proveedores
        
	
	
        -- Catalogo de  Grupos de  Proveedores
        IF app_selected = 29 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        INSERT INTO cxp_prov_grupos (titulo) VALUES (str_data[5]);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_prov_grupos SET titulo=str_data[5] WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        DELETE FROM cxp_prov_grupos WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Grupos de  proveedores
	
	
	
	
	
	-- Catalogo de tipos de movimientos  de  Proveedores
        IF app_selected = 31 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        --str_data[6]         descripcion
                        --str_data[7]         moneda_id
                        INSERT INTO cxp_mov_tipos (titulo,moneda_id,descripcion) VALUES (str_data[5],str_data[7]::integer,str_data[6]);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_mov_tipos SET titulo=str_data[5],descripcion=str_data[6],moneda_id=str_data[7]::integer
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        DELETE FROM cxp_mov_tipos WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo tipos de  movimientos de proveedores
        
        -- Catalogo de tipos de movimientos  de  clientes
	IF app_selected = 32 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        --str_data[6]         descripcion
                        --str_data[7]         moneda_id
                        INSERT INTO cxc_mov_tipos (titulo,moneda_id,descripcion) VALUES (str_data[5],str_data[7]::integer,str_data[6]);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxc_mov_tipos SET titulo=str_data[5],descripcion=str_data[6],moneda_id=str_data[7]::integer 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        DELETE FROM cxc_mov_tipos WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo tipos de movimientos  de  clientes
	
	
        -- Catalogo de tipos de mensajes  de  clientes
        IF app_selected = 33 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         titulo
                        --str_data[6]         descripcion
                        --str_data[7]         moneda_id
                        INSERT INTO cxc_clie_mensajes (cxc_mov_tipo_id,cxc_clie_id,msg_1) VALUES (str_data[4]::integer,str_data[5]::integer,str_data[6]);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxc_clie_mensajes SET cxc_mov_tipo_id=str_data[4],cxc_clie_id=str_data[5] ,msg_1=str_data[6] 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        DELETE FROM cxc_mov_tipos WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo tipos de  mensajes de clientes
	
	
	
	
	
	-- Catalogo de Catalogo de tipos de movimientos de inventarios
	IF app_selected = 35 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	tipo
			--str_data[6]	descripcion
			--str_data[7]	mov_de_ajuste

			INSERT INTO inv_mov_tipos(titulo, descripcion, ajuste,momento_creacion, borrado_logico, grupo, afecta_compras, 
			afecta_ventas, considera_consumo, tipo_costo)
			VALUES(str_data[5],str_data[6],str_data[7]::boolean,now(), false, str_data[8]::smallint,str_data[9]::boolean,
			str_data[10]::boolean,str_data[11]::boolean,str_data[12]::smallint);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE inv_mov_tipos SET titulo=str_data[5],
						descripcion=str_data[6],
						ajuste=str_data[7]::boolean,
						momento_actualizacion=now(), 
						grupo=str_data[8]::smallint, 
						afecta_compras=str_data[9]::boolean, 
						afecta_ventas=str_data[10]::boolean, 
						considera_consumo=str_data[11]::boolean, 
						tipo_costo=str_data[12]::smallint
						
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_mov_tipos SET borrado_logico=true, momento_baja=now() WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de tipos de movimientos de inventarios

	-- Catalogo de Catalogo de invsecciones
	IF app_selected = 37 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			--str_data[7]	estatus

			INSERT INTO inv_secciones(titulo, descripcion, activa,momento_creacion, borrado_logico,gral_usr_id_creacion,gral_emp_id,gral_suc_id)
			VALUES(str_data[5],str_data[6],str_data[7]::boolean,now(), false, usuario_id, emp_id, suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE inv_secciones SET titulo=str_data[5],
						descripcion=str_data[6],
						activa=str_data[7]::boolean,
						momento_actualizacion=now(),
						gral_usr_id_actualizacion=usuario_id
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_secciones SET borrado_logico=true, momento_baja=now(),gral_usr_id_baja=usuario_id
			WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de invsecciones

	-- Catalogo de inventario de  Marcas
	IF app_selected = 38 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         titulo
			--str_data[6]     estatus
			--strd_data[7]    url
			INSERT INTO inv_mar (titulo,borrado_logico,url,momento_creacion,estatus, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
			VALUES (str_data[5],false,str_data[7],now(),str_data[6]::boolean,  usuario_id, emp_id, suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE inv_mar SET titulo=str_data[5],
					    estatus=str_data[6]::boolean,
					    momento_actualizacion=now(),
					    gral_usr_id_actualizacion=usuario_id
			WHERE inv_mar.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_mar SET momento_baja=now(),
					    borrado_logico=true,
					    gral_usr_id_baja=usuario_id
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Marcas

	-- Catalogo de inv_prod_lineas
	IF app_selected = 39 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			--str_data[7]	seccion
			--str_data[8]	marcas
			
			INSERT INTO inv_prod_lineas(titulo, descripcion, inv_seccion_id,momento_creacion, borrado_logico,gral_usr_id_creacion, gral_emp_id, gral_suc_id)
			VALUES(str_data[5],str_data[6],str_data[7]::integer,now(), false, usuario_id, emp_id, suc_id) 
			RETURNING id INTO ultimo_id;
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			
			IF extra_data[1] != 'sin datos' THEN
				--RAISE EXCEPTION '%' ,extra_data[cont_fila]::integer;
				FOR cont_fila IN 1 .. total_filas LOOP
					insert into inv_lm(inv_prod_linea_id,inv_mar_id) 
					values(ultimo_id, extra_data[cont_fila]::integer);
				END LOOP;
			END IF;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			
			UPDATE inv_prod_lineas SET titulo=str_data[5],
						descripcion=str_data[6],
						inv_seccion_id=str_data[7]::integer,
						momento_actualizacion=now(),
						gral_usr_id_actualizacion=usuario_id
			WHERE id = str_data[4]::integer;

			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			IF extra_data[1] != 'sin datos' THEN
				delete from inv_lm where inv_prod_linea_id=str_data[4]::integer;
				FOR cont_fila IN 1 .. total_filas LOOP
					insert into inv_lm(inv_prod_linea_id,inv_mar_id) values(str_data[4]::integer, extra_data[cont_fila]::integer);
				END LOOP;
			END IF;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_prod_lineas SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id  WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;----termina Catalogo de inv_prod_lineas

	-- Catalogo de  Zonas de invetarios
	IF app_selected = 40 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         descripcion
			--str_data[6]     estatus
			--strd_data[7]    zona
			INSERT INTO inv_zonas (titulo,descripcion,borrado_logico,momento_creacion,estatus) VALUES (str_data[7],str_data[5],false,now(),str_data[6]::boolean);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
		
			UPDATE inv_zonas SET titulo=str_data[7],						
						estatus=str_data[6]::boolean,
						descripcion=str_data[5],
						momento_actualizacion=now()
						WHERE inv_zonas.id = str_data[4]::integer;
						valor_retorno := '1';
		END IF;
			
		IF command_selected = 'delete' THEN
			 UPDATE inv_zonas SET momento_baja=now(),
					    borrado_logico=true
					    WHERE id = str_data[4]::integer;
					    valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Zonas de invetarios
	
	
	
	
	
	-- Catalogo de tes_mov_tipos
	IF app_selected = 41 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			--str_data[7]	grupo--int
			--str_data[8]	tipo
			--str_data[9]	conconsecutivo
			--str_data[10]	conciliacionautomatica
			
			INSERT INTO tes_mov_tipos(titulo, descripcion, tipo,consecutivo,grupo,conciliacion, borrado_logico, momento_creacion, gral_emp_id, gral_suc_id, gral_usr_id_creacion) 
			VALUES(str_data[5],str_data[6],str_data[8]::boolean,str_data[9]::boolean, str_data[7]::integer, str_data[10]::boolean, false, now(), emp_id, suc_id, usuario_id ) RETURNING id INTO ultimo_id;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			
			UPDATE tes_mov_tipos SET titulo=str_data[5],
						descripcion=str_data[6],
						tipo=str_data[8]::boolean,
						consecutivo=str_data[9]::boolean,
						grupo=str_data[7]::integer,
						conciliacion=str_data[10]::boolean, 
						borrado_logico=false,
						momento_actualizacion=now(),
						gral_usr_id_actualizacion=usuario_id
			WHERE id = str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE tes_mov_tipos SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de tes_mov_tipos
	
	
	
	
	-- Catalogo de tes_ban
	IF app_selected = 42 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			--str_data[7]	clave
			
			INSERT INTO tes_ban(titulo, descripcion, borrado_logico, momento_creacion,gral_usr_id_creacion,gral_emp_id, gral_suc_id, clave) 
			VALUES(str_data[5],str_data[6],false, now(), usuario_id, emp_id, suc_id, str_data[7]) RETURNING id INTO ultimo_id;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN			
			UPDATE tes_ban SET titulo=str_data[5],descripcion=str_data[6],clave=str_data[7],borrado_logico=false,momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id 
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE tes_ban SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id  WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de tes_ban

	
	-- Catalogo de Familias
        IF app_selected = 43 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         familia
                        --str_data[6]         descripcion
                        INSERT INTO inv_prod_familias (titulo,descripcion,momento_creacion, borrado_logico, gral_usr_id_creacion, gral_emp_id, gral_suc_id, inv_prod_tipo_id)
                        VALUES (str_data[5],str_data[6],now(),false,  usuario_id, emp_id, suc_id, str_data[7]::integer)
                        RETURNING id INTO ultimo_id;
                        
                        UPDATE inv_prod_familias SET identificador_familia_padre=ultimo_id WHERE id=ultimo_id;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE inv_prod_familias SET titulo=str_data[5],descripcion=str_data[6],momento_actualizacion=now(), gral_usr_id_actualizacion=usuario_id, inv_prod_tipo_id=str_data[7]::integer 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        UPDATE inv_prod_familias SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id  WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina catalogo de Familias

	-- Catalogo de Conceptos Bancarios(tes_con)
	IF app_selected = 44 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			
			INSERT INTO tes_con(titulo, descripcion, tipo, borrado_logico,gral_usr_id_creacion,gral_emp_id,gral_suc_id, momento_creacion) 
			VALUES(str_data[5],str_data[6],str_data[7]::boolean,false,usuario_id, emp_id, suc_id, now()) RETURNING id INTO ultimo_id;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE tes_con SET 
				titulo=str_data[5],
				descripcion=str_data[6],
				tipo=str_data[7]::boolean,
				borrado_logico=false,
				gral_usr_id_actualizacion=usuario_id,
				momento_actualizacion=now() 
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE tes_con SET borrado_logico=true,gral_usr_id_baja=usuario_id, momento_baja=now() WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de tes_con
	
	
	
	-- Catalogo de  producto grupos
        IF app_selected = 45 THEN
                IF command_selected = 'new' THEN
                       --id [4]                 id
                        --str_data[5]         grupo
                        --str_data[6]     descripcion
                        
                        INSERT INTO inv_prod_grupos (titulo,descripcion,borrado_logico, gral_usr_id_creacion, gral_emp_id, gral_suc_id, momento_creacion) 
                        VALUES (str_data[5],str_data[6],false, usuario_id, emp_id, suc_id, now());
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
			UPDATE inv_prod_grupos SET 
				titulo=str_data[5],						
				descripcion=str_data[6],
				momento_actualizacion=now(),
				gral_usr_id_actualizacion=usuario_id
			WHERE inv_prod_grupos.id = str_data[4]::integer;
			valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                         UPDATE inv_prod_grupos SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id
                         WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo producto grupos	
	
	
	
	-- Catalogo de  Plazas
        IF app_selected = 46 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         plaza
                        --str_data[6]     nombre
                        --str_data[7]     id_zona
                        INSERT INTO gral_plazas (titulo,descripcion,momento_creacion,borrado_logico,empresa_id,estatus,inv_zonas_id) VALUES (str_data[5],str_data[6],now(),false,emp_id,str_data[8]::boolean,str_data[7]::integer);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE gral_plazas SET titulo=str_data[5], descripcion=str_data[6], inv_zonas_id =str_data[7]::integer, momento_actualizacion=now(), estatus=str_data[8]::boolean 
                        WHERE gral_plazas.id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                         UPDATE gral_plazas SET momento_baja=now(), borrado_logico=true WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Plazas

	-- Catalogo de inv_pre
	IF app_selected = 47 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[76]	select_presentacion
			
			INSERT INTO inv_pre(
				inv_prod_id, 
				precio_1,
				precio_2,
				precio_3,
				precio_4,
				precio_5,
				precio_6,
				precio_7,
				precio_8,
				precio_9,
				precio_10,
				descuento_1,
				descuento_2,
				descuento_3,
				descuento_4,
				descuento_5,
				descuento_6,
				descuento_7,
				descuento_8,
				descuento_9,
				descuento_10, 
				default_precio_1, --str_data[26]::double precision
				default_precio_2, --str_data[27]::double precision
				default_precio_3, --str_data[28]::double precision
				default_precio_4, --str_data[29]::double precision
				default_precio_5, --str_data[30]::double precision
				default_precio_6, --str_data[31]::double precision
				default_precio_7, --str_data[32]::double precision
				default_precio_8, --str_data[33]::double precision
				default_precio_9, --str_data[34]::double precision
				default_precio_10, --str_data[35]::double precision
				base_precio_1, --str_data[36]::integer
				base_precio_2, --str_data[37]::integer
				base_precio_3, --str_data[38]::integer
				base_precio_4, --str_data[39]::integer
				base_precio_5, --str_data[40]::integer
				base_precio_6, --str_data[41]::integer
				base_precio_7, --str_data[42]::integer
				base_precio_8, --str_data[43]::integer
				base_precio_9, --str_data[44]::integer
				base_precio_10, --str_data[45]::integer
				calculo_precio_1, --str_data[46]::integer
				calculo_precio_2, --str_data[47]::integer
				calculo_precio_3, --str_data[48]::integer
				calculo_precio_4, --str_data[49]::integer
				calculo_precio_5, --str_data[50]::integer
				calculo_precio_6, --str_data[51]::integer
				calculo_precio_7, --str_data[52]::integer
				calculo_precio_8, --str_data[53]::integer
				calculo_precio_9, --str_data[54]::integer
				calculo_precio_10, --str_data[55]::integer
				operacion_precio_1, --str_data[56]::integer
				operacion_precio_2, --str_data[57]::integer
				operacion_precio_3, --str_data[58]::integer
				operacion_precio_4, --str_data[59]::integer
				operacion_precio_5, --str_data[60]::integer
				operacion_precio_6, --str_data[61]::integer
				operacion_precio_7, --str_data[62]::integer
				operacion_precio_8, --str_data[63]::integer
				operacion_precio_9, --str_data[64]::integer
				operacion_precio_10, --str_data[65]::integer
				redondeo_precio_1, --str_data[66]::integer
				redondeo_precio_2, --str_data[67]::integer
				redondeo_precio_3, --str_data[68]::integer
				redondeo_precio_4, --str_data[69]::integer
				redondeo_precio_5, --str_data[70]::integer
				redondeo_precio_6, --str_data[71]::integer
				redondeo_precio_7, --str_data[72]::integer
				redondeo_precio_8, --str_data[73]::integer
				redondeo_precio_9, --str_data[74]::integer
				redondeo_precio_10, --str_data[75]::integer
				inv_prod_presentacion_id, --str_data[76]::integer
				gral_mon_id_pre1, --str_data[77]::integer
				gral_mon_id_pre2, --str_data[78]::integer
				gral_mon_id_pre3, --str_data[79]::integer
				gral_mon_id_pre4, --str_data[80]::integer
				gral_mon_id_pre5, --str_data[81]::integer
				gral_mon_id_pre6, --str_data[82]::integer
				gral_mon_id_pre7, --str_data[83]::integer
				gral_mon_id_pre8, --str_data[84]::integer
				gral_mon_id_pre9, --str_data[85]::integer
				gral_mon_id_pre10, --str_data[86]::integer
				gral_emp_id, --emp_id
				gral_usr_id_creacion, --usuario_id, 
				borrado_logico,--FALSE
				momento_creacion --now()
			) 
			VALUES(str_data[5]::integer,
				str_data[6]::double precision,str_data[7]::double precision,str_data[8]::double precision,str_data[9]::double precision,str_data[10]::double precision,str_data[11]::double precision,str_data[12]::double precision,str_data[13]::double precision,str_data[14]::double precision,str_data[15]::double precision,
				str_data[16]::double precision,str_data[17]::double precision,str_data[18]::double precision,str_data[19]::double precision,str_data[20]::double precision,str_data[21]::double precision,str_data[22]::double precision,str_data[23]::double precision,str_data[24]::double precision,str_data[25]::double precision,
				str_data[26]::double precision,str_data[27]::double precision,str_data[28]::double precision,str_data[29]::double precision,str_data[30]::double precision,str_data[31]::double precision,str_data[32]::double precision,str_data[33]::double precision,str_data[34]::double precision,str_data[35]::double precision,
				str_data[36]::integer,str_data[37]::integer,str_data[38]::integer,str_data[39]::integer,str_data[40]::integer,str_data[41]::integer,str_data[42]::integer,str_data[43]::integer,str_data[44]::integer,str_data[45]::integer,
				str_data[46]::integer,str_data[47]::integer,str_data[48]::integer,str_data[49]::integer,str_data[50]::integer,str_data[51]::integer,str_data[52]::integer,str_data[53]::integer,str_data[54]::integer,str_data[55]::integer,
				str_data[56]::integer,str_data[57]::integer,str_data[58]::integer,str_data[59]::integer,str_data[60]::integer,str_data[61]::integer,str_data[62]::integer,str_data[63]::integer,str_data[64]::integer,str_data[65]::integer,
				str_data[66]::integer,str_data[67]::integer,str_data[68]::integer,str_data[69]::integer,str_data[70]::integer,str_data[71]::integer,str_data[72]::integer,str_data[73]::integer,str_data[74]::integer,str_data[75]::integer,
				str_data[76]::integer, str_data[77]::integer, str_data[78]::integer, str_data[79]::integer, str_data[80]::integer, str_data[81]::integer, str_data[82]::integer, str_data[83]::integer, str_data[84]::integer, str_data[85]::integer, str_data[86]::integer,
				emp_id,usuario_id,false,now()
			) RETURNING id INTO ultimo_id;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			
			UPDATE inv_pre SET precio_1=str_data[6]::double precision,
				precio_2=str_data[7]::double precision,
				precio_3=str_data[8]::double precision,
				precio_4=str_data[9]::double precision,
				precio_5=str_data[10]::double precision,
				precio_6=str_data[11]::double precision,
				precio_7=str_data[12]::double precision,
				precio_8=str_data[13]::double precision,
				precio_9=str_data[14]::double precision,
				precio_10=str_data[15]::double precision,
				descuento_1=str_data[16]::double precision,
				descuento_2=str_data[17]::double precision,
				descuento_3=str_data[18]::double precision,
				descuento_4=str_data[19]::double precision,
				descuento_5=str_data[20]::double precision,
				descuento_6=str_data[21]::double precision,
				descuento_7=str_data[22]::double precision,
				descuento_8=str_data[23]::double precision,
				descuento_9=str_data[24]::double precision,
				descuento_10=str_data[25]::double precision,
				default_precio_1 = str_data[26]::double precision,
				default_precio_2 = str_data[27]::double precision,
				default_precio_3 = str_data[28]::double precision,
				default_precio_4 = str_data[29]::double precision,
				default_precio_5 = str_data[30]::double precision,
				default_precio_6 = str_data[31]::double precision,
				default_precio_7 = str_data[32]::double precision,
				default_precio_8 = str_data[33]::double precision,
				default_precio_9 = str_data[34]::double precision,
				default_precio_10 = str_data[35]::double precision,
				base_precio_1 = str_data[36]::integer,
				base_precio_2 = str_data[37]::integer,
				base_precio_3 = str_data[38]::integer,
				base_precio_4 = str_data[39]::integer,
				base_precio_5 = str_data[40]::integer,
				base_precio_6 = str_data[41]::integer,
				base_precio_7 = str_data[42]::integer,
				base_precio_8 = str_data[43]::integer,
				base_precio_9 = str_data[44]::integer,
				base_precio_10 = str_data[45]::integer,
				calculo_precio_1 = str_data[46]::integer,
				calculo_precio_2 = str_data[47]::integer,
				calculo_precio_3 = str_data[48]::integer,
				calculo_precio_4 = str_data[49]::integer,
				calculo_precio_5 = str_data[50]::integer,
				calculo_precio_6 = str_data[51]::integer,
				calculo_precio_7 = str_data[52]::integer,
				calculo_precio_8 = str_data[53]::integer,
				calculo_precio_9 = str_data[54]::integer,
				calculo_precio_10 = str_data[55]::integer,
				operacion_precio_1 = str_data[56]::integer,
				operacion_precio_2 = str_data[57]::integer,
				operacion_precio_3 = str_data[58]::integer,
				operacion_precio_4 = str_data[59]::integer,
				operacion_precio_5 = str_data[60]::integer,
				operacion_precio_6 = str_data[61]::integer,
				operacion_precio_7 = str_data[62]::integer,
				operacion_precio_8 = str_data[63]::integer,
				operacion_precio_9 = str_data[64]::integer,
				operacion_precio_10 = str_data[65]::integer,
				redondeo_precio_1 = str_data[66]::integer,
				redondeo_precio_2 = str_data[67]::integer,
				redondeo_precio_3 = str_data[68]::integer,
				redondeo_precio_4 = str_data[69]::integer,
				redondeo_precio_5 = str_data[70]::integer,
				redondeo_precio_6 = str_data[71]::integer,
				redondeo_precio_7 = str_data[72]::integer,
				redondeo_precio_8 = str_data[73]::integer,
				redondeo_precio_9 = str_data[74]::integer,
				redondeo_precio_10 = str_data[75]::integer,
				inv_prod_presentacion_id=str_data[76]::integer,
				gral_mon_id_pre1 = str_data[77]::integer,
				gral_mon_id_pre2 = str_data[78]::integer,
				gral_mon_id_pre3 = str_data[79]::integer,
				gral_mon_id_pre4 = str_data[80]::integer,
				gral_mon_id_pre5 = str_data[81]::integer,
				gral_mon_id_pre6 = str_data[82]::integer,
				gral_mon_id_pre7 = str_data[83]::integer,
				gral_mon_id_pre8 = str_data[84]::integer,
				gral_mon_id_pre9 = str_data[85]::integer,
				gral_mon_id_pre10 = str_data[86]::integer,
				gral_usr_id_actualizacion = usuario_id,
				borrado_logico=false,
				momento_actualizacion=now() 
			WHERE id = str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_pre SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id  WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de inv_pre
	

	-- Catalogo de SubFamilias
        IF app_selected = 48 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         familia
                        --str_data[6]         descripcion
                        --str_data[7]         select_familia
                        INSERT INTO inv_prod_familias (titulo,descripcion,identificador_familia_padre,momento_creacion, borrado_logico, gral_usr_id_creacion, gral_emp_id, gral_suc_id,inv_prod_tipo_id)
                        VALUES (str_data[5],str_data[6],str_data[7]::integer,now(),false, usuario_id, emp_id, suc_id, str_data[8]::integer);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE inv_prod_familias SET titulo=str_data[5],descripcion=str_data[6],identificador_familia_padre=str_data[7]::integer,momento_actualizacion=now(), gral_usr_id_actualizacion=usuario_id, inv_prod_tipo_id=str_data[8]::integer 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        UPDATE inv_prod_familias SET borrado_logico=true, momento_baja=now(), gral_usr_id_baja=usuario_id  WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina catalogo de SubFamilias

        	

	-- Catalogo de unidades
	IF app_selected = 49 THEN
		IF command_selected = 'new' THEN
			INSERT INTO inv_prod_unidades (titulo,borrado_logico,titulo_abr,decimales)
				VALUES (str_data[6],false, str_data[5],str_data[7]::integer);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE inv_prod_unidades SET titulo=str_data[6],
						     titulo_abr=str_data[5],
						     decimales=str_data[7]::integer
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			eliminar_registro=true;

			IF eliminar_registro=TRUE THEN
				exis:=0;
				SELECT count(id) FROM inv_prod WHERE empresa_id=emp_id AND borrado_logico=false AND unidad_id=str_data[4]::integer 
				INTO exis;
				IF exis>0 THEN 
					valor_retorno := 'La Unidad de Medida no pudo ser eliminada porque est&aacute; asignado uno o m&aacute;s productos.';
					eliminar_registro=FALSE;
				END IF;
			END IF;

			IF eliminar_registro=TRUE THEN
				UPDATE inv_prod_unidades SET borrado_logico=true  WHERE inv_prod_unidades.id=str_data[4]::integer;
				valor_retorno := 'La Unidadde Medida fue eliminada con exito.';
			END IF;
		END IF;
	END IF;--termina catalogo de unidades
	
	
	
	-- Catalogo de  inventario de Clasicacion de   stock
        IF app_selected = 50 THEN
                IF command_selected = 'new' THEN
			--str_data[4]         id
                        --str_data[5]        titulo
                        --str_data[6]     descripcion
                        INSERT INTO inv_stock_clasificaciones (titulo,descripcion,borrado_logico,momento_creacion,gral_emp_id,gral_suc_id,gral_usr_id_creacion) 
                        VALUES (str_data[5],str_data[6],false,now(),emp_id,suc_id,usuario_id);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE inv_stock_clasificaciones SET  
				titulo=str_data[5],   						
				      descripcion=str_data[6],
				      momento_actualizacion= now(),
				      gral_usr_id_actualizacion=usuario_id
				WHERE inv_stock_clasificaciones.id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                         UPDATE inv_stock_clasificaciones SET borrado_logico=true,
                                                 gral_usr_id_baja= usuario_id
			 WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo inventario Clasificacion stock
	
	

	-- Catalogo de Comisiones
	IF app_selected = 51 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			
			INSERT INTO inv_com(  inv_prod_id,
			  limite_inferior,--smallint
			  limite_superior,
			  comision,
			  comision_valor,
			  nivel,
			  escala ,
			 borrado_logico, momento_creacion ) 
			VALUES(str_data[5]::integer,
			str_data[8]::double precision,
			str_data[9]::double precision,
			str_data[10]::double precision,
			str_data[11]::double precision,
			str_data[7]::smallint,
			str_data[6]::smallint,
			false, now()) RETURNING id INTO ultimo_id;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			
			UPDATE inv_com SET 
			  limite_inferior=str_data[8]::double precision,
			  limite_superior=str_data[9]::double precision,
			  comision=str_data[10]::double precision,
			  comision_valor=str_data[11]::double precision,
			  nivel=str_data[7]::smallint,
			  escala=str_data[6]::smallint ,
			borrado_logico=false,
			momento_actualizacion=now() 
			WHERE id = str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_com SET borrado_logico=true, momento_baja=now() WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de Comisiones

	
	
	
	-- Catalogo de  inventario de Clasicaciones
        IF app_selected = 52 THEN
                IF command_selected = 'new' THEN
			--str_data[4]         id
                        --str_data[5]         titulo
                        --str_data[6]         descripcion
                        --str_data[7]         factorseguridad
                        --str_data[8]         stockseguridad
                        INSERT INTO inv_clas (titulo,descripcion,borrado_logico,stock_seguridad,factor_maximo,momento_creacion,gral_emp_id,gral_suc_id,gral_usr_id_creacion) 
                        VALUES (str_data[5],str_data[6],false,str_data[7]::double precision,str_data[8]::double precision,now(),emp_id,suc_id,usuario_id);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                
                        UPDATE inv_clas SET  titulo=str_data[5],   						
					      descripcion=str_data[6],
					      momento_actualizacion= now(),
					      stock_seguridad=str_data[7]::double precision,
					      factor_maximo=str_data[8]::double precision,
					      gral_usr_id_actualizacion=usuario_id
			WHERE inv_clas.id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                         UPDATE inv_clas SET borrado_logico=true,
				             momento_baja=now(),
                                             gral_usr_id_baja= usuario_id
			 WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo inventario Clasificaciones

	-- Catalogo de inv_pre_ofe
	IF app_selected = 53 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			
			INSERT INTO inv_pre_ofe(  inv_prod_id,
			  precio_oferta,
			  descto_max,
			  criterio_oferta,
			  precio_lista_1,precio_lista_2,
			  precio_lista_3,precio_lista_4,
			  precio_lista_5,precio_lista_6,
			  precio_lista_7,precio_lista_8,
			  precio_lista_9,precio_lista_10,
			  fecha_inicial,fecha_final,
			  tipo_descto_precio,
			 borrado_logico, momento_creacion ) 
			VALUES(str_data[5]::integer,
			str_data[8]::double precision,
			0,
			str_data[10]::boolean,
			str_data[11]::boolean,
			str_data[12]::boolean,
			str_data[13]::boolean,
			str_data[14]::boolean,
			str_data[15]::boolean,
			str_data[16]::boolean,
			str_data[17]::boolean,
			str_data[18]::boolean,
			str_data[19]::boolean,
			str_data[20]::boolean,
			str_data[6]::date,
			str_data[7]::date,
			str_data[21]::boolean,
			false, now()) RETURNING id INTO ultimo_id;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			
			UPDATE inv_pre_ofe SET 
			  precio_oferta=str_data[8]::double precision,
			  descto_max=0,
			  criterio_oferta=str_data[10]::boolean,
			  precio_lista_1=str_data[11]::boolean,precio_lista_2=str_data[12]::boolean,
			  precio_lista_3=str_data[13]::boolean,precio_lista_4=str_data[14]::boolean,
			  precio_lista_5=str_data[15]::boolean,precio_lista_6=str_data[16]::boolean,
			  precio_lista_7=str_data[17]::boolean,precio_lista_8=str_data[18]::boolean,
			  precio_lista_9=str_data[19]::boolean,precio_lista_10=str_data[20]::boolean,
			  fecha_inicial=str_data[6]::date,fecha_final=str_data[7]::date,
			  tipo_descto_precio=str_data[21]::boolean,
			borrado_logico=false,
			momento_actualizacion=now() 
			WHERE id = str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE inv_pre_ofe SET borrado_logico=true, momento_baja=now() WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de inv_pre_ofe

	-- Catalogo de  inventario plazas-sucursales
	IF app_selected = 54 THEN
		--str_data[1]         app_selected
		--str_data[2]         command_selected
		--str_data[3]         id_sucursal
		--str_data[4]         plazasAgregadas
			
		IF str_data[4] = '' THEN
			 DELETE FROM gral_suc_pza WHERE sucursal_id=str_data[3]::integer; 
			valor_retorno := '1';
		END IF;
		
		--RAISE EXCEPTION '%','total  de filas???'||str_data[4];
		
		IF str_data[4] != '' THEN
			DELETE FROM gral_suc_pza WHERE sucursal_id=str_data[3]::integer;  
			
			--convertir en arreglo las plazasAgregadas
			SELECT INTO str_filas string_to_array(str_data[4],',');
			
			--obtiene numero de elementos del arreglo str_fila
			tot_filas:= array_length(str_fila,1);
			
			--crea registros gral_suc_pza
			FOR cont_fila_pres IN 1 .. tot_filas LOOP
				INSERT INTO gral_suc_pza(plaza_id,sucursal_id) VALUES (str_fila[cont_fila_pres]::integer,str_data[3]::integer);
			END LOOP;
			
			valor_retorno := '1';
		END IF;
                
        END IF;--termina Catalogo inventario plazas-sucursales
	
	
	
	-- Catalogo Direcciones de proveedores
       IF app_selected = 56 THEN
             IF command_selected = 'new' THEN
		--str_data[4]        id                --str_data[5]        calle
                --str_data[6]	     codigoPostal      --str_data[7]        colonia
                --str_data[8]        entreCalles       --str_data[9]	     extDos
                --str_data[10]       extUno            --str_data[11]       numExterior
                --str_data[12]	     numInterior       --str_data[13]       proveedor
                --str_data[14]       id_estado         --str_data[15]	     id_municipio
                --str_data[16]       id_pais           --str_data[17]       telDos
                --str_data[18]	     telUno
                
		INSERT INTO cxp_prov_dir(proveedor_id,calle,entre_calles,numero_interior,numero_exterior,colonia,cp,pais_id,estado_id,municipio_id,telefono1,extension1,telefono2,borrado_logico,momento_creacion,id_usuario_creacion,gral_emp_id,gral_suc_id ) 
		VALUES (str_data[13]::integer,str_data[5],str_data[8],str_data[12],str_data[11],str_data[7]::character varying,str_data[6]::integer,str_data[16]::integer,str_data[14]::integer,str_data[15]::integer,str_data[18],str_data[10],str_data[17],false,now(),usuario_id,emp_id,suc_id );
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxp_prov_dir SET 
				proveedor_id=str_data[13]::INTEGER,
				calle        	=str_data[5],  --calle
				entre_calles 	=str_data[8], --entre calles
				numero_interior =str_data[12], --numero interior
				numero_exterior =str_data[11], --numeroexterior
				colonia 	=str_data[7], --colonia
				cp 		=str_data[6], --cp
				pais_id 	=str_data[16]::INTEGER,--pais
				estado_id 	=str_data[14]::INTEGER, --edo
				municipio_id 	=str_data[15]::INTEGER, --mpio
				telefono1 	=str_data[18],--567
				extension1 	=str_data[10],--5678
				telefono2 	=str_data[17],--56789	
				extension2 	=str_data[9],--567890				
				momento_actualizacion =now(),
				id_usuario_actualizacion= usuario_id						
                        WHERE cxp_prov_dir.id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                         UPDATE cxp_prov_dir SET momento_baja=now(),
                                            borrado_logico=true,
                                            id_usuario_baja=usuario_id
                         WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo Direcciones de proveedores
	
	
	

	--Catalogo de Chequera
	IF app_selected = 59 THEN
		--str_data[1]  app_selected                     str_data[11]  id_estado                         
		--str_data[2]  command_selected			str_data[12]  id_moneda				str_data[21]  telefono1
		--str_data[3]  id_usuario			str_data[13]  id_banco			        str_data[22]  extencion1
		--str_data[4]  id				str_data[14]  chk_imprimir_chequeningles	str_data[23]  telefono2	
		--str_data[5]  chequera				str_data[15]  calle				str_data[24]  extencion2		
		--str_data[6]  chk_modificar_consecutivo 	str_data[16]  numero				str_data[25]  fax
		--str_data[7]  chk_modificar_fecha		str_data[17]  colonia				str_data[26]  gerente
		--str_data[8]  chk_modificar_cheque		str_data[18]  cp				str_data[27]  ejecutivo
		--str_data[9]  id_pais				str_data[19]  numero_sucursal		        str_data[28]  email;
		--str_data[10]  id_municipio			str_data[20]  nombre_sucursal        		str_data[29]  id_cta_activo
		
		
		--RAISE EXCEPTION '%','total  de filas???'||str_data[4];
		-- Catalogo de  inventario de Clasicaciones
                IF command_selected = 'new' THEN
			INSERT INTO tes_che (titulo ,aut_modif_consecutivo,   aut_modif_fecha ,    aut_modif_cheque ,       gral_pais_id ,  gral_mun_id ,                gral_edo_id ,            moneda_id ,          tes_ban_id ,  imp_cheque_ingles,  calle  ,  numero  ,  colonia ,  codigo_postal ,num_sucursal ,  nombre_sucursal  ,  telefono1  ,  extencion1  ,  telefono2  ,  extencion2  ,  fax  ,  gerente  ,  ejecutivo  ,  email  ,  momento_creacion , borrado_logico ,  gral_usr_id_creacion ,    gral_emp_id ,  gral_suc_id, ctb_cta_id_activo) 
			VALUES (str_data[5],str_data[6]::boolean, str_data[7]::boolean,str_data[8]::boolean,str_data[9]::integer,str_data[10]::integer, str_data[11]::integer, str_data[12]::integer,str_data[13]::integer,str_data[14]::boolean, str_data[15], str_data[16], str_data[17], str_data[18]::integer, str_data[19]::integer,str_data[20],str_data[21],str_data[22],str_data[23],str_data[24],str_data[25],str_data[26],str_data[27], str_data[28], now(), false,usuario_id, emp_id, suc_id, str_data[29]::integer);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE tes_che SET  titulo=str_data[5],
                        aut_modif_consecutivo =  str_data[6]::boolean,
                        aut_modif_fecha =str_data[7]::boolean,
                        aut_modif_cheque=str_data[8]::boolean,
                        gral_pais_id=str_data[9]::integer,
                        gral_mun_id =str_data[10]::integer, 
                        gral_edo_id = str_data[11]::integer,
                        moneda_id= str_data[12]::integer,
                        tes_ban_id =str_data[13]::integer,
                        imp_cheque_ingles=str_data[14]::boolean  ,
                        calle   =str_data[15],
                        numero = str_data[16] ,
                        colonia= str_data[17],
                        codigo_postal =str_data[18]::integer,
                        num_sucursal =str_data[19]::integer , 
                        nombre_sucursal =str_data[20],
                        telefono1 =str_data[21],
                        extencion1  =str_data[22],
                        telefono2 =str_data[23],
                        extencion2  =str_data[24],
                        fax  =str_data[25],
                        gerente =str_data[26] ,
                        ejecutivo =str_data[27],
                        email =str_data[28],
                        ctb_cta_id_activo=str_data[29]::integer,
			momento_actualizacion=now(),
			gral_usr_id_actualizacion=usuario_id
			WHERE tes_che.id = str_data[4]::integer;
			
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                         UPDATE tes_che SET borrado_logico=true,momento_baja=now(),gral_usr_id_baja= usuario_id
			 WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
	END IF;--termina Catalogo Chequeras

	

	-- Catalogo de inventario de  Presentaciones
	IF app_selected = 68 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         titulo
			--str_data[6]         cantidad-equivalencia
			INSERT INTO inv_prod_presentaciones (titulo,borrado_logico,momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id, cantidad) 
			VALUES (str_data[5],false,now(), usuario_id, emp_id, suc_id,str_data[6]::double precision );
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE inv_prod_presentaciones SET titulo=str_data[5], cantidad = str_data[6]::double precision, momento_actualizacion=now(), gral_usr_id_actualizacion=usuario_id
			WHERE inv_prod_presentaciones.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			eliminar_registro=true;
			
			IF eliminar_registro=TRUE THEN 
				exis:=0;
				SELECT count(id) FROM inv_prod_pres_x_prod WHERE presentacion_id=str_data[4]::integer
				INTO exis;
				IF exis>0 THEN 
					valor_retorno := 'La presentaci&oacute;n no pudo ser eliminada porque est&aacute; asignado como presentaci&oacute;n de uno o m&aacute;s productos.';
					eliminar_registro=FALSE;
				END IF;
			END IF;
			
			IF eliminar_registro=TRUE THEN
				exis:=0;
				SELECT count(id) FROM inv_prod WHERE empresa_id=emp_id AND borrado_logico=FALSE AND inv_prod_presentacion_id=str_data[4]::integer 
				INTO exis;
				IF exis>0 THEN 
					valor_retorno := 'La presentaci&oacute;n no pudo ser eliminada porque est&aacute; asignado como presentaci&oacute;n default de uno o m&aacute;s productos.';
					eliminar_registro=FALSE;
				END IF;
			END IF;
			
			--Verificar si hay que validar existencias de Presentaciones
			IF controlExisPres=true THEN 
				IF eliminar_registro=TRUE THEN
					exis:=0;
					SELECT count(id) FROM env_conf WHERE gral_emp_id=emp_id AND borrado_logico=FALSE AND inv_prod_presentacion_id=str_data[4]::integer 
					INTO exis;
					IF exis > 0 THEN 
						valor_retorno := 'La presentaci&oacute;n no pudo ser eliminada porque est&aacute; asociado a un registro en el cat&aacute;logo de configuraci&oacute;n de envases.';
						eliminar_registro=FALSE;
					ELSE
						--Eliminar registro de existencias por presentaciones si es que existe
						DELETE FROM inv_exi_pres WHERE inv_prod_presentacion_id=str_data[4]::integer;
					END IF;
				END IF;
			END IF;
			
			
			IF eliminar_registro=TRUE THEN
				UPDATE inv_prod_presentaciones SET momento_baja=now(), borrado_logico=true, gral_usr_id_baja=usuario_id WHERE id = str_data[4]::integer;
				
				--Eliminar de la tabla de costos
				DELETE FROM inv_prod_costos WHERE ano=EXTRACT(YEAR  FROM now()) AND inv_prod_presentacion_id=str_data[4]::integer;
				
				--Eliminar de la Lista de Precios
				DELETE FROM inv_pre WHERE gral_emp_id=emp_id AND inv_prod_presentacion_id=str_data[4]::integer;
				
				valor_retorno := 'La presentacion fue eliminada.';
			END IF;
			
		END IF;
	END IF;--termina Catalogo Presentaciones

	--Catalogo de formulas
	IF app_selected = 69 THEN
		--str_data[1]  app_selected
		--str_data[2]  command_selected	
		--str_data[3]  id_usuario
		--str_data[4]  id
		--str_data[5]  id_prod_master
		--str_data[6]  inv_prod_id
		--str_data[7]  nivel
		--str_data[8]  producto_elemento_id
		--str_data[9]  cantidad
		--RAISE EXCEPTION '%','total  de filas???'||str_data[4];
		-- Catalogo de  formulas

                IF command_selected = 'new' THEN
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
		
			IF extra_data[1] != 'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP
				
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
					--aqui se vuelven a crear los registros
					INSERT INTO inv_formulas ( inv_prod_id_master ,  inv_prod_id ,          producto_elemento_id ,     cantidad ,                         nivel ) 
					VALUES (                  str_data[5]::integer,  str_data[6]::integer,  str_filas[1]::integer,      str_filas[2]::Double precision,   str_data[7]::integer);
					valor_retorno := '1';
					
				END LOOP;
			END IF;
		END IF;
                
                IF command_selected = 'edit' THEN
                        total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;

			DELETE FROM  inv_formulas WHERE inv_formulas.inv_prod_id_master = str_data[5]::integer AND inv_formulas.inv_prod_id=str_data[6]::integer AND inv_formulas.nivel=str_data[7]::integer;
			--RAISE EXCEPTION '%','update:'||'DELETE FROM  inv_formulas WHERE inv_formulas.inv_prod_id_master = '||str_data[5]::integer||' AND inv_formulas.inv_prod_id='||str_data[6]::integer||' AND inv_formulas.nivel='||str_data[7]::integer||' ';
			--IF extra_data[1] != 'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
					INSERT INTO inv_formulas ( inv_prod_id_master ,  inv_prod_id ,          producto_elemento_id ,     cantidad ,                         nivel ) 
					VALUES (                  str_data[5]::integer,  str_data[6]::integer,  str_filas[1]::integer,      str_filas[2]::Double precision,   str_data[7]::integer);
				END LOOP;
			--END IF;
			valor_retorno := '1';
                END IF;

                IF command_selected = 'delete' THEN
                         DELETE  FROM inv_formulas 
			 WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
	END IF;--termina Catalogo de formulas
	
	
	
	
	-- Catalogo de inventario de  Vehiculos
	IF app_selected = 73 THEN
		IF command_selected = 'new' THEN
			--str_data[1]	app_selected
			--str_data[2]	command_selected
			--str_data[3]	id_usuario
			--str_data[4]	id
			--str_data[5]	select_tipo_unidad
			--str_data[6]	select_clase
			--str_data[7]	select_marca
			--str_data[8]	select_anio
			--str_data[9]	color
			--str_data[10]	no_economico
			--str_data[11]	select_tipo_placa
			--str_data[12]	placas
			--str_data[13]	no_serie
			--str_data[14]	select_tipo_rodada
			--str_data[15]	select_tipo_caja
			--str_data[16]	cap_volumen
			--str_data[17]	cap_peso
			--str_data[18]	select_clasif2
			--str_data[19]	id_prov
			--str_data[20]	id_operador
			--str_data[21]	comentarios
			

			--Folio Catalogo de Unidades(LOG)
			id_tipo_consecutivo:=53;
			
			--Aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--Concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;

			
			INSERT INTO log_vehiculos (
				folio, --nuevo_folio,
				log_vehiculo_tipo_id, --str_data[5]::integer,
				log_vehiculo_clase_id, --str_data[6]::integer,
				log_vehiculo_marca_id, --str_data[7]::integer,
				anio, --str_data[8]::integer,
				color, --str_data[9],
				numero_economico, --str_data[10],
				log_vehiculo_tipo_placa_id, --str_data[11]::integer,
				placa, --str_data[12],
				numero_serie, --str_data[13],
				log_vehiculo_tipo_rodada_id, --str_data[14]::integer,
				log_vehiculo_tipo_caja_id, --str_data[15]::integer,
				cap_volumen, --str_data[16]::double precision,
				cap_peso, --str_data[17]::double precision,
				clasificacion2, --str_data[18]::integer,
				cxp_prov_id, --str_data[19]::integer,
				log_chofer_id, --str_data[20]::integer,
				comentarios, --str_data[21],
				gral_emp_id, --emp_id,
				gral_suc_id, --suc_id,
				borrado_logico, --false,
				momento_crea, --espacio_tiempo_ejecucion,
				gral_usr_id_crea --usuario_id
			) 
			VALUES (nuevo_folio, str_data[5]::integer, str_data[6]::integer, str_data[7]::integer, str_data[8]::integer, str_data[9], str_data[10], str_data[11]::integer, str_data[12], str_data[13], str_data[14]::integer, str_data[15]::integer, str_data[16]::double precision, str_data[17]::double precision, str_data[18]::integer, str_data[19]::integer, str_data[20]::integer, str_data[21], emp_id, suc_id, false, espacio_tiempo_ejecucion, usuario_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE log_vehiculos SET log_vehiculo_tipo_id=str_data[5]::integer, log_vehiculo_clase_id=str_data[6]::integer, log_vehiculo_marca_id=str_data[7]::integer, anio=str_data[8]::integer, color=str_data[9], numero_economico=str_data[10], log_vehiculo_tipo_placa_id=str_data[11]::integer, placa=str_data[12], numero_serie=str_data[13], log_vehiculo_tipo_rodada_id=str_data[14]::integer, log_vehiculo_tipo_caja_id=str_data[15]::integer, cap_volumen=str_data[16]::double precision, cap_peso=str_data[17]::double precision, clasificacion2=str_data[18]::integer, cxp_prov_id=str_data[19]::integer, log_chofer_id=str_data[20]::integer, comentarios=str_data[21], momento_actualiza=espacio_tiempo_ejecucion, gral_usr_id_actualiza=usuario_id 
			WHERE log_vehiculos.id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE log_vehiculos SET momento_baja=espacio_tiempo_ejecucion, gral_usr_id_baja=usuario_id, borrado_logico=true 
			WHERE log_vehiculos.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Vehiculo
	
	
	
	
	-- Catalogo de inventario de  Puestos
	IF app_selected = 75 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			INSERT INTO gral_puestos (titulo,borrado_logico,momento_creacion,gral_usr_id_creacion,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE gral_puestos SET titulo=str_data[5],
					    momento_actualizacion=now(),
					    gral_usr_id_actualizacion=usuario_id
			WHERE gral_puestos.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE gral_puestos SET momento_baja=now(),borrado_logico=true 
			WHERE gral_puestos.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Puestos
	
	
	-- Catalogo de inventario de  escolaridades
	IF app_selected = 77 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         titulo
			INSERT INTO gral_escolaridads (titulo,borrado_logico,momento_creacion,gral_usr_id_creacion,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE gral_escolaridads SET titulo=str_data[5],momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id
			WHERE gral_escolaridads.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			UPDATE gral_escolaridads SET momento_baja=now(),borrado_logico=true 
			WHERE gral_escolaridads.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Escolaridades

	-- Catalogo de inventario de  Religiones
	IF app_selected = 78 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         titulo
			INSERT INTO gral_religions (titulo,borrado_logico,momento_creacion,gral_usr_id_creacion,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE gral_religions SET titulo=str_data[5],momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id
			WHERE gral_religions.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			UPDATE gral_religions SET momento_baja=now(),borrado_logico=true 
			WHERE gral_religions.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Religiones
	
	
	
	-- Catalogo de inventario de  tipos de sangre
	IF app_selected = 79 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         titulo
			INSERT INTO gral_sangretipos (titulo,borrado_logico,momento_creacion,gral_usr_id_creacion,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE gral_sangretipos SET titulo=str_data[5],momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id
			WHERE gral_sangretipos.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			UPDATE gral_sangretipos SET momento_baja=now(),borrado_logico=true 
			WHERE gral_sangretipos.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Tipo de sangre

	
	-- Catalogo de inventario de  departamentos
	IF app_selected = 82 THEN
		IF command_selected = 'new' THEN
		--RAISE EXCEPTION '%','titulo'||str_data[5];
			--str_data[4]         id select * from gral_deptos
			--str_data[5]         titulo
			INSERT INTO gral_deptos (titulo,costo_prorrateo,vigente, borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id,    gral_suc_id) 
			VALUES (str_data[5], str_data[6]::double precision, true,false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
			
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE gral_deptos SET titulo=str_data[5],
					      costo_prorrateo=str_data[6]::double precision,
					      momento_actualizacion=now(),
					      gral_usr_id_actualizacion=usuario_id
		        WHERE gral_deptos.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update gral_deptos SET momento_baja=now(),
					      borrado_logico=true 
			WHERE gral_deptos.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de  departamentos
	
	
	-- Catalogo de inventario de  tipo de equipos
	IF app_selected = 83 THEN
		IF command_selected = 'new' THEN
			--str_data[4]         id
			--str_data[5]         titulo
			INSERT INTO pro_tipo_equipo (titulo,borrado_logico,momento_creacion,gral_usr_id_creacion,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE pro_tipo_equipo SET titulo=str_data[5],momento_actualizacion=now(),gral_usr_id_actualizacion=usuario_id
		        WHERE pro_tipo_equipo.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update pro_tipo_equipo SET momento_baja=now(),borrado_logico=true 
			WHERE pro_tipo_equipo.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de  tipo de equipos

	-- Catalogo de inventario de  dias no laborables
	IF app_selected = 84 THEN
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        fecha_no_laborable
			--str_data[6]        descripcion
			INSERT INTO gral_dias_no_laborables (fecha_no_laborable, descripcion, borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
			VALUES (str_data[5]::date,str_data[6],false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN		
			UPDATE gral_dias_no_laborables SET fecha_no_laborable=str_data[5]::date,
					      descripcion = str_data[6],
					      momento_actualizacion=now(),
					      gral_usr_id_actualizacion=usuario_id
		        WHERE gral_dias_no_laborables.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update gral_dias_no_laborables SET momento_baja=now(),
					      borrado_logico=true 
			WHERE gral_dias_no_laborables.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de  dias no laborables

	-- Catalogo de inventario de  categorias
	IF app_selected = 85 THEN
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        titulo(categ)
			--str_data[6]        sueldo_por_hora 	
			--str_data[7]        sueldo_por_horas_ext 
			--str_data[8]        gral_puesto_id (puesto)select * from gral_categ
			INSERT INTO gral_categ (titulo,      sueldo_por_hora,               sueldo_por_horas_ext,          gral_puesto_id,          borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
			VALUES (                str_data[5], str_data[6]::double precision, str_data[7]::double precision, str_data[8]::integer,    false,          now(),            usuario_id,           emp_id,      suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE gral_categ SET titulo=str_data[5],
			                      sueldo_por_hora=str_data[6]::double precision,
			                      sueldo_por_horas_ext=str_data[7]::double precision,
			                      gral_puesto_id=str_data[8]::integer,
					      momento_actualizacion=now(),
					      gral_usr_id_actualizacion=usuario_id
		        WHERE gral_categ.id = str_data[4]::integer;
			valor_retorno := '0';

		END IF;
		
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update gral_categ SET momento_baja=now(),
					      borrado_logico=true 
			WHERE gral_categ.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de  tipo de categorias

	
	-- Catalogo de inventario de  turnos
	IF app_selected = 92 THEN
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        turno
			--str_data[6]        hora_ini 	
			--str_data[7]        hora_fin 
			--str_data[8]        gral_deptos_id (depto) select * from gral_deptos_turnos
			INSERT INTO gral_deptos_turnos (turno,       hora_ini,                         hora_fin,                         gral_deptos_id,          borrado_logico, momento_creacion, gral_usr_id_creacion, gral_emp_id, gral_suc_id) 
			VALUES (                        str_data[5]::integer, str_data[6]::time with time zone, str_data[7]::time with time zone, str_data[8]::integer,    false,          now(),            usuario_id,           emp_id,      suc_id);
			valor_retorno := '1';
		END IF;
			
		IF command_selected = 'edit' THEN
			UPDATE gral_deptos_turnos SET turno=str_data[5]::integer,
			                      hora_ini=str_data[6]::time with time zone,
			                      hora_fin=str_data[7]::time with time zone,
			                      gral_deptos_id=str_data[8]::integer,
					      momento_actualizacion=now(),
					      gral_usr_id_actualizacion=usuario_id
		        WHERE gral_deptos_turnos.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;

		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update gral_deptos_turnos SET momento_baja=now(),
					      borrado_logico=true 
			WHERE gral_deptos_turnos.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de  tipo de turnos
	

	--Catalogo de productos equivalentes 
	IF app_selected = 96 THEN 
		--str_data[1]  app_selected 
		--str_data[2]  command_selected     
		--str_data[3]  id_usuario 
		--str_data[4]  id 
		--str_data[5]  inv_prod_id 
		--str_data[6]  inv_prod_id_equiv
		--RAISE EXCEPTION '%','total  de filas???'||str_data[4];

		IF command_selected = 'new' THEN 
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo 
			cont_fila:=1; 

			IF extra_data[1] != 'sin datos' THEN 
				FOR cont_fila IN 1 .. total_filas LOOP 
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___'); 
					--aqui se vuelven a crear los registros 
					INSERT INTO inv_prod_equiv ( inv_prod_id ,          inv_prod_id_equiv ,    observaciones )  
					VALUES (                     str_data[5]::integer,  str_filas[2]::integer,  str_filas[3]); 
					valor_retorno := '1'; 
				END LOOP; 
			END IF; 
		END IF; 

		IF command_selected = 'edit' THEN 
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo 
			cont_fila:=1; 
			--DELETE FROM  inv_prod_equiv WHERE inv_prod_equiv.id = '||str_data[4]::integer||' AND inv_formulas.inv_prod_id='||str_data[6]::integer||' AND inv_formulas.nivel='||str_data[7]::integer||' ';
			DELETE  FROM inv_prod_equiv WHERE inv_prod_id = str_data[5]::integer;
			--IF extra_data[1] = 'sin datos' THEN 
				FOR cont_fila IN 1 .. total_filas LOOP 
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___'); 
					--aqui se vuelven a crear los registros 
					INSERT INTO inv_prod_equiv ( inv_prod_id ,          inv_prod_id_equiv ,    observaciones )  
					VALUES (                     str_data[5]::integer,  str_filas[2]::integer,  str_filas[3]); 
				END LOOP; 
				valor_retorno := '1';
			--END IF;
		END IF; 

		IF command_selected = 'delete' THEN 
			DELETE  FROM inv_prod_equiv  
			WHERE inv_prod_id = str_data[4]::integer; 
			valor_retorno := '1'; 
		END IF; 

	END IF;--termina Catalogo de productos equivalentes 

	
	
	-- Aplicativo de edicion de codigo ISO
	IF app_selected = 99 THEN
		IF command_selected = 'edit' THEN
			UPDATE gral_docs_conf SET valor=str_data[5] WHERE gral_docs_conf.gral_doc_id = str_data[4]::integer and campo='CODIGO1';
			
			UPDATE gral_docs_conf SET valor=str_data[6] WHERE gral_docs_conf.gral_doc_id = str_data[4]::integer and campo='CODIGO2';

			UPDATE gral_docs SET momento_actualizacion=now(), gral_usr_id_actualizacion=usuario_id
			WHERE gral_docs.id = str_data[4]::integer;
			
			valor_retorno := '0';
		END IF;
	END IF;--termina Catalogo Puestos

	
	
	-- Catalogo de Motivos de Visitas
	IF app_selected = 109 THEN
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        descripcion
			
			id_tipo_consecutivo:=33;--Folio de motivo de visita
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			INSERT INTO crm_motivos_visita (folio_mv,
						descripcion,
						borrado_logico, 
						momento_creacion, 
						gral_usr_id_creacion, 
						gral_emp_id, 
						gral_suc_id) 
						
					VALUES ( nuevo_folio,
						str_data[5],
						false,          
						now(),            
						usuario_id,           
						emp_id,      
						suc_id);
			valor_retorno := '1';
		END IF;	
		IF command_selected = 'edit' THEN
			UPDATE crm_motivos_visita SET descripcion=str_data[5],
						      momento_actualizacion=now(),
						      gral_usr_id_actualizacion=usuario_id	      
		        WHERE crm_motivos_visita.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update crm_motivos_visita SET momento_baja=now(),borrado_logico=true 
			WHERE crm_motivos_visita.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo de Motivos de Visita

	
	-- Catalogo de Formas de Contacto
	IF app_selected = 110 THEN
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        descripcion
			
			id_tipo_consecutivo:=34;--Folio de forma de contacto
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			INSERT INTO crm_formas_contacto (folio_fc,
						descripcion,
						borrado_logico, 
						momento_creacion, 
						gral_usr_id_creacion, 
						gral_emp_id, 
						gral_suc_id) 
						
					VALUES ( nuevo_folio,
						str_data[5],
						false,          
						now(),            
						usuario_id,           
						emp_id,      
						suc_id);
			valor_retorno := '1';
		END IF;	
		IF command_selected = 'edit' THEN
			UPDATE crm_formas_contacto SET descripcion=str_data[5],
						      momento_actualizacion=now(),
						      gral_usr_id_actualizacion=usuario_id	      
		        WHERE crm_formas_contacto.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update crm_formas_contacto SET momento_baja=now(),borrado_logico=true 
			WHERE crm_formas_contacto.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Formas de Contacto
	
	-- Catalogo de Motivos de Llamada
	IF app_selected = 111 THEN
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        descripcion
			
			id_tipo_consecutivo:=35;--Folio de motivo de llamada
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			INSERT INTO crm_motivos_llamada (folio_mll,
						descripcion,
						borrado_logico, 
						momento_creacion, 
						gral_usr_id_creacion, 
						gral_emp_id, 
						gral_suc_id) 
						
					VALUES ( nuevo_folio,
						str_data[5],
						false,          
						now(),            
						usuario_id,           
						emp_id,      
						suc_id);
			valor_retorno := '1';
		END IF;	
		IF command_selected = 'edit' THEN
			UPDATE crm_motivos_llamada SET descripcion=str_data[5],
						      momento_actualizacion=now(),
						      gral_usr_id_actualizacion=usuario_id	      
		        WHERE crm_motivos_llamada.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		IF command_selected = 'delete' THEN
			--RAISE EXCEPTION '%','id de la tabla'||str_data[4];
			update crm_motivos_llamada SET momento_baja=now(),borrado_logico=true 
			WHERE crm_motivos_llamada.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Motivos de Llamada
	

	--Catalogo de Prospectos(CRM)
	IF app_selected = 113 THEN
		IF command_selected = 'new' THEN
			id_tipo_consecutivo:=38;--Folio Catalogo de Prospectos
			
			--aqui entra para tomar el consecutivo del folio  la sucursal actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			--RAISE EXCEPTION '%','emp_id: '||emp_id;
			--RAISE EXCEPTION '%','nombre_consecutivo: '||nombre_consecutivo;
			--RAISE EXCEPTION '%','cadena_extra: '||cadena_extra;
			--RAISE EXCEPTION '%','numero_control_client: '||numero_control_client;
			
			INSERT INTO crm_prospectos(
					numero_control,--nuevo_folio
					
					estatus ,--str_data[5]
					crm_etapas_prospecto_id ,--str_data[6]
					tipo_prospecto_id,--str_data[7]

					rfc,--str_data[8]
					razon_social,--str_data[9]
					calle,--str_data[10]
					numero,--str_data[11]
					entre_calles,--str_data[12]
					numero_exterior,--str_data[13]
					colonia,--str_data[14]
					cp,--str_data[15]
					pais_id,--str_data[16]::integer
					estado_id,--str_data[17]::integer
					municipio_id,--str_data[18]::integer
					localidad_alternativa,--str_data[19]
					telefono1,--str_data[20]
					extension1,--str_data[21]
					fax,--str_data[22]
					telefono2,--str_data[23]
					extension2,--str_data[24]
					email,--str_data[25]
					contacto,--str_data[26]
					clasificacion_id, --str_data[27] ,
					tipo_industria_id,--str_data[28],
					observaciones,--str_data[29]
					
					momento_creacion,--now()
					gral_usr_id_creacion,--usuario_id
					gral_emp_id,--emp_id
					gral_suc_id--suc_id
				)VALUES (
					nuevo_folio,
					str_data[5]::integer,
					str_data[6]::integer,
					str_data[7]::integer,

					str_data[8],
					str_data[9],
					str_data[10],
					str_data[11],
					str_data[12],
					str_data[13],
					str_data[14],
					str_data[15],
					str_data[16]::integer,
					str_data[17]::integer,
					str_data[18]::integer,
					str_data[19],
					str_data[20],
					str_data[21],
					str_data[22],
					str_data[23],
					str_data[24],
					str_data[25],
					str_data[26],
					str_data[27]::integer,
					str_data[28]::integer,
					str_data[29],
				
					now(),
					usuario_id,
					emp_id,
					suc_id
				)RETURNING id INTO ultimo_id;
			
				
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			--SELECT INTO str_data string_to_array(''||campos_data||'','___');
			--RAISE EXCEPTION '%',str_data[1];
			--RAISE EXCEPTION '%',identificador;
			--RAISE EXCEPTION '%','total  de filas???'||str_data[5]||'___'||str_data[6]||'___'||str_data[7]||'___'||str_data[8]||'___'||str_data[9]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10]||'___'||str_data[10];
			UPDATE crm_prospectos SET 
					--numero_control,--nuevo_folio
					estatus=str_data[5]::integer,
					crm_etapas_prospecto_id=str_data[6]::integer,
					tipo_prospecto_id=str_data[7]::integer,

					rfc=str_data[8],
					razon_social=str_data[9],
					calle=str_data[10],
					numero=str_data[11],
					entre_calles=str_data[12],
					numero_exterior=str_data[13],
					colonia=str_data[14],
					cp=str_data[15],
					pais_id=str_data[16]::integer,
					estado_id=str_data[17]::integer,
					municipio_id=str_data[18]::integer,
					localidad_alternativa=str_data[19],
					telefono1=str_data[20],
					extension1=str_data[21],
					fax=str_data[22],
					telefono2=str_data[23],
					extension2=str_data[24],
					email=str_data[25],
					contacto=str_data[26],
					clasificacion_id=str_data[27]::integer ,
					tipo_industria_id=str_data[28]::integer,
					observaciones=str_data[29],
					borrado_logico=false,
					momento_creacion=now(),
					gral_usr_id_actualizacion=usuario_id
					--gral_emp_id,--emp_id
					--gral_suc_id--suc_id
			WHERE id=str_data[4]::integer;
			
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE crm_prospectos SET borrado_logico=true, momento_baja=now(),gral_usr_id_baja = str_data[3]::integer WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina catalogo de Prospectos(CRM)

	
	
	-- Catalogo de Direcciones Fiscales de Clientes
	IF app_selected = 118 THEN
		IF command_selected = 'new' THEN
			--str_data[1] app_selected
			--str_data[2] command_selected
			--str_data[3] id_usuario
			--str_data[4] identificador
			--str_data[5] id_cliente
			--str_data[6] calle
			--str_data[7] numero_int
			--str_data[8] numero_ext
			--str_data[9] colonia
			--str_data[10] cp
			--str_data[11] select_pais
			--str_data[12] select_estado
			--str_data[13] select_municipio
			--str_data[14] entrecalles
			--str_data[15] tel1
			--str_data[16] ext1
			--str_data[17] fax
			--str_data[18] tel2
			--str_data[19] ext2
			--str_data[20] email
			--str_data[21] contacto
			
			INSERT INTO cxc_clie_df
			(
				cxc_clie_id,--str_data[5]::integer,
				calle,--str_data[6],
				numero_interior,--str_data[7],
				numero_exterior,--str_data[8],
				colonia,--str_data[9],
				cp,--str_data[10],
				gral_pais_id,--str_data[11]::integer,
				gral_edo_id,--str_data[12]::integer,
				gral_mun_id,--str_data[13]::integer,
				entre_calles,--str_data[14],
				telefono1,--str_data[15],
				extension1,--str_data[16],
				telefono2,--str_data[18],
				extension2,--str_data[19],
				fax,--str_data[17],
				email,--str_data[20],
				contacto,--str_data[21],
				momento_creacion,--now(),
				gra_usr_id_creacion--usuario_id
			)
			VALUES(str_data[5]::integer, str_data[6], str_data[7], str_data[8], str_data[9], str_data[10], str_data[11]::integer, str_data[12]::integer, str_data[13]::integer, str_data[14], str_data[15], str_data[16], str_data[18], str_data[19], str_data[17], str_data[20], str_data[21], now(), usuario_id);
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE cxc_clie_df SET cxc_clie_id=str_data[5]::integer, calle=str_data[6], numero_interior=str_data[7], numero_exterior=str_data[8], colonia=str_data[9], cp=str_data[10], gral_pais_id=str_data[11]::integer, gral_edo_id=str_data[12]::integer, gral_mun_id=str_data[13]::integer, entre_calles=str_data[14], telefono1=str_data[15], extension1=str_data[16], telefono2=str_data[18], extension2=str_data[19], fax=str_data[17], email=str_data[20], contacto=str_data[21], momento_actualizacion=now(), gra_usr_id_actualizacion=usuario_id
			WHERE id=str_data[4]::integer;
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE cxc_clie_df SET momento_baja=now(),borrado_logico=true, gra_usr_id_baja=usuario_id
			WHERE id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina catalogo de Direcciones Fiscales de Clientes

	
	-- Actualizador de Tipos de Cambio
	IF app_selected = 119 THEN
		IF command_selected = 'new'  or command_selected = 'edit' THEN
		--119___edit____1___4____3___2013-01-18___555
		--app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+moneda_id+"___"+fecha+"___"+tipo_cambio;
		--RAISE EXCEPTION '%','DataString::'||str_data[1]||'___'||str_data[2]||'____'||str_data[3]||'___'||str_data[4]||'____'||str_data[5]||'___'||str_data[6]||'___'||str_data[7]||'___'||str_data[8];
			----str_data[1]    app_selected       119
			----str_data[2]    command_selected   edit
			----str_data[3]    id_usuario         1
		        ----str_data[4]         id            4
			----str_data[5]         moneda_id     3
			----str_data[6]     fecha             2013-01-18
			----strd_data[7]    tipo_cambio       555
			----strd_data[8]     fecha_de_hoy   
/*		         RAISE EXCEPTION '%','id Encontrado::'||'select count(erp_monedavers.id ) as cantidad_registros
			from  erp_monedavers 
			WHERE erp_monedavers.moneda_id='||str_data[5]::integer ||' and to_char(erp_monedavers.momento_creacion,''yyyy-mm-dd'') = '''||str_data[8]||'''';
*/
			
			select count(erp_monedavers.id ) as cantidad_registros
			from  erp_monedavers 
			WHERE erp_monedavers.moneda_id=str_data[5]::integer  and to_char(erp_monedavers.momento_creacion,'yyyy-mm-dd') = str_data[8]
			INTO exis;
			
			--INSERT INTO erp_monedavers (valor ,  momento_creacion )
			--VALUES                (str_data[7],   now())
			--app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+moneda_id+"___"+fecha+"___"+tipo_cambio;
			IF exis = 0 THEN
				INSERT INTO erp_monedavers (valor ,  momento_creacion,moneda_id ,version) VALUES (str_data[7]::double precision,   now(), str_data[5]::integer,'ERP');  
			ELSE --select * from erp_monedavers where moneda_id=3
				UPDATE erp_monedavers SET momento_creacion=now(),valor=str_data[7]::double precision, version ='ERP'
				WHERE erp_monedavers.moneda_id=str_data[5]::integer and to_char(erp_monedavers.momento_creacion,'yyyy-mm-dd') = str_data[8];
			END IF;
			
			 
			valor_retorno := '1';
			
		END IF;
	END IF;--termina Actualizador de Tipos de cambio
	
	
	
	
	
	--Empieza Job Actualiza Moneda
	IF app_selected = 121 THEN
	/*GAS-SEP
		IF command_selected = 'new' THEN
			--str_data[4]        id
			--str_data[5]        valor
			--str_data[5]        tc
			--str_data[6]        moneda_desc
			
			total_filas:= array_length(extra_data,1);--obtiene total de elementos del arreglo
			cont_fila:=1;
			
			IF extra_data[1]<>'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');

					--RAISE EXCEPTION '%','extra_data[cont_fila]::'||extra_data[cont_fila];
					
					if str_filas[2]::double precision>0 then 
						select id from gral_mon where descripcion ilike '%'||str_filas[1]||'%' and borrado_logico=false limit 1 INTO ultimo_id;

						IF ultimo_id is not null THEN 
							select count(id) as cantidad from erp_monedavers where moneda_id=ultimo_id and momento_creacion > (select (select now())::date) INTO rowCount;
							
							IF rowCount <= 0 THEN 
								INSERT INTO erp_monedavers (moneda_id, valor, momento_creacion, version) 
								VALUES (ultimo_id, str_filas[2]::double precision, now(), str_filas[3]);
							end if;
						end if;
					end if;
				END LOOP;
			END IF;
			valor_retorno := '1';
		END IF;	
		*/
	END IF;--termina Job Actualiza Moneda
	
	
	
	
	--Aplicativo Actualizador de Contraseña del usuario
	IF app_selected = 155 THEN
		IF command_selected = 'edit' THEN
			UPDATE gral_usr SET password=str_data[5] WHERE id=str_data[3]::integer;
			
			valor_retorno := '1';
		END IF;	
		
	END IF;--termina Aplicativo Actualizador de Contraseña del usuario
	
	
	
	-- Catalogo de inventario de  IEPS
	IF app_selected = 167 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	descripcion
			--str_data[7]	tasa
			INSERT INTO gral_ieps (titulo,descripcion,tasa,borrado_logico,momento_creacion,gral_usr_id_crea,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],str_data[6],str_data[7]::double precision,false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE gral_ieps SET titulo=str_data[5],descripcion=str_data[6],tasa=str_data[7]::double precision,momento_actualizacion=now(),gral_usr_id_actualiza=usuario_id
			WHERE gral_ieps.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE gral_ieps SET momento_baja=now(),borrado_logico=true 
			WHERE gral_ieps.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina Catalogo IEPS

	-- Catalogo de inventario de  Percepciones
	IF app_selected = 170 THEN
		IF command_selected = 'new' THEN
			id_tipo_consecutivo:=48;--Folio Catalogo de Percepciones
			
			--aqui entra para tomar el consecutivo del folio de la Percepcionesactual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			
			nuevo_folio:= lpad(nuevo_folio, 3, '0');
			
			--str_data[4]	id
			--str_data clave nuevo_folio
			--str_data[5]	titulo
			--str_data[6]	activo
			--str_data[7]	tipopercepciones
			INSERT INTO nom_percep (clave,titulo,activo,nom_percep_tipo_id,borrado_logico,momento_creacion,gral_usr_id_crea,gral_emp_id,gral_suc_id) 
			VALUES (nuevo_folio,str_data[5],str_data[6]::boolean,str_data[7]::integer,false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE nom_percep SET titulo=str_data[5],activo=str_data[6]::boolean,nom_percep_tipo_id=str_data[7]::integer,momento_actualiza=now(),gral_usr_id_actualiza=usuario_id
			WHERE nom_percep.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE nom_percep SET momento_baja=now(),borrado_logico=true WHERE nom_percep.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;--termina Catalogo Percepciones
	
	
	
	-- Catalogo de inventario de  Deducciones
	IF app_selected = 171 THEN
		IF command_selected = 'new' THEN
			id_tipo_consecutivo:=49;--Folio Catalogo de Deducciones
			
			--aqui entra para tomar el consecutivo del folio  de Deducciones actual
			UPDATE 	gral_cons SET consecutivo=( SELECT sbt.consecutivo + 1  FROM gral_cons AS sbt WHERE sbt.id=gral_cons.id )
			WHERE gral_emp_id=emp_id AND gral_suc_id=suc_id AND gral_cons_tipo_id=id_tipo_consecutivo  RETURNING prefijo,consecutivo INTO prefijo_consecutivo,nuevo_consecutivo;
			
			--concatenamos el prefijo y el nuevo consecutivo para obtener el nuevo folio 
			nuevo_folio := prefijo_consecutivo || nuevo_consecutivo::character varying;
			nuevo_folio:= lpad(nuevo_folio, 3, '0');
			
			--str_data[4]	id
			--str_data clave nuevo_folio
			--str_data[5]	titulo
			--str_data[6]	activo
			--str_data[7]	tipopercepciones
			INSERT INTO nom_deduc (clave,titulo,activo,nom_deduc_tipo_id,borrado_logico,momento_creacion,gral_usr_id_crea,gral_emp_id,gral_suc_id) 
			VALUES (nuevo_folio,str_data[5],str_data[6]::boolean,str_data[7]::integer,false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE nom_deduc SET titulo=str_data[5],activo=str_data[6]::boolean,nom_deduc_tipo_id=str_data[7]::integer, momento_actualiza=now(), gral_usr_id_actualiza=usuario_id 
			WHERE nom_deduc.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE nom_deduc SET momento_baja=now(),borrado_logico=true WHERE nom_deduc.id = str_data[4]::integer; 
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina Catalogo Deducciones

	
	-- Catalogo de inventario de  Periodicidad de Pago
	IF app_selected = 172 THEN
		IF command_selected = 'new' THEN

			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	no_periodos
			--str_data[7]	activo
			INSERT INTO nom_periodicidad_pago (titulo,no_periodos,activo,borrado_logico,momento_creacion,gral_usr_id_crea,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5],str_data[6]::integer,str_data[7]::boolean,false,now(),usuario_id,emp_id,suc_id);
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE nom_periodicidad_pago SET titulo=str_data[5],no_periodos=str_data[6]::integer,activo=str_data[7]::boolean,momento_actualiza=now(), gral_usr_id_actualiza=usuario_id 
			WHERE nom_periodicidad_pago.id = str_data[4]::integer;
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE nom_periodicidad_pago SET momento_baja=now(),borrado_logico=true, gral_usr_id_baja=usuario_id  WHERE nom_periodicidad_pago.id = str_data[4]::integer; 
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina Catalogo Periodicidad de Pago

-- Catalogo de Configuración Periodicidad de Pago
	IF app_selected = 174 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data clave nuevo_folio
			--str_data[5]	año
			--str_data[6]	tipoperiodicidad
			--str_data[7]	descripcion
			
			INSERT INTO nom_periodos_conf (ano,nom_periodicidad_pago_id,prefijo,borrado_logico,momento_creacion,gral_usr_id_crea,gral_emp_id,gral_suc_id) 
			VALUES (str_data[5]::integer,str_data[6]::integer,str_data[7],false,now(),usuario_id,emp_id,suc_id)
			RETURNING id INTO ultimo_id;
			valor_retorno := '1';
			
			total_filas:= array_length(extra_data,1);
			cont_fila:=1;
			
			IF extra_data[1]<>'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
					--str_filas[1]	id_reg 
					--str_filas[2]	id_periodo 
					--str_filas[3]	folio 
					--str_filas[4]	tituloperiodo
					--str_filas[5]	fecha_inicio 
					--str_filas[6]	fecha_final
					
					--crea registro en nom_periodos_conf_det
					INSERT INTO nom_periodos_conf_det(
						nom_periodos_conf_id,--str_data[4]::integer,
						folio,--str_filas[3]	folio 
						titulo,--str_filas[4]	tituloperiodo 
						fecha_ini,--str_filas[5]	fecha_inicio
						fecha_fin--str_filas[6]	fecha_final
						
					 ) VALUES(ultimo_id, str_filas[3]::integer, str_filas[4], str_filas[5]::date,str_filas[6]::date);
					 --RETURNING id INTO ultimo_id;
					 valor_retorno := '1';
				END LOOP;
			END IF;
			
		END IF;
		
		IF command_selected = 'edit' THEN
			UPDATE nom_periodos_conf SET ano=str_data[5]::integer,nom_periodicidad_pago_id=str_data[6]::integer,prefijo=str_data[7],momento_actualiza=now(),gral_usr_id_actualiza=usuario_id 
			WHERE nom_periodos_conf.id = str_data[4]::integer;
			valor_retorno := '0';
			
			total_filas:= array_length(extra_data,1);
			cont_fila:=1;
			
			IF extra_data[1]<>'sin datos' THEN
				FOR cont_fila IN 1 .. total_filas LOOP 
					SELECT INTO str_filas string_to_array(extra_data[cont_fila],'___');
					--str_filas[1]	id_reg 
					--str_filas[2]	id_periodo 
					--str_filas[3]	folio 
					--str_filas[4]	tituloperiodo
					--str_filas[5]	fecha_inicio 
					--str_filas[6]	fecha_final
					
					UPDATE nom_periodos_conf_det SET folio=str_filas[3]::integer,titulo=str_filas[4],fecha_ini=str_filas[5]::date,fecha_fin=str_filas[6]::date
					WHERE nom_periodos_conf_det.id = str_filas[1]::integer;
					valor_retorno := '0';
				END LOOP;
			END IF;
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE nom_periodos_conf SET momento_baja=now(),borrado_logico=true 
			WHERE nom_periodos_conf.id = str_data[4]::integer;
			valor_retorno := '1';
		END IF;
	END IF;-- Catalogo de Configuración Periodicidad de Pago

	
	-- Catalogo de descuentos  de  clientes
	IF app_selected = 176 THEN
                IF command_selected = 'new' THEN
                        --str_data[4]         id
                        --str_data[5]         cliente
                        --str_data[6]         valor
                        INSERT INTO cxc_clie_descto (cxc_clie_id,tipo,valor) VALUES (str_data[5]::integer,1,str_data[6]::double precision);
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'edit' THEN
                        UPDATE cxc_clie_descto SET cxc_clie_id=str_data[5]::integer,valor=str_data[6]::double precision 
                        WHERE id = str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
                
                IF command_selected = 'delete' THEN
                        DELETE FROM cxc_clie_descto WHERE id=str_data[4]::integer;
                        valor_retorno := '1';
                END IF;
        END IF;--termina Catalogo descuentos  de  clientes

        
	--Catalogo de IVA Trasladado
	IF app_selected = 204 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	tasa
			--str_data[7]	cta_id
			
			if str_data[6]::double precision>0 then 
				str_data[6] := str_data[6]::double precision/100;
			end if;
			
			INSERT INTO gral_imptos (descripcion,iva_1,momento_creacion,gral_usr_id_crea,borrado_logico) VALUES (str_data[5],str_data[6]::double precision,espacio_tiempo_ejecucion,usuario_id,false) 
			RETURNING id INTO ultimo_id;

			if incluye_modulo_contabilidad then 
				IF EXISTS (SELECT * FROM information_schema.tables WHERE table_name='gral_impto_cta') THEN
					insert into gral_impto_cta(gral_impto_id,ctb_cta_id,momento_actualiza,gral_usr_id_actualiza,gral_suc_id) 
					values(ultimo_id,str_data[7]::integer,espacio_tiempo_ejecucion,usuario_id,suc_id);
				END IF;
			end if;
						
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN 
			if str_data[6]::double precision>0 then 
				str_data[6] := str_data[6]::double precision/100;
			end if;
			
			UPDATE gral_imptos SET descripcion=str_data[5],iva_1=str_data[6]::double precision,momento_actualizacion=espacio_tiempo_ejecucion,gral_usr_id_actualiza=usuario_id 
			WHERE id=str_data[4]::integer;

			if incluye_modulo_contabilidad then 
				IF EXISTS (SELECT * FROM information_schema.tables WHERE table_name='gral_impto_cta') THEN 
					if (select count(id) from gral_impto_cta where gral_impto_id=str_data[4]::integer)>0 then 
						update gral_impto_cta set ctb_cta_id=str_data[7]::integer, momento_actualiza=espacio_tiempo_ejecucion, gral_usr_id_actualiza=usuario_id 
						where gral_impto_id=str_data[4]::integer;
					else 
						insert into gral_impto_cta(gral_impto_id,ctb_cta_id,momento_actualiza,gral_usr_id_actualiza,gral_suc_id) 
						values(str_data[4]::integer,str_data[7]::integer,espacio_tiempo_ejecucion,usuario_id,suc_id);
					end if;
				END IF;
			end if;
			
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE gral_imptos SET momento_baja=espacio_tiempo_ejecucion,gral_usr_id_cancela=usuario_id,borrado_logico=true 
			WHERE id=str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina Catalogo de IVA Trasladado

	--Catalogo de IVA Retenido
	IF app_selected = 205 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	titulo
			--str_data[6]	tasa
			--str_data[7]	cta_id
			
			INSERT INTO gral_imptos_ret(titulo,tasa,momento_creacion,gral_usr_id_crea,borrado_logico) 
			VALUES (str_data[5],str_data[6]::double precision,espacio_tiempo_ejecucion,usuario_id,false) 
			RETURNING id INTO ultimo_id;
			
			if incluye_modulo_contabilidad then 
				IF EXISTS (SELECT * FROM information_schema.tables WHERE table_name='gral_impto_ret_cta') THEN
					insert into gral_impto_ret_cta(gral_impto_ret_id,ctb_cta_id,momento_actualiza,gral_usr_id_actualiza,gral_suc_id) 
					values(ultimo_id,str_data[7]::integer,espacio_tiempo_ejecucion,usuario_id,suc_id);
				END IF;
			end if;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN 
			UPDATE gral_imptos_ret SET titulo=str_data[5],tasa=str_data[6]::double precision,momento_actualizacion=espacio_tiempo_ejecucion,gral_usr_id_actualiza=usuario_id 
			WHERE id=str_data[4]::integer;
			
			if incluye_modulo_contabilidad then 
				IF EXISTS (SELECT * FROM information_schema.tables WHERE table_name='gral_impto_ret_cta') THEN 
					if (select count(id) from gral_impto_ret_cta where gral_impto_ret_id=str_data[4]::integer)>0 then 
						update gral_impto_ret_cta set ctb_cta_id=str_data[7]::integer, momento_actualiza=espacio_tiempo_ejecucion, gral_usr_id_actualiza=usuario_id 
						where gral_impto_ret_id=str_data[4]::integer;
					else 
						insert into gral_impto_ret_cta(gral_impto_ret_id,ctb_cta_id,momento_actualiza,gral_usr_id_actualiza,gral_suc_id) 
						values(str_data[4]::integer,str_data[7]::integer,espacio_tiempo_ejecucion,usuario_id,suc_id);
					end if;
				END IF;
			end if;
			
			valor_retorno := '0';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE gral_imptos_ret SET momento_baja=espacio_tiempo_ejecucion,gral_usr_id_cancela=usuario_id,borrado_logico=true 
			WHERE id=str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina Catalogo de IVA Retenido

	--Catalogo de Metodos de Pago
	IF app_selected = 209 THEN
		IF command_selected = 'new' THEN
			--str_data[4]	id
			--str_data[5]	clave
			--str_data[6]	titulo
			
			INSERT INTO fac_metodos_pago(clave_sat,titulo,momento_creacion,gral_usr_id_creacion,borrado_logico) 
			VALUES (str_data[5],str_data[6],espacio_tiempo_ejecucion,usuario_id,false);
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'edit' THEN 
			UPDATE fac_metodos_pago SET clave_sat=str_data[5],titulo=str_data[6],momento_actualiza=espacio_tiempo_ejecucion,gral_usr_id_actualizacion=usuario_id 
			WHERE id=str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
		
		IF command_selected = 'delete' THEN
			UPDATE fac_metodos_pago SET momento_baja=espacio_tiempo_ejecucion,gral_usr_id_baja=usuario_id,borrado_logico=true 
			WHERE id=str_data[4]::integer;
			
			valor_retorno := '1';
		END IF;
	END IF;
	--Termina Catalogo de Metodos de Pago

	
	
	
	
	RETURN valor_retorno;
	
END;
$BODY$;
