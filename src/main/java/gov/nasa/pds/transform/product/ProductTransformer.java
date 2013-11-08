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

import java.io.File;
import java.util.List;

/**
 * Interface to perform transformations on PDS data products.
 *
 * @author mcayanan
 *
 */
public interface ProductTransformer {

  /**
   * Transform a single target.
   *
   * @param target file specification to the PDS label.
   * @param outputDir directory where the output file will be
   * written.
   * @param format Valid format file type.
   *
   * @throws TransformException
   */
  public void transform(File target, File outputDir, String format)
  throws TransformException;

  /**
   * Transform multiple targets.
   *
   * @param targets a list of file specifications to the PDS labels.
   * @param outputDir directory where the output file will be
   * written.
   * @param format Valid format file type.
   *
   * @throws TransformException
   */
  public void transform(List<File> targets, File outputDir, String format)
  throws TransformException;
}
