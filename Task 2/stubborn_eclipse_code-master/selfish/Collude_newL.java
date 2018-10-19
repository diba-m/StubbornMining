package selfish;

import java.io.IOException;


public class Collude_newL {
	
	// Collude Honest = Honest
	
	// Collude Selfish mine = Selfish mining pool, computed theoretically.

	// C, B
	public static Gain eclipseColludeL(double a, double g, double l) {
		Gain gain = StubbornMiningNewL.newL(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;		
	}

	// C, G
	public static Gain eclipseColludeLF(double a, double g, double l) {
		Gain gain = StubbornMiningNewL.newLF(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;		

	}
	
	public static Gain eclipseColludeF(double a, double g, double l) {
		Gain gain = StubbornMining.selfishMiningF(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;		
	}
	
	public static Gain eclipseColludeT(double a, double g, double l) throws IOException {
		Gain gain = StubbornMining.selfishMiningT(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;	
	}
	
	public static Gain eclipseColludeLT(double a, double g, double l) {
		Gain gain = StubbornMiningNewL.newLT(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;	
	}
	
	public static Gain eclipseColludeFT(double a, double g, double l) throws IOException {
		Gain gain = StubbornMining.selfishMiningFT(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;	
	
	}
	
	public static Gain eclipseColludeLFT(double a, double g, double l) {
		Gain gain = StubbornMiningNewL.newLFT(a + l, g);
		Double alphagain = gain.a;
		gain.setAlpha(alphagain * a / (a+l));
		gain.setLambda(alphagain * l / (a + l));
		
		long alphablocks = gain.bA;
		gain.setBlocks((long)(alphablocks * a * 1.0 / (a+l)), gain.bB, (long)(alphablocks * l * 1.0 / (a+l)));
		return gain;
	}
	
}
