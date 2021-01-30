class Factory(object):
    '''
    '''
    inceptors = {}

    def __init__(self):
        pass

    def is_supported(self, i):
        ic = self.inceptors.get(i, None)
        return False if not ic else True

    def subscribe(self, i, ic):
        self.inceptors[i] = ic

    def incept(self, i):
        ic = self.inceptors.get(i, None)
        return None if ic is None else ic()
