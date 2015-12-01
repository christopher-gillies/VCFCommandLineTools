package org.kidneyomics.VCFCommandLineTools;

public class GTRendererFactory {
	enum GT_RENDER_TYPE {
		NUMERIC,
		NUCLEOTIDE
	}
	public static GTRenderer getGTRenderer(GT_RENDER_TYPE type) throws IllegalArgumentException {
		switch(type) {
		case NUMERIC:
			return new GTNumericRenderer();
		case NUCLEOTIDE:
			return new GTNucleotideRenderer();
		default:
			throw new IllegalArgumentException("Unknown GT Renderer");
		}
	}
}
