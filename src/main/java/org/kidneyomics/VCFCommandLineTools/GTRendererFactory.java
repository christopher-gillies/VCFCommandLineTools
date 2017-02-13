package org.kidneyomics.VCFCommandLineTools;

public class GTRendererFactory {
	enum GT_RENDER_TYPE {
		NUMERIC,
		NUCLEOTIDE,
		CUSTOM
	}
	public static GTRenderer getGTRenderer(GT_RENDER_TYPE type) throws IllegalArgumentException {
		switch(type) {
		case NUMERIC:
			return new GTNumericRenderer();
		case NUCLEOTIDE:
			return new GTNucleotideRenderer();
		case CUSTOM:
			return new GTCustomRenderer();
		default:
			throw new IllegalArgumentException("Unknown GT Renderer");
		}
	}
	
	public static void setCustomKey(GTRenderer gtRenderer, String key) {
		if(gtRenderer.getClass() == GTCustomRenderer.class) {
			((GTCustomRenderer) gtRenderer).setKey(key);
		}
	}
}
