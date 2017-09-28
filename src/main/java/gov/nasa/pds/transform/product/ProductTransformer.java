// Copyright 2006-2015, by the California Institute of Technology.
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
import java.net.URL;
import java.net.URISyntaxException;


/**
 * Interface to perform transformations on PDS data products.
 *
 * @author mcayanan
 *
 */
public interface ProductTransformer {

  /**
   * Transform a single target. This will transform
   * the first image/table found within the first data file found.
   *
   * @param target file specification to the PDS label.
   * @param outputDir directory where the output file will be
   * written.
   * @param format Valid format file type.
   *
   * @return The resulting output file.
   *
   * @throws TransformException If an error occurred during the
   * transformation process.
   */
  public File transform(File target, File outputDir, String format)
  throws TransformException;
  
  public File transform(URL url, File outputDir, String format)
		  throws TransformException, URISyntaxException, Exception;

  /**
   * Transform a single target.
   *
   * @param target  file specification to the PDS label.
   * @param outputDir directory where the output file will be written.
   * @param format Valid format file type.
   * @param dataFile Tells the tool which data file to transform.
   *  If this argument is an empty string, the default is to transform
   *  the first data file found in the label.
   * @param index The index of the data. This tells the tool which image
   * or table to transform if there are multiple images/tables within a
   * single data file.
   *
   * @return The resulting output file.
   *
   * @throws TransformException If an error occurred during the
   * transformation process.
   */
  public File transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException;
  
  public File transform(URL target, File outputDir, String format,
	      String dataFile, int index) 
  throws TransformException, URISyntaxException, Exception;

  /**
   * Transform multiple targets. This will transform
   * the first image/table found within the first data file found in
   * each target.
   *
   * @param targets a list of URL specifications to the PDS labels.
   * @param outputDir directory where the output file will be
   * written.
   * @param format Valid format file type.
   *
   * @return The resulting output files.
   *
   * @throws TransformException If an error occurred during the
   * transformation process.
   */
  public List<File> transform(List<URL> targets, File outputDir, String format)
		  throws TransformException, URISyntaxException, Exception;


  /**
   * Transform all images/tables found in the given target.
   *
   * @param target  file specification to the PDS label.
   * @param outputDir directory where the output file will be written.
   * @param format Valid format file type.
   *
   * @return The resulting output files.
   *
   * @throws TransformException If an error occurred during the
   * transformation process.
   */
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException;
  
  public List<File> transformAll(URL url, File outputDir, String format)
	      throws TransformException, URISyntaxException, Exception;

  /**
   * Transform all images/tables found in each target.
   *
   * @param targets a list of URL specifications to the PDS labels.
   * @param outputDir directory where the output file will be written.
   * @param format Valid format file type.
   *
   * @return The resulting output files.
   *
   * @throws TransformException If an error occurred during the
   * transformation process.
   */
  public List<File> transformAll(List<URL> targets, File outputDir, String format)
	      throws TransformException, URISyntaxException, Exception;
}
