from bitcoin import core
from chain import Block
from chain import BlockOrigin

genesis_hash = core.CoreRegTestParams.GENESIS_BLOCK.GetHash()
genesis_block = Block(core.CoreRegTestParams.GENESIS_BLOCK, BlockOrigin.public)
genesis_block.height = 0
genesis_block.transfer_allowed = True
genesis_block.cblock = core.CoreRegTestParams.GENESIS_BLOCK
