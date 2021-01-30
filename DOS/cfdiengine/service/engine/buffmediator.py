from misc.slackpool import SlackPool
from engine.error import ErrorCode


class BuffMediator(object):
    """
    Mediator to deal with buffer transfers
    """

    IN_STREAM, OUT_STREAM = range(2)

    def __init__(self, logger, pt):
        self.logger = logger
        self.pt = pt
        self.sp = SlackPool(start=1, last=10,
                            increment=1, reset=11)

    def mediate(self, sense, size, on_release):
        m = {
            'BUFF': bytearray(),
            'SENSE': sense,
            'SIZE': size,
            'ON_RELEASE': on_release
        }
        return self.sp.place_smart(m)

    def release(self, sid):
        m = self.sp.fetch_from(sid)
        self.sp.destroy_at(sid)
        if len(m['BUFF']) == m['SIZE']:
            return m['ON_RELEASE'](self.logger, self.pt, m['BUFF'])
        else:
            return ErrorCode.BUFF_INCOMPLETE.value

    def write(self, sid, buff):
        m = self.sp.fetch_from(sid)
        m['BUFF'] += buff
