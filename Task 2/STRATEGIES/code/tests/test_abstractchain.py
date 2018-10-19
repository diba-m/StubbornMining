import unittest
from chain import Block
from strategy import BlockOrigin
from bitcoin.core import CBlockHeader
from bitcoin.core import CBlock
import test_util


class AbstractChainTest(unittest.TestCase):

    def __init__(self, *args, **kwargs):
        super(AbstractChainTest, self).__init__(*args, **kwargs)

        self.first_block_chain_a = None
        self.second_block_chain_a = None
        self.third_a_block_chain_a = None
        self.third_b_block_chain_a = None
        self.fourth_block_chain_a = None
        self.first_block_chain_b = None
        self.second_block_chain_b = None
        self.third_a_block_chain_b = None
        self.third_b_block_chain_b = None
        self.fourth_block_chain_b = None

    def setUp(self):
        self.first_block_chain_a = Block(None, BlockOrigin.private)
        self.first_block_chain_a.height = 1
        self.first_block_chain_a.prevBlock = test_util.genesis_block
        self.first_block_chain_a.cached_hash = '1a'
        self.first_block_chain_a.cblock_header = CBlockHeader(nNonce=11)
        self.first_block_chain_a.cblock = CBlock(nNonce=11)

        self.second_block_chain_a = Block(None, BlockOrigin.private)
        self.second_block_chain_a.height = 2
        self.second_block_chain_a.prevBlock = self.first_block_chain_a
        self.second_block_chain_a.cached_hash = '2a'
        self.second_block_chain_a.cblock_header = CBlockHeader(nNonce=21)
        self.second_block_chain_a.cblock = CBlock(nNonce=21)

        self.third_a_block_chain_a = Block(None, BlockOrigin.private)
        self.third_a_block_chain_a.height = 3
        self.third_a_block_chain_a.prevBlock = self.second_block_chain_a
        self.third_a_block_chain_a.cached_hash = '3a_1'
        self.third_a_block_chain_a.cblock_header = CBlockHeader(nNonce=311)
        self.third_a_block_chain_a.cblock = CBlock(nNonce=311)

        self.third_b_block_chain_a = Block(None, BlockOrigin.private)
        self.third_b_block_chain_a.height = 3
        self.third_b_block_chain_a.prevBlock = self.second_block_chain_a
        self.third_b_block_chain_a.cached_hash = '3a_2'
        self.third_b_block_chain_a.cblock_header = CBlockHeader(nNonce=312)
        self.third_b_block_chain_a.cblock = CBlock(nNonce=312)

        self.fourth_block_chain_a = Block(None, BlockOrigin.private)
        self.fourth_block_chain_a.height = 4
        self.fourth_block_chain_a.prevBlock = self.third_a_block_chain_a
        self.fourth_block_chain_a.cached_hash = '4a'
        self.fourth_block_chain_a.cblock_header = CBlockHeader(nNonce=41)
        self.fourth_block_chain_a.cblock = CBlock(nNonce=41)

        self.first_block_chain_b = Block(None, BlockOrigin.public)
        self.first_block_chain_b.height = 1
        self.first_block_chain_b.prevBlock = test_util.genesis_block
        self.first_block_chain_b.cached_hash = '1b'
        self.first_block_chain_b.cblock_header = CBlockHeader(nNonce=12)
        self.first_block_chain_b.cblock = CBlock(nNonce=12)

        self.second_block_chain_b = Block(None, BlockOrigin.public)
        self.second_block_chain_b.height = 2
        self.second_block_chain_b.prevBlock = self.first_block_chain_b
        self.second_block_chain_b.cached_hash = '2b'
        self.second_block_chain_b.cblock_header = CBlockHeader(nNonce=22)
        self.second_block_chain_b.cblock = CBlock(nNonce=22)

        self.third_a_block_chain_b = Block(None, BlockOrigin.public)
        self.third_a_block_chain_b.height = 3
        self.third_a_block_chain_b.prevBlock = self.second_block_chain_b
        self.third_a_block_chain_b.cached_hash = '3b_1'
        self.third_a_block_chain_b.cblock_header = CBlockHeader(nNonce=321)
        self.third_a_block_chain_b.cblock = CBlock(nNonce=321)

        self.third_b_block_chain_b = Block(None, BlockOrigin.public)
        self.third_b_block_chain_b.height = 3
        self.third_b_block_chain_b.prevBlock = self.second_block_chain_b
        self.third_b_block_chain_b.cached_hash = '3b_2'
        self.third_b_block_chain_b.cblock_header = CBlockHeader(nNonce=322)
        self.third_b_block_chain_b.cblock = CBlock(nNonce=322)

        self.fourth_block_chain_b = Block(None, BlockOrigin.public)
        self.fourth_block_chain_b.height = 4
        self.fourth_block_chain_b.prevBlock = self.third_a_block_chain_b
        self.fourth_block_chain_b.cached_hash = '4b'
        self.fourth_block_chain_b.cblock_header = CBlockHeader(nNonce=42)
        self.fourth_block_chain_b.cblock = CBlock(nNonce=42)
