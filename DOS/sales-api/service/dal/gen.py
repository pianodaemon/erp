from abc import ABCMeta, abstractmethod
from .helperpg import HelperPg
from .error import DocBuilderStepError
import psycopg2


class BuilderGen(metaclass=ABCMeta):
    """
    Builder interface base class.
    """

    def  __init__(self):
        pass

    def __str__(self):
        return self.__class__.__name__

    def pg_query(self, conn, sql):
        try:
            return HelperPg.query(conn, sql)
        except psycopg2.Error as e:
            raise DocBuilderStepError("An error occurred when executing query: {}".format(e))
        except Exception as e:
            raise DocBuilderStepError(e)

    @abstractmethod
    def data_acq(self, conn, d_rdirs, **kwargs):
        """document's data acquisition"""
