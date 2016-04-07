package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class MergeVCFColumnsCommand implements RunCommand {

	
	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public MergeVCFColumnsCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	
	@Override
	public void runCommand() {
		/*
		 * Assumptions
		 * (1) Only two vcfs that are sorted with the same contig order
		 * (2) if contigs on it same order, then we will just skip that contig
		 * (3) No overlapping samples allowed
		 * 
		 * Output:
		 * A vcf where intersecting sites are merged together and will only return biallelic markers
		 * 
		 */
		
		Collection<File> vcfs = applicationOptions.getVcfs();
		
		if(vcfs.size() != 2) {
			throw new IllegalArgumentException("This function requires exactly two vcfs");
		}
		
		Iterator<File> vcfFileIter = vcfs.iterator();
		
		File vcf1 = vcfFileIter.next();
		File vcf2 = vcfFileIter.next();
		VCFFileReader reader1 = new VCFFileReader(vcf1, false);
		VCFFileReader reader2 = new VCFFileReader(vcf2, false);
		
		Iterator<VariantContext> iter1 = reader1.iterator();
		Iterator<VariantContext> iter2 = reader2.iterator();
		
		VariantContextComparator comparator = new VariantContextComparator();
		
		
		while(iter1.hasNext() || iter2.hasNext()) {
			
			if(iter1.hasNext() && iter2.hasNext()) {
				
				
				VariantContext variant1 = iter1.next();
				VariantContext variant2 = iter2.next();
				
				
				
				int cmp = comparator.compare(variant1, variant2);
				
				if(cmp < 0) {
					iter1.next();
				} else if(cmp > 0) {
					iter2.next();
				} else {
					// they equal position
					
					if(variant1.isBiallelic() && variant2.isBiallelic() 
							&& variant1.getReference().equals(variant2.getReference()) &&
							variant1.getAlternateAllele(0).equals(variant2.getAlternateAllele(0))) {
						
						//TODO: Finish merging
						//both variants are bialleleic and the reference and alternative alleles match
						
						
						
						
					} else {
						//skip
						iter1.next();
						iter2.next();
					}
				}
				
				
			} else if(iter1.hasNext()) {
				//just skip remaining variants
				iter1.next();
			} else if(iter2.hasNext()) {
				//just skip remaining varaints
				iter2.next();
			} else {
				throw new IllegalStateException("Error should not of reached this point");
			}
			
		}
		
		reader1.close();
		reader2.close();
		
		
	}

}
