package selfish;


public class EclipseColludeDestroy {

	public static Gain eclipseColludeLambda(double a, double g, double l) {
		int state = 0;
		int ours = 0;
		int lambdas = 0;
		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		Eclipse.Party party;
		for (long i = 0; i < Eclipse.ITERATIONS; i++) {
			party = Eclipse.getNextBlockParty(a, l, g);
			switch (state) {
				case 0:
					if (Eclipse.Party.LAMBDA.equals(party)) {
						state = 1;
						lambdas++;
					} else if (Eclipse.Party.ALPHA.equals(party)) {
						state = 1;
						ours++;
					} else {
						state = 0;
						publicGained++;
					}
					break;
				case 1:
					if (Eclipse.Party.LAMBDA.equals(party)) {
						state = 2;
						lambdas++;
					} else if (Eclipse.Party.ALPHA.equals(party)) {
						state = 2;
						ours++;
					} else {
						state = -1;
					}
					break;
				case -1:
					if (party.equals(Eclipse.Party.LAMBDA)) {
						state = 0;
						lambdaGained = lambdaGained + lambdas + 1;
						blocksGained = blocksGained + ours;
						lambdas = ours = 0;
					} else if (party.equals(Eclipse.Party.ALPHA)) {
						state = 0;
						lambdaGained = lambdaGained + lambdas;
						blocksGained = blocksGained + ours + 1;
						lambdas = ours = 0;
					} else if (party.equals(Eclipse.Party.BETA) && Eclipse.GAMMABETA) {
						state = 0;
						lambdaGained = lambdaGained + lambdas;
						blocksGained = blocksGained + ours;
						publicGained++;
						lambdas = ours = 0;
					} else { // beta
						state = 0;
						publicGained = publicGained + ours + lambdas + 1;
						lambdas = ours = 0;
					}
					break;
				case 2:
					if (Eclipse.Party.LAMBDA.equals(party)) {
						state = 3;
						lambdas++;
					} else if (Eclipse.Party.ALPHA.equals(party)) {
						state = 3;
						ours++;
					} else {
						state = 0;
						lambdaGained = lambdaGained + lambdas;
						blocksGained = blocksGained + ours;
						lambdas = ours = 0;
					}
					break;
				default:
					if (Eclipse.Party.LAMBDA.equals(party)) {
						state += 1;
						lambdas++;
					} else if (Eclipse.Party.ALPHA.equals(party)) {
						state += 1;
						ours++;
					} else {
						state -= 1;
					}
			}
		}
		Gain gain = new Gain();
		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained / (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}

	public static Gain eclipseDestroyLambda(double a, double g, double l) {
			int state = 0;
			long blocksGained = 0;
			long publicGained = 0;
			long lambdaGained = 0;
			Eclipse.Party party;
			for (long i = 0; i < Eclipse.ITERATIONS; i++) {
				party = Eclipse.getNextBlockParty(a, l, g);
				switch (state) {
					case 0:
						if (Eclipse.Party.LAMBDA.equals(party)) {
							// ignore
						} else if (Eclipse.Party.ALPHA.equals(party)) {
							state = 1;
						} else {
							state = 0;
							publicGained++;
						}
						break;
					case 1:
						if (Eclipse.Party.LAMBDA.equals(party)) {
							// ignore
						} else if (Eclipse.Party.ALPHA.equals(party)) {
							state = 2;
						} else {
							state = -1;
						}
						break;
					case -1:
						if (Eclipse.Party.LAMBDA.equals(party)) {
							// ignore lambda
						} else if (Eclipse.Party.ALPHA.equals(party)) {
							// I mine on myself
							state = 0;
							blocksGained += 2;
						} else if (Eclipse.Party.BETA.equals(party) && Eclipse.GAMMABETA) {
							// public mined on me
							state = 0;
							blocksGained += 1;
							publicGained += 1;
						} else { // beta
							state = 0;
							publicGained += 2;
							// no gain when public mines on public
						}
						break;
					case 2:
						if (Eclipse.Party.LAMBDA.equals(party)) {
							// ignore
						} else if (Eclipse.Party.ALPHA.equals(party)) {
							state = 3;
						} else {
							state = 0;
							blocksGained += 2;
						}
						break;
					default: // state 3 or more
						if (Eclipse.Party.LAMBDA.equals(party)) {
							// ignore
						} else if (Eclipse.Party.ALPHA.equals(party)) {
							state += 1;
						} else {
							state -= 1;
							blocksGained += 1;
						}
				}
			}
			Gain gain = new Gain();
			gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained + lambdaGained));
			gain.setBeta(1.0 * publicGained / (publicGained + blocksGained + lambdaGained));
			gain.setLambda(1.0 * lambdaGained / (publicGained + blocksGained + lambdaGained));
			gain.setBlocks(blocksGained, publicGained, lambdaGained);
			return gain;
		}
}
