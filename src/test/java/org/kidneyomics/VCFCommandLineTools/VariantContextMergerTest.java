package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class VariantContextMergerTest {

	@Test
	public void test() {
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
		
		
		LinkedList<Genotype> gts2 = new LinkedList<>();
		gts2.add(gt3);
		
		
		VariantContext vc1 = builder.genotypes(gts1).make();
		VariantContext vc2 = builder.genotypes(gts2).make();
		
		int ac1 = 0;
		int count1 = 0;
		for(Genotype gt : vc1.getGenotypes()) {
			if(gt.isHomVar()) {
				ac1 += 2;
			} else if(gt.isHet()) {
				ac1 += 1;
			}
			count1++;
		}
		
		assertEquals(1,ac1);
		assertEquals(2,count1);
		
		
		int ac2 = 0;
		int count2 = 0;
		for(Genotype gt : vc2.getGenotypes()) {
			if(gt.isHomVar()) {
				ac2 += 2;
			} else if(gt.isHet()) {
				ac2 += 1;
			}
			count2++;
		}
		
		assertEquals(2,ac2);
		assertEquals(1,count2);
		
		
		VariantContext merge = VariantContextMerger.merge(vc1, vc2);
		
		int ac3 = 0;
		int count3 = 0;
		for(Genotype gt : merge.getGenotypes()) {
			if(gt.isHomVar()) {
				ac3 += 2;
			} else if(gt.isHet()) {
				ac3 += 1;
			}
			count3++;
		}
		
		assertEquals(3,ac3);
		assertEquals(3,count3);
		
		
	}

}
