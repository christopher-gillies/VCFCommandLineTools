package org.kidneyomics.stats;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.renjin.sexp.ListVector;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

public class MapToListVectorConverterTest {

	@Test
	public void test() throws ScriptException {
	    ScriptEngineManager manager = new ScriptEngineManager();
	    // create a Renjin engine:
	    ScriptEngine engine = manager.getEngineByName("Renjin");
	    // check if the engine has loaded correctly:
	    if(engine == null) {
	        throw new RuntimeException("Renjin Script Engine not found on the classpath.");
	    }
	    
	    VCFHeader header = new VCFHeader();
	    header.addMetaDataLine(new VCFInfoHeaderLine("GCScore", 1, VCFHeaderLineType.Float, ""));
	    header.addMetaDataLine(new VCFInfoHeaderLine("custom", 2, VCFHeaderLineType.Integer, ""));
	    
	    MapToListVectorConverter converter = new MapToListVectorConverter(header);
	    
	    Map<String,Object> map = new HashMap<String,Object>();
	    
	    assertTrue(new Integer[] {  2,4} instanceof Integer[]);
	    
	    map.put("GCScore", 99.1);
	    map.put("custom", new Integer[] {  2,4});
	    ListVector res = converter.convert(map);
	    
	    engine.put("res", res);
	    
	    engine.eval("print(res); print(res$custom); print(res$GCScore);");
	    
	    assertEquals(99.1,res.get("GCScore").asReal(),0.00001);
	}

}
