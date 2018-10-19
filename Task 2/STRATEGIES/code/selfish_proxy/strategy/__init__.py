from enum import Enum


class Action(Enum):
    adopt = 'a'
    override = 'o'
    match = 'm'
    wait = 'w'


class ForkState(Enum):
    irrelevant = 0
    relevant = 1
    active = 2


class BlockOrigin(Enum):
    private = 0
    public = 1


def opposite_origin(block_origin):
    if block_origin is BlockOrigin.private:
        return BlockOrigin.public
    else:
        return BlockOrigin.private


class ActionException(Exception):
    def __init__(self, message):
        self.message = message

    def __str__(self):
        return repr(self.message)
