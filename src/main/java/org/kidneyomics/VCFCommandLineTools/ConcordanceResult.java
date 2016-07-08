package org.kidneyomics.VCFCommandLineTools;

public class ConcordanceResult {
	
	private ConcordanceResult(String truthSample, String testSample) {
		this.truthSample = truthSample;
		this.testSample = testSample;
	}
	
	static ConcordanceResult create(String truthSample, String testSample) {
		return new ConcordanceResult(truthSample, testSample);
	}
	
	private final String truthSample;
	private final String testSample;
	
	private int tp = 0;
	private int fp = 0;
	private int tn = 0;
	private int fn = 0;
	
	/*
	
	tp = 1 or 2 in 1000g and 1 or 2 in MIPS
	fn = 1 or 2 in 1000g and 0 or missing in MIPS
	fp = 0 or missing in 1000G and 1 or 2 in MIPS
	tn = 0 in 1000G and 0 in MIPS
	*/
	void update(int truthGt, int testGt) {
		if( (truthGt == -1 || truthGt == 0 || truthGt == 1 || truthGt == 2) &&
				(testGt == -1 || testGt == 0 || testGt == 1 || testGt == 2)	) {
			
			switch(truthGt) {
			case -1:
				if(testGt == 1 || testGt == 2) {
					fp++;
				}
				break;
			case 0:
			{
				if(testGt == -1) {
					//do nothing
				} else if(testGt == 0) {
					tn++;
				} else if(testGt == 1 || testGt == 2) {
					fp++;
				}
			}
				break;
			case 1:
			case 2:
			{
				if(testGt == -1 || testGt == 0) {
					fn++;
				} else if(testGt == 1 || testGt == 2) {
					tp++;
				}
			}
				break;
			}
			
			
		} else {
			throw new RuntimeException(truthGt + " or " + testGt + " is not biallelic");
		}
	}
	
	/**
	 * Reset counts
	 */
	public void clear() {
		tp = 0;
		fp = 0;
		tn = 0;
		fn = 0;
	}

	public int tp() {
		return tp;
	}

	public int fp() {
		return fp;
	}

	public int tn() {
		return tn;
	}

	public int fn() {
		return fn;
	}

	public String truthSample() {
		return truthSample;
	}

	public String testSample() {
		return testSample;
	}
	
	public double sensitivity() {
		//true positive rate
		//true positives / total positives
		return tp / ( (double) tp + fn   );
	}
	
	public double specificity() {
		//true negative rate
		// true negatives / total negatives
		return tn / ( (double) tn + fp);
	}
	
	public double fdr() {
		//false discovery rate
		//fraction of false positives / total positive calls
		return fp / ( (double) fp + tp);
	}
	
	@Override
	public String toString() {
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Truth Sample: ");
		sb.append(truthSample);
		sb.append("\n");
		
		sb.append("Test Sample: ");
		sb.append(testSample);
		sb.append("\n");
		
		sb.append("True Positives: ");
		sb.append(tp);
		sb.append("\n");
		
		sb.append("False Positives: ");
		sb.append(fp);
		sb.append("\n");
		
		sb.append("True Negatives: ");
		sb.append(tn);
		sb.append("\n");
		
		sb.append("False Negatives: ");
		sb.append(fn);
		sb.append("\n");
		
		sb.append("Sensitivity: ");
		sb.append(sensitivity());
		sb.append("\n");
		
		sb.append("Specificity: ");
		sb.append(specificity());
		sb.append("\n");
		
		sb.append("False Discovery Rate: ");
		sb.append(fdr());
		sb.append("\n");
		
		return sb.toString();
		
	}
		
}