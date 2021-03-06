package org.kidneyomics.illumina.array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;

public class IlluminaReportLine {

	private IlluminaReportLine() {
	
	}
	
	private String sampleId;
	private String snpName;
	private IlluminaManifestMarker.STRAND illuminaStrand;
	private String topAllele1;
	private String topAllele2;
	private String snpString;
	
	private float gcScore;
	private float gtScore;
	private float r;
	private float theta;
	private float x;
	private float y;
	private float xRaw;
	private float yRaw;
	private float bAlleleFreq;
	private float logRRatio;
	private final Map<String,Object> attributes = new HashMap<>();
	
	public boolean valid(IlluminaManifestMarker marker) {
		if(this.snpString.equals(marker.getSnpString()) && this.illuminaStrand.equals(marker.getIlmnStrand())
				&& this.snpName.equals(marker.getName())) {
			return true;
		} else {
			return false;
		}
	}
	
	public static IlluminaReportLine create(Map<String,String> vals) {
		
		IlluminaReportLine instance = new IlluminaReportLine();
		
		
		instance.snpName = vals.get("SNP Name");
		instance.sampleId = vals.get("Sample ID");
		instance.topAllele1 = vals.get("Allele1 - Top");
		instance.topAllele2 = vals.get("Allele2 - Top");
		instance.gcScore = Float.parseFloat(vals.get("GC Score"));
		instance.gtScore = Float.parseFloat(vals.get("GT Score"));
		instance.r =  Float.parseFloat(vals.get("R"));
		instance.theta =  Float.parseFloat(vals.get("Theta"));
		
		instance.x =  Float.parseFloat(vals.get("X"));
		instance.y =  Float.parseFloat(vals.get("Y"));
		instance.xRaw =  Float.parseFloat(vals.get("X Raw"));
		instance.yRaw =  Float.parseFloat(vals.get("Y Raw"));
		
		instance.bAlleleFreq =  Float.parseFloat(vals.get("B Allele Freq"));
		instance.logRRatio =  Float.parseFloat(vals.get("Log R Ratio"));
		
		instance.snpString = vals.get("SNP");
		
		instance.illuminaStrand = IlluminaManifestMarker.STRAND.valueOf(vals.get("ILMN Strand"));
		
		
		instance.attributes.put("GCScore", instance.gcScore);
		instance.attributes.put("GTScore", instance.gtScore);
		instance.attributes.put("R", instance.r);
		instance.attributes.put("Theta", instance.theta);
		instance.attributes.put("X", instance.x);
		instance.attributes.put("Y", instance.y);
		instance.attributes.put("XRaw", instance.xRaw);
		instance.attributes.put("YRaw", instance.yRaw);
		instance.attributes.put("BAlleleFreq", instance.bAlleleFreq);
		instance.attributes.put("LogRRatio", instance.logRRatio);
		
		return instance;
		
	}

	
	public boolean noCall() {
		if(this.getTopAllele1().equals("-") || this.getTopAllele1().equals(".") || this.getTopAllele2().equals("-") || this.getTopAllele2().equals(".")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setSampleId(String id) {
		this.sampleId = id;
	}
	
	public String getSampleId() {
		return sampleId;
	}

	public String getTopAllele1() {
		return topAllele1;
	}

	public String getTopAllele2() {
		return topAllele2;
	}

	public float getGcScore() {
		return gcScore;
	}

	public float getGtScore() {
		return gtScore;
	}

	public float getR() {
		return r;
	}

	public float getTheta() {
		return theta;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getxRaw() {
		return xRaw;
	}

	public float getyRaw() {
		return yRaw;
	}

	public float getbAlleleFreq() {
		return bAlleleFreq;
	}

	public float getLogRRatio() {
		return logRRatio;
	}
	
	public String getSnpString() {
		return snpString;
	}
	
	public String getSnpName() {
		return snpName;
	}

	
	@Override
	public String toString() {
		return this.getSnpName() + "\t" + this.getSampleId() + "\t" + this.getTopAllele1() + "\t" + this.getTopAllele2();
	}
	
	public Genotype getGenotype(IlluminaManifestMarker marker) {
		
		if(!this.valid(marker)) {
			throw new IllegalStateException("The marker does not match this report line " + marker.getIllmId());
		}
		
		List<Allele> alleles = new ArrayList<>(2);
		

		if(this.topAllele1.equals(marker.getRefAlleleTop())) {
			alleles.add(marker.refAllele());
		} else if(this.topAllele1.equals(marker.getAltAlleleTop())) {
			alleles.add(marker.altAllele());
		} else if(this.topAllele1.equals("-") || this.topAllele1.equals(".")) {
			alleles.add(Allele.NO_CALL);
		} else {
			throw new IllegalStateException("Error: " + this.topAllele1 + " does not match " + marker.getIllmId());
		}
		
		if(this.topAllele2.equals(marker.getRefAlleleTop())) {
			alleles.add(marker.refAllele());
		} else if(this.topAllele2.equals(marker.getAltAlleleTop())) {
			alleles.add(marker.altAllele());
		} else if(this.topAllele2.equals("-") || this.topAllele2.equals(".")) {
			alleles.add(Allele.NO_CALL);
		} else {
			throw new IllegalStateException("Error: " + this.topAllele2 + " does not match " + marker.getIllmId());
		}
		
		
		Genotype gt = GenotypeBuilder.create(this.getSampleId(), alleles, attributes);

		return gt;
	}
	
}
