### Jonas Guzik ###
### v1.0 ###

"""
Generate artificial reads with specified length from a given fasta file.
"""

from argparse import ArgumentParser
from sys import exit
from string import whitespace
import textwrap

"""
Parse a fasta file as a generator that yields the individual blocks.
"""


def readFasta(file):
    header = None
    sequence = []
    for line in file:
        # remove all whitespace from the ends
        line = line.strip()
        if line.startswith(">"):
            # if the current line is a header, return the previous FASTA block in tuple form after concatenating the sequence lines (if there is a previous block)
            if header:
                yield (header, "".join(sequence))
            header = line
            sequence = []
        else:
            # if the current line is not a header, add the line to the list of sequence lines of the current FASTA block after removing all whitespace from it
            sequence.append(line.translate(str.maketrans("", "", whitespace)))
    if header:
        yield (header, "".join(sequence))


"""
Generate artificial reads with specified length from a given fasta file.
"""


def generate_artificial_reads(input_filepath, read_length, output_filepath):
    with open(input_filepath, "r") as input_file:
        with open(output_filepath, "w") as output_file:
            for header, sequence in readFasta(input_file):
                if len(sequence) <= 0:
                    continue

                # split sequence into chunks of size read_length. The last chunk may have a smaller size
                artificial_reads = textwrap.wrap(sequence, read_length)

                # limit is always at least 1 because artificial_reads contains the empty string if sequence is empty
                limit = len(artificial_reads)
                if len(artificial_reads[-1]) < 0.75 * read_length:
                    # if the last artificial read is smaller than 75% of the specified read length, it is not included in the resulting output file
                    limit = limit - 1

                for i in range(limit):
                    output_file.write(f"{header} {i}\n")
                    output_file.write(f"{artificial_reads[i]}\n")


def main():
    parser = ArgumentParser()
    parser.add_argument("filepath", help="path to the input fasta file")
    parser.add_argument(
        "-l",
        "--length",
        dest="length",
        type=int,
        required=True,
        help="length of an artificial read",
    )
    parser.add_argument(
        "-o",
        "--output",
        dest="output_file",
        default="./artificial_reads.fasta",
        help="path to the output file",
    )
    args = parser.parse_args()

    generate_artificial_reads(args.filepath, args.length, args.output_file)


main()
