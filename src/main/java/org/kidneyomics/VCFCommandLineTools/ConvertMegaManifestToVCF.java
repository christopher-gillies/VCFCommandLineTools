package org.kidneyomics.VCFCommandLineTools;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.kidneyomics.illumina.array.IlluminaManifestFileReader;
import org.kidneyomics.illumina.array.IlluminaManifestMarker;
import org.kidneyomics.referenceseq.ReferenceFASTA;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;

@Component()
public class ConvertMegaManifestToVCF implements RunCommand {

	
	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public ConvertMegaManifestToVCF(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	@Override
	public void runCommand() {
		
		logger.info("Converting Illumina manifest file to sites vcf");
		File manifest = new File(applicationOptions.getManifest());
		
		String outfile = applicationOptions.getOutFile();
		
		logger.info("Loading sequence");
		ReferenceFASTA reference = ReferenceFASTA.create(new File(applicationOptions.getReferenceSeq()));
		
		SAMSequenceDictionary dict = new SAMSequenceDictionary();
		
		VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
			       .setReferenceDictionary(dict)
			       .setOption(Options.INDEX_ON_THE_FLY)
			       .setBuffer(8192);
		
	    VariantContextWriter writer = builder
		       .setOutputFile(outfile)
		       .build();
		
	    writer.writeHeader(IlluminaManifestMarker.header());
	    
		
	    logger.info("Processing markers");
		try(IlluminaManifestFileReader reader = IlluminaManifestFileReader.create(manifest, reference)) {
	
					
			//writer.write(IlluminaManifestMarker.header();
			
			for(IlluminaManifestMarker marker : reader) {
				if(marker.hasError()) {
					continue;
				}
				
				writer.add(marker.toVariantContext());
				
			}
			
			writer.close();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		logger.info("Converting finished");
	}

}
