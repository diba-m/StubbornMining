package selfish;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Random;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;



public class StubbornMining {

	public static int ITERATIONS = 100000;
	public static int REPS = 100;
	public static Random rand = new Random();


	public static void main(String args[]) throws IOException {
		double gin = Double.parseDouble(args[0]);
		DecimalFormat df = new DecimalFormat("0.00");
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(args[1] + "_" + gin));

		double confidence = 0.95;
		
		for (double g = gin; g < gin + 0.1; g = g + 0.0025) {
			if (g > 1.0)
				continue;
			for (double a = 0.3; a < 0.5; a = a + 0.0025) {
				
				SummaryStatistics honesta = new SummaryStatistics();
				SummaryStatistics tsma = new SummaryStatistics();
				SummaryStatistics newLa = new SummaryStatistics();
				SummaryStatistics fa = new SummaryStatistics();
				SummaryStatistics newLfa = new SummaryStatistics();
				SummaryStatistics ta = new SummaryStatistics();
				SummaryStatistics newLta = new SummaryStatistics();
				SummaryStatistics fta = new SummaryStatistics();
				SummaryStatistics newLfta = new SummaryStatistics();
				
				for (int rep = 0; rep < REPS; rep++) {
					Gain f = selfishMiningF(a, g);


					double theoreticalSelfish = theoreticalSelfishMining(a, g);

					Gain t = selfishMiningT(a, g);

					Gain ft = selfishMiningFT(a, g);

					Gain newL = StubbornMiningNewL.newL(a,g);
					Gain newLt = StubbornMiningNewL.newLT(a,g);
					Gain newLf = StubbornMiningNewL.newLF(a,g);
					Gain newLft = StubbornMiningNewL.newLFT(a,g);
					
					honesta.addValue(a);
					tsma.addValue(theoreticalSelfish);
					newLa.addValue(newL.a);
					fa.addValue(f.a);
					newLfa.addValue(newLf.a);
					//bw2.write("newLF " + newLf.a + " " + newLf.bA + "\n");
					ta.addValue(t.a);
					newLta.addValue(newLt.a);
					fta.addValue(ft.a);
					newLfta.addValue(newLft.a);
				}
				
				
				double[] means = new double[9];
				double[] sems = new double[9];
				int maxidx = 0;
				int secmaxidx = 0;
				
				SummaryStatistics[] ss = new SummaryStatistics[] {honesta, tsma, newLa, fa, newLfa, ta, newLta, fta, newLfta};
				int count = 0;
				for (SummaryStatistics s : ss){
					double sem = calcSEM(s, 0.95);
			        means[count] = s.getMean();
			        sems[count] = sem;
			        
			        
			        if (means[count] > means[maxidx]){
			        	secmaxidx = maxidx;
			        	maxidx = count;
			        }
			        
			        if (count == 1 && maxidx == secmaxidx){
			        	secmaxidx = 1;
			        }
			        
			        count += 1;
				}
				
		        TDistribution tDist = new TDistribution(REPS - 1);
		        double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidence) / 2);
	            // Calculate confidence interval
	            
		        
		        if (means[maxidx] - means[secmaxidx] < critVal * Math.sqrt(sems[maxidx] * sems[maxidx] + sems[secmaxidx] * sems[secmaxidx])){
		        	maxidx = 9;
		        }
		        bw2.write(String.format("%.4f %.4f %d |", a,g,maxidx));
		        for(count = 0; count < 9; count += 1)
		        	bw2.write(String.format(" %.4f", means[count]));
		        bw2.write("\n");
			}
		}
		bw2.flush();
		bw2.close();
	}

	static double theoreticalSelfishMining(double a, double g) {
		return (a*(1-a)*(1-a)*(4*a + g*(1-2*a)) - a*a*a)/(1 - a * (1 + 2 * a - a * a));
	}
	
	public static double calcSEM(SummaryStatistics stats, double level) {
        return stats.getStandardDeviation() / Math.sqrt(stats.getN());
    }



	public static Gain selfishMiningF(double a, double g) {
		int state = 0;
		boolean isSelfish;
		long blocksGained = 0;
		long publicGained = 0;
		long pending = 0;
		for (long i = 0; i < ITERATIONS; i++) {
			isSelfish = isNextBlockSelfish(a);
			switch (state) {
			case 0:
				if (i > ITERATIONS - 100)
					break;
				state = isSelfish ? 1 : 0;
				if (!isSelfish) {
					publicGained++;
				}
				break;
			case 1:
				state = isSelfish ? 2 : -1;
				if (state == -1){
					pending++;
				}
				break;
			case -1:
				double rand = getRand();
				if (rand < a) {
					state = 1;
				} else if (rand < a + g * (1 - a)) {
					blocksGained += pending;
					pending = 0;
					publicGained += 1;
					state = 0;
				} else {
					publicGained += (1 + pending);
					pending = 0;
					state = 0;
				}
				break;
			case 2:
				if (isSelfish) {
					state = 3;
				} else {
					state = 0;
					blocksGained += (2 + pending);
					pending = 0;
				}
				break;
			default:
				if (isSelfish) {
					state++;
				} else {
					state--;
					pending++;
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

	public static Gain selfishMiningT(double a, double g) throws IOException {
		int state = 0;
		boolean isSelfish;
		long blocksGained = 0;
		long publicGained = 0;
		long pending = 0;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for (long i = 0; i < ITERATIONS; i++) {
			isSelfish = isNextBlockSelfish(a);
			if (state == 0 && i > ITERATIONS - 1000){
				break;
			}

			if (printAll == true) {
				System.out.println("State = " + state + "; Bob = " +
						publicGained + "; Alice = " + blocksGained + "; pending = " + pending);
			}
			switch (state) {
			case 0:
				state = isSelfish ? 1 : 0;
				if (!isSelfish) {
					if (printAll == true)
						System.out.println("Bob mines");

					publicGained++;
					if (printAll == true)
						System.out.println("Bob += 1");
				} else if (printAll == true) {
					System.out.println("Alice mines.");
				}
				break;
			case 1:
				if (printAll == true) {
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				state = isSelfish ? 2 : -100;
				if (state == -100){
					pending++;
				}
				break;
			case -100: // 0'
				double rand = getRand();
				if (rand < a) {
					blocksGained += pending + 1;
					if (printAll == true){
						System.out.println("Alice mines");
						System.out.println("Alice += " + (pending + 1));
					}
					pending = 0;
					state = 0;
				} else if (rand < a + g * (1 - a)) {
					blocksGained += pending;
					if (printAll == true){
						System.out.println("Bob Gamma mines");

						System.out.println("Bob += " + pending);
					}
					pending = 0;
					publicGained += 1;
					state = 0;
				} else {
					state = -1;
					if (printAll == true){
						System.out.println("Bob (1-gamma) mines");
					}
				}
				break;
			case -1:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (!isSelfish) {
					state = 0;
					publicGained += (2 + pending);
					if (printAll == true)
						System.out.println("Bob += " + (pending + 2));
					pending = 0;
					if (printAll == true)
						br.readLine();
				} else {
					state = -200; // 0''
					pending += 1;
				}
				break;
			case -200:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (isSelfish) {
					blocksGained += pending + 1;
					if (printAll == true)
						System.out.println("Alice += " + (pending + 1));
					pending = 0;
					state = 0;
					if (printAll == true)
						br.readLine();
				} else {
					state = -1;
				}
				break;
			case 2:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (isSelfish) {
					state++;
				} else{
					state = 0;
					blocksGained += pending + 2;
					if (printAll == true)
						System.out.println("Alice += " + (pending + 2));
					pending = 0;
				}
				break;
			default:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (isSelfish) {
					state++;
				} else {
					state--;
					pending++;
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

	public static Gain selfishMiningFT(double a, double g) throws IOException {
		int state = 0;
		boolean isSelfish;
		long blocksGained = 0;
		long publicGained = 0;
		long pending = 0;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for (long i = 0; i < ITERATIONS; i++) {
			isSelfish = isNextBlockSelfish(a);
			if (state == 0 && i > ITERATIONS - 1000){
				break;
			}

			if (printAll == true)
				System.out.println("State = " + state + "; Bob = " +
						publicGained + "; Alice = " + blocksGained + "; pending = " + pending);
			switch (state) {
			case 0:
				state = isSelfish ? 1 : 0;
				if (!isSelfish) {
					if (printAll == true)
						System.out.println("Bob mines");

					publicGained++;
					if (printAll == true)
						System.out.println("Bob += 1");
				}
				else if (printAll == true)
					System.out.println("Alice mines.");
				break;
			case 1:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				state = isSelfish ? 2 : -100;
				if (state == -100){
					pending++;
				}
				break;
			case -100: // 0'
				double rand = getRand();
				if (rand < a) {
					state = 1;
				} else if (rand < a + g * (1 - a)) {
					blocksGained += pending;
					if (printAll == true){
						System.out.println("Bob Gamma mines");

						System.out.println("Bob += " + pending);
					}
					pending = 0;
					publicGained += 1;
					state = 0;
				} else {
					state = -1;
					if (printAll == true){
						System.out.println("Bob (1-gamma) mines");
					}
				}
				break;
			case -1:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (!isSelfish) {
					state = 0;
					publicGained += (2 + pending);
					if (printAll == true)
						System.out.println("Bob += " + (pending + 2));
					pending = 0;
					if (printAll == true)
						br.readLine();
				} else {
					state = -200; // 0''
					pending += 1;
				}
				break;
			case -200:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (isSelfish) {
					state = 1;
				} else {
					state = -1;
				}
				break;
			case 2:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (isSelfish) {
					state++;
				} else{
					state = 0;
					blocksGained += pending + 2;
					if (printAll == true)
						System.out.println("Alice += " + (pending + 2));
					pending = 0;
				}
				break;
			default:
				if (printAll == true){
					if (isSelfish)
						System.out.println("Alice mines");
					else
						System.out.println("Bob mines.");
				}
				if (isSelfish) {
					state++;
				} else {
					state--;
					pending++;
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

	public static Gain selfishMining(double a, double g) {
		int state = 0;
		boolean isSelfish;
		long blocksGained = 0;
		long publicGained = 0;
		for (long i = 0; i < ITERATIONS; i++) {
			isSelfish = isNextBlockSelfish(a);
			switch (state) {
			case 0:
				state = isSelfish ? 1 : 0;
				if (!isSelfish) {
					publicGained++;
				}
				break;
			case 1:
				state = isSelfish ? 2 : -1;
				break;
			case -1:
				double rand = getRand();
				state = 0;
				if (rand < a) {
					blocksGained += 2; // I mined on me
				} else if (rand < a + g * (1 - a)) {
					blocksGained += 1; // public mined on me
					publicGained += 1;
				} else {
					publicGained += 2;
					// no gain when public mines on public
				}
				break;
			case 2:
				if (isSelfish) {
					state = 3;
				} else {
					state = 0;
					blocksGained += 2;
				}
				break;
			default: // state 3 or more
				if (isSelfish) {
					state += 1;
				} else {
					state -= 1;
					blocksGained += 1;
				}
				break;
			}
		}
		Gain gain = new Gain();
		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained));
		gain.setBlocks(blocksGained, publicGained, -1);
		return gain;

		//		System.out.println(a + "," + g + "," + blocksGained  + "," + publicGained + ",selfish");
		//		System.out.println(a + "," + g + "," + 1.0 * blocksGained / (publicGained + blocksGained) + ",selfish");
	}

	static boolean isNextBlockSelfish(double a) {
		double next = rand.nextDouble();
		return next < a;
	}

	static double getRand() {
		double next = rand.nextDouble();
		return next;
	}
}
