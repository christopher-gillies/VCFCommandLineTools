package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class FilterableVariantContextReaderTest {

	@Test
	public void testFilterLd() throws Exception {
		VariantContextLdCalculator mockCalc = mock(VariantContextLdCalculator.class);
		
		when(mockCalc.pearsonR2(any(VariantContext.class), any(VariantContext.class)))
		.thenReturn(0.7)
		.thenReturn(0.19)
		.thenReturn(0.2)
		.thenReturn(0.21).thenReturn(0.1);
		
		
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
		
		
		FilterableVariantContextReader reader = FilterableVariantContextReader.createByIterableContextsAndFiltersAndLdAndWindowAndLDCALC(queue, null, 0.2, 1000000, mockCalc);

		// the first will be popped off
		// 4 calls to the pearsonR2
		
	
		for(VariantContext context : reader) {
			
		}
		
		assertEquals(3,reader.getVariantsKept());
		
		/*
		 	5 variants --- keep
		 	-----
		 	pull first out
		 	4 variants left in queue
		 	----
		 	apply filter (4 times)
		 	---
		 	2 variants left --keep
		 	----
		 	pull out next variant
		 	1 left in the queue
		 	---
		 	apply filter (1 time)
		 	----
		 	still one left in the queue
		 	return
		 	-----
		 	pull out of queue and now the queue is empty
		 */
		
		verify(mockCalc, times(5)).pearsonR2(any(VariantContext.class), any(VariantContext.class));
		
		assertEquals(5,reader.getVariantsRead());
		assertEquals(0,reader.numberOfFilters());
		reader.close();
	}
	
	
	
	@Test
	public void testFilterChr() throws Exception {
		Queue<VariantContext> queue = new LinkedList<VariantContext>();
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.alleles("A","T");
		builder.chr("chr1").start(100).stop(100);
		
		//add five items
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.chr("chr2").make());
		queue.add(builder.make());
		
		
		assertEquals(5,queue.size());
		
		List<VariantContextFilter> filters = new LinkedList<>(); 
		filters.add(new ExcludeChrVariantContextFilter("chr1"));
		FilterableVariantContextReader reader = FilterableVariantContextReader.createByIterableContextsAndFilters(queue, filters);

		for(VariantContext context : reader) {
			
		}
		
		assertEquals(2,reader.getVariantsKept());
		assertEquals(5,reader.getVariantsRead());
		assertEquals(1,reader.numberOfFilters());
		reader.close();
	}

	
	@Test
	public void testFilterPositionForLD() {
		VariantContextLdCalculator mockCalc = mock(VariantContextLdCalculator.class);
		
		when(mockCalc.pearsonR2(any(VariantContext.class), any(VariantContext.class))).thenReturn(1.0);

		Queue<VariantContext> queue = new LinkedList<VariantContext>();
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.alleles("A","T");
		builder.chr("chr1").start(100).stop(100);
		
		//add five items
		queue.add(builder.make());
		queue.add(builder.make());
		queue.add(builder.start(100 + 1000 * 1000).stop(100 + 1000 * 1000).make());
		//don't filter these two
		queue.add(builder.start(101 + 1000 * 1000).stop(101 + 1000 * 1000).make());
		//filter this one
		queue.add(builder.make());
		
		
		assertEquals(5,queue.size());
		
		
		List<VariantContextFilter> filters = new LinkedList<>(); 
		filters.add(new ExcludeChrVariantContextFilter("chr1"));
		try(FilterableVariantContextReader reader = FilterableVariantContextReader.createByIterableContextsAndFiltersAndLdAndWindowAndLDCALC(queue, null, 0.2, 1000000, mockCalc)) {
			for(VariantContext context : reader) {
				
			}
			assertEquals(2,reader.getVariantsKept());
			assertEquals(5,reader.getVariantsRead());
			assertEquals(0,reader.numberOfFilters());
		} catch(Exception e) {
			e.printStackTrace();
		}
		

		
	}
	
	
	@Test
	public void testFilter() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
				
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		
		options.setMinAc(10);		
		options.setMaxLd(0.2);
		options.setWindowSizeKb(100);
		
		try(FilterableVariantContextReader reader = FilterableVariantContextReader.createByAppliationOptions(options)) {
			for(VariantContext context : reader) {
				int ac = VariantContextUtil.ac(context);
				assertTrue(ac >= 10);
			}
			assertEquals(59253,reader.getVariantsRead());
			assertTrue(59253> reader.getVariantsKept());
			assertEquals(1,reader.numberOfFilters());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	@Test
	public void testFilterExcludeChr() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
				
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		options.addChrToExclude("20");
		
		try(FilterableVariantContextReader reader = FilterableVariantContextReader.createByAppliationOptions(options)) {
			for(VariantContext context : reader) {

			}
			assertEquals(59253,reader.getVariantsRead());
			assertTrue(0 == reader.getVariantsKept());
			assertEquals(2,reader.numberOfFilters());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testFilterString() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
				
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		options.setFilterString("start == 23657494;");
		
		try(FilterableVariantContextReader reader = FilterableVariantContextReader.createByAppliationOptions(options)) {
			for(VariantContext context : reader) {
				assertEquals(23657494,context.getStart());
			}
			assertEquals(59253,reader.getVariantsRead());
			assertTrue(1 == reader.getVariantsKept());
			//2 minac and filterstring
			assertEquals(2,reader.numberOfFilters());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testHWEPop() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
		ClassPathResource ped = new ClassPathResource("omni_samples.20141118.panel");
		
		options.addVcfFile(vcf1.getFile().getAbsolutePath());

		options.setInFile(ped.getFile().getAbsolutePath());
		options.setIdCol("sample");
		options.setPopCol("pop");
		options.setHwe(0.01);
		options.setMinAc(0);
		
		try(FilterableVariantContextReader reader = FilterableVariantContextReader.createByAppliationOptions(options)) {
			for(VariantContext context : reader) {
				
			}
			assertEquals(59253,reader.getVariantsRead());
			assertTrue(reader.getVariantsKept() > 0);
			
			//minac not counted b/c will only add for over 0
			assertEquals(1,reader.numberOfFilters());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
