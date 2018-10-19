from strategy import BlockOrigin
import chain
from sets import Set
import logging
from bitcoin import core


def get_private_public_fork(tips):
    highest_private = get_highest_block(tips, BlockOrigin.private)
    highest_public = get_highest_block(tips, BlockOrigin.public)

    high_tip, low_tip = (highest_private, highest_public) \
        if highest_private.height > highest_public.height else (highest_public, highest_private)

    while high_tip.height > low_tip.height:
        high_tip = high_tip.prevBlock

    while high_tip is not low_tip:
        high_tip = high_tip.prevBlock
        low_tip = low_tip.prevBlock

    fork_height = high_tip.height
    return Fork(highest_private, highest_private.height - fork_height,
                highest_public, highest_public.height - fork_height)


def get_highest_block(tips, block_origin, override_block_origin=None):
    if not override_block_origin:
        override_block_origin = block_origin

    highest_block = chain.Block(core.CoreRegTestParams.GENESIS_BLOCK, block_origin)
    highest_block.height = -1

    for tip in get_tips_for_block_origin(tips, block_origin):
        if tip.block_origin is override_block_origin:
            if highest_block.height <= tip.height:
                highest_block = tip
        else:
            if highest_block.height < tip.height:
                highest_block = tip
    return highest_block


def get_highest_block_with_cblock(tips, block_origin):
    block = get_highest_block(tips, block_origin)

    while block.cblock is None:
        logging.debug('using prev block because cblock for {} is not available'.format(block.hash_repr()))
        block = block.prevBlock

    logging.debug('current highest {} with cblock and block_origin={}'.format(block.hash_repr(), block_origin))
    return block


def respond_get_headers(tips, block_origin, vhave, hashstop):
    block = get_highest_block(tips, block_origin, BlockOrigin.private)

    candidate_blocks = []
    while block.hash() != core.CoreRegTestParams.GENESIS_BLOCK.GetHash() and block.hash() not in vhave:
        candidate_blocks.append(block)
        block = block.prevBlock

    blocks = []
    for block in reversed(candidate_blocks):
        if block.cblock is not None:
            blocks.append(block)
        else:
            break

        if block.hash() == hashstop:
            break
    return blocks


def get_tips_for_block_origin(tips, block_origin):
    tips_for_block_origin = Set()
    for tip in tips:
        tmp = tip
        while tmp.block_origin is not block_origin and tmp.transfer_allowed is False:
            tmp = tmp.prevBlock
        tips_for_block_origin.add(tmp)
    return list(tips_for_block_origin)


def request_get_headers(tips, block_origin):
    tip = get_highest_block_with_cblock(tips, block_origin)

    headers = [tip.hash()]

    i = 1
    tmp = tip
    while i <= 16 and tmp.prevBlock is not None:
        tmp = tmp.prevBlock
        i += 1

        if i in [2, 4, 8, 16]:
            headers.append(tmp.hash())
    return headers


class Fork:
    def __init__(self, private_tip, private_height, public_tip, public_height):
        self.private_tip = private_tip
        self.private_height = private_height
        self.public_tip = public_tip
        self.public_height = public_height

    def __repr__(self):
        return '{}(private_height={} public_height={})' \
            .format(self.__class__.__name__, self.private_height, self.public_height)

    def __eq__(self, other):
        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        return self.__dict__ != other.__dict__