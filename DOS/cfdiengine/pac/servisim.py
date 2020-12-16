from pac.adapter import Adapter, AdapterError
from zeep import Client


class Servisim(Adapter):
    """
    Current WS API of PAC Servisim
    """
    __PAC_DESC = 'Servisim - Facturacion Electronica'

    def __init__(self, logger, **kwargs):
        super().__init__(logger, self.__PAC_DESC)
        self.config = {
            'EP': kwargs.get('end_point', None),
            'LOGIN': kwargs.get('login', None),
            'PASSWD': kwargs.get('passwd', None),
        }

    def __setup_req(self):

        try:
            connection = Client(self.config['EP'])
            self.logger.debug(
                "{0} adapter is up and ready to kick buttocks\n{1}".format(
                    self.pac_name, connection))
            return connection
        except (Exception) as e:
            raise AdapterError('can not connect with end point {}: {}'.format(
                    self.config['EP'], e))

    def __check(self, r, usage):
        try:
            self.logger.info('Code {} received from PAC'.format(r['Codigo']))
            if r['Codigo'] != 0:
                raise AdapterError(
                    "{} experimenting problems: {} ({})".format(usage, r['Descripcion'], r['Codigo']))
            return r['Xml']
        except KeyError:
            raise AdapterError('unexpected format of PAC reply')


    def stamp(self, xml, xid):
        """
        Timbrado usando XML firmado por el cliente
        Args:
            xml (str): xml de cfdi firmado por cliente
            xid (str): mi identificador alternativo de cfdi
        """
        try:
            conn = self.__setup_req()
            return self.__check(
                conn.service.timbrarCFDI({
                    'User' : self.config['LOGIN'],
                    'Pass' : self.config['PASSWD'],
                    'TipoPeticion' : '1',  # SIGNED BY CUSTOMER
                    'IdComprobante' : xid,
                    'Xml' : xml
                }),
                'Stamp'
            )
        except AdapterError:
            raise
        except (Exception) as e:
            raise AdapterError("Stamp experimenting problems: {}".format(e))

    def fetch(self, xid):
        """
        Obtencion de cfdi previamente timbrado mediante
        identificador de cfdi
        Args:
            xid (str): mi identificador alternativo de cfdi
        """
        pass

    def cancel(self, xml, emisor):
        """
        Cancelacion de XML firmado por el cliente
        Args:
            xml (str): xml de cfdi firmado por cliente
        """
        try:
            conn = self.__setup_req()
            return self.__check(
                conn.service.cancelarCFDI({
                    'User' : self.config['LOGIN'],
                    'Pass' : self.config['PASSWD'],
                    'TipoPeticion' : '2',
                    'Emisor' : emisor,     # RFC
                    'Xml' : xml
                }), 
                'Cancel'
            )
        except AdapterError:
            raise
        except (Exception) as e:
            raise AdapterError("Cancel experimenting problems".format(e))
