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

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.objectAccess.TwoDImageExporter;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

/**
 * Class to perform transformations of PDS4 images.
 *
 * @author mcayanan
 *
 */
public class Pds4ImageTransformer extends DefaultTransformer {

  /**
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public Pds4ImageTransformer(boolean overwrite) {
    super(overwrite);
  }

  @Override
  public File transform(File target, File outputDir, String format)
      throws TransformException {
    File result = null;
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      List<FileAreaObservational> fileAreas = Utility.getFileAreas(target);
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational area in "
            + "the label: " + target.toString());
      } else {
        for (FileAreaObservational fao : fileAreas) {
          TwoDImageExporter exporter = ExporterFactory.get2DImageExporter(fao,
              objectAccess);
          List<Array2DImage> images = objectAccess.getArray2DImages(fao);
          if (!images.isEmpty()) {
            for (Array2DImage image : images) {
              log.log(new ToolsLogRecord(ToolsLevel.INFO,
                  "Transforming image file: " + fao.getFile().getFileName(),
                  target));
              File outputFile = Utility.createOutputFile(
                  new File(fao.getFile().getFileName()), outputDir, format);
              if (outputFile.exists() && !overwriteOutput) {
                log.log(new ToolsLogRecord(ToolsLevel.INFO,
                    "Output file already exists. No transformation will occur: "
                    + outputFile.toString(), target));

              } else {
                if ("jp2".equalsIgnoreCase(format)) {
                  exporter.setExportType("jpeg2000");
                } else {
                  exporter.setExportType(format);
                }
                exporter.convert(image, new FileOutputStream(outputFile));
                log.log(new ToolsLogRecord(ToolsLevel.INFO,
                    "Successfully transformed image file '"
                    + fao.getFile().getFileName() + "' to the following output: "
                    + outputFile.toString(), target));
              }
              result = outputFile;
            }
          } else {
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                "No images found in label.", target));
          }
        }
        return result;
      }
    } catch (ParseException pe) {
      throw new TransformException("Problems parsing label: "
          + pe.getMessage());
    } catch (Exception e) {
      throw new TransformException("Problem occurred during "
          + "transformation: " + e.getMessage());
    }
  }

}
