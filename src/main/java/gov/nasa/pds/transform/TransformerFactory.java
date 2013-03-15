// Copyright 2006-2012, by the California Institute of Technology.
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
package gov.nasa.pds.transform;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

/**
 * Transformer Factory class that determines whether to transform a
 * PDS3 or PDS4 product.
 *
 * @author mcayanan
 *
 */
public class TransformerFactory {
  /** Holds the factory object. */
  private static TransformerFactory factory = null;

  /** Private constructor. */
  private TransformerFactory() {}

  /** Gets an instance of the factory.
   *
   */
  public static synchronized TransformerFactory getInstance() {
    if (factory == null) {
      factory = new TransformerFactory();
    }
    return factory;
  }

  /**
   * Gets an instance of a Transformer.
   *
   * @param label A PDS3 or PDS4 label. Filename must end in 'xml' or 'lbl'.
   *
   * @return The appropriate Transformer object.
   *
   * @throws TransformException If the label filename does not end in 'xml'
   *  or 'lbl', case-insensitive.
   */
  public PdsTransformer newInstance(File label) throws TransformException {
    if (!label.exists()) {
      throw new TransformException("File not found: " + label);
    }
    String extension = FilenameUtils.getExtension(label.toString());
    if (extension.equalsIgnoreCase("xml")) {
      return new Pds4Transformer();
    } else if (extension.equalsIgnoreCase("lbl") || (extension.equalsIgnoreCase("img"))) {
      return new Pds3Transformer();
    } else {
      throw new TransformException(
          "Target does not appear to be a PDS3 or PDS4 label.");
    }
  }
}
