import test_abstractchain
import chainutil
from strategy import BlockOrigin
from mock import patch
from mock import MagicMock
import test_util
from chain import Block
from bitcoin.core import CBlock


class ChainUtilTest(test_abstractchain.AbstractChainTest):

    def __init__(self, *args, **kwargs):
        super(ChainUtilTest, self).__init__(*args, **kwargs)

    def test_get_private_public_fork_no_private_tip(self):
        fork = chainutil.get_private_public_fork([self.second_block_chain_b])
        self.assertEqual(fork.private_height, 0)
        self.assertEqual(fork.private_tip.hash(), test_util.genesis_hash)

        self.assertEqual(fork.public_height, 2)
        self.assertEqual(fork.public_tip.hash(), '2b')

    def test_get_private_public_fork_no_public_tip(self):
        fork = chainutil.get_private_public_fork([self.second_block_chain_a])
        self.assertEqual(fork.public_height, 0)
        self.assertEqual(fork.public_tip.hash(), test_util.genesis_hash)

        self.assertEqual(fork.private_height, 2)
        self.assertEqual(fork.private_tip.hash(), '2a')

    def test_get_private_public_fork_lead_public(self):
        fork = chainutil.get_private_public_fork([self.second_block_chain_b, self.first_block_chain_a])
        self.assertEqual(fork.private_height, 1)
        self.assertEqual(fork.private_tip.hash(), '1a')

        self.assertEqual(fork.public_height, 2)
        self.assertEqual(fork.public_tip.hash(), '2b')

    def test_get_private_public_fork_lead_private(self):
        fork = chainutil.get_private_public_fork([self.first_block_chain_b, self.second_block_chain_a])

        self.assertEqual(fork.private_height, 2)
        self.assertEqual(fork.private_tip.hash(), '2a')

        self.assertEqual(fork.public_height, 1)
        self.assertEqual(fork.public_tip.hash(), '1b')

    def test_get_private_public_fork_private_transferred(self):
        self.first_block_chain_a.transfer_allowed = True
        self.second_block_chain_a.transfer_allowed = True
        fork = chainutil.get_private_public_fork([self.first_block_chain_b, self.second_block_chain_a])

        self.assertEqual(fork.private_height, 0)
        self.assertEqual(fork.private_tip.hash(), '2a')

        self.assertEqual(fork.public_height, 0)
        self.assertEqual(fork.public_tip.hash(), '2a')

    def test_get_private_public_fork_one_private_transferred(self):
        self.first_block_chain_a.transfer_allowed = True
        fork = chainutil.get_private_public_fork([self.second_block_chain_a, self.first_block_chain_b])

        self.assertEqual(fork.private_height, 2)
        self.assertEqual(fork.private_tip.hash(), '2a')

        self.assertEqual(fork.public_height, 1)

    def test_get_private_public_fork_private_fork_point(self):
        fork = chainutil.get_private_public_fork(
            [self.fourth_block_chain_a, self.third_b_block_chain_a, self.second_block_chain_b])

        self.assertEqual(fork.private_height, 4)
        self.assertEqual(fork.private_tip.hash(), '4a')

        self.assertEqual(fork.public_height, 2)
        self.assertEqual(fork.public_tip.hash(), '2b')

    def test_get_private_public_fork_half_private_fork_transferred(self):
        self.first_block_chain_a.transfer_allowed = True
        self.second_block_chain_a.transfer_allowed = True
        self.third_b_block_chain_a.transfer_allowed = True

        fork = chainutil.get_private_public_fork(
            [self.second_block_chain_b, self.fourth_block_chain_a, self.third_b_block_chain_a])

        self.assertEqual(fork.private_height, 2)
        self.assertEqual(fork.private_tip.hash(), '4a')

        self.assertEqual(fork.public_height, 1)
        self.assertEqual(fork.public_tip.hash(), '3a_2')

    def test_get_private_public_fork_longest_private_fork_transferred(self):
        self.first_block_chain_a.transfer_allowed = True
        self.second_block_chain_a.transfer_allowed = True
        self.third_a_block_chain_a.transfer_allowed = True
        self.fourth_block_chain_a.transfer_allowed = True

        fork = chainutil.get_private_public_fork(
            [self.second_block_chain_b, self.fourth_block_chain_a, self.third_b_block_chain_a])
        self.assertEqual(fork.private_height, 0)
        self.assertEqual(fork.private_tip.hash(), '4a')

        self.assertEqual(fork.public_height, 0)
        self.assertEqual(fork.public_tip.hash(), '4a')

    def test_get_private_public_fork_public_fork_point(self):
        fork = chainutil.get_private_public_fork(
            [self.fourth_block_chain_b, self.third_b_block_chain_b, self.second_block_chain_a])
        self.assertEqual(fork.private_height, 2)
        self.assertEqual(fork.private_tip.hash(), '2a')

        self.assertEqual(fork.public_height, 4)
        self.assertEqual(fork.public_tip.hash(), '4b')

    def test_get_private_public_fork_after_match(self):
        self.first_block_chain_a.transfer_allowed = True
        self.first_block_chain_b.transfer_allowed = True
        fork = chainutil.get_private_public_fork([self.first_block_chain_a, self.second_block_chain_b])

        self.assertEqual(fork.private_height, 1)
        self.assertEqual(fork.private_tip.hash(), '1a')

        self.assertEqual(fork.public_height, 2)
        self.assertEqual(fork.public_tip.hash(), '2b')

    def test_get_highest_block_genesis_block(self):
        tip = chainutil.get_highest_block([test_util.genesis_block], BlockOrigin.private)

        self.assertEqual(tip, test_util.genesis_block)

    def test_get_highest_block_same_origin(self):
        tip = chainutil.get_highest_block([self.first_block_chain_a], BlockOrigin.private)

        self.assertEqual(tip, self.first_block_chain_a)

    def test_get_highest_block_different_origin(self):
        tip = chainutil.get_highest_block([self.first_block_chain_a], BlockOrigin.public)

        self.assertEqual(tip, test_util.genesis_block)

    def test_get_highest_block_different_origin_transfer_allowed(self):
        self.first_block_chain_a.transfer_allowed = True
        tip = chainutil.get_highest_block([self.first_block_chain_a], BlockOrigin.public)

        self.assertEqual(tip, self.first_block_chain_a)

    def test_get_highest_block_public_tip_lower_then_transferable_public_block(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.third_a_block_chain_a, self.first_block_chain_b], BlockOrigin.public)

        self.assertEqual(tip, self.second_block_chain_a)

    def test_get_highest_block_public_tip_same_height_then_transferable_public_block(self):
        self.first_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.second_block_chain_a, self.first_block_chain_b], BlockOrigin.public)

        self.assertEqual(tip, self.first_block_chain_b)

    def test_get_highest_block_tips_same_height_override_private(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.second_block_chain_a, self.second_block_chain_b],
                                          BlockOrigin.public, BlockOrigin.private)

        self.assertEqual(tip, self.second_block_chain_a)

    def test_get_highest_block_lead_private_override_private(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.third_a_block_chain_a, self.second_block_chain_b],
                                          BlockOrigin.public, BlockOrigin.private)

        self.assertEqual(tip, self.second_block_chain_a)

    def test_get_highest_block_lead_public_override_private(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.third_a_block_chain_a, self.third_a_block_chain_b],
                                          BlockOrigin.public, BlockOrigin.private)

        self.assertEqual(tip, self.third_a_block_chain_b)

    def test_get_highest_block_tips_same_height_override_public(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.second_block_chain_a, self.second_block_chain_b],
                                          BlockOrigin.public, BlockOrigin.public)

        self.assertEqual(tip, self.second_block_chain_b)

    def test_get_highest_block_lead_private_override_public(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.third_a_block_chain_a, self.second_block_chain_b],
                                          BlockOrigin.public, BlockOrigin.public)

        self.assertEqual(tip, self.second_block_chain_b)

    def test_get_highest_block_lead_public_override_public(self):
        self.second_block_chain_a.transfer_allowed = True

        tip = chainutil.get_highest_block([self.third_a_block_chain_a, self.third_a_block_chain_b],
                                          BlockOrigin.public, BlockOrigin.public)

        self.assertEqual(tip, self.third_a_block_chain_b)

    def test_get_tips_for_block_origin_with_transfer_allowed(self):
        self.third_a_block_chain_b.transfer_allowed = True

        tips = chainutil.get_tips_for_block_origin([self.third_a_block_chain_b], BlockOrigin.private)

        self.assertEqual(len(tips), 1)
        self.assertEqual(tips[0], self.third_a_block_chain_b)

    def test_get_tips_for_block_origin_with_transfer_allowed_of_parent(self):
        self.second_block_chain_b.transfer_allowed = True

        tips = chainutil.get_tips_for_block_origin([self.third_a_block_chain_b], BlockOrigin.private)

        self.assertEqual(len(tips), 1)
        self.assertEqual(tips[0], self.second_block_chain_b)

    def test_get_tips_for_block_origin_with_same_origin(self):
        tips = chainutil.get_tips_for_block_origin([self.third_a_block_chain_a], BlockOrigin.private)

        self.assertEqual(len(tips), 1)
        self.assertEqual(tips[0], self.third_a_block_chain_a)

    def test_get_tips_for_block_origin_with_two_same_tips(self):
        self.second_block_chain_a.transfer_allowed = True

        self.second_block_chain_b.prevBlock = self.first_block_chain_a
        tips = chainutil.get_tips_for_block_origin([self.third_a_block_chain_a, self.third_b_block_chain_a], BlockOrigin.public)

        self.assertEqual(len(tips), 1)
        self.assertEqual(tips[0], self.second_block_chain_a)

    def test_get_tips_for_block_origin_with_two_tips(self):
        self.second_block_chain_a.transfer_allowed = True

        self.second_block_chain_b.prevBlock = self.first_block_chain_a
        tips = chainutil.get_tips_for_block_origin([self.second_block_chain_a, self.second_block_chain_b], BlockOrigin.public)

        self.assertEqual(len(tips), 2)
        self.assertTrue(self.second_block_chain_a in tips)
        self.assertTrue(self.second_block_chain_b in tips)

    @patch('chainutil.get_highest_block')
    def test_get_headers_with_genesis_block(self, mock):
        mock.return_value = test_util.genesis_block

        blocks = chainutil.respond_get_headers([], None, [], 0)

        self.assertEqual(len(blocks), 0)

    @patch('chainutil.get_highest_block')
    def test_get_headers_empty_until(self, mock):
        mock.return_value = self.second_block_chain_b

        blocks = chainutil.respond_get_headers([], None, [], 0)

        self.assertEqual(len(blocks), 2)
        self.assertIn(self.first_block_chain_b, blocks)
        self.assertIn(self.second_block_chain_b, blocks)

    @patch('chainutil.get_highest_block')
    def test_get_headers_with_cblock_missing(self, mock):
        self.second_block_chain_b.cblock = None
        mock.return_value = self.second_block_chain_b

        blocks = chainutil.respond_get_headers([], None, [], 0)

        self.assertEqual(len(blocks), 1)
        self.assertIn(self.first_block_chain_b, blocks)

    @patch('chainutil.get_highest_block')
    def test_get_headers_with_cblock_missing_in_the_middle(self, mock):
        self.second_block_chain_b.cblock = None
        mock.return_value = self.third_a_block_chain_b

        blocks = chainutil.respond_get_headers([], None, [], 0)

        self.assertEqual(len(blocks), 1)
        self.assertIn(self.first_block_chain_b, blocks)

    @patch('chainutil.get_highest_block')
    def test_get_headers_with_until(self, mock):
        mock.return_value = self.second_block_chain_a

        blocks = chainutil.respond_get_headers([], None, [test_util.genesis_block.hash()], 0)

        self.assertEqual(len(blocks), 2)
        self.assertIn(self.first_block_chain_a, blocks)
        self.assertIn(self.second_block_chain_a, blocks)

    @patch('chainutil.get_highest_block')
    def test_get_headers_until_like_tip(self, mock):
        mock.return_value = self.third_a_block_chain_b

        blocks = chainutil.respond_get_headers([], None, [self.third_a_block_chain_b.hash()], 0)

        self.assertEqual(len(blocks), 0)

    @patch('chainutil.get_highest_block')
    def test_get_highest_block_with_cblock_cblock_not_present(self, mock):
        mock.return_value = self.first_block_chain_a
        self.first_block_chain_a.cblock = None

        block = chainutil.get_highest_block_with_cblock([], None)

        self.assertEqual(block, test_util.genesis_block)

    @patch('chainutil.get_highest_block')
    def test_get_highest_block_with_cblock(self, mock):
        mock.return_value = self.second_block_chain_a

        block = chainutil.get_highest_block_with_cblock([], None)

        self.assertEqual(block, self.second_block_chain_a)

    @patch('chainutil.get_highest_block_with_cblock')
    def test_request_get_headers_with_3_headers(self, mock):
        mock.return_value = self.fourth_block_chain_b

        headers = chainutil.request_get_headers([MagicMock()], BlockOrigin.public)

        self.assertEqual(len(headers), 3)
        self.assertEqual(headers[0], self.fourth_block_chain_b.hash())
        self.assertEqual(headers[1], self.third_a_block_chain_b.hash())
        self.assertEqual(headers[2], self.first_block_chain_b.hash())

    @patch('chainutil.get_highest_block_with_cblock')
    def test_request_get_headers_including_genesis_block(self, mock):
        mock.return_value = self.first_block_chain_b

        headers = chainutil.request_get_headers([MagicMock()], BlockOrigin.public)

        self.assertEqual(len(headers), 2)
        self.assertEqual(headers[0], self.first_block_chain_b.hash())
        self.assertEqual(headers[1], test_util.genesis_hash)

    @patch('chainutil.get_highest_block_with_cblock')
    def test_request_get_headers_very_long_chain(self, mock):
        first_block = Block(CBlock(), BlockOrigin.public)
        first_block.prevBlock = None
        first_block.cached_hash = '0'

        tmp = first_block
        for i in range(1, 17):
            block = Block(CBlock(), BlockOrigin.public)
            block.prevBlock = tmp
            block.cached_hash = str(i)

            tmp = block

        mock.return_value = tmp

        headers = chainutil.request_get_headers([MagicMock()], BlockOrigin.public)

        self.assertEqual(len(headers), 5)
        self.assertEqual(headers[0], '16')
        self.assertEqual(headers[1], '15')
        self.assertEqual(headers[2], '13')
        self.assertEqual(headers[3], '9')
        self.assertEqual(headers[4], '1')
