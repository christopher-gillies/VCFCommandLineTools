package org.kidneyomics.VCFCommandLineTools;

import java.util.List;
import java.util.LinkedList;

import org.kidneyomics.VCFCommandLineTools.GTRendererFactory.GT_RENDER_TYPE;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.File;

@Component
public class ApplicationOptions {

	private String jarLocation;
	Logger logger;
	private Command command = Command.NONE;
	
	private List<File> vcfs; 
	
	private int minAc = 1;
	
	private String outFile;
	
	private String inFile;
	
	private String referenceSeq;
	private String manifest;
	
	private List<String> sites;
	
	private List<String> infos;
	
	private GT_RENDER_TYPE gtRendererType = GT_RENDER_TYPE.NUMERIC;
	
	private double maxLd = 1.0;
	
	private int windowSizeKb = 1000;
	
	@Autowired
	ApplicationOptions(LoggerService loggerService) {
		this.logger = loggerService.getLogger(this);
		jarLocation =  new ApplicationHome(ApplicationOptions.class).getSource().getAbsolutePath();
		vcfs = new LinkedList<File>();
		sites = new LinkedList<String>();
		infos = new LinkedList<>();
	}
	
	public enum Command {
		NONE,
		HELP,
		FIND_OVERLAP,
		SELECT_SITES,
		VIEW_GENOTYPES,
		VIEW_INFO,
		FIND_OVERLAPPING_SAMPLES_FROM_LIST,
		MAKE_VCF_FROM_ILLUMINA,
		MAKE_VCF_FROM_ILLUMINA_REPORTS,
		MERGE_VCF_COLUMNS,
		FILTER
	}

	
	
	public String getReferenceSeq() {
		return referenceSeq;
	}

	public void setReferenceSeq(String referenceSeq) {
		this.referenceSeq = referenceSeq;
	}

	public String getManifest() {
		return manifest;
	}

	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

	public String getJarLocation() {
		return jarLocation;
	}
	
	public void addVcfFile(String file) {
		File f = new File(file);
		if(!f.exists()) {
			throw new IllegalArgumentException(file + " does not exist!");
		} else {
			vcfs.add(f);
		}
	}
	
	public List<File> getVcfs() {
		return this.vcfs;
	}
	
	public Command getCommand() {
		return this.command;
	}
	
	public GT_RENDER_TYPE getGtRendererType() {
		return this.gtRendererType;
	}
	
	public void setNucleotideRender() {
		this.gtRendererType = GT_RENDER_TYPE.NUCLEOTIDE;
	}
	
	public void setCommand(String commandString) {
		
		switch(commandString) {
		case "findOverlap":
			command = Command.FIND_OVERLAP;
			break;
		case "selectSites":
			command = Command.SELECT_SITES;
			break;
		case "help":
			command = Command.HELP;
			break;
		case "viewGenotypes":
			command = Command.VIEW_GENOTYPES;
			break;
		case "findOverlappingSamplesFromList":
			command = Command.FIND_OVERLAPPING_SAMPLES_FROM_LIST;
			break;
		case "viewInfo":
			command = Command.VIEW_INFO;
			break;
		case "makeVcfFromManifest":
			command = Command.MAKE_VCF_FROM_ILLUMINA;
			break;
		case "makeVcfFromReports":
			command = Command.MAKE_VCF_FROM_ILLUMINA_REPORTS;
			break;
		case "mergeVcfColumns":
			command = Command.MERGE_VCF_COLUMNS;
			break;
		case "filter":
			command = Command.FILTER;
			break;
		default: 
			command = Command.NONE;
			break;
		 
		}
		
	}
	
	public List<String> getSites() {
		return this.sites;
	}
	

	public void addSite(String site) {
		this.sites.add(site);
	}
	
	public void addInfo(String info) {
		this.infos.add(info);
	}
	
	public List<String> getInfos() {
		return this.infos;
	}
	
