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
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.Array3DSpectrum;
import gov.nasa.arc.pds.xml.generated.DisplaySettings;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.objectAccess.Exporter;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ImageExporter;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.ImageProperties;
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
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    
    File result = null;
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      ImageProperties imageProperties = Utility.getImageProperties(target);
      List<FileAreaObservational> fileAreas = imageProperties.getFileAreas();
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
          process(target, objectAccess, fileArea, outputFile, format, index, 
              imageProperties.getDisplaySettings());
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
    return Arrays.asList(result);
    
  }

  @Override
  public List<File> transform(URL url, File outputDir, String format,
		  String dataFile, int index) throws TransformException, URISyntaxException, Exception {
	  File result = null;
	  File target = null;
	  try {
		  ObjectProvider objectAccess = new ObjectAccess(url);
		  ImageProperties imageProperties = Utility.getImageProperties(url);
		  List<FileAreaObservational> fileAreas = imageProperties.getFileAreas();
		  if (fileAreas.isEmpty()) {
			  throw new TransformException("Cannot find File_Area_Observational "
					  + "area in the label: " + url.toString());
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
				  process(new File(url.getFile()), objectAccess, fileArea, outputFile, format, index, 
						  imageProperties.getDisplaySettings());
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
	  return Arrays.asList(result);
  }
  
  private void process(File target, ObjectProvider objectAccess,
      FileAreaObservational fileArea, File outputFile, String format, 
      int index, List<DisplaySettings> displaySettings)
          throws Exception {
    if ( (outputFile.exists() && outputFile.length() != 0)
        && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
    } else {
      List<Array> arrays = objectAccess.getArrays(fileArea);
      arrays = Utility.getSupportedImages(arrays);
      Array selectedArray = null;
      try {
        selectedArray = arrays.get(index-1);
      } catch (IndexOutOfBoundsException ie) {
        throw new Exception("Image index '" + index
            + "' is greater than the max number of supported images found "
            + "for the label '" + (arrays.size()) + "' for data file '"
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
    	  File imageFile = null;
    	  // need to copy over the imageFile from the URL
    	  URL url = objectAccess.getRoot();
    	  if (url.getProtocol().startsWith("http")) {
    		  String urlStr = url.toString(); 
    		  String urlLocation = urlStr.substring(0, urlStr.lastIndexOf('/'));
    		  imageFile = Utility.getFileFromURL(new URL(urlLocation+"/"+fileArea.getFile().getFileName()), outputFile.getParentFile());
    	  }
    	  else 
    		  imageFile = new File(target.getParent(), fileArea.getFile().getFileName());
    	  
    	  Transcoder transcoder = new Transcoder();
    	  try {
    		  // For FITS, we need to map the selected index to the
    		  // actual HDU index in the file since the Transcoder
    		  // requires this.
    		  int hduIndex = Utility.getHDUIndex(imageFile, index-1);
    		  transcoder.transcode(imageFile, outputFile, format, hduIndex,
    				  true);
    		  
    		  // TODO TODO TODO: delete copied image file 
    	  } catch (Exception e) {
    		  throw new TransformException(e.getMessage());
    	  }
      } else {
    	  Exporter exporter = getImageExporter(selectedArray, fileArea, 
    			  objectAccess, displaySettings); 
    	  if ("jp2".equalsIgnoreCase(format)) {
    		  exporter.setExportType("jpeg2000");
    	  } else {
    		  exporter.setExportType(format);
    	  }
    	  exporter.convert(selectedArray, new FileOutputStream(outputFile));           
      }
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
    		  "Successfully transformed image '" + index + "' of file '"
    				  + fileArea.getFile().getFileName()
    				  + "' to the following output: "
    				  + outputFile.toString(), target));
    }
  }
  
  /**
   * Returns the correct ImageExporter object to do the transformation.
   * 
   * @param array
   * @param fileArea
   * @param objectAccess
   * @return The Exporter object associated with the given parameters.
   * @throws Exception
   */
  private Exporter getImageExporter(Array array, FileAreaObservational fileArea,
      ObjectProvider objectAccess, List<DisplaySettings> displaySettings)
          throws Exception {
    Exporter exporter = null;
    if (array instanceof Array2DImage) {
      exporter = ExporterFactory.get2DImageExporter(fileArea, objectAccess);
    } else if (array instanceof Array3DImage) {
      exporter = ExporterFactory.get3DImageExporter(fileArea, objectAccess);
    } else if (array instanceof Array3DSpectrum) {
      exporter = ExporterFactory.get3DSpectrumExporter(fileArea, objectAccess);
    } else {
      throw new Exception("Could not find an Exporter Class for "
          + array.getClass().getSimpleName());
    }
    ImageExporter ie = (ImageExporter) exporter;
    ie.setDisplaySettings(displaySettings);
    return exporter;
  }

  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    List<File> results = new ArrayList<File>();
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      ImageProperties imageProperties = Utility.getImageProperties(target);
      List<FileAreaObservational> fileAreas = imageProperties.getFileAreas();
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational "
            + "area in the label: " + target.toString());
      } else {
        for (FileAreaObservational fao : fileAreas) {
          List<Array> arrays = objectAccess.getArrays(fao);
          //Filter list to only the objects we support
          arrays = Utility.getSupportedImages(arrays);
          int numImages = arrays.size();
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
              process(target, objectAccess, fao, outputFile, format, (i+1), 
                  imageProperties.getDisplaySettings());
              results.add(outputFile);
            } catch (Exception e) {
              log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                  target));
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
  
  @Override
  public List<File> transformAll(URL url, File outputDir, String format)
      throws TransformException {
    List<File> results = new ArrayList<File>();
    File target = null;
    try {
      ObjectProvider objectAccess = new ObjectAccess(url);      
      ImageProperties imageProperties = Utility.getImageProperties(url);
      List<FileAreaObservational> fileAreas = imageProperties.getFileAreas();
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational "
            + "area in the label: " + url.toString());
      } else {
        for (FileAreaObservational fao : fileAreas) {
          List<Array> arrays = objectAccess.getArrays(fao);
          //Filter list to only the objects we support
          arrays = Utility.getSupportedImages(arrays);
          int numImages = arrays.size();
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
              process(new File(url.getFile()), objectAccess, fao, outputFile, format, (i+1), 
                  imageProperties.getDisplaySettings());
              results.add(outputFile);
            } catch (Exception e) {
              log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                  target));
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
