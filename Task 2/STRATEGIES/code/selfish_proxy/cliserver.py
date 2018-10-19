from SimpleXMLRPCServer import SimpleXMLRPCServer
from SimpleXMLRPCServer import SimpleXMLRPCRequestHandler
import logging
from bitcoin import core
import chainutil
from chain import BlockOrigin


class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)


class Functions:
    
    def __init__(self, chain, sync):
        self.chain = chain
        self.sync = sync

    def get_best_public_block_hash(self):
        self.sync.lock.acquire()
        try:
            logging.debug('received get_best_public_block_hash over cli')

            return core.b2lx(chainutil.get_highest_block_with_cblock(self.chain.tips, BlockOrigin.public).hash())
        finally:
            self.sync.lock.release()
            logging.debug('send get_best_public_block_hash over cli')

    def get_start_hash(self):
        try:
            logging.debug('received get_start_hash over cli')

            return core.b2lx(self.chain.start_hash)
        finally:
            logging.debug('send start_hash over cli')


def start(chain, sync):

    server = SimpleXMLRPCServer(("localhost", 8000),
                                requestHandler=RequestHandler)
    server.register_introspection_functions()

    server.register_instance(Functions(chain, sync))

    logging.info('started cli-server')
    server.serve_forever()





