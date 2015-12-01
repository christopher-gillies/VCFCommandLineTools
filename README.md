# VCFCommandLineTools

## Example view genotypes

```bash
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export vcf="~/Documents/workspace-sts-3.6.1.RELEASE/vsearchDataDirectory/reheader.neptune.all.snp.sorted.all.indels.eff.dbsnp.b138.esp.1kgp1.dbnsfp.2.5.hgmd.LowQC.0.7.targeted.vcf.gz"
$vcfTools --vcf $vcf --command viewGenotypes --site 17:48156000-48157000  --site 17:48158680
``` 
Tab separated table that is easier to view than vcf format
| SAMPLE_ID | 	17:48156449:A:G |	17:48156557:G:A	 | 17:48158680:A:G |
|-----------|:-----------------:|:------------------:|:---------------:|	
| SAMPLE1	| 0                 |        	0	     |            0    |	
| SAMPLE2	| 0                 |        	1	     |            0    |		
| SAMPLE3	| 0                 |        	0	     |            2    |	
| SAMPLE4	| 0                 |        	0	     |            0    |	