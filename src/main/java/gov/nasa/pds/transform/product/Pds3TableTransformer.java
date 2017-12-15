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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URISyntaxException;

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

/**
 * Class that supports PDS3 table transformations.
 * 
 * @author mcayanan
 *
 */
public class Pds3TableTransformer extends DefaultTransformer {
  
  /** A PDS3 to PDS4 label transformer */
  private Pds3LabelTransformer labelTransformer;
  
  /** A transformer that converts a PDS4 table to CSV */
  private Pds4TableTransformer tableTransformer;
  
  /** A list of include paths to find label fragments. */
  private List<String> includePaths;
  
  public Pds3TableTransformer(boolean overwrite) {
    super(overwrite);
    labelTransformer = new Pds3LabelTransformer(overwrite);
    tableTransformer = new Pds4TableTransformer(overwrite);
    includePaths = new ArrayList<String>();
  }
  
  @Override
  public File transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    File pds4Label = toPds4Label(target, outputDir);
    File outputFile = null;
    try {
      tableTransformer.setDataFileBasePath(target.getParentFile().toURI().toURL().toString());
      outputFile = tableTransformer.transform(pds4Label, outputDir, format, 
          dataFile, index);
    } catch (Exception te) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, 
          "Error occurred while transforming table: " + te.getMessage(),
          pds4Label));
      throw new TransformException("Unsuccessful table transformation. "
          + "Check transformed PDS4 label for possible errors.");
    }
    return outputFile;
  }
  
  @Override
  public File transform(URL url, File outputDir, String format,
      String dataFile, int index) throws TransformException, URISyntaxException, Exception {
    File outputFile = null;  
    File pds4Label = toPds4Label(url, outputDir);
    try {
      tableTransformer.setDataFileBasePath(Utility.getParent(url).toString());
      outputFile = tableTransformer.transform(pds4Label, outputDir, format, 
          dataFile, index);
    } catch (TransformException te) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, 
          "Error occurred while transforming table: " + te.getMessage(),
          pds4Label));
      throw new TransformException("Unsuccessful table transformation. "
          + "Check transformed PDS4 label for possible errors.");
    }
    return outputFile;
  }

  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    File pds4Label = toPds4Label(target, outputDir);
    List<File> outputFiles = new ArrayList<File>();
    try {
      tableTransformer.setDataFileBasePath(target.getParentFile().toURI().toURL().toString());
      outputFiles = tableTransformer.transformAll(pds4Label, outputDir, format);
    } catch (Exception te) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, 
          "Error occurred while transforming table: " + te.getMessage(),
          pds4Label));
      throw new TransformException("Unsuccessful table transformation. "
          + "Check transformed PDS4 label for possible errors.");
    }
    return outputFiles;
  }
  
  @Override
  public List<File> transformAll(URL url, File outputDir, String format)
      throws TransformException, URISyntaxException, Exception {
    File pds4Label = toPds4Label(url, outputDir);
    List<File> outputFiles = new ArrayList<File>();
    try {
      tableTransformer.setDataFileBasePath(Utility.getParent(url).toString());
      outputFiles = tableTransformer.transformAll(pds4Label, outputDir, format);
    } catch (TransformException te) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, 
          "Error occurred while transforming table: " + te.getMessage(),
          pds4Label));
      throw new TransformException("Unsuccessful table transformation. "
          + "Check transformed PDS4 label for possible errors.");
    }
    return outputFiles;
  }

  /**
   * Transforms a PDS3 to a PDS4 label.
   * 
   * @param pds3Label The PDS3 label to transform.
   * @param outputDir The output directory to place the PDS4 label.
   * 
   * @return The PDS4 label.
   * 
   * @throws TransformException If an error occurred while transforming
   *  the label.
   */
  private File toPds4Label(File pds3Label, File outputDir)
      throws TransformException {
    File pds4Label = null;
    try {
      pds4Label = labelTransformer.transform(pds3Label, outputDir, 
          "pds4-label");
    } catch (TransformException te) {
      throw new TransformException(
          "Error occurred while transforming to a pds4 label: "
              + te.getMessage());
    }
    return pds4Label;
  }
  
  private File toPds4Label(URL pds3Label, File outputDir)
		  throws TransformException, URISyntaxException, Exception {
	  File pds4Label = null;
	  try {
		  pds4Label = labelTransformer.transform(pds3Label, outputDir, 
				  "pds4-label");
	  } catch (TransformException te) {
		  throw new TransformException(
				  "Error occurred while transforming to a pds4 label: "
						  + te.getMessage());
	  }
	  return pds4Label;
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
    labelTransformer.setIncludePaths(includePaths);
  }
  
}
