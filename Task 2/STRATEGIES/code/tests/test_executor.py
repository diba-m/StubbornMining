import unittest
from mock import MagicMock
from chain import Block
import test_util
from strategy import BlockOrigin, Action, ActionException
from strategy.executor import Executor
from bitcoin.core import CBlock


class ExecutorTest(unittest.TestCase):

    def __init__(self, *args, **kwargs):
        super(ExecutorTest, self).__init__(*args, **kwargs)

        self.executor = None
        self.networking = None

        self.first_block_chain_a = None
        self.second_block_chain_a = None
        self.first_block_chain_b = None
        self.second_block_chain_b = None

    def setUp(self):
        self.networking = MagicMock()
        self.executor = Executor(self.networking)

        self.first_block_chain_b = Block(CBlock(), BlockOrigin.public)
        self.first_block_chain_b.height = 1
        self.first_block_chain_b.prevBlock = test_util.genesis_block
        self.first_block_chain_b.cached_hash = '1b'

        self.second_block_chain_b = Block(CBlock(), BlockOrigin.public)
        self.second_block_chain_b.height = 2
        self.second_block_chain_b.prevBlock = self.first_block_chain_b
        self.second_block_chain_b.cached_hash = '2b'

        self.first_block_chain_a = Block(CBlock(), BlockOrigin.private)
        self.first_block_chain_a.height = 1
        self.first_block_chain_a.prevBlock = test_util.genesis_block
        self.first_block_chain_a.cached_hash = '1a'

        self.second_block_chain_a = Block(CBlock(), BlockOrigin.private)
        self.second_block_chain_a.height = 2
        self.second_block_chain_a.prevBlock = self.first_block_chain_a
        self.second_block_chain_a.cached_hash = '2a'

    def test_match_same_height(self):
        self.executor.execute(Action.match, self.first_block_chain_a, self.first_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)

        blocks = [block.hash() for block in self.networking.send_inv.call_args[0][0]]

        self.assertEqual(len(blocks), 2)
        self.assertTrue('1a' in blocks)
        self.assertTrue('1b' in blocks)

    def test_match_lead_private(self):
        self.executor.execute(Action.match, self.second_block_chain_a, self.first_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)

        blocks = [block.hash() for block in self.networking.send_inv.call_args[0][0]]

        self.assertEqual(len(blocks), 2)
        self.assertTrue('1a' in blocks)
        self.assertTrue('1b' in blocks)

    def test_match_lead_public(self):
        private_tip = Block(CBlock(), None)
        private_tip.height = 1

        public_tip = Block(CBlock(), None)
        public_tip.height = 2

        with self.assertRaisesRegexp(ActionException, "private tip.*must >= then public tip.*match.*"):
            self.executor.execute(Action.match, private_tip, public_tip)

    def test_override_lead_public(self):
        private_tip = Block(CBlock(), None)
        private_tip.height = 1

        public_tip = Block(CBlock(), None)
        public_tip.height = 2

        with self.assertRaisesRegexp(ActionException, "private tip.*must > then public tip.*override.*"):
            self.executor.execute(Action.override, private_tip, public_tip)

    def test_override_same_height(self):
        private_tip = Block(CBlock(), None)
        private_tip.height = 2

        public_tip = Block(CBlock(), None)
        public_tip.height = 2

        with self.assertRaisesRegexp(ActionException, "private tip.*must > then public tip.*override.*"):
            self.executor.execute(Action.override, private_tip, public_tip)

    def test_override_lead_private(self):
        self.executor.execute(Action.override, self.second_block_chain_a, self.first_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)

        blocks = [block.hash() for block in self.networking.send_inv.call_args[0][0]]

        self.assertEqual(len(blocks), 3)
        self.assertTrue('1a' in blocks)
        self.assertTrue('2a' in blocks)
        self.assertTrue('1b' in blocks)

    def test_override_two_blocks_lead_private(self):
        third_block_chain_a = Block(CBlock(), BlockOrigin.private)
        third_block_chain_a.height = 3
        third_block_chain_a.prevBlock = self.second_block_chain_a
        third_block_chain_a.cached_hash = '3a'

        self.executor.execute(Action.override, third_block_chain_a, self.first_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)

        blocks = [block.hash() for block in self.networking.send_inv.call_args[0][0]]

        self.assertEqual(len(blocks), 3)
        self.assertTrue('1a' in blocks)
        self.assertTrue('2a' in blocks)
        self.assertTrue('1b' in blocks)

    def test_adopt_private_lead(self):
        private_tip = Block(CBlock(), None)
        private_tip.height = 3

        public_tip = Block(CBlock(), None)
        public_tip.height = 2

        with self.assertRaisesRegexp(ActionException, "public tip.*must > then private tip.*adopt.*"):
            self.executor.execute(Action.adopt, private_tip, public_tip)

    def test_adopt_same_height(self):
        private_tip = Block(CBlock(), None)
        private_tip.height = 2

        public_tip = Block(CBlock(), None)
        public_tip.height = 2

        with self.assertRaisesRegexp(ActionException, "public tip.*must > then private tip.*adopt.*"):
            self.executor.execute(Action.adopt, private_tip, public_tip)

    def test_adopt_lead_public(self):
        self.executor.execute(Action.adopt, self.first_block_chain_a, self.second_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)

        blocks = [block.hash() for block in self.networking.send_inv.call_args[0][0]]

        self.assertEqual(len(blocks), 2)
        self.assertTrue('1b' in blocks)
        self.assertTrue('2b' in blocks)

    def test_adopt_two_blocks_lead_public(self):
        third_block_chain_b = Block(CBlock(), BlockOrigin.public)
        third_block_chain_b.height = 3
        third_block_chain_b.prevBlock = self.second_block_chain_b
        third_block_chain_b.cached_hash = '3b'

        self.executor.execute(Action.adopt, self.first_block_chain_a, third_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)

        blocks = [block.hash() for block in self.networking.send_inv.call_args[0][0]]

        self.assertEqual(len(blocks), 3)
        self.assertTrue('1b' in blocks)
        self.assertTrue('2b' in blocks)
        self.assertTrue('3b' in blocks)

    def test_execute_action_check_if_transfer_allowed_is_set(self):
        self.executor.execute(Action.match, self.first_block_chain_a, self.first_block_chain_b)

        self.assertTrue(self.networking.send_inv.called)
        self.assertEqual(len(self.networking.send_inv.call_args[0][0]), 2)
        for block in self.networking.send_inv.call_args[0][0]:
            self.assertTrue(block.transfer_allowed)
