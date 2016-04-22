package org.kidneyomics.stats;

import static org.junit.Assert.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.renjin.sexp.AttributeMap;
import org.renjin.sexp.AttributeMap.Builder;
import org.renjin.sexp.DoubleVector;
import org.renjin.sexp.IntVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.ListVector.NamedBuilder;
import org.renjin.sexp.Logical;
import org.renjin.sexp.LogicalVector;
import org.renjin.sexp.StringVector;

public class RenjinTest {

	@Test
	public void test() throws ScriptException {
		//http://docs.renjin.org/en/latest/moving-data-between-java-and-r-code.html#tab-renjin-type-classes
	    ScriptEngineManager manager = new ScriptEngineManager();
	    // create a Renjin engine:
	    ScriptEngine engine = manager.getEngineByName("Renjin");
	    // check if the engine has loaded correctly:
	    if(engine == null) {
	        throw new RuntimeException("Renjin Script Engine not found on the classpath.");
	    }
	    
	    //engine.eval("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))");
	    //engine.eval("print(df)");
	    //engine.eval("print(lm(y ~ x, df))");
	    
	    LogicalVector res = (LogicalVector) engine.eval("5 == 5");
	    
	    assertEquals(Logical.TRUE, res.asLogical());
	    
	    
	    DoubleVector res2 = (DoubleVector) engine.eval("a <- 1:10; mean(a)");

	    assertEquals(5.5,res2.asReal(),0.0001);
	    
	
	    //build list
	    NamedBuilder lBuilder = ListVector.newNamedBuilder();
	    lBuilder.add("ID", "ABC");
	    lBuilder.add("GT", 2);
	  

	    
	    
	    engine.put("data", lBuilder.build());
	    engine.put("x", 4);
	    engine.eval("print('data');print(data);");
	    
	    
	    //build list of lists
	    
	    NamedBuilder listBuilder2 = ListVector.newNamedBuilder();
	    listBuilder2.add("ABC", lBuilder.build());
	    
	    //create second item
	    lBuilder =  ListVector.newNamedBuilder();
	    lBuilder.add("ID", "ABCD");
	    lBuilder.add("GT", 1);
	    listBuilder2.add("ABCD", lBuilder.build());
	    
	    //add to list manager
	    engine.put("data2", listBuilder2.build());
	    
	    engine.eval("print('data2 ABC');print(data2$ABC$GT);print(data2$ABC$ID);");
	    engine.eval("print('data2 ABCD');print(data2$ABCD$GT);print(data2$ABCD$ID);");
	    
	    
	    
	}

}
