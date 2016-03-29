package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class SampleFile {
	private String id;
	private File file;
	
	public String getId() {
		return id;
	}
	
	public SampleFile setId(String id) {
		this.id = id;
		return this;
	}
	
	public File getFile() {
		return file;
	}
	
	public SampleFile setFile(File file) {
		this.file = file;
		return this;
	}
	
	public static List<String> getSampleIds(List<SampleFile> sfs) {
		List<String> ids = new ArrayList<String>(sfs.size());
		
		for(SampleFile sf : sfs) {
			ids.add(sf.getId());
		}
		
		return ids;
	}
	
	public static Map<String,SampleFile> getSampleFileMap(List<SampleFile> sfs) {
		HashMap<String,SampleFile> map = new HashMap<>(sfs.size() * 2);
		
		for(SampleFile sf : sfs) {
			map.put(sf.getId(), sf);
		}
		
		return map;
	}
	
	public static List<SampleFile> createFromList(File file, String delimiter) {
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<SampleFile> result = new ArrayList<SampleFile>(lines.size());
		
		for(String line : lines) {
			if(line == null || line.isEmpty()) {
				continue;
			}
			
			String[] vals = line.split(delimiter);
			if(vals.length < 2) {
				throw new IllegalStateException("Error not enough columns on this line: " + line);
			}
			
			SampleFile sf = new SampleFile();
			
			sf.id = vals[0];
			sf.file = new File(vals[1]);
			
			if(!sf.file.exists()) {
				throw new IllegalStateException(sf.file.getAbsolutePath() + " does not exist.");
			}
			
			result.add(sf);
			
			
			
		}
		
		return result;
		
	}
} 
