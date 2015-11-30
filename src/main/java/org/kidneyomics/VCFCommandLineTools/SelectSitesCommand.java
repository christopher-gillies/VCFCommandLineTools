package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;

@Component()
public class SelectSitesCommand implements RunCommand {

	
	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public SelectSitesCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	@Override
	public void runCommand() {
		
		String infile = applicationOptions.getInFile();
		String outfile = applicationOptions.getOutFile();
		File vcf = applicationOptions.getVcfs().get(0);
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(new File(infile));
		} catch(Exception e) {
			logger.info(e.getMessage());
			System.exit(1);
		}
		
		HashSet<String> variantsToKeep = new HashSet<String>(lines);
		
		
		VCFFileReader reader = new VCFFileReader(vcf, false);
		
		
		VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
			       .setReferenceDictionary(VCFFileReader.getSequenceDictionary(vcf))
			       .setOption(Options.INDEX_ON_THE_FLY)
			       .setBuffer(8192);
		
		VariantContextWriter writer = builder
			       .setOutputFile(outfile)
			       .build();
		
		writer.writeHeader(reader.getFileHeader());
		
		
		int readCount = 0;
		int writeCount = 0;
		for(VariantContext vc : reader) {
			readCount++;
			
			if(readCount % 100000 == 0) {
				logger.info("Read " + readCount + " lines");
			}
			
			if(vc.isBiallelic() && vc.isSNP()) {

				
				Allele ref = vc.getReference();
				Allele alt = vc.getAlternateAllele(0);
				String variant = VariantKeyRenderer.render(vc.getContig(),  vc.getStart(), ref.getBaseString(), alt.getBaseString());
				if(variantsToKeep.contains(variant)) {
					writeCount++;
					
					if(writeCount % 100000 == 0) {
						logger.info("Wrote " + writeCount + " lines");
						logger.info("Current key " + variant);
					}
					
					writer.add(vc);
				}
			}
		}
		
		reader.close();
		
		writer.close();
		
		
		
	}

}
