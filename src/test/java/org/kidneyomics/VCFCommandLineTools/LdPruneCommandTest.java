package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.Queue;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

import org.junit.Test;

public class LdPruneCommandTest {

	
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
		command.filterQueueLd(queue, 0.2, 1000);
		
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
		command.filterQueueLd(queue, 0.2, 1000);
		
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
		command.filterQueueLd(queue, 0.2, 1000);
		
		assertEquals(2,queue.size());
		
		verify(mockCalc, times(2)).pearsonR2(any(VariantContext.class), any(VariantContext.class));
	}

}
