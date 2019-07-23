// Copyright © 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
// 
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// • Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
// • Redistributions must reproduce the above copyright notice, this list of
//   conditions and the following disclaimer in the documentation and/or other
//   materials provided with the distribution.
// • Neither the name of Caltech nor its operating division, the Jet Propulsion
//   Laboratory, nor the names of its contributors may be used to endorse or
//   promote products derived from this software without specific prior written
//   permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.transform.util;

import gov.nasa.pds.transform.TransformException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jpl.mipl.io.jConvertIIO;

/**
 * Wrapper class to the Transcoder in the VICAR IO library.
 *
 * @author mcayanan
 *
 */
public class Transcoder {

  /**
   * Constructor.
   *
   */
  public Transcoder() {}

  /**
   * Transcode the given input.
   *
   * @param input The input file.
   * @param output The output file.
   * @param format The format of the resulting transformation.
   *
   * @throws TransformException If an error occurred transcoding
   * the input file.
   */
  public void transcode(File input, File output, String format)
      throws TransformException {
    transcode(input, output, format, 0, true);
  }

  /**
   * Transcode the given input.
   *
   * @param input The input file.
   * @param output The output file.
   * @param format The format of the resulting transformation.
   * @param index The index of the image within the input file.
   * @param readAsRenderedImage 'true' to read the input as a rendered
   * image, 'false' otherwise.
   *
   * @throws TransformException If an error occurred transcoding
   * the input file.
   */
  public void transcode(File input, File output, String format,
      int index, boolean readAsRenderedImage) throws TransformException {
    List<String> args = new ArrayList<String>();
    args.add("INP=" + input.toString());
    args.add("OUT=" + output.toString());
    if("jp2".equalsIgnoreCase(format)) {
      args.add("FORMAT=jpeg2000");
    } else {
      args.add("FORMAT=" + format);
    }
    if (readAsRenderedImage) {
      args.add("RI");
    }
    if (index != 0) {
      args.add("IMAGE_INDEX=" + index);
    }
    args.add("OFORM=BYTE");
    try {
      System.getProperties().setProperty(
          "javax.xml.parsers.DocumentBuilderFactory",
          "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
      System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
          "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
      jConvertIIO.main(args.toArray(new String[0]));
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
  }
}
