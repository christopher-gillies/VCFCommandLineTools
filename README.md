# VCFCommandLineTools

##Download

```
wget -O rna-seq-pipeline-1.0.0.jar https://github.com/christopher-gillies/VCFCommandLineTools/blob/master/release/VCFCommandLineTools-0.0.1.jar?raw=true
```

## Run on windows
* Install java
* Download the latest jar file from release folder of this repository

```
set vcftools="c:\Users\cgillies\Downloads\VCFCommandLineTools-0.0.1.jar"
set vcf="Z:\path\all.chr.snp.indel.phased.vcf.gz"
java -jar %vcftools% --vcf %vcf% --command viewGenotypes --site 14:106337470 --site 14:106341031 --site 14:106341805
java -jar %vcftools% --vcf %vcf% --command viewGenotypes --site 14:106337470 --site 14:106341031 --site 14:106341805 --nucleotide
```

## Example view genotypes

```bash
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export vcf=~/Documents/workspace-sts-3.6.1.RELEASE/vsearchDataDirectory/reheader.neptune.all.snp.sorted.all.indels.eff.dbsnp.b138.esp.1kgp1.dbnsfp.2.5.hgmd.LowQC.0.7.targeted.vcf.gz
$vcfTools --vcf $vcf --command viewGenotypes --site 17:48156000-48157000  --site 17:48158680


SAMPLE_ID	17:48156449:A:G	17:48156557:G:A	17:48158680:A:G	
SAMPLE_1	0	0	0	
SAMPLE_2	0	0	0	
SAMPLE_3	0	0	0	
SAMPLE_4	0	0	0



$vcfTools --vcf $vcf --command viewGenotypes --site 17:48156000-48157000  --site 17:48158680 --nucleotide

SAMPLE_ID	17:48156449:A:G	17:48156557:G:A	17:48158680:A:G	
SAMPLE_1	A/A	G/G	A/A	
SAMPLE_2	A/A	G/G	A/A	
SAMPLE_3	A/A	G/G	A/A	
SAMPLE_4	A/A	G/G	A/A	
``` 


## Example find overlapping samples from list

```
export vcfTools="java -jar /home/cgillies/programs/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF=/kidneyomics/1000G_Phase3/ALL.chr1.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz
$vcfTools --command findOverlappingSamplesFromList --vcf $VCF --infile /kidneyomics/NEPTUNE_FLUIDIGM/1000G.samples.ids --outfile /kidneyomics/NEPTUNE_FLUIDIGM/1000G.samples.ids.phase3.overlap.txt
```

```
cat /kidneyomics/NEPTUNE_FLUIDIGM/1000G.samples.ids.phase3.overlap.txt
HG00100
HG00138
HG00160
HG00233
HG00246
...
```

## Help
```
$vcfTools --help
       _  _  _                                         ___             
 \  / /  |_ /   _  ._ _  ._ _   _. ._   _| |  o ._   _  |  _   _  |  _ 
  \/  \_ |  \_ (_) | | | | | | (_| | | (_| |_ | | | (/_ | (_) (_) | _> 
                                                                       
usage: VCFCommandLineTools
    --command <command>   The command you would like to perform:
                          findOverlap, selectSites, viewGenotypes
    --help                Print the help message
    --infile <infile>     The to read in
    --minAc <minAc>       The minimum allele count for a variant to be
                          considered
    --nucleotide          Show nucleotides instead of numeric genotype
    --outfile <outfile>   The file to write out to
    --site <site>         please specify a site
    --vcf <vcf>           a vcf file

```

