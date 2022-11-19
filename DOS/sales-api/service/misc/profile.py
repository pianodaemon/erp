import json
import os
from .error import FatalError


def env_property(prop, caster=None):
    '''Read env variables for microservice's sake'''

    val = os.environ.get(prop)
    if val is None:
        raise FatalError("Enviroment variable {} has not been set !!".format(prop))

    if caster is None:
       return val

    try:
        return caster(val)
    except:
        raise FatalError("Enviroment variable {} could not be casted !!".format(prop))


class ProfileTree:
    """object to chain other config nodes"""

    def __init__(self, data):
        self.data=data

    def __getattr__(self, key):

        try:
            return ProfileTree(self.data[key])
        except TypeError:
            result=[]
            for item in self.data:
                if key in item:
                    try:
                        result.append(item[key])
                    except TypeError: pass
            return ProfileTree(result)

    def __getitem__(self, key):

        return ProfileTree(self.data[key])

    def __iter__(self):

        if isinstance(self.data, str):
            # self.data might be a str or unicode object
            yield self.data
        else:
            # self.data might be a list or tuple
            try:
                for item in self.data:
                    yield item
            except TypeError:
                # self.data might be an int or float
                yield self.data

    def __length_hint__(self):
        return len(self.data)


class ProfileReader(object):
    """
    create a profile tree as per
    a determined config profile
    """

    PNODE_UNIQUE, PNODE_MANY = range(2)

    def __init__(self, logger):
        self.__logger = logger


    def __call__(self, p_file_path):

        def parse_profile():
            """
            Parses a profile in json format
            """
            try:
                json_lines = open(p_file_path).read()
                parsed_json = json.loads(json_lines)
                return parsed_json['profile']
            except (KeyError, OSError, IOError) as e:
                self.__logger.error(e)
                self.__logger.fatal(
                    "malformed profile file in: {0}".format(p_file_path)
                )
                raise

        try:
            prof = parse_profile()
            prof['source'] = p_file_path
            return ProfileTree(prof)
        except:
            raise

    @staticmethod
    def get_content(pt_node, flavor):
        return [
            lambda : list(pt_node)[0],
            lambda : list(pt_node)
        ][flavor]()
