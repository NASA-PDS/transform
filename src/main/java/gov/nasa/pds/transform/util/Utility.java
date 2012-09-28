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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}
