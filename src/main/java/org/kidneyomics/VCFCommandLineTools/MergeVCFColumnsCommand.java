package org.kidneyomics.VCFCommandLineTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.IllegalSelectorException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;

@Component
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
		
		logger.info("MergeVCFColumnsCommand");
		/*
		 * Assumptions
		 * (1) Only two vcfs that are sorted with the same contig order
		 * (2) if contigs on it same order, then we will just skip that contig
		 * (3) No overlapping samples allowed
		 * 
		 * Output:
		 * A vcf where intersecting sites are merged together and will only return biallelic markers
		 * the info field will be cleared
		 * the only GT FORMAT field will be there
		 */
		
		Collection<File> vcfs = applicationOptions.getVcfs();
		String outfile = applicationOptions.getOutFile();
		
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
		
		
		
		/*
		 * Merge headers
		 */
		VCFHeader header1 = reader1.getFileHeader();
		
		VCFHeader header2 = reader2.getFileHeader();
		
		List<String> samples1 = header1.getGenotypeSamples();
		List<String> samples2 = header2.getGenotypeSamples();
		
		List<String> mergedSamples = new ArrayList<>(samples1.size() + samples2.size());
		mergedSamples.addAll(samples1);
		mergedSamples.addAll(samples2);
		
		//Validate that there are no duplicates
		HashSet<String> sampleSet = new HashSet<String>();
		for(String id : mergedSamples) {
			if(sampleSet.contains(id)) {
				throw new IllegalArgumentException("Duplicate id found: " + id);
			} else {
				sampleSet.add(id);
			}
		}
		
		
		HashSet<VCFHeaderLine> meta = new HashSet<>();
		meta.add(new VCFFormatHeaderLine("GT",1,VCFHeaderLineType.String,"GT"));
		meta.addAll(header1.getContigLines());
		VCFHeader mergedHeader = new VCFHeader(meta, mergedSamples);
		

		/*
		 * Create encoder
		 */
		VCFEncoder encoder = new VCFEncoder(mergedHeader, false, false);
		
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
			VCFHeaderWriter.writeHeader(writer, mergedHeader);
			logger.info("Wrote header");
			
			
			VariantContext previous1 = null;
			VariantContext previous2 = null;
			
			int count = 0;
			
			int countFile1 = 0;
			int countFile2 = 0;
			
			boolean usePrevious1 = false;
			boolean usePrevious2 = false;
			while(iter1.hasNext() || iter2.hasNext()) {
				
				if( (iter1.hasNext() || usePrevious1)  && (iter2.hasNext() || usePrevious2 )) {
					
					VariantContext variant1 = null;
					
					VariantContext variant2 = null;
					
//					if(usePrevious1 == true && usePrevious2 == true && comparator.compare(previous1,previous2) != 0) {
//						//then skip both
//						usePrevious1 = false;
//						usePrevious2 = false;
//					}
					
					if(usePrevious1) {
						variant1 = previous1;
					} else {
						variant1 = iter1.next();
						countFile1++;
					}
					
					if(usePrevious2) {
						variant2 = previous2;
					} else {
						variant2 = iter2.next();
						countFile2++;
					}
					
					
					//check that variants are ordered correctly
					if(previous1 != null && previous1 != variant1 && comparator.compare(previous1, variant1) > 0) {
						throw new IllegalStateException(previous1.getContig() + ":" + previous1.getStart() + " > " + variant1.getContig() + ":" + variant1.getStart());
					}
					
					if(previous2 != null && previous2 != variant2 && comparator.compare(previous2, variant2) > 0) {
						throw new IllegalStateException(previous2.getContig() + ":" + previous2.getStart() + " > " + variant2.getContig() + ":" + variant2.getStart());
					}
					
					int cmp = comparator.compare(variant1, variant2);
					
					if(cmp < 0) {
						//logger.info("Skipping VCF1: " + variant1.getContig() + ":" + variant1.getStart() + "\t" + variant1.getReference().toString() +  "\t" + variant1.getAlternateAlleles());
						
						if(usePrevious1 == true && usePrevious2 == true) {
							//variant1 < variant2
							//we need to go to next variant in vcf1
							usePrevious1 = false;
						}
						
						usePrevious2 = true;
					} else if(cmp > 0) {
						
						if(usePrevious1 == true && usePrevious2 == true) {
							//variant1 > variant2
							//we need to go to next variant in vcf2
							usePrevious2 = false;
						}
						
						usePrevious1 = true;
						//logger.info("Skipping VCF2: " + variant2.getContig() + ":" + variant2.getStart() + "\t" + variant2.getReference().toString() +  "\t" + variant2.getAlternateAlleles());
					} else {
						// they equal position
						usePrevious1 = false;
						usePrevious2 = false;
						
						if(variant1.isBiallelic() && variant2.isBiallelic() 
								&& variant1.getReference().equals(variant2.getReference()) &&
								variant1.getAlternateAllele(0).equals(variant2.getAlternateAllele(0))) {
							
							//TODO: Finish merging
							//both variants are bialleleic and the reference and alternative alleles match
							
							count++;
							if(count % 10000 == 0) {
								logger.info(count + " mergeable variants found");
							}
							
							
							VariantContext merged = VariantContextMerger.merge(variant1, variant2);
							
							writer.write(encoder.encode(merged));
							writer.write("\n");
							
						} else {
							//skip if they do not equal
							
				//			logger.info("Skipping: " + variant1.getContig() + ":" + variant1.getStart() + "\t" + variant1.getReference().toString() +  "\t" + variant1.getAlternateAlleles());
				//			logger.info("Skipping: " + variant2.getContig() + ":" + variant2.getStart() + "\t" + variant2.getReference().toString() +  "\t" + variant2.getAlternateAlleles());
						}
					}
					
					
					previous1 = variant1;
					previous2 = variant2;
				} else if(iter1.hasNext()) {
					//just skip remaining variants
					VariantContext current = iter1.next();
					countFile1++;
					
					if(previous1 != null && current != null && comparator.compare(previous1, current) > 0) {
						throw new IllegalStateException(previous1.getContig() + ":" + previous1.getStart() + " > " + current.getContig() + ":" + current.getStart());
					}
					
					previous1 = current;
					
					//logger.info("Skipping: " + previous1.getContig() + ":" + previous1.getStart() + "\t" + previous1.getReference().toString() +  "\t" + previous1.getAlternateAlleles());
					
				} else if(iter2.hasNext()) {
					//just skip remaining variants
					//fixed bug/ was iter1 changed to iter2
					VariantContext current = iter2.next();
					countFile2++;
					
					if(previous2 != null && current != null && comparator.compare(previous2, current) > 0) {
						throw new IllegalStateException(previous2.getContig() + ":" + previous2.getStart() + " > " + current.getContig() + ":" + current.getStart());
					}
					
					previous2 = current;
					
					//logger.info("Skipping: " + previous2.getContig() + ":" + previous2.getStart() + "\t" + previous2.getReference().toString() +  "\t" + previous2.getAlternateAlleles());
				} else {
					throw new IllegalStateException("Error should not of reached this point");
				}
				
			} 
			
			reader1.close();
			reader2.close();
			
			logger.info(count + "  merged variants");
			logger.info(countFile1 + "  variants in " + vcf1.getAbsolutePath());
			logger.info(countFile2 + "  variants in " + vcf2.getAbsolutePath());
			
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
		
		
		
		
		logger.info("finished merging vcfs");
		
	}

}
