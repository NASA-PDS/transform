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

package gov.nasa.pds.transform.util;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.DisciplineArea;
import gov.nasa.arc.pds.xml.generated.DisplaySettings;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.imaging.generate.Generator;
import gov.nasa.pds.imaging.generate.context.ContextMappings;
import gov.nasa.pds.imaging.generate.label.PDS3Label;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.readers.ParserType;
import gov.nasa.pds.imaging.generate.util.TextUtil;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.util.MessageUtils;
import gov.nasa.pds.transform.constants.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import java.io.OutputStream;
import java.io.FileOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.ImageHDU;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

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
  
  public static Label parsePds3Label(URL label) throws Exception {
	  try {
		  SSLContext context = SSLContext.getInstance("TLSv1.2");
		  context.init(null, null, new java.security.SecureRandom());
		  HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	  } catch (Exception e) {
		  throw new IOException ("Error while setting SSLSocket connection to TLSv1.2: " + e.getMessage());
	  }
	  ManualPathResolver resolver = new ManualPathResolver();
	  DefaultLabelParser parser = new DefaultLabelParser(false, true, resolver);
	  Label l = null;
	  try {
		  l = parser.parseLabel(label);
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
		  throws ParseException, MalformedURLException, URISyntaxException {
	  List<FileAreaObservational> result = new ArrayList<FileAreaObservational>();
	  ObjectProvider objectAccess = new ObjectAccess();
	  ProductObservational product = objectAccess.getProduct(pds4Label,
			  ProductObservational.class);
	  if (product.getFileAreaObservationals() != null) {
		  result.addAll(product.getFileAreaObservationals());
	  }
	  return result;
  }
  
  public static List<FileAreaObservational> getFileAreas(URL pds4Label)
		  throws ParseException, MalformedURLException, URISyntaxException, IOException {
	  try {
		  SSLContext context = SSLContext.getInstance("TLSv1.2");
		  context.init(null, null, new java.security.SecureRandom());
		  HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	  } catch (Exception e) {
		  throw new IOException ("Error while setting SSLSocket connection to TLSv1.2: " + e.getMessage());
	  }
	  List<FileAreaObservational> result = new ArrayList<FileAreaObservational>();
	  ObjectProvider objectAccess = new ObjectAccess(pds4Label);
	  ProductObservational product = objectAccess.getProduct(pds4Label,
			  ProductObservational.class);
	  if (product.getFileAreaObservationals() != null) {
		  result.addAll(product.getFileAreaObservationals());
	  }
	  return result;
  }

  public static FileAreaObservational getFileArea(File pds4Label,
		  String dataFile) throws ParseException, MalformedURLException, 
		  URISyntaxException {
	  FileAreaObservational result = null;
	  List<FileAreaObservational> fileAreas = getFileAreas(pds4Label);
	  if (dataFile.isEmpty()) {
		  result = fileAreas.get(0);
	  } else {
		  for (FileAreaObservational fa : fileAreas) {
			  if (fa.getFile().getFileName().equals(dataFile)) {
				  result = fa;
			  }
		  }
	  }
	  return result;
  }

  public static File createOutputFile(File file, File outputDir, String format) {
    return createOutputFile(file, outputDir, format, -1);
  }

  public static File createOutputFile(File file, File outputDir, String format, int index) {
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
    } else if ("pds3-label".equals(format)) {
      fileExtension = "LBL";
      baseFilename = baseFilename.toUpperCase();
    }
    if (index != -1) {
      baseFilename += "_" + index;
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
    generate(target, outputFile, templateName, new ArrayList<String>());
  }
  
  public static void generate(File target, File outputFile, String templateName, List<String> includePaths)
  throws Exception {
    System.getProperties().setProperty(
        "javax.xml.parsers.DocumentBuilderFactory",
        "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    System.getProperties().setProperty("javax.xml.transform.TransformerFactory",
        "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    System.getProperties().setProperty("javax.xml.parsers.SAXParserFactory", 
        "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    Generator generator = new Generator();
    generator.setOutputFile(outputFile);
    PDS3Label pdsLabel = new PDS3Label(target.toString());
    pdsLabel.setParserType(ParserType.PRODUCT_TOOLS);
    pdsLabel.setIncludePaths(includePaths);
    PDSObject pdsObject = pdsLabel;
    pdsObject.setMappings();
    generator.setPDSObject(pdsObject);
    generator.setContextMappings(new ContextMappings(pdsObject));
    VelocityEngine engine = new VelocityEngine();
    engine.setProperty("resource.loader", "classpath");
    engine.setProperty("classpath.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    engine.init();
    generator.setTemplate(
        engine.getTemplate("/gov/nasa/pds/transform/util/" + templateName));
    generator.setContext();
    generator.getContext().put("FilenameUtils", FilenameUtils.class);
    generator.getContext().put("TextUtil", TextUtil.class);
    generator.getContext().put("log", Logger.getLogger(Utility.class.getName()));
    generator.generate(false);
  }
  
  /**
   * Gets the HDU index that corresponds to the given index.
   *
   * @param fitsFile The FITS file to look for HDUs.
   * @param index The desired index.
   *
   * @return An HDU index that corresponds to the given index.
   *
   * @throws FitsException If an error occurred reading the FITS file.
   * @throws IOException If the FITS file cannot be read.
   */
  public static int getHDUIndex(File fitsFile, int index) throws FitsException,
  IOException {
    int hduIndex = 0;
    int numImages = 0;
    Fits fits = new Fits();
    FileInputStream inputStream = new FileInputStream(fitsFile);
    try {
      fits.read(inputStream);
      for (int i = 0; i < fits.getNumberOfHDUs(); i++) {
        BasicHDU hdu = fits.getHDU(i);
        if (hdu instanceof ImageHDU) {
          if (index == numImages) {
            break;
          } else {
            numImages++;
          }
        }
        hduIndex++;
      }
      return hduIndex;
    } finally {
      inputStream.close();
    }
  }
  
  /**
   * Returns a list of supported image objects.
   * 
   * @param arrays A list of Array objects to filter.
   * 
   * @return A list of supported Array image objects.
   */
  public static List<Array> getSupportedImages(List<Array> arrays) {
    List<Array> results = new ArrayList<Array>();
    for (Array array : arrays) {
      if (Constants.SUPPORTED_IMAGES.contains(array.getClass().getSimpleName())) {
        results.add(array);
      }
    }
    return results;
  }
  
  /**
   * Gets image properties associated with the given PDS4 label.
   *  
   * @param pds4Label A pds4 label to parse.
   * 
   * @return An ImageProperties object that contains some image
   *  information about the given label.
   *  
   * @throws Exception If an error occurred during the parsing
   *  process.
   */
  public static ImageProperties getImageProperties(File pds4Label) 
      throws Exception {
    ObjectProvider objectAccess = new ObjectAccess();
    ProductObservational product = objectAccess.getProduct(pds4Label,
        ProductObservational.class);
    DisciplineArea disciplineArea = null;
    List<DisplaySettings> displaySettings = new ArrayList<DisplaySettings>();
    try {
      if (product.getObservationArea() != null) {
        disciplineArea = product.getObservationArea().getDisciplineArea();
        if (disciplineArea != null) {
          for (Object object : disciplineArea.getAnies()) {
            if (object instanceof DisplaySettings) {
              displaySettings.add((DisplaySettings) object);
            }
          }
        }
      }
    } catch (IndexOutOfBoundsException e) {
      String message = "Label has no such ObservationalArea";
      throw new Exception(message);
    }
    return new ImageProperties(product.getFileAreaObservationals(), 
        displaySettings);
  }
  
  public static ImageProperties getImageProperties(URL pds4Label) 
		  throws Exception {
	  try {
		  SSLContext context = SSLContext.getInstance("TLSv1.2");
		  context.init(null, null, new java.security.SecureRandom());
		  HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	  } catch (Exception e) {
		  throw new IOException ("Error while setting SSLSocket connection to TLSv1.2: " + e.getMessage());
	  }
	  ObjectProvider objectAccess = new ObjectAccess();
	  ProductObservational product = objectAccess.getProduct(pds4Label,
			  ProductObservational.class);
	  DisciplineArea disciplineArea = null;
	  List<DisplaySettings> displaySettings = new ArrayList<DisplaySettings>();
	  try {
		  if (product.getObservationArea() != null) {
			  disciplineArea = product.getObservationArea().getDisciplineArea();
			  if (disciplineArea != null) {
				  for (Object object : disciplineArea.getAnies()) {
					  if (object instanceof DisplaySettings) {
						  displaySettings.add((DisplaySettings) object);
					  }
				  }
			  }
		  }
	  } catch (IndexOutOfBoundsException e) {
		  String message = "Label has no such ObservationalArea";
		  throw new Exception(message);
	  }
	  return new ImageProperties(product.getFileAreaObservationals(), 
			  displaySettings);
  }
 
  public static File getFileFromURL(URL url, File outputDir) throws Exception {
	  try {
		  SSLContext context = SSLContext.getInstance("TLSv1.2");
		  context.init(null, null, new java.security.SecureRandom());
		  HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	  } catch (Exception e) {
		  throw new IOException ("Error while setting SSLSocket connection to TLSv1.2: " + e.getMessage());
	  }
	  if (url.getProtocol().startsWith("http")) {
		  String urlFilename = url.toString(); 
		  String fileName = urlFilename.substring(urlFilename.lastIndexOf('/') + 1);
		  InputStream in = url.openStream();
		  byte[] buffer = IOUtils.toByteArray(in);   		
		  in.close();

		  File targetFile = new File(outputDir + File.separator + fileName);
		  OutputStream outStream = new FileOutputStream(targetFile);
		  outStream.write(buffer);
		  outStream.close();
		  
		  return targetFile;        
	  } else {
		  // for "file:" or local file
		  return new File(url.toURI());
	  }
  }
  
  public static URL getParent(URL url) throws MalformedURLException, 
  URISyntaxException {
    URL parent = url.toURI().getPath().endsWith("/") ?
        url.toURI().resolve("..").toURL() :
          url.toURI().resolve(".").toURL();
    return parent;
  }
}
