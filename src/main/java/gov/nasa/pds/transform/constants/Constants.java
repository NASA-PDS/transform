// Copyright 2006-2017, by the California Institute of Technology.
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
package gov.nasa.pds.transform.constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

/**
 * Constants class.
 *
 * @author mcayanan
 *
 */
public class Constants {
  /**
   * Contains the valid format file types that can be used for
   * transformations.
   */
  public static List<String> COMMON_VALID_FORMATS = new ArrayList<String>();
  static {
    HashSet<String> set = new HashSet<String>();
    for (String format : Arrays.asList(ImageIO.getReaderFormatNames())) {
      set.add(format.toLowerCase());
    }
    COMMON_VALID_FORMATS.addAll(set);
    COMMON_VALID_FORMATS.remove("pds");
    COMMON_VALID_FORMATS.remove("pds4");
    COMMON_VALID_FORMATS.remove("vicar");
    COMMON_VALID_FORMATS.remove("vic");
    COMMON_VALID_FORMATS.remove("isis");
    COMMON_VALID_FORMATS.remove("fits");
    COMMON_VALID_FORMATS.remove("jpeg 2000");
    COMMON_VALID_FORMATS.remove("jpeg2000");
    COMMON_VALID_FORMATS.remove("mgn-fbidr");
    COMMON_VALID_FORMATS.remove("wbmp");
    COMMON_VALID_FORMATS.remove("bmp");
    COMMON_VALID_FORMATS.remove("raw");
    COMMON_VALID_FORMATS.add("jp2");
    COMMON_VALID_FORMATS.add("csv");
  }

  public static Map<String, String> STYLESHEETS = new HashMap<String, String>();
  static {
    Properties props = new Properties();
    try {
      URL propertyFile = Constants.class.getResource("stylesheets.properties");
      InputStream in = propertyFile.openStream();
      props.load(in);
     } catch (IOException io) {
         throw new RuntimeException(io.getMessage());
     }
    for(Object key : props.keySet()) {
      STYLESHEETS.put(key.toString(), props.getProperty(key.toString()));
    }
  }

  public static Map<String, File> EXTERNAL_PROGRAMS = new HashMap<String, File>();
  static {
    Properties props = new Properties();
    try {
      URL propertyFile = Constants.class.getResource("external-programs.properties");
      InputStream in = propertyFile.openStream();
      props.load(in);
     } catch (IOException io) {
         throw new RuntimeException(io.getMessage());
     }
    for(Object key : props.keySet()) {
      File program = new File(
          System.getProperties().getProperty("external.programs.home"),
          props.getProperty(key.toString()));
      EXTERNAL_PROGRAMS.put(key.toString(), program);
    }
  }

  public static List<String> PDS3_ONLY_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS3_ONLY_VALID_FORMATS.add("pds");
    PDS3_ONLY_VALID_FORMATS.add("pds4-label");
  }

  /**
   * This is used to only tell which transformations are PDS4 specific.
   *
   */
  public static List<String> PDS4_ONLY_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS4_ONLY_VALID_FORMATS.add("pvl");
    PDS4_ONLY_VALID_FORMATS.add("html");
    PDS4_ONLY_VALID_FORMATS.add("html-structure-only");
    PDS4_ONLY_VALID_FORMATS.add("pds3-label");
    PDS4_ONLY_VALID_FORMATS.add("fits");
  }

  /**
   * Contains the valid transformation formats that apply to only PDS3 data
   * products.
   *
   */
  public static List<String> PDS3_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS3_VALID_FORMATS.addAll(COMMON_VALID_FORMATS);
    PDS3_VALID_FORMATS.addAll(PDS3_ONLY_VALID_FORMATS);
  }

  /**
   * Contains the valid transformation formats that apply to only PDS4
   * data products.
   *
   */
  public static List<String> PDS4_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS4_VALID_FORMATS.addAll(COMMON_VALID_FORMATS);
    PDS4_VALID_FORMATS.addAll(PDS4_ONLY_VALID_FORMATS);
  }
  
  /**
   * Contains a list of supported table objects.
   */
  public static List<String> SUPPORTED_TABLES = new ArrayList<String>();
  static {
    SUPPORTED_TABLES.add(gov.nasa.arc.pds.xml.generated.TableBinary.class.getSimpleName());
    SUPPORTED_TABLES.add(gov.nasa.arc.pds.xml.generated.TableCharacter.class.getSimpleName());
    SUPPORTED_TABLES.add(gov.nasa.arc.pds.xml.generated.TableDelimited.class.getSimpleName());    
  }
  
  /**
   * Contains a list of supported image objects.
   */
  public static List<String> SUPPORTED_IMAGES = new ArrayList<String>();
  static {
    SUPPORTED_IMAGES.add(gov.nasa.arc.pds.xml.generated.Array2DImage.class.getSimpleName());
    SUPPORTED_IMAGES.add(gov.nasa.arc.pds.xml.generated.Array3DImage.class.getSimpleName());
    SUPPORTED_IMAGES.add(gov.nasa.arc.pds.xml.generated.Array3DSpectrum.class.getSimpleName());
  }
  
  /**
   * Contains a list of supported objects.
   */
  public static List<String> SUPPORTED_OBJECTS = new ArrayList<String>();
  static {
    SUPPORTED_OBJECTS.addAll(SUPPORTED_TABLES);
    SUPPORTED_OBJECTS.addAll(SUPPORTED_IMAGES);
  }
}
