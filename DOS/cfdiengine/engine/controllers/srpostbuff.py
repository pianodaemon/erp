from bbgum.controller import Sr
from engine.erp import ErrorCode


impt_class = 'SrPostBuff'


class SrPostBuff(Sr):
    """
    Deals with single receive transaction's actions
    """

    def __init__(self, logger, bm):
        super().__init__()
        self.logger = logger
        self.bm = bm

    def process_buff(self, buff):
        rc = ErrorCode.SUCCESS.value
        sid = buff[0]
        self.bm.write(sid, buff[1:])
        return rc
