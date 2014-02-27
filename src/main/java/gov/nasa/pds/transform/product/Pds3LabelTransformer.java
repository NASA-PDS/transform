// Copyright 2006-2014, by the California Institute of Technology.
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
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.util.logging.Logger;

/**
 * Class to support transformations given a PDS3 label.
 *
 * @author mcayanan
 *
 */
public class Pds3LabelTransformer extends DefaultTransformer {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      Pds3LabelTransformer.class.getName());

  @Override
  public File transform(File target, File outputDir, String format)
      throws TransformException {
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Transforming label file: " + target, target));
    File outputFile = Utility.createOutputFile(target, outputDir, format);
    try {
      Utility.generate(target, outputFile, "generic-pds3_to_pds4.vm");
    } catch (Exception e) {
      e.printStackTrace();
      throw new TransformException("Error occurred while generating "
          + "PDS4 label: " + e.getMessage());
    }
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Successfully transformed PDS3 label '" + target
        + "' to a PDS4 label '" + outputFile + "'", target));
    return outputFile;
  }

}
