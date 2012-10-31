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

import java.io.File;

/**
 * Interface for doing transformations.
 *
 * @author mcayanan
 *
 */
public interface PdsTransformer {

  /**
   * Perform the transformation.
   *
   * @param input Input file.
   * @param output Output file.
   * @param format Valid format file type.
   *
   * @throws TransformException
   */
  public void transform(File input, File output, String format)
  throws TransformException;
}
