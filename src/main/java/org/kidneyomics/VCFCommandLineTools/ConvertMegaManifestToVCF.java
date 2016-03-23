package org.kidneyomics.VCFCommandLineTools;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;

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
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderVersion;

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

		String errorFile = outfile + ".err";

		logger.info("Loading sequence");
		ReferenceFASTA reference = ReferenceFASTA.create(new File(applicationOptions.getReferenceSeq()));

		logger.info("Processing markers");
		int errorCount = 0;
		int missingPos = 0;
		int noReferenceAllele = 0;
		int noSurroundingSequenceMatches = 0;
		int total = 0;
		try (IlluminaManifestFileReader reader = IlluminaManifestFileReader.create(manifest, reference)) {

			try (BufferedWriter errorWriter = Files.newBufferedWriter(Paths.get(errorFile), Charset.defaultCharset(),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

				try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outfile), Charset.defaultCharset(),
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

					// write header
					writer.write(VCFHeader.METADATA_INDICATOR + VCFHeaderVersion.VCF4_2.getFormatString() + "="
							+ VCFHeaderVersion.VCF4_2.getVersionString());
					writer.write("\n");

					// writer header lines

					for (final VCFHeaderLine line : IlluminaManifestMarker.header().getMetaDataInSortedOrder()) {
						if (VCFHeaderVersion.isFormatString(line.getKey()))
							continue;

						writer.write(VCFHeader.METADATA_INDICATOR);
						writer.write(line.toString());
						writer.write("\n");
					}

					// write out the column line
					writer.write(VCFHeader.HEADER_INDICATOR);
					boolean isFirst = true;
					for (final VCFHeader.HEADER_FIELDS field : IlluminaManifestMarker.header().getHeaderFields()) {
						if (isFirst)
							isFirst = false; // don't write out a field
												// separator
						else
							writer.write(VCFConstants.FIELD_SEPARATOR);
						writer.write(field.toString());
					}

					// if ( header.hasGenotypingData() ) {
					// writer.write(VCFConstants.FIELD_SEPARATOR);
					// writer.write("FORMAT");
					// for (final String sample : header.getGenotypeSamples() )
					// {
					// writer.write(VCFConstants.FIELD_SEPARATOR);
					// writer.write(sample);
					// }
					// }

					writer.write("\n");
					writer.flush();

					for (IlluminaManifestMarker marker : reader) {
						total++;

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
						}

						writer.write(marker.toVCFLine());
						writer.write("\n");

					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
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
