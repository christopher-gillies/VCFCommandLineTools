package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import org.junit.Test;

public class VariantSiteKeyTest {

	@Test
	public void test() {
		String stringKey = "1:123:A:T";
		
		VariantSiteKey key = VariantSiteKey.create(stringKey);
		
		assertEquals(stringKey,key.getKey());
	}

}
