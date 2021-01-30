

def qrcode_cfdi(as_usr, uuid, erfc, rrfc, total, chunk):
    """
    creates qrcode as per cfdi v33 constrains
    """
    import tempfile
    import qrcode
    from misc.helperstr import HelperStr

    def incept_file(i):
        SIZE_RANDOM_STR = 8
        fname = '{}/{}.jpg'.format(
            tempfile.gettempdir(),
            HelperStr.random_str(SIZE_RANDOM_STR)
        )
        with open(fname, 'wb') as q:
            i.save(q, 'JPEG')
        return fname

    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_M,
        box_size=10,
        border=4,
    )
    qr.add_data(
        '{}?&id={}&re={}&rr={}&tt={}&fe={}'.format(
            as_usr, uuid, erfc, rrfc, total, chunk
        )
    )
    qr.make(fit=True)
    return incept_file(qr.make_image())


def writedom_cfdi(d, propos, file_out):
    """
    writes and makes up a cfdi's dom as per purpose
    """
    import sat.artifacts as sa
    import xml.etree.ElementTree as ET
    from pyxb.namespace import XMLSchema_instance as xsi
    from pyxb.namespace import XMLNamespaces as xmlns

    foundation_schema = 'http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd' 

    def makeup_fac():
        d.documentElement.setAttributeNS(
                xsi.uri(), 'xsi:schemaLocation', foundation_schema)
        d.documentElement.setAttributeNS(xmlns.uri(), 'xmlns:xsi', xsi.uri())

        return {
            'cfdi': 'http://www.sat.gob.mx/cfd/3',
            'xsi': 'http://www.w3.org/2001/XMLSchema-instance'
         }

    def makeup_pag():
         pag_schema = 'http://www.sat.gob.mx/Pagos http://www.sat.gob.mx/sitio_internet/cfd/Pagos/Pagos10.xsd'
         d.documentElement.setAttributeNS(
             xsi.uri(), 'xsi:schemaLocation', '{} {}'.format(foundation_schema, pag_schema))
         d.documentElement.setAttributeNS(xmlns.uri(), 'xmlns:xsi', xsi.uri())

         return {
            'cfdi': 'http://www.sat.gob.mx/cfd/3',
            'xsi': 'http://www.w3.org/2001/XMLSchema-instance'
        }

    try:
        namespace_set = {
            sa.CfdiType.FAC: makeup_fac,
            sa.CfdiType.NCR: makeup_fac,
            sa.CfdiType.PAG: makeup_pag,
        }[propos]()
        for prefix, uri in namespace_set.items():
            ET.register_namespace(prefix, uri)
    except KeyError:
        raise Exception("To make up purpose {} is not supported yet".format(propos))

    def indent(elem, level=0):
        """
        in-place prettyprint formatter. Adds whitespaces
        to the tree because of ElementTree is not including
        such feature yet!.
        """
        i = "\n" + level*"  "
        if len(elem):
            if not elem.text or not elem.text.strip():
                elem.text = i + "  "
            if not elem.tail or not elem.tail.strip():
                elem.tail = i
            for elem in elem:
                indent(elem, level+1)
            if not elem.tail or not elem.tail.strip():
                elem.tail = i
        else:
            if level and (not elem.tail or not elem.tail.strip()):
                elem.tail = i

    root = ET.fromstring(d.toxml("utf-8").decode())
    indent(root)
    t = ET.ElementTree(root)
    t.write(file_out, xml_declaration=True,
           encoding='utf-8', method="xml")


def sign_cfdi(file_pk, file_xslt, file_xml):
    """
    signs either cfdi xml
    """
    import crypto.signer as cs
    import misc.helperxml as hx

    # it'll extract the original string as per xslt given
    original = hx.HelperXml.run_xslt(file_xml, file_xslt)
    with open(file_xml, 'r') as f:
        try:
            xml = f.read()
            s = cs.Signer(cs.Signer.SHA256, None, file_pk)
            sign = s.sign(original)
            return xml.replace('__DIGITAL_SIGN_HERE__', sign)
        except cs.SignerError as e:
            raise Exception(e)
