package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class DelimitedParserTest {

	@Test
	public void test() throws IOException {
		ClassPathResource ped = new ClassPathResource("test.ped");
		DelimitedFileParser parser = new DelimitedFileParser();
		List<Map<String,String>> data = parser.parseFile(ped.getFile());
		
		assertEquals(3,data.size());
		
		assertEquals("1",data.get(0).get("#ID"));
		assertEquals("2",data.get(1).get("#ID"));
		assertEquals("3",data.get(2).get("#ID"));
		
		assertEquals("EUR",data.get(0).get("ANC"));
		assertEquals("AFR",data.get(1).get("ANC"));
		assertEquals("AMR",data.get(2).get("ANC"));
	}
	
	@Test
	public void test2() throws IOException {
		ClassPathResource ped = new ClassPathResource("omni_samples.20141118.panel");
		DelimitedFileParser parser = new DelimitedFileParser();
		List<Map<String,String>> data = parser.parseFile(ped.getFile());
		
		assertEquals(2318,data.size());
	}

}
