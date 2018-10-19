from bitcoin import core
from strategy import BlockOrigin
from strategy import ActionException
import logging
import chainutil


class Chain:
    def __init__(self, executor, strategy, start_hash=None):
        self.executor = executor
        self.strategy = strategy

        if start_hash:
            self.start_hash = start_hash
            self.initializing = True
        else:
            self.initializing = False

        block = Block(core.CoreRegTestParams.GENESIS_BLOCK, BlockOrigin.public)
        block.transfer_allowed = True
        block.height = 0
        block.cblock = core.CoreRegTestParams.GENESIS_BLOCK

        self.blocks = {core.CoreRegTestParams.GENESIS_BLOCK.GetHash(): block}
        self.tips = [block]
        self.orphan_blocks = []

        logging.info('created chain with start_hash={}'.format(core.b2lx(start_hash)))

    def process_header(self, header, block_origin):
        hash_ = header.GetHash()
        logging.info('process Block(hash={}) from {}'.format(core.b2lx(hash_), block_origin))

        fork_before = chainutil.get_private_public_fork(self.tips)
        logging.info('fork before {}'.format(fork_before))

        self.try_to_insert_header(header, block_origin)

        fork_after = chainutil.get_private_public_fork(self.tips)
        logging.info('fork after {}'.format(fork_after))

        if self.initializing:
            if hash_ == self.start_hash or header.hashPrevBlock == self.start_hash:
                self.initializing = False
                logging.info('Initializing over; now starting selfish mining')
            else:
                logging.info('chain is initializing - no action needs to be taken')
        else:
            if fork_before != fork_after:
                logging.debug('fork tip_private={}'.format(core.b2lx(fork_after.private_tip.hash())))
                logging.debug('fork tip_public={}'.format(core.b2lx(fork_after.public_tip.hash())))
                try:
                    action = self.strategy.find_action(fork_after.private_height, fork_after.public_height, block_origin)
                    logging.info('found action={}'.format(action))

                    self.executor.execute(action, fork_after.private_tip, fork_after.public_tip)
                except ActionException as exception:
                    logging.warn(exception.message)
            else:
                logging.debug('the two forks are the same - no action needs to be taken')

    def try_to_insert_header(self, header, block_origin):
        prevBlock = None
        for tip in self.tips:
            if tip.hash() == header.hashPrevBlock:
                prevBlock = tip
                break
        if prevBlock is None:
            for block in self.blocks.values():
                if block.hash() == header.hashPrevBlock:
                    prevBlock = block
                    break

        block = Block(header, block_origin)
        self.blocks[block.hash()] = block

        if prevBlock is None:
            self.orphan_blocks.append(block)
            logging.info('{} with prevBlock={} added to orphan blocks'.format(block, core.b2lx(block.hashPrevBlock())))
        else:
            self.insert_block(prevBlock, block)

            inserted = True
            while inserted:

                inserted_orphan_blocks = []
                for orphan_block in self.orphan_blocks:
                    if block.hash() == orphan_block.hashPrevBlock():
                        self.insert_block(block, orphan_block)
                        inserted_orphan_blocks.append(orphan_block)
                        block = orphan_block

                if len(inserted_orphan_blocks) == 0:
                    inserted = False

                for inserted_orphan_block in inserted_orphan_blocks:
                    if inserted_orphan_block in self.orphan_blocks:
                        self.orphan_blocks.remove(inserted_orphan_block)

    def insert_block(self, prevBlock, block):
        if prevBlock in self.tips:
            self.tips.remove(prevBlock)
        self.tips.append(block)
        block.height = prevBlock.height + 1
        block.prevBlock = prevBlock

        if self.initializing:
            block.transfer_allowed = True

        logging.info('{} inserted into chain'.format(block))


class Block(object):
    __slots__ = ['cblock_header', 'prevBlock', 'cblock', 'height', 'block_origin', 'transfer_allowed', 'cached_hash']

    def __init__(self, cblock_header, block_origin):
        self.cblock_header = cblock_header
        self.prevBlock = None
        self.cblock = None
        self.height = 0
        self.block_origin = block_origin
        self.transfer_allowed = False
        self.cached_hash = None

    def __repr__(self):
        return '{}(hash={} height={} block_origin={})'\
            .format(self.__class__.__name__, core.b2lx(self.hash()), self.height, self.block_origin)

    def hash_repr(self):
        return 'Block(hash={})' \
            .format(core.b2lx(self.hash()))

    def hash(self):
        if self.cached_hash:
            return self.cached_hash
        else:
            self.cached_hash = self.cblock_header.GetHash()
            return self.cached_hash

    def hashPrevBlock(self):
        return self.cblock_header.hashPrevBlock

    def __eq__(self, other):
        return self.hash() == other.hash()

    def __ne__(self, other):
        return self.hash() != other.hash()

    def __hash__(self):
        return hash(self.hash())
