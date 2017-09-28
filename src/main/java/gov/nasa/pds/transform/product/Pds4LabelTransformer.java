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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URISyntaxException;

import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.PDS3LabelWriter;
import gov.nasa.pds.transform.util.Utility;

/**
 * Class that performs transformations on a PDS4 label.
 * 
 * @author mcayanan
 *
 */
public class Pds4LabelTransformer extends DefaultTransformer {
  
  /**
   * Constructor.
   * 
   * @param overwrite Flag to allow overwriting of the output file.
   */
  public Pds4LabelTransformer(boolean overwrite) {
    super(overwrite);
  }
  
  @Override
  public File transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Transforming label file: " + target, target));
    File outputFile = Utility.createOutputFile(target, outputDir, format);
    Pds4ToPds3LabelTransformer transformer = new Pds4ToPds3LabelTransformer(outputFile);
    if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
      return outputFile;
    }
    try {
      Label label = transformer.transform(target);
      PDS3LabelWriter writer = new PDS3LabelWriter();
      writer.write(label);
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Successfully transformed target label to a PDS3 label: " + outputFile.toString(),
          target));
      return label.getLabelFile();
    } catch (TransformException t) {
      t.printStackTrace();
      throw t;
    } catch (IOException io) {
      throw new TransformException("Error while writing label to a file: " + io.getMessage());
    }
  }  
  
  @Override
  public File transform(URL url, File outputDir, String format,
      String dataFile, int index) throws TransformException, URISyntaxException, Exception {
    File target = new File(url.toURI());
	log.log(new ToolsLogRecord(ToolsLevel.INFO,
	        "Transforming label file: " + url.toString(), url.toString()));
	
    File outputFile = Utility.createOutputFile(new File(url.getFile()), outputDir, format);
    Pds4ToPds3LabelTransformer transformer = new Pds4ToPds3LabelTransformer(outputFile);
    if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
      return outputFile;
    }
    try {
      Label label = transformer.transform(target);
      PDS3LabelWriter writer = new PDS3LabelWriter();
      writer.write(label);
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Successfully transformed target label to a PDS3 label: " + outputFile.toString(),
          target));
      return label.getLabelFile();
    } catch (TransformException t) {
      t.printStackTrace();
      throw t;
    } catch (IOException io) {
      throw new TransformException("Error while writing label to a file: " + io.getMessage());
    }
  }  
  
  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    List<File> outputs = new ArrayList<File>();
    outputs.add(transform(target, outputDir, format));
    return outputs;
  }
  
  @Override
  public List<File> transformAll(URL url, File outputDir, String format)
      throws TransformException, URISyntaxException, Exception {
    List<File> outputs = new ArrayList<File>();
    outputs.add(transform(url, outputDir, format));
    return outputs;
  }
}
