package org.kidneyomics.VCFCommandLineTools;

import java.io.IOException;
import java.util.List;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;

public class GTNumericRenderer implements GTRenderer {

	@Override
	public String render(Genotype genotype) {
		List<Allele> alleles = genotype.getAlleles();
		int gtCount = 0;
		for(Allele a : alleles) {
			
			if(a.isNoCall()) {
				continue;
			}
			
			if(a.isNonReference()) {
				gtCount++;
			}
		}
		return Integer.toString(gtCount);
	}

}
