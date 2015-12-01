package org.kidneyomics.VCFCommandLineTools;

import java.util.Iterator;
import java.util.List;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;

public class GTNucleotideRenderer implements GTRenderer {

	@Override
	public String render(Genotype genotype) {
		List<Allele> alleles = genotype.getAlleles();
		String bases = "";
		//should be very few bases
		Iterator<Allele> iter = alleles.iterator();
		while(iter.hasNext()) {
			Allele a = iter.next();
			
			bases += a.getBaseString();
			
			if(iter.hasNext()) {
				if(genotype.isPhased()) {
					bases += Genotype.PHASED_ALLELE_SEPARATOR;
				} else {
					bases += Genotype.UNPHASED_ALLELE_SEPARATOR;
				}
			}
		}
		return bases;
	}

}
