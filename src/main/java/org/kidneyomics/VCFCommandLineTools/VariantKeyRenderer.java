package org.kidneyomics.VCFCommandLineTools;

import org.stringtemplate.v4.ST;

import htsjdk.variant.variantcontext.VariantContext;

public class VariantKeyRenderer {

	private VariantKeyRenderer() {
		
	}
	
	private static ST template = new ST("<chr>:<start>:<ref>:<alt>");
	
	
	public static String render(String chr, int start, String ref, String alt) {
		template.remove("chr");
		template.remove("start");
		template.remove("ref");
		template.remove("alt");
		template.add("chr", chr);
		template.add("start", start);
		template.add("ref", ref);
		template.add("alt", alt);
		return template.render();
	}
	
	public static String render(VariantContext context) {
		return render(context.getContig(),context.getStart(),context.getReference().getBaseString(),context.getAlternateAllele(0).getBaseString());
	}
}
