package org.kidneyomics.VCFCommandLineTools;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.kidneyomics.stats.GenotypeToListVectorConverter;
import org.kidneyomics.stats.MapToListVectorConverter;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Logical;
import org.renjin.sexp.LogicalVector;
import org.springframework.util.StringUtils;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

public class FilterStringVariantContextFilter implements VariantContextFilter {
 
	private final ScriptEngine engine;
	private final String filterString;
	
	private GenotypeToListVectorConverter gtConverter;
	private MapToListVectorConverter infoConverter;
	
	
	public FilterStringVariantContextFilter(ScriptEngine engine, String filterString, VCFHeader header) {
		
		if(header != null) {
			gtConverter = new GenotypeToListVectorConverter(header);
			infoConverter = new MapToListVectorConverter(header);
		} else {
			throw new IllegalArgumentException("header cannot be null");
		}
		
		
		
		if(engine == null) {
			throw new IllegalArgumentException("Error engine cannot be null");
		}
		
		this.engine = engine;
		
		if(StringUtils.isEmpty(filterString)) {
			throw new IllegalArgumentException("filterString cannot be empty");
		}
		
		this.filterString = filterString;
	}
	
	@Override
	public boolean keep(VariantContext vc) {
		
		ListVector gtInfo = gtConverter.convert(vc.getGenotypes());
		ListVector infoField = infoConverter.convert(vc.getAttributes());
		//put basic info
		engine.put("chr", vc.getContig());
		engine.put("start", vc.getStart());
		engine.put("stop", vc.getEnd());
		engine.put("ref", vc.getReference().toString().replace("*", ""));
		//TODO: make this work with multiple alleles
		//TODO: just convert it to a list of strings
		engine.put("alt", vc.getAlternateAllele(0).toString());
		engine.put("qual", vc.getPhredScaledQual());
		engine.put("filters", vc.getFilters());
		engine.put("info", infoField);
		
		engine.put("samples", vc.getSampleNames());
		//put genotype variable into R environment
		engine.put("gtInfo", gtInfo);
		try {
			LogicalVector result = (LogicalVector) engine.eval(filterString);
			//What should we do about these?
			return result.asLogical() == Logical.TRUE;
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
