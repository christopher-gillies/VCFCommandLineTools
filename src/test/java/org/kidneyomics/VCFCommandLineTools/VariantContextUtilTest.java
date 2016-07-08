package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class VariantContextUtilTest {

	@Test
	public void testAc() {
		Allele ref = Allele.create("A", true);
		Allele alt = Allele.create("T");
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(ref);
		alleles.add(alt);
		
		VariantContextBuilder builder = new VariantContextBuilder(null,"1",100,100,alleles);
		
		Genotype gt1 = SimpleGenotypeBuilder.create("HG1", ref,ref);
		Genotype gt2 = SimpleGenotypeBuilder.create("HG2", ref,alt);
		
		
		Genotype gt3 = SimpleGenotypeBuilder.create("HG3", alt,alt);
		
		LinkedList<Genotype> gts1 = new LinkedList<>();
		gts1.add(gt1);
		gts1.add(gt2);
		gts1.add(gt3);
		
		
		
		VariantContext vc1 = builder.genotypes(gts1).make();
		
		assertEquals(3,VariantContextUtil.ac(vc1));
	}

}
