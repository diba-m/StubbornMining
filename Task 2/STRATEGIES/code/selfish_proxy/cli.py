import xmlrpclib
import argparse

server = xmlrpclib.ServerProxy('http://localhost:8000')


def get_best_public_block_hash():
    print(server.get_best_public_block_hash())


def get_start_hash():
    print(server.get_start_hash())

FUNCTION_MAP = {
    'get_best_public_block_hash': get_best_public_block_hash,
    'get_start_hash': get_start_hash,
}

parser = argparse.ArgumentParser(description='Execute cli commands against Selfish Mining Proxy.')
parser.add_argument('command', choices=FUNCTION_MAP.keys())

args = parser.parse_args()
func = FUNCTION_MAP[args.command]
func()
