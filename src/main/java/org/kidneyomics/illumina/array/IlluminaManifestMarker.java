package org.kidneyomics.illumina.array;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kidneyomics.referenceseq.ReferenceFASTA;

import htsjdk.samtools.util.SequenceUtil;
import htsjdk.tribble.annotation.Strand;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

public class IlluminaManifestMarker {

	private String illmId;
	private String name;
	private String topStrandSeqBase;
	private STRAND ilmnStrand;
	private String snpString;
	private String chr;
	
	
	private String refAlleleString;
	private String altAlleleString;
	private String alt2AlleleString;
	
	private Allele refAllele;
	private Allele altAllele;
	private Allele alt2Allele;
	
	private String refPlusSeq;
	private String altPlusSeq;
	private String alt2PlusSeq;
	
	private String refAlleleTop;
	private String altAlleleTop;
	private String alt2AlleleTop;
	
	private float gtScore = -1.0f;
	
	private int pos;
	
	private boolean surroundingSequenceMatches = true;
	private boolean missingPos = false;
	private boolean topStrandIsPlus;
	private boolean indel;
	private boolean hasRefAllele = true;
	private boolean matchesReference = true; 
	private String snpAllele1;
	private String snpAllele2;
	
	
	static final Pattern snpPattern = Pattern.compile("^\\[([^/]+)/([^\\]]+)\\]$");
	static final Pattern seqPattern = Pattern.compile("^([ACTGNURYSWKMBDHVactgnuryswkmbdhv]+([ACTGNURYSWKMBDHVactgnuryswkmbdhv]))\\[([^/]+)/([^\\]]+)\\](([ACTGNURYSWKMBDHVactgnuryswkmbdhv])[ACTGNURYSWKMBDHVactgnuryswkmbdhv]+)$");
	static final Pattern replacePattern = Pattern.compile("[^\\[\\]\\/ACTGNactgn-]");
	
	private static final VCFEncoder encoder = encoder();
	
	public static VCFHeader header() {
		return header(null);
	}
	
