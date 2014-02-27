// Copyright 2006-2013, by the California Institute of Technology.
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
package gov.nasa.pds.transform.product;

import gov.nasa.pds.transform.TransformException;
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
public class ProductTransformerFactory {
  /** Holds the factory object. */
  private static ProductTransformerFactory factory = null;

  /** Private constructor. */
  private ProductTransformerFactory() {}

  /** Gets an instance of the factory.
   *
   */
  public static synchronized ProductTransformerFactory getInstance() {
    if (factory == null) {
      factory = new ProductTransformerFactory();
    }
    return factory;
  }

  /**
   * Gets an instance of a Transformer. If the given label ends in ".xml",
   * then this will return a PDS4 Transformer object. Otherwise, a PDS3
   * Transformer object will be returned.
   *
   * @param target A PDS3 or PDS4 label file.
   * @param format The transformation format.
   *
   * @return The appropriate Transformer object.
   *
   * @throws TransformException If the input label could not be opened or
   * the format type is not one of the valid formats.
   */
  public ProductTransformer newInstance(File target, String format)
  throws TransformException {
    if (!target.exists()) {
      throw new TransformException("Target not found: " + target);
    }
    String extension = FilenameUtils.getExtension(target.toString());
    if (extension.equalsIgnoreCase("xml")) {
      if (Constants.PDS4_VALID_FORMATS.contains(format)) {
        if ("csv".equals(format)) {
          return new Pds4TableTransformer();
        } else if (Constants.STYLESHEETS.containsKey(format)) {
          return new StylesheetTransformer();
        } else {
          return new Pds4ImageTransformer();
        }
      } else {
        throw new TransformException("Format value '" + format
            + "' is not one of the valid formats for a PDS4 transformation: "
            + Constants.PDS4_VALID_FORMATS);
      }
    } else {
      if (Constants.PDS3_VALID_FORMATS.contains(format)) {
        if ("pds4-label".equals(format)) {
          return new Pds3LabelTransformer();
        } else {
          return new Pds3ImageTransformer();
        }
      } else {
        throw new TransformException("Format value '" + format
            + "' is not one of the valid formats for a PDS3 transformation: "
            + Constants.PDS3_VALID_FORMATS);
      }
    }
  }
}
