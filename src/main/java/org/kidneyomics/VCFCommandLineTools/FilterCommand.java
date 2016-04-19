package org.kidneyomics.VCFCommandLineTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	Logger logger;
	ApplicationOptions applicationOptions;
	VariantContextLdCalculator ldCalc;
	
	LinkedList<VariantContextFilter> filters = new LinkedList<>();
	
	@Autowired
	public FilterCommand(LoggerService loggerService, ApplicationOptions applicationOptions, VariantContextLdCalculator ldCalc) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
		this.ldCalc = ldCalc;
	}
	
	
	@Override
	public void runCommand() {
		
		logger.info("Filtering vcf");
		
		//r^2
		double maxLd = applicationOptions.getMaxLd();
		String outfile = applicationOptions.getOutFile();
		File vcf = applicationOptions.getVcfs().get(0);
		int windowSizeKb = applicationOptions.getWindowSizeKb();
		int windowSizeBp = windowSizeKb * 1000;
		logger.info("Options in effect");
		logger.info("vcf: " + vcf.getAbsolutePath());
		logger.info("maxLd: " + maxLd);
		logger.info("windowSizeKb: " + windowSizeKb);
		logger.info("outfile: " + outfile);
		
		VCFFileReader reader = new VCFFileReader(vcf, false);
		Iterator<VariantContext> iter = reader.iterator();
		
		VCFHeader header = reader.getFileHeader();
		
		/*
		 * Create encoder
		 */
		VCFEncoder encoder = new VCFEncoder(header, false, false);
		
		BufferedWriter writer = null;
		
		int variantsRead = 0;
		int variantsKept = 0;
		
		try {
			if(outfile.endsWith(".gz")) {
				BlockCompressedOutputStream outstream = new BlockCompressedOutputStream(new File(outfile));
				writer = new BufferedWriter( new OutputStreamWriter(outstream));
			} else {
				writer = Files.newBufferedWriter(Paths.get(outfile), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}
		
			/*
			 * Write header
			 */
			VCFHeaderWriter.writeHeader(writer, header);
			logger.info("Wrote header");
			
			Queue<VariantContext> queue = new LinkedList<VariantContext>();
			

			
			
			while(iter.hasNext()) {
				
				VariantContext lead = queue.peek();
				VariantContext next = iter.next();
				variantsRead++;
				
				if(variantsRead % 10000 == 0) {
					logger.info(variantsRead + " variants read");
				}
				
				if(lead == null) {
					queue.add(next);
				} else if(lead.getContig().equals(next.getContig()) && next.getStart() - lead.getStart() > windowSizeBp) {
					//if the next variant is from he same chromosome but the position is over 1mb away, then filter the variants
					//filter the queue
					VariantContext head = applyFilters(queue,maxLd, windowSizeBp);
					
					if(head != null) {
						//write the lead marker to file
						variantsKept++;
						if(variantsKept % 10000 == 0) {
							logger.info(variantsKept + " variants kept");
						}
						writer.write(encoder.encode(head));
						writer.write("\n");
					}
					
					queue.add(next);
				} else if(!lead.getContig().equals(next.getContig())) {
					//different chromosome
					//clear out the queue
					while(queue.peek() != null) {
						//filter polls the queue
						VariantContext head = applyFilters(queue,maxLd,windowSizeBp);
						
						if(head != null) {
							variantsKept++;
							//write the lead marker to file
							if(variantsKept % 10000 == 0) {
								logger.info(variantsKept + " variants kept");
							}
							writer.write(encoder.encode(head));
							writer.write("\n");
						}
					}
				} else {
					queue.add(next);
				}
				
				
			}
			
			//filter out remaining variants out of queue
			while(queue.peek() != null) {
				//filter polls the queue
				VariantContext head = applyFilters(queue,maxLd,windowSizeBp);
				
				if(head != null) {
					//write the lead marker to file
					variantsKept++;
					if(variantsKept % 10000 == 0) {
						logger.info(variantsKept + " variants kept");
					}
					writer.write(encoder.encode(head));
					writer.write("\n");
				}
			}
			
			reader.close();
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
		
		logger.info(variantsKept + " variants passed filters of " + variantsRead + " (" + String.format("%.2f",(variantsKept/ (double) variantsRead) * 100) + "%)");
	}
	
	
	VariantContext applyFilters(Queue<VariantContext> queue, double maxLd, int windowSizeBp) {
		
		//apply single variant filters
		VariantContext vc = queue.peek();
		if(vc == null) {
			return null;
		}
		
		//apply single filters
		for(VariantContextFilter filter : filters) {
			if(!filter.keep(vc)) {
				return null;
			}
		}
		
		//apply ld filter
		if(maxLd == 1.0) {
			//no need to filter subsequent markers
			vc = queue.poll();
		} else {
			vc = filterQueueLd(queue,maxLd,windowSizeBp);
		}
		return vc;
	}
	
	/**
	 * 
	 * 
	 * @param queue -- queue that is sorted in genomic coordinate order
	 * @param maxLd -- maximum allowable ld between the lead snp at other markers
	 * @param windowSizeKb -- the maximum window size in kilobases
	 * @return returns the lead marker that is popped off of the queue, and removes all markers whose LD is greater than maxLd
	 */
	VariantContext filterQueueLd(Queue<VariantContext> queue, double maxLd, int windowSizeBp) {
		VariantContext vc1 = queue.poll();
		if(vc1 == null) {
			return null;
		}
		
		Iterator<VariantContext> iter = queue.iterator();
		while(iter.hasNext()) {
			VariantContext vc2 = iter.next();
			if(!vc1.getContig().equals(vc2.getContig()) || vc2.getStart() - vc1.getStart() > windowSizeBp) {
				break;
			}
			
			double r2 = ldCalc.pearsonR2(vc1, vc2);
			
			
			if(r2 > maxLd) {
				iter.remove();
			}
		}
		
		
		return vc1;
		
	}

}
