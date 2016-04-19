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
		
		Option siteOption = Option.builder()
		.argName("site")
		.longOpt("site")
		.desc("please specify a site")
		.numberOfArgs(100)
		.hasArg(true)
		.valueSeparator(' ')
		.required(false)
		.build();
		
		options.addOption(siteOption);
		
		Option refOption = Option.builder()
		.argName("ref")
		.longOpt("ref")
		.desc("please specify a reference sequence")
		.numberOfArgs(1)
		.hasArg(true)
		.valueSeparator(' ')
		.required(false)
		.build();
		
		options.addOption(refOption);
		
		
		Option manifestOption = Option.builder()
		.argName("manifest")
		.longOpt("manifest")
		.desc("please specify a illumina manifest file")
		.numberOfArgs(1)
		.hasArg(true)
		.valueSeparator(' ')
		.required(false)
		.build();
		
		options.addOption(manifestOption);
		
		
		Option commandOption = Option.builder()
		.argName("command")
		.longOpt("command")
		.desc("The command you would like to perform: findOverlap, selectSites, viewGenotypes, viewInfo, findOverlappingSamplesFromList, makeVcfFromManifest, makeVcfFromReports. findOverlap requires you to input at least two vcf files and the program will find the samples biallelic sites in both vcf files. selectSites will select biallelic sites from the file that you specify with format chr:pos:ref:alt for each variant. viewGenotypes will display the genotypes for sites of interest. viewInfo will display a variants information from info field. makeVcfFromManifest takes an input of a illumina manifest file and creates a vcf sites file for sites that have a reference allele. makeVcfFromReports creates a vcf from illumina standard report files. mergeVcfColumns merges two vcf files (only biallelic sites; no duplicate sample ids). filter -- remove variants from vcf, can be used to ld-prune")
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
		
		
		Option maxLdOption = Option.builder()
		.argName("maxLd")
		.longOpt("maxLd")
		.desc("The maximum allowable pairwise ld when filtering variants")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(maxLdOption);
		
		Option windowSizeKbOption = Option.builder()
		.argName("windowSizeKb")
		.longOpt("windowSizeKb")
		.desc("The window size in kilobases when filtering variants")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(windowSizeKbOption);
		
		Option outfileOp = Option.builder()
		.argName("outfile")
		.longOpt("outfile")
		.desc("The file to write out to")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(outfileOp);
		
		
		Option infileOp = Option.builder()
		.argName("infile")
		.longOpt("infile")
		.desc("The file to read in")
		.numberOfArgs(1)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(infileOp);
		
		
		Option infoOpt = Option.builder()
		.argName("info")
		.longOpt("info")
		.desc("The info to select out of vcf")
		.numberOfArgs(100)
		.hasArg(true)
		.required(false)
		.build();
		options.addOption(infoOpt);
		
		Option helpOption = Option.builder()
		.longOpt("help")
		.desc("Print the help message")
		.hasArg(false)
		.required(false)
		.build();
		
		options.addOption(helpOption);
		
		
		Option nucleotideOpt = Option.builder()
		.longOpt("nucleotide")
		.desc("Show nucleotides instead of numeric genotype")
		.hasArg(false)
		.required(false)
		.build();
		
		options.addOption(nucleotideOpt);
		
		//logger.info(StringUtils.arrayToCommaDelimitedString(args));

		
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
		
		if(cmd.hasOption("nucleotide")) {
			applicationOptions.setNucleotideRender();
		}
		
		if(cmd.hasOption("vcf")) {
			String[] vcfs = cmd.getOptionValues("vcf");
			for(String vcf : vcfs) {
				applicationOptions.addVcfFile(vcf);
			}
		}
		
		if(cmd.hasOption("info")) {
			String[] infos = cmd.getOptionValues("info");
			for(String info : infos) {
				applicationOptions.addInfo(info);
			}
		}
		
		
		if(cmd.hasOption("site")) {
			String[] sites = cmd.getOptionValues("site");
			for(String site : sites) {
				applicationOptions.addSite(site);
			}
		}
		
		if(cmd.hasOption("command")) {
			applicationOptions.setCommand(cmd.getOptionValue("command"));
		}
		
		if(cmd.hasOption("outfile")) {
			applicationOptions.setOutFile(cmd.getOptionValue("outfile"));
		}
		
		if(cmd.hasOption("ref")) {
			applicationOptions.setReferenceSeq(cmd.getOptionValue("ref"));
		}
		
		if(cmd.hasOption("manifest")) {
			applicationOptions.setManifest(cmd.getOptionValue("manifest"));
		}
		
		if(cmd.hasOption("infile")) {
			applicationOptions.setInFile(cmd.getOptionValue("infile"));
		}
		
		if(cmd.hasOption("minAc")) {
			applicationOptions.setMinAc(Integer.parseInt(cmd.getOptionValue("minAc")));
		}
		
		if(cmd.hasOption("maxLd")) {
			applicationOptions.setMaxLd(Double.parseDouble(cmd.getOptionValue("maxLd")));
		}
		
		if(cmd.hasOption("windowSizeKb")) {
			applicationOptions.setWindowSizeKb(Integer.parseInt(cmd.getOptionValue("windowSizeKb")));
		}
		
	}
	
	
	public void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "VCFCommandLineTools", options );
		//System.exit(0);
	}


}
