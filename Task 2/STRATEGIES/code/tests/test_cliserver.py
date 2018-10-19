import unittest
from mock import MagicMock
from mock import patch
from cliserver import Functions
from bitcoin import core
from chain import Block
from chain import BlockOrigin


class CliServerTest(unittest.TestCase):

    def __init__(self, *args, **kwargs):
        super(CliServerTest, self).__init__(*args, **kwargs)
        self.sync = None
        self.chain = None
        self.functions = None

    def setUp(self):
        super(CliServerTest, self).setUp()
        self.sync = MagicMock()
        self.chain = MagicMock()
        self.functions = Functions(self.chain, self.sync)

    @patch('chainutil.get_highest_block_with_cblock')
    def test_get_best_public_block_hash(self, mock):
        mock.return_value = Block(core.CoreRegTestParams.GENESIS_BLOCK, BlockOrigin.public)

        self.functions.get_best_public_block_hash()

