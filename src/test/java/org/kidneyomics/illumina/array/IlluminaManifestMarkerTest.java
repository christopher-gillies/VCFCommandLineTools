package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;

import org.junit.Test;
import org.kidneyomics.illumina.array.IlluminaManifestMarker.STRAND;
import org.springframework.core.io.ClassPathResource;

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
		
		assertEquals("A",marker.getRefAllele());
		assertEquals("G",marker.getAltAllele());
		
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
		
		assertEquals("G",marker.getRefAllele());
		assertEquals("T",marker.getAltAllele());
		
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
		
		assertEquals("T",marker.getRefAllele());
		assertEquals("A",marker.getAltAllele());
		
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
		
		assertEquals("T",marker.getRefAllele());
		assertEquals("A",marker.getAltAllele());
		
		assertEquals("A",marker.getRefAlleleTop());
		assertEquals("T",marker.getAltAlleleTop());
		
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtTgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getRefPlusSeq());
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtAgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getAltPlusSeq());
	}
	
	
	@Test
	public void testThatSequenceParsedCorrectly5() {

		

		HashMap<String,String> data = new HashMap<>();
		
		data.put("IlmnID", "20:10393913-C-T-0_B_F_2304273903");
		data.put("Name", "20:10393913-C-T");
		data.put("IlmnStrand", "BOT");
		data.put("SNP", "[T/C]");
		data.put("Chr", "20");
		data.put("MapInfo", "10237223");
		data.put("TopGenomicSeq", "TCAGTCACCTTTTGGTCACACATCCCATTTTAAAGATCCTGACAGCCTCCATACAGAAT[A/G]ATGTGTCAAGCTTCAGTGATTGTGGCTTATTCACAGCTATTCTTTGCTGCAACCTGATT");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		
		assertEquals("T",marker.getRefAllele());
		assertEquals("A",marker.getAltAllele());
		
		assertEquals("A",marker.getRefAlleleTop());
		assertEquals("T",marker.getAltAlleleTop());
		
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtTgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getRefPlusSeq());
		assertEquals("tttacagataaaatactgaggaaatcacaagttaaaaatatacaCTGATGagcctttgtAgaaagcactttggtaatatacctcagctgtcattaatttattttctttgacctgtaaat".toUpperCase(),marker.getAltPlusSeq());
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
		
		assertEquals("A",marker.getRefAllele());
		assertEquals("ACAGG",marker.getAltAllele());
		
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
		
		assertEquals("GAGTACTACTAA",marker.getRefAllele());
		assertEquals("G",marker.getAltAllele());
		
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
		
		assertEquals("GAGTACTACTAA",marker.getRefAllele());
		assertEquals("G",marker.getAltAllele());
		
		assertEquals("I",marker.getRefAlleleTop());
		assertEquals("D",marker.getAltAlleleTop());
		
		assertEquals("GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTGAGTACTACTAAAGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC",marker.getRefPlusSeq());
		assertEquals("GGTGAGCATACAGGCAGGTTTACTTGTTAATATACTACGCACCAAACAAAGGAGGATCTGAGTCCACTGGGATTCGACAACCACAGGTCTCAGACTTGAGATAACTGATGCAAAGACTC",marker.getAltPlusSeq());
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
		
		System.err.println(marker.toVCFLine());
	}
	
	
	
}
