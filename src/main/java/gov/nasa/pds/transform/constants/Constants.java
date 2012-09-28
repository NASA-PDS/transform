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
package gov.nasa.pds.transform.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  public static List<String> VALID_FORMATS = new ArrayList<String>();

  static {
    VALID_FORMATS.addAll(Arrays.asList(ImageIO.getReaderFormatNames()));
    VALID_FORMATS.remove("pds");
    VALID_FORMATS.remove("vicar");
    VALID_FORMATS.remove("vic");
    VALID_FORMATS.remove("isis");
    VALID_FORMATS.remove("fits");
    VALID_FORMATS.add("pvl");
    VALID_FORMATS.add("PVL");
  }
}
