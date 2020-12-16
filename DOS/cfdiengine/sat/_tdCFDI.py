# ./sat/_tdCFDI.py
# -*- coding: utf-8 -*-
# PyXB bindings for NM:7f69522594759023a0dca99376f087fbe62b3c39
# Generated 2017-05-26 09:05:43.260296 by PyXB version 1.2.5 using Python 3.5.3.final.0
# Namespace http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI [xmlns:tdCFDI]

from __future__ import unicode_literals
import pyxb
import pyxb.binding
import pyxb.binding.saxer
import io
import pyxb.utils.utility
import pyxb.utils.domutils
import sys
import pyxb.utils.six as _six
# Unique identifier for bindings created at the same time
_GenerationUID = pyxb.utils.utility.UniqueIdentifier('urn:uuid:3bf9fb50-421c-11e7-8690-70188b13bae9')

# Version of PyXB used to generate the bindings
_PyXBVersion = '1.2.5'
# Generated bindings are not compatible across PyXB versions
if pyxb.__version__ != _PyXBVersion:
    raise pyxb.PyXBVersionError(_PyXBVersion)

# A holder for module-level binding classes so we can access them from
# inside class definitions where property names may conflict.
_module_typeBindings = pyxb.utils.utility.Object()

# Import bindings for namespaces imported into schema
import pyxb.binding.datatypes

# NOTE: All namespace declarations are reserved within the binding
Namespace = pyxb.namespace.NamespaceForURI('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI', create_if_missing=True)
Namespace.configureCategories(['typeBinding', 'elementBinding'])

def CreateFromDocument (xml_text, default_namespace=None, location_base=None):
    """Parse the given XML and use the document element to create a
    Python instance.

    @param xml_text An XML document.  This should be data (Python 2
    str or Python 3 bytes), or a text (Python 2 unicode or Python 3
    str) in the L{pyxb._InputEncoding} encoding.

    @keyword default_namespace The L{pyxb.Namespace} instance to use as the
    default namespace where there is no default namespace in scope.
    If unspecified or C{None}, the namespace of the module containing
    this function will be used.

    @keyword location_base: An object to be recorded as the base of all
    L{pyxb.utils.utility.Location} instances associated with events and
    objects handled by the parser.  You might pass the URI from which
    the document was obtained.
    """

    if pyxb.XMLStyle_saxer != pyxb._XMLStyle:
        dom = pyxb.utils.domutils.StringToDOM(xml_text)
        return CreateFromDOM(dom.documentElement, default_namespace=default_namespace)
    if default_namespace is None:
        default_namespace = Namespace.fallbackNamespace()
    saxer = pyxb.binding.saxer.make_parser(fallback_namespace=default_namespace, location_base=location_base)
    handler = saxer.getContentHandler()
    xmld = xml_text
    if isinstance(xmld, _six.text_type):
        xmld = xmld.encode(pyxb._InputEncoding)
    saxer.parse(io.BytesIO(xmld))
    instance = handler.rootObject()
    return instance

def CreateFromDOM (node, default_namespace=None):
    """Create a Python instance from the given DOM node.
    The node tag must correspond to an element declaration in this module.

    @deprecated: Forcing use of DOM interface is unnecessary; use L{CreateFromDocument}."""
    if default_namespace is None:
        default_namespace = Namespace.fallbackNamespace()
    return pyxb.binding.basis.element.AnyCreateFromDOM(node, default_namespace)


# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_CURP
class t_CURP (pyxb.binding.datatypes.string):

    """Tipo definido para expresar la Clave Única de Registro de Población (CURP)"""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_CURP')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 3, 2)
    _Documentation = 'Tipo definido para expresar la Clave Única de Registro de Población (CURP)'
