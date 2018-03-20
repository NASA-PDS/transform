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
