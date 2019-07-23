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

import java.math.BigInteger;

import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.table.DelimiterType;

/**
 * Class to transform a non-CSV Table_Delimited object to a
 * CSV Table_Delimited object.
 * 
 * @author mcayanan
 *
 */
public class DelimitedTableLabelTransformer implements 
TableLabelTransformer<TableDelimited> {

  @Override
  public TableDelimited toTableDelimited(Object table) {
    return toTableDelimited(table, DelimiterType.COMMA);
  }

  @Override
  public TableDelimited toTableDelimited(Object table,
      DelimiterType type) {
    return toTableDelimited((TableDelimited) table, type);
  }
  

  @Override
  public TableDelimited toTableDelimited(TableDelimited table) {
    return toTableDelimited(table, DelimiterType.COMMA);
  }

  @Override
  public TableDelimited toTableDelimited(TableDelimited table,
      DelimiterType type) {
    if (!table.getFieldDelimiter().equals(type.getXmlType())) {
      table.setFieldDelimiter(type.getXmlType());
    }
    //Should always set the value to 0
    table.getOffset().setValue(BigInteger.valueOf(0));
    return table;
  }

}
