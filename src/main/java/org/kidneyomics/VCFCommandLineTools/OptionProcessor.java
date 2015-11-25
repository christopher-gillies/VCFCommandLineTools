package org.kidneyomics.VCFCommandLineTools;

import org.apache.commons.cli.ParseException;

public interface OptionProcessor {
	void processInputs(String[] args) throws ParseException;
}
