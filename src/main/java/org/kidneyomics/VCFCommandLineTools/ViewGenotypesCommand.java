package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

@Component
public class ViewGenotypesCommand implements RunCommand {

	
	Logger logger;
	ApplicationOptions applicationOptions;
	
	@Autowired
	public ViewGenotypesCommand(LoggerService loggerService, ApplicationOptions applicationOptions) {
		this.logger = loggerService.getLogger(this);
		this.applicationOptions = applicationOptions;
	}
	
	@Override
	public void runCommand() {
		File vcf = applicationOptions.getVcfs().get(0);
		
		GTRenderer gtRenderer = GTRendererFactory.getGTRenderer(applicationOptions.getGtRendererType());
		
		VCFFileReader reader = new VCFFileReader(vcf);
		
		Set<String> sampleIds = null;
		HashMap<String,List<Genotype>> genotypesPerSample = new HashMap<String,List<Genotype>>();
		
		List<String> variantKeys = new LinkedList<String>();
		
		for(String site : applicationOptions.getSites()) {
			ChromosomePositionInterval cpi = new ChromosomePositionInterval(site);
			CloseableIterator<VariantContext> iter;
			if(cpi.isInterval()) {
				iter = reader.query(cpi.getChromosome(), cpi.getStartPostion(), cpi.getEndPostion());
			} else {
				iter = reader.query(cpi.getChromosome(), cpi.getStartPostion(), cpi.getStartPostion());
			}
			
			while(iter.hasNext()) {
				VariantContext vc = iter.next();
				String varianKey = VariantKeyRenderer.render(vc);
				variantKeys.add(varianKey);
				GenotypesContext gc = vc.getGenotypes();
				if(sampleIds == null) {
					sampleIds = gc.getSampleNames();
					for(String sampleId : sampleIds) {
						genotypesPerSample.put(sampleId, new LinkedList<Genotype>());
					}
				}
				
				for(String sampleId : sampleIds) {
					genotypesPerSample.get(sampleId).add(  gc.get(sampleId)  );
				}
				
			}
			iter.close();
						
		}
		
		if(sampleIds == null) {
			sampleIds = new HashSet<String>();
			logger.warn("No site found");
		}
		
		
		//Write out table
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		
		//Writer header
		sb.append("SAMPLE_ID\t");
		Iterator<String> variantKeyIter = variantKeys.iterator();
		while(variantKeyIter.hasNext()) {
			String site = variantKeyIter.next();
			sb.append(site);
			sb.append("\t");
		}
		
		
		sb.append("\n");
		
		Iterator<String> sampleIter = sampleIds.iterator();
		while(sampleIter.hasNext()) {
			String sampleId = sampleIter.next();
			List<Genotype> gts = genotypesPerSample.get(sampleId);
			sb.append(sampleId);
			sb.append("\t");
			for(Genotype gt : gts) {

				sb.append(gtRenderer.render(gt));
				sb.append("\t");
				
			}
			
			sb.append("\n");
		}
		
		reader.close();
		
		System.out.print(sb.toString());
	}	

	

}
