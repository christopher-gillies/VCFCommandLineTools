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
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --maxLd 0.2 --windowSizeKb 1000 --minAc 2 --excludeChr Y --excludeChr X --excludeChr MT
```


## Example filter by hwe across all population (remove if the excat test HWE p-value is less than 0.01)

```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --hwe 0.01 --minAc 0 
```

## Example filter by hwe across each population (remove if the excat test HWE p-value is less than 0.01 in any population)
### The population information is in a tab delimited infile, where the first row is a header line. The idCol option specifies the column of the sample id, and the popCol option specifies the population column
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz"
export ANCESTRY="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/omni_samples.20141118.panel"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --hwe 0.01 --minAc 0 --infile $ANCESTRY --idCol sample --popCol pop
```

## Example filter using R script string
### The --filterString option must return a R logical value. That is the last line of the script should be a logical value. Sites are kept if the result is TRUE otherwise it will filter the site out
### Variables available:
* chr -- The chromosome
* start -- The start position
* stop -- The stop position
* id -- The variant id
* ref -- The reference allele
* alt -- The first alternative allele
* qual -- The quality score
* filters -- The filter information for the variant
* info -- A list() object representing the info field (only those annotated in the VCF header). For example, if you have an AC info item it could be accessed info[['AC']] in the R script.
* samples -- a list() of sample ids
* gtInfo -- a list() of lists() one for each sample. This can be used to pull of genotype specific format fields. For example to get a list of genotypes from all samples you could write
```
gts = sapply(gtInfo,FUN=function(x){ x[['GT']] });
```
* Note that the GT field is encoded as 0 for HOM_REF, 1 For HET, and 2 HOM_ALT.

### This code will only print sites with exactly 11 heterozygotes
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --minAc 0 --filterString "gts = sapply(gtInfo,FUN=function(x){ x[['GT']] }); sum( gts == 1 ) == 11;"
```

### Combination of hwe filter and R filter and ld filter
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --minAc 0 --hwe 0.1 --maxLd 0.1 --filterString "gts = sapply(gtInfo,FUN=function(x){ x[['GT']] }); sum( gts == 1 ) == 11;"
```

### R filter using GCScore field from VCF. Only keep variants if the mean GCScore across all non-zero subjects is greater than 0.7
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Google Drive/1_7_2016_Megachip/chr20.merged.reports.rc.dict.sorted.filtered.1000G.vcf.gz"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --minAc 0 --filterString "gc = sapply(gtInfo,FUN=function(x){ x[['GCScore']] }); mean(gc,an.rm=T) > 0.7"
```

### R filter using GCScore field from VCF. Only keep variants if the mean GCScore is greater than the mean non-zero GTScore
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Google Drive/1_7_2016_Megachip/chr20.merged.reports.rc.dict.sorted.filtered.1000G.vcf.gz"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --minAc 0 --filterString "gc = sapply(gtInfo,FUN=function(x){ x[['GCScore']] }); gt = sapply(gtInfo,FUN=function(x){ x[['GTScore']] }); mean(gc,an.rm=T) > mean(gt[gt != 0],na.rm=T)"
```

### R filter using info field. Find the variant with IlluminaId == '20:10036280-GA-0_T_F_2299624158' and print the IlluminaId for every variant
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Google Drive/1_7_2016_Megachip/chr20.sites.vcf"
export OUT="/tmp/test.vcf.gz"
$vcfTools --command filter --outfile "$OUT" --vcf "$VCF1" --minAc 0 --filterString "print(info[['IlluminaId']]);info[['IlluminaId']] == '20:10036280-GA-0_T_F_2299624158'"
```


### Concordance
```
export vcfTools="java -jar /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/release/VCFCommandLineTools-0.0.1.jar"
export VCF1="/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz"

$vcfTools --command concordance --vcf "$VCF1" --vcf "$VCF1" --minAc 10 --sample HG00100 --sample HG00100


Truth VCF: /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz
Test VCF: /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/VCFCommandLineTools/src/test/resources/ALL.chip.omni_broad_sanger_combined.20140818.snps.genotypes.chr20.subset.vcf.gz
Truth Sample: HG00100
Test Sample: HG00100
True Positives: 16554
False Positives: 0
True Negatives: 12676
False Negatives: 0
Sensitivity: 1.0
Specificity: 1.0
False Discovery Rate: 0.0
```

## Help
```
$vcfTools --help            
       _  _  _                                         ___             
 \  / /  |_ /   _  ._ _  ._ _   _. ._   _| |  o ._   _  |  _   _  |  _ 
  \/  \_ |  \_ (_) | | | | | | (_| | | (_| |_ | | | (/_ | (_) (_) | _> 
                                                                       
usage: VCFCommandLineTools
    --command <command>             The command you would like to perform:
                                    findOverlap, selectSites,
                                    viewGenotypes, viewInfo,
                                    findOverlappingSamplesFromList,
                                    makeVcfFromManifest,
                                    makeVcfFromReports. findOverlap
                                    requires you to input at least two vcf
                                    files and the program will find the
                                    samples biallelic sites in both vcf
                                    files. selectSites will select
                                    biallelic sites from the file that you
                                    specify with format chr:pos:ref:alt
                                    for each variant. viewGenotypes will
                                    display the genotypes for sites of
                                    interest. viewInfo will display a
                                    variants information from info field.
                                    makeVcfFromManifest takes an input of
                                    a illumina manifest file and creates a
                                    vcf sites file for sites that have a
                                    reference allele. makeVcfFromReports
                                    creates a vcf from illumina standard
                                    report files. mergeVcfColumns merges
                                    two vcf files (only biallelic sites;
                                    no duplicate sample ids). filter --
                                    remove variants from vcf, can be used
                                    to ld-prune. concordance -- between two
                                    samples from different vcfs
    --excludeChr <excludeChr>       please specify a chr to exclude
    --filterString <filterString>   The filter string to appy to the
                                    filter command. Each variants
                                    genotypes will be put into a variable
                                    gtInfo. This is a list that acts much
                                    like a hash table. Any valid R code
                                    can be used, however, it MUST return a
                                    LOGICAL R value.
    --help                          Print the help message
    --hwe <hwe>                     The Hardy-Weinberg p-value threshold
                                    (Exact test); used in filtering
    --idCol <idCol>                 The identity column to pull out of the
                                    infile. Requires a header
    --infile <infile>               The file to read in
    --info <info>                   The info to select out of vcf
    --manifest <manifest>           please specify a illumina manifest
                                    file
    --maxLd <maxLd>                 The maximum allowable pairwise ld when
                                    filtering variants
    --minAc <minAc>                 The minimum allele count for a variant
                                    to be considered
    --nucleotide                    Show nucleotides instead of numeric
                                    genotype
    --outfile <outfile>             The file to write out to
    --popCol <popCol>               The population column to pull out of
                                    the infile. Requires a header
    --ref <ref>                     please specify a reference sequence
    --sample <sample>               specify a sample id
    --site <site>                   please specify a site
    --vcf <vcf>                     a vcf file
    --windowSizeKb <windowSizeKb>   The window size in kilobases when
                                    filtering variants

```

