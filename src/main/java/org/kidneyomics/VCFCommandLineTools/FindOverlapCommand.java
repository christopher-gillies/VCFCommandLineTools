package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;

import htsjdk.samtools.util.IntervalList;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

@Component
public class FindOverlapCommand implements RunCommand {

	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public FindOverlapCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	@Override
	public void runCommand() {
		Collection<File> vcfs = applicationOptions.getVcfs();
		
		final int minAc  = applicationOptions.getMinAc();

		HashMap<String,HashSet<String>> fileVariants =  new HashMap<String,HashSet<String>>();
		
		for(File vcf : vcfs) {
			
			/*
			 * Create entry for this file in the map
			 */
			
			HashSet<String> variants = new HashSet<String>();
			fileVariants.put(vcf.getAbsolutePath(), variants);
			
			VCFFileReader reader = new VCFFileReader(vcf, false);
			
			int variantCount = 0;
			for(final VariantContext vc : reader) {
				variantCount++;
				
				if(variantCount % 100000 == 0) {
					logger.info("Scanned " + variantCount + " variants");
					logger.info("Currently at " + vc.getContig() + ":" + vc.getStart());
				}
				
				if(vc.isBiallelic() && vc.isSNP()) {
									
					Allele ref = vc.getReference();
					Allele alt = vc.getAlternateAllele(0);
					
					int ac = vc.getAttributeAsInt("AC", -1);
					if(ac == -1) {
						//logger.info("Scanning genotypes b/c not AC in info field");
						ac = vc.getCalledChrCount(alt);
					}
					
					
					
					if(ac >= minAc) {
						
						String variant = VariantKeyRenderer.render(vc.getContig(),  vc.getStart(), ref.getBaseString(), alt.getBaseString());
						
						variants.add(variant);
						
					}
					
					
				}
			}
			
			reader.close();
			
		}
		
		HashSet<String> intersection = null;
		for(File vcf : vcfs) {
			HashSet<String> variants = fileVariants.get(vcf.getAbsolutePath());
			logger.info(vcf.getAbsolutePath());
			logger.info("Biallelic SNPs with ac > " + minAc +": " + variants.size());
			
			if(intersection == null) {
				intersection = variants;
			} else {
				HashSet<String> newIntersection = new HashSet<String>(intersection.size());
				String[] vals = new String[intersection.size()];
				vals = intersection.toArray(vals);
				for(String key : vals) {
					if(variants.contains(key)) {
						newIntersection.add(key);
					}
				}
				intersection = newIntersection;
			}
		}
		
		logger.info("Intersecting Biallelic SNP with ac > " + minAc +": " + intersection.size());
		
		try {
			logger.info("Writting to " + applicationOptions.getOutFile());
			FileUtils.writeLines(new File(applicationOptions.getOutFile()), intersection);
		} catch(Exception e) {
			logger.info(e.getMessage());
		}
		
	}

}
