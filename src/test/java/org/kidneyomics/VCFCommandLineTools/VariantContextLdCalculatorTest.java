package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class VariantContextLdCalculatorTest {

	@Test
	public void test1() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",a,a),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",a,a),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(-1.0,r2,0.0001);
	}
	
	@Test
	public void test2() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(1,r2,0.0001);
	}

	@Test
	public void test3() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,t),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",t,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(1,r2,0.0001);
	}
	
	@Test
	public void test4() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",t,t),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,t),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,t),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",t,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(0.8741,r2,0.0001);
	}

	@Test
	public void test5() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",t,t),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,t),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",t,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(0.4596,r2,0.0001);
	}
	
	@Test
	public void test6() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",t,t),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",a,a),
				SimpleGenotypeBuilder.create("ID6",t,t),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,t),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",a,a),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",t,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(0.009380863,r2,0.0001);
	}
	
	@Test
	public void test7() {
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.chr("chr1").start(100).stop(100);
		
		Allele a = Allele.create("A", true);
		Allele t = Allele.create("T", false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(a);
		alleles.add(t);
		
		builder.alleles(alleles);
		
		
		
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",t,t),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",a,a),
				SimpleGenotypeBuilder.create("ID6",t,t),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",a,t),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc1 = builder.make();
		
		builder.genotypes(
				SimpleGenotypeBuilder.create("ID1",t,t),
				SimpleGenotypeBuilder.create("ID2",a,a),
				SimpleGenotypeBuilder.create("ID3",a,a),
				SimpleGenotypeBuilder.create("ID4",a,a),
				SimpleGenotypeBuilder.create("ID5",t,t),
				SimpleGenotypeBuilder.create("ID6",a,a),
				SimpleGenotypeBuilder.create("ID7",a,a),
				SimpleGenotypeBuilder.create("ID8",t,a),
				SimpleGenotypeBuilder.create("ID9",a,a),
				SimpleGenotypeBuilder.create("ID10",a,a)
				);
		VariantContext vc2 = builder.make();
		
		double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
		
		assertEquals(0.147929,r2,0.0001);
	}

}
