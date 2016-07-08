package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import org.junit.Test;


public class ConcorandResultTest {

	@Test
	public void test() {
		ConcordanceResult res = ConcordanceResult.create("true", "test");
		
		res.update(-1, -1); //nothing
		res.update(-1, 0); //nothing
		res.update(-1, 1); //fp
		res.update(-1, 2); //fp
		
		assertEquals(0, res.tp());
		assertEquals(2, res.fp());
		assertEquals(0, res.tn());
		assertEquals(0, res.fn());
		
		res.update(0, -1); //nothing
		res.update(0, 0); //tn
		res.update(0, 1); //fp
		res.update(0, 2); //fp
		
		assertEquals(0, res.tp());
		assertEquals(4, res.fp());
		assertEquals(1, res.tn());
		assertEquals(0, res.fn());
		
		res.update(1, -1); //fn
		res.update(1, 0); //fn
		res.update(1, 1); //tp
		res.update(1, 2); //tp
		
		assertEquals(2, res.tp());
		assertEquals(4, res.fp());
		assertEquals(1, res.tn());
		assertEquals(2, res.fn());
		
		res.update(2, -1); //fn
		res.update(2, 0); //fn
		res.update(2, 1); //tp
		res.update(2, 2); //tp
		
		//tp = 4
		//fp = 4
		//tn = 1
		//fn = 4
		
		assertEquals(4, res.tp());
		assertEquals(4, res.fp());
		assertEquals(1, res.tn());
		assertEquals(4, res.fn());
		
		assertEquals(4 / (4.0 + 4.0), res.sensitivity(),0.0001);
		assertEquals(1 / (4.0 + 1.0), res.specificity(),0.0001);
		assertEquals(4 / (4.0 + 4.0), res.fdr(),0.0001);
	}

}
