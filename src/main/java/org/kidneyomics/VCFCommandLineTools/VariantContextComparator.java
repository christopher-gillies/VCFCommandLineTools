package org.kidneyomics.VCFCommandLineTools;

import java.util.Comparator;
import java.util.regex.Pattern;

import htsjdk.variant.variantcontext.VariantContext;

public class VariantContextComparator implements Comparator<VariantContext> {

	private static final Pattern pattern = Pattern.compile("[0-9]+");
	
	@Override
	public int compare(VariantContext o1, VariantContext o2) {
		
		
		String chr1 = o1.getContig().replace("chr", "");
		String chr2 = o2.getContig().replace("chr", "");
		
		if(chr1.equalsIgnoreCase(chr2)) {
			
			//compare position
			
			if(o1.getStart() < o2.getStart()) {
				return -1;
			} else if(o1.getStart() > o2.getStart()) {
				return 1;
			} else {
				// positions match
				return 0;
			}
			
		} else {
			
			//which comes before?
			
			CHR_TYPE type1 = CHR_TYPE.classify(chr1);
			CHR_TYPE type2 = CHR_TYPE.classify(chr2);

			int cmp = type1.compareTo(type2);
			if(cmp < 0) {
				return -1;
			} else if(cmp > 0) {
				return 1;
			} else {
				// = 0
				
				if(type1 == CHR_TYPE.NUMERIC && type2 == CHR_TYPE.NUMERIC) {
					int intChrCmp = Integer.compare(getInt(chr1), getInt(chr2));
					
					if(intChrCmp < 0) {
						return -1;
					} else if(intChrCmp > 0) {
						return 1;
					} else {
						// = 0
						throw new IllegalStateException("Error chromosomes are equal");
					}
				} else {
					throw new IllegalStateException("Error chromosomes are equal");
				}
				
			}
			
		}
		
	}
	
	
	
	public enum CHR_TYPE {
		NUMERIC,
		X,
		Y,
		MT;
		
		
		
		private static boolean isX(String in) {
			return in.equalsIgnoreCase("X");
		}
		
		private static boolean isY(String in) {
			return in.equalsIgnoreCase("Y");
		}
		
		private static boolean isMT(String in) {
			return in.equalsIgnoreCase("M") || in.equalsIgnoreCase("MT");
		}
		
		private static boolean isNumeric(String in) {
			return pattern.matcher(in).matches();
		}
		
		public static CHR_TYPE classify(String chr) {
			if(isX(chr)) {
				return X;
			} else if(isY(chr)) {
				return Y;
			} else if(isMT(chr)) {
				return MT;
			} else if(isNumeric(chr)) {
				return NUMERIC;
			} else {
				throw new IllegalArgumentException("Unsupported chromosome");
			}
		}

		
	}
	
	public int getInt(String in) {
		return Integer.parseInt(in);
	}
	
	

}
