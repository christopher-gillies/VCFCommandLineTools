package org.kidneyomics.illumina.array;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.reference.FastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

public class ReferenceFASTA {

	private ReferenceFASTA() {
		
	}
	
	private Map<String, ReferenceSequence> chromosomes = new HashMap<>();
	
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
	
	
	
	public String query(String chr, int start, int end) {
		ReferenceSequence seq = chromosomes.get(chr);
		
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
		
		return new String(res);
 	}
	
	
	
	
}
