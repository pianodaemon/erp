from pac.adapter import Adapter


__complement = '''
    <cfdi:Complemento>

        <tfd:TimbreFiscalDigital xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sat.gob.mx/TimbreFiscalDigital http://www.sat.gob.mx/sitio_internet/cfd/TimbreFiscalDigital/TimbreFiscalDigitalv11.xsd" FechaTimbrado="2019-08-01T18:38:21" NoCertificadoSAT="00001000000413990493" RfcProvCertif="PFE140312IW8" SelloCFD="enytZjgJcp22ofUhqHV1dUQ3eJNnscpJPqt1KpbMdcpnAK4tqOK0rtADAfheRycXO7CMUCxnfTSvE++Jfuq7Yg25Oj7nc4y4tVHf4tPhMn4VBPeK55xInqLxEKrjgTiGbEoozhktw14wU4q27SE+WkQe7icyOLVHJidru++mU1bY5G5yjJOtLSdChMgiqR7R0D4LMI+B9bieljH1n8dXXRw1I7jg92xPHE2toqOwJigtlG11kwP4AZu6LfQQJY+WVc6nSJgTtIbzbNGN3q5uF/r4Uf1dv/y7Is/b9a1KFLSXVqkSOR3Et2eaPd6cJ6nyTjctFGGyyvbqs7qdrt+Cbw==" SelloSAT="ItheHhejC5UJvzeJAhcCnqDMfO9882WU4ktQLRTSqfq9IbN7+GON4aAvn7/yzeDqo+bIdpBK8RsAfJ5BXNehAanmvbaLVoUSuGqnLH8eT2VwXtIUKKMG4npErBEps97Fv9MqINIC0FsHQDIwR4G7avsvvgle90S7NHFigaX50RO/Zf5vtVtUFcHoZx3OYYsM8O3JKj52sG9TvMSPC0D18PxTryjxvTEDBRyDmjVOXUg/Kh8sXFAaRUnieYjm+CGup+kIR8vAWWFKw3PPhc9gQfoms+2Oe2GirfzTNBELmfju4DSXSv6OHXeOyEEZSb6WFPRFo0Xc53l5+UWR6X/2yg==" UUID="eae49b31-7af2-405a-9928-327d8cf3f3e2" Version="1.1" xmlns:tfd="http://www.sat.gob.mx/TimbreFiscalDigital" />

    </cfdi:Complemento>
</cfdi:Comprobante>
'''

_fakeit = lambda input_xml: input_xml.replace('</cfdi:Comprobante>', __complement)

class Fake(Adapter):
    """
    Current Fake API
    """

    __PAC_DESC = 'Immortal crab - Facturacion Electronica'


    def __init__(self, logger, **kwargs):
        super().__init__(logger, self.__PAC_DESC)

    def stamp(self, xml, xid):
        """
        Timbrado usando XML firmado por el cliente
        Args:
            xml (str): xml de cfdi firmado por cliente
            xid (str): mi identificador alternativo de cfdi
        """
        return _fakeit(xml)

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
        return _fakeit(xml)
