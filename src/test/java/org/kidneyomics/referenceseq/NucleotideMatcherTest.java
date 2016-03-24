package org.kidneyomics.referenceseq;

import static org.junit.Assert.*;

import org.junit.Test;

public class NucleotideMatcherTest {

	@Test
	public void test() {
		
		assertTrue(NucleotideMatcher.matches('A', 'A'));
		assertTrue(NucleotideMatcher.matches('C', 'C'));
		assertTrue(NucleotideMatcher.matches('G', 'G'));
		assertTrue(NucleotideMatcher.matches('U', 'U'));
		assertTrue(NucleotideMatcher.matches('T', 'T'));
		
		assertFalse(NucleotideMatcher.matches('T', 'A'));
		
		
		assertTrue(NucleotideMatcher.matches('R', 'R'));

		//test R
		assertTrue(NucleotideMatcher.matches('R', 'A'));
		assertTrue(NucleotideMatcher.matches('R', 'G'));
		assertFalse(NucleotideMatcher.matches('R', 'C'));
		assertFalse(NucleotideMatcher.matches('R', 'T'));
		
		
		//test Y
		assertTrue(NucleotideMatcher.matches('C', 'Y'));
		assertTrue(NucleotideMatcher.matches('T', 'Y'));
		assertFalse(NucleotideMatcher.matches('A', 'Y'));
		assertFalse(NucleotideMatcher.matches('G', 'Y'));
		
		//test S
		assertTrue(NucleotideMatcher.matches('S', 'G'));
		assertTrue(NucleotideMatcher.matches('S', 'C'));
		assertFalse(NucleotideMatcher.matches('S', 'A'));
		assertFalse(NucleotideMatcher.matches('S', 'T'));
		
		//test W
		assertTrue(NucleotideMatcher.matches('W', 'A'));
		assertTrue(NucleotideMatcher.matches('W', 'T'));
		assertFalse(NucleotideMatcher.matches('W', 'C'));
		assertFalse(NucleotideMatcher.matches('W', 'G'));
		
		//test K
		assertTrue(NucleotideMatcher.matches('K', 'G'));
		assertTrue(NucleotideMatcher.matches('K', 'T'));
		assertFalse(NucleotideMatcher.matches('K', 'C'));
		assertFalse(NucleotideMatcher.matches('K', 'A'));
		
		//test M
		assertTrue(NucleotideMatcher.matches('M', 'A'));
		assertTrue(NucleotideMatcher.matches('M', 'C'));
		assertFalse(NucleotideMatcher.matches('M', 'G'));
		assertFalse(NucleotideMatcher.matches('M', 'T'));
		
		//test B
		assertTrue(NucleotideMatcher.matches('B', 'C'));
		assertTrue(NucleotideMatcher.matches('B', 'G'));
		assertTrue(NucleotideMatcher.matches('B', 'T'));
		assertFalse(NucleotideMatcher.matches('B', 'A'));
		
		//test D
		assertTrue(NucleotideMatcher.matches('D', 'A'));
		assertTrue(NucleotideMatcher.matches('D', 'G'));
		assertTrue(NucleotideMatcher.matches('D', 'T'));
		assertFalse(NucleotideMatcher.matches('D', 'C'));
		
		//test H
		assertTrue(NucleotideMatcher.matches('H', 'A'));
		assertTrue(NucleotideMatcher.matches('H', 'C'));
		assertTrue(NucleotideMatcher.matches('H', 'T'));
		assertFalse(NucleotideMatcher.matches('H', 'G'));
		
		//test V
		assertTrue(NucleotideMatcher.matches('V', 'A'));
		assertTrue(NucleotideMatcher.matches('V', 'C'));
		assertTrue(NucleotideMatcher.matches('V', 'G'));
		assertFalse(NucleotideMatcher.matches('V', 'T'));
		
		//test N
		assertTrue(NucleotideMatcher.matches('N', 'A'));
		assertTrue(NucleotideMatcher.matches('N', 'C'));
		assertTrue(NucleotideMatcher.matches('N', 'G'));
		assertTrue(NucleotideMatcher.matches('N', 'T'));
		assertFalse(NucleotideMatcher.matches('N', '.'));
		assertFalse(NucleotideMatcher.matches('N', '-'));
	}

}
