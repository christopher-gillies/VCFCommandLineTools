package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;

import org.junit.Test;
import org.kidneyomics.illumina.array.IlluminaManifestMarker.STRAND;
import org.kidneyomics.referenceseq.ReferenceFASTA;
import org.springframework.core.io.ClassPathResource;

import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

public class IlluminaManifestMarkerTest {

	static ReferenceFASTA fasta;
	static {
		ClassPathResource resource = new ClassPathResource("20.fa.gz");
	
		try {
			fasta = ReferenceFASTA.create(resource.getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
	@Test
	public void testSnpPattern1() {
		
		Matcher m = IlluminaManifestMarker.snpPattern.matcher("[A/T]");
		
		
		assertTrue(m.matches());
		assertEquals(2,m.groupCount());
		assertEquals("A",m.group(1));
		assertEquals("T",m.group(2));
	}
	
	@Test
	public void testSnpPattern2() {
		
		Matcher m = IlluminaManifestMarker.snpPattern.matcher("[A/G]");
		
		
		assertTrue(m.matches());
		assertEquals(2,m.groupCount());
		assertEquals("A",m.group(1));
		assertEquals("G",m.group(2));
	}
	
	@Test
	public void testSnpPattern3() {
		
		Matcher m = IlluminaManifestMarker.snpPattern.matcher("[D/I]");
		
		
		assertTrue(m.matches());
		assertEquals(2,m.groupCount());
		assertEquals("D",m.group(1));
		assertEquals("I",m.group(2));
	}

	
	@Test
	public void testSeqPattern1() {
		
		Matcher m = IlluminaManifestMarker.seqPattern.matcher("CAGCATGGATGATCCCATCATATCCTGTCAGAAACTTCTTATCTTCTTCACTGTCTTCT[A/G]CCTGCTGATACCCCATGAAAATCATTGTCACCGGTTCTGTATCATTTATGTCTGGAGGC");
		
		
		assertTrue(m.matches());
		assertEquals(6,m.groupCount());
		assertEquals("CAGCATGGATGATCCCATCATATCCTGTCAGAAACTTCTTATCTTCTTCACTGTCTTCT",m.group(1));
		assertEquals("T",m.group(2));
		assertEquals("A",m.group(3));
		assertEquals("G",m.group(4));
		assertEquals("CCTGCTGATACCCCATGAAAATCATTGTCACCGGTTCTGTATCATTTATGTCTGGAGGC",m.group(5));
		assertEquals("C",m.group(6));
	}
	
	@Test
	public void testSeqPattern2() {
		
		Matcher m = IlluminaManifestMarker.seqPattern.matcher("tggggtctgcacctgtaacctccacattgttcaaggatcaactCTAAACACAAACCTGT[A/C]TATCTTCACAGGTCAAAAGTTGTGACACTTCACTGTCAATCAATGTGAAGGAGCCAATA");
		
		
		assertTrue(m.matches());
		assertEquals(6,m.groupCount());
		assertEquals("tggggtctgcacctgtaacctccacattgttcaaggatcaactCTAAACACAAACCTGT",m.group(1));
		assertEquals("T",m.group(2));
		assertEquals("A",m.group(3));
		assertEquals("C",m.group(4));
		assertEquals("TATCTTCACAGGTCAAAAGTTGTGACACTTCACTGTCAATCAATGTGAAGGAGCCAATA",m.group(5));
		assertEquals("T",m.group(6));
	}
	
	@Test
	public void testSeqPattern3() {
		
		Matcher m = IlluminaManifestMarker.seqPattern.matcher("ACATCCAGAATGTGCCTATAATCTTGTGAATTCTCCACACTTAAAACCTGCCTGGGTCTT[-/AGAC]AGAGCACTTTGGCGTTTCTCCTGTGATGTTGCAGAAGGGAAATACAAAGAAAAGGGAAT");
		
		
		assertTrue(m.matches());
		assertEquals(6,m.groupCount());
		assertEquals("ACATCCAGAATGTGCCTATAATCTTGTGAATTCTCCACACTTAAAACCTGCCTGGGTCTT",m.group(1));
		assertEquals("T",m.group(2));
		assertEquals("-",m.group(3));
		assertEquals("AGAC",m.group(4));
		assertEquals("AGAGCACTTTGGCGTTTCTCCTGTGATGTTGCAGAAGGGAAATACAAAGAAAAGGGAAT",m.group(5));
		assertEquals("A",m.group(6));
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectly1() {
		/*
		 * 		
		instance.illmId = vals.get("IlmnID");
		instance.name = vals.get("Name");
		instance.ilmnStrand = STRAND.valueOf(vals.get("IlmnStrand"));
		instance.snpString = vals.get("SNP");
		instance.chr = vals.get("chr");
		instance.pos = Integer.parseInt(vals.get("MapInfo"));
		instance.topStrandSeqBase = vals.get("TopGenomicSeq").toUpperCase();
		 */
		
		//case 1
		//top is plus strand
		//ref = A
		//alt = G
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10023905-AG-0_T_F_2299624118");
		data.put("Name", "20:10023905-AG");
		data.put("IlmnStrand", "TOP");
		data.put("SNP", "[A/G]");
		data.put("Chr", "20");
		data.put("MapInfo", "10023905");
		data.put("TopGenomicSeq", "TGTCAACAATTCTACCTATGAAGGAAAGCCAATATTCCTTAGAGCTTGTGAAGATGCAC[A/G]TGATGTTAAAGATGTGTGCCTGACATTTTTGGAAAAAGGAGCCAATCCTAATGCAATCA");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertEquals("A",marker.getRefAlleleString());
		assertEquals("G",marker.getAltAlleleString());
		
		assertEquals("A",marker.getRefAlleleTop());
		assertEquals("G",marker.getAltAlleleTop());
		
		assertEquals("TGTCAACAATTCTACCTATGAAGGAAAGCCAATATTCCTTAGAGCTTGTGAAGATGCACATGATGTTAAAGATGTGTGCCTGACATTTTTGGAAAAAGGAGCCAATCCTAATGCAATCA",marker.getRefPlusSeq());
		assertEquals("TGTCAACAATTCTACCTATGAAGGAAAGCCAATATTCCTTAGAGCTTGTGAAGATGCACGTGATGTTAAAGATGTGTGCCTGACATTTTTGGAAAAAGGAGCCAATCCTAATGCAATCA",marker.getAltPlusSeq());
		
	}
	
	@Test
	public void testThatSequenceParsedCorrectly2() {

		
		//case 4
		//top is minus strand
		//reference = G
		//alternative = C
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10030352-GT-0_T_R_2299624127");
		data.put("Name", "20:10030352-GT");
		data.put("IlmnStrand", "TOP");
		data.put("SNP", "[A/C]");
		data.put("Chr", "20");
		data.put("MapInfo", "10030352");
		data.put("TopGenomicSeq", "TCCTTTAAAGAATTCATTAATATTGACCCCTCCTCCCCGGGTTTTCTCATGAAGGTGAG[A/C]GATGGCAGCCAGCTGTTCTGAGCTTGCATAATCCTGCCTTTCCTCCAACACCATCACGA");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertEquals("G",marker.getRefAlleleString());
		assertEquals("T",marker.getAltAlleleString());
		
		assertEquals("C",marker.getRefAlleleTop());
		assertEquals("A",marker.getAltAlleleTop());
		
		assertEquals("TCGTGATGGTGTTGGAGGAAAGGCAGGATTATGCAAGCTCAGAACAGCTGGCTGCCATCGCTCACCTTCATGAGAAAACCCGGGGAGGAGGGGTCAATATTAATGAATTCTTTAAAGGA",marker.getRefPlusSeq());
		assertEquals("TCGTGATGGTGTTGGAGGAAAGGCAGGATTATGCAAGCTCAGAACAGCTGGCTGCCATCTCTCACCTTCATGAGAAAACCCGGGGAGGAGGGGTCAATATTAATGAATTCTTTAAAGGA",marker.getAltPlusSeq());
	}
	
	@Test
	public void testThatSequenceParsedCorrectly3() {

		
		//case 2
		//top is plus strand
		//reference = G
		//alternative = C
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10237223-T-A-0_T_F_2304290883");
		data.put("Name", "20:10237223-T-A");
		data.put("IlmnStrand", "TOP");
		data.put("SNP", "[A/T]");
		data.put("Chr", "20");
		data.put("MapInfo", "10237223");
		data.put("TopGenomicSeq", "tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgt[A/T]gaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertEquals("T",marker.getRefAlleleString());
		assertEquals("A",marker.getAltAlleleString());
		
		assertEquals("T",marker.getRefAlleleTop());
		assertEquals("A",marker.getAltAlleleTop());
		
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtTgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getRefPlusSeq());
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtAgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getAltPlusSeq());
	}
	
	@Test
	public void testThatSequenceParsedCorrectly4() {

		
		//case 3
		//top is minus strand
		//reference = T
		//alternative = A
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10237223-T-A-0_T_F_2304290883");
		data.put("Name", "20:10237223-T-A");
		data.put("IlmnStrand", "TOP");
		data.put("SNP", "[A/T]");
		data.put("Chr", "20");
		data.put("MapInfo", "10237223");
		data.put("TopGenomicSeq", "atttacaggtcaaagaaaataaattaatgacagctgaggtatattaccaaagtgctttc[A/T]acaaaggctCATCAGtgtatatttttaacttgtgatttcctcagtattttatctgtaaa");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertEquals("T",marker.getRefAlleleString());
		assertEquals("A",marker.getAltAlleleString());
		
		assertEquals("A",marker.getRefAlleleTop());
		assertEquals("T",marker.getAltAlleleTop());
		
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtTgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getRefPlusSeq());
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtAgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getAltPlusSeq());
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectly6() {

		
		//case 3
		//top is minus strand
		//reference = T
		//alternative = A
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:9464244-C-T-0_T_R_2304199051");
		data.put("Name", "20:9464244-C-T");
		data.put("IlmnStrand", "TOP");
		data.put("SNP", "[A/G]");
		data.put("Chr", "20");
		data.put("MapInfo", "9464244");
		data.put("TopGenomicSeq", "gccttctgttagatttgcacaagtgatgaagaaggagaagctagagttttgaacgaggc[A/G]tccctaagataacctcagcaataaaataataggggtattccttcttcactctccataaT");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertTrue(marker.surroundingSequenceMatches());
		
//		assertEquals("T",marker.getRefAllele());
//		assertEquals("A",marker.getAltAllele());
//		
//		assertEquals("A",marker.getRefAlleleTop());
//		assertEquals("T",marker.getAltAlleleTop());
//		
//		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtTgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getRefPlusSeq());
//		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtAgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getAltPlusSeq());
//	
	}
	

	
	@Test
	public void testReplaceAll() {
		String data = "CYATCGAATCCGCTATCCAGACTTGGTATAWCCAAGGAATCATTATTTAATGAACAAGTA[C/-]AGGAGAAATGTAGATGATGAGAAATGAGGAAAAGAGAAATGGAAACACAGGGACAACGGC";
		
		String result = data.replaceAll(IlluminaManifestMarker.replacePattern.pattern(),"");
		
		assertEquals("CATCGAATCCGCTATCCAGACTTGGTATACCAAGGAATCATTATTTAATGAACAAGTA[C/-]AGGAGAAATGTAGATGATGAGAAATGAGGAAAAGAGAAATGGAAACACAGGGACAACGGC",result);
		
	}
	
	@Test
	public void testThatSequenceParsedCorrectly5() {

		

		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393913-C-T-0_B_F_2304273903");
		data.put("Name", "20:10393913-C-T");
		data.put("IlmnStrand", "BOT");
		data.put("SNP", "[T/C]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393913");
		data.put("TopGenomicSeq", "TCAGTCACCTTTTGGTCACACATCCCATTTTAAAGATCCTGACAGCCTCCATACAGAAT[A/G]ATGTGTCAAGCTTCAGTGATTGTGGCTTATTCACAGCTATTCTTTGCTGCAACCTGATT");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		assertTrue(marker.hasError());
//		assertEquals("G",marker.getRefAllele());
//		assertEquals("T",marker.getAltAllele());
//		assertEquals("C",marker.getAlt2Allele());
//		
//		assertEquals("C",marker.getRefAlleleTop());
//		assertEquals("A",marker.getAltAlleleTop());
//		assertEquals("G",marker.getAlt2AlleleTop());
//		
//		assertEquals("AATCAGGTTGCAGCAAAGAATAGCTGTGAATAAGCCACAATCACTGAAGCTTGACACATGATTCTGTATGGAGGCTGTCAGGATCTTTAAAATGGGATGTGTGACCAAAAGGTGACTGA".toUpperCase(),marker.getRefPlusSeq());
//		assertEquals("AATCAGGTTGCAGCAAAGAATAGCTGTGAATAAGCCACAATCACTGAAGCTTGACACATTATTCTGTATGGAGGCTGTCAGGATCTTTAAAATGGGATGTGTGACCAAAAGGTGACTGA".toUpperCase(),marker.getAltPlusSeq());
//		assertEquals("AATCAGGTTGCAGCAAAGAATAGCTGTGAATAAGCCACAATCACTGAAGCTTGACACATCATTCTGTATGGAGGCTGTCAGGATCTTTAAAATGGGATGTGTGACCAAAAGGTGACTGA".toUpperCase(),marker.getAlt2PlusSeq());
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectly7() {

		

		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393913-C-T-0_B_F_2304273903");
		data.put("Name", "20:10393913-C-T");
		data.put("IlmnStrand", "BOT");
		data.put("SNP", "[T/C]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393913");
		data.put("TopGenomicSeq", "TCAGTCACCTTTTGGTCACACATCCCATTTTAAAGATCCTGACAGCCTCCATACAGAAT[A/G]ATGTGTCAAGCTTCAGTGATTGTGGCTTATTCACAGCTATTCTTTGCTGCAACCTGATT");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);

		
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectly8() throws IOException {

		ClassPathResource resource = new ClassPathResource("chr19.fa.gz");
		
		ReferenceFASTA chr19 = ReferenceFASTA.create(resource.getFile());
		
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "seq-t1d-19-60034052-C-T-1_T_R_2304705745");
		data.put("Name", "seq-t1d-19-60034052-C-T-1_T_R_2304705745");
		data.put("IlmnStrand", "TOP");
		data.put("SNP", "[A/G]");
		data.put("Chr", "19");
		data.put("MapInfo", "55342240");
		data.put("TopGenomicSeq", "TATGTGGACATGGTAATGATAACAGCGGTTTCTTTCAGCGAATACAGTGTCACATTACCT[A/G]AAGCAATGAGGGCAGACATGTTTATTTGAANAGGAGACAGCTACATTGAAATCACAAAAA");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, chr19);
		
		assertTrue(marker.surroundingSequenceMatches());
		
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectly9() throws IOException {

		ClassPathResource resource = new ClassPathResource("chr11.fa.gz");
		
		ReferenceFASTA chr11 = ReferenceFASTA.create(resource.getFile());
		
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "11:1161982-GA-0_B_R_2299273705");
		data.put("Name", "11:1161982-GA");
		data.put("IlmnStrand", "BOT");
		data.put("SNP", "[T/C]");
		data.put("Chr", "11");
		data.put("MapInfo", "1161982");
		data.put("TopGenomicSeq", "CCTGGGGTCCCGCCCCACAGCCCCCAGCAAAACCCTTGTCCTTTGTGTCCCCAGCCAAC[A/G]TCACCATCTTCAGACCCTCAACCTTCTTCATCATCGCCCAGACCAGCCTGGGCCTGCAG");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, chr11);
		
		assertTrue(marker.surroundingSequenceMatches());
		
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectlyIndel1() {

		//case 1
		//top is plus strand
		//ref = A
		//alt = G
		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393286-A-ACAGG-0_M_R_2304843851");
		data.put("Name", "20:10393286-A-ACAGG");
		data.put("IlmnStrand", "MINUS");
		data.put("SNP", "[D/I]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393286");
		data.put("TopGenomicSeq", "GGCAATAATACGATGCATATTGAGAAACTGCTTCAAAGATGGATGTATAACTTTTTGGCA[-/CAGG]CAGGACAAGATCTACGTGGTCACTGATTAGCTGCCTTCCTAGGTTAAGCAGCTGGTCCA");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertTrue(marker.isIndel());
		
		assertEquals("A",marker.getRefAlleleString());
		assertEquals("ACAGG",marker.getAltAlleleString());
		
		assertEquals("D",marker.getRefAlleleTop());
		assertEquals("I",marker.getAltAlleleTop());
		
		assertEquals("GGCAATAATACGATGCATATTGAGAAACTGCTTCAAAGATGGATGTATAACTTTTTGGCACAGGACAAGATCTACGTGGTCACTGATTAGCTGCCTTCCTAGGTTAAGCAGCTGGTCCA",marker.getRefPlusSeq());
		assertEquals("GGCAATAATACGATGCATATTGAGAAACTGCTTCAAAGATGGATGTATAACTTTTTGGCACAGGCAGGACAAGATCTACGTGGTCACTGATTAGCTGCCTTCCTAGGTTAAGCAGCTGGTCCA",marker.getAltPlusSeq());
	}
	
	@Test
	public void testThatSequenceParsedCorrectlyIndel2() {


		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393722-GAGTACTACTAA-G-0_P_F_2304231134");
		data.put("Name", "20:10393722-GAGTACTACTAA-G");
		data.put("IlmnStrand", "PLUS");
		data.put("SNP", "[I/D]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393722");
		data.put("TopGenomicSeq", "GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTG[-/AGTACTACTAA]AGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertTrue(marker.isIndel());
		
		assertEquals("GAGTACTACTAA",marker.getRefAlleleString());
		assertEquals("G",marker.getAltAlleleString());
		
		assertEquals("I",marker.getRefAlleleTop());
		assertEquals("D",marker.getAltAlleleTop());
		
		assertEquals("GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTGAGTACTACTAAAGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC",marker.getRefPlusSeq());
		assertEquals("GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTGAGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC",marker.getAltPlusSeq());
	}
	
	@Test
	public void testThatSequenceParsedCorrectlyIndel3() {


		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393722-GAGTACTACTAA-G-0_P_F_2304231134");
		data.put("Name", "20:10393722-GAGTACTACTAA-G");
		data.put("IlmnStrand", "MINUS");
		data.put("SNP", "[I/D]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393722");
		data.put("TopGenomicSeq", "GAGTCTTTGCATCAGTTATCTCAAGTCTGAGACCTGTGGTTGTCGAATCCCAGTGGACT[-/TTAGTAGTACT]CAGATCCTCCTTTGTTTGGTGCGTAGTATATTAACAAGTAAACCTGCCTGTATGCTCACC");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertTrue(marker.isIndel());
		
		assertEquals("GAGTACTACTAA",marker.getRefAlleleString());
		assertEquals("G",marker.getAltAlleleString());
		
		assertEquals("I",marker.getRefAlleleTop());
		assertEquals("D",marker.getAltAlleleTop());
		
		assertEquals("GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTGAGTACTACTAAAGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC",marker.getRefPlusSeq());
		assertEquals("GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTGAGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC",marker.getAltPlusSeq());
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectlyIndel4() {


		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:3026341-AGCCCC-AGCCCCGCCCC-XXX");
		data.put("Name", "20:3026341-AGCCCC-AGCCCCGCCCC");
		data.put("IlmnStrand", "MINUS");
		data.put("SNP", "[I/D]");
		data.put("Chr", "20");
		data.put("MapInfo", "3026341");
		data.put("TopGenomicSeq", "ACGAGACCAGTGTCCTGAGACATGACCGCCACCTCTCCCTCCGCAGACCGCAGCCCGAGA[-/GCCCCGCCCC]GCCCCGCCCCGCCATCCTCCAATAAAGTGTGAGGTTCTCCGAAGCTGTTGCGTCGAGTT");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertTrue(marker.isIndel());
		
		assertTrue(marker.hasError());
		
//		assertEquals(3026340,marker.getPos());
//		assertEquals("AGCCCC",marker.getRefAllele());
//		assertEquals("A",marker.getAltAllele());
//		assertEquals("AGCCCCGCCCC",marker.getAlt2Allele());
//		
//		assertEquals("AGCCCC",marker.getRefAlleleTop());
//		assertEquals("D",marker.getAltAlleleTop());
//		assertEquals("I",marker.getAlt2AlleleTop());
//		
//		//TODO: validate
//		assertEquals("ACGAGACCAGTGTCCTGAGACATGACCGCCACCTCTCCCTCCGCAGACCGCAGCCCGAGAGCCCCGCCCCGCCCCGCCATCCTCCAATAAAGTGTGAGGTTCTCCGAAGCTGTTGCGTCGAGTT",marker.getRefPlusSeq());
//		assertEquals("ACGAGACCAGTGTCCTGAGACATGACCGCCACCTCTCCCTCCGCAGACCGCAGCCCGAGAGCCCCGCCCCGCCATCCTCCAATAAAGTGTGAGGTTCTCCGAAGCTGTTGCGTCGAGTT",marker.getAltPlusSeq());
//		assertEquals("ACGAGACCAGTGTCCTGAGACATGACCGCCACCTCTCCCTCCGCAGACCGCAGCCCGAGAGCCCCGCCCCGCCCCGCCCCGCCATCCTCCAATAAAGTGTGAGGTTCTCCGAAGCTGTTGCGTCGAGTT",marker.getAlt2PlusSeq());
	}
	
	@Test
	public void testToVCFLine() {


		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393722-GAGTACTACTAA-G-0_P_F_2304231134");
		data.put("Name", "20:10393722-GAGTACTACTAA-G");
		data.put("IlmnStrand", "MINUS");
		data.put("SNP", "[I/D]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393722");
		data.put("TopGenomicSeq", "GAGTCTTTGCATCAGTTATCTCAAGTCTGAGACCTGTGGTTGTCGAATCCCAGTGGACT[-/TTAGTAGTACT]CAGATCCTCCTTTGTTTGGTGCGTAGTATATTAACAAGTAAACCTGCCTGTATGCTCACC");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		//validate that the id is set correctly
		assertEquals("20:10393722-GAGTACTACTAA-G", marker.toVariantContext().getID());
		
		System.err.println(marker.toVCFLine());
	}
	
	
	@Test
	public void testWriteVCF() {
		
		VCFEncoder encoder = IlluminaManifestMarker.encoder();
		
		HashMap<String,String> data = new HashMap<>();
		data.put("IlmnID", "20:10393722-GAGTACTACTAA-G-0_P_F_2304231134");
		data.put("Name", "20:10393722-GAGTACTACTAA-G");
		data.put("IlmnStrand", "MINUS");
		data.put("SNP", "[I/D]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393722");
		data.put("TopGenomicSeq", "GAGTCTTTGCATCAGTTATCTCAAGTCTGAGACCTGTGGTTGTCGAATCCCAGTGGACT[-/TTAGTAGTACT]CAGATCCTCCTTTGTTTGGTGCGTAGTATATTAACAAGTAAACCTGCCTGTATGCTCACC");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		System.err.println(IlluminaManifestMarker.header().toString());
		
		String encoding = encoder.encode(marker.toVariantContext());
		System.err.println(encoding);
		
		
	}
	
	
	@Test
	public void testTrimTrailingN() {
		assertEquals("AAAAAANAAAAA",IlluminaManifestMarker.replaceTrailingN("NNNNNAAAAAANAAAAANNNNN"));
		
		assertEquals("AAAAAANNNAAAAA",IlluminaManifestMarker.replaceTrailingN("NNNNNAAAAAANNNAAAAANNNNN"));
		
		
	}
	
//	@Test
//	public void testComputeLongestSubsequenceFromStart() {
//		String reference = "AGCCCCGCCCCGCCCCGCCccgccATCCTCCAATAAAGTGTGAGGTTCTCCGAAGCTGTTGCGTCGAGTT".toUpperCase();
//		String alt1 = "";
//		String alt2 = "AGCCCCGCCCC";
//		
//		String result = IlluminaManifestMarker.computeLongestSubsequenceFromStart(reference, alt1, alt2);
//	}
	
	
	
}
