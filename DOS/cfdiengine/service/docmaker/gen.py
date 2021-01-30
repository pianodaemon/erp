from abc import ABCMeta, abstractmethod
from misc.helperpg import HelperPg
from docmaker.error import DocBuilderStepError
import psycopg2


class BuilderGen(metaclass=ABCMeta):
    """
    Builder interface base class.
    """

    def  __init__(self, logger):
        self.logger = logger

    def __str__(self):
        return self.__class__.__name__

    def pg_query(self, conn, sql):
        try:
            self.logger.debug("Performing query: {}".format(sql))
            return HelperPg.query(conn, sql)
        except psycopg2.Error as e:
            raise DocBuilderStepError("An error occurred when executing query: {}".format(e))
        except Exception as e:
            raise DocBuilderStepError(e)

    @abstractmethod
    def data_acq(self, conn, d_rdirs, **kwargs):
        """document's data acquisition"""

    @abstractmethod
    def format_wrt(self, output_file, dat):
        """writes the document"""

    @abstractmethod
    def data_rel(self, dat):
        """release resources previously gotten"""
