package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import org.junit.Test;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;

public class ConcordanceCalculatorTest {

	@Test
	public void testAddVariant1() {
		ConcordanceCalculator calc = ConcordanceCalculator.create("truth", "test");
		
		VariantSiteKey key1 = VariantSiteKey.create("1:10:A:T");
		VariantSiteKey key2 = VariantSiteKey.create("1:11:A:T");
		VariantSiteKey key3 = VariantSiteKey.create("1:12:A:T");
		VariantSiteKey key4 = VariantSiteKey.create("1:13:A:T");
		VariantSiteKey key5 = VariantSiteKey.create("1:14:A:T");
		
		calc.addTestSampleVariant(key1, 0); //tn
		calc.addTruthSampleVariant(key1, 0);
		
		calc.addTestSampleVariant(key2, 0); //fn
		calc.addTruthSampleVariant(key2, 1);
		
		calc.addTestSampleVariant(key3, 1); //fp
		calc.addTruthSampleVariant(key3, -1);
		
		calc.addTestSampleVariant(key4, 1); //tp
		calc.addTruthSampleVariant(key4, 1);
		
		
		calc.addTestSampleVariant(key5, -1); //fn
		calc.addTruthSampleVariant(key5, 1);
		
		
		ConcordanceResult res = calc.computeConcordance();
		
		assertEquals(1,res.tp());
		assertEquals(1,res.fp());
		assertEquals(1,res.tn());
		assertEquals(2,res.fn());
	}
	
	
	@Test
	public void testGetGt() {
		ConcordanceCalculator calc = ConcordanceCalculator.create("truth", "test");
		Genotype gt1 = SimpleGenotypeBuilder.create("test", Allele.create("A", true),Allele.create("T"));
		assertEquals(1,calc.getGt(gt1));
		
		Genotype gt2 = SimpleGenotypeBuilder.create("test", Allele.create("A", true),Allele.create("A",true));
		assertEquals(0,calc.getGt(gt2));
		
		Genotype gt3 = SimpleGenotypeBuilder.create("test", Allele.create("A", false),Allele.create("A",false));
		assertEquals(2,calc.getGt(gt3));
		
		Genotype gt4 = SimpleGenotypeBuilder.create("test", Allele.NO_CALL,Allele.NO_CALL);
		assertEquals(-1,calc.getGt(gt4));
		
	}

}
