package org.kidneyomics.VCFCommandLineTools;

import java.util.ArrayList;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;

public class SimpleGenotypeBuilder {
	
	public static Genotype create(String sampleId, Allele... alleles) {
		
		ArrayList<Allele> list = new ArrayList<>(alleles.length);
		for(Allele a : alleles) {
			list.add(a);
		}
		
		return GenotypeBuilder.create(sampleId, list);
	}
}
