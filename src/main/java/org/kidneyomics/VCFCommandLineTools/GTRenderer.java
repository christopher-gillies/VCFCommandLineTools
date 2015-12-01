package org.kidneyomics.VCFCommandLineTools;

import java.io.IOException;

import htsjdk.variant.variantcontext.Genotype;

public interface GTRenderer {
	
	String render(Genotype genotype);
}
