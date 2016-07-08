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
		int ac = VariantContextUtil.ac(vc);
		return ac >= minAc;
	}

}
