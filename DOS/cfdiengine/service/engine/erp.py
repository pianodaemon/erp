from misc.factory import Factory
from misc.tricks import dict_params
from custom.profile import ProfileReader
from engine.buffmediator import BuffMediator
from misc.tricks import dump_exception
from engine.error import ErrorCode
import os
import json
import sys


def do_request(logger, pt, req, adapter=None):
    """"""
    def apply_adapter():
        if adapter is not None:
            return adapter()
        # So we assumed request are bytes of a json string
        json_lines = req.decode(encoding='UTF-8')
        return json.loads(json_lines)

    d = apply_adapter()
    try:
        business_mod = d['request']['to']
        action = d['request']['action']
        args = d['request']['args']

        m = __import__(business_mod)

        if not hasattr(m, action):
            msg = "module {0} has no handler {1}".format(business_mod, action)
            raise RuntimeError(msg)

        handler = getattr(m, action)
        return handler(logger, pt, args)
    except (ImportError, RuntimeError) as e:
        logger.fatal("support module failure {}".format(e))
        return ErrorCode.MOD_BUSINESS_NOT_LOADED.value
    except:
        logger.error(dump_exception())
        return ErrorCode.MOD_BUSINESS_UNEXPECTED_FAIL.value


class ControllerFactory(Factory):

    _CONTROLLERS = [
        {"archetype": "0x30", "event_mod": "srhello"},
        {"archetype": "0x24", "event_mod": "srpostbuff"},
        {"archetype": "0x28", "event_mod": "rwrbufftrans"},
    ]

    def __init__(self, logger, profile_path):
        super().__init__()
        self.logger = logger
        pt = self.__read_settings(profile_path)
        for name in ["controllers", "business"]:
            sys.path.append(
                os.path.abspath(os.path.join(
                    os.path.dirname(__file__), name)))
        self.bm = BuffMediator(self.logger, pt)
        self.__makeup_factory()

    def __read_settings(self, s_file):
        self.logger.debug("looking for config profile file in:\n{0}".format(
            os.path.abspath(s_file)))
        if os.path.isfile(s_file):
            reader = ProfileReader(self.logger)
            return reader(s_file)
        raise Exception("unable to locate the config profile file")

    def __makeup_factory(self):
        devents = dict_params(self._CONTROLLERS, 'archetype', 'event_mod')
        for archetype, event_mod in devents.items():
            try:
                m = __import__(event_mod)

                if not hasattr(m, "impt_class"):
                    msg = "module {0} has no impt_class attribute".format(event_mod)
                    raise RuntimeError(msg)
                cname = getattr(m, "impt_class")

                if not hasattr(m, cname):
                    msg = "module {0} has no {1} class implemented".format(event_mod, cname)
                    raise RuntimeError(msg)
                ic = getattr(m, cname)
                self.subscribe(int(archetype, 0), ic)
            except (ImportError, RuntimeError) as e:
                self.logger.fatal("{0} support library failure".format(event_mod))
                raise e

    def incept(self, i):
        ic = self.inceptors.get(i, None)
        return None if ic is None else ic(self.logger, self.bm)
