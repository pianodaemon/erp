import unidecode
import math
import os
import base64
import datetime
from decimal import Decimal
from misc.tricks import truncate
from .gen import BuilderGen
from .error import DocBuilderStepError

class FactRepr(BuilderGen):

    __NDECIMALS = 2

    def __init__(self):
        pass

    def __q_serie_folio(self, conn, usr_id):
        """
        Consulta la serie y folio a usar en dbms
        """
        SQL = """select fac_cfds_conf_folios.serie as serie,
            fac_cfds_conf_folios.folio_actual::character varying as folio
            FROM gral_suc AS SUC
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id = fac_cfds_conf.id
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            WHERE fac_cfds_conf_folios.proposito = 'FAC'
            AND USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            # Just taking first row of query result
            return {
                'serie': row['serie'],
                'folio': row['folio']
            }

    def __q_emisor(self, conn, usr_id):
        """
        Consulta el emisor en dbms
        """
        SQL = """select upper(EMP.rfc) as rfc, upper(EMP.titulo) as titulo,
            upper(REG.numero_control) as numero_control
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN gral_emp AS EMP ON EMP.id = SUC.empresa_id
            LEFT JOIN cfdi_regimenes AS REG ON REG.numero_control = EMP.regimen_fiscal
            WHERE USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            # Just taking first row of query result
            return {
                'rfc': row['rfc'],
                'nombre': unidecode.unidecode(row['titulo']),
                'regimen_fiscal': row['numero_control']
            }

    def __q_lugar_expedicion(self, conn, usr_id):
        """
        Consulta el lugar de expedicion en dbms
        """
        SQL = """select SUC.cp
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc as USR_SUC ON USR_SUC.gral_suc_id=SUC.id
            WHERE USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            # Just taking first row of query result
            return row['cp']

    def __q_metodo_pago(self, conn, prefact_id):
        """
        Consulta el metodo de pago
        """
        SQL = """SELECT MP.clave
            FROM erp_prefacturas as EP
            JOIN cfdi_metodos_pago as MP ON EP.cfdi_metodo_id = MP.id
            WHERE EP.id = {}"""
        for row in self.pg_query(conn, SQL.format(prefact_id)):
            # Just taking first row of query result
            return row['clave']

    def __q_forma_pago(self, conn, prefact_id):
        """
        Consulta la forma de pago y numero de cuenta
        """
        SQL= """SELECT FP.clave_sat, EP.no_cuenta
            FROM erp_prefacturas as EP
            JOIN fac_metodos_pago as FP ON EP.fac_metodos_pago_id = FP.id
            WHERE EP.id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, prefact_id)):
            # Just taking first row of query result
            return {
                'clave': row['clave_sat'],
                'cuenta': row['no_cuenta']
            }


    def __q_moneda(self, conn, prefact_id):
        """
        Consulta la moneda de la prefactura en dbms
        """
        SQL = """SELECT
            upper(gral_mon.iso_4217) AS moneda_iso_4217,
            upper(gral_mon.simbolo) AS moneda_simbolo,
            erp_prefacturas.tipo_cambio
            FROM erp_prefacturas
            LEFT JOIN gral_mon ON gral_mon.id=erp_prefacturas.moneda_id
            WHERE erp_prefacturas.id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, prefact_id)):
            # Just taking first row of query result
            return {
                'iso_4217': row['moneda_iso_4217'],
                'simbolo': row['moneda_simbolo'],
                'tipo_de_cambio': row['tipo_cambio']
            }

    def __q_receptor(self, conn, prefact_id):
        """
        Consulta el cliente de la prefactura en dbms
        """
        SQL = """SELECT
            upper(cxc_clie.razon_social) as razon_social,
            upper(cxc_clie.rfc) as rfc,
            cfdi_usos.numero_control as uso,
            cxc_clie.cp as cp,
            cxc_clie.cxc_clie_grupo_id::character varying as regimen_fiscal_receptor,
            (select abreviacion from gral_pais where id=cxc_clie.pais_id) as residencia_fiscal
            FROM erp_prefacturas
            LEFT JOIN cxc_clie ON cxc_clie.id = erp_prefacturas.cliente_id
            LEFT JOIN cfdi_usos ON cfdi_usos.id = erp_prefacturas.cfdi_usos_id
            WHERE erp_prefacturas.id = {}"""
        for row in self.pg_query(conn, SQL.format(prefact_id)):
            # Just taking first row of query result
            return {
                'rfc': row['rfc'],
                'nombre': unidecode.unidecode(row['razon_social']),
                'domicilio_fiscal_receptor': row['cp'],
                'residencia_fiscal': row['residencia_fiscal'],
                'regimen_fiscal_receptor': row['regimen_fiscal_receptor'],
                'uso_cfdi': row['uso']
            }

    def __q_conceptos(self, conn, prefact_id):
        """
        Consulta los conceptos de la prefactura en dbms
        """
        SQL = """SELECT upper(inv_prod.sku) as sku,
            upper(inv_prod.descripcion) as descripcion,
            cfdi_claveprodserv.clave AS prodserv,
            cfdi_claveunidad.clave AS unidad,
            erp_prefacturas_detalles.cant_facturar AS cantidad,
            erp_prefacturas_detalles.precio_unitario,
            (
              erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario
            ) AS importe,
            (
              (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) *
              (erp_prefacturas_detalles.descto::double precision/100)
            ) AS descto,
            -- From this point onwards tax related columns
            (
              (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) *
              erp_prefacturas_detalles.valor_ieps
            ) AS importe_ieps,
            (
              (
                (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) +
                (
                  (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) *
                  erp_prefacturas_detalles.valor_ieps
                )
              ) * erp_prefacturas_detalles.valor_imp
            ) AS importe_impuesto,
            (
                (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) *
                erp_prefacturas_detalles.tasa_ret
            ) AS importe_ret,
            (erp_prefacturas_detalles.valor_ieps * 100::double precision) AS tasa_ieps,
            (erp_prefacturas_detalles.valor_imp * 100::double precision) AS tasa_impuesto,
            (erp_prefacturas_detalles.tasa_ret * 100::double precision) AS tasa_ret,
            erp_prefacturas_detalles.gral_ieps_id as ieps_id,
            erp_prefacturas_detalles.tipo_impuesto_id as impto_id,
            erp_prefacturas_detalles.gral_imptos_ret_id as ret_id,
            erp_prefacturas_detalles.producto_id,
            erp_prefacturas_detalles.inv_prod_alias_id
            FROM erp_prefacturas
            JOIN erp_prefacturas_detalles on erp_prefacturas_detalles.prefacturas_id=erp_prefacturas.id
            LEFT JOIN inv_prod on inv_prod.id = erp_prefacturas_detalles.producto_id
            LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = erp_prefacturas_detalles.inv_prod_unidad_id
            LEFT JOIN inv_prod_tipos on inv_prod_tipos.id = inv_prod.tipo_de_producto_id
            LEFT JOIN cfdi_claveunidad on inv_prod_unidades.cfdi_unidad_id = cfdi_claveunidad.id
            LEFT JOIN cfdi_claveprodserv on inv_prod.cfdi_prodserv_id = cfdi_claveprodserv.id
            WHERE erp_prefacturas_detalles.prefacturas_id="""
        order_clause = ' ORDER BY erp_prefacturas_detalles.id ASC;'

        alias_sql = '''SELECT descripcion FROM inv_prod_alias WHERE producto_id = {0} AND alias_id = {1};'''

        rowset = []
        for row in self.pg_query(conn, "{0}{1}{2}".format(SQL, prefact_id, order_clause)):

            rowset.append({
                'sku': row['sku'],
                'descripcion': unidecode.unidecode(row['descripcion']),
                'unidad': row['unidad'],
                'prodserv': row['prodserv'],
                'cantidad': row['cantidad'],
                'precio_unitario': self.__narf(row['precio_unitario']),
                'importe': row['importe'],
                'descto': truncate(row['descto'], self.__NDECIMALS),
                # From this point onwards tax related elements
                'importe_ieps': row['importe_ieps'],
                'importe_impuesto' : row['importe_impuesto'],
                'importe_ret' : row['importe_ret'],
                'tasa_ieps': row['tasa_ieps'],
                'tasa_impuesto': row['tasa_impuesto'],
                'tasa_ret': row['tasa_ret'],
                'ieps_id': row['ieps_id'],
                'impuesto_id': row['impto_id'],
                'ret_id': row['ret_id']
            })
        return rowset

    def __calc_totales(self, l_items):
        totales = {
            'monto_total': Decimal(0),
            'importe_sum': Decimal(0),
            'importe_sum_impuesto': Decimal(0),
            'importe_sum_ieps': Decimal(0),
            'importe_sum_ret': Decimal(0),
            'descto_sum': Decimal(0),
        }

        for item in l_items:
            totales['importe_sum'] += self.__narf(item['importe'])
            totales['descto_sum'] += self.__narf(item['descto'])
            totales['importe_sum_ieps'] += self.__narf(
                self.__calc_imp_tax(
                    self.__abs_importe(item),
                    self.__place_tasa(item['tasa_ieps'])
                )
            )
            totales['importe_sum_impuesto'] += self.__narf(
                 self.__calc_imp_tax(
                    self.__calc_base(self.__abs_importe(item), self.__place_tasa(item['tasa_ieps'])),
                    self.__place_tasa(item['tasa_impuesto'])
                 )
            )
            totales['importe_sum_ret'] += self.__narf(
                self.__calc_imp_tax(
                    self.__abs_importe(item),
                    self.__place_tasa(item['tasa_ret'])
                )
            )

        totales['monto_total'] = self.__narf(totales['importe_sum']) - self.__narf(totales['descto_sum']) + self.__narf(totales['importe_sum_ieps']) + self.__narf(totales['importe_sum_impuesto']) - self.__narf(totales['importe_sum_ret'])
        return {k: truncate(float(v), self.__NDECIMALS) for k, v in totales.items()}

    def __calc_retenciones(self, l_items, l_riva):
        """
        Calcula los impuestos retenidos
        """
        retenciones = []

        for tax in l_riva:
            # next two variables shall get lastest value of loop
            # It's not me. It is the Noe approach :|
            impto_id = 0
            base_sum = Decimal(0)
            importe_sum = Decimal(0)
            for item in l_items:
                if tax['id'] == item['ret_id']:
                    impto_id = item['ret_id']
                    base = self.__abs_importe(item)
                    importe_sum += self.__narf(self.__calc_imp_tax(
                        base,
                        self.__place_tasa(item['tasa_ret'])
                    ))
                    base_sum += base
            if impto_id > 0:
                retenciones.append({
                    'impuesto': '002',
                    'importe': truncate(float(importe_sum), self.__NDECIMALS)
                })
        return retenciones

    def __calc_traslados(self, l_items, l_ieps, l_iva):
        """
        Calcula los impuestos trasladados
        """
        traslados = []

        for tax in l_iva:
            # next two variables shall get lastest value of loop
            # It's not me. It is the Noe approach :|
            impto_id = 0
            tasa = 0
            base_sum = Decimal(0)
            importe_sum = Decimal(0)
            for item in l_items:
                if tax['id'] == item['impuesto_id']:
                    impto_id = item['impuesto_id']
                    tasa = item['tasa_impuesto']
                    base = self.__calc_base(self.__abs_importe(item), self.__place_tasa(item['tasa_ieps']))
                    importe_sum += self.__narf(self.__calc_imp_tax(
                        base,
                        self.__place_tasa(item['tasa_impuesto'])
                    ))
                    base_sum += base
            if impto_id > 0:
                traslados.append({
                    'base': base_sum,
                    'impuesto': '002',
                    'tipo_factor': 'Tasa',
                    'tasa_o_cuota': tasa / 100.0,
                    'importe': truncate(float(importe_sum), self.__NDECIMALS)
                })

        for tax in l_ieps:
            # next two variables shall get lastest value of loop
            # It's not me. It is the Noe approach :|
            impto_id = 0
            tasa = 0
            base_sum = Decimal(0)
            importe_sum = Decimal(0)
            for item in l_items:
                if tax['id'] == item['ieps_id']:
                    impto_id = item['ieps_id']
                    tasa = item['tasa_ieps']
                    base = self.__abs_importe(item)
                    importe_sum += self.__narf(self.__calc_imp_tax(
                        base,
                        self.__place_tasa(item['tasa_ieps'])
                    ))
                    base_sum += base
            if impto_id > 0:
                traslados.append({
                    'base': base_sum,
                    'impuesto': '003',
                    'tipo_factor': 'Tasa',
                    'tasa_o_cuota': tasa / 100.0,
                    'importe': truncate(float(importe_sum), self.__NDECIMALS)
                })
        return traslados

    def __q_ivas(self, conn):
        """
        Consulta el total de IVA activos en dbms
        """
        SQL = """SELECT id, descripcion AS titulo, iva_1 AS tasa
            FROM gral_imptos
            WHERE borrado_logico=false"""
        rowset = []
        for row in self.pg_query(conn, SQL):
            rowset.append({
                'id' : row['id'],
                'desc': row['titulo'],
                'tasa': row['tasa']
            })
        return rowset

    def __q_rivas(self, conn):
        SQL="""SELECT id, descripcion AS titulo, tasa
            FROM public.gral_imptos_ret
            WHERE borrado_logico=false"""
        return [{'id' : row['id'], 'desc': row['titulo'], 'tasa': row['tasa']} for row in self.pg_query(conn, SQL)]

    def __q_ieps(self, conn, usr_id):
        """
        Consulta el total de lo IEPS activos en dbms
        """
        SQL = """SELECT gral_ieps.id as id, cci.clave as clave,
            gral_ieps.titulo as desc, gral_ieps.tasa as tasa
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN gral_emp AS EMP ON EMP.id = SUC.empresa_id
            LEFT JOIN gral_ieps ON gral_ieps.gral_emp_id = EMP.id
            LEFT JOIN cfdi_c_impuesto AS cci ON cci.id = gral_ieps.cfdi_c_impuesto
            WHERE gral_ieps.borrado_logico=false AND
            USR_SUC.gral_usr_id="""
        rowset = []
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            rowset.append({
                'id' : row['id'],
                'clave': row['clave'],
                'desc': row['desc'],
                'tasa': row['tasa']
            })
        return rowset

    def __q_sign_params(self, conn, usr_id):
        """
        Consulta parametros requeridos para firmado cfdi
        """
        SQL = """SELECT fac_cfds_conf.archivo_llave as pk
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            WHERE USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            # Just taking first row of query result
            return {
                'pkname': row['pk']
            }

    def data_acq(self, conn, **kwargs):

        usr_id = kwargs.get('usr_id', None)
        prefact_id = kwargs.get('prefact_id', None)
        serie = kwargs.get('serie_arg', None)
        folio = kwargs.get('folio_arg', None)

        if usr_id is None:
            raise DocBuilderStepError("user id not fed")
        if prefact_id is None:
            raise DocBuilderStepError("prefact id not fed")
        if serie is None:
            raise DocBuilderStepError("serie not fed")
        if folio is None:
            raise DocBuilderStepError("folio not fed")

        ed = self.__q_emisor(conn, usr_id)
        conceptos = self.__q_conceptos(conn, prefact_id)
        traslados = self.__calc_traslados(conceptos,
            self.__q_ieps(conn, usr_id), self.__q_ivas(conn))
        retenciones = self.__calc_retenciones(conceptos,
            self.__q_rivas(conn))
        forma_pago = self.__q_forma_pago(conn, prefact_id)
        totales = self.__calc_totales(conceptos)
        moneda = self.__q_moneda(conn, prefact_id)

        tot_traslados = 0.0
        for t in traslados:
            tot_traslados += t['importe']

        tot_retenciones = 0.0
        for r in retenciones:
            tot_retenciones += r['importe']

        impuestos = {
            'total_impuestos_retenidos': tot_retenciones,
            'total_impuestos_trasladados': tot_traslados,
            'retenciones': retenciones,
            'traslados': traslados
        }

        shaped_conceptos = self.__shape_conceptos(conceptos)

        return {
            'serie': serie,
            'folio': folio,
            'fecha': '{0:%Y-%m-%dT%H:%M:%S}'.format(datetime.datetime.now()),
            'forma_pago': forma_pago['clave'],
            'subtotal': totales['importe_sum'],
            'descuento': totales['descto_sum'],
            'moneda': moneda['iso_4217'],
            'tipo_cambio': moneda['tipo_de_cambio'],
            'total': totales['monto_total'],
            'tipo_de_comprobante': 'I',
            'exportacion': '01',
            'metodo_pago': self.__q_metodo_pago(conn, prefact_id),
            'lugar_expedicion': self.__q_lugar_expedicion(conn, usr_id),
            'emisor': ed,
            'receptor': self.__q_receptor(conn, prefact_id),
            'conceptos': shaped_conceptos,
            'impuestos': impuestos
        }

    def __place_tasa(self, x):
        """
        smart method to deal with a tasa less
        than zero or greater than zero
        """
        try:
            return x * 10 ** -2 if math.log10(x) >= 0 else x
        except ValueError:
            # Silent the error and just return value passed
            return x

    def __narf(self, v):
        return  Decimal(truncate(float(v), self.__NDECIMALS, True))

    def __calc_imp_tax(self, imp, tasa):
        return truncate(
            float( Decimal(imp) * Decimal(tasa) ),
            self.__NDECIMALS
        )

    def __calc_base(self, imp, tasa):
        return self.__narf(
            Decimal(imp) + Decimal( self.__calc_imp_tax(imp, tasa) )
        )

    def __abs_importe(self, a):
        return float(Decimal(str(a['importe'])) - Decimal(str(a['descto'])))

    def __shape_conceptos(self, conceptos):
        shaped_conceptos = []

        for row in conceptos:

            conc_traslados = []
            if row['impuesto_id'] > 0:
                conc_traslados.append(
                    {
                        'base': row['importe'],
                        'impuesto': '002',
                        'tipo_factor': 'Tasa',
                        'tasa_o_cuota': row['tasa_impuesto'] / 100.0,
                        'importe' : row['importe_impuesto'],
                    }
                )
            if row['ieps_id'] > 0:
                conc_traslados.append(
                    {
                        'base': row['importe'],
                        'impuesto': '003',
                        'tipo_factor': 'Tasa',
                        'tasa_o_cuota': row['tasa_ieps'] / 100.0,
                        'importe' : row['importe_ieps'],
                    }
                )

            conc_retenciones = []
            if row['ret_id'] > 0:
                conc_retenciones.append(
                    {
                        'base': row['importe'],
                        'impuesto': '002',
                        'tipo_factor': 'Tasa',
                        'tasa_o_cuota': row['tasa_ret'],
                        'importe' : row['importe_ret'],
                    }
                )
            
            shaped_conceptos.append({
                'clave_prod_serv': row['prodserv'],
                'no_identificacion': row['sku'],
                'cantidad': row['cantidad'],
                'clave_unidad': row['unidad'],
                'unidad': 'propia',
                'descripcion': unidecode.unidecode(row['descripcion']),
                'valor_unitario': self.__narf(row['precio_unitario']),
                'importe': row['importe'],
                'descuento': truncate(row['descto'], self.__NDECIMALS),
                'objeto_imp': '02',
                'traslados': conc_traslados,
                'retenciones': conc_retenciones
            })

        return shaped_conceptos
