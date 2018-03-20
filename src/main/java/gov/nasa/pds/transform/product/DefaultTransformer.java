// Copyright 2006-2018, by the California Institute of Technology.
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
import gov.nasa.pds.transform.TransformLauncher;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Default implementation of the ProductTransformer interface.
 *
 * @author mcayanan
 *
 */
public abstract class DefaultTransformer implements ProductTransformer {
  /** logger object. */
  protected static Logger log = Logger.getLogger(
      TransformLauncher.class.getName());

  /**
   * Flag to indicate whether to overwrite an existing output file.
   */
  protected boolean overwriteOutput;

  protected boolean appendIndexToOutputFile;

  /**
   * Default constructor. Sets the flag to overwrite outputs to
   * true.
   *
   */
  public DefaultTransformer() {
    this(true);
    appendIndexToOutputFile = false;
  }

  /**
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public DefaultTransformer(boolean overwrite) {
    this.overwriteOutput = overwrite;
  }

  @Override
  public List<File> transform(File target, File outputDir, String format)
  throws TransformException {
    try {
      return transform(target.toURI().toURL(), outputDir, format);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
    /*
    List<File> results = new ArrayList<File>();
    try {
      results = transform(target, outputDir, format, "", 1);
    } catch (TransformException te) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, te.getMessage(), target));
    }
    return results;
    */
  }

  @Override
  public List<File> transform(URL url, File outputDir, String format)
		  throws TransformException, URISyntaxException, Exception {
	  List<File> results = new ArrayList<File>();
	  try {
		  results = transform(url, outputDir, format, "", 1);
	  } catch (TransformException te) {
		  log.log(new ToolsLogRecord(ToolsLevel.SEVERE, te.getMessage(), url.toString()));
	  }
	  return results;
  }
  
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    try {
      return transform(target.toURI().toURL(), outputDir, format, dataFile, index);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
  }
  
  public abstract List<File> transform(URL url, File outputDir, String format,
	      String dataFile, int index) throws TransformException, URISyntaxException, Exception;
  
  @Override
  public List<File> transform(List<URL> targets, File outputDir, String format)
      throws TransformException, URISyntaxException, Exception {
    List<File> results = new ArrayList<File>();
    for (URL target : targets) {
      try {
        results.addAll(transform(target, outputDir, format));
      } catch (TransformException te) {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, te.getMessage(), target.toString()));
      }
    }
    return results;
  }
  
  @Override
  public List<File> transformAll(File target, File outputDir, String format)
  throws TransformException {
    try {
      return transformAll(target.toURI().toURL(), outputDir, format);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
  }
 
  @Override
  public abstract List<File> transformAll(URL url, File outputDir, String format)
  throws TransformException, URISyntaxException, Exception;
 
  public List<File> transformAll(List<URL> targets, File outputDir,
		  String format) throws TransformException, URISyntaxException, Exception {
	  List<File> results = new ArrayList<File>();
	  for (URL target : targets) {
		  try {
			  results.addAll(transformAll(target, outputDir, format));
		  } catch (TransformException te) {
			  log.log(new ToolsLogRecord(ToolsLevel.SEVERE, te.getMessage(), target.toString()));
		  }
	  }
	  return results;
  }
}
