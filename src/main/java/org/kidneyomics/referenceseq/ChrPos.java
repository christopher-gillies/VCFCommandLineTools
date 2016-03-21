package org.kidneyomics.referenceseq;

class ChrPos implements Comparable<ChrPos> {
	private final int pos;
	private final byte[] chr;
	private final int hashCode;
	
	public ChrPos(byte[] chr, int pos) {
		this.pos = pos;
		this.chr = chr;
		//collisions will be < 23 or so for different chromosomes
		hashCode = pos;
	}
	
	
	public ChrPos(String chr, int pos) {
		this.pos = pos;
		this.chr = chr.getBytes();
		//collisions will be < 23 or so for different chromosomes
		hashCode = pos;
	}
	
	public String chr() {
		return new String(chr);
	}
	
	public int pos() {
		return pos;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ChrPos)) {
			return false;
		}
		
		ChrPos cmp = (ChrPos) o;
		
		if(this.pos == cmp.pos && this.chr.equals(cmp.chr)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public int compareTo(ChrPos o) {
		/*
		 * Designed for within chr comparisons
		 */
		return Integer.compare(this.pos, o.pos);
	}
}

