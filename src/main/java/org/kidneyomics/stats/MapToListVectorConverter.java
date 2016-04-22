package org.kidneyomics.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.StringArrayVector;
import org.renjin.sexp.ListVector.NamedBuilder;
import org.springframework.core.convert.converter.Converter;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;


public class MapToListVectorConverter implements Converter<Map<String,Object>, ListVector>  {
	
	private final VCFHeader header;
	
	public MapToListVectorConverter(final VCFHeader header) {
		this.header = header;
	}

	@Override
	public ListVector convert(Map<String, Object> input) {
		
		NamedBuilder builder = ListVector.newNamedBuilder();
		if(header != null) {
			for(VCFInfoHeaderLine hl : header.getInfoHeaderLines()) {
				String key = hl.getID();
				VCFHeaderLineType type = hl.getType();
				
				if(!input.containsKey(key)) {
					continue;
				}
				
				Object val = input.get(key);
				switch(type) {
				case String:
				{
					
					if(val instanceof List<?>) {
						List<String> items = (List<String>) val;
						StringArrayVector vec = new StringArrayVector(items);
						builder.add(key, vec);
					} else if(val instanceof String[]) {
						String[] items = (String[]) val;
						StringArrayVector vec = new StringArrayVector(items);
						builder.add(key, vec);
					} else if(val instanceof String) {
						String items = (String) val;
						StringArrayVector vec = new StringArrayVector(items);
						builder.add(key, vec);
					}
						
					break;
				}
				case Float:
				{
					
					if(val instanceof List<?>) {
						List<Double> items = (List<Double>) val;
						DoubleArrayVector vec = new DoubleArrayVector(items);
						builder.add(key, vec);
					} else if(val instanceof Double[]) {
						double[] items = ArrayUtils.toPrimitive((Double[]) val);
						DoubleArrayVector vec = new DoubleArrayVector(items);
						builder.add(key, vec);
					} else if(val instanceof Double) {
						Double items = (Double) val;
						DoubleArrayVector vec = new DoubleArrayVector(items);
						builder.add(key, vec);
					}
					
					break;
				}
				case Integer:
				{

					if(val instanceof List<?>) {
						List<Integer> items = (List<Integer>) val;
						int[] arr = new int[items.size()];
						for(int i = 0; i < items.size(); i++) {
							arr[i] = items.get(i);
						}
						IntArrayVector vec = new IntArrayVector(arr);
						builder.add(key, vec);
					} else if(val instanceof Integer[]) {
						int[] items = ArrayUtils.toPrimitive((Integer[]) val);
						IntArrayVector vec = new IntArrayVector(items);
						builder.add(key, vec);
					} else if(val instanceof Integer) {
						Integer items = (Integer) val;
						IntArrayVector vec = new IntArrayVector(items);
						builder.add(key, vec);
					}
					
					break;
				}
				case Character:
				{
					
					char value = (char) input.get(key);
					builder.add(key, new String(new char[] { value }));
	
					break;
				}
				case Flag:
				{
					boolean value = input.containsKey(key);
					builder.add(key, value);
					break;
				}
					default:
						throw new IllegalStateException(type + " is not supported");
				}
			}
		} 
		return builder.build();
	}
	
	
}
