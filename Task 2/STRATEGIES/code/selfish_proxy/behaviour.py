from bitcoinnetwork import network
from bitcoin import core
from bitcoin import messages
import logging
import chainutil
from strategy import BlockOrigin


class CatchUpBehaviour(network.ClientBehavior):
    def __init__(self, network_client, catch_up_connection, chain):
        super(CatchUpBehaviour, self).__init__(network_client)

        self.catch_up_connection = catch_up_connection
        self.chain = chain

    def on_version(self, connection, unused_message):
        if connection.incoming:
            self.send_version(connection)
            self.send_verack(connection)

        else:
            self.send_verack(connection)

        msg = messages.msg_getheaders()
        msg.locator = messages.CBlockLocator()
        headers = chainutil.request_get_headers(self.chain.tips, BlockOrigin.public)
        msg.locator.vHave = headers
        connection.send('getheaders', msg)
        logging.info('requested getheaders with starting hash={} from new connection={}'
                     .format(core.b2lx(headers[0]), connection.host))
