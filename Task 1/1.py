#!/usr/bin/python3

from random import random

# selfish mining simulation

# fraction of hash power owned by selfish miner
alpha = 0.33

# fraction of honest miners that build on selfish miner block in a tie.
gamma = 0.3

def runsimulation(iter):
    hmblocks = 0
    hmorphans = 0
    hmchain = 0
    smblocks = 0
    smorphans = 0
    smchain = 0

    for _ in range(1, iter):

        # Invariants
        # SM chain is always longer than HM chain
        # because otherwise SM switches to HM chain
        assert smchain >= hmchain
        # SM will never risk more than one block to a tie
        assert smchain != hmchain or smchain < 2
        
        if (random() < alpha):
            # SM found a block
            if smchain > 0 and smchain == hmchain:
                # SM publishes its chain to resolve tie.
                smchain = smchain + 1
                smblocks += smchain
                hmorphans += hmchain
                hmchain = 0
                smchain = 0
            else:
                # SM mines selfishly
                assert (smchain == 0 and hmchain == 0) or smchain > hmchain
                smchain = smchain + 1
        else:
            #HM found a block
            if smchain == 0:
                # HM publishes and SM builds on top
                assert hmchain == 0
                hmchain = hmchain + 1
                hmblocks += hmchain
                smorphans += smchain
                hmchain = 0
                smchain = 0
            elif (smchain == hmchain and random() < gamma):
                # In case of a tie, a fraction of HM may build on SM's chain
                # and this gets the longest published chain
                assert hmchain == 1
                smblocks += smchain
                hmorphans += hmchain
                hmchain = 0
                smchain = 0
                hmblocks = hmblocks + 1
            else:
                # HM builds on its own chain.
                hmchain = hmchain + 1
                if smchain == hmchain + 1:
                    # If SMs chain is longer by exactly 1,
                    # SM will publish his longer chain.
                    smblocks += smchain
                    hmorphans += hmchain
                    hmchain = 0
                    smchain = 0

                if hmchain > smchain:
                    # if HM has longer chain, SM switches to it.
                    hmblocks += hmchain
                    smorphans += smchain
                    hmchain = 0
                    smchain = 0

    print("Iterations: %d, alpha: %f, gamma: %f" % (iter, alpha, gamma))
    print("SM %d blocks, HM %d blocks, ratio %f"
            % (smblocks, hmblocks,
               smblocks / float(smblocks + hmblocks)))
    print("   Orphans: SM %d, HM %d, orphan ratio %f; SM: %f, HM: %f"
            % (smorphans, hmorphans,
               (smorphans + hmorphans) / float(smblocks + hmblocks + smorphans + hmorphans),
               smorphans / float(smblocks + smorphans),
               hmorphans / float(hmblocks + hmorphans)))
    print("   still contested: SM %d, HM %d"
            % (smchain, hmchain))

runsimulation(10000)