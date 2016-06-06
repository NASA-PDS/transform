// Copyright 2006-2016, by the California Institute of Technology.
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

import java.util.List;

import gov.nasa.arc.pds.xml.generated.DisplaySettings;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;

/**
 * Class to hold image properties.
 * 
 * @author mcayanan
 *
 */
public class ImageProperties {
  
  /** List of FileAreaObservational elements. */
  private List<FileAreaObservational> fileAreas;
  
  /** List of DisplaySettings. */
  private List<DisplaySettings> displaySettings;
  
  /**
   * Default constructor.
   * 
   * @param fileAreas A list of FileAreaObservational objects.
   * @param displaySettings A list of DisplaySettings objects.
   */
  public ImageProperties(List<FileAreaObservational> fileAreas, 
      List<DisplaySettings> displaySettings) {
    this.fileAreas = fileAreas;
    this.displaySettings = displaySettings;
  }
  
  /**
   * 
   * @return the list of FileAreaObservational objects.
   */
  public List<FileAreaObservational> getFileAreas() {
    return fileAreas;
  }
  
  /**
   * 
   * @return the list of DisplaySettings objects.
   */
  public List<DisplaySettings> getDisplaySettings() {
    return displaySettings;
  }
}
