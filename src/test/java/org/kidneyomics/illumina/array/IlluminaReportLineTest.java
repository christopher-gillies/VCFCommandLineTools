package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.kidneyomics.referenceseq.ReferenceFASTA;
import org.springframework.core.io.ClassPathResource;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFEncoder;
import htsjdk.variant.vcf.VCFHeader;


public class IlluminaReportLineTest {

	static ReferenceFASTA fasta;
	static {
		ClassPathResource resource = new ClassPathResource("20.fa.gz");
	
		try {
			fasta = ReferenceFASTA.create(resource.getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {

		
		HashMap<String,String> data = new HashMap<>();
		data.put("IlmnID", "20:10393722-GAGTACTACTAA-G-0_P_F_2304231134");
		data.put("Name", "20:10393722-GAGTACTACTAA-G");
		data.put("IlmnStrand", "MINUS");
		data.put("SNP", "[I/D]");
		data.put("Chr", "20");
		data.put("MapInfo", "10393722");
		data.put("TopGenomicSeq", "GAGTCTTTGCATCAGTTATCTCAAGTCTGAGACCTGTGGTTGTCGAATCCCAGTGGACT[-/TTAGTAGTACT]CAGATCCTCCTTTGTTTGGTGCGTAGTATATTAACAAGTAAACCTGCCTGTATGCTCACC");
		
		IlluminaManifestMarker marker = IlluminaManifestMarker.create(data, fasta);
		VariantContext vc = marker.toVariantContext();
		
		// 1
		HashMap<String,String> gtInfo1 = new HashMap<>();
		
		//HET
		gtInfo1.put("SNP Name", "20:10393722-GAGTACTACTAA-G");
		gtInfo1.put("Sample ID", "HG00100");
		gtInfo1.put("Allele1 - Top", marker.getRefAlleleTop());
		gtInfo1.put("Allele2 - Top", marker.getAltAlleleTop());
		gtInfo1.put("GC Score", "0.89");
		gtInfo1.put("GT Score", "0.86");
		gtInfo1.put("R", "0.97");
		gtInfo1.put("Theta", "0.39");
		
		gtInfo1.put("X", "0.99");
		gtInfo1.put("Y", "0.1");
		gtInfo1.put("X Raw", "8167");
		gtInfo1.put("Y Raw", "289");
		gtInfo1.put("B Allele Freq", "0.4999");
		gtInfo1.put("Log R Ratio", "-0.223");
		gtInfo1.put("SNP", "[I/D]");
		gtInfo1.put("ILMN Strand", "MINUS");
		
		IlluminaReportLine rl1 = IlluminaReportLine.create(gtInfo1);
		
		Genotype gt1 = rl1.getGenotype(vc,marker);
		
		assertTrue(gt1.isHet());
		
		// 2
		HashMap<String,String> gtInfo2 = new HashMap<>();
		
		//HOM REF
		gtInfo2.put("SNP Name", "20:10393722-GAGTACTACTAA-G");
		gtInfo2.put("Sample ID", "HG00101");
		gtInfo2.put("Allele1 - Top", marker.getRefAlleleTop());
		gtInfo2.put("Allele2 - Top", marker.getRefAlleleTop());
		gtInfo2.put("GC Score", "0.89");
		gtInfo2.put("GT Score", "0.86");
		gtInfo2.put("R", "0.97");
		gtInfo2.put("Theta", "0.39");
		
		gtInfo2.put("X", "0.99");
		gtInfo2.put("Y", "0.1");
		gtInfo2.put("X Raw", "8167");
		gtInfo2.put("Y Raw", "289");
		gtInfo2.put("B Allele Freq", "0.4999");
		gtInfo2.put("Log R Ratio", "-0.223");
		gtInfo2.put("SNP", "[I/D]");
		gtInfo2.put("ILMN Strand", "MINUS");
		
		IlluminaReportLine rl2 = IlluminaReportLine.create(gtInfo2);
		
		Genotype gt2 = rl2.getGenotype(vc,marker);
		
		assertTrue(gt2.isHomRef());
		
		//3
		
		HashMap<String,String> gtInfo3 = new HashMap<>();
		
		//HOM REF
		gtInfo3.put("SNP Name", "20:10393722-GAGTACTACTAA-G");
		gtInfo3.put("Sample ID", "HG00102");
		gtInfo3.put("Allele1 - Top", marker.getAltAlleleTop());
		gtInfo3.put("Allele2 - Top", marker.getAltAlleleTop());
		gtInfo3.put("GC Score", "0.89");
		gtInfo3.put("GT Score", "0.86");
		gtInfo3.put("R", "0.97");
		gtInfo3.put("Theta", "0.39");
		
		gtInfo3.put("X", "0.99");
		gtInfo3.put("Y", "0.1");
		gtInfo3.put("X Raw", "8167");
		gtInfo3.put("Y Raw", "289");
		gtInfo3.put("B Allele Freq", "0.4999");
		gtInfo3.put("Log R Ratio", "-0.223");
		gtInfo3.put("SNP", "[I/D]");
		gtInfo3.put("ILMN Strand", "MINUS");
		
		IlluminaReportLine rl3 = IlluminaReportLine.create(gtInfo3);
		
		Genotype gt3 = rl3.getGenotype(vc,marker);
		
		assertTrue(gt3.isHomVar());
		
		//add genotypes to variant context
		
		VariantContext withGenotypes = new VariantContextBuilder(vc).genotypes(gt1,gt2,gt3).make();
		
		assertTrue(withGenotypes.hasGenotypes());
		
		List<String> ids = new LinkedList<String>();
		
		ids.add("HG00100");
		ids.add("HG00101");
		ids.add("HG00102");
		
		//create encoder with ids
		VCFEncoder encoder = IlluminaManifestMarker.encoder(ids);
		
		String encoding = encoder.encode(withGenotypes);
		
		String[] cols = encoding.split("\t");
		
		assertEquals(12,cols.length);
		
		assertTrue(cols[9].startsWith("0/1"));
		assertTrue(cols[10].startsWith("0/0"));
		assertTrue(cols[11].startsWith("1/1"));
		
		System.err.println(encoding);
		
		
	}

}
