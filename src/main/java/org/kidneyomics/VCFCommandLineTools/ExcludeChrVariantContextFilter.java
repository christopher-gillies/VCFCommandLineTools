package org.kidneyomics.VCFCommandLineTools;

import htsjdk.variant.variantcontext.VariantContext;

public class ExcludeChrVariantContextFilter implements VariantContextFilter {

	private final String chrToExclude;
	
	public ExcludeChrVariantContextFilter(final String chrToExclude) {
		this.chrToExclude = chrToExclude.replaceAll("chr", "");
	}
	
	@Override
	public boolean keep(VariantContext vc) {
		if(vc.getContig().equalsIgnoreCase(this.chrToExclude)) {
			return false;
		} else if(vc.getContig().equalsIgnoreCase("chr" + this.chrToExclude)) {
			return false;
		} else {
			return true;
		}
	}

}
