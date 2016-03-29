package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class ConvertStandardReportToVCFTest {

	@Test
	public void test() throws IOException {
		
		ApplicationOptions options = new ApplicationOptions(new LoggerService());
		
		
		//need to set
		//infile
		//manifest file
		//reference file
		//outfile
		
		File tempDir = FileUtils.getTempDirectory();
		
		ClassPathResource refSeq = new ClassPathResource("20.fa.gz");
		
		String outFile = tempDir.getAbsolutePath() + "/out.vcf.gz";
		
		File outFileRef = new File(outFile);
		if(outFileRef.exists()) {
			outFileRef.delete();
		}
		
		System.err.println("Writing vcf to " + outFile);
		
		ClassPathResource manifestFile = new ClassPathResource("manifest.test.chr20.csv");
		
		options.setManifest(manifestFile.getFile().getAbsolutePath());
		options.setOutFile(outFile);
		options.setReferenceSeq(refSeq.getFile().getAbsolutePath());
		
		//create infile
		List<String> lines = new LinkedList<String>();
		
		ClassPathResource s1 = new ClassPathResource("HG00100.chr20.txt");
		ClassPathResource s2 = new ClassPathResource("HG00138.chr20.txt");
		ClassPathResource s3 = new ClassPathResource("HG00160.chr20.txt");
		
		lines.add( "HG00100\t" + s1.getFile().getAbsolutePath());
		lines.add( "HG00138\t" + s2.getFile().getAbsolutePath());
		lines.add( "HG00160\t" + s3.getFile().getAbsolutePath());
		
		File inFile = new File(tempDir.getAbsolutePath() + "/reports.txt");
		
		FileUtils.writeLines(inFile, lines);
		options.setInFile(inFile.getAbsolutePath());
		
		ConvertStandardReportToVCF converter = new ConvertStandardReportToVCF(new LoggerService(), options);
		
		
		
		converter.runCommand();
		
		//validate that the file was created
		assertTrue(outFileRef.exists());
		//check the number of lines makes sense
		
		VCFFileReader vcfReader = new VCFFileReader(outFileRef,false);
		int count = 0;
		
		boolean found9286330 = false;
		boolean found56135934 = false;
		for(VariantContext vc : vcfReader) {
			count++;
			
			if(vc.getStart() == 9286330) {
				found9286330 = true;
				Genotype gt1 = vc.getGenotype("HG00100");
				assertTrue(gt1.isHomVar());
				
				Genotype gt2 = vc.getGenotype("HG00138");
				assertTrue(gt2.isHet());
				
				Genotype gt3 = vc.getGenotype("HG00160");
				assertTrue(gt3.isHet());
			}
			
			if(vc.getStart() == 56135934) {
				found56135934 = true;
				Genotype gt1 = vc.getGenotype("HG00100");
				assertTrue(gt1.isHet());
				
				Genotype gt2 = vc.getGenotype("HG00138");
				assertTrue(gt2.isHet());
				
				Genotype gt3 = vc.getGenotype("HG00160");
				assertTrue(gt3.isHomRef());
			}
		}
		
		assertTrue(found9286330);
		assertTrue(found56135934);
		
		vcfReader.close();
		assertEquals(6209 - 175,count);
		
		
		
		outFileRef.delete();
		inFile.delete();
		
	}

}
