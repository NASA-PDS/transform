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