	public static VCFHeader header(List<String> sampleIds) {
		
		
		
		VCFHeader header = null;
		
		if(sampleIds != null) {
			Set<VCFHeaderLine> meta = new HashSet<VCFHeaderLine>();
			header = new VCFHeader(meta , sampleIds);	
		} else {
			header = new VCFHeader();
		}
		
		VCFInfoHeaderLine topStrand = new VCFInfoHeaderLine("TopStrand", 1, VCFHeaderLineType.String, "The top strand sequence from illumina manifest file");
		header.addMetaDataLine(topStrand);
		
		
		VCFInfoHeaderLine topStrandPlus = new VCFInfoHeaderLine("TopStrandIsPlus", 1, VCFHeaderLineType.Flag, "This flag is set depending on whether or not the top strand is on the plus or minus strand");
		header.addMetaDataLine(topStrandPlus);
		
		VCFInfoHeaderLine refPlus = new VCFInfoHeaderLine("RefSeqPlus", 1, VCFHeaderLineType.String, "The reference or inferred reference probe sequence in terms of the plus strand");
		header.addMetaDataLine(refPlus);
		
		VCFInfoHeaderLine refTop = new VCFInfoHeaderLine("RefAlleleTop", 1, VCFHeaderLineType.String, "The top strand reference allele");
		header.addMetaDataLine(refTop);
		
		VCFInfoHeaderLine alt1Plus = new VCFInfoHeaderLine("Alt1SeqPlus", 1, VCFHeaderLineType.String, "The alternative allele in terms of the plus strand");
		header.addMetaDataLine(alt1Plus);
		
		VCFInfoHeaderLine alt1Top = new VCFInfoHeaderLine("Alt1AlleleTop", 1, VCFHeaderLineType.String, "The top strand alternative allele 1");
		header.addMetaDataLine(alt1Top);
		
		VCFInfoHeaderLine alt2Plus = new VCFInfoHeaderLine("Alt2SeqPlus", 1, VCFHeaderLineType.String, "The second alternative allele in terms of the plus strand");
		header.addMetaDataLine(alt2Plus);
		
		VCFInfoHeaderLine alt2Top = new VCFInfoHeaderLine("Alt2AlleleTop", 1, VCFHeaderLineType.String, "The top strand alternative allele 2");
		header.addMetaDataLine(alt2Top);
		
		VCFInfoHeaderLine snpInfo = new VCFInfoHeaderLine("SNP", 1, VCFHeaderLineType.String, "The nucleotide change");
		header.addMetaDataLine(snpInfo);
		
		VCFInfoHeaderLine illuminaStrand = new VCFInfoHeaderLine("IlluminaStrand", 1, VCFHeaderLineType.String, "TOP/BOT/PLUS/MINUS strand information for SNP field");
		header.addMetaDataLine(illuminaStrand);
		
		VCFInfoHeaderLine illuminaId = new VCFInfoHeaderLine("IlluminaId", 1, VCFHeaderLineType.String, "The marker id for this marker");
		header.addMetaDataLine(illuminaId);
		
		VCFInfoHeaderLine noReference = new VCFInfoHeaderLine("NoReferenceProbe", 1, VCFHeaderLineType.Flag, "The flag specifies that no allele matches the reference");
		header.addMetaDataLine(noReference);
		
		header.addMetaDataLine(new VCFInfoHeaderLine("GenTrainScore", 1, VCFHeaderLineType.Float, "This is the GTScore from the standard report files. This value is a score between 0 and 1 that indicates how well this snp clustered. Scores closer to one indicate a higher quality cluster"));
		
		header.addMetaDataLine( new VCFFormatHeaderLine("GT",1,VCFHeaderLineType.String,"GT"));
		header.addMetaDataLine( new VCFFormatHeaderLine("GCScore",1,VCFHeaderLineType.Float,"GC Score GenCall Score"));
		header.addMetaDataLine( new VCFFormatHeaderLine("GTScore",1,VCFHeaderLineType.Float,"GT Score GenTrain Score"));
		header.addMetaDataLine( new VCFFormatHeaderLine("R",1,VCFHeaderLineType.Float,"R"));
		header.addMetaDataLine( new VCFFormatHeaderLine("Theta",1,VCFHeaderLineType.Float,"Theta"));
		
		header.addMetaDataLine( new VCFFormatHeaderLine("X",1,VCFHeaderLineType.Float,"X"));
		header.addMetaDataLine( new VCFFormatHeaderLine("Y",1,VCFHeaderLineType.Float,"X"));
		header.addMetaDataLine( new VCFFormatHeaderLine("XRaw",1,VCFHeaderLineType.Float,"X Raw"));
		header.addMetaDataLine( new VCFFormatHeaderLine("YRaw",1,VCFHeaderLineType.Float,"Y Raw"));
		
		header.addMetaDataLine( new VCFFormatHeaderLine("BAlleleFreq",1,VCFHeaderLineType.Float,"B allele frequency"));
		header.addMetaDataLine( new VCFFormatHeaderLine("LogRRatio",1,VCFHeaderLineType.Float,"Log R Ratio"));
		return header;
	}
	
	
	public static VCFEncoder encoder() {
		VCFEncoder encoder = new VCFEncoder(header(), false, false);
		return encoder;
	}
	
	
	public static VCFEncoder encoder(List<String> sampleIds) {
		VCFEncoder encoder = new VCFEncoder(header(sampleIds), false, false);
		return encoder;
	}
	
	public static VCFEncoder encoder(VCFHeader header) {
		VCFEncoder encoder = new VCFEncoder(header, false, false);
		return encoder;
	}
	
	public boolean hasError() {
		return missingPos || !hasRefAllele || !matchesReference;
	}
	
	public boolean missingPos() {
		return missingPos;
	}
	
	public boolean surroundingSequenceMatches() {
		return surroundingSequenceMatches;
	}
	
	private IlluminaManifestMarker() {
		
	}
	
	public enum STRAND {
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
		
