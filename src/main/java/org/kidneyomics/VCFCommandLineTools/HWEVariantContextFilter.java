package org.kidneyomics.VCFCommandLineTools;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.kidneyomics.stats.HWECalculator;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

public class HWEVariantContextFilter implements VariantContextFilter {

	private final double threshold;
	private final Map<String,String> popMap;
	private final boolean popMode;
	private HWECalculator calculator = HWECalculator.INSTANCE;
	private final int MIN_AN = 20;
	
	public HWEVariantContextFilter(double threshold) {
		this.threshold = threshold;
		this.popMap = null;
		popMode = false;
	}
	
	public HWEVariantContextFilter(double threshold, Map<String,String> popMap) {
		this.threshold = threshold;
		this.popMap = popMap;
		if(popMap != null) {
			popMode = true;
		} else {
			popMode = false;
		}
	}
	
	/*
	 * Allows mocking
	 */
	void setCalculator(HWECalculator calculator) {
		this.calculator = calculator;
	}
	
	@Override
	public boolean keep(VariantContext vc) {
		
		//System.err.println(vc.getContig() + ":" + vc.getStart());
		if(!vc.isBiallelic()) {
			return true;
		} else {
			
			if(popMode) {
				//calculate hwe p-value for each population
				// return true if all populations are in hwe
				// otherwise return false
				
				//get complete set of population labels
				Set<String> populations = new HashSet<String>();
				populations.addAll(popMap.values());
				
				Map<String,Iterable<Genotype>> popGenoMap = new HashMap<>();
				//initialize popGenoMap
				for(String pop : populations) {
					popGenoMap.put(pop, new LinkedList<Genotype>());
				}
				
				//add samples to corresponding population
				for(Genotype gt : vc.getGenotypes()) {
					
					String pop = popMap.get(gt.getSampleName());
					if(pop == null) {
						throw new IllegalStateException(gt.getSampleName() + " has no population");
					}
					
					Collection<Genotype> genotypes = (Collection<Genotype>) popGenoMap.get(pop);
					if(genotypes == null) {
						throw new IllegalStateException(pop + " has no genotypes list");
					}
					
					//add the genotype to the corresponding population
					genotypes.add(gt);
					
				}
				
				//compute HWE for each population
				for(Map.Entry<String,Iterable<Genotype>> entry : popGenoMap.entrySet()) {
					
					Iterable<Genotype> genotypes = entry.getValue();
					String pop = entry.getKey();
					
					//System.err.println(pop);
					double p = calculatePValue(genotypes);
					
					if(p < threshold) {
						//System.err.println(vc.getContig() + ":" + vc.getStart());
						//System.err.println(pop + " p-value: " + p);
						return false;
					} 
				}
				
				//every population in HWE
				return true;
				
				
			} else {
				//all populations				
				double p = calculatePValue(vc.getGenotypes());
				
				
				//return true if the p value is greater than the threshold
				return p >= threshold;
			}
			
		}
	}
	
	
	double calculatePValue(Iterable<Genotype> genotypes) {
		int homRef = 0;
		int homAlt = 0;
		int het = 0;
		
		//loop through each
		for(Genotype gt : genotypes) {
			if(gt.isHomRef()) {
				homRef++;
			} else if(gt.isHet()) {
				het++;
			} else if(gt.isHomVar()) {
				homAlt++;
			}
		}
		
		int total = 2 * (homRef + het + homAlt);
		if(total < MIN_AN) {
			//if we don't have enough data then just skip
			return 1.0;
		}
		
		//System.err.println("homref: " + homRef + " het: " + het + " homalt: " + homAlt);
		double p = calculator.SNPHWE(het, homRef, homAlt);
		return p;
	}
}
