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
package gov.nasa.pds.transform.util;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.imaging.generate.Generator;
import gov.nasa.pds.imaging.generate.context.ContextMappings;
import gov.nasa.pds.imaging.generate.label.PDS3Label;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.util.MessageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

/**
 * Utility class.
 *
 */
public class Utility {

  /**
   * Convert a string to a URL.
   *
   * @param s The string to convert
   * @return A URL of the input string
   */
  public static URL toURL(String s) throws MalformedURLException {
    URL url = null;
    try {
      url = new URL(s);
    } catch (MalformedURLException ex) {
      url = new File(s).toURI().toURL();
    }
    return url;
  }

  /**
   * Convert a string to a URI.
   *
   * @param s The string to convert.
   *
   * @return A well-formed URI.
   */
  public static String toWellFormedURI(String s) {
    return s.replaceAll(" ", "%20");
  }

  /**
   * Get the current date time.
   *
   * @return A date time.
   */
  public static String getDateTime() {
    SimpleDateFormat df = new SimpleDateFormat(
    "EEE, MMM dd yyyy 'at' hh:mm:ss a");
    Date date = Calendar.getInstance().getTime();
    return df.format(date);
  }

  public static Label parsePds3Label(File label) throws Exception {
    ManualPathResolver resolver = new ManualPathResolver();
    DefaultLabelParser parser = new DefaultLabelParser(false, true, resolver);
    Label l = null;
    try {
      l = parser.parseLabel(label.toURI().toURL());
    } catch (LabelParserException lp) {
      throw new Exception("Problem while parsing input label: "
          + MessageUtils.getProblemMessage(lp));
    } catch (Exception e) {
      throw new Exception("Problem while parsing input label: "
          + e.getMessage());
    }
    return l;
  }

  public static List<FileAreaObservational> getFileAreas(File pds4Label)
  throws ParseException {
    List<FileAreaObservational> result = new ArrayList<FileAreaObservational>();
    ObjectProvider objectAccess = new ObjectAccess();
    ProductObservational product = objectAccess.getProduct(pds4Label,
        ProductObservational.class);
    if (product.getFileAreaObservationals() != null) {
      result.addAll(product.getFileAreaObservationals());
    }
    return result;
  }

  public static File createOutputFile(File file, File outputDir, String format) {
    String fileExtension = format;
    String baseFilename = FilenameUtils.getBaseName(file.getName());
    if ("html-structure-only".equals(format)) {
      fileExtension = "html";
      baseFilename += "-structure";
    } else if ("pds".equals(format)) {
      fileExtension = "img";
    } else if ("pds4-label".equals(format)) {
      fileExtension = "xml";
      baseFilename = baseFilename.toLowerCase();
    }
    File outputFile = new File(outputDir, baseFilename + "." + fileExtension);
    return outputFile;
  }

  public static void exec(File program, String[] args)
  throws IOException, InterruptedException {
    List<String> cmdArray = new ArrayList<String>();
    cmdArray.add(program.toString());
    cmdArray.addAll(Arrays.asList(args));
    if ("py".equalsIgnoreCase(FilenameUtils.getExtension(
        program.getName()))) {
      cmdArray.add(0, "python");
    }
    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    try {
      process = runtime.exec(cmdArray.toArray(new String[0]));
    } catch (IOException io) {
      throw new IOException("Error occurred while running external "
          + "transform process: " + io.getMessage());
    }
    InputStream errstream = process.getErrorStream();
    BufferedReader errReader = new BufferedReader(
        new InputStreamReader(errstream));
    String errorMsg = "";
    try {
      String line = "";
      while ( (line = errReader.readLine()) != null) {
        errorMsg += line + "\n";
      }
    } catch (IOException io) {
      throw new IOException("Error occurred while reading error stream: "
          + io.getMessage());
    }
    try {
      if (process.waitFor() != 0) {
        PrintStream ps = new PrintStream(process.getOutputStream());
        ps.println();

        throw new IOException("Process did not terminate normally, exit code ["
            + process.exitValue() + "]: " + errorMsg);
      }
    } catch (InterruptedException i) {
      throw i;
    } finally {
      if (errReader != null) {
        try {
          errReader.close();
        } catch (Exception ignore) {}
      }
    }
  }

  public static void generate(File target, File outputFile, String templateName)
  throws Exception {
    System.getProperties().setProperty(
        "javax.xml.parsers.DocumentBuilderFactory",
        "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    Generator generator = new Generator();
    generator.setOutputFile(outputFile);
    PDSObject pdsObject = new PDS3Label(target.toString());
    pdsObject.setMappings();
    generator.setPDSObject(pdsObject);
    generator.setContextMappings(new ContextMappings(pdsObject));
    final Properties props = new Properties();
    props.setProperty("resource.loader", "string");
    props.setProperty("resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
    // Property to disable the velocity logging
    props.setProperty("runtime.log.logsystem.class",
        "org.apache.velocity.runtime.log.NullLogSystem");
    Velocity.init(props);
    if (!Velocity.resourceExists(templateName)) {
      InputStream stream = Utility.class.getResourceAsStream(templateName);
      String resourceContents = IOUtils.toString(stream);
      StringResourceRepository repo = StringResourceLoader.getRepository();
      repo.putStringResource(templateName, resourceContents);
    }
    generator.setTemplate(Velocity.getTemplate(templateName));
    generator.setContext();
    generator.getContext().put("FilenameUtils", FilenameUtils.class);
    generator.generate(false);
  }
}
