package org.kidneyomics.illumina.array;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kidneyomics.referenceseq.ReferenceFASTA;

public class IlluminaManifestFileReader implements AutoCloseable, Iterable<IlluminaManifestMarker>, Iterator<IlluminaManifestMarker> {

	
	/*
	 * Sections
	 * [Heading]
	 * [Assay]
	 * [Controls]
	 */
	
	private BufferedReader reader = null;
	private ReferenceFASTA reference = null;
	private String[] header = null;
	private String nextLine = null;
	private String delimiter = ",";
	
	private IlluminaManifestFileReader(File manifest, ReferenceFASTA reference) {
		
		this.reference = reference;
		
		try {
			reader = Files.newBufferedReader(manifest.toPath(), Charset.defaultCharset());
			
			String line = null;
			
			while((line = reader.readLine()) != null && !line.equals("[Assay]"));
			
			String headerLine = reader.readLine();
			//System.err.println(headerLine);
			header = headerLine.split(delimiter);
			nextLine = reader.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException("Cannot open " + manifest.getAbsolutePath());
		}
		
		
	}
	
	public static IlluminaManifestFileReader create(File manifest, ReferenceFASTA reference) {
		return new IlluminaManifestFileReader(manifest,reference);
	}

	@Override
	public void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}

	@Override
	public Iterator<IlluminaManifestMarker> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return nextLine != null && !nextLine.equals("[Controls]");
	}

	@Override
	public IlluminaManifestMarker next() {
		IlluminaManifestMarker marker = null;
		if(hasNext()) {		
			//System.err.println(nextLine);
			String vals[] = nextLine.split(delimiter);
			Map<String,String> valsMap = new HashMap<>();
			
			if(vals.length != header.length) {
				throw new IllegalStateException("Line does not have enough columns " + nextLine);
			}
			
			for(int i = 0; i < header.length; i++) {
				valsMap.put(header[i], vals[i]);
			}
			
			marker = IlluminaManifestMarker.create(valsMap, reference);
			
			//read next line
			try {
				nextLine = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return marker;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
		
	
}
