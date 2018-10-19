package selfish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

import selfish.Eclipse.Party;

public class DnsNewL {

	public static Gain destroyIfNoStakeNewL(double a, double g, double l)
			throws IOException {
		/*
		 * Destroy if lambda leads. Start colluding when you lead. Use blue
		 * selfish mining strategy.
		 */

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with
									// public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		boolean isPrime = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		State lastState4 = null;

		Gain gain = new Gain();

		Queue<Party> privateChain = new LinkedList<Party>();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", isPrime = " + isPrime + ", x = "
						+ x + ", y = " + y + ", ta = " + ta + ", tl = " + tl
						+ ", tb = " + tb);
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
				if (printAll == true) {
					if (Eclipse.GAMMABETA == false)
						System.out.println(i + ") B; 1-GB");
					else
						System.out.println(i + ") B; GB");
				}
			}

			if (state == 4) {
				gain.increaseS4();
				// All together
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
					isPrime = false;
					privateChain.clear();
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
					isPrime = false;
					privateChain.add(party);
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
					isPrime = false;
					privateChain.clear();
				}
				lastState4 = new State(a, g, blocksGained, publicGained,
						lambdaGained, 0, isPrime, state);
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get
								// destroyed
						privateChain.add(party);
						isPrime = false;
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						x = y = 0;
						tl = ta = tb = 0;
						isPrime = false;
						privateChain.clear();
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
						privateChain.add(party);
						isPrime = false;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						ta = tb = 0; // a and b sync.
						isPrime = false;
						privateChain.clear();
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				// y = 0 always
				if (x == 0) { // equivalent to 0'
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						state = 4;
						// gains
						privateChain.add(party);

						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;

						// lambdaGained += tl;
						// blocksGained += ta;
						// if (printAll == true){
						// System.out.println("Lambda+" + tl);
						// System.out.println("Alpha+" + ta);
						// }
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						state = 4;
						isPrime = false;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += privateChain.size() + 1;
							privateChain.clear();
							if (printAll == true)
								System.out
										.println("Public through (1-Gamma)beta+"
												+ tb);
						} else {
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();

							// blocksGained += ta;
							// lambdaGained += tl;
							// publicGained += 1;
							// if (printAll == true){
							// System.out.println("Public through gamma-beta+"
							// +tb);
							// System.out.println("Alpha+="+ta);
							// System.out.println("Lambda+=" + tl);
							// }
						}

						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						x++;
						privateChain.add(party);
					} else {
						if (!isPrime) {
							x--;
							isPrime = true;
						} else {
							if (Eclipse.GAMMABETA == true) {
								x--;
								while (privateChain.size() > x + 1) {
									Party p = privateChain.poll();
									if (Party.ALPHA.equals(p)) {
										if (printAll == true)
											System.out.println("Alpha+=1");
										blocksGained += 1;
									} else {
										if (printAll == true)
											System.out.println("Lambda+=1");
										lambdaGained += 1;
									}
								}
							} else {
								x--;
							}
						}

					}
				}
			} else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x > 0 && Party.BETA.equals(party)) {
					if (!isPrime) {
						x--;
						isPrime = true;
					} else {
						if (Eclipse.GAMMABETA == true) {
							x--;
							while (privateChain.size() > x + 1) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
						} else {
							x--;
						}
					}
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {
						state = 4;
						isPrime = false;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += privateChain.size() + 1;
							privateChain.clear();
							if (printAll == true)
								System.out
										.println("Public through (1-Gamma)beta+"
												+ tb);
						} else {
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();

						}

						x = y = 0;
						tl = ta = tb = 0;
					} else {
						state = 2;
						isPrime = false;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += privateChain.size() + 1;
							privateChain.clear();
							if (printAll == true)
								System.out
										.println("Public through (1-Gamma)beta+"
												+ tb);
						} else {
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();

						}

						// tl -= tb;
						tb = ta = 0;
						y--;
					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					if (x == 0) {
						state = 2;
						x = 0;
						y--;

						privateChain.add(party);

						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;

						ta = 0;
						tb = 0;
					} else {
						y--;
						x++;
						privateChain.add(party);
					}
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					if (x == 0) {
						state = 4;
						// gains
						privateChain.add(party);

						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;

						ta = tb = tl = 0;
						x = y = 0;
					} else {
						state = 3;
						privateChain.add(party);
						x++;
						tl = 0;
					}
				}
			}
		}
		// System.out.println("done! " + 1.0 * lastState4.blocksGained /
		// (lastState4.publicGained + lastState4.blocksGained +
		// lastState4.lambdaGained));
		gain.setAlpha(1.0
				* lastState4.blocksGained
				/ (lastState4.publicGained + lastState4.blocksGained + lastState4.lambdaGained));
		gain.setLambda(1.0
				* lastState4.lambdaGained
				/ (lastState4.publicGained + lastState4.blocksGained + lastState4.lambdaGained));
		gain.setBeta(1.0
				* lastState4.publicGained
				/ (lastState4.publicGained + lastState4.blocksGained + lastState4.lambdaGained));
		gain.setBlocks(lastState4.blocksGained, lastState4.publicGained,
				lastState4.lambdaGained);
		return gain;
	}

	public static Gain destroyIfNoStakeNewLF(double a, double g, double l)
			throws IOException {
		/*
		 * Destroy if lambda leads. Start colluding when you lead. Use blue
		 * selfish mining strategy.
		 */

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with
									// public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		boolean isPrime = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		State lastState4 = null;

		Gain gain = new Gain();

		Queue<Party> privateChain = new LinkedList<Party>();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", isPrime = " + isPrime + ", x = "
						+ x + ", y = " + y + ", ta = " + ta + ", tl = " + tl
						+ ", tb = " + tb);
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
				if (printAll == true) {
					if (Eclipse.GAMMABETA == false)
						System.out.println(i + ") B; 1-GB");
					else
						System.out.println(i + ") B; GB");
				}
			}

			if (state == 4) {
				gain.increaseS4();
				// All together
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
					isPrime = false;
					privateChain.clear();
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
					isPrime = false;
					privateChain.add(party);
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
					isPrime = false;
					privateChain.clear();
				}
				lastState4 = new State(a, g, blocksGained, publicGained,
						lambdaGained, 0, isPrime, state);
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get
								// destroyed
						privateChain.add(party);
						isPrime = false;
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						x = y = 0;
						tl = ta = tb = 0;
						isPrime = false;
						privateChain.clear();
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
						privateChain.add(party);
						isPrime = false;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						ta = tb = 0; // a and b sync.
						isPrime = false;
						privateChain.clear();
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				// y = 0 always
				if (x == 0) { // equivalent to 0'
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						// gains
						privateChain.add(party);
						x++;
					} else {
						state = 4;
						isPrime = false;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += privateChain.size() + 1;
							privateChain.clear();
							if (printAll == true)
								System.out
										.println("Public through (1-Gamma)beta+"
												+ tb);
						} else {
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();
						}

						x = y = 0;
						tl = ta = tb = 0;
					}
				} else {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						x++;
						privateChain.add(party);
					} else {
						if (!isPrime) {
							x--;
							isPrime = true;
						} else {
							if (Eclipse.GAMMABETA == true) {
								x--;
								while (privateChain.size() > x + 1) {
									Party p = privateChain.poll();
									if (Party.ALPHA.equals(p)) {
										if (printAll == true)
											System.out.println("Alpha+=1");
										blocksGained += 1;
									} else {
										if (printAll == true)
											System.out.println("Lambda+=1");
										lambdaGained += 1;
									}
								}
							} else {
								x--;
							}
						}

					}
				}
			} else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x > 0 && Party.BETA.equals(party)) {
					if (!isPrime) {
						x--;
						isPrime = true;
					} else {
						if (Eclipse.GAMMABETA == true) {
							x--;
							while (privateChain.size() > x + 1) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
						} else {
							x--;
						}
					}
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {
						state = 4;
						isPrime = false;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += privateChain.size() + 1;
							privateChain.clear();
							if (printAll == true)
								System.out
										.println("Public through (1-Gamma)beta+"
												+ tb);
						} else {
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();

						}

						x = y = 0;
						tl = ta = tb = 0;
					} else {
						state = 2;
						isPrime = false;
						// gains
						if (Eclipse.GAMMABETA == false) {
							publicGained += privateChain.size() + 1;
							privateChain.clear();
							if (printAll == true)
								System.out
										.println("Public through (1-Gamma)beta+"
												+ tb);
						} else {
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();

						}

						// tl -= tb;
						tb = ta = 0;
						y--;
					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					y--;
					x++;
					privateChain.add(party);
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					state = 3;
					privateChain.add(party);
					x++;
					tl = 0;
				}
			}
		}
		// System.out.println("done! " + 1.0 * lastState4.blocksGained /
		// (lastState4.publicGained + lastState4.blocksGained +
		// lastState4.lambdaGained));
		gain.setAlpha(1.0
				* lastState4.blocksGained
				/ (lastState4.publicGained + lastState4.blocksGained + lastState4.lambdaGained));
		gain.setLambda(1.0
				* lastState4.lambdaGained
				/ (lastState4.publicGained + lastState4.blocksGained + lastState4.lambdaGained));
		gain.setBeta(1.0
				* lastState4.publicGained
				/ (lastState4.publicGained + lastState4.blocksGained + lastState4.lambdaGained));
		gain.setBlocks(lastState4.blocksGained, lastState4.publicGained,
				lastState4.lambdaGained);
		return gain;
	}

	public static Gain destroyIfNoStakeLT(double a, double g, double l)
			throws IOException {
		/*
		 * Destroy if lambda leads. Start colluding when you lead.
		 */

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with
									// public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean printAll = false;
		boolean isPrime = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Gain gain = new Gain();

		Queue<Party> privateChain = new LinkedList<Party>();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", x = " + x + ", y = " + y
						+ ", ta = " + ta + ", tl = " + tl + ", tb = " + tb);
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
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
					isPrime = false;
					privateChain.clear();
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
					isPrime = false;
					privateChain.add(party);
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
					isPrime = false;
					privateChain.clear();
				}
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get
								// destroyed
						privateChain.add(party);
						isPrime = false;
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						x = y = 0;
						tl = ta = tb = 0;
						isPrime = false;
						privateChain.clear();
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
						privateChain.add(party);
						isPrime = false;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						ta = tb = 0; // a and b sync.
						isPrime = false;
						privateChain.clear();
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				// y = 0 always
				if (x == 0) {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						state = 4;
						// gains
						privateChain.add(party);

						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;
						x = y = 0;
						tl = ta = tb = 0;
					} else {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							// SRIJAN NEW
							state = -13;
							x--;
							isPrime = true; // is already true
						} else {
							state = 4;
							isPrime = false;
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();
							x = y = 0;
							tl = ta = tb = 0;
						}
					}
				} else {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						x++;
						privateChain.add(party);
					} else {
						if (!isPrime) {
							x--;
							isPrime = true;
						} else {
							if (Eclipse.GAMMABETA == true) {
								x--;
								while (privateChain.size() > x + 1) {
									Party p = privateChain.poll();
									if (Party.ALPHA.equals(p)) {
										if (printAll == true)
											System.out.println("Alpha+=1");
										blocksGained += 1;
									} else {
										if (printAll == true)
											System.out.println("Lambda+=1");
										lambdaGained += 1;
									}
								}
							} else {
								x--;
							}
						}
					}
					// }
				}
			} else if (state == -13) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)) {
					state = -1300;
					x++;
					privateChain.add(party);
				} else if (Party.BETA.equals(party)) {
					state = 4;
					isPrime = false;
					publicGained += privateChain.size() + 1;
					privateChain.clear();
					if (printAll == true)
						System.out
								.println("Public through (1-Gamma)beta+" + tb);
					ta = tb = tl = 0;
					x = 0;
					y = 0;
				}
			} else if (state == -1300) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)) {
					state = 4;
					privateChain.add(party);
					Party p = privateChain.poll();
					while (p != null) {
						if (Party.ALPHA.equals(p)) {
							if (printAll == true)
								System.out.println("Alpha+=1");
							blocksGained += 1;
						} else {
							if (printAll == true)
								System.out.println("Lambda+=1");
							lambdaGained += 1;
						}
						p = privateChain.poll();
					}
					privateChain.clear();
					isPrime = false;
					x = y = 0;
					tl = ta = tb = 0;
				} else {
					state = -13;
					x--;
				}
			} else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x > 0 && Party.BETA.equals(party)) {
					if (!isPrime) {
						x--;
						isPrime = true;
					} else {
						if (Eclipse.GAMMABETA == true) {
							x--;
							while (privateChain.size() > x + 1) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
						} else {
							x--;
						}
					}
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							// SRIJAN NEW
							state = -11;
							x--;
							isPrime = true;
						} else {
							state = 4;
							isPrime = false;
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							privateChain.clear();
							x = y = 0;
							tl = ta = tb = 0;
						}
					} else {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							// SRIJAN NEW
							state = -11;
							x--;
							isPrime = true;
						} else {
							state = 2;
							isPrime = false;

							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							tb = ta = 0;
							y--;
						}
					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					if (x == 0) {
						state = 2;
						x = 0;
						y--;

						privateChain.add(party);

						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;

						ta = 0;
						tb = 0;
					} else {
						y--;
						x++;
						privateChain.add(party);
					}
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					if (x == 0) {
						state = 4;
						privateChain.add(party);
						Party p = privateChain.poll();
						// gains
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;
						ta = tb = tl = 0;
						x = y = 0;
					} else {
						state = 3;
						privateChain.add(party);
						x++;
						tl = 0;
					}
				}
			} else if (state == -11) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party)) {
					privateChain.add(party);
					if (y == 0) {
						state = -1300;
						tl = 0;
						x++;
					} else {
						state = -1100;
						y--;
						x++;
					}

				} else if (Party.BETA.equals(party)) {
					if (y == 0) {
						state = 4;
						isPrime = false;
						publicGained += privateChain.size() + 1;
						privateChain.clear();
						if (printAll == true)
							System.out.println("Public through (1-Gamma)beta+"
									+ tb);
						ta = tb = tl = 0;
						x = 0;
						y = 0;
					} else {
						state = 2;
						isPrime = false;
						publicGained += privateChain.size() + 1;
						privateChain.clear();
						if (printAll == true)
							System.out.println("Public through (1-Gamma)beta+"
									+ tb);
						ta = tb = 0;
						y--;
						x = 0;
					}
				} else {
					y++;
				}
			} else if (state == -1100) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party)) {
					if (y == 0) {
						state = 4;

						privateChain.add(party);
						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;
						ta = tb = tl = 0;
						x = 0;
						y = 0;
					} else {
						state = 2;
						privateChain.add(party);
						Party p = privateChain.poll();
						while (p != null) {
							if (Party.ALPHA.equals(p)) {
								if (printAll == true)
									System.out.println("Alpha+=1");
								blocksGained += 1;
							} else {
								if (printAll == true)
									System.out.println("Lambda+=1");
								lambdaGained += 1;
							}
							p = privateChain.poll();
						}
						privateChain.clear();
						isPrime = false;
						ta = tb = 0;
						y--;
						x = 0;
					}

				} else if (Party.BETA.equals(party)) {
					state = -11;
					x--;
				} else {
					y++;
				}
			}
		}

		gain.setAlpha(1.0 * blocksGained
				/ (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained
				/ (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained
				/ (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}

	public static Gain destroyIfNoStakeLFT(double a, double g, double l)
			throws IOException {
		/*
		 * Destroy if lambda leads. Start colluding when you lead.
		 */

		long blocksGained = 0;
		long publicGained = 0;
		long lambdaGained = 0;
		int state = 4;
		int ta = 0, tb = 0, tl = 0; // blocks that each will get, if merged with
									// public
		int x = 0, y = 0; // x = a - b, y = l - a
		Party party;
		boolean isPrime = false;
		boolean printAll = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Gain gain = new Gain();

		Queue<Party> privateChain = new LinkedList<Party>();

		for (int i = 0; i < Eclipse.ITERATIONS; i++) {
			if (printAll == true)
				System.out.println(state + ", x = " + x + ", y = " + y
						+ ", ta = " + ta + ", tl = " + tl + ", tb = " + tb);
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
				if (Party.LAMBDA.equals(party)) {
					state = 2;
					y = 1;
					x = 0;
					isPrime = false;
					privateChain.clear();
				} else if (Party.ALPHA.equals(party)) {
					state = 3;
					x = 1;
					y = 0;
					isPrime = false;
					privateChain.add(party);
				} else {
					publicGained++;
					if (printAll == true)
						System.out.println("Public++");
					x = y = 0;
					tl = ta = tb = 0;
					isPrime = false;
					privateChain.clear();
				}
			} else if (state == 2) {
				gain.increaseS2();
				// A B together, L separate
				// x = 0 always in this
				if (y == 0) {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 3;
						x = 1;
						y = 0;
						tl = 0; // alpha and lambda sync; lambda's blocks get
								// destroyed
						privateChain.add(party);
						isPrime = false;
					} else {
						state = 4;
						// gains
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						x = y = 0;
						tl = ta = tb = 0;
						isPrime = false;
						privateChain.clear();
					}
				} else {
					if (Party.LAMBDA.equals(party)) {
						y++;
						isPrime = false;
						privateChain.clear();
					} else if (Party.ALPHA.equals(party)) {
						state = 1;
						y--;
						x++;
						privateChain.add(party);
						isPrime = false;
					} else {
						y--;
						publicGained += tb;
						if (printAll == true)
							System.out.println("Public+=" + tb);
						ta = tb = 0; // a and b sync.
						isPrime = false;
						privateChain.clear();
					}
				}
			} else if (state == 3) {
				gain.increaseS3();
				// y = 0 always
				if (x == 0) {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						state = 4;
						// gains
						privateChain.add(party);
						x++;
					} else {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							// SRIJAN NEW
							state = -13;
							x--;
							isPrime = true; // is already true
						} else {
							state = 4;
							isPrime = false;
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							if (printAll == true)
								System.out
										.println("Public through gamma-beta+1");
							privateChain.clear();
							x = y = 0;
							tl = ta = tb = 0;
						}
					}
				} else {
					if (Party.LAMBDA.equals(party) || Party.ALPHA.equals(party)) {
						x++;
						privateChain.add(party);
					} else {
						if (!isPrime) {
							x--;
							isPrime = true;
						} else {
							if (Eclipse.GAMMABETA == true) {
								x--;
								while (privateChain.size() > x + 1) {
									Party p = privateChain.poll();
									if (Party.ALPHA.equals(p)) {
										if (printAll == true)
											System.out.println("Alpha+=1");
										blocksGained += 1;
									} else {
										if (printAll == true)
											System.out.println("Lambda+=1");
										lambdaGained += 1;
									}
								}
							} else {
								x--;
							}
						}
					}
					// }
				}
			} else if (state == -13) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)) {
					state = -1300;
					x++;
					privateChain.add(party);
				} else if (Party.BETA.equals(party)) {
					state = 4;
					isPrime = false;
					publicGained += privateChain.size() + 1;
					privateChain.clear();
					if (printAll == true)
						System.out
								.println("Public through (1-Gamma)beta+" + tb);
					ta = tb = tl = 0;
					x = 0;
					y = 0;
				}
			} else if (state == -1300) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party) || Party.LAMBDA.equals(party)) {
					state = 3;
					x++;
					isPrime = true;
				} else {
					state = -13;
					x--;
				}
			} else if (state == 1) {
				gain.increaseS1();
				if (Party.LAMBDA.equals(party)) {
					y++;
				} else if (x > 0 && Party.BETA.equals(party)) {
					if (!isPrime) {
						x--;
						isPrime = true;
					} else {
						if (Eclipse.GAMMABETA == true) {
							x--;
							while (privateChain.size() > x + 1) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
						} else {
							x--;
						}
					}
				} else if (x == 0 && Party.BETA.equals(party)) {
					if (y == 0) {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							// SRIJAN NEW
							state = -11;
							x--;
							isPrime = true;
						} else {
							state = 4;
							isPrime = false;
							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							privateChain.clear();
							x = y = 0;
							tl = ta = tb = 0;
						}
					} else {
						// gains
						if (Eclipse.GAMMABETA == false) {
							// take to minus one state
							// SRIJAN NEW
							state = -11;
							x--;
							isPrime = true;
						} else {
							state = 2;
							isPrime = false;

							while (privateChain.size() > 0) {
								Party p = privateChain.poll();
								if (Party.ALPHA.equals(p)) {
									if (printAll == true)
										System.out.println("Alpha+=1");
									blocksGained += 1;
								} else {
									System.out.println("WTF!!!!!");
									if (printAll == true)
										System.out.println("Lambda+=1");
									lambdaGained += 1;
								}
							}
							publicGained += 1;
							tb = ta = 0;
							y--;
						}
					}
				} else if (y > 0 && Party.ALPHA.equals(party)) {
					y--;
					x++;
					privateChain.add(party);
				} else if (y == 0 && Party.ALPHA.equals(party)) {
					state = 3;
					privateChain.add(party);
					x++;
					tl = 0;
				}
			} else if (state == -11) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party)) {
					privateChain.add(party);
					if (y == 0) {
						state = -1300;
						tl = 0;
						x++;
					} else {
						state = -1100;
						y--;
						x++;
					}

				} else if (Party.BETA.equals(party)) {
					if (y == 0) {
						state = 4;
						isPrime = false;
						publicGained += privateChain.size() + 1;
						privateChain.clear();
						if (printAll == true)
							System.out.println("Public through (1-Gamma)beta+"
									+ tb);
						ta = tb = tl = 0;
						x = 0;
						y = 0;
					} else {
						state = 2;
						isPrime = false;
						publicGained += privateChain.size() + 1;
						privateChain.clear();
						if (printAll == true)
							System.out.println("Public through (1-Gamma)beta+"
									+ tb);
						ta = tb = 0;
						y--;
						x = 0;
					}
				} else {
					y++;
				}
			} else if (state == -1100) {
				// SRIJAN NEW
				if (Party.ALPHA.equals(party)) {
					if (y == 0) {
						state = 3;
						x++;
						y = 0;
						tl = 0;
						privateChain.add(party);
					} else {
						state = 1;
						privateChain.add(party);
						x++;
						y--;
					}
				} else if (Party.BETA.equals(party)) {
					state = -11;
					x--;
				} else {
					y++;	
				}
			}
		}

		gain.setAlpha(1.0 * blocksGained
				/ (publicGained + blocksGained + lambdaGained));
		gain.setLambda(1.0 * lambdaGained
				/ (publicGained + blocksGained + lambdaGained));
		gain.setBeta(1.0 * publicGained
				/ (publicGained + blocksGained + lambdaGained));
		gain.setBlocks(blocksGained, publicGained, lambdaGained);
		return gain;
	}

}
