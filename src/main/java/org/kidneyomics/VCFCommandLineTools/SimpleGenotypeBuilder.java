package org.kidneyomics.VCFCommandLineTools;

import java.util.ArrayList;
import java.util.Map;

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
	
	public static Genotype createWithAttributes(String sampleId,  Map<String,Object> attributes, Allele... alleles) {
		
		ArrayList<Allele> list = new ArrayList<>(alleles.length);
		for(Allele a : alleles) {
			list.add(a);
		}
		
		return GenotypeBuilder.create(sampleId, list, attributes);
	}
}
