import unidecode
import math
import os
import base64
import datetime
import tempfile
import pyxb
from decimal import Decimal
from misc.helperstr import HelperStr
from docmaker.error import DocBuilderStepError
from misc.tricks import truncate
from docmaker.gen import BuilderGen
from sat.v33 import Comprobante
from sat.requirement import writedom_cfdi, sign_cfdi
from sat.artifacts import CfdiType


impt_class='FacXml'


class FacXml(BuilderGen):

    __NDECIMALS = 2
    __MAKEUP_PROPOS = CfdiType.FAC

    def __init__(self, logger):
        super().__init__(logger)

    def __q_no_certificado(self, conn, usr_id):
        """
        Consulta el numero de certificado en dbms
        """
        SQL =  """select CFDI_CONF.numero_certificado
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf AS CFDI_CONF ON CFDI_CONF.gral_suc_id = SUC.id
            WHERE USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            # Just taking first row of query result
            return row['numero_certificado']

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
                'SERIE': row['serie'],
                'FOLIO': row['folio']
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
                'RFC': row['rfc'],
                'RAZON_SOCIAL': unidecode.unidecode(row['titulo']),
                'REGIMEN_FISCAL': row['numero_control']
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
                'CLAVE': row['clave_sat'],
                'CUENTA': row['no_cuenta']
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
                'ISO_4217': row['moneda_iso_4217'],
                'SIMBOLO': row['moneda_simbolo'],
                'TIPO_DE_CAMBIO': row['tipo_cambio']
            }

    def __q_receptor(self, conn, prefact_id):
        """
        Consulta el cliente de la prefactura en dbms
        """
        SQL = """SELECT
            upper(cxc_clie.razon_social) as razon_social,
            upper(cxc_clie.rfc) as rfc,
            cfdi_usos.numero_control as uso
            FROM erp_prefacturas
            LEFT JOIN cxc_clie ON cxc_clie.id = erp_prefacturas.cliente_id
            LEFT JOIN cfdi_usos ON cfdi_usos.id = erp_prefacturas.cfdi_usos_id
            WHERE erp_prefacturas.id = {}"""
        for row in self.pg_query(conn, SQL.format(prefact_id)):
            # Just taking first row of query result
            return {
                'RFC': row['rfc'],
                'RAZON_SOCIAL': unidecode.unidecode(row['razon_social']),
                'USO_CFDI': row['uso']
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
            (erp_prefacturas_detalles.tasa_ret * 100::double precision) AS ret_tasa,
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

        alias_sql = '''SELECT descripcion FROM inv_prod_alias WHERE producto_id = {0} AND alias_id = {1};'''

        rowset = []
        for row in self.pg_query(conn, "{0}{1}".format(SQL, prefact_id)):

            alias = ''
            if row['inv_prod_alias_id'] > 0:
                alias_rowset = self.pg_query(conn, alias_sql.format(row['producto_id'], row['inv_prod_alias_id']))
                if alias_rowset:
                    alias = alias_rowset[0]['descripcion']

            rowset.append({
                'SKU': row['sku'],
                'DESCRIPCION': unidecode.unidecode(row['descripcion']),
                'UNIDAD': row['unidad'],
                'PRODSERV': row['prodserv'],
                'CANTIDAD': row['cantidad'],
                'PRECIO_UNITARIO': self.__narf(row['precio_unitario']),
                'IMPORTE': row['importe'],
                'DESCTO': truncate(row['descto'], self.__NDECIMALS),
                # From this point onwards tax related elements
                'IMPORTE_IEPS': row['importe_ieps'],
                'IMPORTE_IMPUESTO' : row['importe_impuesto'],
                'IMPORTE_RET' : row['importe_ret'],
                'TASA_IEPS': row['tasa_ieps'],
                'TASA_IMPUESTO': row['tasa_impuesto'],
                'TASA_RET': row['ret_tasa'],
                'IEPS_ID': row['ieps_id'],
                'IMPUESTO_ID': row['impto_id'],
                'RET_ID': row['ret_id'],
                'PROD_ALIAS': alias
            })
        return rowset

    def __calc_totales(self, l_items):
        totales = {
            'MONTO_TOTAL': Decimal(0),
            'IMPORTE_SUM': Decimal(0),
            'IMPORTE_SUM_IMPUESTO': Decimal(0),
            'IMPORTE_SUM_IEPS': Decimal(0),
            'IMPORTE_SUM_RET': Decimal(0),
            'DESCTO_SUM': Decimal(0),
        }

        for item in l_items:
            totales['IMPORTE_SUM'] += self.__narf(item['IMPORTE'])
            totales['DESCTO_SUM'] += self.__narf(item['DESCTO'])
            totales['IMPORTE_SUM_IEPS'] += self.__narf(
                self.__calc_imp_tax(
                    self.__abs_importe(item),
                    self.__place_tasa(item['TASA_IEPS'])
                )
            )
            totales['IMPORTE_SUM_IMPUESTO'] += self.__narf(
                 self.__calc_imp_tax(
                    self.__calc_base(self.__abs_importe(item), self.__place_tasa(item['TASA_IEPS'])),
                    self.__place_tasa(item['TASA_IMPUESTO'])
                 )
            )
            totales['IMPORTE_SUM_RET'] += self.__narf(
                self.__calc_imp_tax(
                    self.__abs_importe(item),
                    self.__place_tasa(item['TASA_RET'])
                )
            )

        totales['MONTO_TOTAL'] = self.__narf(totales['IMPORTE_SUM']) - self.__narf(totales['DESCTO_SUM']) + self.__narf(totales['IMPORTE_SUM_IEPS']) + self.__narf(totales['IMPORTE_SUM_IMPUESTO']) - self.__narf(totales['IMPORTE_SUM_RET'])
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
            tasa = 0
            importe_sum = Decimal(0)
            for item in l_items:
                if tax['ID'] == item['RET_ID']:
                    impto_id = item['RET_ID']
                    tasa = item['TASA_RET']
                    importe_sum += self.__narf(self.__calc_imp_tax(
                        self.__abs_importe(item), self.__place_tasa(item['TASA_RET'])
                    ))
            if impto_id > 0:
                retenciones.append({
                    'impuesto': 'IVA',
                    'clave': '002',
                    'importe': truncate(float(importe_sum), self.__NDECIMALS),
                    'tasa': tasa
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
            importe_sum = Decimal(0)
            for item in l_items:
                if tax['ID'] == item['IMPUESTO_ID']:
                    impto_id = item['IMPUESTO_ID']
                    tasa = item['TASA_IMPUESTO']  
                    importe_sum += self.__narf(self.__calc_imp_tax(
                        self.__calc_base(self.__abs_importe(item), self.__place_tasa(item['TASA_IEPS'])),
                        self.__place_tasa(item['TASA_IMPUESTO'])
                    ))
            if impto_id > 0:
                traslados.append({
                    'impuesto': 'IVA',
                    'clave': '002',
                    'importe': truncate(float(importe_sum), self.__NDECIMALS),
                    'tasa': tasa
                })

        for tax in l_ieps:
            # next two variables shall get lastest value of loop
            # It's not me. It is the Noe approach :|
            impto_id = 0
            tasa = 0
            importe_sum = Decimal(0)
            for item in l_items:
                if tax['ID'] == item['IEPS_ID']:
                    impto_id = item['IEPS_ID']
                    tasa = item['TASA_IEPS']
                    importe_sum += self.__narf(self.__calc_imp_tax(
                        self.__abs_importe(item), self.__place_tasa(item['TASA_IEPS'])
                    ))
            if impto_id > 0:
                traslados.append({
                    'impuesto': 'IEPS',
                    'clave': '003',
                    'importe': truncate(float(importe_sum), self.__NDECIMALS),
                    'tasa': tasa
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
                'ID' : row['id'],
                'DESC': row['titulo'],
                'TASA': row['tasa']
            })
        return rowset

    def __q_rivas(self, conn):
        SQL="""SELECT id, descripcion AS titulo, tasa
            FROM public.gral_imptos_ret
            WHERE borrado_logico=false"""
        return [{'ID' : row['id'], 'DESC': row['titulo'], 'TASA': row['tasa']} for row in self.pg_query(conn, SQL)]

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
                'ID' : row['id'],
                'CLAVE': row['clave'],
                'DESC': row['desc'],
                'TASA': row['tasa']
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
                'PKNAME': row['pk']
            }

    def __q_cert_file(self, conn, usr_id):
        """
        Consulta el certificado que usa el usuario en dbms
        """
        SQL = """select fac_cfds_conf.archivo_certificado as cert_file
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc ON gral_usr_suc.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            WHERE gral_usr_suc.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(SQL, usr_id)):
            # Just taking first row of query result
            return row['cert_file']

    def data_acq(self, conn, d_rdirs, **kwargs):

        usr_id = kwargs.get('usr_id', None)
        prefact_id = kwargs.get('prefact_id', None)

        if usr_id is None:
            raise DocBuilderStepError("user id not fed")
        if prefact_id is None:
            raise DocBuilderStepError("prefact id not fed")

        ed = self.__q_emisor(conn, usr_id)
        sp = self.__q_sign_params(conn, usr_id)

        # dirs with full emisor rfc path
        sslrfc_dir = os.path.join(d_rdirs['ssl'], ed['RFC'])

        cert_file = os.path.join(
                       sslrfc_dir, self.__q_cert_file(conn, usr_id))
        certb64 = None
        with open(cert_file, 'rb') as f:
            content = f.read()
            certb64 = base64.b64encode(content).decode('ascii')

        conceptos = self.__q_conceptos(conn, prefact_id)
        traslados = self.__calc_traslados(conceptos,
            self.__q_ieps(conn, usr_id), self.__q_ivas(conn))
        retenciones = self.__calc_retenciones(conceptos,
            self.__q_rivas(conn))

        return {
            'TIME_STAMP': '{0:%Y-%m-%dT%H:%M:%S}'.format(datetime.datetime.now()),
            'CONTROL': self.__q_serie_folio(conn, usr_id),
            'CERT_B64': certb64,
            'KEY_PRIVATE': os.path.join(sslrfc_dir, sp['PKNAME']),
            'XSLT_SCRIPT': os.path.join(d_rdirs['cfdi_xslt'], "cadenaoriginal_3_3.xslt"),
            'EMISOR': ed,
            'NUMERO_CERTIFICADO': self.__q_no_certificado(conn, usr_id),
            'RECEPTOR': self.__q_receptor(conn, prefact_id),
            'METODO_PAGO': self.__q_metodo_pago(conn, prefact_id),
            'MONEDA': self.__q_moneda(conn, prefact_id),
            'FORMA_PAGO': self.__q_forma_pago(conn, prefact_id),
            'LUGAR_EXPEDICION': self.__q_lugar_expedicion(conn, usr_id),
            'CONCEPTOS': conceptos,
            'TRASLADOS': traslados,
            'RETENCIONES' : retenciones,
            'TOTALES': self.__calc_totales(conceptos)
        }

    def format_wrt(self, output_file, dat):
        self.logger.debug('dumping contents of dat: {}'.format(repr(dat)))

        def save(xo):
            tmp_dir = tempfile.gettempdir()
            f = os.path.join(tmp_dir, HelperStr.random_str())
            writedom_cfdi(xo.toDOM(), self.__MAKEUP_PROPOS, f)
            return f

        c = Comprobante()
        c.Version = '3.3'
        c.Serie = dat['CONTROL']['SERIE']  # optional
        c.Folio = dat['CONTROL']['FOLIO']  # optional
        c.Fecha = dat['TIME_STAMP']
        c.Sello = '__DIGITAL_SIGN_HERE__'
        c.FormaPago = dat["FORMA_PAGO"]['CLAVE']  # optional
        c.NoCertificado = dat['NUMERO_CERTIFICADO']
        c.Certificado = dat['CERT_B64']
        c.SubTotal = dat['TOTALES']['IMPORTE_SUM']
        c.Descuento = dat['TOTALES']['DESCTO_SUM'] if dat['TOTALES']['DESCTO_SUM'] > 0 else None
        c.Total = dat['TOTALES']['MONTO_TOTAL']
        if dat['MONEDA']['ISO_4217'] == 'MXN':
            c.TipoCambio = 1
        else:
            # optional (requerido en ciertos casos)
            c.TipoCambio = truncate(dat['MONEDA']['TIPO_DE_CAMBIO'], self.__NDECIMALS)
        c.Moneda = dat['MONEDA']['ISO_4217']
        c.TipoDeComprobante = 'I'
        c.MetodoPago = dat['METODO_PAGO']  # optional
        c.LugarExpedicion = dat['LUGAR_EXPEDICION']

        c.Emisor = pyxb.BIND()
        c.Emisor.Nombre = dat['EMISOR']['RAZON_SOCIAL']  # optional
        c.Emisor.Rfc = dat['EMISOR']['RFC']
        c.Emisor.RegimenFiscal = dat['EMISOR']['REGIMEN_FISCAL']

        c.Receptor = pyxb.BIND()
        c.Receptor.Nombre = dat['RECEPTOR']['RAZON_SOCIAL']  # optional
        c.Receptor.Rfc = dat['RECEPTOR']['RFC']
        c.Receptor.UsoCFDI = dat['RECEPTOR']['USO_CFDI']

        c.Conceptos = pyxb.BIND()
        for i in dat['CONCEPTOS']:
            alias = i['PROD_ALIAS'] if i['PROD_ALIAS'] else i['DESCRIPCION']
            c.Conceptos.append(pyxb.BIND(
                Cantidad=i['CANTIDAD'],
                ClaveUnidad=i['UNIDAD'],
                ClaveProdServ=i['PRODSERV'],
                Descripcion=alias,
                ValorUnitario=i['PRECIO_UNITARIO'],
                NoIdentificacion=i['SKU'],  # optional
                Importe=truncate(i['IMPORTE'], self.__NDECIMALS),
                Descuento=i['DESCTO'] if i['DESCTO'] > 0 else None,
                Impuestos=self.__tag_impuestos(i) if i['TASA_IMPUESTO'] > 0 else None
            ))

        def traslado(c, tc, imp):
            return pyxb.BIND(TipoFactor='Tasa',
                Impuesto=c, TasaOCuota=tc, Importe=imp)

        def retencion(c, imp):
            return pyxb.BIND(Impuesto=c, Importe=imp)

        def zigma(v):
            z = Decimal(0)
            for w in v:
                z += self.__narf(w['importe'])
            return float(z)

        c.Impuestos = pyxb.BIND(
            TotalImpuestosRetenidos=zigma(dat['RETENCIONES']) if zigma(dat['RETENCIONES']) > 0 else None,
            TotalImpuestosTrasladados=zigma(dat['TRASLADOS']),
            Traslados=pyxb.BIND(
                *tuple([traslado(t['clave'], self.__place_tasa(t['tasa']), t['importe']) for t in dat['TRASLADOS']])
            ),
            Retenciones=pyxb.BIND(
                *tuple([retencion(t['clave'], t['importe']) for t in dat['RETENCIONES']])
            ) if dat['RETENCIONES'] else None
        )

        tmp_file = save(c)
        HelperStr.edit_pattern('TipoCambio="1.0"', 'TipoCambio="1"', tmp_file)  # XXX: Horrible workaround
        HelperStr.edit_pattern('(Descuento=)"([0-9]*(\.[0-9]{0,1})?)"', lambda x: 'Descuento="%.2f"' % (float(x.group(2)),), tmp_file)
        HelperStr.edit_pattern('(Importe=)"([0-9]*(\.[0-9]{0,1})?)"', lambda x: 'Importe="%.2f"' % (float(x.group(2)),), tmp_file)
        HelperStr.edit_pattern('(TasaOCuota=)"([0-9]*(\.[0-9]{0,2})?)"', lambda x: 'TasaOCuota="%.6f"' % (float(x.group(2)),), tmp_file)
        with open(output_file, 'w', encoding="utf-8") as a:
            a.write(sign_cfdi(dat['KEY_PRIVATE'], dat['XSLT_SCRIPT'], tmp_file))
        os.remove(tmp_file)

    def data_rel(self, dat):
        pass

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
        return float(Decimal(str(a['IMPORTE'])) - Decimal(str(a['DESCTO'])))


    def __tag_traslados(self, i):

        def traslado(b, c, tc, imp):
            return pyxb.BIND(
                Base=b, TipoFactor='Tasa',
                Impuesto=c, TasaOCuota=tc, Importe=imp)

        taxes = []
        if i['IMPORTE_IMPUESTO'] > 0:
            base = self.__calc_base(self.__abs_importe(i), self.__place_tasa(i['TASA_IEPS']))
            taxes.append(
                traslado(
                    base, "002", self.__place_tasa(i['TASA_IMPUESTO']), self.__calc_imp_tax(
                        base, self.__place_tasa(i['TASA_IMPUESTO'])
                    )
                )
            )
        if i['IMPORTE_IEPS'] > 0:
            taxes.append(
                traslado(
                    i['IMPORTE'], "003", self.__place_tasa(i['TASA_IEPS']), self.__calc_imp_tax(
                        i['IMPORTE'], self.__place_tasa(i['TASA_IEPS'])
                    )
                )
            )
        return pyxb.BIND(*tuple(taxes))

    def __tag_retenciones(self, i):

        def retencion(b, c, tc, imp):
            return pyxb.BIND(
                Base=b, TipoFactor='Tasa',
                Impuesto=c, TasaOCuota=tc, Importe=imp)

        taxes = []
        if i['IMPORTE_RET'] > 0:
            taxes.append(
                retencion(
                    i['IMPORTE'], "002", self.__place_tasa(i['TASA_RET']), self.__calc_imp_tax(
                        i['IMPORTE'], self.__place_tasa(i['TASA_RET'])
                    )
                )
            )
        else:
            return taxes

        return pyxb.BIND(*tuple(taxes))

    def __tag_impuestos(self, i):
        notaxes = True
        kwargs = {}
        if i['IMPORTE_IMPUESTO'] > 0 or i['IMPORTE_IEPS'] > 0 or i['IMPORTE_RET'] > 0:
            notaxes = False
            if self.__tag_traslados(i):
                kwargs['Traslados'] = self.__tag_traslados(i)
            if self.__tag_retenciones(i):
                kwargs['Retenciones'] = self.__tag_retenciones(i)
        return pyxb.BIND() if notaxes else pyxb.BIND(**kwargs)
