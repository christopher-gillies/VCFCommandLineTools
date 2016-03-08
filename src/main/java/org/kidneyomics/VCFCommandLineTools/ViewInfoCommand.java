package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

@Component
public class ViewInfoCommand implements RunCommand {

	
	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public ViewInfoCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	@Override
	public void runCommand() {
		//List<String> sites = applicationOptions.getSites();
		String infile = applicationOptions.getInFile();
		String outfile = applicationOptions.getOutFile();
		
		List<String> infos = applicationOptions.getInfos();
		List<File> vcfs = applicationOptions.getVcfs();
		
		File vcf = vcfs.get(0);
		
		
		
		
		


		
		Writer writer = null;
		try {
			VCFFileReader reader = new VCFFileReader(vcf, false);
			
			if(outfile != null && !outfile.isEmpty()) {
				writer = Files.newBufferedWriter(Paths.get(outfile), Charset.defaultCharset(), StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
			} else {
				writer = new PrintWriter(System.out);
			}
			
			//writer header
			writer.write("Variant\t");
			
			Iterator<String> iter = infos.iterator();
			while(iter.hasNext()) {
				writer.write(iter.next());
				if(iter.hasNext()) {
					writer.write("\t");
				} else {
					writer.write("\n");
				}
			}
			
			List<VariantSiteKey> variants = VariantSiteKey.create(FileUtils.readLines(new File(infile)));
			for(VariantSiteKey variant : variants) {
				
				int count = 0;
				CloseableIterator<VariantContext> vcIter = reader.query(variant.getChr(), variant.getPos(), variant.getPos());
				while(vcIter.hasNext()) {
					VariantContext vc = vcIter.next();
					
					//skip if this variant does not match chr:pos:ref:alt
					if(!variant.matchesKey(vc)) {
						continue;
					}
					
					count++;
					
	
					
					writer.write(variant.getKey());
					writer.write("\t");
					
					//skip
					
					//print out infos for variant
					iter = infos.iterator();
					while(iter.hasNext()) {
						String value = vc.getAttributeAsString(iter.next(), "NA");
						writer.write(value);
						if(iter.hasNext()) {
							writer.write("\t");
						} else {
							writer.write("\n");
						}
					}
					
				}
				
				vcIter.close();
				
				if(count == 0) {
					//variant not found!
					//write nas
					writer.write(variant.getKey());
					writer.write("\t");
					iter = infos.iterator();
					while(iter.hasNext()) {
						writer.write("NA");
						if(iter.hasNext()) {
							writer.write("\t");
						} else {
							writer.write("\n");
						}
					}
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}

}
