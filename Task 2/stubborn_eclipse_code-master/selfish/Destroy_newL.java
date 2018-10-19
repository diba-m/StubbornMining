package selfish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import selfish.Eclipse.Party;

public class Destroy_newL {
	
	// Destroy + honest = Gain in reduced network, computedd theoretically
	
	// Destroy + selfish = Selfish gain in reduced network, computed theoretically

	// Destroy + blue
	public static Gain destroyL(double a, double g, double l) throws IOException {
	    Gain gain = StubbornMiningNewL.newL(a /(1-l), g);
	    return gain;		
	}
	
	// Destroy + Green
	public static Gain destroyLF(double a, double g, double l) throws IOException {
	    Gain gain = StubbornMiningNewL.newLF(a /(1-l), g);
	    return gain;		
	}
	
	public static Gain destroyF(double a, double g, double l) {
		Gain gain = StubbornMining.selfishMiningF(a /(1-l), g);
		return gain;		
	}
	
	public static Gain destroyT(double a, double g, double l) throws IOException {
		Gain gain = StubbornMining.selfishMiningT(a /(1-l), g);
		return gain;	
	}
	
	public static Gain destroyLT(double a, double g, double l) {
		Gain gain = StubbornMiningNewL.newLT(a /(1-l), g);
		return gain;	
	}
	
	public static Gain destroyFT(double a, double g, double l) throws IOException {
		Gain gain = StubbornMining.selfishMiningFT(a /(1-l), g);
		
		return gain;	
	
	}
	
	public static Gain destroyLFT(double a, double g, double l) {
		Gain gain = StubbornMiningNewL.newLFT(a /(1-l), g);
		return gain;
	}
	
}