		//clean up top-strand to remove non-ACTGN bases
		//we do not use this anymore as I implemented the IUPAC code http://www.bioinformatics.org/sms/iupac.html
		//instance.topStrandSeqBase = instance.topStrandSeqBase.replaceAll(replacePattern.pattern(), "");
		
		if(instance.pos == 0 || instance.chr.equals("0")) {
			instance.missingPos = true;
			return instance;
		}
		
		
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
		
		//convert to uppercase
		topSeqPart1 = topSeqPart1.toUpperCase();
		topSeqPart2 = topSeqPart2.toUpperCase();
		topAllele1 = topAllele1.toUpperCase();
		topAllele2 = topAllele2.toUpperCase();
		
		// Step (1) assign the plus or minus to top strand
		String topSeqAllele1 = topSeqPart1 + topAllele1 + topSeqPart2;
		String topSeqAllele2 = topSeqPart1 + topAllele2 + topSeqPart2;
		String bottomSeqAllele1 = reverseComplement(topSeqAllele1);
		String bottomSeqAllele2 =  reverseComplement(topSeqAllele2);
		
		//convert to upper case
		String refPlus = reference.query(instance.chr, instance.pos - 1000, instance.pos + 1000).toUpperCase();
		
		//we need to trim trailing N from sequence
		refPlus = replaceTrailingN(refPlus);
		
