// Copyright 2006-2015, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
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
