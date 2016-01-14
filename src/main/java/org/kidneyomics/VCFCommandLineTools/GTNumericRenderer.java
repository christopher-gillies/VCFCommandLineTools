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
		boolean missingFlag = false;
		
		for(Allele a : alleles) {
			
			if(a.isNoCall()) {
				missingFlag = true;
				continue;
			}
			
			if(a.isNonReference()) {
				gtCount++;
			}
		}
		
		if(missingFlag == true) {
			return ".";
		} else {
			return Integer.toString(gtCount);
		}
	}

}
