package org.kidneyomics.referenceseq;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class KmerHashMapTest {

	@Test
	public void test() {
		KmerHashMap<ChrPos> map = new KmerHashMap<ChrPos>(3);
		
		assertEquals(125,map.size());
		
		char[] bases = { 'A', 'C', 'T', 'G', 'N' };
		
		List<String> kmers = new LinkedList<String>();
		
		for(int i = 0; i < 5; i++) {
			char[] kmer = new char[3];
			
			for(int j = 0; j < 5; j++) {
				
				for(int k = 0; k < 5; k++) {
					kmer[0] = bases[i];
					kmer[1] = bases[j];
					kmer[2] = bases[k];
					String kmerString = new String(kmer);
					kmers.add(kmerString);
				}
			}
		}
		
		assertEquals(125,kmers.size());
		
		for(String kmer : kmers) {
			KmerKey key = new KmerKey(kmer);
			System.err.println(key.getKmerString() + " " + key.hashCode());
			assertEquals(0,map.get(key).size());
		}
		
		for(String kmer : kmers) {
			KmerKey key = new KmerKey(kmer);
			map.putSingle(key, new ChrPos("20", key.hashCode()));
		}
		
		for(String kmer : kmers) {
			KmerKey key = new KmerKey(kmer);
			assertEquals(1,map.get(key).size());
		}
		
	}
	
	@Test
	public void test2() {
		//KmerHashMap<ChrPos> map = new KmerHashMap<ChrPos>(7);
		
		KmerKey key = new KmerKey("AATTCTA");
		System.err.println(key.hashCode());
		
		KmerKey key2 = new KmerKey("CTTTCAT");
		System.err.println(key2.hashCode());
		
		KmerKey key3 = new KmerKey("NNNNNNN");
		System.err.println(key3.hashCode());
		assertEquals(78124,key3.hashCode());
		
		KmerKey key4 = new KmerKey("TATAAGA");
		System.err.println(key4.hashCode());
		assertEquals(32515,key4.hashCode());
	}

}
