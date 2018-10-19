package selfish;

public class StubbornMiningNewL {
	
	public static Gain newL(double a, double g){
		AbstractZeroHandler lZeroHandler = new AbstractZeroHandler() {
			
			@Override
			public void zeroHandler(State init) {
				double rand = StubbornMining.getRand();
				if (rand < init.a) {
					init.blocksGained += (1 + init.pending);
				} else if (rand < init.a + init.g * (1 - init.a)) {
					init.blocksGained += init.pending;
					init.publicGained += 1;
				} else {
					init.publicGained += (1 + init.pending);
				}
				init.pending = 0;
				init.primeState = false;
			}
		};
		
		Gain newL = baseCode(a, g, lZeroHandler, false);
		return newL;
	}
	
	public static Gain newLF(double a, double g){
		AbstractZeroHandler lfZeroHandler = new AbstractZeroHandler() {
			
			@Override
			public void zeroHandler(State init) {
				double rand = StubbornMining.getRand();
				if (rand < init.a) {
					init.state = 1;
				} else if (rand < init.a + init.g * (1 - init.a)) {
					init.blocksGained += init.pending;
					init.publicGained += 1;
					init.pending = 0;
					init.state = 0;
					init.primeState = false;
				} else {
					init.publicGained += (1 + init.pending);
					init.pending = 0;
					init.state = 0;
					init.primeState = false;
				}
			}
		};
		
		Gain newLf = baseCode(a, g, lfZeroHandler, true);
		return newLf;
	}
	
	public static Gain newLT(double a, double g){
		AbstractZeroHandler ltZeroHandler = new AbstractZeroHandler() {
			
			@Override
			public void zeroHandler(State init) {
				double rand = StubbornMining.getRand();
				if (rand < init.a) {
					init.state = 1;
				} else if (rand < init.a + init.g * (1 - init.a)) {
					init.blocksGained += init.pending;
					init.publicGained += 1;
					init.pending = 0;
					init.state = 0;
					init.primeState = false;
				} else {
					init.state = -1;
					init.primeState = false;
				}
			}
		};
		
		Gain newLt = baseCode(a, g, ltZeroHandler, false);
		return newLt;
	}
	
	public static Gain newLFT(double a, double g){
		AbstractZeroHandler lftZeroHandler = new AbstractZeroHandler() {
			
			@Override
			public void zeroHandler(State init) {
				double rand = StubbornMining.getRand();
				if (rand < init.a) {
					init.state = 1;
				} else if (rand < init.a + init.g * (1 - init.a)) {
					init.blocksGained += init.pending;
					init.publicGained += 1;
					init.pending = 0;
					init.state = 0;
					init.primeState = false;
				} else {
					init.state = -1;
					init.primeState = false;
				}
			}
		};
		
		Gain newLft = baseCode(a, g, lftZeroHandler, true);
		return newLft;
	}


	public static Gain selfishMiningNewL(double a, double g) {
		int state = 0;
		boolean isSelfish;
		long blocksGained = 0;
		long publicGained = 0;
		long pending = 0;
		double rand;
		boolean primeState = false;
		
		for (long i = 0; i < StubbornMining.ITERATIONS; i++) {
			
			switch (state) {
				case 0:
					if (primeState){
						rand = StubbornMining.getRand();
						if (rand < a) {
							blocksGained += (1 + pending);
						} else if (rand < a + g * (1 - a)) {
							blocksGained += pending;
							publicGained += 1;
						} else {
							publicGained += (1 + pending);
						}
						pending = 0;
						primeState = false;
					}
					else{
						primeState = false;
						isSelfish = StubbornMining.isNextBlockSelfish(a);
						state = isSelfish ? 1 : 0;
						if (!isSelfish) {
							publicGained++;
						}
					}
					break;
				default:
					if (primeState){
						rand = StubbornMining.getRand();
						if (rand < a) {
							state++;
						} else if (rand < a + g * (1 - a)) {
							blocksGained += pending;
							pending = 0;
							state--;
							pending++;
						} else{
							state--;
							pending++;
						}
					}
					else{
						isSelfish = StubbornMining.isNextBlockSelfish(a);
						
						if (isSelfish) {
							state++;
						} else {
							primeState = true;
							state--;
							pending++;
						}
					}
					break;
			}
		}
		Gain gain = new Gain();
		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained));
		gain.setBlocks(blocksGained, publicGained, -1);
		return gain;
	}

	public static Gain baseCode(double a, double g, AbstractZeroHandler zeroHandler, boolean EFstubborn) {
		int state = 0;
		boolean isSelfish = false;
		long blocksGained = 0;
		long publicGained = 0;
		long pending = 0;
		double rand;
		boolean primeState = false;
		State lastZeroState = null;
		for (long i = 0; i < StubbornMining.ITERATIONS; i++) {
			switch (state) {
				case 0:
					if (primeState){
						State s = new State(a, g, blocksGained, publicGained, 0, pending, primeState, state);
						zeroHandler.zeroHandler(s);
						a = s.a;
						g = s.g;
						blocksGained = s.blocksGained;
						publicGained = s.publicGained;
						pending = s.pending;
						primeState = s.primeState;
						state = s.state;
					}
					else{
						primeState = false;
						isSelfish = StubbornMining.isNextBlockSelfish(a);
						state = isSelfish ? 1 : 0;
						if (!isSelfish) {
							publicGained++;
						}
						lastZeroState = new State(a, g, blocksGained, publicGained, 0, pending, primeState, state);
					}
					break;
				case -1:
					if (!isSelfish) {
						state = -2;
					} else {
						state = -200; // 0''
						pending += 1;
					}
					break;
				case -200:
					if (primeState) {
						System.out.println("WTH!!! prime state at -1?");
					}
					isSelfish = StubbornMining.isNextBlockSelfish(a);
					if (isSelfish) {
						if (!EFstubborn){
							blocksGained += pending + 1;
							pending = 0;
							state = 0;
							primeState = false; // just making sure
						}
						else{
							// If equal fork stubborn, does not release and goes to state 1
							state = 1;
							primeState = false; 
						}
					} else {
						state = -1;
					}
					break;
				case -2:
					if (!isSelfish) {
						state = 0;
						publicGained += (3 + pending);
						
						pending = 0;
						
					}
					else{
						state++;
						pending += 1;
					}
					break;
				default:
					if (primeState){
						rand = StubbornMining.getRand();
						if (rand < a) {
							state++;
						} else if (rand < a + g * (1 - a)) {
							blocksGained += pending;
							pending = 0;
							state--;
							pending++;
						} else{
							state--;
							pending++;
						}
					}
					else{
						isSelfish = StubbornMining.isNextBlockSelfish(a);
						if (isSelfish) {
							state++;
						} else {
							primeState = true;
							state--;
							pending++;
						}
					}
					break;
			}
		}
		
		Gain gain = new Gain();
		gain.setAlpha(1.0 * lastZeroState.blocksGained / (lastZeroState.publicGained + lastZeroState.blocksGained));
		gain.setBeta(1.0 * lastZeroState.publicGained / (lastZeroState.publicGained + lastZeroState.blocksGained));
		gain.setBlocks(lastZeroState.blocksGained, lastZeroState.publicGained, -1);
		return gain;
	}

}
