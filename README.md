# VCFCommandLineTools

##Download

```
wget -O VCFCommandLineTools-1.0.0.jar https://github.com/christopher-gillies/VCFCommandLineTools/blob/master/release/VCFCommandLineTools-0.0.1.jar?raw=true
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

## Example select out allele frequency, allele count adn allele number from VCF

This examples requires that you specify an input file with a list of variants. In this file, each line has the following format: "chr:pos:ref:alt". There should be no header line.
The output option is optional.
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF=/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/FluidigmReferences/ExAC.r0.3.sites.vep.prefix.decomposed.normalized.pass.vcf.gz
export variants=/Users/cgillies/Google\ Drive/Fluidigm_Pipeline/PAPER_ANALYSIS/exome_chip_sensitivity/singleton_variants.txt
export out=/Users/cgillies/Google\ Drive/Fluidigm_Pipeline/PAPER_ANALYSIS/exome_chip_sensitivity/allele_frequency.txt
$vcfTools --command viewInfo --vcf $VCF --info EXAC_AC_Adj --info EXAC_AN_Adj -info EXAC_AF --infile "$variants" --outfile "$out"
```

## Example make vcf from illumina manifest file

```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export REF="/Users/cgillies/Google Drive/1_7_2016_Megachip/chr20.fa.gz"
export MANIFEST="/Users/cgillies/Google Drive/1_7_2016_Megachip/MEGA_Consortium_15063755_B2.chr20.csv"
export OUT="/Users/cgillies/Google Drive/1_7_2016_Megachip/chr20.sites.vcf"
$vcfTools --command makeVcfFromManifest --outfile "$OUT" --ref "$REF" --manifest "$MANIFEST"
```

## Example make vcf from Illumina standard report files

```

export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export REF="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/20.fa.gz"
export MANIFEST="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/manifest.test.chr20.csv"
export OUT="/Users/cgillies/Google Drive/1_7_2016_Megachip/gt.vcf.gz"
export IN="/Users/cgillies/Google Drive/1_7_2016_Megachip/sampleList.txt"

vi "$IN"
HG00100	/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/HG00100.chr20.txt
HG00138	/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/HG00138.chr20.txt
HG00160	/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/HG00160.chr20.txt


$vcfTools --command makeVcfFromReports --outfile "$OUT" --ref "$REF" --manifest "$MANIFEST" --infile "$IN"
```


## Example merge biallelic vcf sites

```
export vcfTools="java -jar /home/cgillies/programs/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/home/cgillies/programs/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.relabel.vcf.gz"
export VCF2="/home/cgillies/programs/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz"
export OUT="/tmp/test.vcf.gz"

$vcfTools --command mergeVcfColumns --outfile "$OUT" --vcf "$VCF1" --vcf "$VCF2"
```


## Example ld-prune (Note: this is setup for unphased markers and LD is estimated with Pearson correlation)

```
export vcfTools="java -jar /home/cgillies/programs/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/home/cgillies/programs/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.relabel.vcf.gz"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --maxLd 0.2 --windowSizeKb 1000 --minAc 2
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

