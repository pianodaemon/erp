
class Transaction(object):

    def __init__(self, c, block, mode=False):
        self.controller = c
        self.server_mode = mode
        self.blocking = block