		if( ReferenceFASTA.containsUseIUPAC(refPlus, topSeqAllele1)) {
			
			instance.topStrandIsPlus = true;
			
			instance.refAlleleTop = topAllele1;
			instance.altAlleleTop = topAllele2;
			
			instance.refAlleleString = instance.refAlleleTop;
			instance.altAlleleString = instance.altAlleleTop;
			
			if(isIndel) {
				
				instance.refAlleleString = baseBeforeChange + instance.refAlleleString;
				instance.altAlleleString =  baseBeforeChange + instance.altAlleleString;
				
				if(topAllele1.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
					//adjust position
					instance.pos = instance.pos - 1;
				}
				
			}
			
			
			instance.refPlusSeq = topSeqAllele1;
			instance.altPlusSeq = topSeqAllele2;
			
		} else if(ReferenceFASTA.containsUseIUPAC(refPlus, topSeqAllele2)) {
			
			instance.topStrandIsPlus = true;
			
			instance.refAlleleTop = topAllele2;
			instance.altAlleleTop = topAllele1;
			
			instance.refAlleleString = instance.refAlleleTop;
			instance.altAlleleString = instance.altAlleleTop;
			
			if(isIndel) {
				
				instance.refAlleleString = baseBeforeChange + instance.refAlleleString;
				instance.altAlleleString =  baseBeforeChange + instance.altAlleleString;
				
				if(topAllele2.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
					//adjust position
					instance.pos = instance.pos - 1;
				}
				

			}
			
			instance.refPlusSeq = topSeqAllele2;
			instance.altPlusSeq = topSeqAllele1;
			
		} else if(ReferenceFASTA.containsUseIUPAC(refPlus, bottomSeqAllele1)) {
			String baseAfterChangeRC = reverseComplement(baseAfterChange);
			
			instance.topStrandIsPlus = false;
			
			instance.refAlleleTop = topAllele1;
			instance.altAlleleTop = topAllele2;
			
			instance.refAlleleString = botAllele1;
			instance.altAlleleString = botAllele2;
			
			if(isIndel) {
				
				instance.refAlleleString = baseAfterChangeRC + instance.refAlleleString;
				instance.altAlleleString =  baseAfterChangeRC + instance.altAlleleString;
				
				if(botAllele1.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
					//adjust position
					instance.pos = instance.pos - 1;
				}
				
			}
			
			
			instance.refPlusSeq = bottomSeqAllele1;
			instance.altPlusSeq = bottomSeqAllele2;
			
		} else if(ReferenceFASTA.containsUseIUPAC(refPlus, bottomSeqAllele2) ) {
			String baseAfterChangeRC = reverseComplement(baseAfterChange);
			
			instance.topStrandIsPlus = false;
			
			instance.refAlleleTop = topAllele2;
			instance.altAlleleTop = topAllele1;
			
			instance.refAlleleString = botAllele2;
			instance.altAlleleString = botAllele1;
			
			if(isIndel) {
				
				instance.refAlleleString = baseAfterChangeRC + instance.refAlleleString;
				instance.altAlleleString =  baseAfterChangeRC + instance.altAlleleString;
				
				if(botAllele2.equals("")) {
					instance.refAlleleTop = "D";
					instance.altAlleleTop = "I";
				} else {
					instance.refAlleleTop = "I";
					instance.altAlleleTop = "D";
					
					//adjust position
					instance.pos = instance.pos - 1;
				}
				
				
			}
			
			
			instance.refPlusSeq = bottomSeqAllele2;
			instance.altPlusSeq = bottomSeqAllele1;
		} else {
			instance.hasRefAllele = false;
			
			String botSeqPart2 = reverseComplement(topSeqPart1);
			String botSeqPart1 = reverseComplement(topSeqPart2);
			
			
			if(ReferenceFASTA.containsUseIUPAC(refPlus, topSeqPart1) && ReferenceFASTA.containsUseIUPAC(refPlus, topSeqPart2) ) {
				
			} else if(ReferenceFASTA.containsUseIUPAC(refPlus, botSeqPart2) && ReferenceFASTA.containsUseIUPAC(refPlus, botSeqPart1)) {
				
			} else {
				instance.surroundingSequenceMatches = false;
				//System.err.println("Skipping..." + instance.name);
				//System.err.println(topSeqAllele1);
				//System.err.println(topSeqAllele2);
				//System.err.println(bottomSeqAllele1);
				//System.err.println(bottomSeqAllele2);
			}
			
//			//could be neither sequence matches the reference
//			//throw new IllegalStateException("Count not match reference for " + instance.illmId);
//			
//			instance.hasRefAllele = false;
//			String botSeqPart2 = reverseComplement(topSeqPart1);
//			String botSeqPart1 = reverseComplement(topSeqPart2);
//			instance.refAllele = reference.query(instance.chr, instance.pos, instance.pos);
//			
//			
//			//check if topseq matches the reference
//			
//			
//			
//			if(refPlus.contains(topSeqPart1) && refPlus.contains(topSeqPart2)) {
//				instance.topStrandIsPlus = true;
//				
//				instance.refAlleleTop = instance.refAllele;
//				instance.altAlleleTop = topAllele1;
//				instance.alt2AlleleTop = topAllele2;
//				
//				//instance.refAllele = instance.refAlleleTop;
//				instance.altAllele = instance.altAlleleTop;
//				instance.alt2Allele = instance.alt2AlleleTop;
//				
//				instance.refPlusSeq = reference.query(instance.chr, instance.pos - topSeqPart1.length(), instance.pos + topSeqPart2.length());
//				instance.altPlusSeq = topSeqAllele1;
//				instance.alt2PlusSeq = topSeqAllele2;
//				
//				if(isIndel) {
//					
//					//CASE WHERE ONE EVENT IS AN INSERTION AND OTHER EVENT IS A DELETION
//					//let the reference allele be the entire sequence region
//					//let I be the insertion event [AAAGGG]
//					//let D be the deletion event [-]
//					
//					//B = BEFORE
//					//A = AFTER
//					//SEQUENCE1......B[-/CHANGE]A.....SEQUENCE2
//					//ALT1 = B
//					//ALT2 = B + CHANGE
//					//REF = B + PART OF CHANGE
//					
//
//					int endOfPartOne = refPlus.indexOf(topSeqPart1) + topSeqPart1.length() - 1;
//					int startOfPartTwo = refPlus.indexOf(topSeqPart2);
//					instance.refAllele  = refPlus.substring(endOfPartOne, startOfPartTwo);
//					
//					
//					instance.refPlusSeq = topSeqPart1 + refPlus.substring(endOfPartOne + 1, startOfPartTwo) + topSeqPart2;
//					
//					instance.altAllele = baseBeforeChange + topAllele1;
//					instance.alt2Allele = baseBeforeChange + topAllele2;
//					
//					instance.refAlleleTop = instance.refAllele;
//					if(topAllele1.equals("")) {
//						instance.altAlleleTop = "D";
//						instance.alt2AlleleTop = "I";
//					} else {
//						instance.altAlleleTop = "I";
//						instance.alt2AlleleTop = "D";
//					}
//					
//					instance.pos = instance.pos - 1;
//					
//				}
//				
//			} else if(refPlus.contains(botSeqPart2) && refPlus.contains(botSeqPart1)) {
//				instance.topStrandIsPlus = false;
//				
//				instance.refAlleleTop = reverseComplement(instance.refAllele);
//				instance.altAlleleTop = topAllele1;
//				instance.alt2AlleleTop = topAllele2;
//				
//				//instance.refAllele = instance.refAllele;
//				instance.altAllele =  botAllele1;
//				instance.alt2Allele = botAllele2;
//				
//				instance.refPlusSeq = reference.query(instance.chr, instance.pos - topSeqPart1.length(), instance.pos + topSeqPart2.length());
//				instance.altPlusSeq = bottomSeqAllele1;
//				instance.alt2PlusSeq = bottomSeqAllele2;
//				
//				String baseAfterChangeRC = reverseComplement(baseAfterChange);
//				
//				if(isIndel) {
//					
//					
//					int endOfPartOne = refPlus.indexOf(botSeqPart2) + botSeqPart2.length() - 1;
//					int startOfPartTwo = refPlus.indexOf(botSeqPart1);
//					instance.refAllele  = refPlus.substring(endOfPartOne, startOfPartTwo);
//					
//					
//					instance.refPlusSeq = botSeqPart2 + refPlus.substring(endOfPartOne + 1, startOfPartTwo) + botSeqPart1;
//					
//					instance.altAllele = baseAfterChangeRC + botAllele1;
//					instance.alt2Allele = baseAfterChangeRC + botAllele2;
//					
//					instance.refAlleleTop = instance.refAllele;
//					if(topAllele1.equals("")) {
//						instance.altAlleleTop = "D";
//						instance.alt2AlleleTop = "I";
//					} else {
//						instance.altAlleleTop = "I";
//						instance.alt2AlleleTop = "D";
//					}
//					
//					instance.pos = instance.pos - 1;
//					
//				}
//
//			} else {
//				throw new IllegalStateException("Count not match nearby reference sequence " + instance.illmId);
//			}
		}
		
