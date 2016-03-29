package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;


import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class IlluminaReportFileReaderTest {

	@Test
	public void test() throws Exception {
		ClassPathResource report = new ClassPathResource("HG00100.chr20.txt");
		
		int count = 0;
		int missingCount = 0;
		try(IlluminaReportFileReader reader = IlluminaReportFileReader.create(report.getFile())) {
			
			for(IlluminaReportLine line : reader) {
				System.err.println(line);
				count++;
				
				if(line.noCall()) {
					missingCount++;
				}
			}
			
		}
		
		System.err.println("The number of lines is " + count);
		System.err.println("The number of no calls is " + missingCount);
		
		assertEquals(6209,count);
		assertEquals(466,missingCount);
	} 
}
