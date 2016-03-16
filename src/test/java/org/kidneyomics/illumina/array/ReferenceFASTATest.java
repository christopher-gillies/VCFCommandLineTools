package org.kidneyomics.illumina.array;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ReferenceFASTATest {

	@Test
	public void readFASTA1() throws IOException {
		ClassPathResource resource = new ClassPathResource("20.fa.gz");
		
		ReferenceFASTA fasta = ReferenceFASTA.create(resource.getFile());
		
		
		String result1 = fasta.query("20", 10023846, 10023964);
		
		assertEquals("TGTCAACAATTCTACCTATGAAGGAAAGCCAATATTCCTTAGAGCTTGTGAAGATGCACATGATGTTAAAGATGTGTGCCTGACATTTTTGGAAAAAGGAGCCAATCCTAATGCAATCA",result1);
		
		
		
		String result2 = fasta.query("20", 10024981, 10025099);
		
		assertEquals("ATTCTGCTACTTAATATGATGGGTATAAACAGCATCTCATATTGGACTATTTCATGTCCAGTCCACAGGCCGCACAGCTTTAATGGAAGCGTCAAGAGAAGGGGTAGTGGAAATAGTTC",result2);
	}
}
