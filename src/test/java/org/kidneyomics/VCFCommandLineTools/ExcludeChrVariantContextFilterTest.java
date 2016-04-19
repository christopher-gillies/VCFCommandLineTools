package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import org.junit.Test;

import htsjdk.variant.variantcontext.VariantContextBuilder;

public class ExcludeChrVariantContextFilterTest {

	@Test
	public void test() {
		ExcludeChrVariantContextFilter filter = new ExcludeChrVariantContextFilter("chrX");
		
		VariantContextBuilder builder = new VariantContextBuilder();
		builder.alleles("A","T");
		assertEquals(false,filter.keep(builder.chr("X").make()));
		assertEquals(false,filter.keep(builder.chr("chrx").make()));
		assertEquals(true,filter.keep(builder.chr("Y").make()));
		assertEquals(true,filter.keep(builder.chr("1").make()));
	}

}
