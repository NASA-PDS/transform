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
  public List<File> transform(File target, File outputDir, String format)
  throws TransformException;
  
  public List<File> transform(URL url, File outputDir, String format)
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
  public List<File> transform(File target, File outputDir, String format,
      String dataFile, int index) throws TransformException;
  
  public List<File> transform(URL target, File outputDir, String format,
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
