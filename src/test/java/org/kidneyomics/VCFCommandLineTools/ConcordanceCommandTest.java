package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ConcordanceCommandTest {

	@Test
	public void testConcordance() throws IOException {
		
		LoggerService logger = new LoggerService();
		ApplicationOptions options = new ApplicationOptions(logger);
		
		ClassPathResource vcf1 = new ClassPathResource("ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz");
		
		//Use the same vcf;
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		options.addVcfFile(vcf1.getFile().getAbsolutePath());
		
		options.addSample("HG01049");
		options.addSample("HG01049");
		
		options.setMinAc(10);
		
		options.setMaxLd(0.2);
		options.setWindowSizeKb(100);
		
		
		ConcordanceCommand command = new ConcordanceCommand(logger, options);
		
		command.runCommand();

		
		ConcordanceResult res = command.getLastResult();
		
		assertNotNull(res);
		assertEquals(res.sensitivity(),1.0,0.0001);
		assertEquals(res.specificity(),1.0,0.0001);
		assertEquals(res.fdr(),0.0,0.0001);

		
	}

}
