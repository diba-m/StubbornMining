package selfish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

import selfish.Eclipse.Party;

public class Dns {

	// DNS + Honest = Destroy

	// DNS, SM
	public static Gain destroyIfNoStake(double a, double g, double l) throws IOException {
		/*
		 * Destroy if lambda leads. 
		 * Start colluding when you lead.
		 */		

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Gain gain = new Gain();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", x = " + x + ", y = " + y + ", ta = " + ta + ", tl = " + tl + ", tb = " + tb );
			if (printAll == true)
				br.readLine();
			party = Eclipse.getNextBlockParty(a, l, g);

			if (Party.LAMBDA.equals(party)) {
				tl++;
				if (printAll == true)
					System.out.println(i + ") L");
			} else if (Party.ALPHA.equals(party)) {
				ta++;
				if (printAll == true)
					System.out.println(i + ") A");
			} else {
				tb++;
				if (printAll == true)
					System.out.println(i + ") B");
			}

			if (state == 4) {
				gain.increaseS4();
				// All together
				if (i > Eclipse.ITERATIONS - 100)
					break;
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
				}
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) { 
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get destroyed
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+="+ tb);
						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" +tb);
						ta = tb = 0; // a and b sync.
						//						tl--;
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				// y = 0 always
				if (x == 0) {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						state = 4;
						// gains
						lambdaGained += tl;
						blocksGained += ta;
						if (printAll == true){
							System.out.println("Lambda+" + tl);
							System.out.println("Alpha+" + ta);
						}
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						state = 4;
						// gains
						if (Eclipse.GAMMABETA == false){
							publicGained += tb;
							if (printAll == true)
								System.out.println("Public through (1-Gamma)beta+" +tb);
						}
						else{
							blocksGained += ta;
							lambdaGained += tl;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);
								System.out.println("Lambda+=" + tl);
							}
						}

						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (x == 2 && Party.BETA.equals(party)) { // selfish mining stuff here
						state = 4;
						// gains
						blocksGained += ta;
						lambdaGained += tl;
						if (printAll == true){
							System.out.println("Lambda+" + tl);
							System.out.println("Alpha+" + ta);
						}
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
							x++;
						} else {
							x--;
						}
					}
				}
			} else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x == 2 && Party.BETA.equals(party) ) {
					state = 2;
					// gains
					blocksGained += ta;
					if (printAll == true)
						System.out.println("Alpha+" + ta);
					tb = 0; // a and b sync up
					ta = 0;
					x = 0;
				} else if (x > 0 && Party.BETA.equals(party)) {
					x--;
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {
						state = 4;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += tb;
							if (printAll == true)
								System.out.println("Public through (1-Gamma)beta+" +tb);
						} else {
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
						}

						x = y = 0;
						tl = ta = tb = 0;
					} else {
						state = 2;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += tb;
							if (printAll == true)
								System.out.println("Public through (1-Gamma)beta+" +tb);
						} else {
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true) {
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
						}

						tb = ta = 0;
						y--;
					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					if (x == 0){
						state = 2;
						x = 0;
						y--;
						blocksGained += ta;
						if (printAll == true)
							System.out.println("Alpha+" + ta);
						ta = 0;
						tb = 0;
					}
					else{
						y--;
						x++;
					}
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					if (x == 0) {
						state = 4;
						// gains
						blocksGained += ta;
						if (printAll == true)
							System.out.println("Alpha+" + ta);
						ta = tb = tl = 0;
						x = y = 0;
					} else {
						state = 3;
						x++;
						tl = 0;
					}
				}
			}
		}

		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained / (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}

	public static Gain destroyIfNoStakeF(double a, double g, double l) throws IOException {
		/*
		 * Destroy if lambda leads. 
		 * Start colluding when you lead.
		 */		

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Gain gain = new Gain();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", x = " + x + ", y = " + y + ", ta = " + ta + ", tl = " + tl + ", tb = " + tb );
			if (printAll == true)
				br.readLine();
			party = Eclipse.getNextBlockParty(a, l, g);

			if (Party.LAMBDA.equals(party)) {
				tl++;
				if (printAll == true)
					System.out.println(i + ") L");
			} else if (Party.ALPHA.equals(party)) {
				ta++;
				if (printAll == true)
					System.out.println(i + ") A");
			} else {
				tb++;
				if (printAll == true)
					System.out.println(i + ") B");
			}

			if (state == 4) {
				gain.increaseS4();
				// All together
				if (i > Eclipse.ITERATIONS - 100)
					break;
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
				}
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) { 
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get destroyed
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+="+ tb);
						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" +tb);
						ta = tb = 0; // a and b sync.
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				if (x == 0) {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						x++;
					} else {
						state = 4;
						// gains
						if (Eclipse.GAMMABETA == false){
							publicGained += tb;
							if (printAll == true)
								System.out.println("Public through (1-Gamma)beta+" +tb);
						}
						else{
							blocksGained += ta;
							lambdaGained += tl;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);
								System.out.println("Lambda+=" + tl);
							}
						}

						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (x == 2 && Party.BETA.equals(party)) { // selfish mining stuff here
						state = 4;
						// gains
						blocksGained += ta;
						lambdaGained += tl;
						if (printAll == true){
							System.out.println("Lambda+" + tl);
							System.out.println("Alpha+" + ta);
						}
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
							x++;
						} else {
							x--;
						}
					}
				}
			} else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x == 2 && Party.BETA.equals(party) ) {
					state = 2;
					// gains
					blocksGained += ta;
					if (printAll == true)
						System.out.println("Alpha+" + ta);
					tb = 0; // a and b sync up
					ta = 0;
					x = 0;
				} else if (x > 0 && Party.BETA.equals(party)) {
					x--;
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {
						state = 4;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += tb;
							if (printAll == true)
								System.out.println("Public through (1-Gamma)beta+" +tb);
						} else {
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
						}

						x = y = 0;
						tl = ta = tb = 0;
					} else {
						state = 2;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += tb;
							if (printAll == true)
								System.out.println("Public through (1-Gamma)beta+" +tb);
						} else {
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true) {
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
						}

						tb = ta = 0;
						y--;
					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					y--;
					x++;
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					state = 3;
					x++;
					tl = 0;
				}
			}
		}

		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained / (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}

	public static Gain destroyIfNoStakeT(double a, double g, double l) throws IOException {
		/*
		 * Destroy if lambda leads. 
		 * Start colluding when you lead.
		 */		

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Gain gain = new Gain();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", x = " + x + ", y = " + y + ", ta = " + ta + ", tl = " + tl + ", tb = " + tb );
			if (printAll == true)
				br.readLine();
			party = Eclipse.getNextBlockParty(a, l, g);

			if (Party.LAMBDA.equals(party)) {
				tl++;
				if (printAll == true)
					System.out.println(i + ") L");
			} else if (Party.ALPHA.equals(party)) {
				ta++;
				if (printAll == true)
					System.out.println(i + ") A");
			} else {
				tb++;
				if (printAll == true)
					System.out.println(i + ") B");
			}

			if (state == 4) {
				gain.increaseS4();
				// All together
				if (i > Eclipse.ITERATIONS - 100)
					break;
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
				}
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) { 
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get destroyed
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+="+ tb);
						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" +tb);
						ta = tb = 0; // a and b sync.
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				if (x == 0) {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						state = 4;
						// gains
						lambdaGained += tl;
						blocksGained += ta;
						if (printAll == true){
							System.out.println("Lambda+" + tl);
							System.out.println("Alpha+" + ta);
						}
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						// gains
						if (Eclipse.GAMMABETA == false){
							// take to minus one state
							state = -13;
							x --;
						}
						else{
							state = 4;
							blocksGained += ta;
							lambdaGained += tl;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);
								System.out.println("Lambda+=" + tl);
							}
							x = y = 0;
							tl = ta = tb = 0;
						}


					}
				} else {
					if (x == 2 && Party.BETA.equals(party)) { // selfish mining stuff here
						state = 4;
						// gains
						blocksGained += ta;
						lambdaGained += tl;
						if (printAll == true){
							System.out.println("Lambda+" + tl);
							System.out.println("Alpha+" + ta);
						}
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
							x++;
						} else {
							x--;
						}
					}
				}
			} 
			else if (state == -13){
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)){
					state = -1300;
					x++;
				}
				else if (Party.BETA.equals(party)){
					state = 4;
					publicGained += tb;
					if (printAll == true)
						System.out.println("Public+=" +tb);
					ta = tb = tl = 0; 
					x = 0;
					y = 0;
				}
			}
			else if (state == -1300){
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)){
					state = 4;
					blocksGained += ta;
					lambdaGained += tl;
					if (printAll == true){
						System.out.println("Alpha+="+ta);
						System.out.println("Lambda+=" + tl);
					}
					x = y = 0;
					tl = ta = tb = 0;
				}
				else{
					state = -13;
					x--;
				}
			}			
			else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x == 2 && Party.BETA.equals(party) ) {
					state = 2;
					// gains
					blocksGained += ta;
					if (printAll == true)
						System.out.println("Alpha+" + ta);
					//tl -= ta;
					tb = 0; // a and b sync up
					ta = 0;
					x = 0;
				} else if (x > 0 && Party.BETA.equals(party)) {
					x--;
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {

						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state 
							state = -11;
							x--;
						} else {
							state = 4;
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
							x = y = 0;
							tl = ta = tb = 0;
						}
					} else {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							state = -11;	
							x--;
						} else {
							state = 2;
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true) {
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
							tb = ta = 0;
							y--;
						}


					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					if (x == 0){
						state = 2;
						x = 0;
						y--;
						blocksGained += ta;
						if (printAll == true)
							System.out.println("Alpha+" + ta);
						ta = 0;
						tb = 0;
					}
					else{
						y--;
						x++;
					}
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					if (x == 0) {
						state = 4;
						// gains
						blocksGained += ta;
						if (printAll == true)
							System.out.println("Alpha+" + ta);
						ta = tb = tl = 0;
						x = y = 0;
					} else {
						state = 3;
						x++;
						tl = 0;
					}
				}
			}
			else if (state == -11){
				if (Party.ALPHA.equals(party)){
					if (y == 0){
						state = -1300;
						tl = 0;
						x++;
					}
					else{
						state = -1100;
						y--;
						x++;
					}

				}
				else if (Party.BETA.equals(party)){
					if (y == 0){
						state = 4;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public += " +tb);
						ta = tb = tl = 0;
						x = 0;
						y = 0;
					}
					else{
						state = 2;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public += " +tb);
						ta = tb = 0;
						y--;	
						x = 0;
					}
				}
				else{
					y++;
				}
			}
			else if (state == -1100){
				if (Party.ALPHA.equals(party)){
					if (y==0){
						state = 4;
						blocksGained += ta;
						if (printAll == true)
							System.out.println("Alpha+" + ta);
						ta = tb = tl = 0;
						x = 0;
						y = 0;
					}
					else{
						state = 2;
						blocksGained += ta;
						if (printAll == true)
							System.out.println("Alpha+" + ta);
						ta = tb = 0;
						y--;
						x = 0;
					}

				}
				else if (Party.BETA.equals(party)){
					state = -11;
					x--;
				}
				else{
					y++;
				}
			}
		}

		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained / (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}

	public static Gain destroyIfNoStakeFT(double a, double g, double l) throws IOException {
		/*
		 * Destroy if lambda leads. 
		 * Start colluding when you lead.
		 */		

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Gain gain = new Gain();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", x = " + x + ", y = " + y + ", ta = " + ta + ", tl = " + tl + ", tb = " + tb );
			if (printAll == true)
				br.readLine();
			party = Eclipse.getNextBlockParty(a, l, g);

			if (Party.LAMBDA.equals(party)) {
				tl++;
				if (printAll == true)
					System.out.println(i + ") L");
			} else if (Party.ALPHA.equals(party)) {
				ta++;
				if (printAll == true)
					System.out.println(i + ") A");
			} else {
				tb++;
				if (printAll == true)
					System.out.println(i + ") B");
			}

			if (state == 4) {
				gain.increaseS4();
				// All together
				if (i > Eclipse.ITERATIONS - 100)
					break;
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
				}
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) { 
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get destroyed
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+="+ tb);
						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" +tb);
						ta = tb = 0; // a and b sync.
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				if (x == 0) {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						x++;
					} else {
						// gains
						if (Eclipse.GAMMABETA == false){
							// take to minus one state
							state = -13;
							x--;
						}
						else{
							state = 4;
							blocksGained += ta;
							lambdaGained += tl;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);
								System.out.println("Lambda+=" + tl);
							}
							x = y = 0;
							tl = ta = tb = 0;
						}


					}
				} else {
					if (x == 2 && Party.BETA.equals(party)) { // selfish mining stuff here
						state = 4;
						// gains
						blocksGained += ta;
						lambdaGained += tl;
						if (printAll == true){
							System.out.println("Lambda+" + tl);
							System.out.println("Alpha+" + ta);
						}
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
							x++;
						} else {
							x--;
						}
					}
				}
			} 
			else if (state == -13){
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)){
					state = -1300;
					x++;
				}
				else if (Party.BETA.equals(party)){
					state = 4;
					publicGained += tb;
					if (printAll == true)
						System.out.println("Public+=" +tb);
					ta = tb = tl = 0; 
					x = 0;
					y = 0;
				}
			}
			else if (state == -1300){
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)){
					state = 3;
					x++;
				}
				else{
					state = -13;
					x--;
				}
			}			
			else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x == 2 && Party.BETA.equals(party) ) {
					state = 2;
					// gains
					blocksGained += ta;
					if (printAll == true)
						System.out.println("Alpha+" + ta);
					//tl -= ta;
					tb = 0; // a and b sync up
					ta = 0;
					x = 0;
				} else if (x > 0 && Party.BETA.equals(party)) {
					x--;
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {

						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state 
							state = -11;
							x--;
						} else {
							state = 4;
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true){
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
							x = y = 0;
							tl = ta = tb = 0;
						}
					} else {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							state = -11;	
							x--;
						} else {
							state = 2;
							blocksGained += ta;
							publicGained += 1;
							if (printAll == true) {
								System.out.println("Public through gamma-beta+" +tb);
								System.out.println("Alpha+="+ta);

							}
							tb = ta = 0;
							y--;
						}


					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					y--;
					x++;
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					state = 3;
					x++;
					tl = 0;
				}
			}
			else if (state == -11){
				if (Party.ALPHA.equals(party)){
					if (y == 0){
						state = -1300;
						tl = 0;
						x++;
					}
					else{
						state = -1100;
						y--;
						x++;
					}

				}
				else if (Party.BETA.equals(party)){
					if (y == 0){
						state = 4;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public += " +tb);
						ta = tb = tl = 0;
						x = 0;
						y = 0;
					}
					else{
						state = 2;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public += " +tb);
						ta = tb = 0;
						y--;	
						x = 0;
					}
				}
				else{
					y++;
				}
			}
			else if (state == -1100){
				if (Party.ALPHA.equals(party)){
					if (y==0){
						state = 3;
						x++;
						y = 0;
						tl = 0;
					}
					else{
						state = 1;
						x++;
						y--;
					}

				}
				else if (Party.BETA.equals(party)){
					state = -11;
					x--;
				}
				else{
					y++;
				}
			}
		}

		gain.setAlpha(1.0 * blocksGained / (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained / (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained / (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}
}
