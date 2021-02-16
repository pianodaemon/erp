import xml.sax


class SaxReader(xml.sax.ContentHandler):
    """
    A very basic cfdi reader.
    """

    def __init__(self):
        self._ds = None

    def __call__(self, xml_file_path):
        try:
            self._reset()
            xml.sax.parse(xml_file_path, self)
            return self._ds, self._get_tos()
        except xml.sax.SAXParseException:
            raise

    @classmethod
    def parse_input_supplied(cls, supplier):
        ic = cls()
        ic._reset()
        try:
            xml.sax.parseString(supplier(), ic)
            return ic._ds, ic._get_tos()
        except xml.sax.SAXParseException as e:
            emsg = "the xml text supplied could not be parsed : {}"
            raise Exception(emsg.format(e))

    def _reset(self):
        self._ds = {
            'TVER': None,
            'PAC': None,
            'STAMP_DATE': None,
            'SAT_CERT_NUMBER': None,
            'UUID': None,
            'SAT_SEAL': None,
            'CFD_SEAL': None,
            'INCEPTOR_REG': None,
            'INCEPTOR_NAME': None,
            'INCEPTOR_RFC': None,
            'INCEPTOR_CP': None,
            'RECEPTOR_NAME': None,
            'RECEPTOR_RFC': None,
            'RECEPTOR_USAGE': None,
            'CFDI_CERT_NUMBER': None,
            'CFDI_DATE': None,
            'CFDI_SERIE': None,
            'CFDI_FOLIO': None,
            'MONEY_EXCHANGE': None,
            'CFDI_SUBTOTAL': None,
            'CFDI_SAVE': None,
            'CFDI_TOTAL': None,
            'FORMA_PAGO': None,
            'METODO_PAGO': None,
            'DOCTOS': [],
            'PAYMENTS': [],
            'ARTIFACTS': [],
            'TAXES': {
                'RET': {
                    'DETAILS': [],
                    'TOTAL': 0
                },
                'TRAS': {
                    'DETAILS': [],
                    'TOTAL': 0
                }
            }
        }

    def startElement(self, name, attrs):

        if name == "cfdi:Emisor":
            for (k, v) in attrs.items():
                if k == "Nombre":
                    self._ds['INCEPTOR_NAME'] = v
                if k == "Rfc":
                    self._ds['INCEPTOR_RFC'] = v
                if k == "RegimenFiscal":
                    self._ds['INCEPTOR_REG'] = v

        if name == "cfdi:Receptor":
            for (k, v) in attrs.items():
                if k == "Nombre":
                    self._ds['RECEPTOR_NAME'] = v
                if k == "Rfc":
                    self._ds['RECEPTOR_RFC'] = v
                if k == "UsoCFDI":
                    self._ds['RECEPTOR_USAGE'] = v

        if name == "cfdi:Comprobante":
            for (k, v) in attrs.items():
                if k == "Total":
                    self._ds['CFDI_TOTAL'] = v
                if k == "SubTotal":
                    self._ds['CFDI_SUBTOTAL'] = v
                if k == "Descuento":
                    self._ds['CFDI_SAVE'] = v
                if k == "TipoCambio":
                    self._ds['MONEY_EXCHANGE'] = v
                if k == "Serie":
                    self._ds['CFDI_SERIE'] = v
                if k == "Folio":
                    self._ds['CFDI_FOLIO'] = v
                if k == "Fecha":
                    self._ds['CFDI_DATE'] = v
                if k == "NoCertificado":
                    self._ds['CFDI_CERT_NUMBER'] = v
                if k == "LugarExpedicion":
                    self._ds['INCEPTOR_CP'] = v
                if k == "FormaPago":
                    self._ds['FORMA_PAGO'] = v
                if k == "MetodoPago":
                    self._ds['METODO_PAGO'] = v

        if name == "cfdi:Concepto":
            c = {}
            for (k, v) in attrs.items():
                if k == "Cantidad":
                    c[k.upper()] = v
                if k == "Descripcion":
                    c[k.upper()] = v
                if k == "Importe":
                    c[k.upper()] = v
                if k == "ClaveProdServ":
                    c[k.upper()] = v
                if k == "NoIdentificacion":
                    c[k.upper()] = v
                if k == "ClaveUnidad":
                    c[k.upper()] = v
                if k == "ValorUnitario":
                    c[k.upper()] = v
            self._ds['ARTIFACTS'].append(c)

        if name == "cfdi:Impuestos":
            for (k, v) in attrs.items():
                if k == "TotalImpuestosRetenidos":
                    self._ds['TAXES']['RET']['TOTAL'] = v
                if k == "TotalImpuestosTrasladados":
                    self._ds['TAXES']['TRAS']['TOTAL'] = v

        if name == "cfdi:Retencion":
            c = {}
            if 'Base' in attrs:
                pass
            else:
                for (k, v) in attrs.items():
                    if k == "Importe":
                        c[k.upper()] = v
                    if k == "Impuesto":
                        c[k.upper()] = v
                    if k == "TasaOCuota":
                        c[k.upper()] = v
                self._ds['TAXES']['RET']['DETAILS'].append(c)

        if name == "cfdi:Traslado":
            c = {}
            if 'Base' in attrs:
                pass
            else:
                for (k, v) in attrs.items():
                    if k == "Importe":
                        c[k.upper()] = v
                    if k == "Impuesto":
                        c[k.upper()] = v
                    if k == "TasaOCuota":
                        c[k.upper()] = v
                self._ds['TAXES']['TRAS']['DETAILS'].append(c)

        if name == "pago10:Pago":
            c = {}
            for (k, v) in attrs.items():
                if k == "NumOperacion":
                    c[k.upper()] = v
                if k == "Monto":
                    c[k.upper()] = v
                if k == "MonedaP":
                    c[k.upper()] = v
                if k == "TipoCambioP":
                    c[k.upper()] = v
                if k == "FormaDePagoP":
                    c[k.upper()] = v
                if k == "FechaPago":
                    c[k.upper()] = v
            self._ds['PAYMENTS'].append(c)

        if name == "pago10:DoctoRelacionado":
            c = {}
            for (k, v) in attrs.items():
                if k == "IdDocumento":
                    c[k.upper()] = v
                if k == "ImpPagado":
                    c[k.upper()] = v
                if k == "ImpSaldoAnt":
                    c[k.upper()] = v
                if k == "ImpSaldoInsoluto":
                    c[k.upper()] = v
                if k == "MetodoDePagoDR":
                    c[k.upper()] = v
                if k == "MonedaDR":
                    c[k.upper()] = v
                if k == "NumParcialidad":
                    c[k.upper()] = v
            self._ds['DOCTOS'].append(c)

        if name == "tfd:TimbreFiscalDigital":
            for (k, v) in attrs.items():
                if k == "Version":
                    self._ds['TVER'] = v
                if k == "UUID":
                    self._ds['UUID'] = v
                if k == "SelloSAT":
                    self._ds['SAT_SEAL'] = v
                if k == "SelloCFD":
                    self._ds['CFD_SEAL'] = v
                if k == "NoCertificadoSAT":
                    self._ds['SAT_CERT_NUMBER'] = v
                if k == "FechaTimbrado":
                    self._ds['STAMP_DATE'] = v
                if k =='RfcProvCertif':
                    self._ds['PAC'] = v

    def _get_tos(self):
        """creates a half bake timbre original string"""
        return '||{}|{}|{}|{}|{}|{}||'.format(
            self._ds['TVER'],
            self._ds['UUID'],
            self._ds['STAMP_DATE'],
            self._ds['PAC'],
            self._ds['CFD_SEAL'],
            self._ds['SAT_CERT_NUMBER']
        )
