package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class FilterCommandTest {

	
	@Test
	public void testFilterLd() {
		VariantContextLdCalculator mockCalc = mock(VariantContextLdCalculator.class);
		
		when(mockCalc.pearsonR2(any(VariantContext.class), any(VariantContext.class)))
		.thenReturn(0.7)
		.thenReturn(0.19)
		.thenReturn(0.2)
		.thenReturn(0.21);
		
		LoggerService logger = new LoggerService();
		FilterCommand command = new FilterCommand(logger, new ApplicationOptions(logger), mockCalc);
		
		
		Queue<VariantContext> queue = new LinkedList<VariantContext>();
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.alleles("A","T");
		builder.chr("chr1").start(100).stop(100);
		
		//add five items
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.make());
		
		
		assertEquals(5,queue.size());
		
		
		// the first will be popped off
		// 4 calls to the pearsonR2
		command.filterQueueLd(queue, 0.2, 1000 * 1000);
		
		assertEquals(2,queue.size());
		
		verify(mockCalc, times(4)).pearsonR2(any(VariantContext.class), any(VariantContext.class));
	}
	
	
	@Test
	public void testFilterChr() {
		VariantContextLdCalculator mockCalc = mock(VariantContextLdCalculator.class);
		
		when(mockCalc.pearsonR2(any(VariantContext.class), any(VariantContext.class))).thenReturn(1.0);
		
		LoggerService logger = new LoggerService();
		FilterCommand command = new FilterCommand(logger, new ApplicationOptions(logger), mockCalc);
		
		
		Queue<VariantContext> queue = new LinkedList<VariantContext>();
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.alleles("A","T");
		builder.chr("chr1").start(100).stop(100);
		
		//add five items
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.chr("chr2").make());
		queue.add(builder.make());
		queue.add(builder.make());
		
		
		assertEquals(5,queue.size());
		
		
		// the first will be popped off
		// 4 calls to the pearsonR2
		command.filterQueueLd(queue, 0.2,  1000 * 1000);
		
		assertEquals(3,queue.size());
		
		verify(mockCalc, times(1)).pearsonR2(any(VariantContext.class), any(VariantContext.class));
	}
	
	@Test
	public void testFilterPosition() {
		VariantContextLdCalculator mockCalc = mock(VariantContextLdCalculator.class);
		
		when(mockCalc.pearsonR2(any(VariantContext.class), any(VariantContext.class))).thenReturn(1.0);
		
		LoggerService logger = new LoggerService();
		FilterCommand command = new FilterCommand(logger, new ApplicationOptions(logger), mockCalc);
		
		
		Queue<VariantContext> queue = new LinkedList<VariantContext>();
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.alleles("A","T");
		builder.chr("chr1").start(100).stop(100);
		
		//add five items
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.start(100 + 1000 * 1000).stop(100 + 1000 * 1000).make());
		queue.add(builder.start(101 + 1000 * 1000).stop(101 + 1000 * 1000).make());
		queue.add(builder.make());
		
		
		assertEquals(5,queue.size());
		
		
		// the first will be popped off
		// 4 calls to the pearsonR2
		command.filterQueueLd(queue, 0.2,  1000 * 1000);
		
		assertEquals(2,queue.size());
		
		verify(mockCalc, times(2)).pearsonR2(any(VariantContext.class), any(VariantContext.class));
	}
	
	@Test
	public void testFilter() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
				
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		
		options.setMinAc(10);
		File tmpDir = FileUtils.getTempDirectory();
		File out = new File(tmpDir.getAbsolutePath() + "/" + "result.vcf.gz");
		options.setOutFile(out.getAbsolutePath());
		
		options.setMaxLd(0.2);
		options.setWindowSizeKb(100);
		
		FilterCommand command = new FilterCommand(logger, options, new VariantContextLdCalculator());
		
		command.runCommand();
		
		
		assertTrue(out.exists());
		out.delete();
		
	}

}
