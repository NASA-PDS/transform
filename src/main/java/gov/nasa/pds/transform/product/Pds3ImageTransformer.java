// Copyright © 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
// 
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// • Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
// • Redistributions must reproduce the above copyright notice, this list of
//   conditions and the following disclaimer in the documentation and/or other
//   materials provided with the distribution.
// • Neither the name of Caltech nor its operating division, the Jet Propulsion
//   Laboratory, nor the names of its contributors may be used to endorse or
//   promote products derived from this software without specific prior written
//   permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.transform.product;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import gov.nasa.pds.tools.containers.FileReference;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.constants.Constants;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Transcoder;
import gov.nasa.pds.transform.util.Utility;

/**
 * Class to transform PDS3 images.
 *
 * @author mcayanan
 *
 */
public class Pds3ImageTransformer extends DefaultTransformer {
  /**
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public Pds3ImageTransformer(boolean overwrite) {
    super(overwrite);
  }
  
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    Label label = null;
    try {
      label = Utility.parsePds3Label(target);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
    // Get Image Pointers
    Map<String, List<PointerStatement>> imagePtrs = getImagePointers(label);
    if (imagePtrs.isEmpty()) {
      throw new TransformException("No image objects found in the label.");
    }
    if (dataFile.isEmpty()) {
      for (String key : imagePtrs.keySet()) {
        dataFile = key;
        break;
      }
    } else {
      if (!imagePtrs.keySet().contains(dataFile)) {
        throw new TransformException("Cannot find data file '" + dataFile
            + "' in the label.");
      }
    }
    // Check to see that the given index is valid
    if ( index > imagePtrs.get(dataFile).size() ) {
      throw new TransformException("Image index '" + index
          + "' is greater than the max number of images found for the "
          + "label '" + imagePtrs.get(dataFile).size() + "' for data file '"
          + dataFile + "'");
    }
    File imageFile = new File(target.getParent(), dataFile);
    if (!imageFile.exists()) {
      throw new TransformException("Image file does not exist: "
          + imageFile.toString());
    } else {
      File outputFile = Utility.createOutputFile(imageFile, outputDir,
          format);
      process(target, imageFile, outputFile, format, index);
      return Arrays.asList(outputFile);
    }
  }
  
  public List<File> transform(URL url, File outputDir, String format, String dataFile, int index) 
  throws TransformException, URISyntaxException, Exception {
	  Label label = null;
	  File target = null;  
	  try {
		  label = Utility.parsePds3Label(url);
	  } catch (Exception e) {
		  throw new TransformException(e.getMessage());
	  }
	  // Get Image Pointers
	  Map<String, List<PointerStatement>> imagePtrs = getImagePointers(label);
	  if (imagePtrs.isEmpty()) {
		  throw new TransformException("No image objects found in the label.");
	  }
	  if (dataFile.isEmpty()) {
		  for (String key : imagePtrs.keySet()) {
			  dataFile = key;
			  break;
		  }
	  } else {
		  if (!imagePtrs.keySet().contains(dataFile)) {
			  throw new TransformException("Cannot find data file '" + dataFile
					  + "' in the label.");
		  }
	  }
	  // Check to see that the given index is valid
	  if ( index > imagePtrs.get(dataFile).size() ) {
		  throw new TransformException("Image index '" + index
				  + "' is greater than the max number of images found for the "
				  + "label '" + imagePtrs.get(dataFile).size() + "' for data file '"
				  + dataFile + "'");
	  }
	  // to copy dataFile from URL to outputDir before the transformation
	  if (url.getProtocol().startsWith("http")) {
		  // need to get fileArea from URL
		  String urlStr = url.toString(); 
		  String urlLocation = urlStr.substring(0, urlStr.lastIndexOf('/'));
		  File fileA = Utility.getFileFromURL(new URL(urlLocation+"/"+dataFile), outputDir);
		  target = Utility.getFileFromURL(url, outputDir);
	  } 
	  else target = new File(url.toURI());
	  
	  File imageFile = new File(target.getParent(), dataFile);
	    
	  if (!imageFile.exists()) {
		  throw new TransformException("Image file does not exist: "
				  + imageFile.toString());
	  } else {
		  File outputFile = Utility.createOutputFile(imageFile, outputDir,
				  format);
		  process(target, imageFile, outputFile, format, index);
		  return Arrays.asList(outputFile);
	  }
  }

  private void process(File target, File imageFile, File outputFile,
      String format, int index) throws TransformException {
    if (outputFile.exists() && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
    } else {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Transforming image file: " + imageFile.toString(),
          target));
      if ("pds".equals(format)) {
        try {
          if (Constants.EXTERNAL_PROGRAMS.containsKey(format)) {
            vicarToPds(Constants.EXTERNAL_PROGRAMS.get(format), target,
                imageFile, outputFile);
          } else {
            throw new TransformException("Could not find an external "
                + "program to run for the following format type: "
                + format);
          }
        } catch (Exception e) {
          throw new TransformException(e.getMessage());
        }
      } else {
        Transcoder transcoder = new Transcoder();
        try {
          String extension = FilenameUtils.getExtension(imageFile.toString());
          if ("fit".equalsIgnoreCase(extension) ||
              "fits".equalsIgnoreCase(extension)) {
            int hduIndex = Utility.getHDUIndex(imageFile, index-1);
            transcoder.transcode(imageFile, outputFile, format, hduIndex,
                true);
          } else {
            transcoder.transcode(target, outputFile, format);
          }
        } catch (Exception e) {
          throw new TransformException(e.getMessage());
        }
      }
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Successfully transformed image file '" + imageFile.toString()
          + "' to the following output: " + outputFile.toString(),
          target));
    }
  }

  private void vicarToPds(File program, File target, File image, File outputFile)
      throws TransformException {
    try {
      String[] args = {
          image.getCanonicalPath(),
          outputFile.getCanonicalPath()
          };
      Utility.exec(program, args);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Transforming label file: " + target, target));
    File pds4Label = Utility.createOutputFile(target, outputFile.getParentFile(),
        "xml");
    try {
      //Transform the label to PDS4 using the Generate library
      Utility.generate(target, pds4Label, "generic-pds3_to_pds4.vm");
    } catch (Exception e) {
      throw new TransformException("Error occurred while "
          + "generating PDS4 label: " + e.getMessage());
    }
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Successfully transformed PDS3 label '" + target
        + "' to a PDS4 label '" + pds4Label + "'",
        target));
  }

  private Map<String, List<PointerStatement>> getImagePointers(Label label) {
    Map<String, List<PointerStatement>> results =
        new LinkedHashMap<String, List<PointerStatement>>();
    List<PointerStatement> pointers = new ArrayList<PointerStatement>();
    List<ObjectStatement> fileObjects = label.getObjects("FILE");
    if (!fileObjects.isEmpty()) {
      for (ObjectStatement fileObject : fileObjects) {
        pointers.addAll(fileObject.getPointers());
      }
    } else {
      pointers.addAll(label.getPointers());
    }
    for (PointerStatement pointer : pointers) {
      if (pointer.getIdentifier().getId().endsWith("IMAGE")) {
        if (!pointer.getFileRefs().isEmpty()) {
          FileReference ref = pointer.getFileRefs().get(0);
          List<PointerStatement> imagePtrs = results.get(ref.getPath());
          if (imagePtrs == null) {
            imagePtrs = new ArrayList<PointerStatement>();
            imagePtrs.add(pointer);
          } else {
            imagePtrs.add(pointer);
          }
          results.put(ref.getPath(), imagePtrs);
        }
      }
    }
    return results;
  }

  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    Label label = null;
    List<File> outputFiles = new ArrayList<File>();
    try {
      label = Utility.parsePds3Label(target);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
    // Get Image Pointers
    Map<String, List<PointerStatement>> imagePtrMap = getImagePointers(label);

    for (String dataFile : imagePtrMap.keySet()) {
      List<PointerStatement> images = imagePtrMap.get(dataFile);
      int numImages = images.size();
      File imageFile = new File(target.getParent(), dataFile);
      for (int i = 0; i < numImages; i++) {
        File outputFile = null;
        if (numImages > 1) {
          outputFile = Utility.createOutputFile(imageFile, outputDir,
              format, (i+1));
        } else {
          outputFile = Utility.createOutputFile(imageFile, outputDir,
            format);
        }
        try {
          process(target, imageFile, outputFile, format, (i+1));
          outputFiles.add(outputFile);
        } catch (Exception e) {
          log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
              target));
        }
      }
    }
    return outputFiles;
  }

  public List<File> transformAll(URL url, File outputDir, String format)
		  throws TransformException {
	  Label label = null;
	  List<File> outputFiles = new ArrayList<File>();
	  File target = null;
	  try {
		  target = Utility.getFileFromURL(url, outputDir);
		  label = Utility.parsePds3Label(url);
	  } catch (Exception e) {
		  throw new TransformException(e.getMessage());
	  }
	  // Get Image Pointers
	  Map<String, List<PointerStatement>> imagePtrMap = getImagePointers(label);

	  for (String dataFile : imagePtrMap.keySet()) {
		  List<PointerStatement> images = imagePtrMap.get(dataFile);
		  int numImages = images.size();
		  File imageFile = new File(url.getPath(), dataFile);
		  for (int i = 0; i < numImages; i++) {
			  File outputFile = null;
			  if (numImages > 1) {
				  outputFile = Utility.createOutputFile(imageFile, outputDir,
						  format, (i+1));
			  } else {
				  outputFile = Utility.createOutputFile(imageFile, outputDir,
						  format);
			  }
			  try {
				  process(target, imageFile, outputFile, format, (i+1));
				  outputFiles.add(outputFile);
			  } catch (Exception e) {
				  log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
						  target));
			  }
		  }
	  }
	  return outputFiles;
  }
}
