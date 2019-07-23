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

package gov.nasa.pds.transform.product.label;

import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.transform.TransformException;

/**
 * Class that determines which TableLabelTransformer sub-class
 * to instantiate based on the given object.
 * 
 * @author mcayanan
 *
 */
public class TableLabelTransformerFactory {
  /** Holds the factory object. */
  private static TableLabelTransformerFactory factory = null;

  /** Private constructor. */
  private TableLabelTransformerFactory() {}

  /** 
   * Gets an instance of the factory.
   */
  public static synchronized TableLabelTransformerFactory getInstance() {
    if (factory == null) {
      factory = new TableLabelTransformerFactory();
    }
    return factory;
  }
  
  /**
   * Instantiates a new TableLabelTransformer object based on the given
   * table object.
   * 
   * @param tableObject Must be either Table_Binary, 
   * Table_Character, or Table_Delimited.
   * 
   * @return a TableLabelTransformer object.
   * 
   * @throws TransformException If the given object is not one of
   * the supported PDS4 table objects.
   */
  public TableLabelTransformer<?> newInstance(Object tableObject)
      throws TransformException {
    if (tableObject instanceof TableBinary) {
      return new BinaryTableLabelTransformer();
    } else if (tableObject instanceof TableCharacter) {
      return new CharacterTableLabelTransformer();
    } else if (tableObject instanceof TableDelimited) {
      return new DelimitedTableLabelTransformer();
    } else {
      throw new TransformException(
          "Table Object must be 'TableBinary', 'TableCharacter' or 'TableDelimited'");
    }
  }
}
