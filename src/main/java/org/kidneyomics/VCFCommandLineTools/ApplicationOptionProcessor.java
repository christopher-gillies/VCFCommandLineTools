package org.kidneyomics.VCFCommandLineTools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApplicationOptionProcessor implements OptionProcessor {

	
	ApplicationOptions applicationOptions;
	
	
	Logger logger;
	
	
	@Autowired
	ApplicationOptionProcessor(ApplicationArguments args, LoggerService loggerService, ApplicationOptions applicationOptions) throws ParseException {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
		if(args == null) {
			throw new NullPointerException("ApplicationArguments args is null");
		}
		try {
			processInputs(args.getSourceArgs());
		} catch(Exception e) {
			logger.info(e.getMessage());
			System.exit(0);
		}
		
	}
	
	@Override
	public void processInputs(String[] args) throws ParseException {
		Options options = new Options();

		Option vcfOption = Option.builder()
		.argName("vcf")
		.longOpt("vcf")
		.desc("a vcf file")
		.numberOfArgs(2)
		.hasArg(true)
		.valueSeparator(' ')
		.required(false)
		.build();
		
		options.addOption(vcfOption);
		
		Option commandOption = Option.builder()
		.argName("command")
		.longOpt("command")
		.desc("The command you would like to perform: findOverlap")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		
		options.addOption(commandOption);
		
		
		Option minAcOption = Option.builder()
		.argName("minAc")
		.longOpt("minAc")
		.desc("The minimum allele count for a variant to be considered")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(minAcOption);
		
		Option outfileOp = Option.builder()
		.argName("outfile")
		.longOpt("outfile")
		.desc("The file to write out to")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(outfileOp);
		
		Option helpOption = Option.builder()
		.longOpt("help")
		.desc("Print the help message")
		.hasArg(false)
		.required(false)
		.build();
		
		options.addOption(helpOption);
		
		logger.info(StringUtils.arrayToCommaDelimitedString(args));

		
		CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = parser.parse( options, args);
		
		if(cmd.getOptions().length == 0) {
			printHelp(options);
			applicationOptions.setCommand("help");
			return;
		}
		
		
		if(cmd.hasOption("help")) {
			printHelp(options);
			applicationOptions.setCommand("help");
			return;
		}
		
		if(cmd.hasOption("vcf")) {
			String[] vcfs = cmd.getOptionValues("vcf");
			for(String vcf : vcfs) {
				applicationOptions.addVcfFile(vcf);
			}
		}
		
		if(cmd.hasOption("command")) {
			applicationOptions.setCommand(cmd.getOptionValue("command"));
		}
		
		if(cmd.hasOption("outfile")) {
			applicationOptions.setOutFile(cmd.getOptionValue("outfile"));
		}
		
		if(cmd.hasOption("minAc")) {
			applicationOptions.setMinAc(Integer.parseInt(cmd.getOptionValue("minAc")));
		}
		
	}
	
	
	public void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "VCFCommandLineTools", options );
		//System.exit(0);
	}


}
