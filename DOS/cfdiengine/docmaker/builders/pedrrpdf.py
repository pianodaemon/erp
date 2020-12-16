from docmaker.gen import BuilderGen
from docmaker.error import DocBuilderStepError
from misc.numspatrans import numspatrans

from reportlab.platypus import BaseDocTemplate, PageTemplate, Frame, Table, TableStyle, Paragraph, Spacer, Image
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import cm
from reportlab.pdfgen import canvas
from reportlab.lib.enums import TA_CENTER

import os


impt_class='PedidoRR'


class PedidoRR(BuilderGen):

    __CAPTIONS = {
            'TL_DOC_NAME': 'REMISION DEL PEDIDO'
            'TL_PEDIDO_NUM': 'NO.',
            'TL_ORDER_DATE': 'FECHA DE PEDIDO',
            'TL_DOC_DATE': 'FECHA DE REMISION',
            'TL_ART_SUBT': 'SUB-TOTAL',
            'TL_ART_TOTAL': 'TOTAL',
            'TL_ART_UNIT': 'UNIDAD',
            'TL_PROVIDER_NAME': 'PROVEEDOR',
            'TL_ART_QUAN': 'CANTIDAD',
            'TL_ART_UP': 'P. UNITARIO',
            'TL_ART_SKU': 'CLAVE',
            'TL_PAY_WAY': 'PLAZO',
            'TL_ART_AMNT': 'IMPORTE',
            'TL_ART_DES': 'DESCRIPCION',
        }

    def __init__(self, logger):
        super().__init__(logger)

    def data_acq(self, conn, d_rdirs, **kwargs):
        return dict([])

    def format_wrt(self, output_file, dat):
        return

    def data_rel(self, dat):
        pass
