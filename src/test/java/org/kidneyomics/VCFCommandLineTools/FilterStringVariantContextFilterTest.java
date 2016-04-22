package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

public class FilterStringVariantContextFilterTest {

	@Test
	public void test() {
		
	    ScriptEngineManager manager = new ScriptEngineManager();
	    // create a Renjin engine:
	    ScriptEngine engine = manager.getEngineByName("Renjin");
	    // check if the engine has loaded correctly:
	    if(engine == null) {
	        throw new RuntimeException("Renjin Script Engine not found on the classpath.");
	    }
	    
	    VariantContextBuilder builder = new VariantContextBuilder();
	    
		LinkedList<Genotype> genotypes = new LinkedList<>();
		
		
		Allele ref = Allele.create("A", true);
		Allele alt = Allele.create("T", false);
		
		LinkedList<Allele> alleles = new LinkedList<Allele>();
		alleles.add(ref);
		alleles.add(alt);
		
		HashMap<String,Object> atts  = new HashMap<>();
		atts.put("Scores", new double[]{99.0, 55.0});
		for(int i = 0; i < 100; i ++) {
			genotypes.add(SimpleGenotypeBuilder.createWithAttributes("id" + i,atts, ref,alt));
		}
		
		for(int i = 100; i < 200; i++) {
			genotypes.add(SimpleGenotypeBuilder.createWithAttributes("id" + i,atts, ref,ref));
		}
		
		genotypes.add(SimpleGenotypeBuilder.createWithAttributes("id" + 200,atts, alt,alt));
		
		builder.alleles(alleles);
		
		builder.start(100);
		builder.stop(100);
		builder.chr("1");
	    builder.attribute("GCScore", 0.9);
		builder.genotypes(genotypes);
		
	    VCFHeader header = new VCFHeader();
	    header.addMetaDataLine(new VCFInfoHeaderLine("GCScore", 1, VCFHeaderLineType.Float, ""));
	    
		String script = "print(samples); print(chr); print(start); print(ref); print(alt); print(info$GCScore); gts = sapply(gtInfo,FUN=function(x){ x$GT}); sum(gts,na.rm=T) > 100";
		FilterStringVariantContextFilter filter = new FilterStringVariantContextFilter(engine, script, header);
		boolean result = filter.keep(builder.make());
		
		assertEquals(true,result);
		
	}

}
