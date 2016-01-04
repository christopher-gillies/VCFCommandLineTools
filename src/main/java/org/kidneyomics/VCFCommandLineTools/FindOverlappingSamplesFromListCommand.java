package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.variant.vcf.VCFFileReader;

@Component
public class FindOverlappingSamplesFromListCommand implements RunCommand {

	
	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public FindOverlappingSamplesFromListCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	
	@Override
	public void runCommand() {
		File vcf = applicationOptions.getVcfs().get(0);
		String infile = applicationOptions.getInFile();
		
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(new File(infile));
		} catch(Exception e) {
			logger.info(e.getMessage());
			System.exit(1);
		}
		
		HashSet<String> sampleIds = new HashSet<>();
		sampleIds.addAll(lines);
		
		
		VCFFileReader reader = new VCFFileReader(vcf);
		List<String> samplesInVCF = reader.getFileHeader().getGenotypeSamples();
		reader.close();
		
		
		for(String sample : samplesInVCF) {
			if(sampleIds.contains(sample)) {
				System.out.println(sample);
			}
		}
		
		
	}

}
