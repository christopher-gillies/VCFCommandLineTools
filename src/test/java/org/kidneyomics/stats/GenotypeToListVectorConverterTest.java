package org.kidneyomics.stats;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.kidneyomics.VCFCommandLineTools.SimpleGenotypeBuilder;
import org.renjin.sexp.DoubleVector;
import org.renjin.sexp.IntVector;
import org.renjin.sexp.ListVector;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;

public class GenotypeToListVectorConverterTest {

	@Test
	public void test() throws ScriptException {

		VCFHeader header = new VCFHeader();
		header.addMetaDataLine(new VCFFormatHeaderLine("Scores", 2, VCFHeaderLineType.Float, ""));
		GenotypeToListVectorConverter converter = new GenotypeToListVectorConverter(header);

		
		LinkedList<Genotype> genotypes = new LinkedList<>();
		
		
		Allele ref = Allele.create("A", true);
		Allele alt = Allele.create("T", false);
		
		HashMap<String,Object> atts  = new HashMap<>();
		atts.put("Scores", new Double[]{99.0, 55.0});
		for(int i = 0; i < 100; i ++) {
			genotypes.add(SimpleGenotypeBuilder.createWithAttributes("id" + i,atts, ref,alt));
		}
		
		for(int i = 100; i < 200; i++) {
			genotypes.add(SimpleGenotypeBuilder.createWithAttributes("id" + i,atts, ref,ref));
		}
		
		genotypes.add(SimpleGenotypeBuilder.createWithAttributes("id" + 200,atts, alt,alt));
		
		ListVector vector = converter.convert(genotypes);
		
		
	    ScriptEngineManager manager = new ScriptEngineManager();
	    // create a Renjin engine:
	    ScriptEngine engine = manager.getEngineByName("Renjin");
	    // check if the engine has loaded correctly:
	    if(engine == null) {
	        throw new RuntimeException("Renjin Script Engine not found on the classpath.");
	    }
	    
	    engine.put("gtInfo", vector);
	    engine.eval("print(gtInfo);gts = sapply(gtInfo,FUN=function(x){ x$GT})");
	    engine.eval("s = sum(gts)");
	    IntVector sum = (IntVector) engine.get("s");
	    assertEquals(102,(int) sum.getElementAsObject(0));
	    
	    
	    String script = "n = length(gts);"
	    		+ "o11 = sum(gts == 0);"
	    		+ "o21 = sum(gts == 1);"
	    		+ "o22 = sum(gts == 2);"
	    		+ "p = (2 * o11 + o21 ) / (2 * n);"
	    		+ "print(p);"
	    		+ "q = 1 - p;"
	    		+ "n11 = p^2*n;"
	    		+ "n21 = 2 * p * q * n;"
	    		+ "n22 = q^2 * n;"
	    		+ "print(n22);"
	    		+ "G = -2 * ( n11 * log(o11/n11) + n21 * log(o21/n21) + n22 * log(o22/n22));"
	    		+ "print(G);"
	    		+ "pval = pchisq(G,1,lower.tail=F);";
	    System.err.println(script);
	    
	    engine.eval(script);
	    DoubleVector pval = (DoubleVector) engine.get("pval");
	    System.err.println(pval);
	    
	    assertTrue(pval.asReal() < 0.05);
	    
	    
	    engine.put("gtInfo", vector);
	    engine.eval("gcScores = sapply(gtInfo,FUN=function(x){ x$Scores[2]})");
	    engine.eval("meanScore = mean(gcScores);print(meanScore)");
	    
	    DoubleVector gc = (DoubleVector) engine.get("meanScore");
	    assertEquals(55.0,gc.asReal(),0.0001);
	}

}
