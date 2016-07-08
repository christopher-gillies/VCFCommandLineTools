package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

@Component
public class ConcordanceCommand implements RunCommand {

	Logger logger;
	ApplicationOptions applicationOptions;

	@Autowired
	public ConcordanceCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}

	private ConcordanceResult lastResult = null;
	
	@Override
	public void runCommand() {

		try {
			List<String> samples = applicationOptions.getSamples();

			assert samples.size() == 2;
			
			String truthSample = samples.get(0);
			String testSample = samples.get(1);
			

			
			if(StringUtils.isEmpty(truthSample) || StringUtils.isEmpty(testSample)) {
				throw new RuntimeException("Please specify both a truth and test sample");
			}
			
			List<File> vcfs = applicationOptions.getVcfs();

			assert vcfs.size() == 2;

			File truthVcf = vcfs.get(0);
			File testVcf = vcfs.get(1);

			ConcordanceCalculator calculator = ConcordanceCalculator.create(truthSample, testSample);

			// Store truth variants for truthSample
			FilterableVariantContextReader truthReader = FilterableVariantContextReader
					.createByAppliationOptionsWithVCFIndex(applicationOptions, 0);

			if (!truthReader.getFileHeader().getSampleNameToOffset().containsKey(truthSample)) {

				truthReader.close();

				throw new RuntimeException("Error " + truthSample + " not in " + truthVcf.getAbsolutePath());
			}

			for (VariantContext vc : truthReader) {
				calculator.addTruthSampleVariant(vc);
			}

			truthReader.close();

			// Store test variants for testSample
			FilterableVariantContextReader testReader = FilterableVariantContextReader
					.createByAppliationOptionsWithVCFIndex(applicationOptions, 1);
			if (!testReader.getFileHeader().getSampleNameToOffset().containsKey(testSample)) {

				testReader.close();

				throw new RuntimeException("Error " + testSample + " not in " + testVcf.getAbsolutePath());
			}
			for (VariantContext vc : testReader) {
				calculator.addTestSampleVariant(vc);
			}

			testReader.close();

			ConcordanceResult res = calculator.computeConcordance();
			lastResult = res;
			System.out.println("Truth VCF: " + truthVcf.getAbsolutePath());
			System.out.println("Test VCF: " + testVcf.getAbsolutePath());
			System.out.println(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ConcordanceResult getLastResult() {
		return this.lastResult;
	}

}
