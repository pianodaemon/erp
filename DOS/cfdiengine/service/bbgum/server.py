from bbgum.frame import Action, Frame, FrameError
from bbgum.monitor import Monitor
from engine.erp import ControllerFactory
from misc.tricks import dump_exception
import logging
import multiprocessing
import socket


class BbGumServer(object):

    __HOST = ''  # Symbolic name meaning all available interfaces
    __QCON_MAX = 5  # Maximum number of queued connections
    __WORKERS = __QCON_MAX + 1

    def __init__(self, queue, profile_path, port):
        self.queue = queue
        self.profile_path = profile_path
        self.port = port
        self.ps = []
        self.conns_queue = multiprocessing.JoinableQueue(-1)

    def start(self, debug):
        """start the service upon selected port"""

        def wakeup():
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.bind((self.__HOST, self.port))
            self.socket.listen(self.__QCON_MAX)

            self.ps = [
                multiprocessing.Process(
                    target=self.conn_delegate, args=(
                        self.conns_queue, self.profile_path,
                        self.queue, self.conn_logconf, debug
                    )
                ) for _ in range(self.__WORKERS)
            ]

            for p in self.ps:
                p.daemon = True
                p.start()
                print("Started process {0}".format(repr(p)))

            print('Use Control-C to exit')
            while True:
                conn, addr = self.socket.accept()
                print("Got connection {} from {}".format(
                                       repr(conn), repr(addr)))
                self.conns_queue.put_nowait(conn)

        def shutdown():
            for p in self.ps:
                p.join()

        try:
            wakeup()
        except KeyboardInterrupt:
            raise
        except:
            raise
        finally:
            print("Shutting down")
            shutdown()

    def conn_logconf(self, queue, debug):
        h = logging.handlers.QueueHandler(queue)
        root = logging.getLogger()
        root.addHandler(h)
        root.setLevel(debug)

    def conn_delegate(self, conns_queue, profile_path, queue, configurer, debug):
        """deals with an active connection"""

        configurer(queue, debug)
        name = multiprocessing.current_process().name
        logger = logging.getLogger(name)
        conn = None

        def read_socket(s):
            d = conn.recv(s)
            if d == b'':
                raise RuntimeError("socket connection broken")
            return d

        read_header = lambda: read_socket(Frame.FRAME_HEADER_LENGTH)
        read_body = lambda hs: read_socket(hs)

        while True:
            try:
                conn = conns_queue.get()

                factory = ControllerFactory(logger, profile_path)
                mon = Monitor(logger, conn, factory)

                while True:
                    mon.receive(Action(read_body(
                            Frame.decode_header(read_header()))))
            except RuntimeError as e:
                logger.info(e)
            except FrameError as e:
                logger.exception(e)
            except KeyboardInterrupt:
                # SIGINT is masked in the child processes.
                # that's why this workaround is required
                # to exit reliably
                logger.debug('Finishing worker {}'.format(name))
                break
            except:
                logger.error(dump_exception())

            logger.debug("Closing socket")
            conn.close()
