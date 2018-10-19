#!/usr/bin/env python
# -*- coding: utf-8 -*-
import argparse, random, copy

parser = argparse.ArgumentParser(description='Simulate selfish miner based on Algorithm 1 in SM paper.  Use the --series option to model mining as a series of mining events as defined in the paper.  The default model in this simulator to model mining as independent and competing factions mining in parallel.')
parser.add_argument('-a', '--alpha', type=float, default=1.0/3.0, help='SM mining power = α, HM mining power = 1-α')
parser.add_argument('-g', '--gamma', type=float, default=0.0, help='γ is the probability a selfish chain wins a block race')
parser.add_argument('-e', '--events', type=int, default=100000, help='Number of mining events')
parser.add_argument('-s', '--series', default=False, dest='series', action='store_true', help='Model mining as a series (from paper) instead of independent parallel processes')
args = parser.parse_args()

alpha = getattr(args, 'alpha')
gamma = getattr(args, 'gamma')
num_events = getattr(args, 'events')
series_model = getattr(args, 'series')
assert alpha>0.0 and alpha<1.0, "alpha must be between 0.0 and 1.0"
assert gamma>=0.0 and gamma<=1.0, "gamma must be in range: 0.0 <= gamma <= 1.0"
assert num_events>0, "number of events must be 1 or more"

SYSTEM_TEST_TRIES = 1000000
BITCOIN_AVERAGE_TIME = 10
HM_AVERAGE_TIME = BITCOIN_AVERAGE_TIME * 1.0/(1.0-alpha)
SM_AVERAGE_TIME = BITCOIN_AVERAGE_TIME * (1.0/alpha)
PUBLIC_CHAIN = [] # add 'h' or 's' for mined blocks
PRIVATE_CHAIN = []
PRIVATE_BRANCH_LEN = 0
HM_BLOCKS_FOUND_COUNTER = 0 # miners compete from same block B to find their own block B'
SM_BLOCKS_FOUND_COUNTER = 0
BRANCH_MINING_COUNTER = 0
HONEST_CLOCK = 0
SELFISH_CLOCK = 0

print( "===== PARAMETERS =====")
print ("alpha={}, gamma={}, model={}, events={}".format(alpha, gamma, "series" if series_model else "parallel", num_events))
print("average block time for honest miner: ", HM_AVERAGE_TIME)
print ("average block time for selfish miner: ", SM_AVERAGE_TIME)
# random.expovariate gives us interval between arrivals averaging x mins
if series_model is not True:
    print ("system test: average arrival of HM blocks in {} tries: {}".format(SYSTEM_TEST_TRIES, sum([random.expovariate(1.0/HM_AVERAGE_TIME) for i in xrange(SYSTEM_TEST_TRIES)])/SYSTEM_TEST_TRIES ))
    print ("system test: average arrival of SM blocks in {} tries: {}".format(SYSTEM_TEST_TRIES, sum([random.expovariate(1.0/SM_AVERAGE_TIME) for i in xrange(SYSTEM_TEST_TRIES)])/SYSTEM_TEST_TRIES ))

