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

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.net.URISyntaxException;

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

  public List<File> transform(URL url, File outputDir, String format)
		  throws TransformException, URISyntaxException, Exception {
	  File target = null;
	  log.log(new ToolsLogRecord(ToolsLevel.INFO,
			  "Transforming label file: " + url.toString(), url.toString()));
	  File outputFile = Utility.createOutputFile(new File(url.getFile()), outputDir, format);
	  if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
		  log.log(new ToolsLogRecord(ToolsLevel.INFO,
				  "Output file already exists. No transformation will occur: "
						  + outputFile.toString(), url.toString()));
		  return Arrays.asList(outputFile);
	  }
	 
      if (url.getProtocol().startsWith("http")) {
		  target = Utility.getFileFromURL(url, outputDir);
	  } 
	  else target = new File(url.toURI());         
	  try {
		  Utility.generate(target, outputFile, "generic-pds3_to_pds4.vm", includePaths);
	  } catch (Exception e) {
		  throw new TransformException("Error occurred while generating "
				  + "PDS4 label: " + e.getMessage());
	  }
	  log.log(new ToolsLogRecord(ToolsLevel.INFO,
			  "Finished transforming PDS3 label '" + url.toString()
			  + "' to a PDS4 label '" + outputFile + "'", url.toString()));
	  return Arrays.asList(outputFile);
  }

  @Override
  public List<File> transform(File target, File outputDir, String format)
      throws TransformException {
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Transforming label file: " + target, target));
    File outputFile = Utility.createOutputFile(target, outputDir, format);
    if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
      return Arrays.asList(outputFile);
    }
    try {
      Utility.generate(target, outputFile, "generic-pds3_to_pds4.vm", includePaths);
    } catch (Exception e) {
      throw new TransformException("Error occurred while generating "
          + "PDS4 label: " + e.getMessage());
    }
    log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Finished transforming PDS3 label '" + target
        + "' to a PDS4 label '" + outputFile + "'", target));
    return Arrays.asList(outputFile);
  }

  @Override
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    return transform(target, outputDir, format);
  }

  @Override
  public List<File> transform(URL url, File outputDir, String format, String dataFile, int index) 
  throws TransformException, URISyntaxException, Exception {
	  return transform(url, outputDir, format);
  }
  
  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    List<File> outputs = new ArrayList<File>();
    outputs.addAll(transform(target, outputDir, format));
    return outputs;
  }
  
  @Override
  public List<File> transformAll(URL url, File outputDir, String format)
		  throws TransformException, URISyntaxException, Exception {
     List<File> outputs = new ArrayList<File>();
	 outputs.addAll(transform(url, outputDir, format));
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
