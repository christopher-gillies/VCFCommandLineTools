package org.kidneyomics.referenceseq;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kidneyomics.referenceseq.KmerKey;
import org.kidneyomics.referenceseq.ReferenceFASTA;
import org.springframework.core.io.ClassPathResource;

public class ReferenceFASTATest {
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
	
//	@Test
//	public void testBuildKmerIndex() {
//		System.err.println("Building kmer index");
//		//fasta.buildKmerIndex();
//		System.err.println(fasta.getBasesPer("20"));
//		System.err.println(fasta.getKmerMapSize("20"));
//		
//		
//		String query = "TGTCAACAATTCTACCTATGAAGGAAAGCCAATATTCCTTAGAGCTTGTGAAGATGCACATGATGTTAAAGATGTGTGCCTGACATTTTTGGAAAAAGGAGCCAATCCTAATGCAATCA";
//		
//		Map<String,List<ChrPos>> locations = fasta.findMatchingKmers(query);
//		
//		for(Map.Entry<String, List<ChrPos>> entry : locations.entrySet()) {
//			String chr = entry.getKey();
//			List<ChrPos> poses = entry.getValue();
//			for(ChrPos chrpos : poses) {
//				System.err.println(chrpos.chr() + ":" + chrpos.pos());
//			}
//		}
//	}
	
	@Test
	public void readFASTA1() throws IOException {

		
		
		String result1 = fasta.query("20", 10023846, 10023964);
		
		assertEquals("TGTCAACAATTCTACCTATGAAGGAAAGCCAATATTCCTTAGAGCTTGTGAAGATGCACATGATGTTAAAGATGTGTGCCTGACATTTTTGGAAAAAGGAGCCAATCCTAATGCAATCA",result1);
		
		
		
		String result2 = fasta.query("20", 10024981, 10025099);
		
		assertEquals("ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTC",result2);
		
		
		String queryRes2 = "GTGGGAGAGAACTGGAACAAGAACCCAGTGCTCTTTCTGCTCTACCCACT" + 
				"GACCCATCCTCTCACGCATCATACACCCATACTCCCATCCACCCACCTTC" + 
				"CCATTCATGCATTCACCCATTCACCCACCTTCCATCCATCTACCATCCAC" + 
				"CACGTACCTACACTCCCATCTACCATCCAACCACATTTCCATTCACCCAT" + 
				"CCTCCCATCCATCAACCCTCCAATCCACCACCCACAGACCTTCCCATCCA" + 
				"TTCATTTACCCATCCACATATTCACCCACCCTCCCATCCATCCATCTACT" + 
				"GTCTATCACCTACTCATTTTCCCATCTGCTATTCACCCCCACCCTCCTAT" + 
				"CCATCCATTCAACCATCCACCCACCCTCCCATCCAATCATCCACTGTCCC" + 
				"AACCATCCACCATCTACCCATCTATCCACCACTCACCCTTTCTTTCATTC" + 
				"ACTCATCCTCCCATCCTCCCATCCACCATCCACCCTCCACCCACCCTCCC" + 
				"ATCCAATCATCCACTGTCCCAACCATCCACCATCTACCCACCTATCCACC" + 
				"ACTCACCCTTTCTTTCATTCACTCATCCTCCCATCCTCCCATCCACCATC" + 
				"CACCCTCCACCCATTTATTTGACAGTTGCTGAGAGCCTACTAACCACCAA" + 
				"TCACTATTCCAGGCACTGAGATATGACTTTGAGCTAGACAAAGTCTTGCT" + 
				"TCATGGAGCTCACATTTTGGCTGTGAATGGCAGCAACAGACAAATAAATA" + 
				"CGTAGTTTACGTGAGGTACTGCAAAGAAAAAATAAAGCAGGGGAGTGATG" + 
				"TCAGGAGTAACTAAGGCAGGATGGTAGTCAGGGAGGTCGTCTCTGAAACG" + 
				"GGACATTTGAGCAGAAGCCTGAAGGAAGTGAGCTCCCTGACAACTTGGGA" + 
				"GAAGAGAATTTCAGGCAGAGAGAACAGTTGTACAAAGGCCCAGTGGTAGG" + 
				"AATAGAAACAGCTTGATGGGATTAAGAATCAACAAAAAGGTCATTATGGA" + 
				"T";
		assertEquals(1001,queryRes2.length());
		assertEquals(queryRes2,fasta.query("20", 1000000, 1001000));
		
		String queryRes3 = "ATCCATAATGACCTTTTTGTTGATTCTTAATCCCATCAAGCTGTTTCTAT" + 
				"TCCTACCACTGGGCCTTTGTACAACTGTTCTCTCTGCCTGAAATTCTCTT" + 
				"CTCCCAAGTTGTCAGGGAGCTCACTTCCTTCAGGCTTCTGCTCAAATGTC" + 
				"CCGTTTCAGAGACGACCTCCCTGACTACCATCCTGCCTTAGTTACTCCTG" + 
				"ACATCACTCCCCTGCTTTATTTTTTCTTTGCAGTACCTCACGTAAACTAC" + 
				"GTATTTATTTGTCTGTTGCTGCCATTCACAGCCAAAATGTGAGCTCCATG" + 
				"AAGCAAGACTTTGTCTAGCTCAAAGTCATATCTCAGTGCCTGGAATAGTG" + 
				"ATTGGTGGTTAGTAGGCTCTCAGCAACTGTCAAATAAATGGGTGGAGGGT" + 
				"GGATGGTGGATGGGAGGATGGGAGGATGAGTGAATGAAAGAAAGGGTGAG" + 
				"TGGTGGATAGGTGGGTAGATGGTGGATGGTTGGGACAGTGGATGATTGGA" + 
				"TGGGAGGGTGGGTGGAGGGTGGATGGTGGATGGGAGGATGGGAGGATGAG" + 
				"TGAATGAAAGAAAGGGTGAGTGGTGGATAGATGGGTAGATGGTGGATGGT" + 
				"TGGGACAGTGGATGATTGGATGGGAGGGTGGGTGGATGGTTGAATGGATG" + 
				"GATAGGAGGGTGGGGGTGAATAGCAGATGGGAAAATGAGTAGGTGATAGA" + 
				"CAGTAGATGGATGGATGGGAGGGTGGGTGAATATGTGGATGGGTAAATGA" + 
				"ATGGATGGGAAGGTCTGTGGGTGGTGGATTGGAGGGTTGATGGATGGGAG" + 
				"GATGGGTGAATGGAAATGTGGTTGGATGGTAGATGGGAGTGTAGGTACGT" + 
				"GGTGGATGGTAGATGGATGGAAGGTGGGTGAATGGGTGAATGCATGAATG" + 
				"GGAAGGTGGGTGGATGGGAGTATGGGTGTATGATGCGTGAGAGGATGGGT" + 
				"CAGTGGGTAGAGCAGAAAGAGCACTGGGTTCTTGTTCCAGTTCTCTCCCA" + 
				"C";
		
		assertEquals(1001,queryRes3.length());
		assertEquals(queryRes3,fasta.queryReverseComplement("20", 1000000, 1001000));
	}
	
	
	@Test
	public void readFASTAOutsideChr() throws IOException {

		String result1 = fasta.query("20", -1000, 1002396400);
		assertNotNull(result1);
	}
	
