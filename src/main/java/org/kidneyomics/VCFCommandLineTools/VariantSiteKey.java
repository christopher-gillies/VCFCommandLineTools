package org.kidneyomics.VCFCommandLineTools;

import java.util.LinkedList;
import java.util.List;

import org.renjin.invoke.reflection.converters.BooleanArrayConverter;

import htsjdk.variant.variantcontext.VariantContext;

public class VariantSiteKey {

	private final String chr;
	private final int pos;
	private final String ref;
	private final String alt;
	private final String key;
	
	private VariantSiteKey(String chr, int  pos, String ref, String alt) {
		this.chr = chr;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
		this.key = VariantKeyRenderer.render(chr, pos, ref, alt);
	}
	
	public static VariantSiteKey create(String chr, int  pos, String ref, String alt) {
		return new VariantSiteKey(chr,  pos, ref, alt);
	}
	
	public static VariantSiteKey create(String variantKey) {
		String vals[] = variantKey.split(":");
		if(vals.length != 4) {
			throw new IllegalArgumentException("Format should be chr:pos:ref:alt " + variantKey);
		}
		
		VariantSiteKey result = new VariantSiteKey(vals[0], Integer.parseInt(vals[1]), vals[2], vals[3]);
		
		return result;	
	}
	
	public static List<VariantSiteKey> create(List<String> variantKeys) {
		LinkedList<VariantSiteKey> result = new LinkedList<>();
		
		for(String key : variantKeys) {
			result.add(create(key));
		}
		
		return result;
	}
	
	public static VariantSiteKey create(VariantContext vc) {
		return create(VariantKeyRenderer.render(vc));
	}
	
	public boolean matchesKey(String variantKey) {
		return this.key.equals(variantKey);
	}
	
	public boolean matchesKey(VariantContext vc) {
		return this.key.equals(VariantKeyRenderer.render(vc));
	}

	public String getChr() {
		return chr;
	}

	public int getPos() {
		return pos;
	}

	public String getRef() {
		return ref;
	}

	public String getAlt() {
		return alt;
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return this.key;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof VariantSiteKey) {
			VariantSiteKey other = (VariantSiteKey) o;
			return this.getKey().equals(other.getKey());
		} else {
			return false;
		}
		
	}
	
	@Override
	public int hashCode() {
		return 17 * this.getKey().hashCode();
	}
	
	
}
