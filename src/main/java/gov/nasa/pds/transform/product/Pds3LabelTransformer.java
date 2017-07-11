// Copyright 2006-2017, by the California Institute of Technology.
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
import java.util.ArrayList;
import java.util.List;

/**
 * Class to support transformations given a PDS3 label.
 *
 * @author mcayanan
 *
 */
public class Pds3LabelTransformer extends DefaultTransformer {

  private List<String> includePaths;
  
  /**
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public Pds3LabelTransformer(boolean overwrite) {
    super(overwrite);
    includePaths = new ArrayList<String>();
  }

  @Override
  public File transform(File target, File outputDir, String format)
      throws TransformException {
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Transforming label file: " + target, target));
    File outputFile = Utility.createOutputFile(target, outputDir, format);
    if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
      return outputFile;
    }
    try {
      Utility.generate(target, outputFile, "generic-pds3_to_pds4.vm", includePaths);
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

  @Override
  public File transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    return transform(target, outputDir, format);
  }

  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    List<File> outputs = new ArrayList<File>();
    outputs.add(transform(target, outputDir, format));
    return outputs;
  }

  /**
   * Set the paths to search for files referenced by pointers.
   * <p>
   * Default is to always look first in the same directory
   * as the label, then search specified directories.
   * @param i List of paths
   */
  public void setIncludePaths(List<String> i) {
    this.includePaths = new ArrayList<String> (i);
    while(this.includePaths.remove(""));
  }
}
