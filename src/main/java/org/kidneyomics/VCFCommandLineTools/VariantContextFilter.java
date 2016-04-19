package org.kidneyomics.VCFCommandLineTools;

import htsjdk.variant.variantcontext.VariantContext;

public interface VariantContextFilter {
	boolean keep(VariantContext vc);
}
