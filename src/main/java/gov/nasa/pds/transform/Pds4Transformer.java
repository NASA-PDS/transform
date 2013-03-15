// Copyright 2006-2012, by the California Institute of Technology.
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

package gov.nasa.pds.transform;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.objectAccess.TwoDImageExporter;
import gov.nasa.pds.transform.constants.Constants;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Class to convert PDS4 data products into a viewable image file.
 *
 * @author mcayanan
 *
 */
public class Pds4Transformer implements PdsTransformer {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      Pds4Transformer.class.getName());

  public void transform(File label, File output, String format)
  throws TransformException {
    if (Constants.STYLESHEETS.containsKey(format.toLowerCase())) {
      // Use saxon for schematron (i.e. the XSLT generation).
      System.setProperty("javax.xml.transform.TransformerFactory",
          "net.sf.saxon.TransformerFactoryImpl");
      TransformerFactory factory = TransformerFactory.newInstance();
      try {
        Transformer pvlTransformer = factory.newTransformer(new StreamSource(
          this.getClass().getResourceAsStream(
              Constants.STYLESHEETS.get(format.toLowerCase()))));
        if (output == null) {
          String extension = format;
          if (format.equalsIgnoreCase("html-structure-only")) {
            extension = "html";
          }
          output = new File(FilenameUtils.getBaseName(label.getName()) + "."
              + extension);
        }
        pvlTransformer.transform(new StreamSource(label),
            new StreamResult(output));
        log.log(new ToolsLogRecord(ToolsLevel.INFO, "Successfully "
            + "transformed input label: " + output.toString(), label));
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
    } else {
      try {
        // This ensures that the tool will get the correct full path to the
        // label.
        File fullPathLabel = FileUtils.toFile(label.toURI().toURL());
        ObjectProvider objectAccess = new ObjectAccess(fullPathLabel.getParent());
        ProductObservational product = objectAccess.getProduct(label,
            ProductObservational.class);
        for (FileAreaObservational fao : product.getFileAreaObservationals()) {
          TwoDImageExporter exporter = ExporterFactory.get2DImageExporter(fao,
              objectAccess);
          Array2DImage image = objectAccess.getArray2DImages(fao).get(0);
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Transforming image file: " + fao.getFile().getFileName(),
              label));
          if (output == null) {
            String fileExtension = format;
            if ("jpeg2000".equals(format)) {
              fileExtension = "jp2";
            } else if ("JPEG2000".equals(format)) {
              fileExtension = "JP2";
            }
            output = new File(FilenameUtils.getBaseName(
                fao.getFile().getFileName()) + "." + fileExtension);
          }
          exporter.setExportType(format);
          exporter.convert(image, new FileOutputStream(output));
          log.log(new ToolsLogRecord(ToolsLevel.INFO,
              "Successfully transformed image file: "
              + output.toString(), label));
        }
      } catch (ParseException pe) {
        throw new TransformException("Problems parsing input label: "
            + pe.getMessage());
      } catch (Exception e) {
        throw new TransformException("Problem occurred during "
            + "transformation: " + e.getMessage());
      }
    }
  }

}
