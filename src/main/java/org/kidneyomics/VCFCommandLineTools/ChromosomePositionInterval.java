package org.kidneyomics.VCFCommandLineTools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/*
 * A class representing a chromosome position or chromosome interval
 */
public class ChromosomePositionInterval {
	public final static String INTERVAL_REGEX = "^(chr)?([0-9XYMxym]{1,2})[:]([0-9,]+)([-]([0-9,]+))?$";
	public final static Pattern pattern = Pattern.compile(INTERVAL_REGEX);
	private String chromosome = "NA";
	private Integer startPostion = -1;
	private Integer endPostion = -1;

	public ChromosomePositionInterval() {
		
	}
	
	public static ChromosomePositionInterval getChromosomePosistionIntervalFromVariantKey(String variantKey, String pattern) throws IllegalArgumentException {
		if(pattern == null) {
			throw new IllegalArgumentException("pattern cannot be null");
		}
		
		if(variantKey == null) {
			throw new IllegalArgumentException("variantKey cannot be null");
		}
		
		String[] variantParts = variantKey.split(pattern);
		if(variantParts.length != 4) {
			throw new IllegalArgumentException("variantKey should have four parts");
		}
		
		ChromosomePositionInterval cpi = new ChromosomePositionInterval();
		cpi.setChromosome(variantParts[0]);
		cpi.setStartPostion(Integer.parseInt(variantParts[1]));
		return cpi;
	}
	
	public ChromosomePositionInterval(String interval) {
		if(StringUtils.isEmpty(interval)) {
			throw new IllegalArgumentException("");
		}
		
		Matcher m = pattern.matcher(interval.trim());
		if(m.matches()) {
			this.chromosome = m.group(2).toUpperCase();
			this.startPostion = Integer.parseInt(m.group(3).replace(",", ""));
			if(m.group(4) != null ) {
				try {
					this.endPostion = Integer.parseInt(m.group(5).replace(",", ""));
				} catch(Exception e) {
					this.endPostion = Integer.MAX_VALUE;
				}
			}
		} else {
			throw new IllegalArgumentException("Format should be chr1:12344-12234 or chr1:123,454-122,434 or 1:12344-12234");
		}
	}
	
	public boolean isInterval() {
		return this.endPostion > -1 && this.startPostion > 0;
	}
	
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public Integer getStartPostion() {
		return startPostion;
	}
	public void setStartPostion(Integer startPostion) {
		this.startPostion = startPostion;
	}
	public Integer getEndPostion() {
		return endPostion;
	}
	public void setEndPostion(Integer endPostion) {
		this.endPostion = endPostion;
	}
	
	@Override
	public String toString() {
		return toString(true);
	}
	
	/**
	 * 
	 * @param includeChr prefix the chromosome position with 'chr' (true/false)
	 * @return
	 */
	public String toString(Boolean includeChr) {
		StringBuilder sb = new StringBuilder();
		if(includeChr) {
			sb.append("chr");
		}
		sb.append(this.chromosome);
		sb.append(":");
		sb.append(this.startPostion);
		if(this.isInterval()) {
			sb.append("-");
			sb.append(this.endPostion);
		} else {
			/*
			 * 
			 */
			sb.append("-").append(this.startPostion);
		}
		return sb.toString();
	}
	
	public String toTabixQuery() {
		return this.toString(false);
		/*
		StringBuilder sb = new StringBuilder();
		if(this.isInterval()) {
			return this.toString(false);
		} else {
			
			sb.append(this.chromosome);
			sb.append(":");
			sb.append(this.startPostion);
			sb.append("-");
			sb.append(this.startPostion);
			return sb.toString();
		}
		*/
	}
	
	@Override
	public boolean equals(Object obj) {
		ChromosomePositionInterval ci = (ChromosomePositionInterval) obj;
		return this.toString().equals(ci.toString());
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	public Integer length() {
		if(isInterval()) {
			return this.endPostion - this.startPostion + 1;
		} else {
			return 1;
		}
	}
	
}
