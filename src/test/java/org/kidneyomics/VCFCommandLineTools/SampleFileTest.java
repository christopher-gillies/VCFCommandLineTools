package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class SampleFileTest {

	@Test
	public void test() {
		List<SampleFile> list = new ArrayList<SampleFile>();
		
		list.add(new SampleFile().setId("2"));
		list.add(new SampleFile().setId("5"));
		list.add(new SampleFile().setId("7"));
		list.add(new SampleFile().setId("1"));
		
		List<String> idList = SampleFile.getSampleIds(list);
		
		assertEquals(4,idList.size());
		assertEquals("2",idList.get(0));
		assertEquals("5",idList.get(1));
		assertEquals("7",idList.get(2));
		assertEquals("1",idList.get(3));
		
	}

}
