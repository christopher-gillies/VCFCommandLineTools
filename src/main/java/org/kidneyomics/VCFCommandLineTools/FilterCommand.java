package org.kidneyomics.VCFCommandLineTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

/**
 * 
 * @author cgillies
 * This is currently setup for only
 */
@Component()
public class FilterCommand implements RunCommand {

	private Logger logger;
	private ApplicationOptions applicationOptions;
	
	
	private LinkedList<VariantContextFilter> filters = new LinkedList<>();
	
	@Autowired
	public FilterCommand(LoggerService loggerService, ApplicationOptions applicationOptions, VariantContextLdCalculator ldCalc) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	

	
	
	
	@Override
	public void runCommand() {
		
		logger.info("Filtering vcf");
		
		String outfile = applicationOptions.getOutFile();
		logger.info("outfile: " + outfile);
		

		BufferedWriter writer = null;
		

		
		try {
			if(outfile.endsWith(".gz")) {
				BlockCompressedOutputStream outstream = new BlockCompressedOutputStream(new File(outfile));
				writer = new BufferedWriter( new OutputStreamWriter(outstream));
			} else {
				writer = Files.newBufferedWriter(Paths.get(outfile), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}
		


			
			try(FilterableVariantContextReader reader = FilterableVariantContextReader.createByAppliationOptions(applicationOptions)) {
				
				VCFHeader header = reader.getFileHeader();
				
				/*
				 * Create encoder
				 */
				VCFEncoder encoder = new VCFEncoder(header, true, false);
				
				/*
				 * Write header
				 */
				VCFHeaderWriter.writeHeader(writer, header);
				logger.info("Wrote header");
				
				
				for(VariantContext context : reader) {
					writer.write(encoder.encode(context));
					writer.write("\n");
				}
				
				logger.info(reader.getVariantsKept() + " variants passed filters of " + reader.getVariantsRead() + " (" + String.format("%.2f",reader.getFractionVariantsKept() * 100) + "%)");
			}
			
			
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(writer != null) {
					try {
						logger.info("Flushing writer");
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		
	}
	
	


}
