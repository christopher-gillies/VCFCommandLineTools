package org.kidneyomics.referenceseq;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class KmerKey {
	
	public KmerKey(byte[] data, int start, int size) {
		
		this.originalSequence = data;
		
		
		this.start = start;
		this.size = size;
		this.hashCode = calcHashcode(this.originalSequence,start,size);
	}
	
	public static int calcHashcode(byte[] sequence, int start, int size) {
		int tmp = 0;
		for(int i = 0; i < size; i++) {
			
			int index = i + start;
			switch(sequence[index]) {
			case 'a':
			case 'A':
				tmp = 5 * tmp + 0;
				break;
			case 'c':
			case 'C':
				tmp = 5 * tmp + 1;
				break;
			case 't':
			case 'T':
				tmp = 5 * tmp + 2;
				break;
			case 'g':
			case 'G':
				tmp = 5 * tmp + 3;
				break;
			case 'n':
			case 'N':
				tmp = 5 * tmp + 4;
				break;
			default:
				throw new IllegalStateException("invalid byte " + sequence[index]);
			}
			
		}
		return tmp;
	}
	
	public KmerKey(byte[] data) {
		
		this.originalSequence = data;
		this.start = 0;
		this.size = data.length;
		this.hashCode = calcHashcode(this.originalSequence,start,size);
	}
	
	public KmerKey(String data) {
		this.originalSequence = data.getBytes(Charset.forName("UTF-8"));
		this.start = 0;
		this.size = data.length();
		this.hashCode = calcHashcode(this.originalSequence,start,size);
	}
	

	
	private final byte[] originalSequence;
	//inclusive
	private final int start;

	private final int size;
	
	private final int hashCode;
	
	public int getSize() {
		return size;
	}
	
	public byte[] getOriginalSequence() {
		return originalSequence;
	}
	
	public byte[] getKmerBytes() {
		byte[] res = new byte[size];
		int index = 0;
		for(int i = start; i < start + size; i++) {
			res[index++] = this.originalSequence[i];
		}
		return res;
	}
	
	public String getKmerString() {
		return new String(getKmerBytes(),Charset.forName("UTF-8"));
	}
	
	public String getOriginalSequenceString() {
		return new String(originalSequence,Charset.forName("UTF-8"));
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof KmerKey)) {
			return false;
		}
		
		
		KmerKey other = (KmerKey) o;
		
		if(other.size == this.size) {
			
			for(int i = 0; i < this.size; i++) {
				if(this.originalSequence[this.start + i] != other.originalSequence[other.start + i]) {
					return false;
				}
			}
			
			return true;
			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public static List<KmerKey> buildKmers(int kmerSize, byte[] bases) {
	//	System.err.println("Total bases: " + bases.length);
		List<KmerKey> result = new LinkedList<KmerKey>();
		for(int i = 0; i < bases.length - kmerSize + 1; i++) {
//			if( i % 10000 == 0) {
//				System.err.println("working on kmer " + i + " of " + bases.length);
//			}
			result.add(new KmerKey(bases,i,kmerSize));	
		}
		//System.err.println("Finished!");
		return result;
	}
}
