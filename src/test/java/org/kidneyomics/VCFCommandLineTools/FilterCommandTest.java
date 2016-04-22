package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFFileReader;

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
	
	@Test
	public void testFilterExcludeChr() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
				
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		
		options.setMinAc(10);
		File tmpDir = FileUtils.getTempDirectory();
		File out = new File(tmpDir.getAbsolutePath() + "/" + "result.vcf.gz");
		options.setOutFile(out.getAbsolutePath());
		
		options.setMaxLd(1);
		options.setWindowSizeKb(100);
		
		options.addChrToExclude("20");
		
		FilterCommand command = new FilterCommand(logger, options, new VariantContextLdCalculator());
		
		command.runCommand();
		
		
		assertTrue(out.exists());
		
		VCFFileReader reader = new VCFFileReader(out,false);
		int count = 0;
		for(VariantContext vc : reader) {
			count++;
		}
		reader.close();
		
		out.delete();
		assertEquals(0,count);
		
	}
	
	
	
	@Test
	public void testFilterString() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
				
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		
		options.setMinAc(10);
		File tmpDir = FileUtils.getTempDirectory();
		File out = new File(tmpDir.getAbsolutePath() + "/" + "result.vcf.gz");
		options.setOutFile(out.getAbsolutePath());
		
		options.setFilterString("start == 23657494;");
		
		FilterCommand command = new FilterCommand(logger, options, new VariantContextLdCalculator());
		
		command.runCommand();
		
		
		assertTrue(out.exists());
		
		VCFFileReader reader = new VCFFileReader(out,false);
		int count = 0;
		for(VariantContext vc : reader) {
			count++;
			System.err.println(vc.getContig() + ":" + vc.getStart());
			assertEquals(23657494,vc.getStart());
		}
		
		reader.close();
		
		out.delete();
		assertEquals(1,count);
		
	}
	
	@Test
	public void testHWEPop() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
		ClassPathResource ped = new ClassPathResource("omni_samples.20141118.panel");
		
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		
		File tmpDir = FileUtils.getTempDirectory();
		File out = new File(tmpDir.getAbsolutePath() + "/" + "result.vcf.gz");
		options.setOutFile(out.getAbsolutePath());
		
		options.setInFile(ped.getFile().getAbsolutePath());
		options.setIdCol("sample");
		options.setPopCol("pop");
		options.setHwe(0.01);
		options.setMinAc(0);
		
		FilterCommand command = new FilterCommand(logger, options, new VariantContextLdCalculator());
		
		command.runCommand();
		
		
		assertTrue(out.exists());
		
		VCFFileReader reader = new VCFFileReader(out,false);
		int count = 0;
		for(VariantContext vc : reader) {
			count++;
		}
		
		reader.close();
		
		out.delete();
	
		
	}
	
//	@Test
//	public void testFilterStringMEGA() {
//		LoggerService logger = new LoggerService();
//		ApplicationOptions options = new ApplicationOptions(logger);
//		String file = "/Users/cgillies/Google Drive/1_7_2016_Megachip/chr20.merged.reports.rc.dict.sorted.filtered.1000G.vcf.gz";
//		options.addVcfFile(file);
//		File tmpDir = FileUtils.getTempDirectory();
//		File out = new File(tmpDir.getAbsolutePath() + "/" + "result.vcf.gz");
//		options.setOutFile(out.getAbsolutePath());
//		options.setFilterString("gc = sapply(gtInfo,FUN=function(x){ x[['GCScore']] }); print(gc); mean(gc,na.rm=T) > 0.7");
//		
//		FilterCommand command = new FilterCommand(logger, options, new VariantContextLdCalculator());
//		
//		command.runCommand();
//		
//		assertTrue(out.exists());
//		out.delete();
//	}

}
