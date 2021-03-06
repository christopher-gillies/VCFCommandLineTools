package org.kidneyomics.VCFCommandLineTools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.kidneyomics.VCFCommandLineTools.ConcordanceResult.COUNT_TYPE;
import org.kidneyomics.VCFCommandLineTools.GTRendererFactory.GT_RENDER_TYPE;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

public class ConcordanceCalculator {
	
	private final Map<VariantSiteKey,Integer> truthSampleVariants;
	private final Map<VariantSiteKey,Integer> testSampleVariants;
	
	private final String truthSample;
	private final String testSample;
	private final GTRenderer render;
	
	private ConcordanceCalculator(String truthSample, String testSample) {
		this.testSample = testSample;
		this.truthSample = truthSample;
		truthSampleVariants = new HashMap<>();
		testSampleVariants = new HashMap<>();
		render = GTRendererFactory.getGTRenderer(GT_RENDER_TYPE.NUMERIC);
	}
	
	public static ConcordanceCalculator create(String truthSample, String testSample) {
		return new ConcordanceCalculator(truthSample, testSample);
	}
	
	int getGt(Genotype gt) {
		String renderRes = render.render(gt);
		int gtNum = -1;
		switch(renderRes) {
		case ".":
			gtNum = -1;
			break;
		case "0":
			gtNum = 0;
			break;
		case "1":
			gtNum = 1;
			break;
		case "2":
			gtNum = 2;
			break;
		default:
			throw new RuntimeException(renderRes + " not supported");
		}
		return gtNum;
	}
	
	public void addTruthSampleVariant(VariantContext vc) {
		// only add biallelic variants
		if(vc.isBiallelic() && (vc.isIndel() || vc.isSNP())) {
			VariantSiteKey key = VariantSiteKey.create(vc);
			Genotype gt = vc.getGenotype(truthSample);
			int gtNum = getGt(gt);
			addTruthSampleVariant(key,gtNum);
		}
	}
	
	
	void addTruthSampleVariant(VariantSiteKey key, int gt) {
		addVariant(key, gt, truthSampleVariants);
	}
	
	public void addTestSampleVariant(VariantContext vc) {
		// only add biallelic variants
		if(vc.isBiallelic() && (vc.isIndel() || vc.isSNP())) {
			VariantSiteKey key = VariantSiteKey.create(vc);
			Genotype gt = vc.getGenotype(testSample);
			int gtNum = getGt(gt);
			addTestSampleVariant(key,gtNum);
		}
	}
	
	void addTestSampleVariant(VariantSiteKey key, int gt) {
		addVariant(key, gt, testSampleVariants);
	}
	
	/**
	 * Add a variant to a sample's variant set
	 * @param key -- the key representing this site
	 * @param gt -- the genotype 0,1,2
	 * @param the variants map
	 */
	void addVariant(VariantSiteKey key, int gt, Map<VariantSiteKey,Integer> variants) {
		if(gt == -1 || gt == 0 || gt == 1 || gt == 2) {
			//insert into the dictionary for this sample
			variants.put(key, gt);
		} else {
			throw new RuntimeException(key.toString() + " is not bialleleic");
		}
		
	}
	
	
	/**
	 * clear dictionary
	 */
	void clear() {
		this.testSampleVariants.clear();
		this.truthSampleVariants.clear();
	}
	
	/**
	 * 
	 * @return ConcordanceResult that contains the overall concordance
	 */
	public ConcordanceResult computeConcordance() {
		ConcordanceResult res = ConcordanceResult.create(truthSample, testSample);
		
		//loop through truth variants
		for(Map.Entry<VariantSiteKey,Integer> entry : truthSampleVariants.entrySet()) {	
			int truthGt = entry.getValue();
			if(testSampleVariants.containsKey(entry.getKey())) {
				int testGt = testSampleVariants.get(entry.getKey());
				COUNT_TYPE type = res.update(truthGt, testGt);
				res.log(entry.getKey().toString(),truthGt, entry.getKey().toString(),testGt, type);
			} else {
				//missing in test set
				int testGt = -1;
				COUNT_TYPE type = res.update(truthGt, testGt);
				res.log(entry.getKey().toString(),truthGt, StringUtils.EMPTY,testGt, type);
			}
		}
		
		//we now have counted the intersection of test and truth variants
		//we now need to count the variants in the test but not the truth
		for(Map.Entry<VariantSiteKey,Integer> entry : testSampleVariants.entrySet()) {	
			if(!truthSampleVariants.containsKey(entry.getKey())) {
				int testGt = entry.getValue();
				//it is not in the truth variants so the truthGt is missing!
				int truthGt = -1;
				COUNT_TYPE type = res.update(truthGt, testGt);
				res.log(StringUtils.EMPTY, truthGt, entry.getKey().toString(), testGt, type);
			}
		}
		
		
		return res;
	}
	 
}
