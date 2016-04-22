package org.kidneyomics.stats;

import static org.junit.Assert.*;

import org.junit.Test;

public class HWECalculatorTest {

	@Test
	public void test1() {
		
		//na = 21
		//nab = 5
		//naa = (21 - 5) / 2
		double p = HWECalculator.INSTANCE.SNPHWE(5, 8, 87);
		//System.err.println(p);
		
		assertTrue(p < 0.000001);
	}

	
	@Test
	public void test2() {
		
		//na = 21
		//nab = 7
		//naa = (21 - 7) / 2
		double p = HWECalculator.INSTANCE.SNPHWE(7, 7, 86);
		//System.err.println(p);
		
		assertEquals(1.4247843459546116E-6,p,0.00000001);
	}
	
	@Test
	public void test3() {
		
		//na = 21
		//nab = 9
		//naa = (21 - 9) / 2
		double p = HWECalculator.INSTANCE.SNPHWE(9, 6, 85);
		//System.err.println(p);
		
		assertEquals(0.000048,p,0.000001);
	}
	
	@Test
	public void test4() {
		
		//na = 21
		//nab = 11
		//naa = (21 - 11) / 2
		double p = HWECalculator.INSTANCE.SNPHWE(11,5, 84);
		//System.err.println(p);
		
		assertEquals(0.000919,p,0.000001);
	}
	
	@Test
	public void test5() {
		

		double p = HWECalculator.INSTANCE.SNPHWE(21,0, 79);
		//System.err.println(p);
		
		assertEquals(0.593645,p,0.000001);
	}
}
