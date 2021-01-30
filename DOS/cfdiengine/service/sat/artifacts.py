import enum


class CfdiType(enum.Enum):
    """
    Cfdi types that shall be applied
    through cfdi writing customization
    """
    FAC = 0
    NCR = 1
    PAG = 2
