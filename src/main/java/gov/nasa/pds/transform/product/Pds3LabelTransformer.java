package gov.nasa.pds.transform.product;

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.util.logging.Logger;

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
