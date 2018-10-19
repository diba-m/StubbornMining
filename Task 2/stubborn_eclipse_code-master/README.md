Running the code
------------------------------------------------------------------------------------------
1) For results related to Stubborn mining (Section 4 in [1])

~~~~
javac -cp commons-math3-3.5.jar:. selfish/*.java
java -cp commons-math3-3.5.jar:. selfish.StubbornMining gammaValue out_file
~~~~

gammaValue: Alice's influence over the network

out_file: output will be stored in out_file_gamma (e.g. if the file name is 'out' and gamma = 0.9, then output will be stored in out_0.9)

This will output Alice's relative gain for all Stubborn mining strategies and the ID corresponding to the best strategy

The output is in the following format:
~~~~
alpha gamma best_strategy | honest SM L F LF T LT FT LFT
~~~~

where best_strategy is an index from 0 to 9 - values 0 to 8 correspond to strategies, 9 corresponds to no strategy is best with 95% confidence

SM: Selfish Mining

L: Lead Stubbornness

F: Fork Stubbornness

T: Trail Stubbornness

e.g.:

0.4775 0.0975 9 | 0.4775 0.8023 0.7824 0.8145 0.7476 0.8015 0.7873 0.8158 0.7625

2) For results related to Eclipse + Stubborn mining (Section 6 in [1])

~~~~
java -cp commons-math3-3.5.jar:. selfish.Eclipse gammaValue dir
~~~~

gammaValue: Alice's influence over the network
dir: output will be saved in dir and dir_blocks (e.g. if dir = 'out', then output will be stored in directories 'out' and 'out_blocks')

This will output Alice's relative gain for all Eclipse mining strategies and the ID corresponding to the best strategy

Output format: 5 tuples corresponding to No eclipsing, Collude, Destroy, DestroyIfNoStake and meta-information separated by '|', first 4 tuples formatted as H, S, L, LF, F, T, FT, LT, LFT and the meta-information formatted as "alpha gamma lambda best_strategy party"

party = 10: Gains for Alice

party = 20: Gains for Bob

party = 30: Gains for Lucy

e.g.:

0.4675 0.7378 0.4142 0.0000 0.7479 0.7374 0.7449 0.2712 0.0000 | 0.4675 0.8704 0.4709 0.0000 0.8759 0.8702 0.8768 0.3047 0.0000 | 0.4795 0.8119 0.4434 0.0000 0.8214 0.8103 0.8217 0.2942 0.0000 | 0.4795 0.8684 0.4416 0.0000 0.8729 0.8703 0.8767 0.6070 0.0000 | 0.4675 0.0000 0.0250 36 10
0.5075 0.2622 0.5858 1.0000 0.2521 0.2626 0.2551 0.7288 1.0000 | 0.5075 0.0831 0.5039 1.0000 0.0772 0.0832 0.0763 0.6790 1.0000 | 0.5205 0.1881 0.5566 1.0000 0.1786 0.1897 0.1783 0.7058 1.0000 | 0.5205 0.0875 0.5356 1.0000 0.0820 0.0856 0.0780 0.3615 1.0000 | 0.4675 0.0000 0.0250 3 20
0.0250 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 | 0.0250 0.0465 0.0252 0.0000 0.0468 0.0465 0.0469 0.0163 0.0000 | 0.0000 0.0000 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 -1.0000 | 0.0000 0.0441 0.0228 0.0000 0.0451 0.0442 0.0453 0.0315 0.0000 | 0.4675 0.0000 0.0250 36 30

--------------------------------------------------------------------------------
Citations
--------------------------------------------------------------------------------
[1] Kartik Nayak*, Srijan Kumar*, Andrew Miller, Elaine Shi

Stubborn Mining: Generalizing Selfish Mining and Combining with an Eclipse Attack
	
[Preprint](http://eprint.iacr.org/2015/796.pdf)
