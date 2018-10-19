import logging
from strategy import Action
from strategy import ActionException
from strategy import BlockOrigin


class Strategy:
    def __init__(self, lead_stubborn=False, equal_fork_stubborn=False, trail_stubborn=0):
        self.lead_stubborn = lead_stubborn
        self.equal_fork_stubborn = equal_fork_stubborn
        self.trail_stubborn = trail_stubborn * -1
        self.active = False

        logging.info('created strategy lead_stubborn={} equal_fork_stubborn={} trail-stubborn={}'
                     .format(lead_stubborn, equal_fork_stubborn, trail_stubborn))

    def find_action(self, length_private, length_public, last_block_origin):
        logging.debug('executing find_action with length_private={}, length_public={} and last_block_origin={}'
                      .format(length_private, length_public, last_block_origin))
        if last_block_origin is BlockOrigin.public:
            length_public -= 1
            self.active = False
        else:
            length_private -= 1

        private_lead = length_private - length_public

        if private_lead == 0:
            if length_private == 0:
                if last_block_origin is BlockOrigin.public:
                    return Action.adopt
                else:
                    return Action.wait
            else:
                if last_block_origin is BlockOrigin.public:
                    if self.trail_stubborn < 0:
                        return Action.wait
                    else:
                        return Action.adopt
                else:
                    if self.active and self.equal_fork_stubborn:
                        return Action.wait
                    else:
                        return Action.override

        elif private_lead > 0:
            if private_lead == 1:
                if last_block_origin is BlockOrigin.public:
                    self.active = True
                    return Action.match
                else:
                    return Action.wait

            elif private_lead == 2:
                if last_block_origin is BlockOrigin.public:
                    if self.lead_stubborn:
                        self.active = True
                        return Action.match
                    else:
                        return Action.override
                else:
                    return Action.wait

            else:
                if last_block_origin is BlockOrigin.public and self.lead_stubborn:
                    self.active = True
                    return Action.match
                return Action.wait

        else:
            if last_block_origin is BlockOrigin.private:
                if private_lead < self.trail_stubborn:
                    raise ActionException("private_lead={} should not be behind trail_stubborn={}"
                                          .format(private_lead, self.trail_stubborn))
                return Action.wait
            else:
                if private_lead <= self.trail_stubborn:
                    return Action.adopt
                else:
                    return Action.wait
