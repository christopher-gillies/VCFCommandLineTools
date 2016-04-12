package org.kidneyomics.VCFCommandLineTools;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.kidneyomics.VCFCommandLineTools.ApplicationOptions.Command;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
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
	    	
	    	RunCommand runCommand = null;
	    	switch(command) {
	    	case FIND_OVERLAP:
	    		runCommand = context.getBean(FindOverlapCommand.class);
	    		break;
	    	case SELECT_SITES:
	    		runCommand = context.getBean(SelectSitesCommand.class);
	    		break;
	    	case VIEW_GENOTYPES:
	    		runCommand = context.getBean(ViewGenotypesCommand.class);
	    		break;
	    	case FIND_OVERLAPPING_SAMPLES_FROM_LIST:
	    		runCommand = context.getBean(FindOverlappingSamplesFromListCommand.class);
	    		break;
	    	case VIEW_INFO:
	    		runCommand = context.getBean(ViewInfoCommand.class);
	    		break;
	    	case MAKE_VCF_FROM_ILLUMINA:
	    		runCommand = context.getBean(ConvertMegaManifestToVCF.class);
	    		break;
	    	case MAKE_VCF_FROM_ILLUMINA_REPORTS:
	    		runCommand = context.getBean(ConvertStandardReportToVCF.class);
	    		break;
	    	case MERGE_VCF_COLUMNS:
	    		runCommand = context.getBean(MergeVCFColumnsCommand.class);
	    		break;
	    	default:
	    			
	    	}
	    	
	    	if(runCommand != null) {
	    		runCommand.runCommand();
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
