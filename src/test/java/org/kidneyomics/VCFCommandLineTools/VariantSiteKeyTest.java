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
	
	@Test
	public void testEquals() {
		String stringKey = "1:123:A:T";
		String stringKey2 = "1:123:A:T";
		String stringKey3 = "1:123:A:A";
		
		VariantSiteKey key = VariantSiteKey.create(stringKey);
		VariantSiteKey key2 = VariantSiteKey.create(stringKey2);
		VariantSiteKey key3 = VariantSiteKey.create(stringKey3);
		
		assertEquals(key,key2);
		assertNotEquals(key, key3);
		
		assertEquals(key.hashCode(),17 * "1:123:A:T".hashCode());
		assertEquals(key3.hashCode(),17 * "1:123:A:A".hashCode());
		
	}

}
