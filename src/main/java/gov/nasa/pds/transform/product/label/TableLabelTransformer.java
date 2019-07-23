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

import gov.nasa.arc.pds.xml.generated.ByteStream;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.table.DelimiterType;

/**
 * Interface class for a TableLabelTransformer.
 * 
 * @author mcayanan
 *
 * @param <T> Should be one of the PDS4 supported table objects.
 */
public interface TableLabelTransformer<T extends ByteStream> {

  /** Convenience wrapper class to convert a given table object
   *  to a Table_Delimited object with a comma field delimter.
   */
  public TableDelimited toTableDelimited(Object table);
  
  
  /**
   * Convenience wrapper class to convert a given table object
   * to a Table_Delimited object with the given field delimiter.
   * 
   * @param table The table object.
   * @param type The field delimiter to set.
   * 
   * @return The transformed Table_Delimited object.
   */
  public TableDelimited toTableDelimited(Object table, DelimiterType type);
  
  /** Converts the given table object to a Table_Delimited object with a 
   *  comma field delimter. 
   */
  public TableDelimited toTableDelimited(T table);
  
  /**
   * Converts the given table to a TableDelimited object.
   * 
   * @param table The table object to convert.
   * @param type The delimiter type to set.
   * 
   * @return The transformed Table_Delimited object.
   */
  public TableDelimited toTableDelimited(T table, DelimiterType type);
}
