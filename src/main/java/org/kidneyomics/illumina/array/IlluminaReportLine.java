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

	private IlluminaReportLine(IlluminaManifestMarker marker) {
		this.marker = marker;
	}
	
	private String id;
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
	private final IlluminaManifestMarker marker;
	private final Map<String,Object> attributes = new HashMap<>();
	
	private boolean valid() {
		if(this.snpString.equals(marker.getSnpString()) && this.illuminaStrand.equals(marker.getIlmnStrand())) {
			return true;
		} else {
			return false;
		}
	}
	
	public static IlluminaReportLine create(IlluminaManifestMarker marker, Map<String,String> vals) {
		
		IlluminaReportLine instance = new IlluminaReportLine(marker);
		
		
		instance.id = vals.get("Sample ID");
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
		
		if(!instance.valid()) {
			throw new IllegalStateException("SNP String or Strand do not match for " + marker.getIllmId());
		}
		
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

	public String getId() {
		return id;
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
	
	public Genotype getGenotype(VariantContext vc) {
		
		List<Allele> alleles = new ArrayList<>(2);
		

		if(this.topAllele1.equals(marker.getRefAlleleTop())) {
			alleles.add(vc.getAllele(marker.getRefAllele()));
		} else if(this.topAllele1.equals(marker.getAltAlleleTop())) {
			alleles.add(vc.getAllele(marker.getAltAllele()));
		} else if(this.topAllele1.equals("-") || this.topAllele1.equals(".")) {
			alleles.add(Allele.NO_CALL);
		} else {
			throw new IllegalStateException("Error: " + this.topAllele1 + " does not match " + marker.getIllmId());
		}
		
		if(this.topAllele2.equals(marker.getRefAlleleTop())) {
			alleles.add(vc.getAllele(marker.getRefAllele()));
		} else if(this.topAllele2.equals(marker.getAltAlleleTop())) {
			alleles.add(vc.getAllele(marker.getAltAllele()));
		} else if(this.topAllele2.equals("-") || this.topAllele2.equals(".")) {
			alleles.add(Allele.NO_CALL);
		} else {
			throw new IllegalStateException("Error: " + this.topAllele2 + " does not match " + marker.getIllmId());
		}
		
		
		Genotype gt = GenotypeBuilder.create(this.getId(), alleles, attributes);

		return gt;
	}
	
}
