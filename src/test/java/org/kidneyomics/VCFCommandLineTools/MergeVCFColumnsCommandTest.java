package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class MergeVCFColumnsCommandTest {

	@Test
	public void test() throws IOException {
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
		ClassPathResource vcf2 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.relabel.vcf.gz");
		
		LoggerService loggerService = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(loggerService);
		
		
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		options.addVcfFile(vcf2.getFile().getAbsolutePath());
		
		
		File tmpDir = FileUtils.getTempDirectory();
		File out = new File(tmpDir.getAbsolutePath() + "/" + "result.vcf.gz");
		options.setOutFile(out.getAbsolutePath());
		
		
		MergeVCFColumnsCommand merger = new MergeVCFColumnsCommand(loggerService, options);
		
		merger.runCommand();
		
		System.err.println(out.getAbsolutePath());
		
		assertTrue(out.exists());

		
		
		VCFFileReader reader = new VCFFileReader(out,false);
		
		int count = 0;
		for(VariantContext vc : reader) {
			count++;
			Set<String> sampleNames = vc.getSampleNames();
			Genotype gt1 = null;
			Genotype gt2 = null;
			for(String sampleId : sampleNames) {
				if(sampleId.endsWith("_1")) {
					gt1 = vc.getGenotype(sampleId);
					gt2 = vc.getGenotype(sampleId.replaceAll("_1", ""));
				} else {
					gt1 = vc.getGenotype(sampleId);
					gt2 = vc.getGenotype(sampleId + "_1");
				}
				
				assertEquals(gt1.getAllele(0),gt2.getAllele(0));
				assertEquals(gt1.getAllele(1),gt2.getAllele(1));
			}
			
			
		}
		
		assertEquals(59247,count);
		reader.close();
		
		
		out.delete();
	}

}
