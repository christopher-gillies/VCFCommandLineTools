package org.kidneyomics.VCFCommandLineTools;


import java.io.IOException;
import java.io.Writer;

import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderVersion;

public class VCFHeaderWriter {
	public static void writeHeader(Writer writer, VCFHeader header) throws IOException {
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
	}
}
