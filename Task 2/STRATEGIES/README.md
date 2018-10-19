# Simulation of different selfish mining strategies in Bitcoin
## Simulation respecting network topology and reference implementation

Diploma thesis submitted in partial fulfillment of the requirements for the degree of Diplom-Ingenieur in "Software Engineering & Internet Computing" at [TU Wien](https://www.tuwien.ac.at/en/). Advisor of the thesis was Privatdoz. Mag.rer.soc.oec. Dipl.-Ing. Dr.techn. Edgar Weippl. Additional assistance was provided by Univ.Lektor Dipl.-Ing. Aljosha Judmayer. For the simulations the framework [Simcoin](https://github.com/sbaresearch/simcoin) was developed and used.

### Abstract

The Cryptocurrency Bitcoin was started in 2008 with the creation of the first block, the Genesis Block. Since then, the computing power of the network, which secures the blockchain of the digital currency, has multiplied considerably. Today, around twenty professional miners are constantly extending the blockchain, always building on the latest block known to them. The miners are incentivised to do so, as they create with each found block new Bitcoins for themselves.

In 2014, Eyal and Gün Sirer showed for the first time that apart from this desired, honest behaviour, there are deviating mining methods that increase the relative gain of a miner compared to the rest of the network. This so-called selfish mining attack and all its modifications are examined in this thesis in a defined scenario with twenty miners and are compared with previous research results. In contrast to previous investigations, a novel, deterministic simulation framework based on Docker was developed for this purpose. This simulation framework makes it possible to naturally include the network latency and to directly reuse the reference implementation of Bitcoin. The latter has the advantage that no time-consuming and error-prone adaptation or abstraction of the reference implementation is necessary and all properties of the implemented Bitcoin protocol are automatically included in the simulation. To simulate the various selfish mining strategies, additionally, a proxy was implemented that eclipsed a node in the network and misuses the node to perform the various selfish mining attacks.

The simulations of the various selfish mining strategies show that a dishonest miner can increase its relative gain over the rest of the network, thus reinforcing the current state of research and the relevance of the selfish mining attack. As the most efficient selfish mining strategies under the simulation scenario with twenty miners, selfish mining and equal-fork-stubbornness were identified.

### Acknowledgements

First, I would like to sincerely thank my parents for their ongoing support throughout my whole education. Without their trust in and my abilities, I would have never had the opportunity to reach this goal in my academic career. Furthermore, I would like to thank my girlfriend for her tireless motivation during critical phases and for reviewing the present work.

Special thanks go to [Internet Foundation Austria](https://www.netidee.at/) for granting me a scholarship for this thesis. With the scholarship, I had the possibility to invest more time and resources in the thesis. Additionally, I could extend the implemented simulation software for a more general purpose and provide it as a stand-alone framework to foster more investigation and development in the research area.

<img src="https://github.com/simonmulser/master-thesis/blob/master/graphics/netidee_logo_scholarship.jpg" alt="Image of netidee scholarship logo" style="width: 248px;"/>

Finally, I would like to thank Privatdoz. Mag.rer.soc.oec. Dipl.-Ing. Dr.techn. Edgar Weippl and Univ.Lektor Dipl.-Ing. Aljosha Judmayer for the possibility of conducting this master thesis as well as their supervision and their constructive feedback.

<img src="https://github.com/simonmulser/master-thesis/blob/master/graphics/sba_logo.jpg" alt="Image of SBA-Research logo" style="width: 236px;"/>
