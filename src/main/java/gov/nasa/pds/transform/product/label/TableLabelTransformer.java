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
