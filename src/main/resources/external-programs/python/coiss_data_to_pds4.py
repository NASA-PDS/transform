# encoding: utf-8
#
# Copyright © 2019, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# • Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
# • Redistributions must reproduce the above copyright notice, this list of
#   conditions and the following disclaimer in the documentation and/or other
#   materials provided with the distribution.
# • Neither the name of Caltech nor its operating division, the Jet Propulsion
#   Laboratory, nor the names of its contributors may be used to endorse or
#   promote products derived from this software without specific prior written
#   permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
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
