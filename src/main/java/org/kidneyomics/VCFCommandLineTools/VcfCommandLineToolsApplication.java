package org.kidneyomics.VCFCommandLineTools;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.kidneyomics.VCFCommandLineTools.ApplicationOptions.Command;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class VcfCommandLineToolsApplication {

    public static void main(String[] args) throws ScriptException {
    	SpringApplication springApplication = new SpringApplication(new Object[] { VcfCommandLineToolsApplication.class });
    	springApplication.setLogStartupInfo(false);
    	ApplicationContext context = springApplication.run(args);
    	
    	ApplicationOptions options = context.getBean(ApplicationOptions.class);
    	
    	
    	LoggerService loggerService = context.getBean(LoggerService.class);
    	Logger logger = loggerService.getLogger(VcfCommandLineToolsApplication.class);
    	
    	try {
	    	Command command = options.validate();
	    	
	    	switch(command) {
	    	case FIND_OVERLAP:
	    		RunCommand foc = context.getBean(FindOverlapCommand.class);
	    		foc.runCommand();
	    		break;
	    	case SELECT_SITES:
	    		RunCommand ssc = context.getBean(SelectSitesCommand.class);
	    		ssc.runCommand();
	    		break;
	    	case VIEW_GENOTYPES:
	    		RunCommand vgc = context.getBean(ViewGenotypesCommand.class);
	    		vgc.runCommand();
	    		break;
	    	default:
	    			
	    	}
    	} catch(Exception e) {
    		logger.error(e.getMessage());
    		throw e;
    	}
    	
        /*
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        engine.eval("var x = 'hello';");
        Object x = engine.get("x");
        
    	Logger logger = LoggerFactory.getLogger(VcfCommandLineToolsApplication.class);
    	
    	logger.info(x.toString());
        
        */
        
    }
}
