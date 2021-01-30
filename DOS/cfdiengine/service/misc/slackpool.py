import threading


class SlackPool(object):
    """pool that stores any kind of elements"""

    pool_lock = threading.Lock()

    def __init__(self, **kwargs):
        try:
            self.num_start_value = kwargs['start']
            self.num_last_value = kwargs['last']
            self.num_increment = kwargs['increment']
            self.max_nodes = kwargs['reset']
        except KeyError:
            raise Exception('one or more elements of pool have not been set')

        # Initialization of elements for transactions pool
        self.next_num = self.num_start_value
        self.pool = [None] * self.max_nodes

    def destroy_at(self, slot):
        """destroy the chosen element"""
        self.place_at(slot, None)

    def place_smart(self, t):
        """place a element at available pool slot"""

        def req_next():
            i = self.next_num
            if (self.pool[i] is not None) and (i == self.num_last_value):
                # From the first shelf we shall start
                # the quest of an available one if
                # next one was ocuppied and the last one.
                i = self.num_start_value

            if self.pool[i] is None:
                # When the shelf is available we shall return it
                # before we shall set next_num variable up for
                # later calls to current function.
                if i == self.num_last_value:
                    self.next_num = self.num_start_value
                else:
                    self.next_num = i + self.num_increment
                return i

            # If you've reached this code block my brother, so...
            # you might be in trouble soon. By the way you seem
            # a lucky folk and perhaps you would find a free
            # shelf by performing sequential search with awful
            # linear time. Otherwise the matter is fucked :(
            j = 0
            while True:
                i += self.num_increment
                j += 1
                if (self.pool[i] is not None) and (j < self.max_nodes):
                    break

            if j == (self.max_nodes - 1):
                self.next_num = i + self.num_increment
            return i

        self.pool_lock.acquire()
        slot = req_next()
        self.pool[slot] = t
        self.pool_lock.release()
        return slot

    def place_at(self, slot, t):
        """place a element at specific pool slot"""
        self.pool_lock.acquire()
        self.pool[slot] = t
        self.pool_lock.release()

    def fetch_from(self, slot):
        """fetches a element from pool"""
        self.pool_lock.acquire()
        t = self.pool[slot]
        self.pool_lock.release()
        return t
