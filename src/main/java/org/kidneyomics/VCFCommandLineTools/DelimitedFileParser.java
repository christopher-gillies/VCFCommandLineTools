package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import htsjdk.samtools.util.StringUtil;


public class DelimitedFileParser {
	
	/**
	 * 
	 * @param file
	 * @return A list of string maps from the file, [  { headerCol1 -> val, headerCol2 -> val } , .. ]
	 * @throws IOException
	 */
	public List<Map<String,String>> parseFile(File file) throws IOException {
		
		List<Map<String,String>> res = new LinkedList<>();
		
		List<String> lines = FileUtils.readLines(file);
		
		Iterator<String> iter = lines.iterator();
		
		String[] header = iter.next().split("\t");
		
		while(iter.hasNext()) {
			HashMap<String,String> vals = new HashMap<>();
			
			String line = iter.next();
			if(StringUtils.isEmpty(line)) {
				continue;
			}
			
			String[] data = line.split("\t");
			
			if(data.length != header.length) {
				throw new IllegalArgumentException("Error header does not match correct number of columns for line " + StringUtil.join("\t", data) + "\t" + StringUtil.join("\t", header));
			}
			
			for(int i = 0; i < header.length; i++) {
				vals.put(header[i], data[i]);
			}
			
			res.add(vals);
		}
		
		return res;
		
		
	}
}