	@Test
	public void charset() {
		System.err.println(Charset.defaultCharset().name());
	}
	
	

	
	@Test
	public void testBuildKmer() {
		String input = "ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTC";
		
		List<KmerKey> kmers = KmerKey.buildKmers(3,input.getBytes(Charset.forName("UTF-8")));
		
		System.err.println(input.length());
		System.err.println(kmers.size());
		
		assertEquals("TTC", kmers.get(kmers.size() - 1).getKmerString());
		assertEquals("ATT", kmers.get(0).getKmerString());
		assertEquals(input.length() - 2, kmers.size());
	}
	
	@Test
	public void testFindMatchingKmers() {
		String input = "ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTC";
		
		//fasta.q
	}
	
	
	@Test
	public void testKmerKey() {
		String input1 = "ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTC";
		String input2 = "ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTC";
		String input3 = "ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTCC";
		
		
		KmerKey key1 = new KmerKey(input1);
		KmerKey key2 = new KmerKey(input2);
		KmerKey key3 = new KmerKey(input3);
		
		assertEquals(key1,key2);
		assertNotEquals(key2,key3);
		assertEquals(key1.hashCode(),key2.hashCode());
		assertNotEquals(key2.hashCode(),key3.hashCode());
	}
	
	@Test
	public void testKmerKey2() {
		String input1 = "CTT";
		KmerKey key1 = new KmerKey(input1);
		assertEquals(1 * 5 * 5 + 2 * 5 + 2,key1.hashCode());
		
		String input2 = "CATT";
		KmerKey key2 = new KmerKey(input2);
		assertEquals(1 * 5 * 5 * 5 + 2 * 5 + 2,key2.hashCode());
	}
	
	
	@Test
	public void testContainsUseIUPAC() {
		String test =   "AAGCAATGAGGGCAGACATGTTTATTTGAANAGGAGACAGCTACATTGAAATCACAAAAA";
		String source = "AAGCAATGAGGGCAGACATGTTTATTTGAAGAGGAGACAGCTACATTGAAATCACAAAAA";
		
		assertTrue(ReferenceFASTA.containsUseIUPAC(source, test));
		
		assertFalse(ReferenceFASTA.containsUseIUPAC(source, "AAGCAATGAGGGCAGACATTGTTTATTTG"));
		
		assertTrue(ReferenceFASTA.containsUseIUPAC(source,"AAGCAATGAGGGCAGACATGTTTATTTGAAN"));
		
		assertTrue(ReferenceFASTA.containsUseIUPAC(source,"GAGGGCAGACATGTTTAT"));
		
		assertTrue(ReferenceFASTA.containsUseIUPAC(source,"TGAAN"));
		
		assertFalse(ReferenceFASTA.containsUseIUPAC(source,"GTTTATTTGAT"));
		
		assertTrue(ReferenceFASTA.containsUseIUPAC(source,""));
	}
}
