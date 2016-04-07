package org.kidneyomics.VCFCommandLineTools;

import static org.junit.Assert.*;

import org.junit.Test;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class VariantContextComparatorTest {

	@Test
	public void test() {
		VariantContextComparator comparator = new VariantContextComparator();
		
		
		VariantContextBuilder builder = new VariantContextBuilder();
		
		builder.alleles("A","T");
		
		assertEquals(0,comparator.compare(builder.chr("chr1").start(100).stop(100).make(), builder.make()));
		
		
		assertEquals(1,comparator.compare(builder.chr("chr1").start(100).stop(100).make(), builder.start(99).stop(99).make()));
		
		assertEquals(-1,comparator.compare(builder.chr("chr1").start(100).stop(100).make(), builder.start(101).stop(101).make()));
		
		
		assertEquals(-1,comparator.compare(builder.chr("chr1").start(100).stop(100).make(), builder.chr("chrX").make()));
		
		assertEquals(-1,comparator.compare(builder.chr("chr22").start(100).stop(100).make(), builder.chr("chrX").make()));
		
		assertEquals(-1,comparator.compare(builder.chr("chr22").start(100).stop(100).make(), builder.chr("chrY").make()));
		
		assertEquals(-1,comparator.compare(builder.chr("chr22").start(100).stop(100).make(), builder.chr("chrMT").make()));
		
		assertEquals(1,comparator.compare(builder.chr("chrX").start(100).stop(100).make(), builder.chr("chr22").make()));
		
		assertEquals(1,comparator.compare(builder.chr("chrY").start(100).stop(100).make(), builder.chr("chr22").make()));
		
		assertEquals(1,comparator.compare(builder.chr("chrMT").start(100).stop(100).make(), builder.chr("chr22").make()));
		
		
		assertEquals(1,comparator.compare(builder.chr("chrX").start(100).stop(100).make(), builder.chr("chr1").make()));
		assertEquals(0,comparator.compare(builder.chr("chrX").start(100).stop(100).make(), builder.chr("chrX").make()));
		assertEquals(-1,comparator.compare(builder.chr("chrX").start(100).stop(100).make(), builder.chr("chrY").make()));
		assertEquals(-1,comparator.compare(builder.chr("chrX").start(100).stop(100).make(), builder.chr("chrMT").make()));
		
		assertEquals(1,comparator.compare(builder.chr("chrY").start(100).stop(100).make(), builder.chr("chr1").make()));
		assertEquals(1,comparator.compare(builder.chr("chrY").start(100).stop(100).make(), builder.chr("chrX").make()));
		assertEquals(0,comparator.compare(builder.chr("chrY").start(100).stop(100).make(), builder.chr("chrY").make()));
		assertEquals(-1,comparator.compare(builder.chr("chrY").start(100).stop(100).make(), builder.chr("chrMT").make()));
		
		assertEquals(1,comparator.compare(builder.chr("chrM").start(100).stop(100).make(), builder.chr("chr1").make()));
		assertEquals(1,comparator.compare(builder.chr("chrM").start(100).stop(100).make(), builder.chr("chrX").make()));
		assertEquals(1,comparator.compare(builder.chr("chrMT").start(100).stop(100).make(), builder.chr("chrY").make()));
		assertEquals(0,comparator.compare(builder.chr("chrMT").start(100).stop(100).make(), builder.chr("chrMT").make()));
		
	}

}
