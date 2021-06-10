from docmaker.gen import BuilderGen
from docmaker.error import DocBuilderStepError
from misc.numspatrans import numspatrans
from sat.requirement import qrcode_cfdi

from reportlab.platypus import BaseDocTemplate, PageTemplate, Frame, Table, TableStyle, Paragraph, Spacer, Image
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import cm
from reportlab.pdfgen import canvas
from reportlab.lib.enums import TA_CENTER

import misc.helperstr as strtricks
import sat.reader as xmlreader
import os


impt_class='NcrPdf'


class NcrPdf(BuilderGen):

    __VERIFICATION_URL = 'https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx'

    __CAPTIONS = {
        'SPA': {
            'TL_DOC_LANG': 'ESPAÑOL',
            'TL_DOC_NAME': 'NOTA DE CREDITO',
            'TL_DOC_DATE': 'FECHA Y HORA',
            'TL_DOC_OBS': 'OBSERVACIONES',
            'TL_CUST_NAME': 'CLIENTE',
            'TL_CUST_ADDR': 'DIRECCIÓN',
            'TL_CUST_ZIPC': 'C.P.',
            'TL_CUST_REG': 'R.F.C',
            'TL_CUST_NUM': 'NO. DE CLIENTE',
            'TL_ORDER_NUM': 'NO. DE ORDEN',
            'TL_BILL_CURR': 'MONEDA',
            'TL_BILL_EXC_RATE': 'TIPO DE CAMBIO',
            'TL_PAY_DATE': 'FECHA DE PAGO',
            'TL_CFDI_USE': 'USO CFDI',
            'TL_PAY_COND': 'CONDICIONES DE PAGO',
            'TL_ACC_NUM': 'NO. DE CUENTA',
            'TL_PAY_MET': 'METODO DE PAGO',
            'TL_PAY_WAY': 'FORMA DE PAGO',
            'TL_ART_SKU': 'CLAVE',
            'TL_ART_DES': 'DESCRIPCIÓN',
            'TL_ART_UNIT': 'UNIDAD',
            'TL_ART_QUAN': 'CANTIDAD',
            'TL_ART_UP': 'P. UNITARIO',
            'TL_ART_AMNT': 'IMPORTE',
            'TL_ART_SUBT': 'SUB-TOTAL',
            'TL_ART_SAVE': 'DESCUENTO',
            'TL_ART_TOTAL': 'TOTAL'
        },
        'ENG': {
            'TL_DOC_LANG': 'ENGLISH',
            'TL_DOC_NAME': 'NOTA DE CREDITO',
            'TL_DOC_DATE': 'DATE',
            'TL_DOC_OBS': 'OBS',
            'TL_CUST_NAME': 'CUSTOMER',
            'TL_CUST_ADDR': 'ADDRESS SOLD TO',
            'TL_CUST_ZIPC': 'ZIP CODE',
            'TL_CUST_REG': 'TAX ID',
            'TL_CUST_NUM': 'CUSTOMER #',
            'TL_ORDER_NUM': 'ORDER #',
            'TL_BILL_CURR': 'CURRENCY',
            'TL_BILL_EXC_RATE': 'EXCHANGE RATE',
            'TL_PAY_DATE': 'PAYMENT DATE',
            'TL_CFDI_USE': 'CFDI USAGE',
            'TL_PAY_COND': 'PAYMENT TERMS',
            'TL_ACC_NUM': 'ACCOUNT #',
            'TL_PAY_MET': 'PAYMENT METHOD',
            'TL_PAY_WAY': 'TERMS',
            'TL_ART_SKU': 'SKU',
            'TL_ART_DES': 'DESCRIPTION',
            'TL_ART_UNIT': 'MEASURE',
            'TL_ART_QUAN': 'QUANTITY',
            'TL_ART_UP': 'UNIT PRICE',
            'TL_ART_AMNT': 'AMOUNT',
            'TL_ART_SUBT': 'SUBT',
            'TL_ART_SAVE': 'SAVE',
            'TL_ART_TOTAL': 'TOTAL'
        }
    }

    def __init__(self, logger):
        super().__init__(logger)

    def __cover_xml_lacks(self, conn, serie_folio, cap):
        q = """select gral_emp.telefono as tel,
            gral_emp.pagina_web as www,
            cfdi_regimenes.descripcion as regimen,
            gral_emp.calle as calle,
            gral_emp.colonia as colonia,
            gral_emp.numero_exterior as no,
            ee.titulo as estado,
            em.titulo as municipio,
            em.titulo || ', ' || ee.titulo as lugar_exp,
            cxc_clie.calle as rcalle,
            cxc_clie.numero as rno,
            cxc_clie.colonia as rcolonia,
            re.titulo as restado,
            rm.titulo as rmunicipio,
            cxc_clie.cp as rcp,
            rp.titulo as rpais
            FROM fac_nota_credito AS NC
            JOIN cxc_clie ON NC.cxc_clie_id = cxc_clie.id
            JOIN gral_emp ON gral_emp.id = cxc_clie.empresa_id
            JOIN gral_pais as rp ON rp.id = cxc_clie.pais_id
            JOIN gral_edo as re ON re.id = cxc_clie.estado_id
            JOIN gral_mun as rm ON rm.id = cxc_clie.municipio_id
            JOIN gral_edo as ee ON ee.id = gral_emp.estado_id
            JOIN gral_mun as em ON em.id = gral_emp.municipio_id
            JOIN cfdi_regimenes ON gral_emp.regimen_fiscal = cfdi_regimenes.numero_control
            WHERE NC.serie_folio = """
        for row in self.pg_query(conn, "{0}'{1}'".format(q, serie_folio)):
            # Just taking first row of query result
            return {
                'TEL': row['tel'],
                'WWW': row['www'],
                'CFDI_ORIGIN_PLACE': row['lugar_exp'],
                'INCEPTOR_REGIMEN': row['regimen'],
                'INCEPTOR_TOWN': row['colonia'],
                'INCEPTOR_SETTLEMENT': row['municipio'],
                'INCEPTOR_STATE': row['estado'],
                'INCEPTOR_STREET': row['calle'],
                'INCEPTOR_STREET_NUMBER': row['no'],
                'RECEPTOR_STREET': row['rcalle'],
                'RECEPTOR_STREET_NUMBER': row['rno'],
                'RECEPTOR_SETTLEMENT': row['rmunicipio'],
                'RECEPTOR_COUNTRY': row['rpais'],
                'RECEPTOR_STATE': row['restado'],
                'RECEPTOR_TOWN': row['rcolonia'],
                'RECEPTOR_CP': row['rcp']
            }

    def __load_extra_info(self, conn, serie_folio, cap):

        q = """SELECT upper(gral_mon.descripcion) AS currency_name,
            gral_mon.descripcion_abr AS currency_abr,
            cxc_clie.numero_control AS customer_control_id,
            NC.observaciones,
            ARRAY[NC.observaciones] as legends
            FROM fac_nota_credito AS NC
            LEFT JOIN gral_mon on gral_mon.id = NC.moneda_id
            JOIN cxc_agen ON cxc_agen.id =  NC.cxc_agen_id
            JOIN cxc_clie ON NC.cxc_clie_id = cxc_clie.id
            WHERE NC.serie_folio =  """
        for row in self.pg_query(conn, "{0}'{1}'".format(q, serie_folio)):
            # Just taking first row of query result
            return {
                'PURCHASE_NUMBER': '',
                'CUSTOMER_CONTROL_ID': row['customer_control_id'],
                'PAYMENT_CONSTRAINT': '',
                'PAYMENT_DATE': '',
                'CURRENCY_ABR': row['currency_abr'],
                'CURRENCY_NAME': row['currency_name'],
                'NO_CUENTA': '',
                'OBSERVACIONES': row['observaciones'],
                'BILL_LEGENDS': row['legends']
            }

    def data_acq(self, conn, d_rdirs, **kwargs):

        def fetch_info(f):
            parser = xmlreader.SaxReader()
            try:
                return parser(f)
            except xml.sax.SAXParseException as e:
                raise DocBuilderStepError("cfdi xml could not be parsed : {}".format(e))
            except Exception as e:
                raise DocBuilderStepError("xsl could not be applied : {}".format(e))

        def extra(serie_folio, c):
            try:
                return self.__load_extra_info(conn, serie_folio, c)
            except Exception as e:
                raise DocBuilderStepError("loading extra info fails: {}".format(e))

        rfc = kwargs.get('rfc', None)
        if rfc is None:
            raise DocBuilderStepError("rfc not found")

        xml = kwargs.get('xml', None)
        if xml is None:
            raise DocBuilderStepError("xml not found")
        f_xml = os.path.join(d_rdirs['cfdi_output'], rfc, xml)
        if not os.path.isfile(f_xml):
            raise DocBuilderStepError("cfdi xml not found")

        cap = kwargs.get('cap', 'SPA')
        if not cap in self.__CAPTIONS:
            raise DocBuilderStepError("caption {0} not found".format(cap))

        logo_filename = os.path.join(d_rdirs['images'], "{}_logo.png".format(rfc))
        if not os.path.isfile(logo_filename):
            raise DocBuilderStepError("logo image {0} not found".format(logo_filename))

        xml_parsed, original = fetch_info(f_xml)
        serie_folio = "%s%s" % (xml_parsed['CFDI_SERIE'], xml_parsed['CFDI_FOLIO'])
        lack = self.__cover_xml_lacks(conn, serie_folio, cap)
        einfo = extra(serie_folio, cap)
        f_qr = qrcode_cfdi(self.__VERIFICATION_URL, xml_parsed['UUID'],
            xml_parsed['INCEPTOR_RFC'], xml_parsed['RECEPTOR_RFC'],
            xml_parsed['CFDI_TOTAL'], xml_parsed['CFD_SEAL'][-8:]
        )

        return {
            'CAP_LOADED': self.__CAPTIONS[cap],
            'QRCODE': f_qr,
            'LOGO': logo_filename,
            'STAMP_ORIGINAL_STR': original,
            'XML_PARSED': xml_parsed,
            'XML_LACK': lack,
            'CUSTOMER_WWW': lack['WWW'],
            'CUSTOMER_PHONE': lack['TEL'],
            'FOOTER_ABOUT': "ESTE DOCUMENTO ES UNA REPRESENTACIÓN IMPRESA DE UN CFDI",
            'EXTRA_INFO': einfo
        }

    def format_wrt(self, output_file, dat):
        self.logger.debug('dumping contents of dat: {}'.format(repr(dat)))

        doc = BaseDocTemplate(output_file, pagesize=letter,
            rightMargin=30, leftMargin=30, topMargin=30, bottomMargin=18,)
        story = []
        logo = Image(dat['LOGO'])
        logo.drawHeight = 3.8*cm
        logo.drawWidth = 5.2*cm

        qrcode = Image(dat['QRCODE'])
        qrcode.drawHeight = 3.2*cm
        qrcode.drawWidth = 3.2*cm

        story.append(self.__top_table(logo, dat))
        story.append(Spacer(1, 0.4 * cm))
        story.append(self.__customer_table(dat))
        story.append(Spacer(1, 0.4 * cm))
        story.append(self.__items_section(dat))
        story.append(Spacer(1, 0.4 * cm))
        story.append(self.__amount_section(dat))
        story.append(Spacer(1, 0.45 * cm))

        ct = self.__comments_section(dat)
        if ct is not None:
            story.append(ct)
        story.append(Spacer(1, 0.6 * cm))
        story.append(self.__info_cert_section(dat))
        story.append(self.__info_stamp_section(qrcode, dat))
        story.append(self.__info_cert_extra(dat))
        story.append(Spacer(1, 0.6 * cm))

        lt = self.__legend_section(dat)
        if lt is not None:
            story.append(lt)

        def fp_foot(c, d):
            c.saveState()
            width, height = letter
            c.setFont('Helvetica', 7)
            c.drawCentredString(width / 2.0, (1.00 * cm), dat['FOOTER_ABOUT'])
            c.restoreState()

        bill_frame = Frame(
            doc.leftMargin, doc.bottomMargin, doc.width, doc.height,
            id='bill_frame'
        )

        doc.addPageTemplates(
            [
                PageTemplate(id='biil_page', frames=[bill_frame], onPage=fp_foot),
            ]
        )
        doc.build(story, canvasmaker=NumberedCanvas)
        return

    def data_rel(self, dat):
        os.remove(dat['QRCODE'])

    def __info_stamp_section(self, cedula, dat):

        def seals():
            c = []
            st = ParagraphStyle(name='seal', fontName='Helvetica', fontSize=6.5, leading=8)
            c.append(["CADENA ORIGINAL DEL TIMBRE:"])
            c.append([Paragraph(dat['STAMP_ORIGINAL_STR'], st)])

            c.append(["SELLO DIGITAL DEL EMISOR:"])
            c.append([Paragraph(dat['XML_PARSED']['CFD_SEAL'], st)])

            c.append(["SELLO DIGITAL DEL SAT:"])
            c.append([Paragraph(dat['XML_PARSED']['SAT_SEAL'], st)])

            t = Table(
                c,
                [
                    15.5 * cm
                ],
                [
                    0.45 * cm,
                    1.2 * cm,
                    0.4 * cm,
                    0.92 * cm,
                    0.4 * cm,
                    1.15 * cm
                ]
            )
            t.setStyle( TableStyle([
                ('FONT', (0, 0), (0, 0), 'Helvetica-Bold', 6.5),
                ('FONT', (0, 2), (0, 2), 'Helvetica-Bold', 6.5),
                ('FONT', (0, 4), (0, 4), 'Helvetica-Bold', 6.5),
            ]))
            return t

        cont = [[cedula, seals()]]

        table = Table(cont,
            [
                4.0 * cm,
                16.0 * cm
            ],
            [
                4.5 * cm
            ]
        )

        table.setStyle( TableStyle([
            ('BOX', (0, 0), (-1, -1), 0.25, colors.black),
            ('VALIGN', (0, 0),(-1, -1), 'MIDDLE'),
            ('ALIGN', (0, 0),(0, 0), 'CENTER'),

            ('ALIGN', (1, 0),(1, 0), 'LEFT'),
            ('BACKGROUND', (1, 0),(1, 0), colors.aliceblue),
            ('LINEBEFORE',(1,0),(1,0), 0.25, colors.black)
        ]))

        return table

    def __legend_section(self, dat):
        if len(dat['EXTRA_INFO']['BILL_LEGENDS']) == 0:
            return None

        st = ParagraphStyle(name='info', alignment=TA_CENTER, fontName='Helvetica', fontSize=7, leading = 7)

        cont = []
        for l in dat['EXTRA_INFO']['BILL_LEGENDS']:
            row = [
                Paragraph(l, st)
            ]
            cont.append(row)
        table = Table(cont,
            [
                20.0 * cm
            ]
        )
        table.setStyle( TableStyle([
            ('BOX', (0, 0), (-1, -1), 0.25, colors.black),
            ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ]))
        return table

    def __info_cert_section(self, dat):
        cont = [['INFORMACIÓN DEL TIMBRE FISCAL DIGITAL']]
        st = ParagraphStyle(name='info', fontName='Helvetica', fontSize=6.5, leading = 8)
        table = Table(cont,
            [
                20.0 * cm
            ],
            [
                0.50 * cm,
            ]
        )
        table.setStyle( TableStyle([
            ('BOX', (0, 0), (0, 0), 0.25, colors.black),
            ('VALIGN', (0, 0),(0, 0), 'MIDDLE'),
            ('ALIGN', (0, 0),(0, 0), 'LEFT'),
            ('FONT', (0, 0), (0, 0), 'Helvetica-Bold', 7),
            ('BACKGROUND', (0, 0),(0, 0), colors.black),
            ('TEXTCOLOR', (0, 0),(0, 0), colors.white)
        ]))
        return table

    def __info_cert_extra(self, dat):

        cont = []
        st = ParagraphStyle(name='info', fontName='Helvetica', fontSize=6.7, leading = 8)

        time_cert_info = {
            'label': "FECHA Y HORA DE CERTIFICACIÓN:",
            'scn': dat['XML_PARSED']['STAMP_DATE'],
        }

        no_cert_info = {
            'label': "NO. CERTIFICADO DEL SAT:",
            'scn': dat['XML_PARSED']['SAT_CERT_NUMBER'],
        }

        p_ti = '''<para align=center><b>%(label)s</b> %(scn)s</para>''' % time_cert_info
        p_no = '''<para align=center><b>%(label)s</b> %(scn)s</para>''' % no_cert_info

        cont.append([Paragraph(p_no, st), '', Paragraph(p_ti, st)])

        table = Table(cont,
            [
                8.0 * cm,
                1.0 * cm,
                9.0 * cm,
            ],
            [
                0.50*cm,
            ]
        )
        table.setStyle( TableStyle([
            ('BOX', (0, 0), (0, 0), 0.25, colors.black),

            ('VALIGN', (0, 0),(-1, -1), 'MIDDLE'),
            ('ALIGN', (0, 0),(-1, -1), 'CENTER'),

            ('BOX', (2, 0), (2, 0), 0.25, colors.black)
        ]))
        return table

    def __comments_section(self, dat):
        if not dat['EXTRA_INFO']['OBSERVACIONES']:
            return None
        st = ParagraphStyle(name='info',fontName='Helvetica', fontSize=7, leading = 8)
        cont = [[ dat['CAP_LOADED']['TL_DOC_OBS'] ]]
        cont.append([ Paragraph( dat['EXTRA_INFO']['OBSERVACIONES'], st) ])
        table = Table(cont,
            [
                20.0 * cm
            ]
        )
        table.setStyle( TableStyle([
            ('BOX', (0, 0), (-1, -1), 0.25, colors.black),
            ('VALIGN', (0, 0),(0, 0), 'MIDDLE'),
            ('ALIGN', (0, 0),(0, 0), 'LEFT'),
            ('FONT', (0, 0), (0, 0), 'Helvetica-Bold', 7),
            ('BACKGROUND', (0, 0),(0, 0), colors.black),
            ('TEXTCOLOR', (0, 0),(0, 0), colors.white),
        ]))
        return table

    def __amount_section(self, dat):

        def letra_section():
            cont = [ [''], ["IMPORTE CON LETRA"] ]
            (c,d) = dat['XML_PARSED']['CFDI_TOTAL'].split('.')
            n = numspatrans(c)
            result = "{0} {1} {2}/100 {3}".format(
                n.upper(),
                dat['EXTRA_INFO']['CURRENCY_NAME'],
                d,
                dat['EXTRA_INFO']['CURRENCY_ABR']
            )

            # substitute multiple whitespace with single whitespace
            cont.append([ ' '.join(result.split()) ] )

            table_letra = Table(cont,
                [
                    12.3 * cm  # rowWitdhs
                ],
                [0.4*cm] * len(cont) # rowHeights
            )

            table_letra.setStyle( TableStyle([
                ('VALIGN', (0,0),(-1,-1), 'MIDDLE'),
                ('ALIGN',  (0,0),(-1,-1), 'LEFT'),
                ('FONT', (0, 1), (-1, 1), 'Helvetica-Bold', 7),
                ('FONT', (0, 2), (-1, 2), 'Helvetica', 7),
            ]))
            return table_letra

        def total_section():
            cont = [
                [
                    dat['CAP_LOADED']['TL_ART_SUBT'],
                    dat['EXTRA_INFO']['CURRENCY_ABR'],
                    strtricks.HelperStr.format_currency(dat['XML_PARSED']['CFDI_SUBTOTAL'])
                ]
            ]

            if dat['XML_PARSED']['CFDI_SAVE'] is not None:
                cont.append([
                    dat['CAP_LOADED']['TL_ART_SAVE'],
                    dat['EXTRA_INFO']['CURRENCY_ABR'],
                    strtricks.HelperStr.format_currency(dat['XML_PARSED']['CFDI_SAVE'])
                ])

            TAXES = {'002':'IVA', '003':'IEPS'} # hardcode taxes as per SAT cat

            for imptras in dat['XML_PARSED']['TAXES']['TRAS']['DETAILS']:
                tasa = str(float(imptras['TASAOCUOTA']) * 100)

                row = [
                    "{0} {1}%".format(
                        'TAX' if dat['CAP_LOADED']['TL_DOC_LANG'] == 'ENGLISH' else TAXES[imptras['IMPUESTO']],
                        tasa
                    ),
                    dat['EXTRA_INFO']['CURRENCY_ABR'],
                    strtricks.HelperStr.format_currency(imptras['IMPORTE'])
                ]
                cont.append(row)

            cont.append([
                dat['CAP_LOADED']['TL_ART_TOTAL'], dat['EXTRA_INFO']['CURRENCY_ABR'],
                strtricks.HelperStr.format_currency(dat['XML_PARSED']['CFDI_TOTAL'])
            ])
            table_total = Table(cont,
                [
                    3.8 * cm,
                    1.28 * cm,
                    2.5 * cm  # rowWitdhs
                ],
                [0.4 * cm] * len(cont)  # rowHeights
            )
            table_total.setStyle(TableStyle([
                ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
                ('ALIGN', (0, 0), (-1, -1), 'RIGHT'),
                ('BOX', (0, 0), (-1, -1), 0.25, colors.black),

                ('FONT', (0, 0), (0, -1), 'Helvetica-Bold', 7),

                ('BOX', (1, 0), (2, -1), 0.25, colors.black),

                ('FONT', (1, 0), (1, -2), 'Helvetica', 7),
                ('FONT', (1, -1), (1, -1), 'Helvetica-Bold', 7),
                ('FONT', (-1, 0), (-1, -1), 'Helvetica-Bold', 7),
            ]))
            return table_total

        cont = [[letra_section(), total_section()]]
        table = Table(cont,
            [
               12.4 * cm,
               8 * cm
            ],
            [1.685 * cm] * len(cont)  # rowHeights
        )
        table.setStyle(TableStyle([
            ('ALIGN', (0, 0), (0, 0), 'LEFT'),
            ('ALIGN', (-1, -1), (-1, -1), 'RIGHT'),
        ]))
        return table

    def __items_section(self, dat):
        add_currency_simbol = lambda c: '${0:>40}'.format(c)

        st = ParagraphStyle(
            name='info',
            fontName='Helvetica',
            fontSize=7,
            leading=8
        )
        header_concepts = (
            dat['CAP_LOADED']['TL_ART_SKU'], dat['CAP_LOADED']['TL_ART_DES'],
            dat['CAP_LOADED']['TL_ART_UNIT'], dat['CAP_LOADED']['TL_ART_QUAN'],
            dat['CAP_LOADED']['TL_ART_UP'], dat['CAP_LOADED']['TL_ART_AMNT']
        )

        cont_concepts = []
        for i in dat['XML_PARSED']['ARTIFACTS']:
            row = [
                i['NOIDENTIFICACION'],
                Paragraph(i['DESCRIPCION'], st),
                i['CLAVEUNIDAD'].upper(),
                strtricks.HelperStr.format_currency(i['CANTIDAD']),
                add_currency_simbol(strtricks.HelperStr.format_currency(i['VALORUNITARIO'])),
                add_currency_simbol(strtricks.HelperStr.format_currency(i['IMPORTE']))
            ]
            cont_concepts.append(row)

        cont = [header_concepts] + cont_concepts

        table = Table(cont,
            [
                2.2 * cm,
                5.6 * cm,
                2.3 * cm,
                2.3 * cm,
                3.8 * cm,
                3.8 * cm
            ]
        )

        table.setStyle( TableStyle([
            #Body and header look and feel (common)
            ('ALIGN', (0,0),(-1,0), 'CENTER'),
            ('VALIGN', (0,0),(-1,-1), 'TOP'),
            ('BOX', (0, 0), (-1, 0), 0.25, colors.black),
            ('BACKGROUND', (0,0),(-1,0), colors.black),
            ('TEXTCOLOR', (0,0),(-1,0), colors.white),
            ('FONT', (0, 0), (-1, -1), 'Helvetica', 7),
            ('FONT', (0, 0), (-1, 0), 'Helvetica-Bold', 7),
            ('ROWBACKGROUNDS', (0, 1),(-1, -1), [colors.white, colors.aliceblue]),
            ('ALIGN', (0, 1),(1, -1), 'LEFT'),
            ('ALIGN', (2, 0),(2, -1), 'CENTER'),
            ('ALIGN', (3, 1),(-1, -1), 'RIGHT'),

            #Clave column look and feel (specific)
            ('BOX', (0, 1), (0, -1), 0.25, colors.black),

            #Description column look and feel (specific)
            ('BOX', (1, 1), (1, -1), 0.25, colors.black),

            #Unit column look and feel (specific)
            ('BOX', (2, 1), (2, -1), 0.25, colors.black),

            #Amount column look and feel (specific)
            ('BOX', (3, 1),(3, -1), 0.25, colors.black),

            #Amount column look and feel (specific)
            ('BOX', (4, 1),(4, -1), 0.25, colors.black),

            #Amount column look and feel (specific)
            ('BOX', (5, 1),(5, -1), 0.25, colors.black),

            #Amount column look and feel (specific)
            ('BOX', (6, 1),(6, -1), 0.25, colors.black),

            #Amount column look and feel (specific)
            ('BOX', (7, 1),(7, -1), 0.25, colors.black),
        ]))
        return table

    def __customer_table(self, dat):

        def customer_sec():
            st = ParagraphStyle(
                name='info',
                fontName='Helvetica',
                fontSize=7,
                leading=8
            )

            c = []
            c.append([ dat['CAP_LOADED']['TL_CUST_NAME'] ])
            c.append([ Paragraph(dat['XML_PARSED']['RECEPTOR_NAME'].upper(), st) ])
            c.append([ dat['CAP_LOADED']['TL_CUST_REG'] ] )
            c.append([ dat['XML_PARSED']['RECEPTOR_RFC'].upper() ])
            c.append([ dat['CAP_LOADED']['TL_CUST_ADDR'] ])
            c.append([ (
                "{0} {1}".format(
                    dat['XML_LACK']['RECEPTOR_STREET'],
                    dat['XML_LACK']['RECEPTOR_STREET_NUMBER']
            )).upper() ])
            c.append([ dat['XML_LACK']['RECEPTOR_SETTLEMENT'].upper() ])
            c.append([ "{0}, {1}".format(
                dat['XML_LACK']['RECEPTOR_TOWN'],
                dat['XML_LACK']['RECEPTOR_STATE']
            ).upper()])
            c.append([ dat['XML_LACK']['RECEPTOR_COUNTRY'].upper() ])
            c.append([ "%s %s" % ( dat['CAP_LOADED']['TL_CUST_ZIPC'], dat['XML_LACK']['RECEPTOR_CP']) ])
            t = Table(c,
                [
                    8.6 * cm   # rowWitdhs
                ],
                [0.35*cm] * 10 # rowHeights
            )
            t.setStyle(TableStyle([
                # Body and header look and feel (common)
                ('ROWBACKGROUNDS', (0, 0), (-1, 4), [colors.aliceblue, colors.white]),
                ('ALIGN', (0, 1), (-1, -1), 'LEFT'),
                ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
                ('BOX', (0, 0), (-1, -1), 0.25, colors.black),
                ('TEXTCOLOR', (0, 0), (-1, -1), colors.black),
                ('FONT', (0, 0), (-1, 0), 'Helvetica-Bold', 7),
                ('FONT', (0, 1), (-1, 1), 'Helvetica', 7),
                ('FONT', (0, 2), (-1, 2), 'Helvetica-Bold', 7),
                ('FONT', (0, 3), (-1, 3), 'Helvetica', 7),
                ('FONT', (0, 4), (-1, 4), 'Helvetica-Bold', 7),
                ('FONT', (0, 5), (-1, 9), 'Helvetica', 7),
            ]))
            return t

        def addons():
            c = []
            c.append([dat['CAP_LOADED']['TL_CUST_NUM'], dat['CAP_LOADED']['TL_PAY_MET']])
            c.append([dat['EXTRA_INFO']['CUSTOMER_CONTROL_ID'], dat['XML_PARSED']['METODO_PAGO']])
            c.append(['', ''])
            c.append(['', ''])
            c.append([dat['CAP_LOADED']['TL_BILL_CURR'], dat['CAP_LOADED']['TL_PAY_WAY']])
            c.append([dat['EXTRA_INFO']['CURRENCY_ABR'], dat['XML_PARSED']['FORMA_PAGO']])
            c.append([dat['CAP_LOADED']['TL_BILL_EXC_RATE'], ''])
            c.append([dat['XML_PARSED']['MONEY_EXCHANGE'], ''])
            c.append(['', dat['CAP_LOADED']['TL_CFDI_USE']])
            c.append(['', dat['XML_PARSED']['RECEPTOR_USAGE']])
            t = Table(c,
                [
                    4.0 * cm,
                    7.0 * cm  # rowWitdhs
                ],
                [0.35 * cm] * 10  # rowHeights
            )
            t.setStyle(TableStyle([
                # Body and header look and feel (common)
                ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
                ('BOX', (0, 0), (-1, -1), 0.25, colors.black),
                ('TEXTCOLOR', (0, 0), (-1, -1), colors.black),
                ('FONT', (0, 0), (-1, 0), 'Helvetica-Bold', 7),
                ('FONT', (0, 1), (-1, 1), 'Helvetica', 7),
                ('FONT', (0, 2), (-1, 2), 'Helvetica-Bold', 7),
                ('FONT', (0, 3), (-1, 3), 'Helvetica', 7),
                ('FONT', (0, 4), (-1, 4), 'Helvetica-Bold', 7),
                ('FONT', (0, 5), (-1, 5), 'Helvetica', 7),
                ('FONT', (0, 6), (-1, 6), 'Helvetica-Bold', 7),
                ('FONT', (0, 7), (-1, 7), 'Helvetica', 7),
                ('FONT', (0, 8), (-1, 8), 'Helvetica-Bold', 7),
                ('FONT', (0, 9), (-1, 9), 'Helvetica', 7),
                ('ROWBACKGROUNDS', (0, 0), (-1, -1), [colors.aliceblue, colors.white]),
                ('ALIGN', (0, 1), (-1, -1), 'LEFT'),
            ]))
            return t

        table = Table([[customer_sec(), addons()]], [
            8.4 * cm,
            12 * cm
        ])
        table.setStyle(TableStyle([
            ('ALIGN', (0, 0), (0, 0), 'LEFT'),
            ('ALIGN', (-1, -1), (-1, -1), 'RIGHT'),
        ]))
        return table

    def __top_table(self, logo, dat):

        def create_emisor_table():
            st = ParagraphStyle(
                name='info',
                fontName='Helvetica',
                fontSize=7,
                leading=9.7
            )
            context = dict(
                inceptor=dat['XML_PARSED']['INCEPTOR_NAME'], rfc=dat['XML_PARSED']['INCEPTOR_RFC'],
                phone=dat['CUSTOMER_PHONE'], www=dat['CUSTOMER_WWW'],
                street=dat['XML_LACK']['INCEPTOR_STREET'].upper(),
                number=dat['XML_LACK']['INCEPTOR_STREET_NUMBER'],
                settlement=dat['XML_LACK']['INCEPTOR_SETTLEMENT'].upper(),
                state=dat['XML_LACK']['INCEPTOR_STATE'].upper(),
                town=dat['XML_LACK']['INCEPTOR_TOWN'].upper(),
                cp=dat['XML_PARSED']['INCEPTOR_CP'].upper(),
                regimen=dat['XML_LACK']['INCEPTOR_REGIMEN'].upper(),
                op=dat['XML_LACK']['CFDI_ORIGIN_PLACE'].upper(), fontSize='7', fontName='Helvetica'
            )
            text = Paragraph(
                '''
                <para align=center spaceb=3>
                    <font name=%(fontName)s size=10 >
                        <b>%(inceptor)s</b>
                    </font>
                    <br/>
                    <font name=%(fontName)s size=%(fontSize)s >
                        <b>RFC: %(rfc)s</b>
                    </font>
                    <br/>
                    <font name=%(fontName)s size=%(fontSize)s >
                        <b>DOMICILIO FISCAL</b>
                    </font>
                    <br/>
                    %(street)s %(number)s %(settlement)s
                    <br/>
                    %(town)s, %(state)s C.P. %(cp)s
                    <br/>
                    TEL./FAX. %(phone)s
                    <br/>
                    %(www)s
                    <br/>
                    %(regimen)s
                    <br/><br/>
                    <b>LUGAR DE EXPEDICIÓN</b>
                    <br/>
                    %(op)s
                </para>
                ''' % context, st
            )
            t = Table([[text]], colWidths = [ 9.0 *cm])
            t.setStyle(TableStyle([('VALIGN',(-1,-1),(-1,-1),'TOP')]))
            return t

        def create_factura_table():
            st = ParagraphStyle(
                name='info',
                fontName='Helvetica',
                fontSize=7,
                leading=8
            )
            serie_folio = "%s%s" % (
                dat['XML_PARSED']['CFDI_SERIE'],
                dat['XML_PARSED']['CFDI_FOLIO']
            )

            cont = []
            cont.append([dat['CAP_LOADED']['TL_DOC_NAME']])
            cont.append(['No.'])
            cont.append([serie_folio])
            cont.append([dat['CAP_LOADED']['TL_DOC_DATE']])
            cont.append([dat['XML_PARSED']['CFDI_DATE']])
            cont.append(['FOLIO FISCAL'])
            cont.append([Paragraph(dat['XML_PARSED']['UUID'], st)])
            cont.append(['NO. CERTIFICADO'])
            cont.append([dat['XML_PARSED']['CFDI_CERT_NUMBER']])

            t = Table(cont,
                [
                    5 * cm,
                ],
                [
                    0.40 * cm,
                    0.37 * cm,
                    0.37 * cm,
                    0.38 * cm,
                    0.38 * cm,
                    0.38 * cm,
                    0.70 * cm,
                    0.38 * cm,
                    0.38 * cm,
                ] # rowHeights
            )
            t.setStyle(TableStyle([
                # Body and header look and feel (common)
                ('BOX', (0, 1), (-1, -1), 0.25, colors.black),
                ('FONT', (0, 0), (0, 0), 'Helvetica-Bold', 10),

                ('TEXTCOLOR', (0, 1), (-1, 1), colors.white),
                ('FONT', (0, 1), (-1, 2), 'Helvetica-Bold', 7),

                ('TEXTCOLOR', (0, 3), (-1, 3), colors.white),
                ('FONT', (0, 3), (-1, 3), 'Helvetica-Bold', 7),
                ('FONT', (0, 4), (-1, 4), 'Helvetica', 7),

                ('TEXTCOLOR', (0, 5), (-1, 5), colors.white),
                ('FONT', (0, 5), (-1, 5), 'Helvetica-Bold', 7),

                ('FONT', (0, 7), (-1, 7), 'Helvetica-Bold', 7),
                ('TEXTCOLOR', (0, 7), (-1, 7), colors.white),
                ('FONT', (0, 8), (-1, 8), 'Helvetica', 7),

                ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.black, colors.white]),
                ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
                ('VALIGN', (0, 1), (-1, -1), 'MIDDLE'),
            ]))
            return t

        et = create_emisor_table()
        ft = create_factura_table()
        cont = [[logo, et, ft]]
        table = Table(cont,
           [
               5.5 * cm,
               9.4 * cm,
               5.5 * cm
           ]
        )
        table.setStyle( TableStyle([
            ('ALIGN', (0, 0),(0, 0), 'LEFT'),
            ('ALIGN', (1, 0),(1, 0), 'CENTRE'),
            ('ALIGN', (-1, 0),(-1, 0), 'RIGHT'),
        ]))
        return table


class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        canvas.Canvas.__init__(self, *args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        """add page info to each page (page x of y)"""
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_number(num_pages)
            canvas.Canvas.showPage(self)
        canvas.Canvas.save(self)

    def draw_page_number(self, page_count):
        width, height = letter
        self.setFont("Helvetica", 7)
        self.drawCentredString(width / 2.0, 0.65*cm,
            "Pagina %d de %d" % (self._pageNumber, page_count))
