package selfish;

public class Gain {

	public double a = -1;
	public double b = -1;
	public double l = -1;

	long bA = -1, bB = -1, bL = -1;
	
	public long s1count = 0;
	public  long s2count = 0;
	public  long s3count = 0;
	public  long s4count = 0;
	
	public void increaseS1(){
		s1count += 1;
	}
	public void increaseS2(){
		s2count += 1;
	}
	public void increaseS3(){
		s3count += 1;
	}
	public void increaseS4(){
		s4count += 1;
	}
	
	public double s1ratio(){
		return s1count*1.0/(s1count + s2count + s3count + s4count);
	}

	public double s2ratio(){
		return s2count*1.0/(s1count + s2count + s3count + s4count);
	}
	public double s3ratio(){
		return s3count*1.0/(s1count + s2count + s3count + s4count);
	}
	public double s4ratio(){
		return s4count*1.0/(s1count + s2count + s3count + s4count);
	}
	
	public void setLambda(double lambda) {
		this.l = lambda;
	}

	public void setBeta(double beta) {
		this.b = beta;
	}

	public void setAlpha(double alpha) {
		this.a = alpha;
	}

	public void setBlocks(long ba, long bb, long bl) {
		this.bA = ba;
		this.bB = bb;
		this.bL = bl;
	}
}
