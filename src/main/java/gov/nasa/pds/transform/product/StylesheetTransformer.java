// Copyright 2006-2013, by the California Institute of Technology.
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

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.constants.Constants;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.util.logging.Logger;

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
  /** logger object. */
  private static Logger log = Logger.getLogger(
      StylesheetTransformer.class.getName());

  public void transform(File target, File outputDir, String format)
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
      transformer.transform(new StreamSource(target),
          new StreamResult(outputFile));
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Successfully transformed target label to the following output: "
          + outputFile.toString(), target));
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
}
