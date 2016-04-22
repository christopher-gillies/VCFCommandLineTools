package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;

public class HWEVariantContextFilterTest {

	@Test
	public void test() {
		HWEVariantContextFilter filter = new HWEVariantContextFilter(0.001);
		
		
		LinkedList<Genotype> genotypes = new LinkedList<>();
		
		
		Allele ref = Allele.create("A", true);
		Allele alt = Allele.create("T", false);
		
		
		for(int i = 0; i < 21; i ++) {
			genotypes.add(SimpleGenotypeBuilder.create("id" + i, ref,alt));
		}
		
		for(int i = 21; i < 100; i++) {
			genotypes.add(SimpleGenotypeBuilder.create("id" + i, ref,ref));
		}
		
		double p = filter.calculatePValue(genotypes);
		
		assertEquals(0.593645,p,0.000001);
	}

}
