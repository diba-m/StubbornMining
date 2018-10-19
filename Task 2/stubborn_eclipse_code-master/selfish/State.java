package selfish;

public class State {
	double a;
	double g;
	long blocksGained;
	long publicGained;
	long lambdaGained;
	
	long pending;
	boolean primeState;
	int state;

	public State(double a,
			double g,
			long blocksGained,
			long publicGained,
			long lambdaGained,
			long pending,
			boolean primeState,
			int state) {
		super();
		this.a = a;
		this.g = g;
		this.blocksGained = blocksGained;
		this.publicGained = publicGained;
		this.lambdaGained = lambdaGained;
		this.pending = pending;
		this.primeState = primeState;
		this.state = state;
	}
}
