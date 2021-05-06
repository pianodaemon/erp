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


impt_class='PagXml'


class PagXml(BuilderGen):

    __NDECIMALS = 2
    __MAKEUP_PROPOS = CfdiType.PAG
    __XSLT_PAG = 'cadenaoriginal_3_3.xslt'

    def __init__(self, logger):
        super().__init__(logger)

    def __narf(self, v):
        return  Decimal(truncate(float(v), self.__NDECIMALS, True))

    def __q_lugar_expedicion(self, conn, usr_id):
        """
        Consulta el lugar de expedicion en dbms
        """
        q = """select SUC.cp
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc as USR_SUC ON USR_SUC.gral_suc_id=SUC.id
            WHERE USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(q, usr_id)):
            # Just taking first row of query result
            return row['cp']

    def __q_cert_file(self, conn, usr_id):
        """
        Consulta el certificado que usa el usuario en dbms
        """
        q = """select fac_cfds_conf.archivo_certificado as cert_file
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc ON gral_usr_suc.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            WHERE gral_usr_suc.gral_usr_id = """
        for row in self.pg_query(conn, "{0}{1}".format(q, usr_id)):
            # Just taking first row of query result
            return row['cert_file']

    def __q_serie_folio(self, conn, usr_id):
        """
        Consulta la serie y folio a usar en dbms
        """
        q = """select fac_cfds_conf_folios.serie as serie,
            fac_cfds_conf_folios.folio_actual::character varying as folio
            FROM gral_suc AS SUC
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id = fac_cfds_conf.id
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            WHERE fac_cfds_conf_folios.proposito = 'PAG'
            AND USR_SUC.gral_usr_id = """
        for row in self.pg_query(conn, "{0}{1}".format(q, usr_id)):
            # Just taking first row of query result
            return { 'SERIE': row['serie'], 'FOLIO': row['folio'] }

    def __q_emisor(self, conn, usr_id):
        """
        Consulta el emisor en dbms
        """
        q = """select upper(EMP.rfc) as rfc, upper(EMP.titulo) as titulo,
            upper(REG.numero_control) as numero_control
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN gral_emp AS EMP ON EMP.id = SUC.empresa_id
            LEFT JOIN cfdi_regimenes AS REG ON REG.numero_control = EMP.regimen_fiscal
            WHERE USR_SUC.gral_usr_id = """
        for row in self.pg_query(conn, "{0}{1}".format(q, usr_id)):
            # Just taking first row of query result
            return {
                'RFC': row['rfc'],
                'RAZON_SOCIAL': unidecode.unidecode(row['titulo']),
                'REGIMEN_FISCAL': row['numero_control']
            }

    def __q_receptor(self, conn, pag_id):
        """
        Consulta el cliente de el pago en dbms
        """
        SQL = """SELECT
            upper(cxc_clie.razon_social) as razon_social,
            upper(cxc_clie.rfc) as rfc
            FROM erp_pagos
            LEFT JOIN cxc_clie ON cxc_clie.id = erp_pagos.cliente_id
            WHERE erp_pagos.numero_transaccion = """
        for row in self.pg_query(conn, "{0}{1}".format(SQL, pag_id)):
            # Just taking first row of query result
            return {
                'RFC': row['rfc'],
                'RAZON_SOCIAL': unidecode.unidecode(row['razon_social']),
                'USO_CFDI': 'P01'
            }

    def __q_no_certificado(self, conn, usr_id):
        """
        Consulta el numero de certificado en dbms
        """
        q = """select CFDI_CONF.numero_certificado
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf AS CFDI_CONF ON CFDI_CONF.gral_suc_id = SUC.id
            WHERE USR_SUC.gral_usr_id = """
        for row in self.pg_query(conn, "{0}{1}".format(q, usr_id)):
            # Just taking first row of query result
            return row['numero_certificado']

    def __q_sign_params(self, conn, usr_id):
        """
        Consulta parametros requeridos para firmado cfdi
        """
        q = """SELECT fac_cfds_conf.archivo_llave as pk
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            WHERE USR_SUC.gral_usr_id="""
        for row in self.pg_query(conn, "{0}{1}".format(q, usr_id)):
            # Just taking first row of query result
            return {
                'PKNAME': row['pk']
            }

    def __q_moneda(self, conn, pag_id):
        """
        Consulta la moneda de el pago en dbms
        """
        q = """SELECT
            'XXX'::character varying as iso_4217,
            upper(simbolo_moneda_fac) as moneda_simbolo,
            tipo_cambio_p
            FROM pagos
            WHERE numero_transaccion = """
        for row in self.pg_query(conn, "{0}{1}".format(q, pag_id)):
            # Just taking first row of query result
            return {
                'ISO_4217': row['iso_4217'],
                'SIMBOLO': row['moneda_simbolo'],
                'TIPO_DE_CAMBIO': row['tipo_cambio_p']
            }

    def __q_conceptos(self, conn):
        """
        Hack que consulta los conceptos de el pago en dbms
        """
        q = """SELECT '84111506'::character varying AS clave_prod,
            'ACT'::character varying AS clave_unidad,
            'ACT'::character varying AS unidad,
            '1'::integer AS cantidad,
            '0'::character varying AS no_identificacion,
            'Pago'::character varying AS descripcion,
            '0'::double precision as valor_unitario,
            '0'::integer as importe """
        rowset = []
        for row in self.pg_query(conn, "{0}".format(q)):
            rowset.append({
                'PRODSERV': row['clave_prod'],
                'SKU': row['no_identificacion'],
                'UNIDAD': row['clave_unidad'],
                'CANTIDAD': row['cantidad'],
                'DESCRIPCION': row['descripcion'],
                'PRECIO_UNITARIO': self.__narf(row['valor_unitario']),
                'IMPORTE': row['importe'],
            })
        return rowset

    def __q_pago(self, conn, pag_id):
        """
        Consulta la informacion de el pago
        """
        q = """ SELECT numero_transaccion::character varying AS numero_operacion,
                monto_aplicado_mn::character varying as monto, moneda_p, forma_de_pago_p,
                fecha_pago, tipo_cambio_p::character varying, serie_folio, imp_saldo_ant::character varying,
                imp_pagado::character varying, imp_saldo_insoluto::character varying,
                moneda_dr, id_documento, xxx::character varying
                FROM pagos WHERE numero_transaccion = """
        rowset = []

        saldo_cfdi_q = """
            SELECT saldo_factura::character varying
              FROM erp_h_facturas
             WHERE serie_folio = '{}'
               AND NOT cancelacion
        """

        pagosxcfdi_q = """
            SELECT pago_id, cantidad::character varying
              FROM erp_pagos_detalles
             WHERE serie_folio = '{}'
               AND NOT cancelacion
             ORDER BY pago_id DESC
        """

        for row in self.pg_query(conn, "{0}{1}".format(q, pag_id)):

            rows = self.pg_query(conn, saldo_cfdi_q.format(row['serie_folio']))
            saldo_cfdi = float(rows[0]['saldo_factura'])

            rows = self.pg_query(conn, pagosxcfdi_q.format(row['serie_folio']))
            n_pagos = len(rows)
            saldo_ant = float(rows[0]['cantidad']) + saldo_cfdi

            rowset.append({
                'NUMERO_OPERACION': row['numero_operacion'],
                'IMP_SALDO_INSOLUTO': str(saldo_cfdi),
                'IMP_SALDO_ANT': str(saldo_ant),
                'ISO_4217': row['moneda_p'],
                'MONTO': row['monto'],
                'IMP_PAGADO': row['imp_pagado'],
                'TIME_STAMP' : '{0:%Y-%m-%dT%H:%M:%S}'.format(row['fecha_pago']), #datetime.datetime.now()),  #"2017-08-22T14:37:50",
                'CLAVE': row['forma_de_pago_p'],
                'TIPO_DE_CAMBIO': row['tipo_cambio_p'],
                'MONEDA_DR': row['moneda_dr'],
                'UUID_DOC': row['id_documento'],
                'TIPO_DE_CAMBIO_DR': row['xxx'],
                'NUM_PARCIALIDAD': str(n_pagos),
            })
        return rowset

    def data_acq(self, conn, d_rdirs, **kwargs):

        usr_id = kwargs.get('usr_id', None)

        if usr_id is None:
            raise DocBuilderStepError("user id not fed")

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

        pag_id = kwargs.get('pag_id', None)

        if pag_id is None:
            raise DocBuilderStepError("pag id not fed")

        conceptos = self.__q_conceptos(conn)

        return {
            'MONEDA': self.__q_moneda(conn, pag_id),
            'TIME_STAMP': '{0:%Y-%m-%dT%H:%M:%S}'.format(datetime.datetime.now()),
            'CONTROL': self.__q_serie_folio(conn, usr_id),
            'CERT_B64': certb64,
            'KEY_PRIVATE': os.path.join(sslrfc_dir, sp['PKNAME']),
            'XSLT_SCRIPT': os.path.join(d_rdirs['cfdi_xslt'], self.__XSLT_PAG),
            'EMISOR': ed,
            'NUMERO_CERTIFICADO': self.__q_no_certificado(conn, usr_id),
            'RECEPTOR': self.__q_receptor(conn, pag_id),
            'LUGAR_EXPEDICION': self.__q_lugar_expedicion(conn, usr_id),
            'CONCEPTOS': conceptos,
            'COMPLEMENTO_PAGOS': self.__q_pago(conn, pag_id)
        }


    def format_wrt(self, output_file, dat):

        self.logger.debug('dumping contents of dat: {}'.format(repr(dat)))

        def save(xo):
            tmp_dir = tempfile.gettempdir()
            f = os.path.join(tmp_dir, HelperStr.random_str())
            writedom_cfdi(xo.toDOM(), self.__MAKEUP_PROPOS, f)
            return f

        def wa(tf):
            """
            The sundry work arounds to apply
            """
            def two_dec_attr(attr):
                HelperStr.edit_pattern(
                    '(' + attr + '=)"([0-9]*(\.[0-9]{0,1})?)"',
                    lambda x: attr + '="%.2f"' % (float(x.group(2)),), tf
                )

            HelperStr.edit_pattern('ValorUnitario="0.0"', 'ValorUnitario="0"', tf)
            HelperStr.edit_pattern('Importe="0(\.0{1})"', 'Importe="0"', tf)
            HelperStr.edit_pattern('Cantidad="1.0"', 'Cantidad="1"', tf)
            HelperStr.edit_pattern('TipoCambio="1.0"', 'TipoCambio="1"', tf)
            HelperStr.edit_pattern('Total="0.0"', 'Total="0"', tf)
            HelperStr.edit_pattern('SubTotal="0.0"', 'SubTotal="0"', tf)

            for a in ['Monto', 'ImpSaldoInsoluto', 'ImpPagado', 'ImpSaldoAnt']:
                two_dec_attr(a)

        def wrap_up(tf, of):
            with open(of, 'w', encoding="utf-8") as a:
                a.write(
                    sign_cfdi(
                        dat['KEY_PRIVATE'],
                        dat['XSLT_SCRIPT'],
                        tf
                    )
                )
            os.remove(tf)

        def paste_tag_pagos(tf, elements):

            import xml.dom.minidom

            doc = xml.dom.minidom.Document()
            base_ns = "http://www.sat.gob.mx/Pagos"
            pagos = doc.createElementNS(base_ns, 'pago10:Pagos')
            pagos.setAttribute("xmlns:pago10", base_ns)
            pagos.setAttribute("xsi:schemaLocation", "http://www.sat.gob.mx/Pagos http://www.sat.gob.mx/sitio_internet/cfd/Pagos/Pagos10.xsd")
            pagos.setAttribute("Version","1.0")

            for d in elements:
                payment = doc.createElement('pago10:Pago')
                payment.setAttribute('NumOperacion', d['NUMERO_OPERACION'])
                payment.setAttribute('Monto', d['MONTO'])
                payment.setAttribute('MonedaP', d['ISO_4217'])
               #GAS payment.setAttribute('TipoCambioP', d['TIPO_DE_CAMBIO'])

                if (d['ISO_4217']) == 'USD':
                   payment.setAttribute('TipoCambioP', d['TIPO_DE_CAMBIO'])

                payment.setAttribute('FormaDePagoP', d['CLAVE'])
                payment.setAttribute('FechaPago', d['TIME_STAMP'])

                dr = doc.createElement('pago10:DoctoRelacionado')
                dr.setAttribute('IdDocumento', d['UUID_DOC'])
                dr.setAttribute('ImpSaldoInsoluto', d['IMP_SALDO_INSOLUTO'])
                dr.setAttribute('ImpSaldoAnt', d['IMP_SALDO_ANT'])
                dr.setAttribute('ImpPagado', d['IMP_PAGADO'])
                dr.setAttribute('MonedaDR', d['MONEDA_DR'])
                dr.setAttribute('NumParcialidad', d['NUM_PARCIALIDAD'])
                dr.setAttribute('MetodoDePagoDR', 'PPD')
                if (d['MONEDA_DR']) == 'USD':
                   if (d['ISO_4217']) == 'MXN':
                      dr.setAttribute('TipoCambioDR',d[ 'TIPO_DE_CAMBIO_DR'])

                payment.appendChild(dr)

                pagos.appendChild(payment)

            doc.appendChild(pagos)
            content_xml = output = doc.toprettyxml()
            chunk = "{}\n{}\n{}\n{}".format('<cfdi:Complemento>',
                                  content_xml[22:], # omits xml declaration
                                  '</cfdi:Complemento>',
                                  '</cfdi:Comprobante>')
            HelperStr.edit_pattern('</cfdi:Comprobante>', chunk, tf)


        c = Comprobante()
        c.Version = '3.3'
        c.Fecha = dat['TIME_STAMP']
        c.Sello = '__DIGITAL_SIGN_HERE__'

        c.Receptor = pyxb.BIND()
        c.Receptor.Nombre = dat['RECEPTOR']['RAZON_SOCIAL']  # optional
        c.Receptor.Rfc = dat['RECEPTOR']['RFC']
        c.Receptor.UsoCFDI = dat['RECEPTOR']['USO_CFDI']

        c.Emisor = pyxb.BIND()
        c.Emisor.Nombre = dat['EMISOR']['RAZON_SOCIAL']  # optional
        c.Emisor.Rfc = dat['EMISOR']['RFC']
        c.Emisor.RegimenFiscal = dat['EMISOR']['REGIMEN_FISCAL']

        c.LugarExpedicion = dat['LUGAR_EXPEDICION']

        c.Serie = dat['CONTROL']['SERIE']  # optional
        c.Folio = dat['CONTROL']['FOLIO']  # optional
        c.NoCertificado = dat['NUMERO_CERTIFICADO']
        c.Certificado = dat['CERT_B64']

        c.TipoDeComprobante = 'P'
        c.Total = '0'
        c.SubTotal = '0'
        c.Moneda = dat['MONEDA']['ISO_4217']

        c.Conceptos = pyxb.BIND()
        for i in dat['CONCEPTOS']:
            c.Conceptos.append(pyxb.BIND(
                Cantidad=i['CANTIDAD'],
                ClaveUnidad=i['UNIDAD'],
                ClaveProdServ=i['PRODSERV'],
                Descripcion=i['DESCRIPCION'],
                ValorUnitario=i['PRECIO_UNITARIO'],
                Importe=i['IMPORTE']
        ))

        tmp_file = save(c)
        paste_tag_pagos(tmp_file, dat['COMPLEMENTO_PAGOS'])
        wa(tmp_file)
        wrap_up(tmp_file, output_file)


    def data_rel(self, dat):
        pass
