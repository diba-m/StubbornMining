from strategy import ActionException
from strategy import Action
import logging


class Executor:

    def __init__(self, networking):
        self.networking = networking

    def execute(self, action, private_tip, public_tip):
        blocks_to_transfer = []

        if action is Action.match:
            if public_tip.height > private_tip.height:
                raise ActionException("private tip_height={} must >= then public tip_height={} -"
                                      " match not possible".format(public_tip.height, private_tip.height))

            private_block = private_tip
            while private_block.height > public_tip.height:
                private_block = private_block.prevBlock

            blocks_to_transfer.extend(get_blocks_transfer_unallowed(private_block))
            blocks_to_transfer.extend(get_blocks_transfer_unallowed(public_tip))

        elif action is Action.override:
            if public_tip.height >= private_tip.height:
                raise ActionException("private tip_height={} must > then public tip_height={} -"
                                      " override not possible".format(public_tip.height, private_tip.height))

            private_block = private_tip
            while private_block.height > public_tip.height + 1:
                private_block = private_block.prevBlock

            blocks_to_transfer.extend(get_blocks_transfer_unallowed(private_block))
            blocks_to_transfer.extend(get_blocks_transfer_unallowed(public_tip))

        elif action is Action.adopt:
            if private_tip.height >= public_tip.height:
                raise ActionException("public tip_height={} must > then private tip_height={} -"
                                      " adopt not possible".format(public_tip.height, private_tip.height))
            blocks_to_transfer.extend(get_blocks_transfer_unallowed(public_tip))

        logging.info('there are {} block to be send'.format(len(blocks_to_transfer)))
        if len(blocks_to_transfer) > 0:
            for block in blocks_to_transfer:
                block.transfer_allowed = True

            self.networking.send_inv(blocks_to_transfer)

        logging.info('executed action {}'.format(action))


def get_blocks_transfer_unallowed(block):
    blocks = []
    while block.transfer_allowed is False:
        blocks.append(block)

        block = block.prevBlock
    return blocks