for mining in xrange(num_events):
    # Who finds the next block?
    if series_model is True:
        # Model A: Series of events
        selfish_next_event = random.random() < alpha
    else:
        # Model B: Indepedendent processes
        ht = random.expovariate(1.0/HM_AVERAGE_TIME)
        st = random.expovariate(1.0/SM_AVERAGE_TIME)
        if PRIVATE_BRANCH_LEN == 0:
            # reset clock: all miners on block B, now compete to find their own block B'
            HONEST_CLOCK = ht
            SELFISH_CLOCK = st
        else:
            # miners are on different branches
            if HONEST_CLOCK == 0.0:
                HONEST_CLOCK = ht
            elif HONEST_CLOCK < SELFISH_CLOCK:
                HONEST_CLOCK += ht
            SELFISH_CLOCK += st
        selfish_next_event = SELFISH_CLOCK < HONEST_CLOCK

    # Stats
    if PRIVATE_BRANCH_LEN==0:
        if selfish_next_event:
            SM_BLOCKS_FOUND_COUNTER += 1
        else:
            HM_BLOCKS_FOUND_COUNTER += 1
    else:
        BRANCH_MINING_COUNTER += 1

    # Algorithm 1 from SM paper
    if selfish_next_event is True:
        delta_prev = len(PRIVATE_CHAIN) - len(PUBLIC_CHAIN)
        assert(delta_prev >= 0)
        PRIVATE_CHAIN.append('s')
        PRIVATE_BRANCH_LEN += 1
        if delta_prev == 0 and PRIVATE_BRANCH_LEN==2:
            # previously was two branches of length 1, selfish miner publishes now to win
            PUBLIC_CHAIN = copy.copy(PRIVATE_CHAIN)
            PRIVATE_BRANCH_LEN = 0
    else:
        delta_prev = len(PRIVATE_CHAIN) - len(PUBLIC_CHAIN)
        assert(delta_prev >= 0)
        PUBLIC_CHAIN.append('h')
        if delta_prev == 0:
            PRIVATE_CHAIN = copy.copy(PUBLIC_CHAIN)
            PRIVATE_BRANCH_LEN = 0
        elif delta_prev == 1:
            # Lead was 1, others find a block.
            # Honest chain has caught up, selfish miner must publish and compete
            if random.random() < gamma:
                PUBLIC_CHAIN = copy.copy(PRIVATE_CHAIN)
        elif delta_prev == 2:
            # Lead was 2, others find a block. 
            # Public chain is one behind, selfish miner publishes now to win
            PUBLIC_CHAIN = copy.copy(PRIVATE_CHAIN)
            PRIVATE_BRANCH_LEN = 0
        else:
            # Lead is more than 2, others find a block.  Publish first block of secret chain
            if random.random() < gamma:
                PUBLIC_CHAIN[-1] = 's'  # honest miners now build from the sm block just published

# Simulation is ending so publish private branch if it has a lead
if PRIVATE_BRANCH_LEN>0:
    PUBLIC_CHAIN = copy.copy(PRIVATE_CHAIN)

total_count = len(PUBLIC_CHAIN)
h_count = PUBLIC_CHAIN.count('h')
s_count = PUBLIC_CHAIN.count('s')
total_found = HM_BLOCKS_FOUND_COUNTER + SM_BLOCKS_FOUND_COUNTER
total_system = total_found + BRANCH_MINING_COUNTER
orphans = total_system - h_count - s_count
share_selfish = 100.0 * s_count / total_count
print ("===== RESULTS =====")
print ( "### Mining")
print ( "               Number of events : {}".format(total_system))
print ( "      Competing from same block : {}".format(total_found))
print ( "             Honest miner found : {0} ({1:.2f}%)".format(HM_BLOCKS_FOUND_COUNTER, HM_BLOCKS_FOUND_COUNTER*100.0/total_found))
print ( "            Selfish miner found : {0} ({1:.2f}%)".format(SM_BLOCKS_FOUND_COUNTER, SM_BLOCKS_FOUND_COUNTER*100.0/total_found))
print ( "  Competing on different blocks : {}".format(BRANCH_MINING_COUNTER))
print ( "### Blockchain")
print ( "              Blockchain length : {}".format(total_count))
print ( "   Honest miner blocks rewarded : {0} ({1:.2f}%)".format(h_count, h_count*100.0/total_count))
print ( "  Selfish miner blocks rewarded : {0} ({1:.2f}%)".format(s_count, share_selfish))
print ( "                Orphaned blocks : {0} ({1:.2f}% of total mining events)".format(orphans, orphans * 100.0/total_system))
print ( "### Summary")
print ( "               Default strategy : {0:.2f}% of blocks rewarded based on hash power".format(100.0 * alpha))
print ( "               Selfish strategy : {0:.2f}% of blocks rewarded".format(share_selfish))
print ( "   Selfish strategy performance : {0:.8f}%".format( 100.0 * (float(s_count)/total_count - alpha)/alpha ))
