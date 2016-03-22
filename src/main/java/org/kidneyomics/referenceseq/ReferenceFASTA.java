package org.kidneyomics.referenceseq;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.util.SequenceUtil;
import htsjdk.samtools.util.StringUtil;

public class ReferenceFASTA {

	private ReferenceFASTA() {
		kmerSize = 7;
		kmerMap = new KmerHashMap<ChrPos>(kmerSize);
	}
	
	private final int kmerSize;
	
	private Map<String, ReferenceSequence> chromosomes = new HashMap<>();
	private KmerHashMap<ChrPos> kmerMap;
	private boolean kmerMapBuilt = false;
	
	public static ReferenceFASTA create(File file) {
		FastaSequenceFile fasta = new FastaSequenceFile(file, true);
		
		ReferenceFASTA ref = new ReferenceFASTA();
		
		
		ReferenceSequence next = fasta.nextSequence();
		while(next != null) {
			ref.chromosomes.put(next.getName(),next);
			
			
			next = fasta.nextSequence();
		}
				
		fasta.close();
		
		return ref;
		
	}
	
	
	
	/**
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 * @param reverse complement
	 * @return the plus reference sequence between these positions
	 */
	public String query(String chr, int start, int end, boolean reverseComplement) {
		ReferenceSequence seq = chromosomes.get(chr);
		
		if(seq == null) {
			seq = chromosomes.get("chr" + chr);
		}
		
		if(seq == null) {
			throw new IllegalArgumentException("No chr found for " + chr);
		}
		
		if(start > end) {
			throw new IllegalArgumentException("Start cannot be greater than end");
		}
		
		byte[] res = new byte[end - start + 1];
		
		byte[] all = seq.getBases();
		
		//include the end position (end - 1)
		int index = 0;
		for(int i = start - 1; i < end; i++) {
			res[index++] = all[i];
		}
		
		
		if(!reverseComplement) {
			return new String(res, Charset.forName("UTF-8"));
		} else {
			SequenceUtil.reverseComplement(res);
			return new String(res, Charset.forName("UTF-8"));
		}
 	}
	
	/**
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 * @return the plus reference sequence between these positions
	 */
	public String query(String chr, int start, int end) {
		return query(chr,start,end,false);
	}
	
	/**
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 * @return the minus reference sequence between these positions
	 */
	public String queryReverseComplement(String chr, int start, int end) {
		return query(chr,start,end,true);
	}
	
	public void buildKmerIndex() {
		
		for(Map.Entry<String, ReferenceSequence> entry : chromosomes.entrySet()) {
			String chr = entry.getKey();
			ReferenceSequence seq = entry.getValue();
			
			byte[] bases = seq.getBases();
			byte[] chrBytes = chr.getBytes();
			for(int i = 0; i < bases.length - kmerSize + 1; i++) {
				int hashCode = KmerKey.calcHashcode(bases,i, kmerSize);
				if( i % 100000 == 0) {
					System.err.println("working on kmer " + i + " of " + bases.length);
					//KmerKey key = new KmerKey(bases,i,kmerSize);
					//System.err.println(key.getKmerString() + " " + key.hashCode() + " " + hashCode);
				}
				
				
				
				
				ChrPos kmerLocation = new ChrPos(chrBytes,i);
				
				if(kmerMap.containsHashCode(hashCode)) {
					Set<ChrPos> kmerLocations = kmerMap.getByHashCode(hashCode);
					kmerLocations.add(kmerLocation);
				} else {
					throw new IllegalStateException("Error all keys should be in the perfect hash");
				}
			}
			
		}
		
		kmerMapBuilt = true;
	}
	
	/**
	 * 
	 * @param chr
	 * @return return the number of k-mers for chr or -1 if none exists
	 */
	int getKmerMapSize(String chr) {
		if(kmerMap.containsKey(chr)) {
			return kmerMap.get(chr).size();
		} else {
			return -1;
		}
	}
	
	/**
	 * 
	 * @param chr
	 * @return return the length of the chromosome or -1 if none exists
	 */
	int getBasesPer(String chr) {
		if(chromosomes.containsKey(chr)) {
			return chromosomes.get(chr).length();
		} else { 
			return -1;
		}
	}
	
	Map<String,List<ChrPos>> findMatchingKmersReverseComplement(String query) {
		return findMatchingKmers(SequenceUtil.reverseComplement(query));
	}
	
	Map<String,List<ChrPos>> findMatchingKmers(String query) {
		
		if(this.kmerMapBuilt == false) {
			throw new IllegalStateException("build kmer map");
		}
		
		Map<String,List<ChrPos>> res = new HashMap<String,List<ChrPos>>();
		
		List<KmerKey> kmerKeys = KmerKey.buildKmers(kmerSize, query.getBytes(Charset.forName("UTF-8")));
		
		//kmer map:
		// kmerkey --> set<chr:pos>
		
		System.err.println("Number of kmers " + kmerKeys.size());
		
		//for each kmer key in the query find all matching coordinates
		for(KmerKey key : kmerKeys) {
			if(this.kmerMap.containsKey(key)) {
				Set<ChrPos> positions = kmerMap.get(key);
				System.err.println("Matches found for: " + key.getKmerString() + " " + positions.size());
				for(ChrPos chrpos : positions) {
					//add to list for chr
					
					//res:
					//chr -> list<chrpos>
					String chr = chrpos.chr();
					if(res.containsKey(chr)) {
						res.get(chr).add(chrpos);
					} else {
						List<ChrPos> valsForChr = new LinkedList<>();
						res.put(chr, valsForChr);
						valsForChr.add(chrpos);
					}
				}
			}
		}
		
		//sort results per chromosome
		for(Map.Entry<String, List<ChrPos>> entry : res.entrySet() ) {
			Collections.sort(entry.getValue());
		}
		
		System.err.println("Finished searching");
		
		return res;
	}	
}
