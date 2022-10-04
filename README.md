# Pangenome of cultivated beet and crop wild relatives

This repository contains scripts relevant for the investigation of the beet pangenome for:

### A) employing *k*-mer- and gene-based methods to resolve the parental relationships of the tetraploid wild beet *B. corolliflora*

and

### B) identifying regions not present in the cultivated sugar beet, but associated with traits with relevance for breeding.


------------------------------------

### A)

Different approaches were developed to investigate the parental relationships of the tetraploid wild beet *B. corolliflora*. The following scripts are provided in this repository:

- trio binning java application:
/polyploid_parental_relations_analyses/SBTTrio/out/artifacts/trio_binning/SBTTrio.jar

- k-mer fingerprinting java application:
/polyploid_parental_relations_analyses/SBTTrio/out/artifacts/fingerprinting/SBTTrio.jar

- generate artificial reads:
/polyploid_parental_relations_analyses/generate_artificial_reads.py

- artificial read mapping:
/polyploid_parental_relations_analyses/map_artificial_reads_distributed.py


### B) 

The script 'find_regions_of_interest.py' allows the identification of zero coverage regions as well as the identification of conserved regions in a species of interest.

As input, a tab separated file with chromosome/contig name (first column), position (second column), coverage at this position (third column) and variant (1) vs. no variant (0) at this position (fourth column) is required.

(Example line: chr1   1   0   0)


The coverage and variance thresholds allow the identification of regions with properties of interest. A 'max_cov' threshold of 0.01 means that the maximal allowed coverage in a window is 1% of the mean coverage over the whole chromosome/contig in a long read-based assembly.
For larger genomes > 1 Gbp it is recommended to split the input file into two smaller files.


Example command to identify zero coverage regions:
~~~
python3 identify_lost_and_conserved_regions/find_regions_of_interest.py  --output_dir /out/name/ --cov_var /in/coverage_and_variants.txt --mode lost --assembly_type long --max_cov 0.01 --var 0 --window_size 2500 --shift 150
~~~

The identified regions (extended_regions.txt) can then be investigated for overlapping genes.
