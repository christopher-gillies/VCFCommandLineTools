package org.kidneyomics.VCFCommandLineTools;

import java.util.ArrayList;
import java.util.List;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class VariantContextMerger {

	public static VariantContext merge(VariantContext vc1, VariantContext vc2) {
		
		VariantContextBuilder builder = new VariantContextBuilder(null,vc1.getContig(),vc1.getStart(),vc1.getEnd(), vc1.getAlleles());
		
		List<Genotype> genotypes = new ArrayList<Genotype>(vc1.getGenotypes().size() + vc2.getGenotypes().size());
		
		//genotypes.addAll(vc1.getGenotypes());
		//genotypes.addAll(vc2.getGenotypes());
		
		for(Genotype gt : vc1.getGenotypes()) {
			Genotype newGt = GenotypeBuilder.create(gt.getSampleName(), gt.getAlleles());
			genotypes.add(newGt);
		}
		
		for(Genotype gt : vc2.getGenotypes()) {
			Genotype newGt = GenotypeBuilder.create(gt.getSampleName(), gt.getAlleles());
			genotypes.add(newGt);
		}
		
		//add the genotypes to the builder
		builder.genotypes(genotypes);
		
		builder.passFilters();
		
		return builder.make();
	}
}
