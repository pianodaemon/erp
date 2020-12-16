from bbgum.controller import Rwr
from engine.buffmediator import BuffMediator
from engine.error import ErrorCode
from engine.erp import do_request


impt_class = 'RwrBuffTrans'


class RwrBuffTrans(Rwr):
    """
    Deals with receive with response transaction's actions
    """

    START_BUFF_TRANS_MODE_GET = b'\xAA'
    START_BUFF_TRANS_MODE_POST = b'\xBB'
    STOP_BUFF_TRANS = b'\xCC'

    def __init__(self, logger, bm):
        super().__init__()
        self.logger = logger
        self.bm = bm

    def process_buff(self, buff):
        mode = buff[0]
        if mode == ord(self.START_BUFF_TRANS_MODE_POST):
            data_str = buff[1:].decode(encoding='UTF-8')
            sid = self.bm.mediate(BuffMediator.IN_STREAM, int(data_str), do_request)
            return ErrorCode.SUCCESS.value, '{:02X}'.format(sid).encode()
        elif mode == ord(self.STOP_BUFF_TRANS):
            sid = buff[1]
            rc = self.bm.release(sid)
            return rc, '{:02X}'.format(sid).encode()
        else:
            # non-supported command
            return ErrorCode.MODE_NOT_SUPPORTED.value, None

    def postmortem(self, failure):
        self.logger.error(failure)
