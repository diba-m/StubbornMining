import logging
from strategy import ForkState
from strategy import Action
from strategy import ActionException
from strategy import BlockOrigin


class Strategy:
    def __init__(self, strategy):
        self.fork_state = ForkState.irrelevant
        self.strategy = strategy
        logging.warn('this strategy is not used. may contain bugs.')

    def find_action(self, length_private, length_public, last_block_origin):
        if length_private == 0 and length_public == 0:
            raise ActionException('both lengths can\'t be zero')

        if self.fork_state is ForkState.active:
            if last_block_origin is BlockOrigin.private and length_private <= length_public:
                self.fork_state = ForkState.irrelevant
                raise ActionException('fork_state=active, block_origin=private and '
                                      'length_private <= length_public')
            elif last_block_origin is BlockOrigin.public and length_private < length_public:
                self.fork_state = ForkState.irrelevant
                raise ActionException('fork_state=active, block_origin=public and '
                                      'length_private < length_public')

        logging.debug('find action old fork_state={}'.format(self.fork_state))
        if last_block_origin is BlockOrigin.public and length_public <= length_private:
            self.fork_state = ForkState.relevant
        elif last_block_origin is BlockOrigin.private and self.fork_state is ForkState.active:
            self.fork_state = ForkState.active
        else:
            self.fork_state = ForkState.irrelevant
        logging.debug('find action new fork_state={}'.format(self.fork_state))

        try:
            action = Action(self.strategy[self.fork_state.value][length_private][length_public])
        except (ValueError, IndexError):
            logging.warn('found no action with length_private={} length_public={} last_block_origin={} fork_state={} '
                         .format(length_private, length_public, last_block_origin, self.fork_state))

            self.fork_state = ForkState.irrelevant
            return Action.adopt

        logging.info('found action={} with length_private={} length_public={} last_block_origin={} fork_state={} '
                     .format(action, length_private, length_public, last_block_origin, self.fork_state))

        if action is Action.match:
            self.fork_state = ForkState.active

        return action
