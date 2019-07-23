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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
  public List<File> transform(URL url, File outputDir, String format,
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
      return Arrays.asList(outputFile);
    }
    try {
      Label label = transformer.transform(target);
      PDS3LabelWriter writer = new PDS3LabelWriter();
      writer.write(label);
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
        "Successfully transformed target label to a PDS3 label: " + outputFile.toString(),
          target));
      return Arrays.asList(label.getLabelFile());
    } catch (TransformException t) {
      t.printStackTrace();
      throw t;
    } catch (IOException io) {
      throw new TransformException("Error while writing label to a file: " + io.getMessage());
    }
  }  
    
  @Override
  public List<File> transformAll(URL url, File outputDir, String format)
      throws TransformException, URISyntaxException, Exception {
    List<File> outputs = new ArrayList<File>();
    outputs.addAll(transform(url, outputDir, format));
    return outputs;
  }
}
