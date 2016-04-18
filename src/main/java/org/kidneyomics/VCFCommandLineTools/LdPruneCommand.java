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

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class LdPruneCommand implements RunCommand {

	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public LdPruneCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	
	@Override
	public void runCommand() {
		
		//r^2
		double maxLd = 0.2;
		String outfile = applicationOptions.getOutFile();
		File vcf = applicationOptions.getVcfs().get(0);
		int windowSizeKb = 1000 * 1000; 
		
		
		
		VCFFileReader reader = new VCFFileReader(vcf, false);
		Iterator<VariantContext> iter = reader.iterator();
		
		VCFHeader header = reader.getFileHeader();
		
		/*
		 * Create encoder
		 */
		VCFEncoder encoder = new VCFEncoder(header, false, false);
		
		BufferedWriter writer = null;
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
				if(lead == null) {
					queue.add(next);
				} else if(lead.getContig().equals(next.getContig()) || next.getStart() - lead.getStart() > windowSizeKb) {
					//if the next variant is from he same chromosome but the position is over 1mb away, then filter the variants
					//filter the queue
					VariantContext head = filterQueue(queue,maxLd, windowSizeKb);
					
					//write the lead marker to file
					writer.write(encoder.encode(head));
					writer.write("\n");
					
					queue.add(next);
				} else if(!lead.getContig().equals(next.getContig())) {
					//different chromosome
					//clear out the queue
					while(queue.peek() != null) {
						//filter polls the queue
						VariantContext head = filterQueue(queue,maxLd,windowSizeKb);
						//write the lead marker to file
						writer.write(encoder.encode(head));
						writer.write("\n");
					}
				} else {
					queue.add(next);
				}
				
				
			}
			
			//filter out remaining variants out of queue
			while(queue.peek() != null) {
				//filter polls the queue
				VariantContext head = filterQueue(queue,maxLd,windowSizeKb);
				//write the lead marker to file
				writer.write(encoder.encode(head));
				writer.write("\n");
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
		
		
	}
	
	static VariantContext filterQueue(Queue<VariantContext> queue, double maxLd, double windowSizeKb) {
		VariantContext vc1 = queue.poll();
		if(vc1 == null) {
			return null;
		}
		
		Iterator<VariantContext> iter = queue.iterator();
		while(iter.hasNext()) {
			VariantContext vc2 = iter.next();
			if(!vc1.getContig().equals(vc2.getContig()) || vc2.getStart() - vc1.getStart() > windowSizeKb) {
				break;
			}
			
			double r2 = VariantContextLdCalculator.INSTANCE.pearsonR2(vc1, vc2);
			
			
			if(r2 > maxLd) {
				iter.remove();
			}
		}
		
		
		return vc1;
		
	}

}
