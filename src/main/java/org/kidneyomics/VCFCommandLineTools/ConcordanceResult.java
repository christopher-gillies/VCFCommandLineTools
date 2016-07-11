package org.kidneyomics.VCFCommandLineTools;

import java.util.LinkedList;
import java.util.List;

public class ConcordanceResult {
	
	public enum COUNT_TYPE {
		TP,FN,FP,TN,UNKNOWN
	}
	
	private ConcordanceResult(String truthSample, String testSample) {
		this.truthSample = truthSample;
		this.testSample = testSample;
		this.log = new LinkedList<>();
	}
	
	static ConcordanceResult create(String truthSample, String testSample) {
		return new ConcordanceResult(truthSample, testSample);
	}
	
	private final String truthSample;
	private final String testSample;
	private final List<String> log;
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
	/**
	 * 
	 * @param truthGt {-1,0,1,2}
	 * @param testGt {-1,0,1,2}
	 * @return COUNT_TYPE of how this pair was counted
	 */
	COUNT_TYPE update(int truthGt, int testGt) {
		COUNT_TYPE res = COUNT_TYPE.UNKNOWN;
		if( (truthGt == -1 || truthGt == 0 || truthGt == 1 || truthGt == 2) &&
				(testGt == -1 || testGt == 0 || testGt == 1 || testGt == 2)	) {
			
			switch(truthGt) {
			case -1:
				if(testGt == 1 || testGt == 2) {
					fp++;
					res = COUNT_TYPE.FP;
				}
				break;
			case 0:
			{
				if(testGt == -1) {
					//do nothing
				} else if(testGt == 0) {
					tn++;
					res = COUNT_TYPE.TN;
				} else if(testGt == 1 || testGt == 2) {
					fp++;
					res = COUNT_TYPE.FP;
				}
			}
				break;
			case 1:
			case 2:
			{
				if(testGt == -1 || testGt == 0) {
					fn++;
					res = COUNT_TYPE.FN;
				} else if(testGt == 1 || testGt == 2) {
					tp++;
					res = COUNT_TYPE.TP;
				}
			}
				break;
			}
			
			
		} else {
			throw new RuntimeException(truthGt + " or " + testGt + " is not biallelic");
		}
		
		return res;
	}
	
	
	/**
	 * 
	 * @param truthKey -- variant key for the truth variant
	 * @param testKey -- variant key for the test variant
	 * @param type
	 * @return the log entry
	 */
	String log(String truthKey, int truthGt, String testKey, int testGt, COUNT_TYPE type) {
		String result = String.format("%1$s\t%2$d\t%3$s\t%4$d\t%5$s", truthKey,truthGt,testKey,testGt,type);
		this.log.add(result);
		return result;
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
	
	/**
	 * 
	 * @return the logged entries
	 */
	public List<String> getLog() {
		return this.log;
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