	public void clearSites() {
		this.sites.clear();
	}
	
	public String getInFile() {
		return inFile;
	}

	public void setInFile(String inFile) {
		this.inFile = inFile;
	}

	public String getOutFile() {
		return outFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public int getMinAc() {
		return minAc;
	}

	public void setMinAc(int minAc) {
		this.minAc = minAc;
	}

	
	public double getMaxLd() {
		return maxLd;
	}

	public void setMaxLd(double maxLd) {
		this.maxLd = maxLd;
	}

	public int getWindowSizeKb() {
		return windowSizeKb;
	}

	public void setWindowSizeKb(int windowSizeKb) {
		this.windowSizeKb = windowSizeKb;
	}

	public Command validate() {
		switch(command) {
		case FIND_OVERLAP:
			if(vcfs.size() < 2) {
				throw new IllegalStateException("Please specify at least two vcfs");
			}
			
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			break;
		case SELECT_SITES:
			if(vcfs.size() != 1) {
				throw new IllegalStateException("Please specify one vcf");
			}
			
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			if(StringUtils.isEmpty(this.getInFile())) {
				throw new IllegalStateException("Please specify an input file");
			}
			
			break;
		case VIEW_GENOTYPES:
			if(vcfs.size() != 1) {
				throw new IllegalStateException("Please specify one vcf");
			}
			
			if(sites.size() < 1) {
				throw new IllegalStateException("Please specify at least one sites");
			}
			
			break;
		case FIND_OVERLAPPING_SAMPLES_FROM_LIST:
			if(vcfs.size() != 1) {
				throw new IllegalStateException("Please specify one vcf");
			}
			
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			if(StringUtils.isEmpty(this.getInFile())) {
				throw new IllegalStateException("Please specify an input file");
			}
			
			
			break;
		case VIEW_INFO:
			if(vcfs.size() != 1) {
				throw new IllegalArgumentException("Please specify one VCF");
			}
			
			if(StringUtils.isEmpty(this.getInFile())) {
				throw new IllegalStateException("Please specify an input file");
			}
			
			if(infos.size() == 0) {
				throw new IllegalArgumentException("Please specify info to select");
			}
			break;
		case MAKE_VCF_FROM_ILLUMINA:
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			if(StringUtils.isEmpty(this.getManifest())) {
				throw new IllegalStateException("Please specify a manifest file");
			}
			
			if(StringUtils.isEmpty(this.getReferenceSeq())) {
				throw new IllegalStateException("Please specify a reference sequence file");
			}
			
			break;
		case MAKE_VCF_FROM_ILLUMINA_REPORTS:
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			if(StringUtils.isEmpty(this.getInFile())) {
				throw new IllegalStateException("Please specify an input file");
			}
			
			
			if(StringUtils.isEmpty(this.getManifest())) {
				throw new IllegalStateException("Please specify a manifest file");
			}
			
			if(StringUtils.isEmpty(this.getReferenceSeq())) {
				throw new IllegalStateException("Please specify a reference sequence file");
			}
			
			break;
			
		case MERGE_VCF_COLUMNS:
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			
			if(vcfs.size() != 2) {
				throw new IllegalStateException("Please specify two vcf files");
			}
			
			
			break;
		case FILTER:
			if(vcfs.size() != 1) {
				throw new IllegalArgumentException("Please specify one VCF");
			}
			
			if(StringUtils.isEmpty(this.getOutFile())) {
				throw new IllegalStateException("Please specify an output file");
			}
			
			//if(maxLd <= 0) {
			//	throw new IllegalArgumentException("maxLd must be greater than 0");
			//}
			
			if(windowSizeKb <= 0) {
				throw new IllegalArgumentException("windowSizeKb must be greater than 0");
			}
			
			break;
		case HELP:
			break;
		default:
			throw new IllegalStateException("Unrecognized command");
		}
		return command;
	}
	
}
