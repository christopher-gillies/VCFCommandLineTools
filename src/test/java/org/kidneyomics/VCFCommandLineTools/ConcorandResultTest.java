package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kidneyomics.VCFCommandLineTools.ConcordanceResult.COUNT_TYPE;


public class ConcorandResultTest {

	@Test
	public void test() {
		ConcordanceResult res = ConcordanceResult.create("true", "test");
		
		assertEquals(ConcordanceResult.COUNT_TYPE.UNKNOWN, res.update(-1, -1)); //nothing
		assertEquals(ConcordanceResult.COUNT_TYPE.UNKNOWN,res.update(-1, 0)); //nothing
		assertEquals(ConcordanceResult.COUNT_TYPE.FP,res.update(-1, 1)); //fp
		assertEquals(ConcordanceResult.COUNT_TYPE.FP,res.update(-1, 2)); //fp
		
		assertEquals(0, res.tp());
		assertEquals(2, res.fp());
		assertEquals(0, res.tn());
		assertEquals(0, res.fn());
		
		assertEquals(ConcordanceResult.COUNT_TYPE.UNKNOWN,res.update(0, -1)); //nothing
		assertEquals(ConcordanceResult.COUNT_TYPE.TN,res.update(0, 0)); //tn
		assertEquals(ConcordanceResult.COUNT_TYPE.FP,res.update(0, 1)); //fp
		assertEquals(ConcordanceResult.COUNT_TYPE.FP,res.update(0, 2)); //fp
		
		assertEquals(0, res.tp());
		assertEquals(4, res.fp());
		assertEquals(1, res.tn());
		assertEquals(0, res.fn());
		
		assertEquals(ConcordanceResult.COUNT_TYPE.FN,res.update(1, -1)); //fn
		assertEquals(ConcordanceResult.COUNT_TYPE.FN,res.update(1, 0)); //fn
		assertEquals(ConcordanceResult.COUNT_TYPE.TP,res.update(1, 1)); //tp
		assertEquals(ConcordanceResult.COUNT_TYPE.TP,res.update(1, 2)); //tp
		
		assertEquals(2, res.tp());
		assertEquals(4, res.fp());
		assertEquals(1, res.tn());
		assertEquals(2, res.fn());
		
		assertEquals(ConcordanceResult.COUNT_TYPE.FN,res.update(2, -1)); //fn
		assertEquals(ConcordanceResult.COUNT_TYPE.FN,res.update(2, 0)); //fn
		assertEquals(ConcordanceResult.COUNT_TYPE.TP,res.update(2, 1)); //tp
		assertEquals(ConcordanceResult.COUNT_TYPE.TP,res.update(2, 2)); //tp
		
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

	@Test
	public void testLog() {
		ConcordanceResult res = ConcordanceResult.create("true", "test");
		
		assertEquals("A\t1\tB\t0\tFN",res.log("A", 1, "B", 0, COUNT_TYPE.FN));
		assertEquals("A\t-1\tB\t-1\tUNKNOWN",res.log("A",-1, "B",-1, COUNT_TYPE.UNKNOWN));
		
		assertEquals(2,res.getLog().size());
	}
}
