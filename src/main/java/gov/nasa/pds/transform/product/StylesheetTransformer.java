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
import gov.nasa.pds.transform.constants.Constants;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.net.URISyntaxException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Class that performs transformations of PDS4 data product labels
 * into other formats defined by a stylesheet.
 *
 * @author mcayanan
 *
 */
public class StylesheetTransformer extends DefaultTransformer {
  /**
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public StylesheetTransformer(boolean overwrite) {
    super(overwrite);
  }
/*
  public List<File> transform(File target, File outputDir, String format)
  throws TransformException {
    // Use saxon for schematron (i.e. the XSLT generation).
    System.setProperty("javax.xml.transform.TransformerFactory",
        "net.sf.saxon.TransformerFactoryImpl");
    TransformerFactory factory = TransformerFactory.newInstance();
    try {
      Transformer transformer = factory.newTransformer(
          new StreamSource(this.getClass().getResourceAsStream(
              Constants.STYLESHEETS.get(format)))
          );
      File outputFile = Utility.createOutputFile(target, outputDir, format);
      if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "Output file already exists. No transformation will occur: "
            + outputFile.toString(), target));
      } else {
        transformer.transform(new StreamSource(target),
            new StreamResult(outputFile));
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "Successfully transformed target label to the following output: "
            + outputFile.toString(), target));
      }
      return Arrays.asList(outputFile);
    } catch (TransformerConfigurationException tce) {
      throw new TransformException(
          "Error occurred while loading stylesheet for the '" + format
          + "' transformation: "
          + tce.getMessage());
    } catch (TransformerException te) {
      throw new TransformException(
          "Error occurred while performing stylesheet transformation: "
          + te.getMessage());
    }
  }
  */
/*
  @Override
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException {
    return transform(target, outputDir, format);
  }
  */
  
  @Override
  public List<File> transform(URL url, File outputDir, String format,
		  String dataFile, int index) throws TransformException, URISyntaxException, Exception {
      try {
		  SSLContext context = SSLContext.getInstance("TLSv1.2");
		  context.init(null, null, new java.security.SecureRandom());
		  HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	  } catch (Exception e) {
		  throw new Exception("Error while setting SSLSocket connection to TLSv1.2: " + e.getMessage());
	  }
      // Use saxon for schematron (i.e. the XSLT generation).
      System.setProperty("javax.xml.transform.TransformerFactory",
          "net.sf.saxon.TransformerFactoryImpl");
      TransformerFactory factory = TransformerFactory.newInstance();
      try {
        Transformer transformer = factory.newTransformer(
            new StreamSource(this.getClass().getResourceAsStream(
                Constants.STYLESHEETS.get(format)))
            );
        File outputFile = Utility.createOutputFile(new File(url.getFile()), outputDir, format);
        if ((outputFile.exists() && outputFile.length() != 0) && !overwriteOutput) {
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Output file already exists. No transformation will occur: "
              + outputFile.toString(), url.toString()));
        } else {
          transformer.transform(new StreamSource(url.toString()),
              new StreamResult(outputFile));
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Successfully transformed target label to the following output: "
              + outputFile.toString(), url.toString()));
        }
        return Arrays.asList(outputFile);
      } catch (TransformerConfigurationException tce) {
        throw new TransformException(
            "Error occurred while loading stylesheet for the '" + format
            + "' transformation: "
            + tce.getMessage());
      } catch (TransformerException te) {
        throw new TransformException(
            "Error occurred while performing stylesheet transformation: "
            + te.getMessage());
      }
  }
/*
  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    List<File> outputs = new ArrayList<File>();
    outputs.addAll(transform(target, outputDir, format));
    return outputs;
  }
*/
  @Override
  public List<File> transformAll(URL url, File outputDir, String format)
      throws TransformException, URISyntaxException, Exception {
    List<File> outputs = new ArrayList<File>();
    outputs.addAll(transform(url, outputDir, format));
    return outputs;
  }
}
