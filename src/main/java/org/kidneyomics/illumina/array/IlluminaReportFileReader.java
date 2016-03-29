package org.kidneyomics.illumina.array;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.StringUtils;

import htsjdk.samtools.util.StringUtil;

public class IlluminaReportFileReader implements AutoCloseable, Iterator<IlluminaReportLine>, Iterable<IlluminaReportLine> {

	private final BufferedReader reader;
	private final String[] header;
	private String nextLine = null;
	private final String delimiter = "\t";
	
	private IlluminaReportFileReader(File report) {

		try {
			reader = Files.newBufferedReader(report.toPath(), Charset.defaultCharset());
			
			String line = null;
			
			//skip until we get to the "[Data]" line
			while((line = reader.readLine()) != null && !line.equals("[Data]"));
			
			String headerLine = reader.readLine();
			//System.err.println(headerLine);
			header = headerLine.split(delimiter);
			nextLine = reader.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException("Cannot open " + report.getAbsolutePath());
		}
		
	}
	
	@Override
	public void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}
	
	
	public static IlluminaReportFileReader create(File report) {
		IlluminaReportFileReader reader = new IlluminaReportFileReader(report);
		return reader;
	}
	

	@Override
	public boolean hasNext() {
		return nextLine != null && !nextLine.isEmpty();
	}

	@Override
	public IlluminaReportLine next() {
		IlluminaReportLine reportLine = null;
		if(hasNext()) {		
			//System.err.println(nextLine);
			String vals[] = nextLine.split(delimiter);
			Map<String,String> valsMap = new HashMap<>();
			
			if(vals.length > header.length) {
				throw new IllegalStateException("Line has too many columns " + nextLine + "\n" + StringUtils.arrayToDelimitedString(header, delimiter) + "\n" + vals.length + " != " + header.length);
			}
			
			for(int i = 0; i < vals.length; i++) {
				valsMap.put(header[i], vals[i]);
			}
			
			reportLine = IlluminaReportLine.create(valsMap);
			
			//read next line
			try {
				nextLine = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return reportLine;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<IlluminaReportLine> iterator() {
		return this;
	}
	
	
	
}
