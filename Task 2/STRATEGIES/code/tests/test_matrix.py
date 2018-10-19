import unittest
from strategy.matrix import Strategy
from strategy import ActionException
from strategy import Action
from strategy import BlockOrigin
from strategy import ForkState


class StrategyTest(unittest.TestCase):

    def __init__(self, *args, **kwargs):
        super(StrategyTest, self).__init__(*args, **kwargs)

        self.strategy = None

    def setUp(self):
        self.strategy = [
            [  # irrelevant
                ['*', '*', '*'],
                ['*', '*', '*'],
                ['*', '*', '*']
            ],
            [  # relevant
                ['*', '*', '*'],
                ['*', '*', '*'],
                ['*', '*', '*']
            ],
            [  # match
                ['*', '*', '*'],
                ['*', '*', '*'],
                ['*', '*', '*']
            ]
        ]

    def test_find_action_both_height_zero(self):
        strategy = Strategy([])

        with self.assertRaisesRegexp(ActionException, "lengths can\'t be zero"):
            strategy.find_action(0, 0, None)

        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_state_unreachable(self):
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(1, 1, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)
        self.assertEqual(strategy.fork_state, ForkState.irrelevant)

    def test_find_action_state_out_of_range(self):
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(100, 100, BlockOrigin.public)
        self.assertEqual(action, Action.adopt)
        self.assertEqual(strategy.fork_state, ForkState.irrelevant)

    def test_find_action_block_origin_public_same_height(self):
        self.strategy[ForkState.relevant.value][2][2] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(2, 2, BlockOrigin.public)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_public_lead_private(self):
        self.strategy[ForkState.relevant.value][1][0] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(1, 0, BlockOrigin.public)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_public_lead_public(self):
        self.strategy[ForkState.irrelevant.value][0][1] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(0, 1, BlockOrigin.public)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_public_same_height_fork_state_active(self):
        self.strategy[ForkState.relevant.value][2][2] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.active

        action = strategy.find_action(2, 2, BlockOrigin.public)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_public_lead_private_fork_state_active(self):
        self.strategy[ForkState.relevant.value][1][0] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.active

        action = strategy.find_action(1, 0, BlockOrigin.public)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_public_lead_public_fork_state_active(self):
        strategy = Strategy([[[]]])
        strategy.fork_state = ForkState.active

        with self.assertRaisesRegexp(ActionException, ".*active.*public.*length_private < length_public"):
            strategy.find_action(0, 1, BlockOrigin.public)

        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_private_same_height(self):
        self.strategy[ForkState.irrelevant.value][2][2] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(2, 2, BlockOrigin.private)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_private_lead_private(self):
        self.strategy[ForkState.irrelevant.value][1][0] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(1, 0, BlockOrigin.private)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_private_lead_public(self):
        self.strategy[ForkState.irrelevant.value][0][1] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.irrelevant

        action = strategy.find_action(0, 1, BlockOrigin.private)

        self.assertEqual(action, Action.wait)
        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_private_same_height_fork_state_active(self):
        strategy = Strategy([[[]]])
        strategy.fork_state = ForkState.active

        with self.assertRaisesRegexp(ActionException, ".*active.*private.*length_private <= length_public"):
            strategy.find_action(2, 2, BlockOrigin.private)

        self.assertNotEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_private_lead_private_fork_state_active(self):
        self.strategy[ForkState.active.value][1][0] = 'w'
        strategy = Strategy(self.strategy)
        strategy.fork_state = ForkState.active

        action = strategy.find_action(1, 0, BlockOrigin.private)

        self.assertEqual(action, Action.wait)
        self.assertEqual(strategy.fork_state, ForkState.active)

    def test_find_action_block_origin_private_lead_public_fork_state_active(self):
        strategy = Strategy([[[]]])
        strategy.fork_state = ForkState.active

        with self.assertRaisesRegexp(ActionException, ".*active.*private.*length_private <= length_public"):
            strategy.find_action(0, 1, BlockOrigin.private)

        self.assertNotEqual(strategy.fork_state, ForkState.active)
