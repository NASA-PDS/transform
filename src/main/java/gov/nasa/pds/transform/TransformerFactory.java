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

import gov.nasa.pds.transform.constants.Constants;

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
   * Gets an instance of a Transformer. If the given label ends in ".xml",
   * then this will return a PDS4 Transformer object. Otherwise, a PDS3
   * Transformer object will be returned.
   *
   * @param label A PDS3 or PDS4 label.
   * @param formatType The transformation format.
   *
   * @return The appropriate Transformer object.
   *
   * @throws TransformException If the input label could not be opened or
   * the format type is not one of the valid formats.
   */
  public PdsTransformer newInstance(File label, String formatType)
  throws TransformException {
    if (!label.exists()) {
      throw new TransformException("File not found: " + label);
    }
    String extension = FilenameUtils.getExtension(label.toString());
    if (extension.equalsIgnoreCase("xml")) {
      if (Constants.PDS4_VALID_FORMATS.contains(formatType)) {
        return new Pds4Transformer();
      } else {
        throw new TransformException("Format type '" + formatType
            + "' is not one of the valid formats for a PDS4 transformation: "
            + Constants.PDS4_VALID_FORMATS);
      }
    } else {
      if (Constants.PDS3_VALID_FORMATS.contains(formatType)) {
        return new Pds3Transformer();
      } else {
        throw new TransformException("Format type '" + formatType
            + "' is not one of the valid formats for a PDS3 transformation: "
            + Constants.PDS3_VALID_FORMATS);
      }
    }
  }
}
