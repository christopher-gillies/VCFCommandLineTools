package org.kidneyomics.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.ListVector.NamedBuilder;
import org.renjin.sexp.StringArrayVector;
import org.springframework.core.convert.converter.Converter;


import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;


public class GenotypeToListVectorConverter implements Converter<Iterable<Genotype>, ListVector> {
	
	private final VCFHeader header;
	
	public GenotypeToListVectorConverter(final VCFHeader header) {
		this.header = header;
	}
	
	public GenotypeToListVectorConverter() {
		this.header = null;
	}
	
	@Override
	public ListVector convert(Iterable<Genotype> genotypes) {
		
		NamedBuilder fullBuilder = ListVector.newNamedBuilder();
		
		//add a named element in the build for each sample
		for(Genotype gt : genotypes) {
			NamedBuilder itemBuilder = ListVector.newNamedBuilder();
			
			//add id
			itemBuilder.add("ID", gt.getSampleName());
			
			if(header == null) {
				//only add genotype
				itemBuilder.add("GT", getGT(gt));
			} else {
				
				itemBuilder.add("GT", getGT(gt));
				Collection<VCFFormatHeaderLine> formatLines = header.getFormatHeaderLines();
				for(VCFFormatHeaderLine hl : formatLines) {
					String key = hl.getID();
					VCFHeaderLineType type = hl.getType();
					
					if(key.equals("GT")) {
						continue;
					}
					
					Object val = gt.getAnyAttribute(key);
					switch(type) {
					case String:
					{
						
						if(val instanceof List<?>) {
							List<String> items = (List<String>) val;
							StringArrayVector vec = new StringArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof String[]) {
							String[] items = (String[]) val;
							StringArrayVector vec = new StringArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof String) {
							String items = (String) val;
							StringArrayVector vec = new StringArrayVector(items);
							itemBuilder.add(key, vec);
						}
							
						break;
					}
					case Float:
					{
						
						if(val instanceof List<?>) {
							List<Double> items = (List<Double>) val;
							DoubleArrayVector vec = new DoubleArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof Double[]) {
							double[] items = ArrayUtils.toPrimitive((Double[]) val);
							DoubleArrayVector vec = new DoubleArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof Double) {
							Double items = (Double) val;
							DoubleArrayVector vec = new DoubleArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof String) {
							Double items = Double.parseDouble((String) val);
							DoubleArrayVector vec = new DoubleArrayVector(items);
							itemBuilder.add(key, vec);
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
							itemBuilder.add(key, vec);
						} else if(val instanceof Integer[]) {
							int[] items = ArrayUtils.toPrimitive((Integer[]) val);
							IntArrayVector vec = new IntArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof Integer) {
							Integer items = (Integer) val;
							IntArrayVector vec = new IntArrayVector(items);
							itemBuilder.add(key, vec);
						} else if(val instanceof String) {
							Integer items = Integer.parseInt((String) val);
							IntArrayVector vec = new IntArrayVector(items);
							itemBuilder.add(key, vec);
						}
						
						break;
					}

					case Character:
					{
						
						char value = (char) gt.getAnyAttribute(key);
						itemBuilder.add(key, new String(new char[] { value }));
		
						break;
					}
						default:
							throw new IllegalStateException(type + " is not supported");
					}
					
				}
				
			}
			
			fullBuilder.add(gt.getSampleName(), itemBuilder.build());
		}
		
		
		return fullBuilder.build();
	}

	private int getGT(Genotype gt) {
		if(gt.isHomRef()) {
			return 0;
		} else if(gt.isHet()) {
			return 1;
		} else {
			return 2;
		}
	}
}
