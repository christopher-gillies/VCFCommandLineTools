package org.kidneyomics.VCFCommandLineTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.kidneyomics.illumina.array.IlluminaManifestFileReader;
import org.kidneyomics.illumina.array.IlluminaManifestMarker;
import org.kidneyomics.illumina.array.IlluminaReportFileReader;
import org.kidneyomics.illumina.array.IlluminaReportLine;
import org.kidneyomics.referenceseq.ReferenceFASTA;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderVersion;

@Component()
public class ConvertStandardReportToVCF implements RunCommand {

	Logger logger;
	ApplicationOptions applicationOptions;

	@Autowired
	public ConvertStandardReportToVCF(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}

	@Override
	public void runCommand() {

		logger.info("Converting Standard Illumina Report to VCF");
		File manifest = new File(applicationOptions.getManifest());
		
		//use infile to store the list of sample ids and manifest files
		// this will be a tab delimited file that contains 
		// [Sample id]	[path to standard report]\n
		
		File sampleListFile = new File(applicationOptions.getInFile());
		
		//read sample files
		List<SampleFile> sampleList = SampleFile.createFromList(sampleListFile, "\t");
		
		//create list of ids
		List<String> sampleIds = SampleFile.getSampleIds(sampleList);
		
		//Create readers
		HashMap<SampleFile, IlluminaReportFileReader> reportReaders = new HashMap<>();
		for(SampleFile sf : sampleList) {
			reportReaders.put(sf, IlluminaReportFileReader.create(sf.getFile()));
		}
		
		String outfile = applicationOptions.getOutFile();
		
		String errorFile = outfile + ".err";

		logger.info("Loading sequence");
		ReferenceFASTA reference = ReferenceFASTA.create(new File(applicationOptions.getReferenceSeq()));

		logger.info("Processing markers and standard reports");
		int errorCount = 0;
		int missingPos = 0;
		int noReferenceAllele = 0;
		int noSurroundingSequenceMatches = 0;
		int total = 0;
		IlluminaManifestFileReader manifestReader = null;
		BufferedWriter writer = null;
		BufferedWriter errorWriter = null;
		try { 
			manifestReader = IlluminaManifestFileReader.create(manifest, reference);

			errorWriter = Files.newBufferedWriter(Paths.get(errorFile), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); 

			

			if(outfile.endsWith(".gz")) {
				BlockCompressedOutputStream outstream = new BlockCompressedOutputStream(new File(outfile));
				writer = new BufferedWriter( new OutputStreamWriter(outstream));
			} else {
				writer = Files.newBufferedWriter(Paths.get(outfile), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}
			
			/*
			 * 
			 * Create the header
			 * 
			 * 
			 */
			//create header and add sample ids
			VCFHeader header = IlluminaManifestMarker.header(sampleIds);
			
			// write header
			writer.write(VCFHeader.METADATA_INDICATOR + VCFHeaderVersion.VCF4_2.getFormatString() + "="
					+ VCFHeaderVersion.VCF4_2.getVersionString());
			writer.write("\n");

			// writer header lines

			for (final VCFHeaderLine line : header.getMetaDataInSortedOrder()) {
				if (VCFHeaderVersion.isFormatString(line.getKey()))
					continue;

				writer.write(VCFHeader.METADATA_INDICATOR);
				writer.write(line.toString());
				writer.write("\n");
			}

			// write out the column line
			writer.write(VCFHeader.HEADER_INDICATOR);
			boolean isFirst = true;
			for (final VCFHeader.HEADER_FIELDS field : header.getHeaderFields()) {
				if (isFirst)
					isFirst = false; // don't write out a field
										// separator
				else
					writer.write(VCFConstants.FIELD_SEPARATOR);
				writer.write(field.toString());
			}
			
			
			//write out sample ids
			 if ( header.hasGenotypingData() ) {
				 writer.write(VCFConstants.FIELD_SEPARATOR);
				 writer.write("FORMAT");
				 for (final String sample : header.getGenotypeSamples() ) {
					 writer.write(VCFConstants.FIELD_SEPARATOR);
					 writer.write(sample);
				 }
			 }

			writer.write("\n");
			writer.flush();

			
			//Create Encoder
			VCFEncoder encoder = IlluminaManifestMarker.encoder(header);
			
			
			for (IlluminaManifestMarker marker : manifestReader) {
				//loop through marker from manifest file
				total++;

				/*
				 * Read marker from each file 
				 */
				float gtScore = 0.0f;
				HashMap<SampleFile,IlluminaReportLine> reportLines = new HashMap<>(2 * sampleIds.size());
				for(Map.Entry<SampleFile, IlluminaReportFileReader> entry : reportReaders.entrySet()) {
					if(entry.getValue().hasNext()) {
						IlluminaReportLine reportLine = entry.getValue().next();
						
						//gtScore should be the same for all variants unless no call
						if(gtScore != 0.0f && gtScore != reportLine.getGtScore() && reportLine.getGtScore() != 0.0f) {
							throw new IllegalStateException("GTScore is not the same for all subjects for " + marker.getName() + " " + gtScore + " " + reportLine.getGtScore() + " " + reportLine.getSampleId());
						}
						gtScore = reportLine.getGtScore();
						
						//make sampleid match the id from the id file
						reportLine.setSampleId(entry.getKey().getId());
						
						reportLines.put(entry.getKey(), reportLine);
					} else {
						throw new IllegalStateException(entry.getKey().getId() + " is missing marker " + marker.getName());
					}
				}
				
				//set gcScore
				marker.setGTScore(gtScore);
				
				if (total % 10000 == 0) {
					logger.info(total + " markers processed. Last marker: " + marker.getName());
				}

				if (marker.hasError()) {
					errorCount++;
					
					errorWriter.write(marker.getIllmId());
					errorWriter.write("\t");
					if (marker.missingPos()) {
						missingPos++;
						errorWriter.write("MISSING_POS,");
					}

					if (!marker.hasReferenceAllele()) {
						errorWriter.write("DOES_NOT_MATCH_REFERENCE,");
						noReferenceAllele++;
					}

					if (!marker.surroundingSequenceMatches()) {
						errorWriter.write("SURROUNDING_SEQUENCE_DOES_NOT_MATCH_REFERENCE");
						noSurroundingSequenceMatches++;
					}
					errorWriter.write("\n");
					continue;
				} else {
					// create genotype list in same order and sampleList
					List<Genotype> genotypes = new ArrayList<Genotype>(sampleIds.size());
					
					for(SampleFile sf : sampleList) {
						
						if(reportLines.containsKey(sf)) {
							Genotype gt = reportLines.get(sf).getGenotype(marker);
							genotypes.add(gt);
						} else {
							throw new IllegalStateException(sf.getId() + " does not have value for " + marker.getName());
						}
						
					}
					
					
					//we need to get genotypes in the correct order
					
					//write marker to file with genotypes
					writer.write(encoder.encode(marker.toVariantContext(genotypes)));
					writer.write("\n");
				}
				
				

	

			}
			
			//close readers and writers
			manifestReader.close();
			errorWriter.close();
			writer.close();
			for(Map.Entry<SampleFile, IlluminaReportFileReader> entry : reportReaders.entrySet()) {
				entry.getValue().close();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			
			
		}
				



		DecimalFormat formater = new DecimalFormat("##.##%");
		logger.info("Converting finished");
		logger.info("Total sites in manifest file: " + total);
		logger.info("Total error sites: " + errorCount);
		logger.info("Total no position sites: " + missingPos);
		logger.info("Total no reference sites: " + noReferenceAllele);
		logger.info(
				"Total no reference sites were surrounding sequence does not match: " + noSurroundingSequenceMatches);
		logger.info("Percent skipped:  " + formater.format(errorCount / (double) total));
		logger.info("Percent missing position:  " + formater.format(missingPos / (double) total));
		logger.info("Percent with no reference:  " + formater.format(noReferenceAllele / (double) total));
		logger.info("Percent of no reference sites were surrounding sequnece does not match: "
				+ formater.format(noSurroundingSequenceMatches / (double) noReferenceAllele));
	}

}