		//set alleles
		if(instance != null) {
			if(instance.refAlleleString != null) {
				instance.refAllele = Allele.create(instance.refAlleleString,true);
			}
			
			if(instance.altAlleleString != null) {
				instance.altAllele = Allele.create(instance.altAlleleString);
			}
			
			if(instance.alt2AlleleString != null) {
				instance.alt2Allele = Allele.create(instance.alt2AlleleString);
			}
		}
		
		//validate reference allele position
		if(!instance.hasError()) {
			String seqToCompare = reference.query(instance.chr, instance.pos, instance.pos + instance.refAlleleString.length() - 1);
			
			if(!seqToCompare.toUpperCase().equals(instance.refAlleleString)) {
				instance.matchesReference = false;
				//throw new IllegalStateException("Error reference allele does not match at: " + instance.illmId +" " + instance.chr + ":" + instance.pos + " " + instance.refAlleleString + " " + seqToCompare);
			}
		}
		
		// 1:100316615-CAG-C  ==> C is  1:100316614, A is 1:100316615 === Deletion is alternative allele
		// 1:100336041-TAGAC-T ==> T is 1:100336040, A is 1:100336041 === Deletion is alternative allele
		return instance;
	}
	
	static String reverseComplement(String in) {
		return SequenceUtil.reverseComplement(in);
	}
	
