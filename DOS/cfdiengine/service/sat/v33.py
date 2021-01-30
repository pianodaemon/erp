# ./sat/v33.py
# -*- coding: utf-8 -*-
# PyXB bindings for NM:b937c6bd2b8fcefaf72b0909cd05dce12a711bb8
# Generated 2017-05-26 09:05:43.264603 by PyXB version 1.2.5 using Python 3.5.3.final.0
# Namespace http://www.sat.gob.mx/cfd/3

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
import sat._catCFDI as _ImportedBinding__catCFDI
import sat._tdCFDI as _ImportedBinding__tdCFDI

# NOTE: All namespace declarations are reserved within the binding
Namespace = pyxb.namespace.NamespaceForURI('http://www.sat.gob.mx/cfd/3', create_if_missing=True)
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


# Atomic simple type: [anonymous]
class STD_ANON (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 26, 22)
    _Documentation = None
STD_ANON._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON._CF_pattern.addPattern(pattern='[a-f0-9A-F]{8}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{4}-[a-f0-9A-F]{12}')
STD_ANON._CF_length = pyxb.binding.facets.CF_length(value=pyxb.binding.datatypes.nonNegativeInteger(36))
STD_ANON._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON._InitializeFacetMap(STD_ANON._CF_pattern,
   STD_ANON._CF_length,
   STD_ANON._CF_whiteSpace)
_module_typeBindings.STD_ANON = STD_ANON

# Atomic simple type: [anonymous]
class STD_ANON_ (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 58, 16)
    _Documentation = None
STD_ANON_._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_._CF_pattern.addPattern(pattern='[^|]{1,254}')
STD_ANON_._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(254))
STD_ANON_._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_._InitializeFacetMap(STD_ANON_._CF_pattern,
   STD_ANON_._CF_minLength,
   STD_ANON_._CF_maxLength,
   STD_ANON_._CF_whiteSpace)
_module_typeBindings.STD_ANON_ = STD_ANON_

# Atomic simple type: [anonymous]
class STD_ANON_2 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 88, 16)
    _Documentation = None
STD_ANON_2._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_2._CF_pattern.addPattern(pattern='[^|]{1,254}')
STD_ANON_2._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_2._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(254))
STD_ANON_2._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_2._InitializeFacetMap(STD_ANON_2._CF_pattern,
   STD_ANON_2._CF_minLength,
   STD_ANON_2._CF_maxLength,
   STD_ANON_2._CF_whiteSpace)
_module_typeBindings.STD_ANON_2 = STD_ANON_2

# Atomic simple type: [anonymous]
class STD_ANON_3 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 106, 16)
    _Documentation = None
STD_ANON_3._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_3._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(40))
STD_ANON_3._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_3._InitializeFacetMap(STD_ANON_3._CF_minLength,
   STD_ANON_3._CF_maxLength,
   STD_ANON_3._CF_whiteSpace)
_module_typeBindings.STD_ANON_3 = STD_ANON_3

# Atomic simple type: [anonymous]
class STD_ANON_4 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 154, 40)
    _Documentation = None
STD_ANON_4._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_4, value=pyxb.binding.datatypes.decimal('0.000001'))
STD_ANON_4._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_4._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_4._InitializeFacetMap(STD_ANON_4._CF_minInclusive,
   STD_ANON_4._CF_fractionDigits,
   STD_ANON_4._CF_whiteSpace)
_module_typeBindings.STD_ANON_4 = STD_ANON_4

# Atomic simple type: [anonymous]
class STD_ANON_5 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 176, 40)
    _Documentation = None
STD_ANON_5._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_5, value=pyxb.binding.datatypes.decimal('0.0'))
STD_ANON_5._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_5._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_5._InitializeFacetMap(STD_ANON_5._CF_minInclusive,
   STD_ANON_5._CF_fractionDigits,
   STD_ANON_5._CF_whiteSpace)
_module_typeBindings.STD_ANON_5 = STD_ANON_5

# Atomic simple type: [anonymous]
class STD_ANON_6 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 209, 40)
    _Documentation = None
STD_ANON_6._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_6, value=pyxb.binding.datatypes.decimal('0.000001'))
STD_ANON_6._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_6._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_6._InitializeFacetMap(STD_ANON_6._CF_minInclusive,
   STD_ANON_6._CF_fractionDigits,
   STD_ANON_6._CF_whiteSpace)
_module_typeBindings.STD_ANON_6 = STD_ANON_6

# Atomic simple type: [anonymous]
class STD_ANON_7 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 231, 40)
    _Documentation = None
STD_ANON_7._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_7, value=pyxb.binding.datatypes.decimal('0.0'))
STD_ANON_7._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_7._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_7._InitializeFacetMap(STD_ANON_7._CF_minInclusive,
   STD_ANON_7._CF_fractionDigits,
   STD_ANON_7._CF_whiteSpace)
_module_typeBindings.STD_ANON_7 = STD_ANON_7

# Atomic simple type: [anonymous]
class STD_ANON_8 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 261, 28)
    _Documentation = None
STD_ANON_8._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_8._CF_pattern.addPattern(pattern='[0-9]{2}  [0-9]{2}  [0-9]{4}  [0-9]{7}')
STD_ANON_8._CF_length = pyxb.binding.facets.CF_length(value=pyxb.binding.datatypes.nonNegativeInteger(21))
STD_ANON_8._InitializeFacetMap(STD_ANON_8._CF_pattern,
   STD_ANON_8._CF_length)
_module_typeBindings.STD_ANON_8 = STD_ANON_8

# Atomic simple type: [anonymous]
class STD_ANON_9 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 279, 28)
    _Documentation = None
STD_ANON_9._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_9._CF_pattern.addPattern(pattern='[0-9]{1,150}')
STD_ANON_9._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_9._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(150))
STD_ANON_9._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_9._InitializeFacetMap(STD_ANON_9._CF_pattern,
   STD_ANON_9._CF_minLength,
   STD_ANON_9._CF_maxLength,
   STD_ANON_9._CF_whiteSpace)
_module_typeBindings.STD_ANON_9 = STD_ANON_9

# Atomic simple type: [anonymous]
class STD_ANON_10 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 315, 34)
    _Documentation = None
STD_ANON_10._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_10._CF_pattern.addPattern(pattern='[0-9]{2}  [0-9]{2}  [0-9]{4}  [0-9]{7}')
STD_ANON_10._CF_length = pyxb.binding.facets.CF_length(value=pyxb.binding.datatypes.nonNegativeInteger(21))
STD_ANON_10._InitializeFacetMap(STD_ANON_10._CF_pattern,
   STD_ANON_10._CF_length)
_module_typeBindings.STD_ANON_10 = STD_ANON_10

# Atomic simple type: [anonymous]
class STD_ANON_11 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 334, 28)
    _Documentation = None
STD_ANON_11._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_11._CF_pattern.addPattern(pattern='[^|]{1,100}')
STD_ANON_11._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_11._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(100))
STD_ANON_11._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_11._InitializeFacetMap(STD_ANON_11._CF_pattern,
   STD_ANON_11._CF_minLength,
   STD_ANON_11._CF_maxLength,
   STD_ANON_11._CF_whiteSpace)
_module_typeBindings.STD_ANON_11 = STD_ANON_11

# Atomic simple type: [anonymous]
class STD_ANON_12 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 347, 28)
    _Documentation = None
STD_ANON_12._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_12, value=pyxb.binding.datatypes.decimal('0.000001'))
STD_ANON_12._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_12._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_12._InitializeFacetMap(STD_ANON_12._CF_minInclusive,
   STD_ANON_12._CF_fractionDigits,
   STD_ANON_12._CF_whiteSpace)
_module_typeBindings.STD_ANON_12 = STD_ANON_12

# Atomic simple type: [anonymous]
class STD_ANON_13 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 359, 28)
    _Documentation = None
STD_ANON_13._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_13._CF_pattern.addPattern(pattern='[^|]{1,20}')
STD_ANON_13._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_13._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(20))
STD_ANON_13._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_13._InitializeFacetMap(STD_ANON_13._CF_pattern,
   STD_ANON_13._CF_minLength,
   STD_ANON_13._CF_maxLength,
   STD_ANON_13._CF_whiteSpace)
_module_typeBindings.STD_ANON_13 = STD_ANON_13

# Atomic simple type: [anonymous]
class STD_ANON_14 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 372, 28)
    _Documentation = None
STD_ANON_14._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_14._CF_pattern.addPattern(pattern='[^|]{1,1000}')
STD_ANON_14._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_14._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(1000))
STD_ANON_14._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_14._InitializeFacetMap(STD_ANON_14._CF_pattern,
   STD_ANON_14._CF_minLength,
   STD_ANON_14._CF_maxLength,
   STD_ANON_14._CF_whiteSpace)
_module_typeBindings.STD_ANON_14 = STD_ANON_14

# Atomic simple type: [anonymous]
class STD_ANON_15 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 403, 22)
    _Documentation = None
STD_ANON_15._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_15._CF_pattern.addPattern(pattern='[^|]{1,100}')
STD_ANON_15._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_15._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(100))
STD_ANON_15._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_15._InitializeFacetMap(STD_ANON_15._CF_pattern,
   STD_ANON_15._CF_minLength,
   STD_ANON_15._CF_maxLength,
   STD_ANON_15._CF_whiteSpace)
_module_typeBindings.STD_ANON_15 = STD_ANON_15

# Atomic simple type: [anonymous]
class STD_ANON_16 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 416, 22)
    _Documentation = None
STD_ANON_16._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_16, value=pyxb.binding.datatypes.decimal('0.000001'))
STD_ANON_16._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_16._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_16._InitializeFacetMap(STD_ANON_16._CF_minInclusive,
   STD_ANON_16._CF_fractionDigits,
   STD_ANON_16._CF_whiteSpace)
_module_typeBindings.STD_ANON_16 = STD_ANON_16

# Atomic simple type: [anonymous]
class STD_ANON_17 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 433, 22)
    _Documentation = None
STD_ANON_17._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_17._CF_pattern.addPattern(pattern='[^|]{1,20}')
STD_ANON_17._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_17._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(20))
STD_ANON_17._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_17._InitializeFacetMap(STD_ANON_17._CF_pattern,
   STD_ANON_17._CF_minLength,
   STD_ANON_17._CF_maxLength,
   STD_ANON_17._CF_whiteSpace)
_module_typeBindings.STD_ANON_17 = STD_ANON_17

# Atomic simple type: [anonymous]
class STD_ANON_18 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 446, 22)
    _Documentation = None
STD_ANON_18._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_18._CF_pattern.addPattern(pattern='[^|]{1,1000}')
STD_ANON_18._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_18._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(1000))
STD_ANON_18._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_18._InitializeFacetMap(STD_ANON_18._CF_pattern,
   STD_ANON_18._CF_minLength,
   STD_ANON_18._CF_maxLength,
   STD_ANON_18._CF_whiteSpace)
_module_typeBindings.STD_ANON_18 = STD_ANON_18

# Atomic simple type: [anonymous]
class STD_ANON_19 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 532, 28)
    _Documentation = None
STD_ANON_19._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_19, value=pyxb.binding.datatypes.decimal('0.0'))
STD_ANON_19._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_19._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_19._InitializeFacetMap(STD_ANON_19._CF_minInclusive,
   STD_ANON_19._CF_fractionDigits,
   STD_ANON_19._CF_whiteSpace)
