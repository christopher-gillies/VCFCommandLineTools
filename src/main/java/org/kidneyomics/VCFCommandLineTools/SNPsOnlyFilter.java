package org.kidneyomics.VCFCommandLineTools;

import htsjdk.variant.variantcontext.VariantContext;

public class SNPsOnlyFilter implements VariantContextFilter {
	
	@Override
	public boolean keep(VariantContext vc) {
		return vc.isSNP();
	}
}
