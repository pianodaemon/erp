#!/usr/bin/python3

import os
from logging.handlers import TimedRotatingFileHandler
import multiprocessing
import traceback
import argparse
import logging
import sys
from bbgum.server import BbGumServer


def listener_configurer(log_path, debug):
    # if no name is specified, return a logger
    # which is the root logger of the hierarchy.
    root = logging.getLogger()

    # create file handler which logs even debug messages
    fh = TimedRotatingFileHandler(log_path, when="d",
                                  interval=1, backupCount=7)
    fh.setLevel(logging.DEBUG if debug else logging.INFO)

    # create console handler with a higher log level
    ch = logging.StreamHandler()
    ch.setLevel(logging.WARNING)

    # create formats and add them to the handlers
    fh_formatter = logging.Formatter(
        '%(asctime)s %(processName)-10s %(name)s %(levelname)-8s %(message)s')
    ch_formatter = logging.Formatter(
        '%(processName)-10s %(name)s %(levelname)-8s - %(filename)s - Line: %(lineno)d - %(message)s')
    fh.setFormatter(fh_formatter)
    ch.setFormatter(ch_formatter)

    # add the handlers to root
    root.addHandler(ch)
    root.addHandler(fh)


def listener_process(queue, configurer, log_path, debug=False):
    '''process that receives log traces from connection process'''

    configurer(log_path, debug)
    while True:
        try:
            record = queue.get()
            if record is None:  # We send this as a sentinel to tell the listener to quit.
                print('Finishing log listener')
                break
            logger = logging.getLogger(record.name)
            logger.handle(record)  # No level or filter logic applied - just do it!
        except KeyboardInterrupt:
            # SIGINT is masked in the child processes. 
            # that's why this workaround is required
            # to exit reliably
            pass
        except:
            if debug:
                print('Whoops! Problem in log listener:', file=sys.stderr)
                traceback.print_exc(file=sys.stderr)


def parse_cmdline():
    """parses the command line arguments at the call."""

    psr_desc = "cfdi engine service interface"
    psr_epi = "select a config profile to specify defaults"

    psr = argparse.ArgumentParser(
        description=psr_desc, epilog=psr_epi)

    psr.add_argument('-d', action='store_true', dest='debug',
                     help='print debug information')

    psr.add_argument('-c', '--config', action='store',
                     dest='config', help='load an specific config profile')

    psr.add_argument('-p', '--port', action='store',
                     dest='port', help='launches service on specific port')

    return psr.parse_args()


if __name__ == "__main__":

    args = parse_cmdline()

    RESOURCES_DIR = '{}/resources'.format(os.environ['ERP_ROOT'])
    PROFILES_DIR = '{}/profiles'.format(RESOURCES_DIR)
    LOGS_DIR = '{}/logs'.format(RESOURCES_DIR)
    LOG_NAME = 'blcore'
    DEFAULT_PORT = 10080
    DEFAULT_PROFILE = 'default.json'

    log_path = '{}/{}.log'.format(LOGS_DIR, LOG_NAME)
    profile_path = '{}/{}'.format(PROFILES_DIR,
                                  args.config if args.config else DEFAULT_PROFILE)
    port = int(args.port) if args.port else DEFAULT_PORT

    queue = multiprocessing.Queue(-1)
    listener = multiprocessing.Process(target=listener_process,
                                       args=(queue, listener_configurer, log_path, args.debug))
    listener.start()

    try:
        server = BbGumServer(queue, profile_path, port)
        server.start(args.debug)
    except KeyboardInterrupt:
        print('Exiting')
    except:
        if args.debug:
            print('Whoops! Problem in server:', file=sys.stderr)
            traceback.print_exc(file=sys.stderr)
        sys.exit(1)

    # it'll break eternal loop inside listener process
    queue.put_nowait(None)
    listener.join()

    # assuming everything went right, exit gracefully
    sys.exit(0)
