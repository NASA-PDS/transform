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
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;
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
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    try {
      return transform(target.toURI().toURL(), outputDir, format, dataFile, index);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
  }
  
  @Override
  public List<File> transform(URL url, File outputDir, String format,
      String dataFile, int index) throws TransformException, URISyntaxException, Exception {
    List<File> outputFiles = new ArrayList<File>();  
    File pds4Label = toPds4Label(url, outputDir);
    try {
      tableTransformer.setDataFileBasePath(Utility.getParent(url).toString());
      outputFiles = tableTransformer.transform(pds4Label, outputDir, format, 
          dataFile, index);
    } catch (TransformException te) {
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
    try {
      return toPds4Label(pds3Label.toURI().toURL(), outputDir);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }
  }
  
  private File toPds4Label(URL pds3Label, File outputDir)
		  throws TransformException, URISyntaxException, Exception {
	  File pds4Label = null;
	  try {
		  List<File> results = labelTransformer.transform(pds3Label, outputDir, 
				  "pds4-label");
		  if (!results.isEmpty()) {
		    //Should only return 1 result
		    pds4Label = results.get(0);
		  }
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
