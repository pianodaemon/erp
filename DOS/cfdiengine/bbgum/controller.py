from bbgum.frame import Action, Frame
from abc import ABCMeta, abstractmethod


class Controller(object):
    """
    Deals back and forth with transaction's actions
    """

    # Success error code must be always zero
    # upon children and even its descendants
    SUCCESS = 0

    def finished(self):
        """indicates when internal state machine has finished"""
        pass

    def outcoming(self, mon, act):
        """handler to work outcoming action out"""
        pass

    def incoming(self, mon, act):
        """handler to work incoming action out"""
        pass

    def get_reply(self):
        """conforms reply for blocking transaction"""
        pass


class Sr(Controller, metaclass=ABCMeta):
    """
    Deals with single receive transaction's actions
    """

    def __init__(self):
        pass

    def finished(self):
        return True

    def incoming(self, mon, act):

        def result_buff():
            rc = self.process_buff(act.buff)
            reply = ord(
                Frame.REPLY_PASS if rc == self.SUCCESS else Frame.REPLY_FAIL
            )
            return bytes([reply, rc])

        a = Action()
        a.archetype = Frame.reply_archetype(act.archetype)
        a.transnum = act.transnum
        a.buff = result_buff()
        mon.send(a)

    @abstractmethod
    def process_buff(self, buff):
        """processes incoming buffer"""


class Rwr(Controller, metaclass=ABCMeta):
    """
    Deals with receive with response transaction's actions
    """

    IN_RECV_REQ, IN_RECV_REPLY = range(2)

    def __init__(self):
        self.current_step = self.IN_RECV_REQ
        self.steps = [self.__recv_request, self.__recv_reply]
        self.finish_flag = False

    def finished(self):
        return self.finish_flag

    def incoming(self, mon, act):
        self.steps[self.current_step](mon, act)

    def __recv_request(self, mon, act):
        """process an incoming request"""

        def res_action(s):
            """creates action with request result code"""
            reply = ord(
                Frame.REPLY_PASS if s == self.SUCCESS else Frame.REPLY_FAIL
            )
            a = Action()
            a.archetype = Frame.reply_archetype(act.archetype)
            a.transnum = act.transnum
            a.buff = bytes([reply, s])
            return a

        def resp_action(d):
            """creates action with response's data"""
            a = Action()
            a.archetype = act.archetype
            a.transnum = act.transnum
            a.buff = d
            return a

        (status, buff) = self.process_buff(act.buff)
        mon.send(res_action(status))

        if status == self.SUCCESS:
            mon.send(resp_action(buff))
            self.current_step = self.IN_RECV_REPLY
        else:
            self.finish_flag = True

    def __recv_reply(self, mon, act):
        """process an incoming reply"""
        if act.buff[0] == Frame.REPLY_FAIL:
            reason = act.buff[1]
            self.postmortem(reason)
        self.finish_flag = True

    @abstractmethod
    def process_buff(self, buff):
        """processes incoming buffer"""

    @abstractmethod
    def postmortem(self, failure):
        """analyzes a failure code"""
