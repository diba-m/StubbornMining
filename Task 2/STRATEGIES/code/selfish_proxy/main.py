import argparse
import logging
import sys
from networking import Networking
from strategy.executor import Executor
from strategy.code import Strategy
from chain import Chain
from bitcoin import core
import threading
import cliserver
from threading import Lock


def check_positive(value):
    integer_value = int(value)
    if integer_value < 0:
        raise argparse.ArgumentTypeError("%s is an invalid positive int value" % value)
    return integer_value


class Sync(object):
    def __init__(self):
        self.lock = Lock()


def parse_args():
    parser = argparse.ArgumentParser(description='Running Selfish Mining Proxy.')

    # general settings #
    parser.add_argument('-v', '--verbose'
                        , help='Increase output verbosity'
                        , action='store_true'
                        )

    parser.add_argument('--start-hash'
                        , help='Set the start hash for selfish mining'
                        )

    parser.add_argument('--private-ip'
                        , help='Set the ip of the private node'
                        , default='240.0.0.2'
                        )

    parser.add_argument('--check-blocks-in-flight-interval'
                        , help='Interval to periodically check blocks in flight'
                        , default=0.5
                        )

    parser.add_argument('--check-blocks-in-flight-interval'
                        , help='Interval to periodically check blocks in flight'
                        , default=0.5
                        )

    # strategies #
    parser.add_argument('--lead-stubborn'
                        , help='Use lead-stubbornness in strategy'
                        , action='store_true'
                        )

    parser.add_argument('--equal-fork-stubborn'
                        , help='Use equal-fork-stubbornness in strategy'
                        , action='store_true'
                        )

    parser.add_argument('--trail-stubborn'
                        , help='Use N-trail-stubbornness in strategy'
                        , type=check_positive
                        , default=0
                        )

    return parser.parse_args()


def config_logger(verbose):
    logFormatter = logging.Formatter("%(asctime)s.%(msecs)03d000 [%(threadName)-12.12s] "
                                     "[%(levelname)-5.5s]  %(message)s", "%Y-%m-%d %H:%M:%S")
    rootLogger = logging.getLogger()

    fileHandler = logging.FileHandler("{0}/{1}.log".format('/tmp/', 'selfish_proxy'))
    fileHandler.setFormatter(logFormatter)
    rootLogger.addHandler(fileHandler)

    consoleHandler = logging.StreamHandler(sys.stdout)
    consoleHandler.setFormatter(logFormatter)
    rootLogger.addHandler(consoleHandler)

    if verbose:
        rootLogger.setLevel(logging.DEBUG)
    else:
        rootLogger.setLevel(logging.INFO)


def main():
    args = parse_args()

    config_logger(args.verbose)

    logging.info("arguments called with: {}".format(sys.argv))
    logging.info("parsed arguments: {}".format(args))

    sync = Sync()

    networking = Networking(args.check_blocks_in_flight_interval, args.private_ip, sync)
    executor = Executor(networking)
    strategy = Strategy(args.lead_stubborn, args.equal_fork_stubborn, args.trail_stubborn)
    if args.start_hash:
        chain = Chain(executor, strategy, core.lx(args.start_hash))
    else:
        chain = Chain(executor, strategy)
    networking.chain = chain

    t = threading.Thread(target=cliserver.start, args=(chain, sync,))
    t.daemon = True
    t.start()

    networking.start()


if __name__ == '__main__':
    main()
