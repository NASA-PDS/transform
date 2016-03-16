// Copyright 2006-2016, by the California Institute of Technology.
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.Array3DSpectrum;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.objectAccess.ThreeDImageExporter;
import gov.nasa.pds.objectAccess.ThreeDSpectrumExporter;
import gov.nasa.pds.objectAccess.TwoDImageExporter;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Transcoder;
import gov.nasa.pds.transform.util.Utility;

/**
 * Class to perform transformations of PDS4 images.
 *
 * @author mcayanan
 *
 */
public class Pds4ImageTransformer extends DefaultTransformer {

  private List<Integer> bands;
  
  /**
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public Pds4ImageTransformer(boolean overwrite) {
    super(overwrite);
    bands = new ArrayList<Integer>();
  }

  @Override
  public File transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    File result = null;
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      List<FileAreaObservational> fileAreas = Utility.getFileAreas(target);
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational "
            + "area in the label: " + target.toString());
      } else {
        FileAreaObservational fileArea = null;
        if (dataFile.isEmpty()) {
          fileArea = fileAreas.get(0);
        } else {
          for (FileAreaObservational fa : fileAreas) {
            if (fa.getFile().getFileName().equals(dataFile)) {
              fileArea = fa;
            }
          }
        }
        if (fileArea != null) {
          File outputFile = Utility.createOutputFile(
              new File(fileArea.getFile().getFileName()), outputDir, format);
          process(target, objectAccess, fileArea, outputFile, format, index);
          result = outputFile;
        } else {
          throw new TransformException("Cannot find data file '" + dataFile
              + "' in the given label.");
        }
      }
    } catch (ParseException pe) {
      throw new TransformException("Problems parsing label: "
          + pe.getMessage());
    } catch (Exception e) {
      throw new TransformException("Problem occurred during "
          + "transformation: " + e.getMessage());
    }
    return result;
  }

  private void process(File target, ObjectProvider objectAccess,
      FileAreaObservational fileArea, File outputFile, String format, int index)
          throws Exception {
    if ( (outputFile.exists() && outputFile.length() != 0)
        && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
    } else {
      List<Array3DSpectrum> array3DSpectrums = objectAccess.getArray3DSpectrums(fileArea);
      if (!array3DSpectrums.isEmpty()) {
        ThreeDSpectrumExporter exporter = ExporterFactory.get3DSpectrumExporter(fileArea, objectAccess);
        Array3DSpectrum spectrum = null;
        try {
          spectrum = array3DSpectrums.get(index-1);
        } catch (IndexOutOfBoundsException ie) {
          throw new Exception("Image index '" + index
              + "' is greater than the max number of images found for the "
              + "label '" + (array3DSpectrums.size()) + "' for data file '"
              + fileArea.getFile().getFileName() + "'");            
        }
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "Transforming image '" + index + "' of file '"
            + fileArea.getFile().getFileName() + "'", target));
        if ("jp2".equalsIgnoreCase(format)) {
          exporter.setExportType("jpeg2000");
        } else {
          exporter.setExportType(format);
        }
        if (!bands.isEmpty()) {
          exporter.setBands(bands);
        }
        exporter.convert(spectrum, new FileOutputStream(outputFile));        
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "Successfully transformed image '" + index + "' of file '"
            + fileArea.getFile().getFileName()
            + "' to the following output: "
            + outputFile.toString(), target));
                
      } else {
        List<Array3DImage> array3DImages = objectAccess.getArray3DImages(fileArea);
        if (!array3DImages.isEmpty()) {
          ThreeDImageExporter exporter = ExporterFactory.get3DImageExporter(fileArea, objectAccess);
          Array3DImage image = null;
          try {
            image = array3DImages.get(index-1);
          } catch (IndexOutOfBoundsException ie) {
            throw new Exception("Image index '" + index
                + "' is greater than the max number of images found for the "
                + "label '" + (array3DImages.size()) + "' for data file '"
                + fileArea.getFile().getFileName() + "'");          
          }
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Transforming image '" + index + "' of file '"
              + fileArea.getFile().getFileName() + "'", target));
          if ("jp2".equalsIgnoreCase(format)) {
            exporter.setExportType("jpeg2000");
          } else {
            exporter.setExportType(format);
          }
          exporter.convert(image, new FileOutputStream(outputFile));        
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Successfully transformed image '" + index + "' of file '"
              + fileArea.getFile().getFileName()
              + "' to the following output: "
              + outputFile.toString(), target));
          
        } else {
          TwoDImageExporter exporter = ExporterFactory.get2DImageExporter(fileArea,
              objectAccess);
          List<Array2DImage> images = objectAccess.getArray2DImages(fileArea);
          if (images.isEmpty()) {
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                "No images found in label.", target));
          } else {
            Array2DImage image = null;
            try {
              image = images.get(index-1);
            } catch (IndexOutOfBoundsException ie) {
              throw new Exception("Image index '" + index
                  + "' is greater than the max number of images found for the "
                  + "label '" + (images.size()) + "' for data file '"
                  + fileArea.getFile().getFileName() + "'");
            }
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                "Transforming image '" + index + "' of file '"
                + fileArea.getFile().getFileName() + "'", target));
            String extension = FilenameUtils.getExtension(
                fileArea.getFile().getFileName());
            // Call the Transcoder if we're transforming a FITS file
            if ("fits".equalsIgnoreCase(extension)
                || "fit".equalsIgnoreCase(extension)) {
              File imageFile = new File(target.getParent(),
                  fileArea.getFile().getFileName());
              Transcoder transcoder = new Transcoder();
              try {
                // For FITS, we need to map the selected index to the
                // actual HDU index in the file since the Transcoder
                // requires this.
                int hduIndex = Utility.getHDUIndex(imageFile, index-1);
                transcoder.transcode(imageFile, outputFile, format, hduIndex,
                  true);
              } catch (Exception e) {
                throw new TransformException(e.getMessage());
              }
            } else {
              // Call the PDS4-Tools library for non-FITS file
              // transformations
              if ("jp2".equalsIgnoreCase(format)) {
                exporter.setExportType("jpeg2000");
              } else {
                exporter.setExportType(format);
              }
              exporter.convert(image, new FileOutputStream(outputFile));
            }
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                "Successfully transformed image '" + index + "' of file '"
                + fileArea.getFile().getFileName()
                + "' to the following output: "
                + outputFile.toString(), target));
          }
        }
      }
    }
  }
  

  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    List<File> results = new ArrayList<File>();
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      List<FileAreaObservational> fileAreas = Utility.getFileAreas(target);
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational "
            + "area in the label: " + target.toString());
      } else {
        for (FileAreaObservational fao : fileAreas) {
          List<Array2DImage> twoDImages = objectAccess.getArray2DImages(fao);
          if (!twoDImages.isEmpty()) {
            int numImages = twoDImages.size();
            for (int i = 0; i < numImages; i++) {
              File outputFile = null;
              if (numImages > 1) {
                outputFile = Utility.createOutputFile(
                    new File(fao.getFile().getFileName()), outputDir, format,
                    (i+1));
              } else {
                outputFile = Utility.createOutputFile(
                    new File(fao.getFile().getFileName()), outputDir, format);
              }
              try {
                process(target, objectAccess, fao, outputFile, format, (i+1));
                results.add(outputFile);
              } catch (Exception e) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                    target));
              }
            }
          }
          List<Array3DImage> threeDImages = objectAccess.getArray3DImages(fao);
          if (!threeDImages.isEmpty()) {
            int numImages = threeDImages.size();
            for (int i = 0; i < numImages; i++) {
              File outputFile = null;
              if (numImages > 1) {
                outputFile = Utility.createOutputFile(
                    new File(fao.getFile().getFileName()), outputDir, format,
                    (i+1));
              } else {
                outputFile = Utility.createOutputFile(
                    new File(fao.getFile().getFileName()), outputDir, format);
              }
              try {
                process(target, objectAccess, fao, outputFile, format, (i+1));
                results.add(outputFile);
              } catch (Exception e) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                    target));
              }
            }            
          }
          List<Array3DSpectrum> threeDSpectrums = objectAccess.getArray3DSpectrums(fao);
          if (!threeDSpectrums.isEmpty()) {
            int numImages = threeDSpectrums.size();
            for (int i = 0; i < numImages; i++) {
              File outputFile = null;
              if (numImages > 1) {
                outputFile = Utility.createOutputFile(
                    new File(fao.getFile().getFileName()), outputDir, format,
                    (i+1));
              } else {
                outputFile = Utility.createOutputFile(
                    new File(fao.getFile().getFileName()), outputDir, format);
              }
              try {
                process(target, objectAccess, fao, outputFile, format, (i+1));
                results.add(outputFile);
              } catch (Exception e) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                    target));
              }              
            }
          }
        }
        return results;
      }
    } catch (ParseException pe) {
      throw new TransformException("Problems parsing label: "
          + pe.getMessage());
    } catch (Exception e) {
      throw new TransformException("Problem occurred during "
          + "transformation: " + e.getMessage());
    }
  }
  
  public void setBands(List<Integer> bands) {
    this.bands = bands;
  }
}
