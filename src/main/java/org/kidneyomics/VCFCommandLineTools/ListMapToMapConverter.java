package org.kidneyomics.VCFCommandLineTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;


public class ListMapToMapConverter implements Converter<List<Map<String,String>>, Map<String,String>> {

	/**
	 * 
	 * @param keyCol -- is the column used in the map key that is selected out of each element
	 * @param otherCol --- is the column used in the map value that is selected out of each element
	 */
	public ListMapToMapConverter(String keyCol, String valCol) {
		this.keyCol = keyCol;
		this.valCol = valCol;
	}
	
	//public static ListMapToMapConverter create(String keyCol, String valCol) {
	//	return new ListMapToMapConverter(keyCol, valCol);
	//}
	
	private String keyCol;
	private String valCol;
	
	@Override
	public Map<String, String> convert(List<Map<String, String>> input) {
		
		HashMap<String,String> map = new HashMap<String,String>();
		int lineNum = 0;
		for(Map<String,String> sample : input) {
			lineNum++;
			String id = sample.get(keyCol);
			if(id == null) {
				throw new IllegalArgumentException("No key column found for line" + lineNum);
			}
			String pop = sample.get(valCol);
			if(pop == null) {
				throw new IllegalArgumentException("No value column found for line" + lineNum);
			}
			
			map.put(id, pop);
		}
		return map;
	}

}
