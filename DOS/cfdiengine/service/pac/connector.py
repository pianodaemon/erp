from custom.profile import ProfileReader
from misc.tricks import dict_params
from pac.servisim import Servisim


def setup_pac(logger, conf):
    """
    Sets a pac adapter up as per configuration object
    """
    support = {
        # Here you should subscribe any newer
        # adapter implementation (AKA pac adapter)
        'servisim': dict(test=(Servisim, conf.test), real=(Servisim, conf.real)),
        'fake': dict(test=(Fake, conf.test), real=(Fake, conf.real))
    }

    name = ProfileReader.get_content(conf.name, ProfileReader.PNODE_UNIQUE)
    mode = ProfileReader.get_content(conf.mode, ProfileReader.PNODE_UNIQUE)
    supplier = support.get(name.lower(), None)
    msg = None

    if supplier is not None:
        try:
            ic, settings = supplier[mode]
            return ic(
                logger, **dict_params(
                    ProfileReader.get_content(
                        settings,
                        ProfileReader.PNODE_MANY
                    ),
                    "param", "value"
                )
            ), msg
        except KeyError:
            msg = "Such pac mode is not supported"
    else:
        msg = "Such pac is not supported yet"

    return None, msg
