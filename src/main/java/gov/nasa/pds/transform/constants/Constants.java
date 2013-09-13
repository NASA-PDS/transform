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
package gov.nasa.pds.transform.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    COMMON_VALID_FORMATS.addAll(Arrays.asList(ImageIO.getReaderFormatNames()));
    COMMON_VALID_FORMATS.remove("pds");
    COMMON_VALID_FORMATS.remove("vicar");
    COMMON_VALID_FORMATS.remove("vic");
    COMMON_VALID_FORMATS.remove("isis");
    COMMON_VALID_FORMATS.remove("fits");
    COMMON_VALID_FORMATS.remove("jpeg 2000");
    COMMON_VALID_FORMATS.remove("jpeg2000");
    COMMON_VALID_FORMATS.remove("JPEG 2000");
    COMMON_VALID_FORMATS.remove("JPEG2000");
    COMMON_VALID_FORMATS.add("jp2");
    COMMON_VALID_FORMATS.add("JP2");
  }

  /**
   * Contains the valid transformation formats that apply to only PDS3 data
   * products.
   *
   */
  public static List<String> PDS3_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS3_VALID_FORMATS.addAll(COMMON_VALID_FORMATS);
  }

  /**
   * Contains the valid transformation formats that apply to only PDS4
   * data products.
   *
   */
  public static List<String> PDS4_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS4_VALID_FORMATS.addAll(COMMON_VALID_FORMATS);
    PDS4_VALID_FORMATS.add("pvl");
    PDS4_VALID_FORMATS.add("PVL");
    PDS4_VALID_FORMATS.add("html");
    PDS4_VALID_FORMATS.add("HTML");
    PDS4_VALID_FORMATS.add("html-structure-only");
    PDS4_VALID_FORMATS.add("HTML-STRUCTURE-ONLY");
    PDS4_VALID_FORMATS.add("csv");
    PDS4_VALID_FORMATS.add("CSV");
  }

  /**
   * This is used to only tell which transformations are PDS4 specific.
   *
   */
  public static List<String> PDS4_ONLY_VALID_FORMATS = new ArrayList<String>();
  static {
    PDS4_ONLY_VALID_FORMATS.add("pvl");
    PDS4_ONLY_VALID_FORMATS.add("PVL");
    PDS4_ONLY_VALID_FORMATS.add("html");
    PDS4_ONLY_VALID_FORMATS.add("HTML");
    PDS4_ONLY_VALID_FORMATS.add("html-structure-only");
    PDS4_ONLY_VALID_FORMATS.add("HTML-STRUCTURE-ONLY");
    PDS4_ONLY_VALID_FORMATS.add("csv");
    PDS4_ONLY_VALID_FORMATS.add("CSV");
  }

  /**
   * Contains all the valid transformation formats possible.
   *
   */
  public static List<String> ALL_VALID_FORMATS = new ArrayList<String>();
  static {
    ALL_VALID_FORMATS.addAll(PDS3_VALID_FORMATS);
    ALL_VALID_FORMATS.addAll(PDS4_VALID_FORMATS);
  }

  public static Map<String, String> STYLESHEETS = new HashMap<String, String>();

  static {
    STYLESHEETS.put("pvl", "pvl.xsl");
    STYLESHEETS.put("html", "html.xsl");
    STYLESHEETS.put("html-structure-only", "html-structure-only.xsl");
  }
}
