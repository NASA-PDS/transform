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
import gov.nasa.pds.transform.constants.Constants;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;

/**
 * Transformer Factory class that determines whether to transform a
 * PDS3 or PDS4 product.
 *
 * @author mcayanan
 *
 */
public class ProductTransformerFactory {
  /** Holds the factory object. */
  private static ProductTransformerFactory factory = null;

  /** Private constructor. */
  private ProductTransformerFactory() {}

  private final String OVERWRITE_PROP = "overwrite.output";

  /** Gets an instance of the factory.
   *
   */
  public static synchronized ProductTransformerFactory getInstance() {
    if (factory == null) {
      factory = new ProductTransformerFactory();
    }
    return factory;
  }

  /**
   * Gets an instance of a Transformer. If the given label ends in ".xml",
   * then this will return a PDS4 Transformer object. Otherwise, a PDS3
   * Transformer object will be returned.
   *
   * @param target A PDS3 or PDS4 label file.
   * @param format The transformation format.
   *
   * @return The appropriate Transformer object.
   *
   * @throws TransformException If the input label could not be opened or
   * the format type is not one of the valid formats.
   */
  public ProductTransformer newInstance(File target, String format)
  throws TransformException {
    if (!target.exists()) {
      throw new TransformException("Target not found: " + target);
    }
    boolean overwrite = Boolean.parseBoolean(
        System.getProperty(OVERWRITE_PROP));
    String extension = FilenameUtils.getExtension(target.toString());
    if (extension.equalsIgnoreCase("xml")) {
      if (Constants.PDS4_VALID_FORMATS.contains(format)) {
        if ("csv".equals(format)) {
          return new Pds4TableTransformer(overwrite);
        } else if (Constants.STYLESHEETS.containsKey(format)) {
          return new StylesheetTransformer(overwrite);
        } else if ("pds3-label".equals(format)) {
          return new Pds4LabelTransformer(overwrite);
        } else {
          return new Pds4ImageTransformer(overwrite);
        }
      } else {
        throw new TransformException("Format value '" + format
            + "' is not one of the valid formats for a PDS4 transformation: "
            + Constants.PDS4_VALID_FORMATS);
      }
    } else {
      if (Constants.PDS3_VALID_FORMATS.contains(format)) {
        if ("pds4-label".equals(format)) {
          return new Pds3LabelTransformer(overwrite);
        } else if ("csv".equals(format)) {
          return new Pds3TableTransformer(overwrite);
        }  else {
          return new Pds3ImageTransformer(overwrite);
        }
      } else {
        throw new TransformException("Format value '" + format
            + "' is not one of the valid formats for a PDS3 transformation: "
            + Constants.PDS3_VALID_FORMATS);
      }
    }
  }
  
  public ProductTransformer newInstance(URL target, String format)
		  throws TransformException {
	  boolean overwrite = Boolean.parseBoolean(
			  System.getProperty(OVERWRITE_PROP));
	  String extension = FilenameUtils.getExtension(target.toString());
	  if (extension.equalsIgnoreCase("xml")) {
		  if (Constants.PDS4_VALID_FORMATS.contains(format)) {
			  if ("csv".equals(format)) {
				  return new Pds4TableTransformer(overwrite);
			  } else if (Constants.STYLESHEETS.containsKey(format)) {
				  return new StylesheetTransformer(overwrite);
			  } else if ("pds3-label".equals(format)) {
          return new Pds4LabelTransformer(overwrite);
        } else {
          return new Pds4ImageTransformer(overwrite);
        }
		  } else {
			  throw new TransformException("Format value '" + format
					  + "' is not one of the valid formats for a PDS4 transformation: "
					  + Constants.PDS4_VALID_FORMATS);
		  }
	  } else {
		  if (Constants.PDS3_VALID_FORMATS.contains(format)) {
			  if ("pds4-label".equals(format)) {
				  return new Pds3LabelTransformer(overwrite);
			  } else if ("csv".equals(format)) {
				  return new Pds3TableTransformer(overwrite);
			  }  else {
				  return new Pds3ImageTransformer(overwrite);
			  }
		  } else {
			  throw new TransformException("Format value '" + format
					  + "' is not one of the valid formats for a PDS3 transformation: "
					  + Constants.PDS3_VALID_FORMATS);
		  }
	  }
  }
}
