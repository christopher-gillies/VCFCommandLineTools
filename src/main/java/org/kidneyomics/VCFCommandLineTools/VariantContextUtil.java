package org.kidneyomics.VCFCommandLineTools;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

public class VariantContextUtil {
	public static int ac(VariantContext context) {
		int ac = 0;
		for(Genotype gt : context.getGenotypes()) {
			if(gt.isHet()) {
				ac++;
			} else if(gt.isHomVar()) {
				ac+=2;
			}
		}
		return ac;
	}
}
