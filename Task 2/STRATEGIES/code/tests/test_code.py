import unittest
from strategy.code import Strategy
from strategy import Action
from strategy import BlockOrigin
from strategy import ActionException


class StrategyTest(unittest.TestCase):

    def test_same_length_block_origin_public(self):
        strategy = Strategy()

        action = strategy.find_action(0, 1, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_same_length_block_origin_private(self):
        strategy = Strategy()

        action = strategy.find_action(1, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_match_block_origin_public(self):
        strategy = Strategy()
        strategy.active = True

        action = strategy.find_action(0, 1, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_match_block_origin_private(self):
        strategy = Strategy()
        strategy.active = True

        action = strategy.find_action(2, 1, BlockOrigin.private)
        self.assertEqual(action, Action.override)

    def test_match_stubborn_block_origin_public(self):
        strategy = Strategy(equal_fork_stubborn=True)
        strategy.active = True

        action = strategy.find_action(0, 1, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_match_stubborn_block_origin_private(self):
        strategy = Strategy(equal_fork_stubborn=True)
        strategy.active = True

        action = strategy.find_action(1, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_match_block_both_chain_same_length_not_active_origin_public(self):
        strategy = Strategy()

        action = strategy.find_action(2, 3, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_match_block_both_chain_same_length_trail_stubborn_not_active_origin_public(self):
        strategy = Strategy(trail_stubborn=1)

        action = strategy.find_action(2, 3, BlockOrigin.public)
        self.assertEqual(action, Action.wait)

    def test_match_block_both_chain_same_length_not_active_origin_private(self):
        strategy = Strategy()

        action = strategy.find_action(3, 2, BlockOrigin.private)
        self.assertEqual(action, Action.override)

    def test_match_block_both_chain_same_length_trail_stubborn_not_active_origin_private(self):
        strategy = Strategy(trail_stubborn=1)

        action = strategy.find_action(3, 2, BlockOrigin.private)
        self.assertEqual(action, Action.override)

    def test_private_lead1_block_origin_public(self):
        strategy = Strategy()

        action = strategy.find_action(1, 1, BlockOrigin.public)
        self.assertEqual(action, Action.match)
        self.assertEqual(strategy.active, True)

    def test_private_lead1_block_origin_private(self):
        strategy = Strategy()

        action = strategy.find_action(2, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_private_lead2_block_origin_public(self):
        strategy = Strategy()

        action = strategy.find_action(2, 1, BlockOrigin.public)
        self.assertEqual(action, Action.override)

    def test_private_lead2_block_origin_private(self):
        strategy = Strategy()

        action = strategy.find_action(3, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_high_private_lead_block_origin_public(self):
        strategy = Strategy()

        action = strategy.find_action(3, 1, BlockOrigin.public)
        self.assertEqual(action, Action.wait)

    def test_high_private_lead_block_origin_private(self):
        strategy = Strategy()

        action = strategy.find_action(4, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_public_lead1_block_origin_public(self):
        strategy = Strategy()

        action = strategy.find_action(0, 2, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_stubborn_private_lead2_block_origin_public(self):
        strategy = Strategy(lead_stubborn=True)

        action = strategy.find_action(2, 1, BlockOrigin.public)
        self.assertEqual(action, Action.match)
        self.assertEqual(strategy.active, True)

    def test_stubborn_private_lead2_block_origin_private(self):
        strategy = Strategy(lead_stubborn=True)

        action = strategy.find_action(3, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_stubborn_high_private_lead_block_origin_public(self):
        strategy = Strategy(lead_stubborn=True)

        action = strategy.find_action(3, 1, BlockOrigin.public)
        self.assertEqual(action, Action.match)
        self.assertEqual(strategy.active, True)

    def test_stubborn_high_private_lead_block_origin_private(self):
        strategy = Strategy(lead_stubborn=True)

        action = strategy.find_action(4, 0, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_stubborn_public_lead1_block_origin_private(self):
        strategy = Strategy(trail_stubborn=1)

        action = strategy.find_action(1, 1, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_stubborn_public_lead1_block_origin_public(self):
        strategy = Strategy(trail_stubborn=1)

        action = strategy.find_action(0, 2, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_stubborn2_public_lead1_block_origin_private(self):
        strategy = Strategy(trail_stubborn=2)

        action = strategy.find_action(1, 1, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_stubborn2_public_lead1_block_origin_public(self):
        strategy = Strategy(trail_stubborn=2)

        action = strategy.find_action(0, 2, BlockOrigin.public)
        self.assertEqual(action, Action.wait)

    def test_stubborn2_public_lead2_block_origin_private(self):
        strategy = Strategy(trail_stubborn=2)

        action = strategy.find_action(1, 2, BlockOrigin.private)
        self.assertEqual(action, Action.wait)

    def test_stubborn2_public_lead2_block_origin_public(self):
        strategy = Strategy(trail_stubborn=2)

        action = strategy.find_action(0, 3, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)

    def test_stubborn_catch_up1(self):
        strategy = Strategy()

        action = strategy.find_action(2, 1, BlockOrigin.private)
        self.assertEqual(action, Action.override)

    def test_stubborn_catch_up2(self):
        strategy = Strategy()

        action = strategy.find_action(3, 2, BlockOrigin.private)
        self.assertEqual(action, Action.override)

    def test_private_lead_behind_trail_stubborn(self):
        strategy = Strategy()

        with self.assertRaisesRegexp(ActionException, "private_lead.*should not be behind trail_stubborn.*"):
            strategy.find_action(1, 1, BlockOrigin.private)

    def test_stubborn_private_lead_behind_trail_stubborn(self):
        strategy = Strategy(trail_stubborn=-2)

        with self.assertRaisesRegexp(ActionException, "private_lead.*should not be behind trail_stubborn.*"):
            strategy.find_action(1, 3, BlockOrigin.private)
