package org.kidneyomics.VCFCommandLineTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

public enum VariantContextLdCalculator {
	INSTANCE;
	
	public double pearsonR2(VariantContext vc1, VariantContext vc2) {
		
		// only perform computation on bialleleic sites
		// missing sites are ignored
		NumericGenotypes representation = toNumericGenotypes(vc1, vc2);
		if(representation.isValid) {

			PearsonsCorrelation pc = new PearsonsCorrelation();
			
			return Math.pow(pc.correlation(representation.gt1, representation.gt2), 2);
		} else {
			return -1;
		}

	}
	
	
	class NumericGenotypes {
		
		public NumericGenotypes() {
			
		}
		
		double[] gt1;
		double[] gt2;
		boolean isValid = true;
	}
	
	 NumericGenotypes toNumericGenotypes(VariantContext vc1, VariantContext vc2) {
		NumericGenotypes res = new NumericGenotypes();
		
		if(vc1.isBiallelic() && vc2.isBiallelic() && vc1.getGenotypes().size() == vc2.getGenotypes().size()) {
			
			
			List<Double> gts1 = new ArrayList<Double>(vc1.getGenotypes().size());
			List<Double> gts2 = new ArrayList<Double>(vc2.getGenotypes().size());
			
			Iterator<Genotype> iter1 = vc1.getGenotypes().iterator();
			Iterator<Genotype> iter2 = vc2.getGenotypes().iterator();
			
			while(iter1.hasNext()) {
				
				Genotype gt1 = iter1.next();
				Genotype gt2 = iter2.next();
				
				if(gt1.getSampleName().equals(gt2.getSampleName())) {
					
					if(!gt1.isNoCall() && !gt1.isNoCall()) {
						gts1.add(gtToDouble(gt1));
						gts2.add(gtToDouble(gt2));
					}
					
				} else {
					throw new IllegalStateException("Samples must be in the same order");
				}
				
			}
			
			int numberOfNonMissingSites = gts1.size();
			
			double gt1Array[] = new double[numberOfNonMissingSites];
			double gt2Array[] = new double[numberOfNonMissingSites];
			
			boolean all0_1 = true;
			boolean all0_2 = true;
			for(int i = 0; i < numberOfNonMissingSites; i++) {
				gt1Array[i] = gts1.get(i);
				if(gt1Array[i] != 0) {
					all0_1 = false;
				}
				gt2Array[i] = gts2.get(i);
				if(gt2Array[i] != 0) {
					all0_2 = false;
				}
			}
			
			res.gt1 = gt1Array;
			res.gt2 = gt2Array;
			res.isValid = !(all0_1 || all0_2);
		} else {
			res.isValid = false;
		}
		
		return res;
	}
	
	
	private double gtToDouble(Genotype gt) {
		if(gt.isHomRef()) {
			return 0.0; 
		} else if(gt.isHet()) {
			return 1.0;
		} else {
			return 2.0;
		}
	}
}
