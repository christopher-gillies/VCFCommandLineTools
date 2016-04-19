package org.kidneyomics.VCFCommandLineTools;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;

public class MinACVariantContextFilter implements VariantContextFilter {

	private final int minAc;
	
	public MinACVariantContextFilter(final int minAc) {
		this.minAc = minAc;
	}
	
	@Override
	public boolean keep(VariantContext vc) {
		GenotypesContext context = vc.getGenotypes();
		int ac = 0;
		for(Genotype gt : context) {
			if(gt.isHet()) {
				ac++;
			} else if(gt.isHomVar()) {
				ac+=2;
			}
		}
		return ac >= minAc;
	}

}
