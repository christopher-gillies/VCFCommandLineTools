package org.kidneyomics.VCFCommandLineTools;
import htsjdk.variant.variantcontext.Genotype;

public class GTCustomRenderer implements GTRenderer {

	private String key = null;
	
	public GTCustomRenderer() {
		key = "GT";
	}
	
	public void setKey(String key) {
		if(key != null) {
			this.key = key;
		}
	}
	
	@Override
	public String render(Genotype genotype) {
		Object attr = genotype.getAnyAttribute(key);
		if(attr != null) {
			return attr.toString();
		} else {
			return ".";
		}
		
	}

}
