package org.kidneyomics.illumina.array;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.SequenceUtil;
import htsjdk.tribble.annotation.Strand;

public class IlluminaManifestMarker {

	private String illmId;
	private String name;
	private String topStrandSeqBase;
	private STRAND ilmnStrand;
	private String snpString;
	private String chr;
	
	
	private String refAllele;
	private String altAllele;
	private String refPlusSeq;
	private String altPlusSeq;
	
	private String refAlleleTop;
	private String altAlleleTop;
	
	private int pos;
	
	
	private boolean topStrandIsPlus;
	private boolean indel;
	private String snpAllele1;
	private String snpAllele2;
	
	
	static final Pattern snpPattern = Pattern.compile("^\\[([^/]+)/([^\\]]+)\\]$");
	static final Pattern seqPattern = Pattern.compile("^([ACTGNactgn]+([ACTGNactgn]))\\[([^/]+)/([^\\]]+)\\](([ACTGNactgn])[ACTGNactgn]+)$");
	
	private IlluminaManifestMarker() {
		
	}
	
	enum STRAND {
		TOP,
		BOT,
		PLUS,
		MINUS
	}
	
	
	/*
	 * Guess if top strand then the SNP column should match the top seq
	 * if bot strand then the SNP column should match the top seq
	 */
	
	
	public static IlluminaManifestMarker create(Map<String,String> vals, ReferenceFASTA reference) {
		
		
		if(vals == null) {
			throw new IllegalArgumentException("vals cannot be null");
		}
		
		IlluminaManifestMarker instance = new IlluminaManifestMarker();
		
		
		instance.illmId = vals.get("IlmnID");
		instance.name = vals.get("Name");
		instance.ilmnStrand = STRAND.valueOf(vals.get("IlmnStrand"));
		instance.snpString = vals.get("SNP");
		instance.chr = vals.get("Chr");
		instance.pos = Integer.parseInt(vals.get("MapInfo"));
		instance.topStrandSeqBase = vals.get("TopGenomicSeq").toUpperCase();
		
		//Assign alleles
		//allele 1 = numerator of [A / T] = A
		//allele 2 = denominator of [A / T] = T
		Matcher snpMatcher = snpPattern.matcher(instance.snpString);
		if(snpMatcher.matches() && snpMatcher.groupCount() == 2) {
			instance.snpAllele1 = snpMatcher.group(1);
			instance.snpAllele2 = snpMatcher.group(2);
			
			//assign seq to be blank if it equals "-"
			if(instance.snpAllele1.equals("-")) {
				instance.snpAllele1 = "";
			}
			
			if(instance.snpAllele2.equals("-")) {
				instance.snpAllele2 = "";
			}
		} else {
			throw new IllegalStateException("Matcher could not extract alleles from " + instance.snpString + " at " + instance.illmId);
		}
		
		boolean isIndel = instance.snpAllele1.equals("D") || instance.snpAllele2.equals("D");
		instance.indel = isIndel;
		
		//Assign Sequences
		Matcher seqMatcher = seqPattern.matcher(instance.topStrandSeqBase);
		String topSeqPart1 = null;
		String topAllele1 = null;
		String topAllele2 = null;
		String botAllele1 = null;
		String botAllele2 = null;
		String topSeqPart2 = null;
		String baseBeforeChange = null;
		String baseAfterChange = null;
		if(seqMatcher.matches() && seqMatcher.groupCount() == 6) {
			topSeqPart1 = seqMatcher.group(1);
			baseBeforeChange = seqMatcher.group(2);
			topAllele1 = seqMatcher.group(3);
			topAllele2 = seqMatcher.group(4);
			topSeqPart2 = seqMatcher.group(5);
			baseAfterChange = seqMatcher.group(6);
			
			//assign seq to be blank if it equals "-"
			if(topAllele1.equals("-")) {
				topAllele1 = "";
				botAllele1 = "";
			} else {
				botAllele1 = reverseComplement(topAllele1);
			}
			
			if(topAllele2.equals("-")) {
				topAllele2 = "";
				botAllele2 = "";
			} else {
				botAllele2 = reverseComplement(topAllele2);
			}
		} else {
			throw new IllegalStateException("Matcher could not extract alleles from " + instance.snpString + " at " + instance.illmId);
		}
		
		// Step (1) assign the plus or minus to top strand
		String topSeqAllele1 = topSeqPart1 + topAllele1 + topSeqPart2;
		String topSeqAllele2 = topSeqPart1 + topAllele2 + topSeqPart2;
		String bottomSeqAllele1 = reverseComplement(topSeqAllele1);
		String bottomSeqAllele2 =  reverseComplement(topSeqAllele2);
		
		String refPlus = reference.query(instance.chr, instance.pos - 1000, instance.pos + 1000);
		
		if(refPlus.contains(topSeqAllele1)) {
			
			instance.topStrandIsPlus = true;
			
			instance.refAlleleTop = topAllele1;
			instance.altAlleleTop = topAllele2;
			
			instance.refAllele = instance.refAlleleTop;
			instance.altAllele = instance.altAlleleTop;
			
			if(isIndel) {
				
				instance.refAllele = baseBeforeChange + instance.refAllele;
				instance.altAllele =  baseBeforeChange + instance.altAllele;
				
				if(topAllele1.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
				}
				
			}
			
			
			instance.refPlusSeq = topSeqAllele1;
			instance.altPlusSeq = topSeqAllele2;
			
		} else if(refPlus.contains(topSeqAllele2)) {
			
			instance.topStrandIsPlus = true;
			
			instance.refAlleleTop = topAllele2;
			instance.altAlleleTop = topAllele1;
			
			instance.refAllele = instance.refAlleleTop;
			instance.altAllele = instance.altAlleleTop;
			
			if(isIndel) {
				
				instance.refAllele = baseBeforeChange + instance.refAllele;
				instance.altAllele =  baseBeforeChange + instance.altAllele;
				
				if(topAllele2.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
				}
				
			}
			
			instance.refPlusSeq = topSeqAllele2;
			instance.altPlusSeq = topSeqAllele1;
			
		} else if(refPlus.contains(bottomSeqAllele1)) {
			String baseAfterChangeRC = reverseComplement(baseAfterChange);
			
			instance.topStrandIsPlus = false;
			
			instance.refAlleleTop = topAllele1;
			instance.altAlleleTop = topAllele2;
			
			instance.refAllele = botAllele1;
			instance.altAllele = botAllele2;
			
			if(isIndel) {
				
				instance.refAllele = baseAfterChangeRC + instance.refAllele;
				instance.altAllele =  baseAfterChangeRC + instance.altAllele;
				
				if(botAllele1.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
				}
				
			}
			
			
			instance.refPlusSeq = bottomSeqAllele1;
			instance.altPlusSeq = bottomSeqAllele2;
			
		} else if(refPlus.contains(bottomSeqAllele2)) {
			String baseAfterChangeRC = reverseComplement(baseAfterChange);
			
			instance.topStrandIsPlus = false;
			
			instance.refAlleleTop = topAllele2;
			instance.altAlleleTop = topAllele1;
			
			instance.refAllele = botAllele2;
			instance.altAllele = botAllele1;
			
			if(isIndel) {
				
				instance.refAllele = baseAfterChangeRC + instance.refAllele;
				instance.altAllele =  baseAfterChangeRC + instance.altAllele;
				
				if(botAllele2.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
				}
				
			}
			
			
			instance.refPlusSeq = bottomSeqAllele2;
			instance.altPlusSeq = bottomSeqAllele1;
		} else {
			//could be neither sequence matches the reference
			throw new IllegalStateException("Count not match reference for " + instance.illmId);
		}
		
		

		
		// 1:100316615-CAG-C  ==> C is  1:100316614, A is 1:100316615 === Deletion is alternative allele
		// 1:100336041-TAGAC-T ==> T is 1:100336040, A is 1:100336041 === Deletion is alternative allele
		return instance;
	}
	