t_CURP._CF_pattern = pyxb.binding.facets.CF_pattern()
t_CURP._CF_pattern.addPattern(pattern='[A-Z][AEIOUX][A-Z]{2}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[MH]([ABCMTZ]S|[BCJMOT]C|[CNPST]L|[GNQ]T|[GQS]R|C[MH]|[MY]N|[DH]G|NE|VZ|DF|SP)[BCDFGHJ-NP-TV-Z]{3}[0-9A-Z][0-9]')
t_CURP._CF_length = pyxb.binding.facets.CF_length(value=pyxb.binding.datatypes.nonNegativeInteger(18))
t_CURP._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_CURP._InitializeFacetMap(t_CURP._CF_pattern,
   t_CURP._CF_length,
   t_CURP._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_CURP', t_CURP)
_module_typeBindings.t_CURP = t_CURP

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_Importe
class t_Importe (pyxb.binding.datatypes.decimal):

    """Tipo definido para expresar importes numéricos con fracción hasta seis decimales. El valor se redondea de acuerdo con el número de decimales que soporta la moneda.  No se permiten valores negativos."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_Importe')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 13, 2)
    _Documentation = 'Tipo definido para expresar importes numéricos con fracción hasta seis decimales. El valor se redondea de acuerdo con el número de decimales que soporta la moneda.  No se permiten valores negativos.'
t_Importe._CF_pattern = pyxb.binding.facets.CF_pattern()
t_Importe._CF_pattern.addPattern(pattern='[0-9]{1,18}(.[0-9]{1,6})?')
t_Importe._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=t_Importe, value=pyxb.binding.datatypes.decimal('0.0'))
t_Importe._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
t_Importe._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_Importe._InitializeFacetMap(t_Importe._CF_pattern,
   t_Importe._CF_minInclusive,
   t_Importe._CF_fractionDigits,
   t_Importe._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_Importe', t_Importe)
_module_typeBindings.t_Importe = t_Importe

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_Fecha
class t_Fecha (pyxb.binding.datatypes.date):

    """Tipo definido para la expresión de la fecha. Se expresa en la forma AAAA-MM-DD."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_Fecha')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 24, 2)
    _Documentation = 'Tipo definido para la expresión de la fecha. Se expresa en la forma AAAA-MM-DD.'
t_Fecha._CF_pattern = pyxb.binding.facets.CF_pattern()
t_Fecha._CF_pattern.addPattern(pattern='((19|20)[0-9][0-9])-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])')
t_Fecha._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_Fecha._InitializeFacetMap(t_Fecha._CF_pattern,
   t_Fecha._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_Fecha', t_Fecha)
_module_typeBindings.t_Fecha = t_Fecha

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_ImporteMXN
class t_ImporteMXN (pyxb.binding.datatypes.decimal):

    """Tipo definido para expresar importes monetarios en moneda nacional MXN con fracción hasta dos decimales. No se permiten valores negativos."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_ImporteMXN')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 33, 2)
    _Documentation = 'Tipo definido para expresar importes monetarios en moneda nacional MXN con fracción hasta dos decimales. No se permiten valores negativos.'
t_ImporteMXN._CF_pattern = pyxb.binding.facets.CF_pattern()
t_ImporteMXN._CF_pattern.addPattern(pattern='[0-9]{1,18}(.[0-9]{1,2})?')
t_ImporteMXN._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=t_ImporteMXN, value=pyxb.binding.datatypes.decimal('0.0'))
t_ImporteMXN._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(2))
t_ImporteMXN._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_ImporteMXN._InitializeFacetMap(t_ImporteMXN._CF_pattern,
   t_ImporteMXN._CF_minInclusive,
   t_ImporteMXN._CF_fractionDigits,
   t_ImporteMXN._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_ImporteMXN', t_ImporteMXN)
_module_typeBindings.t_ImporteMXN = t_ImporteMXN

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_CuentaBancaria
class t_CuentaBancaria (pyxb.binding.datatypes.integer):

    """Tipo definido para expresar la cuenta bancarizada."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_CuentaBancaria')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 44, 2)
    _Documentation = 'Tipo definido para expresar la cuenta bancarizada.'
t_CuentaBancaria._CF_pattern = pyxb.binding.facets.CF_pattern()
t_CuentaBancaria._CF_pattern.addPattern(pattern='[0-9]{10,18}')
t_CuentaBancaria._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_CuentaBancaria._InitializeFacetMap(t_CuentaBancaria._CF_pattern,
   t_CuentaBancaria._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_CuentaBancaria', t_CuentaBancaria)
_module_typeBindings.t_CuentaBancaria = t_CuentaBancaria

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_RFC
class t_RFC (pyxb.binding.datatypes.string):

    """Tipo definido para expresar claves del Registro Federal de Contribuyentes"""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_RFC')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 53, 2)
    _Documentation = 'Tipo definido para expresar claves del Registro Federal de Contribuyentes'
t_RFC._CF_pattern = pyxb.binding.facets.CF_pattern()
t_RFC._CF_pattern.addPattern(pattern='[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]')
t_RFC._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(12))
t_RFC._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(13))
t_RFC._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_RFC._InitializeFacetMap(t_RFC._CF_pattern,
   t_RFC._CF_minLength,
   t_RFC._CF_maxLength,
   t_RFC._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_RFC', t_RFC)
_module_typeBindings.t_RFC = t_RFC

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_RFC_PM
class t_RFC_PM (pyxb.binding.datatypes.string):

    """Tipo definido para la expresión de un Registro Federal de Contribuyentes de persona moral."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_RFC_PM')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 64, 2)
    _Documentation = 'Tipo definido para la expresión de un Registro Federal de Contribuyentes de persona moral.'
t_RFC_PM._CF_pattern = pyxb.binding.facets.CF_pattern()
t_RFC_PM._CF_pattern.addPattern(pattern='[A-Z&Ñ]{3}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]')
t_RFC_PM._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(12))
t_RFC_PM._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_RFC_PM._InitializeFacetMap(t_RFC_PM._CF_pattern,
   t_RFC_PM._CF_minLength,
   t_RFC_PM._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_RFC_PM', t_RFC_PM)
_module_typeBindings.t_RFC_PM = t_RFC_PM

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_RFC_PF
class t_RFC_PF (pyxb.binding.datatypes.string):

    """Tipo definido para la expresión de un Registro Federal de Contribuyentes de persona física."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_RFC_PF')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 74, 2)
    _Documentation = 'Tipo definido para la expresión de un Registro Federal de Contribuyentes de persona física.'
t_RFC_PF._CF_pattern = pyxb.binding.facets.CF_pattern()
t_RFC_PF._CF_pattern.addPattern(pattern='[A-Z&Ñ]{4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]')
t_RFC_PF._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(13))
t_RFC_PF._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_RFC_PF._InitializeFacetMap(t_RFC_PF._CF_pattern,
   t_RFC_PF._CF_minLength,
   t_RFC_PF._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_RFC_PF', t_RFC_PF)
_module_typeBindings.t_RFC_PF = t_RFC_PF

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_FechaHora
class t_FechaHora (pyxb.binding.datatypes.dateTime):

    """Tipo definido para la expresión de la fecha y hora. Se expresa en la forma AAAA-MM-DDThh:mm:ss"""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_FechaHora')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 84, 2)
    _Documentation = 'Tipo definido para la expresión de la fecha y hora. Se expresa en la forma AAAA-MM-DDThh:mm:ss'
t_FechaHora._CF_pattern = pyxb.binding.facets.CF_pattern()
t_FechaHora._CF_pattern.addPattern(pattern='((19|20)[0-9][0-9])-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])')
t_FechaHora._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_FechaHora._InitializeFacetMap(t_FechaHora._CF_pattern,
   t_FechaHora._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_FechaHora', t_FechaHora)
_module_typeBindings.t_FechaHora = t_FechaHora

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_FechaH
class t_FechaH (pyxb.binding.datatypes.dateTime):

    """Tipo definido para la expresión de la fecha y hora. Se expresa en la forma AAAA-MM-DDThh:mm:ss"""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_FechaH')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 93, 2)
    _Documentation = 'Tipo definido para la expresión de la fecha y hora. Se expresa en la forma AAAA-MM-DDThh:mm:ss'
t_FechaH._CF_pattern = pyxb.binding.facets.CF_pattern()
t_FechaH._CF_pattern.addPattern(pattern='(20[1-9][0-9])-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9])')
t_FechaH._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_FechaH._InitializeFacetMap(t_FechaH._CF_pattern,
   t_FechaH._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_FechaH', t_FechaH)
_module_typeBindings.t_FechaH = t_FechaH

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_Descrip100
class t_Descrip100 (pyxb.binding.datatypes.string):

    """Tipo definido para expresar la calle en que está ubicado el domicilio del emisor del comprobante o del destinatario de la mercancía."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_Descrip100')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 102, 2)
    _Documentation = 'Tipo definido para expresar la calle en que está ubicado el domicilio del emisor del comprobante o del destinatario de la mercancía.'
t_Descrip100._CF_pattern = pyxb.binding.facets.CF_pattern()
t_Descrip100._CF_pattern.addPattern(pattern='([A-Z]|[a-z]|[0-9]| |Ñ|ñ|!|"|%|&|\'|´|-|:|;|>|=|<|@|_|,|\\{|\\}|`|~|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ü|Ü){1,100}')
t_Descrip100._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
t_Descrip100._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(100))
t_Descrip100._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_Descrip100._InitializeFacetMap(t_Descrip100._CF_pattern,
   t_Descrip100._CF_minLength,
   t_Descrip100._CF_maxLength,
   t_Descrip100._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_Descrip100', t_Descrip100)
_module_typeBindings.t_Descrip100 = t_Descrip100

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_NumeroDomicilio
class t_NumeroDomicilio (pyxb.binding.datatypes.string):

    """Tipo definido para expresar el número interior o el número exterior en donde se ubica el domicilio del emisor del comprobante o del destinatario de la mercancía."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_NumeroDomicilio')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 113, 2)
    _Documentation = 'Tipo definido para expresar el número interior o el número exterior en donde se ubica el domicilio del emisor del comprobante o del destinatario de la mercancía.'
t_NumeroDomicilio._CF_pattern = pyxb.binding.facets.CF_pattern()
t_NumeroDomicilio._CF_pattern.addPattern(pattern='([A-Z]|[a-z]|[0-9]| |Ñ|ñ|!|"|%|&|\'|´|-|:|;|>|=|<|@|_|,|\\{|\\}|`|~|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ü|Ü){1,55}')
t_NumeroDomicilio._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
t_NumeroDomicilio._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(55))
t_NumeroDomicilio._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_NumeroDomicilio._InitializeFacetMap(t_NumeroDomicilio._CF_pattern,
   t_NumeroDomicilio._CF_minLength,
   t_NumeroDomicilio._CF_maxLength,
   t_NumeroDomicilio._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_NumeroDomicilio', t_NumeroDomicilio)
_module_typeBindings.t_NumeroDomicilio = t_NumeroDomicilio

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_Referencia
class t_Referencia (pyxb.binding.datatypes.string):

    """Tipo definido para expresar la referencia geográfica adicional que permita una  fácil o precisa ubicación del domicilio del emisor del comprobante o del destinatario de la mercancía, por ejemplo las coordenadas GPS."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_Referencia')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 124, 2)
    _Documentation = 'Tipo definido para expresar la referencia geográfica adicional que permita una  fácil o precisa ubicación del domicilio del emisor del comprobante o del destinatario de la mercancía, por ejemplo las coordenadas GPS.'
t_Referencia._CF_pattern = pyxb.binding.facets.CF_pattern()
t_Referencia._CF_pattern.addPattern(pattern='([A-Z]|[a-z]|[0-9]| |Ñ|ñ|!|"|%|&|\'|´|-|:|;|>|=|<|@|_|,|\\{|\\}|`|~|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ü|Ü){1,250}')
t_Referencia._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
t_Referencia._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(250))
t_Referencia._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_Referencia._InitializeFacetMap(t_Referencia._CF_pattern,
   t_Referencia._CF_minLength,
   t_Referencia._CF_maxLength,
   t_Referencia._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_Referencia', t_Referencia)
_module_typeBindings.t_Referencia = t_Referencia

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_Descrip120
class t_Descrip120 (pyxb.binding.datatypes.string):

    """Tipo definido para expresar la colonia, localidad o municipio en que está ubicado el domicilio del emisor del comprobante o del destinatario de la mercancía."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_Descrip120')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 135, 2)
    _Documentation = 'Tipo definido para expresar la colonia, localidad o municipio en que está ubicado el domicilio del emisor del comprobante o del destinatario de la mercancía.'
t_Descrip120._CF_pattern = pyxb.binding.facets.CF_pattern()
t_Descrip120._CF_pattern.addPattern(pattern='([A-Z]|[a-z]|[0-9]| |Ñ|ñ|!|"|%|&|\'|´|-|:|;|>|=|<|@|_|,|\\{|\\}|`|~|á|é|í|ó|ú|Á|É|Í|Ó|Ú|ü|Ü){1,120}')
t_Descrip120._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
t_Descrip120._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(120))
t_Descrip120._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_Descrip120._InitializeFacetMap(t_Descrip120._CF_pattern,
   t_Descrip120._CF_minLength,
   t_Descrip120._CF_maxLength,
   t_Descrip120._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_Descrip120', t_Descrip120)
_module_typeBindings.t_Descrip120 = t_Descrip120

# Atomic simple type: {http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI}t_TipoCambio
class t_TipoCambio (pyxb.binding.datatypes.decimal):

    """Tipo definido para expresar el tipo de cambio. No se permiten valores negativos."""

    _ExpandedName = pyxb.namespace.ExpandedName(Namespace, 't_TipoCambio')
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/tipoDatos/tdCFDI/tdCFDI.xsd', 146, 2)
    _Documentation = 'Tipo definido para expresar el tipo de cambio. No se permiten valores negativos.'
t_TipoCambio._CF_pattern = pyxb.binding.facets.CF_pattern()
t_TipoCambio._CF_pattern.addPattern(pattern='[0-9]{1,18}(.[0-9]{1,6})?')
t_TipoCambio._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=t_TipoCambio, value=pyxb.binding.datatypes.decimal('0.0'))
t_TipoCambio._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
t_TipoCambio._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
t_TipoCambio._InitializeFacetMap(t_TipoCambio._CF_pattern,
   t_TipoCambio._CF_minInclusive,
   t_TipoCambio._CF_fractionDigits,
   t_TipoCambio._CF_whiteSpace)
Namespace.addCategoryObject('typeBinding', 't_TipoCambio', t_TipoCambio)
_module_typeBindings.t_TipoCambio = t_TipoCambio
