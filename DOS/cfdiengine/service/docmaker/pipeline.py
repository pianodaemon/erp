import logging
import sys
import os
import psycopg2
import importlib
from docmaker.gen import BuilderGen
from custom.profile import ProfileReader
from misc.helperpg import HelperPg
from docmaker.error import DocBuilderImptError, DocBuilderStepError, DocBuilderError


sys.path.append(
    os.path.abspath(
        os.path.join(os.path.dirname(__file__), "builders")
    )
)


class DocPipeLine(object):
    """
    creator instance of documents.
    """

    def __init__(self, logger, resdir = None, rdirs_conf = None):
        self.logger = logger

        if resdir == None:
            raise DocBuilderError('resources directory not fed!!')
        self.resdir = resdir

        if rdirs_conf == None:
            raise DocBuilderError("rdirs config info not fed!!")
        self.rdirs_conf = rdirs_conf

    def run(self, b, output_file, **kwargs):
        """runs docmaker's pipeline to create a document"""

        try:
            self.logger.debug("attempting the import of {0} library".format(b))
            self.logger.debug("Current sys.path content {}".format(sys.path))
            importlib.invalidate_caches()
            doc_mod = importlib.__import__(b)

            if not hasattr(doc_mod, "impt_class"):
                msg = "module {0} has no impt_class attribute".format(b)
                raise DocBuilderImptError(msg)

            self.logger.debug("library {0} succesfully imported".format(b))

            cname = getattr(doc_mod, "impt_class")

            if not hasattr(doc_mod, cname):
                msg = "module {0} has no {1} class implemented".format(b, cname)
                raise DocBuilderImptError(msg)

            self.builder = getattr(doc_mod, cname)(self.logger)

            if not isinstance(self.builder, BuilderGen) and not issubclass(self.builder.__class__, BuilderGen):
                msg = "unknown support library specification in {0}".format(self.builder)
                raise DocBuilderImptError(msg)

        except (ImportError, DocBuilderImptError) as e:
            self.logger.fatal("{0} support library failure".format(b))
            raise e

        self.__create(
            self.__open_dbms_conn(),
            {p["name"]: p["value"] for p in ProfileReader.get_content(
                self.rdirs_conf,
                ProfileReader.PNODE_MANY)
            },
            output_file, **kwargs
        )

    def __create(self, conn, d_rdirs, output_file, **kwargs):
        """runs pipeline's steps"""
        dat = None

        if len(d_rdirs) > 0:
            pass
        else:
            raise DocBuilderError("slack resource dirs configuration")

        fullpath_dirs = {}
        for k, v in d_rdirs.items():
            fullpath_dirs[k] = '{}/{}'.format(self.resdir, v)

        try:
            dat = self.builder.data_acq(conn, fullpath_dirs, **kwargs)
        except DocBuilderStepError:
            raise
        finally:
            conn.close()

        try:
            self.builder.format_wrt(output_file, dat)
        except DocBuilderStepError:
            raise

        try:
            self.builder.data_rel(dat)
        except DocBuilderStepError:
            raise

    def __open_dbms_conn(self):
        """opens a connection to postgresql"""
        try:
            return HelperPg.connect()
        except psycopg2.Error as e:
            self.logger.error(e)
            raise DocBuilderError("dbms was not connected")
        except KeyError as e:
            self.logger.error(e)
            raise DocBuilderError("slack pgsql configuration")
