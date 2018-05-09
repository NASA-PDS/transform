// Copyright 2006-2018, by the California Institute of Technology.
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
package gov.nasa.pds.transform.product.label;

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
    table.getOffset().setValue(0);
    return table;
  }

}
