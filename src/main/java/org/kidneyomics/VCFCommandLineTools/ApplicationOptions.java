package org.kidneyomics.VCFCommandLineTools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.rabbitmq.client.Command;

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
	
	private List<String> sites;
	
	@Autowired
	ApplicationOptions(LoggerService loggerService) throws UnsupportedEncodingException {
		this.logger = loggerService.getLogger(this);
		jarLocation =  new ApplicationHome(ApplicationOptions.class).getSource().getAbsolutePath();
		vcfs = new LinkedList<File>();
		sites = new LinkedList<String>();
	}
	
	public enum Command {
		NONE,
		HELP,
		FIND_OVERLAP,
		SELECT_SITES,
		VIEW_GENOTYPES
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
		case HELP:
			break;
		default:
			throw new IllegalStateException("Unrecognized command");
		}
		return command;
	}
	
}
