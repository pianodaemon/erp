import xml.sax


class SaxReader(xml.sax.ContentHandler):
    """
    A very basic cfdi reader.
    """
    __ds = None

    def __init__(self):
        pass

    def __call__(self, xml_file_path):
        try:
            self._reset()
            xml.sax.parse(xml_file_path, self)
            return self.__ds, self.__get_tos()
        except xml.sax.SAXParseException:
            raise

    @classmethod
    def parse_input_supplied(cls, supplier):
        ic = cls()
        ic._reset()
        try:
            xml.sax.parseString(supplier(), ic)
            return self.__ds, self.__get_tos()
        except xml.sax.SAXParseException as e:
            emsg = "the xml text supplied could not be parsed : {}"
            raise Exception(emsg.format(e))

    def _reset(self):
        self.__ds = {
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
                    self.__ds['INCEPTOR_NAME'] = v
                if k == "Rfc":
                    self.__ds['INCEPTOR_RFC'] = v
                if k == "RegimenFiscal":
                    self.__ds['INCEPTOR_REG'] = v

        if name == "cfdi:Receptor":
            for (k, v) in attrs.items():
                if k == "Nombre":
                    self.__ds['RECEPTOR_NAME'] = v
                if k == "Rfc":
                    self.__ds['RECEPTOR_RFC'] = v
                if k == "UsoCFDI":
                    self.__ds['RECEPTOR_USAGE'] = v

        if name == "cfdi:Comprobante":
            for (k, v) in attrs.items():
                if k == "Total":
                    self.__ds['CFDI_TOTAL'] = v
                if k == "SubTotal":
                    self.__ds['CFDI_SUBTOTAL'] = v
                if k == "Descuento":
                    self.__ds['CFDI_SAVE'] = v
                if k == "TipoCambio":
                    self.__ds['MONEY_EXCHANGE'] = v
                if k == "Serie":
                    self.__ds['CFDI_SERIE'] = v
                if k == "Folio":
                    self.__ds['CFDI_FOLIO'] = v
                if k == "Fecha":
                    self.__ds['CFDI_DATE'] = v
                if k == "NoCertificado":
                    self.__ds['CFDI_CERT_NUMBER'] = v
                if k == "LugarExpedicion":
                    self.__ds['INCEPTOR_CP'] = v
                if k == "FormaPago":
                    self.__ds['FORMA_PAGO'] = v
                if k == "MetodoPago":
                    self.__ds['METODO_PAGO'] = v

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
            self.__ds['ARTIFACTS'].append(c)

        if name == "cfdi:Impuestos":
            for (k, v) in attrs.items():
                if k == "TotalImpuestosRetenidos":
                    self.__ds['TAXES']['RET']['TOTAL'] = v
                if k == "TotalImpuestosTrasladados":
                    self.__ds['TAXES']['TRAS']['TOTAL'] = v

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
                self.__ds['TAXES']['RET']['DETAILS'].append(c)

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
                self.__ds['TAXES']['TRAS']['DETAILS'].append(c)

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
            self.__ds['PAYMENTS'].append(c)

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
            self.__ds['DOCTOS'].append(c)

        if name == "tfd:TimbreFiscalDigital":
            for (k, v) in attrs.items():
                if k == "Version":
                    self.__ds['TVER'] = v
                if k == "UUID":
                    self.__ds['UUID'] = v
                if k == "SelloSAT":
                    self.__ds['SAT_SEAL'] = v
                if k == "SelloCFD":
                    self.__ds['CFD_SEAL'] = v
                if k == "NoCertificadoSAT":
                    self.__ds['SAT_CERT_NUMBER'] = v
                if k == "FechaTimbrado":
                    self.__ds['STAMP_DATE'] = v
                if k =='RfcProvCertif':
                    self.__ds['PAC'] = v

    def __get_tos(self):
        """creates a half bake timbre original string"""
        return '||{}|{}|{}|{}|{}|{}||'.format(
            self.__ds['TVER'],
            self.__ds['UUID'],
            self.__ds['STAMP_DATE'],
            self.__ds['PAC'],
            self.__ds['CFD_SEAL'],
            self.__ds['SAT_CERT_NUMBER']
        )
