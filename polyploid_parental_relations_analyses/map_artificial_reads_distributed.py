### Jonas Guzik ###
### v1.0 ###

"""
Map artificial reads against a combined reference and
count the number of reads in each of the four categories:
1. maps exclusively to Blom
2. maps exclusively to Bmrh
3. maps to both
4. maps to neither
"""

import mappy as mp
import multiprocessing
import csv
import psutil
import itertools
import time
from argparse import ArgumentParser
from sys import exit

# mapping index
index = None
# minimal identity needed to consider a mapping
threshold = 0

"""
During the concatenation of the assemblies into a combined reference,
the contig names need to be edited such that there is a prefix common to
all contigs of Blom and a different prefix common to all contigs of
Bmrh.
"""
# each contig in the assembly of Blom has ">bl_tig" as a prefix in its name
prefix_Blom = "bl_tig"
# each contig in the assembly of Bmrh has ">bm_tig" as a prefix in its name
prefix_Bmrh = "bm_tig"


def init(aligner, matches):
    global index
    global threshold
    index = aligner
    threshold = matches


"""
Map a given read against the combined reference and check whether it
maps exclusively to Blom, exclusively to Bmrh, neither of both.
"""


def perform_mapping(input_read):
    global index
    global threshold
    assert index is not None

    input_sequence = input_read[1]

    # count number of primary mappings for both Blom and Bmrh
    # this allows for more fine grained thresholds
    # in the sense of when you consider a mapping exclusive to either species
    primary_mappings_to_Blom = 0
    primary_mappings_to_Bmrh = 0
    length = len(input_sequence)

    for mapping in index.map(input_sequence):
        # only check primary mappings where num_matches/query_length >= threshold, i.e. identity >= threshold
        if mapping.is_primary and (mapping.mlen / length) >= threshold:
            # test whether the reference sequence of the mapping is from Blom or Bmrh
            if mapping.ctg.startswith(prefix_Blom):
                primary_mappings_to_Blom += 1

            if mapping.ctg.startswith(prefix_Bmrh):
                primary_mappings_to_Bmrh += 1

    if primary_mappings_to_Blom == 0 and primary_mappings_to_Bmrh == 0:
        return "None"
    elif primary_mappings_to_Blom > 0 and primary_mappings_to_Bmrh == 0:
        return "Blom"
    elif primary_mappings_to_Blom == 0 and primary_mappings_to_Bmrh > 0:
        return "Bmrh"
    else:
        return "Both"


"""
Output the number of reads in each of the four categories:
1. maps exclusively to Blom
2. maps exclusively to Bmrh
3. maps to both
4. maps to neither
"""


def write_output(output_file, counts):
    try:
        with open(output_file, "w") as output_file:
            writer = csv.writer(output_file)
            fields = ["Category", "Count"]

            writer.writerow(fields)
            for row in counts.items():
                writer.writerow(row)
    except:
        print("Could not open/create or write to specified output file.")
        exit(1)


def main():
    parser = ArgumentParser()
    parser.add_argument("reference_filepath", help="path to the reference fasta file")
    parser.add_argument("query_filepath", help="path to the query fasta/fastq file")
    parser.add_argument(
        "-t",
        "--threads",
        dest="num_threads",
        type=int,
        default=4,
        help="number of threads to use during indexing",
    )
    parser.add_argument(
        "-p",
        "--processes",
        dest="num_processes",
        type=int,
        default=4,
        help="number of processes to use during mapping",
    )
    parser.add_argument(
        "-m",
        "--matches",
        dest="threshold",
        type=float,
        default=0.6,
        help="threshold for considering mappings. To be considered, a mapping must have matches/query_length >= threshold",
    )
    parser.add_argument(
        "-o",
        "--output",
        dest="output_file",
        default="./artificial_read_mapping_results.csv",
        help="path to the output file",
    )
    args = parser.parse_args()

    timeStarted = time.time()

    print("Creating index")
    aligner = mp.Aligner(
        fn_idx_in=args.reference_filepath, n_threads=args.num_threads, preset="map-pb"
    )
    if not aligner:
        raise Exception(
            f"ERROR: failed to load/build index file with reference {args.reference_filepath}"
        )

    print("The size of the index in mb is roughly:")
    print(psutil.Process().memory_info().rss / (1024 * 1024))

    counts = {"None": 0, "Blom": 0, "Bmrh": 0, "Both": 0}

    print("Mapping reads")
    with multiprocessing.Pool(
        args.num_processes, init, [aligner, args.threshold]
    ) as pool:
        for result in pool.imap_unordered(
            perform_mapping, mp.fastx_read(args.query_filepath)
        ):
            counts[result] += 1

    print(counts)

    print("Writing output")
    write_output(args.output_file, counts)

    timeFinished = time.time()
    print(f"Computation took {(timeFinished - timeStarted)/60} minutes")


if __name__ == "__main__":
    main()
