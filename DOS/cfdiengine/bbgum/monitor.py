from queue import Queue, Empty
from bbgum.frame import Action, Frame, FrameError
from bbgum.transaction import Transaction
from misc.slackpool import SlackPool


class Monitor(object):
    """Entity to deal with incoming/outcoming transactions"""

    def __init__(self, logger, conn, factory):
        self.logger = logger
        self.conn = conn
        self.factory = factory
        self.outgoing = Queue(maxsize=0)
        self.tp = SlackPool(start=2, last=254,
                            increment=2, reset=256)

    def push_buff(self, archetype, buff, block=True):

        def incept_trans():
            try:
                return Transaction(self.factory.incept(archetype), block)
            except Exception as e:
                self.logger.exception(e)
                raise FrameError('Transaction could not be created')

        def makeup_action(transnum):
            act = Action()
            act.archetype = archetype
            act.buff = buff
            act.transnum = transnum
            return act

        t = incept_trans()
        slot = self.tp.place_smart(self, t)
        a = makeup_action(bytes([slot]))

        t.controller.outcoming(self, a)

        if t.blocking:
            try:
                t.sleep()
            except Exception as e:
                self.logger.exception(e)
                raise FrameError('Transaction could not await')

            reply = t.controller.get_reply()
            self.tp.destroy_at(a.transnum)
            # Blocking transaction returns a dictionary
            return reply
        else:
            # Non-blocking transaction returns None
            return None

    def receive(self, a):
        """receives action from upper layer"""

        client_origin = lambda n: (n % 2) == 1

        if (not self.factory.is_supported(
                a.archetype)) and (not self.factory.is_supported(
                a.archetype - 1)):
            msg = '{} {}!'.format(
                'The client side sent an invalid action which',
                'is not registered yet!. It will be ignore'
            )
            raise FrameError(msg)

        t = self.tp.fetch_from(a.transnum)

        if t is None:
            if client_origin(a.transnum):
                try:
                    t = Transaction(self.factory.incept(a.archetype), False)
                except Exception as e:
                    self.logger.exception(e)
                    raise FrameError("Transaction could not be created")

                self.tp.place_at(a.transnum, t)
                t.controller.incoming(self, a)
            else:
                msg = '{} ({}) {}. {}'.format(
                    "The transaction number",
                    ord(a.transnum),
                    'in the Action is not a client transaction number.',
                    "It will be ignore!"
                )
                raise FrameError(msg)
        else:
            t.controller.incoming(self, a)

        if t.controller.finished():
            # finalization for actions
            # incepted through push_buff
            if t.blocking:
                t.wake_up()
            else:
                self.tp.destroy_at(a.transnum)

    def send(self, a):
        """write action upon socket"""

        def release():
            try:
                frame = self.outgoing.get_nowait()
                buff = frame.dump()
                size = len(buff)
                total = 0
                while total < size:
                    sent = self.conn.send(buff[total:])
                    if sent == 0:
                        raise RuntimeError("socket connection broken")
                    total += sent
            except Empty as e:
                self.logger.warning(e)

        available = self.outgoing.empty()
        self.outgoing.put(Frame(a))
        if available:
            while not self.outgoing.empty():
                release()
