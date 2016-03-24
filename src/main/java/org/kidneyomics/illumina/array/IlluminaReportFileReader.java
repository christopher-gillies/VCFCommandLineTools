package org.kidneyomics.illumina.array;

import java.util.Iterator;

public class IlluminaReportFileReader implements AutoCloseable, Iterator<IlluminaManifestMarker> {

	private IlluminaReportFileReader() {
		
	}
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	public static IlluminaReportFileReader create() {
		IlluminaReportFileReader reader = new IlluminaReportFileReader();
		return reader;
	}
	
	/**
	 * 
	 * @param marker -- find the next line if the file that matches the marker
	 * @return
	 */
	public IlluminaReportLine next(IlluminaManifestMarker marker) {
		// TODO 
		return null;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IlluminaManifestMarker next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