	static String reverseComplement(String in) {
		return SequenceUtil.reverseComplement(in);
	}

	public String getIllmId() {
		return illmId;
	}

	public String getName() {
		return name;
	}

	public String getTopStrandSeqBase() {
		return topStrandSeqBase;
	}

	public STRAND getIlmnStrand() {
		return ilmnStrand;
	}

	public String getSnpString() {
		return snpString;
	}


	public String getChr() {
		return chr;
	}

	public String getRefAllele() {
		return refAllele;
	}

	public String getAltAllele() {
		return altAllele;
	}

	public String getRefPlusSeq() {
		return refPlusSeq;
	}

	public String getAltPlusSeq() {
		return altPlusSeq;
	}

	public String getRefAlleleTop() {
		return refAlleleTop;
	}

	public String getAltAlleleTop() {
		return altAlleleTop;
	}

	public int getPos() {
		return pos;
	}

	public boolean isTopStrandIsPlus() {
		return topStrandIsPlus;
	}

	public String getSnpAllele1() {
		return snpAllele1;
	}

	public String getSnpAllele2() {
		return snpAllele2;
	}

	
	public boolean isIndel() {
		return indel;
	}
	
	@Override
	public String toString() {
		return toVCFLine();
	}
	
	public String toVCFLine() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.chr);
		sb.append("\t");
		sb.append(this.pos);
		sb.append("\t");
		sb.append(this.name);
		sb.append("\t");
		
		sb.append(this.refAllele);
		sb.append("\t");
		
		sb.append(this.altAllele);
		sb.append("\t");
		
		sb.append(".");
		sb.append("\t");
		
		sb.append(".");
		sb.append("\t");
		
		sb.append("TopStrand=");
		sb.append(this.getTopStrandSeqBase());
		sb.append(";");
		
		sb.append("TopStrandIsPlus=");
		sb.append(this.topStrandIsPlus);
		sb.append(";");
		
		sb.append("RefSeqPlus=");
		sb.append(this.refPlusSeq);
		sb.append(";");
		
		sb.append("AltSeqPlus=");
		sb.append(this.altPlusSeq);
		sb.append(";");
		
		sb.append("SNP=");
		sb.append(this.snpString);
		sb.append(";");
		
		sb.append("IlluminaStrand=");
		sb.append(this.ilmnStrand);
		sb.append(";");
		
		sb.append("IlluminaId=");
		sb.append(this.illmId);
		sb.append(";");
		
		return sb.toString();
	}
}
