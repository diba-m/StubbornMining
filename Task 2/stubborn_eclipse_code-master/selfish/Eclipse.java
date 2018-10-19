package selfish;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Eclipse {
	public static long ITERATIONS = 100000;
	private static int REPS = 100;
	public static Random rando = new Random();
	enum Party { ALPHA, BETA, LAMBDA}; 
	public static boolean GAMMABETA = false;

	public static void main(String args[]) throws IOException {
		runFull(args);
	}
	
	static void runOne(String args[]) throws IOException {
		double g = Double.parseDouble(args[0]);
		double a = Double.parseDouble(args[1]);
		double l = Double.parseDouble(args[2]);
		
		Gain desIfNoStake = Dns.destroyIfNoStakeT(a, g, l);
		
		System.out.format("%.4f\n",  desIfNoStake.a);	
	}

	static void runFull(String args[]) throws IOException {
		
		double g = Double.parseDouble(args[0]);
		double a,l;
		
		DecimalFormat df = new DecimalFormat("0.00");
		double confidence = 0.95;
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[1] + "/" +  g));
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(args[1] + "_blocks/" + g));

			for (l = 0.0025; l < 0.5020; l += 0.0025) {
				for (a = 0.0025; a < 0.5; a += 0.0025) {
					if (a + l > 0.5) {
						continue;
					}
					
					SummaryStatistics[] statsA = new SummaryStatistics[36];
					SummaryStatistics[] statsB = new SummaryStatistics[36];
					SummaryStatistics[] statsL = new SummaryStatistics[36];
					SummaryStatistics[] statsblocksA = new SummaryStatistics[36];
					SummaryStatistics[] statsblocksB = new SummaryStatistics[36];
					SummaryStatistics[] statsblocksL = new SummaryStatistics[36];
					
					for(int count = 0; count < 36; count+=1){
						statsA[count] = new SummaryStatistics();
						statsB[count] = new SummaryStatistics();
						statsL[count] = new SummaryStatistics();
						statsblocksA[count] = new SummaryStatistics();
						statsblocksB[count] = new SummaryStatistics();
						statsblocksL[count] = new SummaryStatistics();
					}
					
					for (int rep = 0; rep < REPS; rep++) {
						double b = 1 - a - l;
						
						int count = 0;
						
						//////////////////////////////////////////
						// honest
						
						statsA[count].addValue(a);
						statsB[count].addValue(b);
						statsL[count].addValue(l);
						statsblocksA[count].addValue((int)(a * ITERATIONS));
						statsblocksB[count].addValue((int)(b * ITERATIONS));
						statsblocksL[count].addValue((int)(l * ITERATIONS));
						count += 1;
						
						Gain hsg = StubbornMining.selfishMining(a, g);
						statsA[count].addValue(hsg.a);
						statsB[count].addValue(hsg.b);
						statsL[count].addValue(hsg.l);
						statsblocksA[count].addValue(hsg.bA);
						statsblocksB[count].addValue((int)(b * hsg.bB / (b + l)));
						statsblocksL[count].addValue((int)(l * hsg.bB / (b + l)));
						count += 1;
						
						// change the rest too
						Gain hsgBlue = StubbornMiningNewL.newL(a, g);
						statsA[count].addValue(hsgBlue.a);
						statsB[count].addValue(hsgBlue.b);
						statsL[count].addValue(hsgBlue.l);
						statsblocksA[count].addValue(hsgBlue.bA);
						statsblocksB[count].addValue((int)( b * hsgBlue.bB / (b + l)));
						statsblocksL[count].addValue((int)( l * hsgBlue.bB / (b + l)));
						count += 1;
						
						Gain hsgGreen = StubbornMiningNewL.newLF(a, g);
						statsA[count].addValue(hsgGreen.a);
						statsB[count].addValue(hsgGreen.b);
						statsL[count].addValue(hsgGreen.l);
						statsblocksA[count].addValue(hsgGreen.bA);
						statsblocksB[count].addValue((int)( b * hsgGreen.bB / (b + l)));
						statsblocksL[count].addValue((int)( l * hsgGreen.bB / (b + l)));
						count += 1;
						
						Gain hsgF = StubbornMining.selfishMiningF(a, g);
						statsA[count].addValue(hsgF.a);
						statsB[count].addValue(hsgF.b);
						statsL[count].addValue(hsgF.l);
						statsblocksA[count].addValue(hsgF.bA);
						statsblocksB[count].addValue(hsgF.bB);
						statsblocksL[count].addValue((int)(l * hsgF.bB / (b + l)));
						count += 1;
						
						Gain hsgMO = StubbornMining.selfishMiningT(a, g);
						statsA[count].addValue(hsgMO.a);
						statsB[count].addValue(hsgMO.b);
						statsL[count].addValue(hsgMO.l);
						statsblocksA[count].addValue(hsgMO.bA);
						statsblocksB[count].addValue(hsgMO.bB);
						statsblocksL[count].addValue((int)(l * hsgMO.bB / (b + l)));
						count += 1;
						
						Gain hsgMOF = StubbornMining.selfishMiningFT(a, g);
						statsA[count].addValue(hsgMOF.a);
						statsB[count].addValue(hsgMOF.b);
						statsL[count].addValue(hsgMOF.l);
						statsblocksA[count].addValue(hsgMOF.bA);
						statsblocksB[count].addValue(hsgMOF.bB);
						statsblocksL[count].addValue((int)(l * hsgMOF.bB / (b + l)));
						count += 1;
						
						Gain hsgMOL = StubbornMiningNewL.newLT(a, g);
						statsA[count].addValue(hsgMOL.a);
						statsB[count].addValue(hsgMOL.b);
						statsL[count].addValue(hsgMOL.l);
						statsblocksA[count].addValue(hsgMOL.bA);
						statsblocksB[count].addValue(hsgMOL.bB);
						statsblocksL[count].addValue((int)(l * hsgMOL.bB / (b + l)));
						count += 1;
						
						Gain hsgMOLF = StubbornMiningNewL.newLFT(a, g);
						statsA[count].addValue(hsgMOLF.a);
						statsB[count].addValue(hsgMOLF.b);
						statsL[count].addValue(hsgMOLF.l);
						statsblocksA[count].addValue(hsgMOLF.bA);
						statsblocksB[count].addValue(hsgMOLF.bB);
						statsblocksL[count].addValue((int)(l * hsgMOLF.bB / (b + l)));
						count += 1;
						
						
						statsA[count].addValue(a);
						statsB[count].addValue(b);
						statsL[count].addValue(l);
						statsblocksA[count].addValue((int)(a * ITERATIONS));
						statsblocksB[count].addValue((int)(b * ITERATIONS));
						statsblocksL[count].addValue((int)(l * ITERATIONS));
						count += 1;
						
						Gain col = EclipseColludeDestroy.eclipseColludeLambda(a, g, l);
						statsA[count].addValue(col.a);
						statsB[count].addValue(col.b);
						statsL[count].addValue(col.l);
						statsblocksA[count].addValue(col.bA);
						statsblocksB[count].addValue(col.bB);
						statsblocksL[count].addValue(col.bL);						
						count += 1;
						
						Gain colBlue = Collude_newL.eclipseColludeL(a, g, l); // C, B
						statsA[count].addValue(colBlue.a);
						statsB[count].addValue(colBlue.b);
						statsL[count].addValue(colBlue.l);
						statsblocksA[count].addValue(colBlue.bA);
						statsblocksB[count].addValue(colBlue.bB);
						statsblocksL[count].addValue(colBlue.bL);
						count += 1;
						
						Gain colGreen = Collude_newL.eclipseColludeLF(a, g, l); // C, G
						statsA[count].addValue(colGreen.a);
						statsB[count].addValue(colGreen.b);
						statsL[count].addValue(colGreen.l);
						statsblocksA[count].addValue(colGreen.bA);
						statsblocksB[count].addValue(colGreen.bB);
						statsblocksL[count].addValue(colGreen.bL);
//						System.out.format("%.4f %.4f %.4f\n", colGreen.a, colGreen.b, colGreen.l);
						count += 1;
						
						Gain colF = Collude_newL.eclipseColludeF(a, g, l);
						statsA[count].addValue(colF.a);
						statsB[count].addValue(colF.b);
						statsL[count].addValue(colF.l);
						statsblocksA[count].addValue(colF.bA);
						statsblocksB[count].addValue(colF.bB);
						statsblocksL[count].addValue(colF.bL);
						count += 1;
						
						Gain colMO = Collude_newL.eclipseColludeT(a, g, l);
						statsA[count].addValue(colMO.a);
						statsB[count].addValue(colMO.b);
						statsL[count].addValue(colMO.l);
						statsblocksA[count].addValue(colMO.bA);
						statsblocksB[count].addValue(colMO.bB);
						statsblocksL[count].addValue(colMO.bL);
						count += 1;
						
						Gain colMOF = Collude_newL.eclipseColludeFT(a, g, l);
						statsA[count].addValue(colMOF.a);
						statsB[count].addValue(colMOF.b);
						statsL[count].addValue(colMOF.l);
						statsblocksA[count].addValue(colMOF.bA);
						statsblocksB[count].addValue(colMOF.bB);
						statsblocksL[count].addValue(colMOF.bL);
						count += 1;
						
						Gain colMOL = Collude_newL.eclipseColludeLT(a, g, l);
						statsA[count].addValue(colMOL.a);
						statsB[count].addValue(colMOL.b);
						statsL[count].addValue(colMOL.l);
						statsblocksA[count].addValue(colMOL.bA);
						statsblocksB[count].addValue(colMOL.bB);
						statsblocksL[count].addValue(colMOL.bL);
						count += 1;
						
						Gain colMOLF = Collude_newL.eclipseColludeLFT(a, g, l);
						statsA[count].addValue(colMOLF.a);
						statsB[count].addValue(colMOLF.b);
						statsL[count].addValue(colMOLF.l);
						statsblocksA[count].addValue(colMOLF.bA);
						statsblocksB[count].addValue(colMOLF.bB);
						statsblocksL[count].addValue(colMOLF.bL);
						count += 1;
						
						//////////////////////////////////////
						// destroy
					
						statsA[count].addValue(a/(1-l));
						statsB[count].addValue(b/(1-l));
						statsL[count].addValue(0.0);
						statsblocksA[count].addValue((int)(a * ITERATIONS));
						statsblocksB[count].addValue((int)(b * ITERATIONS));
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain des = EclipseColludeDestroy.eclipseDestroyLambda(a, g, l);
						statsA[count].addValue(des.a);
						statsB[count].addValue(des.b);
						statsL[count].addValue(des.l);
						statsblocksA[count].addValue(des.bA);
						statsblocksB[count].addValue(des.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desBlue = Destroy_newL.destroyL(a, g, l); // D, B
						statsA[count].addValue(desBlue.a);
						statsB[count].addValue(desBlue.b);
						statsL[count].addValue(desBlue.l);
						statsblocksA[count].addValue(desBlue.bA);
						statsblocksB[count].addValue(desBlue.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desGreen = Destroy_newL.destroyLF(a, g, l); // D, G
						statsA[count].addValue(desGreen.a);
						statsB[count].addValue(desGreen.b);
						statsL[count].addValue(desGreen.l);
						statsblocksA[count].addValue(desGreen.bA);
						statsblocksB[count].addValue(desGreen.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desF = Destroy_newL.destroyF(a, g, l);
						statsA[count].addValue(desF.a);
						statsB[count].addValue(desF.b);
						statsL[count].addValue(desF.l);
						statsblocksA[count].addValue(desF.bA);
						statsblocksB[count].addValue(desF.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desMO = Destroy_newL.destroyT(a, g, l);
						statsA[count].addValue(desMO.a);
						statsB[count].addValue(desMO.b);
						statsL[count].addValue(desMO.l);
						statsblocksA[count].addValue(desMO.bA);
						statsblocksB[count].addValue(desMO.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desMOF = Destroy_newL.destroyFT(a, g, l);
						statsA[count].addValue(desMOF.a);
						statsB[count].addValue(desMOF.b);
						statsL[count].addValue(desMOF.l);
						statsblocksA[count].addValue(desMOF.bA);
						statsblocksB[count].addValue(desMOF.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desMOL = Destroy_newL.destroyLT(a, g, l);
						statsA[count].addValue(desMOL.a);
						statsB[count].addValue(desMOL.b);
						statsL[count].addValue(desMOL.l);
						statsblocksA[count].addValue(desMOL.bA);
						statsblocksB[count].addValue(desMOL.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						Gain desMOLF = Destroy_newL.destroyLFT(a, g, l);
						statsA[count].addValue(desMOLF.a);
						statsB[count].addValue(desMOLF.b);
						statsL[count].addValue(desMOLF.l);
						statsblocksA[count].addValue(desMOLF.bA);
						statsblocksB[count].addValue(desMOLF.bB);
						statsblocksL[count].addValue(0);
						count += 1;
						
						////////////////////////////////////////////
						// DNS
						statsA[count].addValue(a/(1-l));
						statsB[count].addValue(b/(1-l));
						statsL[count].addValue(0);
						statsblocksA[count].addValue((int)(a * ITERATIONS));
						statsblocksB[count].addValue((int)(b * ITERATIONS));
						statsblocksL[count].addValue(0.0);
						count += 1;
						
		
						Gain dns = Dns.destroyIfNoStake(a, g, l);        // DNS, SM
						statsA[count].addValue(dns.a);
						statsB[count].addValue(dns.b);
						statsL[count].addValue(dns.l);
						statsblocksA[count].addValue(dns.bA);
						statsblocksB[count].addValue(dns.bB);
						statsblocksL[count].addValue(dns.bL);
						count += 1;
						
						Gain dnsBlue = DnsNewL.destroyIfNoStakeNewL(a, g, l);// DNS, B
						statsA[count].addValue(dnsBlue.a);
						statsB[count].addValue(dnsBlue.b);
						statsL[count].addValue(dnsBlue.l);
						statsblocksA[count].addValue(dnsBlue.bA);
						statsblocksB[count].addValue(dnsBlue.bB);
						statsblocksL[count].addValue(dnsBlue.bL);
						count += 1;
						
						Gain dnsGreen = DnsNewL.destroyIfNoStakeNewLF(a, g, l); // DNS, G
						statsA[count].addValue(dnsGreen.a);
						statsB[count].addValue(dnsGreen.b);
						statsL[count].addValue(dnsGreen.l);
						statsblocksA[count].addValue(dnsGreen.bA);
						statsblocksB[count].addValue(dnsGreen.bB);
						statsblocksL[count].addValue(dnsGreen.bL);
						count += 1;
						
						Gain dnsF = Dns.destroyIfNoStakeF(a, g, l);
						statsA[count].addValue(dnsF.a);
						statsB[count].addValue(dnsF.b);
						statsL[count].addValue(dnsF.l);
						statsblocksA[count].addValue(dnsF.bA);
						statsblocksB[count].addValue(dnsF.bB);
						statsblocksL[count].addValue(dnsF.bL);
						count += 1;
						
						Gain dnsMO = Dns.destroyIfNoStakeT(a, g, l);
						statsA[count].addValue(dnsMO.a);
						statsB[count].addValue(dnsMO.b);
						statsL[count].addValue(dnsMO.l);
						statsblocksA[count].addValue(dnsMO.bA);
						statsblocksB[count].addValue(dnsMO.bB);
						statsblocksL[count].addValue(dnsMO.bL);
						count += 1;
						
						Gain dnsMOF = Dns.destroyIfNoStakeFT(a, g, l);
						statsA[count].addValue(dnsMOF.a);
						statsB[count].addValue(dnsMOF.b);
						statsL[count].addValue(dnsMOF.l);
						statsblocksA[count].addValue(dnsMOF.bA);
						statsblocksB[count].addValue(dnsMOF.bB);
						statsblocksL[count].addValue(dnsMOF.bL);
						count += 1;
						
						Gain dnsMOL = DnsNewL.destroyIfNoStakeLT(a, g, l);
						statsA[count].addValue(dnsMOL.a);
						statsB[count].addValue(dnsMOL.b);
						statsL[count].addValue(dnsMOL.l);
						statsblocksA[count].addValue(dnsMOL.bA);
						statsblocksB[count].addValue(dnsMOL.bB);
						statsblocksL[count].addValue(dnsMOL.bL);
						count += 1;
						
						Gain dnsMOLF = DnsNewL.destroyIfNoStakeLFT(a, g, l);	
						statsA[count].addValue(dnsMOLF.a);
						statsB[count].addValue(dnsMOLF.b);
						statsL[count].addValue(dnsMOLF.l);
						statsblocksA[count].addValue(dnsMOLF.bA);
						statsblocksB[count].addValue(dnsMOLF.bB);
						statsblocksL[count].addValue(dnsMOLF.bL);
						count += 1;					
						
						// Alpha 10, Beta 20, Lambda 30
//						bw.write(String.format("%.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %d %.4f %.4f %.4f 10\n",
//							a,       hsg.a, hsgBlue.a, hsgGreen.a, hsgF.a, hsgMO.a, hsgMOF.a, hsgMOL.a, hsgMOLF.a, 
//							a,       col.a, colBlue.a, colGreen.a, colF.a, colMO.a, colMOF.a, colMOL.a, colMOLF.a, 
//							a/(1-l), des.a, desBlue.a, desGreen.a, desF.a, desMO.a, desMOF.a, desMOL.a, desMOLF.a,
//							a/(1-l), dns.a, dnsBlue.a, dnsGreen.a, dnsF.a, dnsMO.a, dnsMOF.a, dnsMOL.a, dnsMOLF.a, 
//							rep, a, g, l));
//	
//						double colBeta = 1 - SelfishMining.theoreticalSelfishMining(a + l, g);
//						bw.write(String.format("%.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %d %.4f %.4f %.4f 20\n",
//								b, b * (1 - hsg.a) / (b + l), b * (1 - hsgBlue.a) / (b + l), b * (1 - hsgGreen.a) / (b + l), b * (1 - hsgF.a) / (b + l), b * (1 - hsgMO.a) / (b + l), b * (1 - hsgMOF.a) / (b + l), b * (1 - hsgMOL.a) / (b + l), b * (1 - hsgMOLF.a) / (b + l), 
//								b,       col.b, colBlue.b, colGreen.b, colF.b, colMO.b, colMOF.b, colMOL.b, colMOLF.b,  
//								b/(1-l), des.b, desBlue.b, desGreen.b, desF.b, desMO.b, desMOF.b, desMOL.b, desMOLF.b,
//								b/(1-l), dns.b, dnsBlue.b, dnsGreen.b, dnsF.b, dnsMO.b, dnsMOF.b, dnsMOL.b, dnsMOLF.b, 
//								rep, a, g, l));
//	
//						bw.write(String.format("%.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f |"
//								+ " %d %.4f %.4f %.4f 30\n",
//								l, l * (1 - hsg.a) / (b + l), l * (1 - hsgBlue.a) / (b + l), l * (1 - hsgGreen.a) / (b + l), l * (1 - hsgF.a) / (b + l), l * (1 - hsgMO.a) / (b + l), l * (1 - hsgMOF.a) / (b + l), l * (1 - hsgMOL.a) / (b + l), l * (1 - hsgMOLF.a) / (b + l), 
//								l, col.l, colBlue.l, colGreen.l, colF.l, colMO.l, colMOF.l, colMOL.l, colMOLF.l, 
//								0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
//								0.0, dns.l, dnsBlue.l, dnsGreen.l, dnsF.l, dnsMO.l, dnsMOF.l, dnsMOL.l, dnsMOLF.l, 
//								rep, a, g, l));
						
						// number of blocks by each party in the chain
//						bw2.write(String.format("%d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %.4f %.4f %.4f 10\n",
//								(int)(a * ITERATIONS), hsg.bA, hsgBlue.bA, hsgGreen.bA, hsgF.bA, hsgMO.bA, hsgMOF.bA, hsgMOL.bA, hsgMOLF.bA, 
//								(int)(a * ITERATIONS), col.bA, colBlue.bA, colGreen.bA, colF.bA, colMO.bA, colMOF.bA, colMOL.bA, colMOLF.bA, 
//								(int)(a * ITERATIONS), des.bA, desBlue.bA, desGreen.bA, desF.bA, desMO.bA, desMOF.bA, desMOL.bA, desMOLF.bA,
//								(int)(a * ITERATIONS), dns.bA, dnsBlue.bA, dnsGreen.bA, dnsF.bA, dnsMO.bA, dnsMOF.bA, dnsMOL.bA, dnsMOLF.bA,
//								rep, a, g, l));
//	
//						bw2.write(String.format("%d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %.4f %.4f %.4f 20\n",
//								(int)(b * ITERATIONS), (int)(b * hsg.bB / (b + l)),(int)( b * hsgBlue.bA / (b + l)),(int)( b * hsgGreen.bA / (b + l)), hsgF.bB, hsgMO.bB, hsgMOF.bB, hsgMOL.bB, hsgMOLF.bB, 
//								(int)(b * ITERATIONS), col.bB, colBlue.bB, colGreen.bB, colF.bB, colMO.bB, colMOF.bB, colMOL.bB, colMOLF.bB, 
//								(int)(b * ITERATIONS), des.bB, desBlue.bB, desGreen.bB, desF.bB, desMO.bB, desMOF.bB, desMOL.bB, desMOLF.bB,
//								(int)(b * ITERATIONS), dns.bB, dnsBlue.bB, dnsGreen.bB, dnsF.bB, dnsMO.bB, dnsMOF.bB, dnsMOL.bB, dnsMOLF.bB, 
//								rep, a, g, l));
//						bw2.write(String.format("%d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %d %d %d %d %d %d %d %d |"
//								+ " %d %.4f %.4f %.4f 30\n",
//								(int)(l * ITERATIONS), (int)(l * hsg.bB / (b + l)), (int)(l * hsgBlue.bB / (b + l)), (int)(l * hsgGreen.bB / (b + l)),  (int)(l * hsgF.bB / (b + l)), (int)(l * hsgMO.bB / (b + l)), (int)(l * hsgMOF.bB / (b + l)), (int)(l * hsgMOL.bB / (b + l)), (int)(l * hsgMOLF.bB / (b + l)), 
//								(int)(l * ITERATIONS), col.bL, colBlue.bL, colGreen.bL, colF.bL, colMO.bL, colMOF.bL, colMOL.bL, colMOLF.bL, 
//								0, 0, 0,0,0,0,0,0,0,
//								0, dns.bL, dnsBlue.bL, dnsGreen.bL, dnsF.bL, dnsMO.bL, dnsMOF.bL, dnsMOL.bL, dnsMOLF.bL,
//								rep, a, g, l));
					}
					
					
					double[] means = new double[36];
					double[] sems = new double[36];
					int maxidx = 0;
					int secmaxidx = 0;
					
					int count = 0;
					for (SummaryStatistics s : statsA){
						double sem = StubbornMining.calcSEM(s, 0.95);
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
			        	maxidx = 36;
			        }
			        
			        for(count = 0; count < 36; count += 1){
			        	bw.write(String.format("%.4f ", means[count]));
			        	if ((count + 1) % 9 == 0 )
			        		bw.write("| ");
			        }
			        bw.write(String.format("%.4f %.4f %.4f %d 10\n", a,g,l,maxidx)); //Alpha
			        
			        // BETA 
			        
			        means = new double[36];
					sems = new double[36];
					maxidx = 0;
					secmaxidx = 0;
					
					count = 0;
					for (SummaryStatistics s : statsB){
						double sem = StubbornMining.calcSEM(s, 0.95);
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
					
			        tDist = new TDistribution(REPS - 1);
			        critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidence) / 2);
		            // Calculate confidence interval
		            
			        
			        if (means[maxidx] - means[secmaxidx] < critVal * Math.sqrt(sems[maxidx] * sems[maxidx] + sems[secmaxidx] * sems[secmaxidx])){
			        	maxidx = 36;
			        }
			        for(count = 0; count < 36; count += 1){
			        	bw.write(String.format("%.4f ", means[count]));
			        	if ((count + 1) % 9 == 0 )
			        		bw.write("| ");
			        }
			        bw.write(String.format("%.4f %.4f %.4f %d 20\n", a,g,l,maxidx)); //BETA
			        
			        // LAMBDA
			        
			        means = new double[36];
					sems = new double[36];
					maxidx = 0;
					secmaxidx = 0;
					
					count = 0;
					for (SummaryStatistics s : statsL){
						double sem = StubbornMining.calcSEM(s, 0.95);
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
					
			        tDist = new TDistribution(REPS - 1);
			        critVal = tDist.inverseCumulativeProbability(1.0 - (1 - confidence) / 2);
		            // Calculate confidence interval
		            
			        
			        if (means[maxidx] - means[secmaxidx] < critVal * Math.sqrt(sems[maxidx] * sems[maxidx] + sems[secmaxidx] * sems[secmaxidx])){
			        	maxidx = 36;
			        }
			        for(count = 0; count < 36; count += 1){
			        	bw.write(String.format("%.4f ", means[count]));
			        	if ((count + 1) % 9 == 0 )
			        		bw.write("| ");
			        }
			        
			        bw.write(String.format("%.4f %.4f %.4f %d 30\n", a,g,l,maxidx)); //Lambda
			        
			        
			        /////////////////////////////////////
			        // Blocks
			        
			        // Alpha
			        means = new double[36];					
					count = 0;
					for (SummaryStatistics s : statsblocksA){
				        means[count] = s.getMean();				        
				        count += 1;
					}
			        for(count = 0; count < 36; count += 1){
			        	bw2.write(String.format("%.4f ", means[count]));
			        	if ((count + 1) % 9 == 0 )
			        		bw2.write("| ");
			        }
			        bw2.write(String.format("%.4f %.4f %.4f 10\n", a,g,l)); // ALPHA
			        
			        // beta
			        means = new double[36];					
					count = 0;
					for (SummaryStatistics s : statsblocksB){
				        means[count] = s.getMean();				        
				        count += 1;
					}
			        for(count = 0; count < 36; count += 1){
			        	bw2.write(String.format("%.4f ", means[count]));
			        	if ((count + 1) % 9 == 0 )
			        		bw2.write("| ");
			        }
			        bw2.write(String.format("%.4f %.4f %.4f 20\n", a,g,l)); // BETA
			        
			        // lambda
			        means = new double[36];					
					count = 0;
					for (SummaryStatistics s : statsblocksL){
				        means[count] = s.getMean();				        
				        count += 1;
					}
			        for(count = 0; count < 36; count += 1){
			        	bw2.write(String.format("%.4f ", means[count]));
			        	if ((count + 1) % 9 == 0 )
			        		bw2.write("| ");
			        }
			        bw2.write(String.format("%.4f %.4f %.4f 30\n", a,g,l)); // LAMBDA
			        
			       
			    
				}
			}
			bw.flush();
			bw.close();
			bw2.flush();
			bw2.close();
	}

	static Party getNextBlockParty(double a, double l, double g) {
		GAMMABETA = false;
		double nextDouble = rando.nextDouble();
		if (nextDouble < a) {
			return Party.ALPHA;
		} else if (nextDouble < a + l) {
			return Party.LAMBDA;
		} else if (nextDouble < a + l + g * (1 - (a + l))) {
			GAMMABETA = true;
			return Party.BETA;
		} else {
			return Party.BETA;
		}
	}
}
