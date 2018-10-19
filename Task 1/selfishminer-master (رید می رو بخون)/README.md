Selfish Miner Simulator (BETA)
=======================

### What is Selfish Mining?

This simulator implements `Algorithm 1: Selfish-Mine` from the paper:

[Majority is not Enough: Bitcoin Mining is Vulnerable](https://arxiv.org/abs/1311.0243) [[pdf](https://arxiv.org/abs/1311.0243.pdf)]

The simplified model of Bitcoin mining presented in the paper has two variables:

* α - alpha is the selfish miner's share of hash power.  The honest miner's share is 1-α.

* γ - gamma is the probability that a selfish miner wins a block race.

The paper claims that with alpha=1/3 and gamma=0, selfish mining is a profitable strategy.

> We further show that the Bitcoin mining protocol will never be safe against
> attacks by a selfish mining pool that commands more than 1/3 of the total
> mining power of the network. Such a pool will always be able to collect mining
> rewards that exceed its proportion of mining power, even if it loses every single
> block race in the network.

However, this simulator can demonstrate the opposite.

### Mining Model

This simulator supports the model of mining presented in the paper:

* Series
  * `H----S----S----H----H`

However, when a group of miners collude and decide to change their behavour to become selfish, they are defecting from the main group.  There are now two independent factions competing in parallel to find blocks.  To model this defection correctly, the simulator supports a parallel model of mining:

* Parallel (e.g. two independent processes which split and combine to build on each others blocks)
  * `H--------------H----H`
  * ` \----S---S---/ \--/ `

The results from the parallel model contradict the results of the series model and support an argument that selfish mining is a losing strategy.

### Running

To launch the simulator in your terminal:

* `selfishminer.py`

Command line options are:

* `-a`, `--alpha` alpha (default is 1/3)
* `-g`, `--gamma` gamma (default is 0)
* `-e`, `--events` number of mining events (default is 100000)
* `-s`, `--series` use series model from paper instead of parallel model
* `-h` show help

### Results

Settings to simulate the results of the model in the paper for alpha=1/3, gamma=0:

`./selfishminer.py --series`

    ===== PARAMETERS =====
    alpha=0.333333333333, gamma=0.0, model=series, events=100000
    average block time for honest miner:  15.0
    average block time for selfish miner:  30.0
    ===== RESULTS =====
    ### Mining
                   Number of events : 100000
          Competing from same block : 52780
                 Honest miner found : 34991 (66.30%)
                Selfish miner found : 17789 (33.70%)
      Competing on different blocks : 47220
    ### Blockchain
                  Blockchain length : 76390
       Honest miner blocks rewarded : 50923 (66.66%)
      Selfish miner blocks rewarded : 25467 (33.34%)
                    Orphaned blocks : 23610 (23.61% of total mining events)
    ### Summary
                   Default strategy : 33.33% of blocks rewarded based on hash power
                   Selfish strategy : 33.34% of blocks rewarded
       Selfish strategy performance : 0.01439979%

When using the parallel model of mining for the same settings, the results are very different:

`./selfishminer.py`

    ===== PARAMETERS =====
    alpha=0.333333333333, gamma=0.0, model=parallel, events=100000
    average block time for honest miner:  15.0
    average block time for selfish miner:  30.0
    system test: average arrival of HM blocks in 1000000 tries: 15.0073631814
    system test: average arrival of SM blocks in 1000000 tries: 30.0006734274
    ===== RESULTS =====
    ### Mining
                   Number of events : 100000
          Competing from same block : 55694
                 Honest miner found : 37044 (66.51%)
                Selfish miner found : 18650 (33.49%)
      Competing on different blocks : 44306
    ### Blockchain
                  Blockchain length : 77847
       Honest miner blocks rewarded : 58924 (75.69%)
      Selfish miner blocks rewarded : 18923 (24.31%)
                    Orphaned blocks : 22153 (22.15% of total mining events)
    ### Summary
                   Default strategy : 33.33% of blocks rewarded based on hash power
                   Selfish strategy : 24.31% of blocks rewarded
       Selfish strategy performance : -27.07618791%

### 1% Sanity Check

Assume a selfish miner with 1% of hash power.  They should lose nearly all their blocks from a strategy of withholding:

`./selfishminer.py -a 0.01`

    ===== PARAMETERS =====
    alpha=0.01, gamma=0.0, model=parallel, events=100000
    average block time for honest miner:  10.101010101
    average block time for selfish miner:  1000.0
    system test: average arrival of HM blocks in 1000000 tries: 10.1097043549
    system test: average arrival of SM blocks in 1000000 tries: 1000.10012288
    ===== RESULTS =====
    ### Mining
                   Number of events : 100000
          Competing from same block : 97946
                 Honest miner found : 96919 (98.95%)
                Selfish miner found : 1027 (1.05%)
      Competing on different blocks : 2054
    ### Blockchain
                  Blockchain length : 98973
       Honest miner blocks rewarded : 98953 (99.98%)
      Selfish miner blocks rewarded : 20 (0.02%)
                    Orphaned blocks : 1027 (1.03% of total mining events)
    ### Summary
                   Default strategy : 1.00% of blocks rewarded based on hash power
                   Selfish strategy : 0.02% of blocks rewarded
       Selfish strategy performance : -97.97924687%

### 50%

Assume a selfish miner with 50% of hash power.

The paper claims that at alpha 0.5, the selfish miner will be rewarded with nearly all available blocks.

`./selfishminer.py -a 0.5 --series`

    ===== PARAMETERS =====
    alpha=0.5, gamma=0.0, model=series, events=100000
    average block time for honest miner:  20.0
    average block time for selfish miner:  20.0
    ===== RESULTS =====
    ### Mining
                   Number of events : 100000
          Competing from same block : 838
                 Honest miner found : 439 (52.39%)
                Selfish miner found : 399 (47.61%)
      Competing on different blocks : 99162
    ### Blockchain
                  Blockchain length : 50629
       Honest miner blocks rewarded : 645 (1.27%)
      Selfish miner blocks rewarded : 49984 (98.73%)
                    Orphaned blocks : 49371 (49.37% of total mining events)
    ### Summary
                   Default strategy : 50.00% of blocks rewarded based on hash power
                   Selfish strategy : 98.73% of blocks rewarded
       Selfish strategy performance : 97.45205317%

However, the parallel model shows a different result, where the selfish miner is unable to race ahead and maintain a branch with a lead of 2 blocks for long.  In the series model, the selfish miner races ahead and is never caught resulting in the low value for `Competing from same block`.

`./selfishminer.py -a 0.5`

    ===== PARAMETERS =====
    alpha=0.5, gamma=0.0, model=parallel, events=100000
    average block time for honest miner:  20.0
    average block time for selfish miner:  20.0
    system test: average arrival of HM blocks in 1000000 tries: 20.0091395069
    system test: average arrival of SM blocks in 1000000 tries: 19.9890492519
    ===== RESULTS =====
    ### Mining
                   Number of events : 100000
          Competing from same block : 35616
                 Honest miner found : 17749 (49.83%)
                Selfish miner found : 17867 (50.17%)
      Competing on different blocks : 64384
    ### Blockchain
                  Blockchain length : 67808
       Honest miner blocks rewarded : 30953 (45.65%)
      Selfish miner blocks rewarded : 36855 (54.35%)
                    Orphaned blocks : 32192 (32.19% of total mining events)
    ### Summary
                   Default strategy : 50.00% of blocks rewarded based on hash power
                   Selfish strategy : 54.35% of blocks rewarded
       Selfish strategy performance : 8.70398773%

### More Results (WIP)

### Orphaned Blocks

* With alpha=1/3, gamma=0.5, series mode, orphan rate is >20%.
* Blockchain.info reports 527 orphans in ~478000 blocks, rate of 0.11%.
* Miners running nodes which do not prune stale blocks can detect the orphan rate rising.