_module_typeBindings.STD_ANON_19 = STD_ANON_19

# Atomic simple type: [anonymous]
class STD_ANON_20 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 588, 10)
    _Documentation = None
STD_ANON_20._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_20._InitializeFacetMap(STD_ANON_20._CF_whiteSpace)
_module_typeBindings.STD_ANON_20 = STD_ANON_20

# Atomic simple type: [anonymous]
class STD_ANON_21 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 598, 10)
    _Documentation = None
STD_ANON_21._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_21._CF_pattern.addPattern(pattern='[^|]{1,25}')
STD_ANON_21._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_21._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(25))
STD_ANON_21._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_21._InitializeFacetMap(STD_ANON_21._CF_pattern,
   STD_ANON_21._CF_minLength,
   STD_ANON_21._CF_maxLength,
   STD_ANON_21._CF_whiteSpace)
_module_typeBindings.STD_ANON_21 = STD_ANON_21

# Atomic simple type: [anonymous]
class STD_ANON_22 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 611, 10)
    _Documentation = None
STD_ANON_22._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_22._CF_pattern.addPattern(pattern='[^|]{1,40}')
STD_ANON_22._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_22._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(40))
STD_ANON_22._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_22._InitializeFacetMap(STD_ANON_22._CF_pattern,
   STD_ANON_22._CF_minLength,
   STD_ANON_22._CF_maxLength,
   STD_ANON_22._CF_whiteSpace)
_module_typeBindings.STD_ANON_22 = STD_ANON_22

# Atomic simple type: [anonymous]
class STD_ANON_23 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 629, 10)
    _Documentation = None
STD_ANON_23._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_23._InitializeFacetMap(STD_ANON_23._CF_whiteSpace)
_module_typeBindings.STD_ANON_23 = STD_ANON_23

# Atomic simple type: [anonymous]
class STD_ANON_24 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 644, 10)
    _Documentation = None
STD_ANON_24._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_24._CF_pattern.addPattern(pattern='[0-9]{20}')
STD_ANON_24._CF_length = pyxb.binding.facets.CF_length(value=pyxb.binding.datatypes.nonNegativeInteger(20))
STD_ANON_24._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_24._InitializeFacetMap(STD_ANON_24._CF_pattern,
   STD_ANON_24._CF_length,
   STD_ANON_24._CF_whiteSpace)
_module_typeBindings.STD_ANON_24 = STD_ANON_24

# Atomic simple type: [anonymous]
class STD_ANON_25 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 656, 10)
    _Documentation = None
STD_ANON_25._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_25._InitializeFacetMap(STD_ANON_25._CF_whiteSpace)
_module_typeBindings.STD_ANON_25 = STD_ANON_25

# Atomic simple type: [anonymous]
class STD_ANON_26 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 666, 10)
    _Documentation = None
STD_ANON_26._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_26._CF_pattern.addPattern(pattern='[^|]{1,1000}')
STD_ANON_26._CF_minLength = pyxb.binding.facets.CF_minLength(value=pyxb.binding.datatypes.nonNegativeInteger(1))
STD_ANON_26._CF_maxLength = pyxb.binding.facets.CF_maxLength(value=pyxb.binding.datatypes.nonNegativeInteger(1000))
STD_ANON_26._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_26._InitializeFacetMap(STD_ANON_26._CF_pattern,
   STD_ANON_26._CF_minLength,
   STD_ANON_26._CF_maxLength,
   STD_ANON_26._CF_whiteSpace)
_module_typeBindings.STD_ANON_26 = STD_ANON_26

# Atomic simple type: [anonymous]
class STD_ANON_27 (pyxb.binding.datatypes.decimal):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 694, 10)
    _Documentation = None
STD_ANON_27._CF_minInclusive = pyxb.binding.facets.CF_minInclusive(value_datatype=STD_ANON_27, value=pyxb.binding.datatypes.decimal('0.000001'))
STD_ANON_27._CF_fractionDigits = pyxb.binding.facets.CF_fractionDigits(value=pyxb.binding.datatypes.nonNegativeInteger(6))
STD_ANON_27._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_27._InitializeFacetMap(STD_ANON_27._CF_minInclusive,
   STD_ANON_27._CF_fractionDigits,
   STD_ANON_27._CF_whiteSpace)
_module_typeBindings.STD_ANON_27 = STD_ANON_27

# Atomic simple type: [anonymous]
class STD_ANON_28 (pyxb.binding.datatypes.string):

    """An atomic simple type."""

    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 726, 10)
    _Documentation = None
STD_ANON_28._CF_pattern = pyxb.binding.facets.CF_pattern()
STD_ANON_28._CF_pattern.addPattern(pattern='[0-9a-zA-Z]{5}')
STD_ANON_28._CF_length = pyxb.binding.facets.CF_length(value=pyxb.binding.datatypes.nonNegativeInteger(5))
STD_ANON_28._CF_whiteSpace = pyxb.binding.facets.CF_whiteSpace(value=pyxb.binding.facets._WhiteSpace_enum.collapse)
STD_ANON_28._InitializeFacetMap(STD_ANON_28._CF_pattern,
   STD_ANON_28._CF_length,
   STD_ANON_28._CF_whiteSpace)
_module_typeBindings.STD_ANON_28 = STD_ANON_28

# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para listar los conceptos cubiertos por el comprobante."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 125, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Concepto uses Python identifier Concepto
    __Concepto = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Concepto'), 'Concepto', '__httpwww_sat_gob_mxcfd3_CTD_ANON_httpwww_sat_gob_mxcfd3Concepto', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 127, 16), )

    
    Concepto = property(__Concepto.value, __Concepto.set, None, 'Nodo requerido para registrar la información detallada de un bien o servicio amparado en el comprobante.')

    _ElementMap.update({
        __Concepto.name() : __Concepto
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON = CTD_ANON


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_ (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para capturar los impuestos aplicables al presente concepto. Cuando un concepto no registra un impuesto, implica que no es objeto del mismo."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 137, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Traslados uses Python identifier Traslados
    __Traslados = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Traslados'), 'Traslados', '__httpwww_sat_gob_mxcfd3_CTD_ANON__httpwww_sat_gob_mxcfd3Traslados', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 139, 28), )

    
    Traslados = property(__Traslados.value, __Traslados.set, None, 'Nodo opcional para asentar los impuestos trasladados aplicables al presente concepto.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Retenciones uses Python identifier Retenciones
    __Retenciones = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Retenciones'), 'Retenciones', '__httpwww_sat_gob_mxcfd3_CTD_ANON__httpwww_sat_gob_mxcfd3Retenciones', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 194, 28), )

    
    Retenciones = property(__Retenciones.value, __Retenciones.set, None, 'Nodo opcional para asentar los impuestos retenidos aplicables al presente concepto.')

    _ElementMap.update({
        __Traslados.name() : __Traslados,
        __Retenciones.name() : __Retenciones
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_ = CTD_ANON_


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_2 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para asentar los impuestos trasladados aplicables al presente concepto."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 143, 30)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Traslado uses Python identifier Traslado
    __Traslado = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Traslado'), 'Traslado', '__httpwww_sat_gob_mxcfd3_CTD_ANON_2_httpwww_sat_gob_mxcfd3Traslado', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 145, 34), )

    
    Traslado = property(__Traslado.value, __Traslado.set, None, 'Nodo requerido para asentar la información detallada de un traslado de impuestos aplicable al presente concepto.')

    _ElementMap.update({
        __Traslado.name() : __Traslado
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_2 = CTD_ANON_2


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_3 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para asentar los impuestos retenidos aplicables al presente concepto."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 198, 30)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Retencion uses Python identifier Retencion
    __Retencion = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Retencion'), 'Retencion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_3_httpwww_sat_gob_mxcfd3Retencion', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 200, 34), )

    
    Retencion = property(__Retencion.value, __Retencion.set, None, 'Nodo requerido para asentar la información detallada de una retención de impuestos aplicable al presente concepto.')

    _ElementMap.update({
        __Retencion.name() : __Retencion
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_3 = CTD_ANON_3


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_4 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional donde se incluyen los nodos complementarios de extensión al concepto definidos por el SAT, de acuerdo con las disposiciones particulares para un sector o actividad específica."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 294, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    _HasWildcardElement = True
    _ElementMap.update({
        
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_4 = CTD_ANON_4


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_5 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo condicional para capturar los impuestos retenidos aplicables. Es requerido cuando en los conceptos se registre algún impuesto retenido."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 485, 18)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Retencion uses Python identifier Retencion
    __Retencion = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Retencion'), 'Retencion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_5_httpwww_sat_gob_mxcfd3Retencion', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 487, 22), )

    
    Retencion = property(__Retencion.value, __Retencion.set, None, 'Nodo requerido para la información detallada de una retención de impuesto específico.')

    _ElementMap.update({
        __Retencion.name() : __Retencion
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_5 = CTD_ANON_5


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_6 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo condicional para capturar los impuestos trasladados aplicables. Es requerido cuando en los conceptos se registre un impuesto trasladado."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 511, 18)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Traslado uses Python identifier Traslado
    __Traslado = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Traslado'), 'Traslado', '__httpwww_sat_gob_mxcfd3_CTD_ANON_6_httpwww_sat_gob_mxcfd3Traslado', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 513, 22), )

    
    Traslado = property(__Traslado.value, __Traslado.set, None, 'Nodo requerido para la información detallada de un traslado de impuesto específico.')

    _ElementMap.update({
        __Traslado.name() : __Traslado
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_6 = CTD_ANON_6


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_7 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional donde se incluye el complemento Timbre Fiscal Digital de manera obligatoria y los nodos complementarios determinados por el SAT, de acuerdo con las disposiciones particulares para un sector o actividad específica."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 567, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    _HasWildcardElement = True
    _ElementMap.update({
        
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_7 = CTD_ANON_7


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_8 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para recibir las extensiones al presente formato que sean de utilidad al contribuyente. Para las reglas de uso del mismo, referirse al formato origen."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 577, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    _HasWildcardElement = True
    _ElementMap.update({
        
    })
    _AttributeMap.update({
        
    })
_module_typeBindings.CTD_ANON_8 = CTD_ANON_8


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_9 (pyxb.binding.basis.complexTypeDefinition):
    """Estándar de Comprobante Fiscal Digital por Internet."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 9, 6)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}CfdiRelacionados uses Python identifier CfdiRelacionados
    __CfdiRelacionados = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'CfdiRelacionados'), 'CfdiRelacionados', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3CfdiRelacionados', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 11, 10), )

    
    CfdiRelacionados = property(__CfdiRelacionados.value, __CfdiRelacionados.set, None, 'Nodo opcional para precisar la información de los comprobantes relacionados.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Emisor uses Python identifier Emisor
    __Emisor = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Emisor'), 'Emisor', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3Emisor', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 44, 10), )

    
    Emisor = property(__Emisor.value, __Emisor.set, None, 'Nodo requerido para expresar la información del contribuyente emisor del comprobante.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Receptor uses Python identifier Receptor
    __Receptor = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Receptor'), 'Receptor', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3Receptor', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 74, 10), )

    
    Receptor = property(__Receptor.value, __Receptor.set, None, 'Nodo requerido para precisar la información del contribuyente receptor del comprobante.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Conceptos uses Python identifier Conceptos
    __Conceptos = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Conceptos'), 'Conceptos', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3Conceptos', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 121, 10), )

    
    Conceptos = property(__Conceptos.value, __Conceptos.set, None, 'Nodo requerido para listar los conceptos cubiertos por el comprobante.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Impuestos uses Python identifier Impuestos
    __Impuestos = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Impuestos'), 'Impuestos', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3Impuestos', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 475, 10), )

    
    Impuestos = property(__Impuestos.value, __Impuestos.set, None, 'Nodo condicional para expresar el resumen de los impuestos aplicables.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Complemento uses Python identifier Complemento
    __Complemento = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Complemento'), 'Complemento', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3Complemento', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 563, 10), )

    
    Complemento = property(__Complemento.value, __Complemento.set, None, 'Nodo opcional donde se incluye el complemento Timbre Fiscal Digital de manera obligatoria y los nodos complementarios determinados por el SAT, de acuerdo con las disposiciones particulares para un sector o actividad específica.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Addenda uses Python identifier Addenda
    __Addenda = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Addenda'), 'Addenda', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_httpwww_sat_gob_mxcfd3Addenda', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 573, 10), )

    
    Addenda = property(__Addenda.value, __Addenda.set, None, 'Nodo opcional para recibir las extensiones al presente formato que sean de utilidad al contribuyente. Para las reglas de uso del mismo, referirse al formato origen.')

    
    # Attribute Version uses Python identifier Version
    __Version = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Version'), 'Version', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Version', _module_typeBindings.STD_ANON_20, fixed=True, unicode_default='3.3', required=True)
    __Version._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 584, 8)
    __Version._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 584, 8)
    
    Version = property(__Version.value, __Version.set, None, 'Atributo requerido con valor prefijado a 3.3 que indica la versión del estándar bajo el que se encuentra expresado el comprobante.')

    
    # Attribute Serie uses Python identifier Serie
    __Serie = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Serie'), 'Serie', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Serie', _module_typeBindings.STD_ANON_21)
    __Serie._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 594, 8)
    __Serie._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 594, 8)
    
    Serie = property(__Serie.value, __Serie.set, None, 'Atributo opcional para precisar la serie para control interno del contribuyente. Este atributo acepta una cadena de caracteres.')

    
    # Attribute Folio uses Python identifier Folio
    __Folio = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Folio'), 'Folio', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Folio', _module_typeBindings.STD_ANON_22)
    __Folio._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 607, 8)
    __Folio._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 607, 8)
    
    Folio = property(__Folio.value, __Folio.set, None, 'Atributo opcional para control interno del contribuyente que expresa el folio del comprobante, acepta una cadena de caracteres.')

    
    # Attribute Fecha uses Python identifier Fecha
    __Fecha = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Fecha'), 'Fecha', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Fecha', _ImportedBinding__tdCFDI.t_FechaH, required=True)
    __Fecha._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 620, 8)
    __Fecha._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 620, 8)
    
    Fecha = property(__Fecha.value, __Fecha.set, None, 'Atributo requerido para la expresión de la fecha y hora de expedición del Comprobante Fiscal Digital por Internet. Se expresa en la forma AAAA-MM-DDThh:mm:ss y debe corresponder con la hora local donde se expide el comprobante.')

    
    # Attribute Sello uses Python identifier Sello
    __Sello = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Sello'), 'Sello', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Sello', _module_typeBindings.STD_ANON_23, required=True)
    __Sello._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 625, 8)
    __Sello._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 625, 8)
    
    Sello = property(__Sello.value, __Sello.set, None, 'Atributo requerido para contener el sello digital del comprobante fiscal, al que hacen referencia las reglas de resolución miscelánea vigente. El sello debe ser expresado como una cadena de texto en formato Base 64.')

    
    # Attribute FormaPago uses Python identifier FormaPago
    __FormaPago = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'FormaPago'), 'FormaPago', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_FormaPago', _ImportedBinding__catCFDI.c_FormaPago)
    __FormaPago._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 635, 8)
    __FormaPago._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 635, 8)
    
    FormaPago = property(__FormaPago.value, __FormaPago.set, None, 'Atributo condicional para expresar la clave de la forma de pago de los bienes o servicios amparados por el comprobante. Si no se conoce la forma de pago este atributo se debe omitir.')

    
    # Attribute NoCertificado uses Python identifier NoCertificado
    __NoCertificado = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'NoCertificado'), 'NoCertificado', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_NoCertificado', _module_typeBindings.STD_ANON_24, required=True)
    __NoCertificado._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 640, 8)
    __NoCertificado._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 640, 8)
    
    NoCertificado = property(__NoCertificado.value, __NoCertificado.set, None, 'Atributo requerido para expresar el número de serie del certificado de sello digital que ampara al comprobante, de acuerdo con el acuse correspondiente a 20 posiciones otorgado por el sistema del SAT.')

    
    # Attribute Certificado uses Python identifier Certificado
    __Certificado = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Certificado'), 'Certificado', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Certificado', _module_typeBindings.STD_ANON_25, required=True)
    __Certificado._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 652, 8)
    __Certificado._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 652, 8)
    
    Certificado = property(__Certificado.value, __Certificado.set, None, 'Atributo requerido que sirve para incorporar el certificado de sello digital que ampara al comprobante, como texto en formato base 64.')

    
    # Attribute CondicionesDePago uses Python identifier CondicionesDePago
    __CondicionesDePago = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'CondicionesDePago'), 'CondicionesDePago', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_CondicionesDePago', _module_typeBindings.STD_ANON_26)
    __CondicionesDePago._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 662, 8)
    __CondicionesDePago._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 662, 8)
    
    CondicionesDePago = property(__CondicionesDePago.value, __CondicionesDePago.set, None, 'Atributo condicional para expresar las condiciones comerciales aplicables para el pago del comprobante fiscal digital por Internet. Este atributo puede ser condicionado mediante atributos o complementos.')

    
    # Attribute SubTotal uses Python identifier SubTotal
    __SubTotal = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'SubTotal'), 'SubTotal', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_SubTotal', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __SubTotal._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 675, 8)
    __SubTotal._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 675, 8)
    
    SubTotal = property(__SubTotal.value, __SubTotal.set, None, 'Atributo requerido para representar la suma de los importes de los conceptos antes de descuentos e impuesto. No se permiten valores negativos.')

    
    # Attribute Descuento uses Python identifier Descuento
    __Descuento = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Descuento'), 'Descuento', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Descuento', _ImportedBinding__tdCFDI.t_Importe)
    __Descuento._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 680, 8)
    __Descuento._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 680, 8)
    
    Descuento = property(__Descuento.value, __Descuento.set, None, 'Atributo condicional para representar el importe total de los descuentos aplicables antes de impuestos. No se permiten valores negativos. Se debe registrar cuando existan conceptos con descuento.')

    
    # Attribute Moneda uses Python identifier Moneda
    __Moneda = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Moneda'), 'Moneda', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Moneda', _ImportedBinding__catCFDI.c_Moneda, required=True)
    __Moneda._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 685, 8)
    __Moneda._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 685, 8)
    
    Moneda = property(__Moneda.value, __Moneda.set, None, 'Atributo requerido para identificar la clave de la moneda utilizada para expresar los montos, cuando se usa moneda nacional se registra MXN. Conforme con la especificación ISO 4217.')

    
    # Attribute TipoCambio uses Python identifier TipoCambio
    __TipoCambio = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TipoCambio'), 'TipoCambio', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_TipoCambio', _module_typeBindings.STD_ANON_27)
    __TipoCambio._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 690, 8)
    __TipoCambio._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 690, 8)
    
    TipoCambio = property(__TipoCambio.value, __TipoCambio.set, None, 'Atributo condicional para representar el tipo de cambio conforme con la moneda usada. Es requerido cuando la clave de moneda es distinta de MXN y de XXX. El valor debe reflejar el número de pesos mexicanos que equivalen a una unidad de la divisa señalada en el atributo moneda. Si el valor está fuera del porcentaje aplicable a la moneda tomado del catálogo c_Moneda, el emisor debe obtener del PAC que vaya a timbrar el CFDI, de manera no automática, una clave de confirmación para ratificar que el valor es correcto e integrar dicha clave en el atributo Confirmacion.')

    
    # Attribute Total uses Python identifier Total
    __Total = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Total'), 'Total', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Total', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __Total._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 702, 8)
    __Total._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 702, 8)
    
    Total = property(__Total.value, __Total.set, None, 'Atributo requerido para representar la suma del subtotal, menos los descuentos aplicables, más las contribuciones recibidas (impuestos trasladados - federales o locales, derechos, productos, aprovechamientos, aportaciones de seguridad social, contribuciones de mejoras) menos los impuestos retenidos. Si el valor es superior al límite que establezca el SAT en la Resolución Miscelánea Fiscal vigente, el emisor debe obtener del PAC que vaya a timbrar el CFDI, de manera no automática, una clave de confirmación para ratificar que el valor es correcto e integrar dicha clave en el atributo Confirmacion. No se permiten valores negativos.')

    
    # Attribute TipoDeComprobante uses Python identifier TipoDeComprobante
    __TipoDeComprobante = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TipoDeComprobante'), 'TipoDeComprobante', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_TipoDeComprobante', _ImportedBinding__catCFDI.c_TipoDeComprobante, required=True)
    __TipoDeComprobante._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 707, 8)
    __TipoDeComprobante._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 707, 8)
    
    TipoDeComprobante = property(__TipoDeComprobante.value, __TipoDeComprobante.set, None, 'Atributo requerido para expresar la clave del efecto del comprobante fiscal para el contribuyente emisor.')

    
    # Attribute MetodoPago uses Python identifier MetodoPago
    __MetodoPago = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'MetodoPago'), 'MetodoPago', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_MetodoPago', _ImportedBinding__catCFDI.c_MetodoPago)
    __MetodoPago._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 712, 8)
    __MetodoPago._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 712, 8)
    
    MetodoPago = property(__MetodoPago.value, __MetodoPago.set, None, 'Atributo condicional para precisar la clave del método de pago que aplica para este comprobante fiscal digital por Internet, conforme al Artículo 29-A fracción VII incisos a y b del CFF.')

    
    # Attribute LugarExpedicion uses Python identifier LugarExpedicion
    __LugarExpedicion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'LugarExpedicion'), 'LugarExpedicion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_LugarExpedicion', _ImportedBinding__catCFDI.c_CodigoPostal, required=True)
    __LugarExpedicion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 717, 8)
    __LugarExpedicion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 717, 8)
    
    LugarExpedicion = property(__LugarExpedicion.value, __LugarExpedicion.set, None, 'Atributo requerido para incorporar el código postal del lugar de expedición del comprobante (domicilio de la matriz o de la sucursal).')

    
    # Attribute Confirmacion uses Python identifier Confirmacion
    __Confirmacion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Confirmacion'), 'Confirmacion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_9_Confirmacion', _module_typeBindings.STD_ANON_28)
    __Confirmacion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 722, 8)
    __Confirmacion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 722, 8)
    
    Confirmacion = property(__Confirmacion.value, __Confirmacion.set, None, 'Atributo condicional para registrar la clave de confirmación que entregue el PAC para expedir el comprobante con importes grandes, con un tipo de cambio fuera del rango establecido o con ambos  casos. Es requerido cuando se registra un tipo de cambio o un total fuera del rango establecido.')

    _ElementMap.update({
        __CfdiRelacionados.name() : __CfdiRelacionados,
        __Emisor.name() : __Emisor,
        __Receptor.name() : __Receptor,
        __Conceptos.name() : __Conceptos,
        __Impuestos.name() : __Impuestos,
        __Complemento.name() : __Complemento,
        __Addenda.name() : __Addenda
    })
    _AttributeMap.update({
        __Version.name() : __Version,
        __Serie.name() : __Serie,
        __Folio.name() : __Folio,
        __Fecha.name() : __Fecha,
        __Sello.name() : __Sello,
        __FormaPago.name() : __FormaPago,
        __NoCertificado.name() : __NoCertificado,
        __Certificado.name() : __Certificado,
        __CondicionesDePago.name() : __CondicionesDePago,
        __SubTotal.name() : __SubTotal,
        __Descuento.name() : __Descuento,
        __Moneda.name() : __Moneda,
        __TipoCambio.name() : __TipoCambio,
        __Total.name() : __Total,
        __TipoDeComprobante.name() : __TipoDeComprobante,
        __MetodoPago.name() : __MetodoPago,
        __LugarExpedicion.name() : __LugarExpedicion,
        __Confirmacion.name() : __Confirmacion
    })
_module_typeBindings.CTD_ANON_9 = CTD_ANON_9


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_10 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para precisar la información de los comprobantes relacionados."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 15, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}CfdiRelacionado uses Python identifier CfdiRelacionado
    __CfdiRelacionado = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'CfdiRelacionado'), 'CfdiRelacionado', '__httpwww_sat_gob_mxcfd3_CTD_ANON_10_httpwww_sat_gob_mxcfd3CfdiRelacionado', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 17, 16), )

    
    CfdiRelacionado = property(__CfdiRelacionado.value, __CfdiRelacionado.set, None, 'Nodo requerido para precisar la información de los comprobantes relacionados.')

    
    # Attribute TipoRelacion uses Python identifier TipoRelacion
    __TipoRelacion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TipoRelacion'), 'TipoRelacion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_10_TipoRelacion', _ImportedBinding__catCFDI.c_TipoRelacion, required=True)
    __TipoRelacion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 37, 14)
    __TipoRelacion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 37, 14)
    
    TipoRelacion = property(__TipoRelacion.value, __TipoRelacion.set, None, 'Atributo requerido para indicar la clave de la relación que existe entre éste que se esta generando y el o los CFDI previos.')

    _ElementMap.update({
        __CfdiRelacionado.name() : __CfdiRelacionado
    })
    _AttributeMap.update({
        __TipoRelacion.name() : __TipoRelacion
    })
_module_typeBindings.CTD_ANON_10 = CTD_ANON_10


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_11 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para precisar la información de los comprobantes relacionados."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 21, 18)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute UUID uses Python identifier UUID
    __UUID = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'UUID'), 'UUID', '__httpwww_sat_gob_mxcfd3_CTD_ANON_11_UUID', _module_typeBindings.STD_ANON, required=True)
    __UUID._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 22, 20)
    __UUID._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 22, 20)
    
    UUID = property(__UUID.value, __UUID.set, None, 'Atributo requerido para registrar el folio fiscal (UUID) de un CFDI relacionado con el presente comprobante, por ejemplo: Si el CFDI relacionado es un comprobante de traslado que sirve para registrar el movimiento de la mercancía. Si este comprobante se usa como nota de crédito o nota de débito del comprobante relacionado. Si este comprobante es una devolución sobre el comprobante relacionado. Si éste sustituye a una factura cancelada.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __UUID.name() : __UUID
    })
_module_typeBindings.CTD_ANON_11 = CTD_ANON_11


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_12 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para expresar la información del contribuyente emisor del comprobante."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 48, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Rfc uses Python identifier Rfc
    __Rfc = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Rfc'), 'Rfc', '__httpwww_sat_gob_mxcfd3_CTD_ANON_12_Rfc', _ImportedBinding__tdCFDI.t_RFC, required=True)
    __Rfc._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 49, 14)
    __Rfc._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 49, 14)
    
    Rfc = property(__Rfc.value, __Rfc.set, None, 'Atributo requerido para registrar la Clave del Registro Federal de Contribuyentes correspondiente al contribuyente emisor del comprobante.')

    
    # Attribute Nombre uses Python identifier Nombre
    __Nombre = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Nombre'), 'Nombre', '__httpwww_sat_gob_mxcfd3_CTD_ANON_12_Nombre', _module_typeBindings.STD_ANON_)
    __Nombre._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 54, 14)
    __Nombre._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 54, 14)
    
    Nombre = property(__Nombre.value, __Nombre.set, None, 'Atributo opcional para registrar el nombre, denominación o razón social del contribuyente emisor del comprobante.')

    
    # Attribute RegimenFiscal uses Python identifier RegimenFiscal
    __RegimenFiscal = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'RegimenFiscal'), 'RegimenFiscal', '__httpwww_sat_gob_mxcfd3_CTD_ANON_12_RegimenFiscal', _ImportedBinding__catCFDI.c_RegimenFiscal, required=True)
    __RegimenFiscal._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 67, 14)
    __RegimenFiscal._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 67, 14)
    
    RegimenFiscal = property(__RegimenFiscal.value, __RegimenFiscal.set, None, 'Atributo requerido para incorporar la clave del régimen del contribuyente emisor al que aplicará el efecto fiscal de este comprobante.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Rfc.name() : __Rfc,
        __Nombre.name() : __Nombre,
        __RegimenFiscal.name() : __RegimenFiscal
    })
_module_typeBindings.CTD_ANON_12 = CTD_ANON_12


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_13 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para precisar la información del contribuyente receptor del comprobante."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 78, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Rfc uses Python identifier Rfc
    __Rfc = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Rfc'), 'Rfc', '__httpwww_sat_gob_mxcfd3_CTD_ANON_13_Rfc', _ImportedBinding__tdCFDI.t_RFC, required=True)
    __Rfc._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 79, 14)
    __Rfc._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 79, 14)
    
    Rfc = property(__Rfc.value, __Rfc.set, None, 'Atributo requerido para precisar la Clave del Registro Federal de Contribuyentes correspondiente al contribuyente receptor del comprobante.')

    
    # Attribute Nombre uses Python identifier Nombre
    __Nombre = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Nombre'), 'Nombre', '__httpwww_sat_gob_mxcfd3_CTD_ANON_13_Nombre', _module_typeBindings.STD_ANON_2)
    __Nombre._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 84, 14)
    __Nombre._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 84, 14)
    
    Nombre = property(__Nombre.value, __Nombre.set, None, 'Atributo opcional para precisar el nombre, denominación o razón social del contribuyente receptor del comprobante.')

    
    # Attribute ResidenciaFiscal uses Python identifier ResidenciaFiscal
    __ResidenciaFiscal = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'ResidenciaFiscal'), 'ResidenciaFiscal', '__httpwww_sat_gob_mxcfd3_CTD_ANON_13_ResidenciaFiscal', _ImportedBinding__catCFDI.c_Pais)
    __ResidenciaFiscal._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 97, 14)
    __ResidenciaFiscal._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 97, 14)
    
    ResidenciaFiscal = property(__ResidenciaFiscal.value, __ResidenciaFiscal.set, None, 'Atributo condicional para registrar la clave del país de residencia para efectos fiscales del receptor del comprobante, cuando se trate de un extranjero, y que es conforme con la especificación ISO 3166-1 alpha-3. Es requerido cuando se incluya el complemento de comercio exterior o se registre el atributo NumRegIdTrib.')

    
    # Attribute NumRegIdTrib uses Python identifier NumRegIdTrib
    __NumRegIdTrib = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'NumRegIdTrib'), 'NumRegIdTrib', '__httpwww_sat_gob_mxcfd3_CTD_ANON_13_NumRegIdTrib', _module_typeBindings.STD_ANON_3)
    __NumRegIdTrib._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 102, 14)
    __NumRegIdTrib._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 102, 14)
    
    NumRegIdTrib = property(__NumRegIdTrib.value, __NumRegIdTrib.set, None, 'Atributo condicional para expresar el número de registro de identidad fiscal del receptor cuando sea residente en el  extranjero. Es requerido cuando se incluya el complemento de comercio exterior.')

    
    # Attribute UsoCFDI uses Python identifier UsoCFDI
    __UsoCFDI = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'UsoCFDI'), 'UsoCFDI', '__httpwww_sat_gob_mxcfd3_CTD_ANON_13_UsoCFDI', _ImportedBinding__catCFDI.c_UsoCFDI, required=True)
    __UsoCFDI._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 114, 14)
    __UsoCFDI._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 114, 14)
    
    UsoCFDI = property(__UsoCFDI.value, __UsoCFDI.set, None, 'Atributo requerido para expresar la clave del uso que dará a esta factura el receptor del CFDI.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Rfc.name() : __Rfc,
        __Nombre.name() : __Nombre,
        __ResidenciaFiscal.name() : __ResidenciaFiscal,
        __NumRegIdTrib.name() : __NumRegIdTrib,
        __UsoCFDI.name() : __UsoCFDI
    })
_module_typeBindings.CTD_ANON_13 = CTD_ANON_13


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_14 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para registrar la información detallada de un bien o servicio amparado en el comprobante."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 131, 18)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Impuestos uses Python identifier Impuestos
    __Impuestos = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Impuestos'), 'Impuestos', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_httpwww_sat_gob_mxcfd3Impuestos', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 133, 22), )

    
    Impuestos = property(__Impuestos.value, __Impuestos.set, None, 'Nodo opcional para capturar los impuestos aplicables al presente concepto. Cuando un concepto no registra un impuesto, implica que no es objeto del mismo.')

    
    # Element {http://www.sat.gob.mx/cfd/3}InformacionAduanera uses Python identifier InformacionAduanera
    __InformacionAduanera = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'InformacionAduanera'), 'InformacionAduanera', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_httpwww_sat_gob_mxcfd3InformacionAduanera', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 252, 24), )

    
    InformacionAduanera = property(__InformacionAduanera.value, __InformacionAduanera.set, None, 'Nodo opcional para introducir la información aduanera aplicable cuando se trate de ventas de primera mano de mercancías importadas o se trate de operaciones de comercio exterior con bienes o servicios.')

    
    # Element {http://www.sat.gob.mx/cfd/3}CuentaPredial uses Python identifier CuentaPredial
    __CuentaPredial = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'CuentaPredial'), 'CuentaPredial', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_httpwww_sat_gob_mxcfd3CuentaPredial', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 270, 24), )

    
    CuentaPredial = property(__CuentaPredial.value, __CuentaPredial.set, None, 'Nodo opcional para asentar el número de cuenta predial con el que fue registrado el inmueble, en el sistema catastral de la entidad federativa de que trate, o bien para incorporar los datos de identificación del certificado de participación inmobiliaria no amortizable.')

    
    # Element {http://www.sat.gob.mx/cfd/3}ComplementoConcepto uses Python identifier ComplementoConcepto
    __ComplementoConcepto = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'ComplementoConcepto'), 'ComplementoConcepto', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_httpwww_sat_gob_mxcfd3ComplementoConcepto', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 290, 24), )

    
    ComplementoConcepto = property(__ComplementoConcepto.value, __ComplementoConcepto.set, None, 'Nodo opcional donde se incluyen los nodos complementarios de extensión al concepto definidos por el SAT, de acuerdo con las disposiciones particulares para un sector o actividad específica.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Parte uses Python identifier Parte
    __Parte = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Parte'), 'Parte', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_httpwww_sat_gob_mxcfd3Parte', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 300, 24), )

    
    Parte = property(__Parte.value, __Parte.set, None, 'Nodo opcional para expresar las partes o componentes que integran la totalidad del concepto expresado en el comprobante fiscal digital por Internet.')

    
    # Attribute ClaveProdServ uses Python identifier ClaveProdServ
    __ClaveProdServ = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'ClaveProdServ'), 'ClaveProdServ', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_ClaveProdServ', _ImportedBinding__catCFDI.c_ClaveProdServ, required=True)
    __ClaveProdServ._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 394, 20)
    __ClaveProdServ._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 394, 20)
    
    ClaveProdServ = property(__ClaveProdServ.value, __ClaveProdServ.set, None, 'Atributo requerido para expresar la clave del producto o del servicio amparado por el presente concepto. Es requerido y deben utilizar las claves del catálogo de productos y servicios, cuando los conceptos que registren por sus actividades correspondan con dichos conceptos.')

    
    # Attribute NoIdentificacion uses Python identifier NoIdentificacion
    __NoIdentificacion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'NoIdentificacion'), 'NoIdentificacion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_NoIdentificacion', _module_typeBindings.STD_ANON_15)
    __NoIdentificacion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 399, 20)
    __NoIdentificacion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 399, 20)
    
    NoIdentificacion = property(__NoIdentificacion.value, __NoIdentificacion.set, None, 'Atributo opcional para expresar el número de parte, identificador del producto o del servicio, la clave de producto o servicio, SKU o equivalente, propia de la operación del emisor, amparado por el presente concepto. Opcionalmente se puede utilizar claves del estándar GTIN.')

    
    # Attribute Cantidad uses Python identifier Cantidad
    __Cantidad = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Cantidad'), 'Cantidad', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_Cantidad', _module_typeBindings.STD_ANON_16, required=True)
    __Cantidad._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 412, 20)
    __Cantidad._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 412, 20)
    
    Cantidad = property(__Cantidad.value, __Cantidad.set, None, 'Atributo requerido para precisar la cantidad de bienes o servicios del tipo particular definido por el presente concepto.')

    
    # Attribute ClaveUnidad uses Python identifier ClaveUnidad
    __ClaveUnidad = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'ClaveUnidad'), 'ClaveUnidad', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_ClaveUnidad', _ImportedBinding__catCFDI.c_ClaveUnidad, required=True)
    __ClaveUnidad._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 424, 20)
    __ClaveUnidad._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 424, 20)
    
    ClaveUnidad = property(__ClaveUnidad.value, __ClaveUnidad.set, None, 'Atributo requerido para precisar la clave de unidad de medida estandarizada aplicable para la cantidad expresada en el concepto. La unidad debe corresponder con la descripción del concepto.')

    
    # Attribute Unidad uses Python identifier Unidad
    __Unidad = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Unidad'), 'Unidad', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_Unidad', _module_typeBindings.STD_ANON_17)
    __Unidad._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 429, 20)
    __Unidad._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 429, 20)
    
    Unidad = property(__Unidad.value, __Unidad.set, None, 'Atributo opcional para precisar la unidad de medida propia de la operación del emisor, aplicable para la cantidad expresada en el concepto. La unidad debe corresponder con la descripción del concepto.')

    
    # Attribute Descripcion uses Python identifier Descripcion
    __Descripcion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Descripcion'), 'Descripcion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_Descripcion', _module_typeBindings.STD_ANON_18, required=True)
    __Descripcion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 442, 20)
    __Descripcion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 442, 20)
    
    Descripcion = property(__Descripcion.value, __Descripcion.set, None, 'Atributo requerido para precisar la descripción del bien o servicio cubierto por el presente concepto.')

    
    # Attribute ValorUnitario uses Python identifier ValorUnitario
    __ValorUnitario = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'ValorUnitario'), 'ValorUnitario', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_ValorUnitario', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __ValorUnitario._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 455, 20)
    __ValorUnitario._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 455, 20)
    
    ValorUnitario = property(__ValorUnitario.value, __ValorUnitario.set, None, 'Atributo requerido para precisar el valor o precio unitario del bien o servicio cubierto por el presente concepto.')

    
    # Attribute Importe uses Python identifier Importe
    __Importe = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Importe'), 'Importe', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_Importe', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __Importe._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 460, 20)
    __Importe._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 460, 20)
    
    Importe = property(__Importe.value, __Importe.set, None, 'Atributo requerido para precisar el importe total de los bienes o servicios del presente concepto. Debe ser equivalente al resultado de multiplicar la cantidad por el valor unitario expresado en el concepto. No se permiten valores negativos. ')

    
    # Attribute Descuento uses Python identifier Descuento
    __Descuento = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Descuento'), 'Descuento', '__httpwww_sat_gob_mxcfd3_CTD_ANON_14_Descuento', _ImportedBinding__tdCFDI.t_Importe)
    __Descuento._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 465, 20)
    __Descuento._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 465, 20)
    
    Descuento = property(__Descuento.value, __Descuento.set, None, 'Atributo opcional para representar el importe de los descuentos aplicables al concepto. No se permiten valores negativos.')

    _ElementMap.update({
        __Impuestos.name() : __Impuestos,
        __InformacionAduanera.name() : __InformacionAduanera,
        __CuentaPredial.name() : __CuentaPredial,
        __ComplementoConcepto.name() : __ComplementoConcepto,
        __Parte.name() : __Parte
    })
    _AttributeMap.update({
        __ClaveProdServ.name() : __ClaveProdServ,
        __NoIdentificacion.name() : __NoIdentificacion,
        __Cantidad.name() : __Cantidad,
        __ClaveUnidad.name() : __ClaveUnidad,
        __Unidad.name() : __Unidad,
        __Descripcion.name() : __Descripcion,
        __ValorUnitario.name() : __ValorUnitario,
        __Importe.name() : __Importe,
        __Descuento.name() : __Descuento
    })
_module_typeBindings.CTD_ANON_14 = CTD_ANON_14


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_15 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para asentar la información detallada de un traslado de impuestos aplicable al presente concepto."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 149, 36)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Base uses Python identifier Base
    __Base = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Base'), 'Base', '__httpwww_sat_gob_mxcfd3_CTD_ANON_15_Base', _module_typeBindings.STD_ANON_4, required=True)
    __Base._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 150, 38)
    __Base._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 150, 38)
    
    Base = property(__Base.value, __Base.set, None, 'Atributo requerido para señalar la base para el cálculo del impuesto, la determinación de la base se realiza de acuerdo con las disposiciones fiscales vigentes. No se permiten valores negativos.')

    
    # Attribute Impuesto uses Python identifier Impuesto
    __Impuesto = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Impuesto'), 'Impuesto', '__httpwww_sat_gob_mxcfd3_CTD_ANON_15_Impuesto', _ImportedBinding__catCFDI.c_Impuesto, required=True)
    __Impuesto._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 162, 38)
    __Impuesto._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 162, 38)
    
    Impuesto = property(__Impuesto.value, __Impuesto.set, None, 'Atributo requerido para señalar la clave del tipo de impuesto trasladado aplicable al concepto.')

    
    # Attribute TipoFactor uses Python identifier TipoFactor
    __TipoFactor = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TipoFactor'), 'TipoFactor', '__httpwww_sat_gob_mxcfd3_CTD_ANON_15_TipoFactor', _ImportedBinding__catCFDI.c_TipoFactor, required=True)
    __TipoFactor._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 167, 38)
    __TipoFactor._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 167, 38)
    
    TipoFactor = property(__TipoFactor.value, __TipoFactor.set, None, 'Atributo requerido para señalar la clave del tipo de factor que se aplica a la base del impuesto.')

    
    # Attribute TasaOCuota uses Python identifier TasaOCuota
    __TasaOCuota = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TasaOCuota'), 'TasaOCuota', '__httpwww_sat_gob_mxcfd3_CTD_ANON_15_TasaOCuota', _module_typeBindings.STD_ANON_5)
    __TasaOCuota._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 172, 38)
    __TasaOCuota._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 172, 38)
    
    TasaOCuota = property(__TasaOCuota.value, __TasaOCuota.set, None, 'Atributo condicional para señalar el valor de la tasa o cuota del impuesto que se traslada para el presente concepto. Es requerido cuando el atributo TipoFactor tenga una clave que corresponda a Tasa o Cuota.')

    
    # Attribute Importe uses Python identifier Importe
    __Importe = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Importe'), 'Importe', '__httpwww_sat_gob_mxcfd3_CTD_ANON_15_Importe', _ImportedBinding__tdCFDI.t_Importe)
    __Importe._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 184, 38)
    __Importe._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 184, 38)
    
    Importe = property(__Importe.value, __Importe.set, None, 'Atributo condicional para señalar el importe del impuesto trasladado que aplica al concepto. No se permiten valores negativos. Es requerido cuando TipoFactor sea Tasa o Cuota')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Base.name() : __Base,
        __Impuesto.name() : __Impuesto,
        __TipoFactor.name() : __TipoFactor,
        __TasaOCuota.name() : __TasaOCuota,
        __Importe.name() : __Importe
    })
_module_typeBindings.CTD_ANON_15 = CTD_ANON_15


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_16 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para asentar la información detallada de una retención de impuestos aplicable al presente concepto."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 204, 36)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Base uses Python identifier Base
    __Base = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Base'), 'Base', '__httpwww_sat_gob_mxcfd3_CTD_ANON_16_Base', _module_typeBindings.STD_ANON_6, required=True)
    __Base._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 205, 38)
    __Base._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 205, 38)
    
    Base = property(__Base.value, __Base.set, None, 'Atributo requerido para señalar la base para el cálculo de la retención, la determinación de la base se realiza de acuerdo con las disposiciones fiscales vigentes. No se permiten valores negativos.')

    
    # Attribute Impuesto uses Python identifier Impuesto
    __Impuesto = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Impuesto'), 'Impuesto', '__httpwww_sat_gob_mxcfd3_CTD_ANON_16_Impuesto', _ImportedBinding__catCFDI.c_Impuesto, required=True)
    __Impuesto._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 217, 38)
    __Impuesto._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 217, 38)
    
    Impuesto = property(__Impuesto.value, __Impuesto.set, None, 'Atributo requerido para señalar la clave del tipo de impuesto retenido aplicable al concepto.')

    
    # Attribute TipoFactor uses Python identifier TipoFactor
    __TipoFactor = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TipoFactor'), 'TipoFactor', '__httpwww_sat_gob_mxcfd3_CTD_ANON_16_TipoFactor', _ImportedBinding__catCFDI.c_TipoFactor, required=True)
    __TipoFactor._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 222, 38)
    __TipoFactor._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 222, 38)
    
    TipoFactor = property(__TipoFactor.value, __TipoFactor.set, None, 'Atributo requerido para señalar la clave del tipo de factor que se aplica a la base del impuesto.')

    
    # Attribute TasaOCuota uses Python identifier TasaOCuota
    __TasaOCuota = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TasaOCuota'), 'TasaOCuota', '__httpwww_sat_gob_mxcfd3_CTD_ANON_16_TasaOCuota', _module_typeBindings.STD_ANON_7, required=True)
    __TasaOCuota._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 227, 38)
    __TasaOCuota._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 227, 38)
    
    TasaOCuota = property(__TasaOCuota.value, __TasaOCuota.set, None, 'Atributo requerido para señalar la tasa o cuota del impuesto que se retiene para el presente concepto.')

    
    # Attribute Importe uses Python identifier Importe
    __Importe = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Importe'), 'Importe', '__httpwww_sat_gob_mxcfd3_CTD_ANON_16_Importe', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __Importe._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 239, 38)
    __Importe._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 239, 38)
    
    Importe = property(__Importe.value, __Importe.set, None, 'Atributo requerido para señalar el importe del impuesto retenido que aplica al concepto. No se permiten valores negativos.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Base.name() : __Base,
        __Impuesto.name() : __Impuesto,
        __TipoFactor.name() : __TipoFactor,
        __TasaOCuota.name() : __TasaOCuota,
        __Importe.name() : __Importe
    })
_module_typeBindings.CTD_ANON_16 = CTD_ANON_16


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_17 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para introducir la información aduanera aplicable cuando se trate de ventas de primera mano de mercancías importadas o se trate de operaciones de comercio exterior con bienes o servicios."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 256, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute NumeroPedimento uses Python identifier NumeroPedimento
    __NumeroPedimento = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'NumeroPedimento'), 'NumeroPedimento', '__httpwww_sat_gob_mxcfd3_CTD_ANON_17_NumeroPedimento', _module_typeBindings.STD_ANON_8, required=True)
    __NumeroPedimento._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 257, 26)
    __NumeroPedimento._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 257, 26)
    
    NumeroPedimento = property(__NumeroPedimento.value, __NumeroPedimento.set, None, 'Atributo requerido para expresar el número del pedimento que ampara la importación del bien que se expresa en el siguiente formato: últimos 2 dígitos del año de validación seguidos por dos espacios, 2 dígitos de la aduana de despacho seguidos por dos espacios, 4 dígitos del número de la patente seguidos por dos espacios, 1 dígito que corresponde al último dígito del año en curso, salvo que se trate de un pedimento consolidado iniciado en el año inmediato anterior o del pedimento original de una rectificación, seguido de 6 dígitos de la numeración progresiva por aduana.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __NumeroPedimento.name() : __NumeroPedimento
    })
_module_typeBindings.CTD_ANON_17 = CTD_ANON_17


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_18 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para asentar el número de cuenta predial con el que fue registrado el inmueble, en el sistema catastral de la entidad federativa de que trate, o bien para incorporar los datos de identificación del certificado de participación inmobiliaria no amortizable."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 274, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Numero uses Python identifier Numero
    __Numero = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Numero'), 'Numero', '__httpwww_sat_gob_mxcfd3_CTD_ANON_18_Numero', _module_typeBindings.STD_ANON_9, required=True)
    __Numero._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 275, 26)
    __Numero._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 275, 26)
    
    Numero = property(__Numero.value, __Numero.set, None, 'Atributo requerido para precisar el número de la cuenta predial del inmueble cubierto por el presente concepto, o bien para incorporar los datos de identificación del certificado de participación inmobiliaria no amortizable, tratándose de arrendamiento.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Numero.name() : __Numero
    })
_module_typeBindings.CTD_ANON_18 = CTD_ANON_18


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_19 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para expresar las partes o componentes que integran la totalidad del concepto expresado en el comprobante fiscal digital por Internet."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 304, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}InformacionAduanera uses Python identifier InformacionAduanera
    __InformacionAduanera = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'InformacionAduanera'), 'InformacionAduanera', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_httpwww_sat_gob_mxcfd3InformacionAduanera', True, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 306, 28), )

    
    InformacionAduanera = property(__InformacionAduanera.value, __InformacionAduanera.set, None, 'Nodo opcional para introducir la información aduanera aplicable cuando se trate de ventas de primera mano de mercancías importadas o se trate de operaciones de comercio exterior con bienes o servicios.')

    
    # Attribute ClaveProdServ uses Python identifier ClaveProdServ
    __ClaveProdServ = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'ClaveProdServ'), 'ClaveProdServ', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_ClaveProdServ', _ImportedBinding__catCFDI.c_ClaveProdServ, required=True)
    __ClaveProdServ._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 325, 26)
    __ClaveProdServ._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 325, 26)
    
    ClaveProdServ = property(__ClaveProdServ.value, __ClaveProdServ.set, None, 'Atributo requerido para expresar la clave del producto o del servicio amparado por la presente parte. Es requerido y deben utilizar las claves del catálogo de productos y servicios, cuando los conceptos que registren por sus actividades correspondan con dichos conceptos.')

    
    # Attribute NoIdentificacion uses Python identifier NoIdentificacion
    __NoIdentificacion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'NoIdentificacion'), 'NoIdentificacion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_NoIdentificacion', _module_typeBindings.STD_ANON_11)
    __NoIdentificacion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 330, 26)
    __NoIdentificacion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 330, 26)
    
    NoIdentificacion = property(__NoIdentificacion.value, __NoIdentificacion.set, None, 'Atributo opcional para expresar el número de serie, número de parte del bien o identificador del producto o del servicio amparado por la presente parte. Opcionalmente se puede utilizar claves del estándar GTIN.')

    
    # Attribute Cantidad uses Python identifier Cantidad
    __Cantidad = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Cantidad'), 'Cantidad', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_Cantidad', _module_typeBindings.STD_ANON_12, required=True)
    __Cantidad._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 343, 26)
    __Cantidad._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 343, 26)
    
    Cantidad = property(__Cantidad.value, __Cantidad.set, None, 'Atributo requerido para precisar la cantidad de bienes o servicios del tipo particular definido por la presente parte.')

    
    # Attribute Unidad uses Python identifier Unidad
    __Unidad = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Unidad'), 'Unidad', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_Unidad', _module_typeBindings.STD_ANON_13)
    __Unidad._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 355, 26)
    __Unidad._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 355, 26)
    
    Unidad = property(__Unidad.value, __Unidad.set, None, 'Atributo opcional para precisar la unidad de medida propia de la operación del emisor, aplicable para la cantidad expresada en la parte. La unidad debe corresponder con la descripción de la parte. ')

    
    # Attribute Descripcion uses Python identifier Descripcion
    __Descripcion = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Descripcion'), 'Descripcion', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_Descripcion', _module_typeBindings.STD_ANON_14, required=True)
    __Descripcion._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 368, 26)
    __Descripcion._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 368, 26)
    
    Descripcion = property(__Descripcion.value, __Descripcion.set, None, 'Atributo requerido para precisar la descripción del bien o servicio cubierto por la presente parte.')

    
    # Attribute ValorUnitario uses Python identifier ValorUnitario
    __ValorUnitario = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'ValorUnitario'), 'ValorUnitario', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_ValorUnitario', _ImportedBinding__tdCFDI.t_Importe)
    __ValorUnitario._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 381, 26)
    __ValorUnitario._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 381, 26)
    
    ValorUnitario = property(__ValorUnitario.value, __ValorUnitario.set, None, 'Atributo opcional para precisar el valor o precio unitario del bien o servicio cubierto por la presente parte. No se permiten valores negativos.')

    
    # Attribute Importe uses Python identifier Importe
    __Importe = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Importe'), 'Importe', '__httpwww_sat_gob_mxcfd3_CTD_ANON_19_Importe', _ImportedBinding__tdCFDI.t_Importe)
    __Importe._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 386, 26)
    __Importe._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 386, 26)
    
    Importe = property(__Importe.value, __Importe.set, None, 'Atributo opcional para precisar el importe total de los bienes o servicios de la presente parte. Debe ser equivalente al resultado de multiplicar la cantidad por el valor unitario expresado en la parte. No se permiten valores negativos.')

    _ElementMap.update({
        __InformacionAduanera.name() : __InformacionAduanera
    })
    _AttributeMap.update({
        __ClaveProdServ.name() : __ClaveProdServ,
        __NoIdentificacion.name() : __NoIdentificacion,
        __Cantidad.name() : __Cantidad,
        __Unidad.name() : __Unidad,
        __Descripcion.name() : __Descripcion,
        __ValorUnitario.name() : __ValorUnitario,
        __Importe.name() : __Importe
    })
_module_typeBindings.CTD_ANON_19 = CTD_ANON_19


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_20 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo opcional para introducir la información aduanera aplicable cuando se trate de ventas de primera mano de mercancías importadas o se trate de operaciones de comercio exterior con bienes o servicios."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 310, 30)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute NumeroPedimento uses Python identifier NumeroPedimento
    __NumeroPedimento = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'NumeroPedimento'), 'NumeroPedimento', '__httpwww_sat_gob_mxcfd3_CTD_ANON_20_NumeroPedimento', _module_typeBindings.STD_ANON_10, required=True)
    __NumeroPedimento._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 311, 32)
    __NumeroPedimento._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 311, 32)
    
    NumeroPedimento = property(__NumeroPedimento.value, __NumeroPedimento.set, None, 'Atributo requerido para expresar el número del pedimento que ampara la importación del bien que se expresa en el siguiente formato: últimos 2 dígitos del año de validación seguidos por dos espacios, 2 dígitos de la aduana de despacho seguidos por dos espacios, 4 dígitos del número de la patente seguidos por dos espacios, 1 dígito que corresponde al último dígito del año en curso, salvo que se trate de un pedimento consolidado iniciado en el año inmediato anterior o del pedimento original de una rectificación, seguido de 6 dígitos de la numeración progresiva por aduana.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __NumeroPedimento.name() : __NumeroPedimento
    })
_module_typeBindings.CTD_ANON_20 = CTD_ANON_20


# Complex type [anonymous] with content type ELEMENT_ONLY
class CTD_ANON_21 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo condicional para expresar el resumen de los impuestos aplicables."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_ELEMENT_ONLY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 479, 12)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Element {http://www.sat.gob.mx/cfd/3}Retenciones uses Python identifier Retenciones
    __Retenciones = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Retenciones'), 'Retenciones', '__httpwww_sat_gob_mxcfd3_CTD_ANON_21_httpwww_sat_gob_mxcfd3Retenciones', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 481, 16), )

    
    Retenciones = property(__Retenciones.value, __Retenciones.set, None, 'Nodo condicional para capturar los impuestos retenidos aplicables. Es requerido cuando en los conceptos se registre algún impuesto retenido.')

    
    # Element {http://www.sat.gob.mx/cfd/3}Traslados uses Python identifier Traslados
    __Traslados = pyxb.binding.content.ElementDeclaration(pyxb.namespace.ExpandedName(Namespace, 'Traslados'), 'Traslados', '__httpwww_sat_gob_mxcfd3_CTD_ANON_21_httpwww_sat_gob_mxcfd3Traslados', False, pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 507, 16), )

    
    Traslados = property(__Traslados.value, __Traslados.set, None, 'Nodo condicional para capturar los impuestos trasladados aplicables. Es requerido cuando en los conceptos se registre un impuesto trasladado.')

    
    # Attribute TotalImpuestosRetenidos uses Python identifier TotalImpuestosRetenidos
    __TotalImpuestosRetenidos = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TotalImpuestosRetenidos'), 'TotalImpuestosRetenidos', '__httpwww_sat_gob_mxcfd3_CTD_ANON_21_TotalImpuestosRetenidos', _ImportedBinding__tdCFDI.t_Importe)
    __TotalImpuestosRetenidos._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 551, 14)
    __TotalImpuestosRetenidos._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 551, 14)
    
    TotalImpuestosRetenidos = property(__TotalImpuestosRetenidos.value, __TotalImpuestosRetenidos.set, None, 'Atributo condicional para expresar el total de los impuestos retenidos que se desprenden de los conceptos expresados en el comprobante fiscal digital por Internet. No se permiten valores negativos. Es requerido cuando en los conceptos se registren impuestos retenidos')

    
    # Attribute TotalImpuestosTrasladados uses Python identifier TotalImpuestosTrasladados
    __TotalImpuestosTrasladados = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TotalImpuestosTrasladados'), 'TotalImpuestosTrasladados', '__httpwww_sat_gob_mxcfd3_CTD_ANON_21_TotalImpuestosTrasladados', _ImportedBinding__tdCFDI.t_Importe)
    __TotalImpuestosTrasladados._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 556, 14)
    __TotalImpuestosTrasladados._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 556, 14)
    
    TotalImpuestosTrasladados = property(__TotalImpuestosTrasladados.value, __TotalImpuestosTrasladados.set, None, 'Atributo condicional para expresar el total de los impuestos trasladados que se desprenden de los conceptos expresados en el comprobante fiscal digital por Internet. No se permiten valores negativos. Es requerido cuando en los conceptos se registren impuestos trasladados.')

    _ElementMap.update({
        __Retenciones.name() : __Retenciones,
        __Traslados.name() : __Traslados
    })
    _AttributeMap.update({
        __TotalImpuestosRetenidos.name() : __TotalImpuestosRetenidos,
        __TotalImpuestosTrasladados.name() : __TotalImpuestosTrasladados
    })
_module_typeBindings.CTD_ANON_21 = CTD_ANON_21


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_22 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para la información detallada de una retención de impuesto específico."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 491, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Impuesto uses Python identifier Impuesto
    __Impuesto = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Impuesto'), 'Impuesto', '__httpwww_sat_gob_mxcfd3_CTD_ANON_22_Impuesto', _ImportedBinding__catCFDI.c_Impuesto, required=True)
    __Impuesto._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 492, 26)
    __Impuesto._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 492, 26)
    
    Impuesto = property(__Impuesto.value, __Impuesto.set, None, 'Atributo requerido para señalar la clave del tipo de impuesto retenido')

    
    # Attribute Importe uses Python identifier Importe
    __Importe = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Importe'), 'Importe', '__httpwww_sat_gob_mxcfd3_CTD_ANON_22_Importe', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __Importe._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 497, 26)
    __Importe._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 497, 26)
    
    Importe = property(__Importe.value, __Importe.set, None, 'Atributo requerido para señalar el monto del impuesto retenido. No se permiten valores negativos.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Impuesto.name() : __Impuesto,
        __Importe.name() : __Importe
    })
_module_typeBindings.CTD_ANON_22 = CTD_ANON_22


# Complex type [anonymous] with content type EMPTY
class CTD_ANON_23 (pyxb.binding.basis.complexTypeDefinition):
    """Nodo requerido para la información detallada de un traslado de impuesto específico."""
    _TypeDefinition = None
    _ContentTypeTag = pyxb.binding.basis.complexTypeDefinition._CT_EMPTY
    _Abstract = False
    _ExpandedName = None
    _XSDLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 517, 24)
    _ElementMap = {}
    _AttributeMap = {}
    # Base type is pyxb.binding.datatypes.anyType
    
    # Attribute Impuesto uses Python identifier Impuesto
    __Impuesto = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Impuesto'), 'Impuesto', '__httpwww_sat_gob_mxcfd3_CTD_ANON_23_Impuesto', _ImportedBinding__catCFDI.c_Impuesto, required=True)
    __Impuesto._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 518, 26)
    __Impuesto._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 518, 26)
    
    Impuesto = property(__Impuesto.value, __Impuesto.set, None, 'Atributo requerido para señalar la clave del tipo de impuesto trasladado.')

    
    # Attribute TipoFactor uses Python identifier TipoFactor
    __TipoFactor = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TipoFactor'), 'TipoFactor', '__httpwww_sat_gob_mxcfd3_CTD_ANON_23_TipoFactor', _ImportedBinding__catCFDI.c_TipoFactor, required=True)
    __TipoFactor._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 523, 26)
    __TipoFactor._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 523, 26)
    
    TipoFactor = property(__TipoFactor.value, __TipoFactor.set, None, 'Atributo requerido para señalar la clave del tipo de factor que se aplica a la base del impuesto.')

    
    # Attribute TasaOCuota uses Python identifier TasaOCuota
    __TasaOCuota = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'TasaOCuota'), 'TasaOCuota', '__httpwww_sat_gob_mxcfd3_CTD_ANON_23_TasaOCuota', _module_typeBindings.STD_ANON_19, required=True)
    __TasaOCuota._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 528, 26)
    __TasaOCuota._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 528, 26)
    
    TasaOCuota = property(__TasaOCuota.value, __TasaOCuota.set, None, 'Atributo requerido para señalar el valor de la tasa o cuota del impuesto que se traslada por los conceptos amparados en el comprobante.')

    
    # Attribute Importe uses Python identifier Importe
    __Importe = pyxb.binding.content.AttributeUse(pyxb.namespace.ExpandedName(None, 'Importe'), 'Importe', '__httpwww_sat_gob_mxcfd3_CTD_ANON_23_Importe', _ImportedBinding__tdCFDI.t_Importe, required=True)
    __Importe._DeclarationLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 540, 26)
    __Importe._UseLocation = pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 540, 26)
    
    Importe = property(__Importe.value, __Importe.set, None, 'Atributo requerido para señalar la suma del importe del impuesto trasladado, agrupado por impuesto, TipoFactor y TasaOCuota. No se permiten valores negativos.')

    _ElementMap.update({
        
    })
    _AttributeMap.update({
        __Impuesto.name() : __Impuesto,
        __TipoFactor.name() : __TipoFactor,
        __TasaOCuota.name() : __TasaOCuota,
        __Importe.name() : __Importe
    })
_module_typeBindings.CTD_ANON_23 = CTD_ANON_23


Comprobante = pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Comprobante'), CTD_ANON_9, documentation='Estándar de Comprobante Fiscal Digital por Internet.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 5, 4))
Namespace.addCategoryObject('elementBinding', Comprobante.name().localName(), Comprobante)



CTD_ANON._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Concepto'), CTD_ANON_14, scope=CTD_ANON, documentation='Nodo requerido para registrar la información detallada de un bien o servicio amparado en el comprobante.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 127, 16)))

def _BuildAutomaton ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton
    del _BuildAutomaton
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Concepto')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 127, 16))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON._Automaton = _BuildAutomaton()




CTD_ANON_._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Traslados'), CTD_ANON_2, scope=CTD_ANON_, documentation='Nodo opcional para asentar los impuestos trasladados aplicables al presente concepto.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 139, 28)))

CTD_ANON_._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Retenciones'), CTD_ANON_3, scope=CTD_ANON_, documentation='Nodo opcional para asentar los impuestos retenidos aplicables al presente concepto.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 194, 28)))

def _BuildAutomaton_ ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_
    del _BuildAutomaton_
    import pyxb.utils.fac as fac

    counters = set()
    cc_0 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 139, 28))
    counters.add(cc_0)
    cc_1 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 194, 28))
    counters.add(cc_1)
    states = []
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_0, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Traslados')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 139, 28))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_1, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Retenciones')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 194, 28))
    st_1 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_1)
    transitions = []
    transitions.append(fac.Transition(st_0, [
        fac.UpdateInstruction(cc_0, True) ]))
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_0, False) ]))
    st_0._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_1, True) ]))
    st_1._set_transitionSet(transitions)
    return fac.Automaton(states, counters, True, containing_state=None)
CTD_ANON_._Automaton = _BuildAutomaton_()




CTD_ANON_2._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Traslado'), CTD_ANON_15, scope=CTD_ANON_2, documentation='Nodo requerido para asentar la información detallada de un traslado de impuestos aplicable al presente concepto.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 145, 34)))

def _BuildAutomaton_2 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_2
    del _BuildAutomaton_2
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_2._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Traslado')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 145, 34))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_2._Automaton = _BuildAutomaton_2()




CTD_ANON_3._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Retencion'), CTD_ANON_16, scope=CTD_ANON_3, documentation='Nodo requerido para asentar la información detallada de una retención de impuestos aplicable al presente concepto.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 200, 34)))

def _BuildAutomaton_3 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_3
    del _BuildAutomaton_3
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_3._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Retencion')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 200, 34))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_3._Automaton = _BuildAutomaton_3()




def _BuildAutomaton_4 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_4
    del _BuildAutomaton_4
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.WildcardUse(pyxb.binding.content.Wildcard(process_contents=pyxb.binding.content.Wildcard.PC_strict, namespace_constraint=pyxb.binding.content.Wildcard.NC_any), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 296, 28))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_4._Automaton = _BuildAutomaton_4()




CTD_ANON_5._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Retencion'), CTD_ANON_22, scope=CTD_ANON_5, documentation='Nodo requerido para la información detallada de una retención de impuesto específico.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 487, 22)))

def _BuildAutomaton_5 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_5
    del _BuildAutomaton_5
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_5._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Retencion')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 487, 22))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_5._Automaton = _BuildAutomaton_5()




CTD_ANON_6._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Traslado'), CTD_ANON_23, scope=CTD_ANON_6, documentation='Nodo requerido para la información detallada de un traslado de impuesto específico.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 513, 22)))

def _BuildAutomaton_6 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_6
    del _BuildAutomaton_6
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_6._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Traslado')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 513, 22))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_6._Automaton = _BuildAutomaton_6()




def _BuildAutomaton_7 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_7
    del _BuildAutomaton_7
    import pyxb.utils.fac as fac

    counters = set()
    cc_0 = fac.CounterCondition(min=0, max=None, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 569, 16))
    counters.add(cc_0)
    states = []
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_0, False))
    symbol = pyxb.binding.content.WildcardUse(pyxb.binding.content.Wildcard(process_contents=pyxb.binding.content.Wildcard.PC_strict, namespace_constraint=pyxb.binding.content.Wildcard.NC_any), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 569, 16))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
        fac.UpdateInstruction(cc_0, True) ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, True, containing_state=None)
CTD_ANON_7._Automaton = _BuildAutomaton_7()




def _BuildAutomaton_8 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_8
    del _BuildAutomaton_8
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.WildcardUse(pyxb.binding.content.Wildcard(process_contents=pyxb.binding.content.Wildcard.PC_strict, namespace_constraint=pyxb.binding.content.Wildcard.NC_any), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 579, 16))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_8._Automaton = _BuildAutomaton_8()




CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'CfdiRelacionados'), CTD_ANON_10, scope=CTD_ANON_9, documentation='Nodo opcional para precisar la información de los comprobantes relacionados.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 11, 10)))

CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Emisor'), CTD_ANON_12, scope=CTD_ANON_9, documentation='Nodo requerido para expresar la información del contribuyente emisor del comprobante.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 44, 10)))

CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Receptor'), CTD_ANON_13, scope=CTD_ANON_9, documentation='Nodo requerido para precisar la información del contribuyente receptor del comprobante.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 74, 10)))

CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Conceptos'), CTD_ANON, scope=CTD_ANON_9, documentation='Nodo requerido para listar los conceptos cubiertos por el comprobante.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 121, 10)))

CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Impuestos'), CTD_ANON_21, scope=CTD_ANON_9, documentation='Nodo condicional para expresar el resumen de los impuestos aplicables.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 475, 10)))

CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Complemento'), CTD_ANON_7, scope=CTD_ANON_9, documentation='Nodo opcional donde se incluye el complemento Timbre Fiscal Digital de manera obligatoria y los nodos complementarios determinados por el SAT, de acuerdo con las disposiciones particulares para un sector o actividad específica.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 563, 10)))

CTD_ANON_9._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Addenda'), CTD_ANON_8, scope=CTD_ANON_9, documentation='Nodo opcional para recibir las extensiones al presente formato que sean de utilidad al contribuyente. Para las reglas de uso del mismo, referirse al formato origen.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 573, 10)))

def _BuildAutomaton_9 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_9
    del _BuildAutomaton_9
    import pyxb.utils.fac as fac

    counters = set()
    cc_0 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 11, 10))
    counters.add(cc_0)
    cc_1 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 475, 10))
    counters.add(cc_1)
    cc_2 = fac.CounterCondition(min=0, max=None, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 563, 10))
    counters.add(cc_2)
    cc_3 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 573, 10))
    counters.add(cc_3)
    states = []
    final_update = None
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'CfdiRelacionados')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 11, 10))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    final_update = None
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Emisor')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 44, 10))
    st_1 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_1)
    final_update = None
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Receptor')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 74, 10))
    st_2 = fac.State(symbol, is_initial=False, final_update=final_update, is_unordered_catenation=False)
    states.append(st_2)
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Conceptos')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 121, 10))
    st_3 = fac.State(symbol, is_initial=False, final_update=final_update, is_unordered_catenation=False)
    states.append(st_3)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_1, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Impuestos')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 475, 10))
    st_4 = fac.State(symbol, is_initial=False, final_update=final_update, is_unordered_catenation=False)
    states.append(st_4)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_2, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Complemento')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 563, 10))
    st_5 = fac.State(symbol, is_initial=False, final_update=final_update, is_unordered_catenation=False)
    states.append(st_5)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_3, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_9._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Addenda')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 573, 10))
    st_6 = fac.State(symbol, is_initial=False, final_update=final_update, is_unordered_catenation=False)
    states.append(st_6)
    transitions = []
    transitions.append(fac.Transition(st_0, [
        fac.UpdateInstruction(cc_0, True) ]))
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_0, False) ]))
    st_0._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_2, [
         ]))
    st_1._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_3, [
         ]))
    st_2._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_4, [
         ]))
    transitions.append(fac.Transition(st_5, [
         ]))
    transitions.append(fac.Transition(st_6, [
         ]))
    st_3._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_4, [
        fac.UpdateInstruction(cc_1, True) ]))
    transitions.append(fac.Transition(st_5, [
        fac.UpdateInstruction(cc_1, False) ]))
    transitions.append(fac.Transition(st_6, [
        fac.UpdateInstruction(cc_1, False) ]))
    st_4._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_5, [
        fac.UpdateInstruction(cc_2, True) ]))
    transitions.append(fac.Transition(st_6, [
        fac.UpdateInstruction(cc_2, False) ]))
    st_5._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_6, [
        fac.UpdateInstruction(cc_3, True) ]))
    st_6._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_9._Automaton = _BuildAutomaton_9()




CTD_ANON_10._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'CfdiRelacionado'), CTD_ANON_11, scope=CTD_ANON_10, documentation='Nodo requerido para precisar la información de los comprobantes relacionados.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 17, 16)))

def _BuildAutomaton_10 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_10
    del _BuildAutomaton_10
    import pyxb.utils.fac as fac

    counters = set()
    states = []
    final_update = set()
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_10._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'CfdiRelacionado')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 17, 16))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
         ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, False, containing_state=None)
CTD_ANON_10._Automaton = _BuildAutomaton_10()




CTD_ANON_14._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Impuestos'), CTD_ANON_, scope=CTD_ANON_14, documentation='Nodo opcional para capturar los impuestos aplicables al presente concepto. Cuando un concepto no registra un impuesto, implica que no es objeto del mismo.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 133, 22)))

CTD_ANON_14._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'InformacionAduanera'), CTD_ANON_17, scope=CTD_ANON_14, documentation='Nodo opcional para introducir la información aduanera aplicable cuando se trate de ventas de primera mano de mercancías importadas o se trate de operaciones de comercio exterior con bienes o servicios.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 252, 24)))

CTD_ANON_14._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'CuentaPredial'), CTD_ANON_18, scope=CTD_ANON_14, documentation='Nodo opcional para asentar el número de cuenta predial con el que fue registrado el inmueble, en el sistema catastral de la entidad federativa de que trate, o bien para incorporar los datos de identificación del certificado de participación inmobiliaria no amortizable.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 270, 24)))

CTD_ANON_14._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'ComplementoConcepto'), CTD_ANON_4, scope=CTD_ANON_14, documentation='Nodo opcional donde se incluyen los nodos complementarios de extensión al concepto definidos por el SAT, de acuerdo con las disposiciones particulares para un sector o actividad específica.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 290, 24)))

CTD_ANON_14._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Parte'), CTD_ANON_19, scope=CTD_ANON_14, documentation='Nodo opcional para expresar las partes o componentes que integran la totalidad del concepto expresado en el comprobante fiscal digital por Internet.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 300, 24)))

def _BuildAutomaton_11 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_11
    del _BuildAutomaton_11
    import pyxb.utils.fac as fac

    counters = set()
    cc_0 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 133, 22))
    counters.add(cc_0)
    cc_1 = fac.CounterCondition(min=0, max=None, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 252, 24))
    counters.add(cc_1)
    cc_2 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 270, 24))
    counters.add(cc_2)
    cc_3 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 290, 24))
    counters.add(cc_3)
    cc_4 = fac.CounterCondition(min=0, max=None, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 300, 24))
    counters.add(cc_4)
    states = []
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_0, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_14._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Impuestos')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 133, 22))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_1, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_14._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'InformacionAduanera')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 252, 24))
    st_1 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_1)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_2, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_14._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'CuentaPredial')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 270, 24))
    st_2 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_2)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_3, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_14._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'ComplementoConcepto')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 290, 24))
    st_3 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_3)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_4, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_14._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Parte')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 300, 24))
    st_4 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_4)
    transitions = []
    transitions.append(fac.Transition(st_0, [
        fac.UpdateInstruction(cc_0, True) ]))
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_0, False) ]))
    transitions.append(fac.Transition(st_2, [
        fac.UpdateInstruction(cc_0, False) ]))
    transitions.append(fac.Transition(st_3, [
        fac.UpdateInstruction(cc_0, False) ]))
    transitions.append(fac.Transition(st_4, [
        fac.UpdateInstruction(cc_0, False) ]))
    st_0._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_1, True) ]))
    transitions.append(fac.Transition(st_2, [
        fac.UpdateInstruction(cc_1, False) ]))
    transitions.append(fac.Transition(st_3, [
        fac.UpdateInstruction(cc_1, False) ]))
    transitions.append(fac.Transition(st_4, [
        fac.UpdateInstruction(cc_1, False) ]))
    st_1._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_2, [
        fac.UpdateInstruction(cc_2, True) ]))
    transitions.append(fac.Transition(st_3, [
        fac.UpdateInstruction(cc_2, False) ]))
    transitions.append(fac.Transition(st_4, [
        fac.UpdateInstruction(cc_2, False) ]))
    st_2._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_3, [
        fac.UpdateInstruction(cc_3, True) ]))
    transitions.append(fac.Transition(st_4, [
        fac.UpdateInstruction(cc_3, False) ]))
    st_3._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_4, [
        fac.UpdateInstruction(cc_4, True) ]))
    st_4._set_transitionSet(transitions)
    return fac.Automaton(states, counters, True, containing_state=None)
CTD_ANON_14._Automaton = _BuildAutomaton_11()




CTD_ANON_19._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'InformacionAduanera'), CTD_ANON_20, scope=CTD_ANON_19, documentation='Nodo opcional para introducir la información aduanera aplicable cuando se trate de ventas de primera mano de mercancías importadas o se trate de operaciones de comercio exterior con bienes o servicios.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 306, 28)))

def _BuildAutomaton_12 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_12
    del _BuildAutomaton_12
    import pyxb.utils.fac as fac

    counters = set()
    cc_0 = fac.CounterCondition(min=0, max=None, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 306, 28))
    counters.add(cc_0)
    states = []
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_0, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_19._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'InformacionAduanera')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 306, 28))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    transitions = []
    transitions.append(fac.Transition(st_0, [
        fac.UpdateInstruction(cc_0, True) ]))
    st_0._set_transitionSet(transitions)
    return fac.Automaton(states, counters, True, containing_state=None)
CTD_ANON_19._Automaton = _BuildAutomaton_12()




CTD_ANON_21._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Retenciones'), CTD_ANON_5, scope=CTD_ANON_21, documentation='Nodo condicional para capturar los impuestos retenidos aplicables. Es requerido cuando en los conceptos se registre algún impuesto retenido.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 481, 16)))

CTD_ANON_21._AddElement(pyxb.binding.basis.element(pyxb.namespace.ExpandedName(Namespace, 'Traslados'), CTD_ANON_6, scope=CTD_ANON_21, documentation='Nodo condicional para capturar los impuestos trasladados aplicables. Es requerido cuando en los conceptos se registre un impuesto trasladado.', location=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 507, 16)))

def _BuildAutomaton_13 ():
    # Remove this helper function from the namespace after it is invoked
    global _BuildAutomaton_13
    del _BuildAutomaton_13
    import pyxb.utils.fac as fac

    counters = set()
    cc_0 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 481, 16))
    counters.add(cc_0)
    cc_1 = fac.CounterCondition(min=0, max=1, metadata=pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 507, 16))
    counters.add(cc_1)
    states = []
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_0, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_21._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Retenciones')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 481, 16))
    st_0 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_0)
    final_update = set()
    final_update.add(fac.UpdateInstruction(cc_1, False))
    symbol = pyxb.binding.content.ElementUse(CTD_ANON_21._UseForTag(pyxb.namespace.ExpandedName(Namespace, 'Traslados')), pyxb.utils.utility.Location('http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd', 507, 16))
    st_1 = fac.State(symbol, is_initial=True, final_update=final_update, is_unordered_catenation=False)
    states.append(st_1)
    transitions = []
    transitions.append(fac.Transition(st_0, [
        fac.UpdateInstruction(cc_0, True) ]))
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_0, False) ]))
    st_0._set_transitionSet(transitions)
    transitions = []
    transitions.append(fac.Transition(st_1, [
        fac.UpdateInstruction(cc_1, True) ]))
    st_1._set_transitionSet(transitions)
    return fac.Automaton(states, counters, True, containing_state=None)
CTD_ANON_21._Automaton = _BuildAutomaton_13()