	public boolean matchesReference() {
		return this.matchesReference;
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

	public String getRefAlleleString() {
		return refAlleleString;
	}

	public String getAltAlleleString() {
		return altAlleleString;
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
	
	public boolean hasReferenceAllele() {
		return this.hasRefAllele;
	}
	
	public String getAlt2AlleleTop() {
		return alt2AlleleTop;
	}

	public void setAlt2AlleleTop(String alt2AlleleTop) {
		this.alt2AlleleTop = alt2AlleleTop;
	}

	public String getAlt2AlleleString() {
		return alt2AlleleString;
	}


	public String getAlt2PlusSeq() {
		return alt2PlusSeq;
	}

	@Override
	public String toString() {
		return toVCFLine();
	}
	
	public VariantContext toVariantContext(Genotype... genotypes) {
		List<Genotype> genotypesList = new LinkedList<Genotype>();
		for(Genotype genotype : genotypes) {
			genotypesList.add(genotype);
		}
		return toVariantContext(genotypesList);
	}
	
	public Allele refAllele() {
		return this.refAllele;
	}
	
	public Allele altAllele() {
		return this.altAllele;
	}
	
	public Allele alt2Allele() {
		return this.alt2Allele;
	}
	
	public float getGTScore() {
		return this.gtScore;
	}
	
	public void setGTScore(float gtScore) {
		this.gtScore = gtScore;
	}
	
	public VariantContext toVariantContext(List<Genotype> genotypes) {
		if(this.hasError()) {
			return null;
		}
		
		List<Allele> alleles = new LinkedList<Allele>();
		alleles.add(this.refAllele());
		alleles.add(this.altAllele());
		if(!this.hasRefAllele) {
			alleles.add(this.alt2Allele());
		}
		
		
		VariantContextBuilder builder = new VariantContextBuilder(null, this.chr, this.pos, this.pos + this.refAlleleString.length() - 1, alleles);
		
		if(genotypes != null && genotypes.size() > 0) {
			builder.genotypes(genotypes);
		}
		
		//adds id column
		builder.id(this.getName());
		
		
		builder.attribute("TopStrand", this.getTopStrandSeqBase());
		
		if(this.topStrandIsPlus) {
			builder.attribute("TopStrandIsPlus", true);
		}
		
		builder.attribute("RefSeqPlus", this.refPlusSeq);
		builder.attribute("RefAlleleTop", this.refAlleleTop);
		
		builder.attribute("Alt1SeqPlus", this.altPlusSeq);
		builder.attribute("Alt1AlleleTop", this.altAlleleTop);
		
		if(!hasRefAllele) {
			builder.attribute("Alt2SeqPlus", this.alt2PlusSeq);
			builder.attribute("Alt2AlleleTop", this.alt2AlleleTop);
		}
		
		builder.attribute("SNP", this.snpString);
		
		builder.attribute("SNP", this.snpString);
		builder.attribute("IlluminaStrand", this.ilmnStrand);
		builder.attribute("IlluminaId", this.illmId);
		
		//set gc score
		builder.attribute("GenTrainScore", this.gtScore);
		
		if(!hasRefAllele) {
			builder.attribute("NoReferenceProbe", true);
		}
		
		VariantContext vc = builder.make();
		

		
		return vc;
	}
	
	public static String replaceTrailingN(String in) {
		return in.replaceFirst("^N+", "").replaceFirst("N+$", "");
	}
	
	public String toVCFLine() {
		return encoder.encode(this.toVariantContext());
	}
	
	public static String computeLongestSubsequenceFromStart(String reference, String alt1, String alt2) {
		String use = null;
		if(alt1.length() > alt2.length()) {
			use = alt1;
		} else {
			use = alt2;
		}
		
		if(reference.charAt(0) != use.charAt(0)) {
			throw new IllegalStateException(reference + "\nis not at correct start position for\n" + use);
		}
		
		int endPos = 0;
		for(endPos = 0; endPos < use.length() && endPos < reference.length(); endPos++) {
			if(reference.charAt(endPos) != use.charAt(endPos)) {
				break;
			}
		}
		
		return reference.substring(0, endPos);
	}
}
