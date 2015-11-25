package org.kidneyomics.VCFCommandLineTools;

import java.util.HashSet;

public class IterableHashSet<T> extends HashSet<T> implements Iterable<T> {


	public IterableHashSet() {
		super();
	}
	
	public IterableHashSet(int n) {
		super(n);
	}
}
