package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.kidneyomics.referenceseq.ReferenceFASTA;
import org.springframework.core.io.ClassPathResource;

public class IlluminaManifestFileReaderTest {

	
	static ReferenceFASTA fasta;
	static {
		ClassPathResource resource = new ClassPathResource("20.fa.gz");
	
		try {
			fasta = ReferenceFASTA.create(resource.getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() throws Exception {
		
		ClassPathResource manifest = new ClassPathResource("manifest.test.chr20.csv");
		
		
		int count = 0;
		int noRefCount = 0;
		int indelCount = 0;
		
		int indelAndNoRefCount = 0;
		
		try(IlluminaManifestFileReader reader =  IlluminaManifestFileReader.create(manifest.getFile(), fasta)) {

			
			for(IlluminaManifestMarker marker : reader) {
				
				if(marker.hasError()) {
					continue;
				}
				//System.err.println(marker);
				count++;
				if(!marker.hasReferenceAllele()) {
					noRefCount++;
				}
				
				if(marker.isIndel()) {
					indelCount++;
				}
				
				if(marker.isIndel() && !marker.hasReferenceAllele()) {
					indelAndNoRefCount++;
				}
				//System.err.println(marker.getName());
				//System.err.println(marker.getRefAllele());
				//System.err.println(marker.getAltAllele());
				//System.err.println(marker.getAlt2Allele());
				//System.err.println(marker.toString());
			}
		}
		
		System.err.println("Count: " + count);
		System.err.println("No ref count: " + noRefCount);
		System.err.println("Indel count: " + indelCount);
		System.err.println("Indel and no ref count: " + indelAndNoRefCount);
		
		assertEquals(6034,count);
	}

}
