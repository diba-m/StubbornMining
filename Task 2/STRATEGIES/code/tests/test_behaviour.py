from mock import MagicMock
from behaviour import CatchUpBehaviour
import unittest
from mock import patch


class BehaviourTest(unittest.TestCase):

    def __init__(self, *args, **kwargs):
        super(BehaviourTest, self).__init__(*args, **kwargs)
        self.client = None
        self.chain = None
        self.ip = None
        self.behaviour = None

    def setUp(self):
        self.client = MagicMock()
        self.chain = MagicMock()
        self.ip = '127.0.0.1'
        self.behaviour = CatchUpBehaviour(self.client, self.ip, self.chain)

    @patch('chainutil.request_get_headers', lambda chain, origin: ['hash1'])
    def test_on_version_incoming_true(self):
        super(BehaviourTest, self).setUp()
        connection = MagicMock()
        connection.host = (self.ip, '1234')
        connection.incoming = True

        self.behaviour.on_version(connection, None)

        self.assertTrue(connection.send.call_count, 3)
        self.assertEqual(connection.send.call_args_list[-1][0][0], 'getheaders')

    @patch('chainutil.request_get_headers', lambda chain, origin: ['hash1'])
    def test_on_version_incoming_false(self):
        super(BehaviourTest, self).setUp()
        connection = MagicMock()
        connection.host = (self.ip, '1234')
        connection.incoming = False

        self.behaviour.on_version(connection, None)

        self.assertTrue(connection.send.call_count, 2)
