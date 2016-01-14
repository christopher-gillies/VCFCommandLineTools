package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.kidneyomics.VCFCommandLineTools.GTRendererFactory.GT_RENDER_TYPE;
import org.slf4j.Logger;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;

public class GTRendererTest {

	@Test
	public void testNumeric() {
		GTRenderer renderer = GTRendererFactory.getGTRenderer(GT_RENDER_TYPE.NUMERIC);
		
		Allele a1 = Allele.create("A", true);
		Allele a2 = Allele.create("T",false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		
		alleles.add(a1);
		alleles.add(a2);
		
		
		Genotype gt = GenotypeBuilder.create("SAMPLE1", alleles);
		
		Logger logger = new LoggerService().getLogger(this);
		
		logger.info(gt.toString());
		logger.info("IS HET? " + gt.isHet());
		
		String result = renderer.render(gt);
		
		assertTrue(result.equals("1"));
		
	}
	
	@Test
	public void testNumeric2() {
		GTRenderer renderer = GTRendererFactory.getGTRenderer(GT_RENDER_TYPE.NUMERIC);
		
		Allele a1 = Allele.create(".", false);
		Allele a2 = Allele.create(".",false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		
		alleles.add(a1);
		alleles.add(a2);
		
		
		Genotype gt = GenotypeBuilder.create("SAMPLE1", alleles);
		
		Logger logger = new LoggerService().getLogger(this);
		
		logger.info(gt.toString());
		logger.info("IS HET? " + gt.isHet());
		
		String result = renderer.render(gt);
		
		assertTrue(result.equals("."));
		
	}
	
	
	@Test
	public void testNucleotide() {
		GTRenderer renderer = GTRendererFactory.getGTRenderer(GT_RENDER_TYPE.NUCLEOTIDE);
		
		Allele a1 = Allele.create("A", true);
		Allele a2 = Allele.create("T",false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		
		alleles.add(a1);
		alleles.add(a2);
		
		GenotypeBuilder builder = new GenotypeBuilder();
		builder.alleles(alleles);
		builder.name("SAMPLE1");
		builder.phased(false);
		
		Genotype gt = builder.make();
		
	
		
		Logger logger = new LoggerService().getLogger(this);
		
		logger.info(gt.toString());
		logger.info("IS HET? " + gt.isHet());
		
		String result = renderer.render(gt);
		
		logger.info("RESULT: " + result);
		
		assertTrue(result.equals("A/T"));
		
	}
	
	@Test
	public void testNucleotide2() {
		GTRenderer renderer = GTRendererFactory.getGTRenderer(GT_RENDER_TYPE.NUCLEOTIDE);
		
		Allele a1 = Allele.create("A", true);
		Allele a2 = Allele.create("T",false);
		
		List<Allele> alleles = new LinkedList<Allele>();
		
		alleles.add(a1);
		alleles.add(a2);
		
		GenotypeBuilder builder = new GenotypeBuilder();
		builder.alleles(alleles);
		builder.name("SAMPLE1");
		builder.phased(true);
		
		Genotype gt = builder.make();
		
	
		
		Logger logger = new LoggerService().getLogger(this);
		
		logger.info(gt.toString());
		logger.info("IS HET? " + gt.isHet());
		
		String result = renderer.render(gt);
		
		logger.info("RESULT: " + result);
		
		assertTrue(result.equals("A|T"));
		
	}

}
