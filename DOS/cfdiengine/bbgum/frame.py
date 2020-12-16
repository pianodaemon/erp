class FrameError(Exception):
    def __init__(self, message=None):
        self.message = message
    def __str__(self):
        return self.message

class Frame(object):

    FRAME_HEADER_LENGTH = 4
    FRAME_BODY_MAX_LENGTH = 512
    ACTION_FLOW_INFO_SEGMENT_LENGTH = 2
    ACTION_ACK_DATA_SIZE = 2
    FRAME_FULL_MAX_LENGTH = FRAME_HEADER_LENGTH + FRAME_BODY_MAX_LENGTH
    ACTION_DATA_SEGMENT_MAX_LENGTH = FRAME_BODY_MAX_LENGTH - ACTION_FLOW_INFO_SEGMENT_LENGTH
    C_NULL_CHARACTER = 0

    REPLY_PASS = b'\x06'
    REPLY_FAIL = b'\x15'

    header = bytearray([0] * FRAME_HEADER_LENGTH)
    body = bytearray()
    action_length = 0

    def __init__(self, action=None):
        if action:
            self.action_length = Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH + len(action.buff) 
            self.header = Frame.encode_header(self.action_length)
            self.body = bytearray([0] * self.action_length)
            self.body[0] = action.archetype
            self.body[1] = action.transnum
            self.body[Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH:] = action.buff

    def get_action(self):
        """fetch the action within current instance"""
        a = Action()
        a.archetype = self.body[0]
        a.transnum = self.body[1]
        a.buff = self.body[Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH:]
        return a

    def dump(self):
        """create a bytes dump of current instance"""
        size = self.FRAME_HEADER_LENGTH + len(self.body)
        d = bytearray([0] * size)
        d[:self.FRAME_HEADER_LENGTH-1] = self.header
        d[self.FRAME_HEADER_LENGTH:] = self.body
        return d

    @staticmethod
    def encode_header(length):
        if length > Frame.FRAME_BODY_MAX_LENGTH:
            raise FrameError("invalid length to encode!!")
        l = []
        for sc in '{:4d}'.format(length):
            l.append(ord(sc))
        return bytes(l)

    @staticmethod
    def decode_header(header):
        """decodes ascii header of a frame"""
        if len(header) == Frame.FRAME_HEADER_LENGTH:
            try:
                return int(header.decode("utf-8"))
            except:
                raise FrameError("unexpected problems when decoding header!!")
        raise FrameError("header's width violation!!")

    @staticmethod
    def reply_archetype(archetype):
        """calculates reply archetype as per fail or pass"""
        return archetype + 1

class Action(object):

    archetype = 0
    transnum = 0
    buff = bytearray()

    def __init__(self, data=None):
        if data is not None:
            length = len(data)
            if length > Frame.FRAME_BODY_MAX_LENGTH:
                msg = '{} {} bytes'.format(
                    "Action can not be bigger than",
                    str(Frame.FRAME_BODY_MAX_LENGTH)
                )
                raise FrameError(msg)
            self.archetype = data[0]
            self.transnum = data[1]
            self.buff = data[Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH:]
