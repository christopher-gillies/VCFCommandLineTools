package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
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
	public void test() throws IOException {
		
		ClassPathResource manifest = new ClassPathResource("manifest.test.chr20.csv");
		
		IlluminaManifestFileReader reader =  IlluminaManifestFileReader.create(manifest.getFile(), fasta);
		
		for(IlluminaManifestMarker marker : reader) {
			System.err.println(marker);
		}
		
		
		
	}

}
