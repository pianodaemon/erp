from custom.profile import ProfileReader
import os


def fetch_rdirs(resdir, rdirs_conf):
    """Creates dict with resource directories of full path"""
    d_rdirs = {p["name"]: p["value"] for p in ProfileReader.get_content(
        rdirs_conf,
        ProfileReader.PNODE_MANY)
    }
    return {k: os.path.join(resdir, v) for k, v in d_rdirs.items()}
