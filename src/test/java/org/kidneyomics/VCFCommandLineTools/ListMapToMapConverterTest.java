package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ListMapToMapConverterTest {

	@Test
	public void test() throws IOException {
		ClassPathResource ped = new ClassPathResource("test.ped");
		DelimitedFileParser parser = new DelimitedFileParser();
		List<Map<String,String>> data = parser.parseFile(ped.getFile());
		ListMapToMapConverter converter = new ListMapToMapConverter("#ID","ANC");
		
		Map<String,String> res = converter.convert(data);
		
		assertEquals(3,res.keySet().size());
		assertEquals(3,res.values().size());
		
		assertTrue(res.containsKey("1"));
		assertTrue(res.containsKey("2"));
		assertTrue(res.containsKey("3"));
		
		assertEquals("AMR",res.get("3"));
		
	}

}
