package org.kidneyomics.VCFCommandLineTools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class FilterableVariantContextReader implements Iterable<VariantContext>, AutoCloseable, Iterator<VariantContext> {

	private final int QUEUE_SIZE = 10000;
	private final File vcf;
	private VariantContextLdCalculator ldCalc = new VariantContextLdCalculator();
	private final Queue<VariantContext> queue;
	private final Iterator<VariantContext> iter;
	private final VCFFileReader reader;
	private final Logger logger = LoggerFactory.getLogger(FilterableVariantContextReader.class);;
	private int windowSizeBp = 1;
	private double maxLd = 1;
	private final LinkedList<VariantContextFilter> filters = new LinkedList<>();
	private final VCFHeader header;
	private int variantsRead = 0;
	private int variantsKept = 0;
	
	
	public int numberOfFilters() {
		return this.filters.size();
	}
	
	public int getVariantsRead() {
		return variantsRead;
	}

	public int getVariantsKept() {
		return variantsKept;
	}
	
	public double getFractionVariantsKept() {
		return variantsKept / ((double ) variantsRead);
	}

	private VariantContext nextReturnVariantContext = null;
	
	private FilterableVariantContextReader(File vcf) {
		this.vcf = vcf;
		this.queue = new LinkedList<VariantContext>();
		this.reader = new VCFFileReader(this.vcf,false);
		this.iter = this.reader.iterator();
		this.header = reader.getFileHeader();
	}
	
	public VCFHeader getFileHeader() {
		return this.header;
	}
	
	
	private FilterableVariantContextReader(Iterable<VariantContext> iterable) {
		this.vcf = null;
		this.queue = new LinkedList<VariantContext>();
		this.reader = null;
		this.iter = iterable.iterator();
		this.header = null;
	}
	
	public static FilterableVariantContextReader createByIterableContextsAndFilters(Iterable<VariantContext> iterable, Collection<VariantContextFilter> filters) {
		return FilterableVariantContextReader.createByIterableContextsAndFiltersAndLdAndWindow(iterable, filters, 1, 1);
	}
	
	public static FilterableVariantContextReader createByIterableContextsAndFiltersAndLdAndWindow(Iterable<VariantContext> iterable, Collection<VariantContextFilter> filters, double maxLd, int windowSizeBp) {
		FilterableVariantContextReader reader = new FilterableVariantContextReader(iterable);
		if(filters != null) {
			reader.filters.addAll(filters);
		}
		reader.maxLd = maxLd;
		reader.windowSizeBp = windowSizeBp;
		reader.setNextReturnVariantContext();
		return reader;
	}
	
	
	public static FilterableVariantContextReader createByIterableContextsAndFiltersAndLdAndWindowAndLDCALC(Iterable<VariantContext> iterable, Collection<VariantContextFilter> filters, double maxLd, int windowSizeBp, VariantContextLdCalculator calc) {
		FilterableVariantContextReader reader = new FilterableVariantContextReader(iterable);
		if(filters != null) {
			reader.filters.addAll(filters);
		}
		reader.maxLd = maxLd;
		reader.windowSizeBp = windowSizeBp;
		reader.ldCalc = calc;
		reader.setNextReturnVariantContext();
		return reader;
	}
	
	public static FilterableVariantContextReader createByAppliationOptions(ApplicationOptions applicationOptions) {
		return FilterableVariantContextReader.createByAppliationOptionsWithVCFIndex(applicationOptions, 0);
	}
	
	public static FilterableVariantContextReader createByAppliationOptionsWithVCFIndex(ApplicationOptions applicationOptions, int vcfNumber) {
		
		
		/**
		 * TODO: Make this work like the filter command does so this component is reusable
		 */
		
		if(vcfNumber >= applicationOptions.getVcfs().size()) {
			throw new RuntimeException("Error not enough vcfs");
		}
		
		//r^2
		double maxLd = applicationOptions.getMaxLd();
		File vcf = applicationOptions.getVcfs().get(vcfNumber);
		int windowSizeKb = applicationOptions.getWindowSizeKb();
		int windowSizeBp = windowSizeKb * 1000;
		int minAc = applicationOptions.getMinAc();
		double hwe = applicationOptions.getHwe();
		String infile = applicationOptions.getInFile();
		String popCol = applicationOptions.getPopCol();
		String idCol = applicationOptions.getIdCol();
		String filterString = applicationOptions.getFilterString();
		boolean snpsOnly = applicationOptions.isSnpsOnly();

		
		
		FilterableVariantContextReader reader = new FilterableVariantContextReader(vcf);
		List<String> chrsToExclude = applicationOptions.getChrsToExclude();
		
		reader.logger.info("Options in effect");
		reader.logger.info("vcf: " + vcf.getAbsolutePath());
		reader.logger.info("maxLd: " + maxLd);
		reader.logger.info("windowSizeKb: " + windowSizeKb);
		
		reader.maxLd = maxLd;
		reader.windowSizeBp = windowSizeBp;
		
		if(snpsOnly) {
			reader.filters.add(new SNPsOnlyFilter());
		}
		
		//check to see if we should add minAc filter
		if(minAc > 0) {
			reader.logger.info("minAc: " + minAc);
			reader.filters.add(new MinACVariantContextFilter(minAc));
		}
		
		//check to see if we should add exclude chr filter
		if(chrsToExclude.size() > 0) {
			for(String chr : chrsToExclude) {
				reader.logger.info("Excluding chr: " + chr);
				reader.filters.add(new ExcludeChrVariantContextFilter(chr));
			}
		}
		
		//check to see if we should add hwe filter
		if(hwe != -1.0) {
			if(infile != null && popCol != null && idCol != null) {
				
				//read population map
				try {
					DelimitedFileParser parser = new DelimitedFileParser();
					List<Map<String,String>> pedData = parser.parseFile(new File(infile));
					ListMapToMapConverter mapConverter = new ListMapToMapConverter(idCol, popCol);
					Map<String,String> popMap = mapConverter.convert(pedData);
					reader.logger.info("Population specific HWE test");
					reader.logger.info("idCol: " + idCol);
					reader.logger.info("popCol: " + popCol);
					reader.logger.info("infile: " + infile);
					reader.filters.add( new HWEVariantContextFilter(hwe,popMap));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				
				reader.filters.add( new HWEVariantContextFilter(hwe));
			}
			reader.logger.info("hwe threshold: " + hwe);
			

		}
		
		
		//add filterString filter
		if(!StringUtils.isEmpty(filterString) ) {
		    ScriptEngineManager manager = new ScriptEngineManager();
		    // create a Renjin engine:
		    ScriptEngine engine = manager.getEngineByName("Renjin");
		    // check if the engine has loaded correctly:
		    if(engine == null) {
		    	reader.reader.close();
		        throw new RuntimeException("Renjin Script Engine not found on the classpath.");
		    } else {
		    	reader.filters.add(new FilterStringVariantContextFilter(engine, filterString, reader.header));
		    }
		}

		
		reader.setNextReturnVariantContext();
		return reader;
	}
	
	@Override
	public boolean hasNext() {
		return this.nextReturnVariantContext != null;
	}

	
	private void setNextReturnVariantContext() {
		
		
		//keep trying to get a variant while we have variants in the vcf of queue
		while((queue.peek() != null || iter.hasNext()) && this.nextReturnVariantContext == null) {
			
			//pad the queue
			while(iter.hasNext() && queue.size() < QUEUE_SIZE) {
				VariantContext next = iter.next();
				queue.add(next);
				variantsRead++;
				if(variantsRead % 10000 == 0) {
					logger.info(variantsRead + " variants read");
				}
				
				//get start of queue;
				VariantContext head = queue.peek();
				VariantContext tail = next;
				
				if(!head.getContig().equals(tail.getContig()) || tail.getStart() - head.getStart() > windowSizeBp) {
					//if the next variant is from a different chromosome or if the distance is greater than the window size, then don't add any more to the queue
					break;
				}
			}
			
			//get the next filtered variant
			this.nextReturnVariantContext = applyFilters(queue,maxLd,windowSizeBp);
			
			if(this.nextReturnVariantContext != null) {
				//logger.info(VariantSiteKey.create(this.nextReturnVariantContext).toString());
				//since this.nextReturnVariantContext != null we will not loop again
				//write the lead marker to file
				variantsKept++;
				if(variantsKept % 10000 == 0) {
					logger.info(variantsKept + " variants kept");
				}
			} 
			
		}
		
	}
	
	@Override
	public VariantContext next() {
		VariantContext ret = this.nextReturnVariantContext;
		//clear to prevent infinite loop
		this.nextReturnVariantContext = null;
		this.setNextReturnVariantContext();
		return ret;
	}

	@Override
	public void remove() {

	}

	@Override
	public void close() throws Exception {
		if(this.reader != null) {
			reader.close();
		}
	}

	@Override
	public Iterator<VariantContext> iterator() {
		return this;
	}
	
	VariantContext applyFilters(Queue<VariantContext> queue, double maxLd, int windowSizeBp) {
		
		//apply single variant filters
		VariantContext vc = queue.peek();
		if(vc == null) {
			return null;
		}
		
		//apply single filters
		for(VariantContextFilter filter : filters) {
			if(!filter.keep(vc)) {
				//remove this variant
				queue.poll();
				return null;
			}
		}
		
		//apply ld filter
		if(maxLd == 1.0) {
			//no need to filter subsequent markers
			vc = queue.poll();
		} else {
			vc = filterQueueLd(queue,maxLd,windowSizeBp);
		}
		return vc;
	}
	
	/**
	 * 
	 * 
	 * @param queue -- queue that is sorted in genomic coordinate order
	 * @param maxLd -- maximum allowable ld between the lead snp at other markers
	 * @param windowSizeKb -- the maximum window size in kilobases
	 * @return returns the lead marker that is popped off of the queue, and removes all markers whose LD is greater than maxLd
	 */
	VariantContext filterQueueLd(Queue<VariantContext> queue, double maxLd, int windowSizeBp) {
		VariantContext vc1 = queue.poll();
		if(vc1 == null) {
			return null;
		}
		
		Iterator<VariantContext> queueIter = queue.iterator();
		while(queueIter.hasNext()) {
			VariantContext vc2 = queueIter.next();
			if(!vc1.getContig().equals(vc2.getContig()) || vc2.getStart() - vc1.getStart() > windowSizeBp) {
				break;
			}
			
			double r2 = ldCalc.pearsonR2(vc1, vc2);
			//logger.info("r2: " + r2);
			
			if(r2 > maxLd) {
				queueIter.remove();
			}
		}
		
		
		return vc1;
		
	}

}
