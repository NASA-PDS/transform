################################################################################
# coiss_data_to_pds4.py
#
# Sample program to read the contents of a Cassini ISS data file and save the
# data objects as unformatted binary files.
#
# Mark Showalter, PDS Rings Node, SETI Institute, 8/24/13
################################################################################

import sys
sys.dont_write_bytecode=True
import numpy as np
from vicar import VicarImage

def coiss_data_to_pds4(vicar_filepath, array_filepath,
                       prefix_filepath=None,
                       binary_header_filepath=None):

    """Reads a raw Cassini image file in VICAR format and saves its contents
    in one or more data files.

    Input:
        vicar_filepath      full file path to the raw Cassini image file.
        array_filepath      path to the output file for the data array.
        prefix_filepath     path to the output file for the prefix bytes of
                            the array; use None to suppress the writing of this
                            file.
        binary_header_filepath
                            path to the output file for the binary header; use
                            None to suppress the writing of this file.

    Return:                 A Python dictionary containing the VICAR keywords
                            and their values.

    Note: Output files are written using the same binary format and byte order
    as the original VICAR file.
    """

    vicar_object = VicarImage.from_file(vicar_filepath)

    vicar_object.data_2d.tofile(array_filepath)

    if prefix_filepath is not None:
        vicar_object.prefix_2d.tofile(prefix_filepath)

    if binary_header_filepath is not None:
        vicar_object.binary_header.tofile(binary_header_filepath)

    return vicar_object.as_dict()

if __name__ == "__main__":
    inputfile = sys.argv[1]
    outputfile = sys.argv[2]
    result = coiss_data_to_pds4(inputfile, outputfile)

################################################################################
