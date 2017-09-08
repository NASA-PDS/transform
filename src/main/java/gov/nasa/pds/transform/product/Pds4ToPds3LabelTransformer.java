// Copyright 2006-2017, by the California Institute of Technology.
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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.Array3DSpectrum;
import gov.nasa.arc.pds.xml.generated.AxisArray;
import gov.nasa.arc.pds.xml.generated.Field;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FileArea;
import gov.nasa.arc.pds.xml.generated.FileAreaBrowse;
import gov.nasa.arc.pds.xml.generated.FileAreaInventory;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.FileAreaSIPDeepArchive;
import gov.nasa.arc.pds.xml.generated.FileAreaTransferManifest;
import gov.nasa.arc.pds.xml.generated.Group;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.GroupFieldCharacter;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.InformationPackageComponent;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.Product;
import gov.nasa.arc.pds.xml.generated.ProductAIP;
import gov.nasa.arc.pds.xml.generated.ProductBrowse;
import gov.nasa.arc.pds.xml.generated.ProductCollection;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.ProductSIPDeepArchive;
import gov.nasa.arc.pds.xml.generated.TableBase;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.tools.dict.parser.DictIDFactory;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.Numeric;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.PointerStatementFactory;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.TextString;
import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.TransformLauncher;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.product.pds3.OrderedObjectStatement;

/**
 * Class to transform a PDS4 label to a PDS3 label.
 * 
 * @author mcayanan
 *
 */
public class Pds4ToPds3LabelTransformer {
  protected static Logger log = Logger.getLogger(
      TransformLauncher.class.getName());
  
  private final String OBJECT_NAME_PREFIX = "TRANSFORM";
  private File pds4Label;
  private Label label;
  
  /**
   * Constructor.
   * 
   * @param output The location of where to store the results.
   */
  public Pds4ToPds3LabelTransformer(File output) {
    label = new Label(output);
  }
  
  /**
   * Transform the given PDS4 label into a PDS3 label.
   * 
   * @param pds4Label The PDS4 label to transform.
   * 
   * @return a Label object representation of the PDS3 label.
   * 
   * @throws TransformException If an error occurred during transformation.
   */
  public Label transform(File pds4Label) throws TransformException {
    this.pds4Label = pds4Label;
    ObjectProvider objectAccess = null;
    try {
      objectAccess = new ObjectAccess(pds4Label);
    } catch (Exception e) {
      //Ignore. Shouldn't happen
    }
    label.addStatement(new AttributeStatement(label, 
        "PDS_VERSION_ID", new TextString("PDS3")));
    //Get the pointer statements
    Map<String, Integer> objectCounter = new LinkedHashMap<String, Integer>();
    List<FileArea> fileAreas = new ArrayList<FileArea>();
    try {
      fileAreas = getFileAreas(pds4Label, objectAccess);
      // Get the pointers
      for (FileArea fileArea : fileAreas) {
        List<Object> objects = objectAccess.getTablesAndImages(fileArea);
        for (Object dataObject : objects) {
          Integer counter = null;
          if (dataObject instanceof TableBase) {
            counter = objectCounter.get("TABLE");
            if (counter == null) {
              counter = new Integer(1);
            }
            objectCounter.put("TABLE", new Integer(counter.intValue() + 1));
          } else if (dataObject instanceof TableDelimited) {
            counter = objectCounter.get("SPREADSHEET");
            if (counter == null) {
              counter = new Integer(1);
            }
            objectCounter.put("SPREADSHEET", new Integer(counter.intValue() + 1));
          } else if (dataObject instanceof Array) {
            counter = objectCounter.get("IMAGE");
            if (counter == null) {
              counter = new Integer(1);
            }
            objectCounter.put("IMAGE", new Integer(counter.intValue() + 1));
          }
          label.addStatement(createPointer(dataObject, counter, getDataFile(fileArea)));    
        }
      }
      // Map the objects
      objectCounter.clear();
      for (FileArea fileArea : fileAreas) {
        List<Object> objects = objectAccess.getTablesAndImages(fileArea);
        boolean addFileInfo = true;
        for (Object dataObject : objects) {
          Integer counter = null;
          if (dataObject instanceof TableBase) {
            counter = objectCounter.get("TABLE");
            if (counter == null) {
              counter = new Integer(1);
            }
            objectCounter.put("TABLE", new Integer(counter.intValue() + 1));
          } else if (dataObject instanceof TableDelimited) {
            counter = objectCounter.get("SPREADSHEET");
            if (counter == null) {
              counter = new Integer(1);
            }
            objectCounter.put("SPREADSHEET", new Integer(counter.intValue() + 1));
          } else if (dataObject instanceof Array) {
            counter = objectCounter.get("IMAGE");
            if (counter == null) {
              counter = new Integer(1);
            }
            objectCounter.put("IMAGE", new Integer(counter.intValue() + 1));
          }
          OrderedObjectStatement objectStmt = mapObject(dataObject, counter);
          if (addFileInfo) {
            TextString recordType = null;
            if (dataObject instanceof TableDelimited) {
              recordType = new TextString("STREAM");
            } else {
              recordType = new TextString("FIXED_LENGTH");
            }
            label.addStatement(new AttributeStatement(label, "RECORD_TYPE", recordType));
            TextString recordBytes = null;
            if (objectStmt.hasAttribute(DictIDFactory.createElementDefId("SAMPLE_BITS")) && 
                objectStmt.hasAttribute(DictIDFactory.createElementDefId("LINE_SAMPLES"))) {
              Value sampleBits = objectStmt.getAttribute("SAMPLE_BITS").getValue();
              Value lineSamples = objectStmt.getAttribute("LINE_SAMPLES").getValue();
              int value = (Integer.parseInt(lineSamples.toString())) * (Integer.parseInt(sampleBits.toString()) / 8);
              recordBytes = new TextString(Integer.toString(value));
            } else if (objectStmt.hasAttribute(DictIDFactory.createElementDefId("ROW_BYTES"))) {
              recordBytes = new TextString(objectStmt.getAttribute("ROW_BYTES").getValue().toString());
            } else {
              log.log(new ToolsLogRecord(ToolsLevel.SEVERE, "Cannot determine RECORD_BYTES value.", pds4Label));
            }
            label.addStatement(new AttributeStatement(label, "RECORD_BYTES", recordBytes));
            addFileInfo = false;
          }
          if (objectStmt != null) {
            label.addStatement(objectStmt);
          }
        }
      }
    } catch (ParseException p) {
      throw new TransformException("Error while parsing label: "
          + p.getMessage());
    }
    return label;
  }
  
  /**
   * Maps the data object to a PDS3 Statement.
   * 
   * @param dataObject The data object to map.
   * @param counter Used to uniquely name the PDS3 object 
   *   (i.e. TRANSFORM1_IMAGE, TRANSFORM2_TABLE)
   * 
   * @return A PDS3 Object Statement.
   * 
   * @throws TransformException If an error occurred during transformation.
   */
  private OrderedObjectStatement mapObject(Object dataObject, Integer counter)
      throws TransformException {
    OrderedObjectStatement objectStatement = null;
    String objectNamePrefix = "TRANSFORM" + counter.toString();
    if (dataObject instanceof Array) {
      objectStatement = mapArray((Array) dataObject, objectNamePrefix + "_IMAGE");
      if (objectStatement.getAttribute("LINE_SAMPLES") == null) {
        throw new TransformException("Missing LINE_SAMPLES from the label.");
      } else if (objectStatement.getAttribute("SAMPLE_BITS") == null) {
        throw new TransformException("Missing SAMPLE_BITS from the label.");
      }
    } else if (dataObject instanceof TableBase) {
      objectStatement = mapTable((TableBase) dataObject, objectNamePrefix + "_TABLE");

    } else if (dataObject instanceof TableDelimited) {
      objectStatement = mapDelimitedTable((TableDelimited) dataObject, objectNamePrefix + "_SPREADSHEET");
    }
    return objectStatement;
  }
  
  /**
   * Maps the PDS4 table object to a PDS3 table object.
   * 
   * @param tableBase The table to map.
   * @param objectName The name to give the PDS3 table object.
   * 
   * @return A PDS3 Object Statement of the table.
   * 
   * @throws TransformException If an error occurred during transformation.
   */
  private OrderedObjectStatement mapTable(TableBase tableBase, String objectName)
      throws TransformException {
    OrderedObjectStatement objectStatement = new OrderedObjectStatement(label, objectName);
    String name = "";
    if (tableBase.getName() != null) {
      name = tableBase.getName().toUpperCase();
    } else if (tableBase.getLocalIdentifier() != null) {
      name = tableBase.getLocalIdentifier().toUpperCase();
    }
    if (!name.isEmpty()) {
      objectStatement.addStatement(new AttributeStatement(label, "NAME", new TextString(name)));
    }
    TextString interchangeFormat = null;
    TextString rows = new TextString(Integer.toString(tableBase.getRecords()));
    TextString columns = null;
    TextString rowBytes = null;
    List<Object> fields = new ArrayList<Object>();
    boolean mappableTable = true;
    if (tableBase instanceof TableCharacter) {
      interchangeFormat = new TextString("ASCII");
      TableCharacter tc = (TableCharacter) tableBase;
      if (tc.getRecordCharacter() != null) {
        columns = new TextString(Integer.toString(tc.getRecordCharacter().getFields()));
        if (tc.getRecordCharacter().getRecordLength() != null) {
          rowBytes = new TextString(Integer.toString(tc.getRecordCharacter().getRecordLength().getValue()));
        } else {
          throw new TransformException("Missing 'record_length' element in the Record_Character area.");
        }
      }
      fields = tc.getRecordCharacter().getFieldCharactersAndGroupFieldCharacters();
    } else if (tableBase instanceof TableBinary) {
      interchangeFormat = new TextString("BINARY");
      TableBinary tb = (TableBinary) tableBase;
      columns = new TextString(Integer.toString(tb.getRecordBinary().getFields()));
      if (tb.getRecordBinary() != null) {
        columns = new TextString(Integer.toString(tb.getRecordBinary().getFields()));
        if (tb.getRecordBinary().getRecordLength() != null) {
          rowBytes = new TextString(Integer.toString(tb.getRecordBinary().getRecordLength().getValue()));
        } else {
          throw new TransformException("Missing 'record_length' element in the Record_Binary area.");
        }
      }
      fields = tb.getRecordBinary().getFieldBinariesAndGroupFieldBinaries();
    } else {
      mappableTable = false;
    }
    if (mappableTable) {
      objectStatement.addStatement(new AttributeStatement(label, "INTERCHANGE_FORMAT", interchangeFormat));
      objectStatement.addStatement(new AttributeStatement(label, "ROWS", rows));
      objectStatement.addStatement(new AttributeStatement(label, "ROW_BYTES", rowBytes));
      objectStatement.addStatement(new AttributeStatement(label, "COLUMNS", columns));
      List<OrderedObjectStatement> columnObjects = mapFields(fields);
      for (OrderedObjectStatement colObject : columnObjects) {
        objectStatement.addStatement(colObject);
      }
    }
    return objectStatement;
  }
  
  /**
   * Maps the PDS4 delimited table object to a PDS3 Spreadsheet object.
   * 
   * @param tableBase The PDS4 Delimited Table object. 
   * @param objectName The name to give the PDS3 spreadsheet object.
   * 
   * @return A PDS3 Object Statement of the spreadsheet.
   * 
   * @throws TransformException If an error occurred during transformation.
   */
  private OrderedObjectStatement mapDelimitedTable(TableDelimited tableDelimited, 
      String objectName) throws TransformException {
    OrderedObjectStatement table = new OrderedObjectStatement(label, objectName);
    String name = "";
    if (tableDelimited.getName() != null) {
      name = tableDelimited.getName().toUpperCase();
    } else if (tableDelimited.getLocalIdentifier() != null) {
      name = tableDelimited.getLocalIdentifier().toUpperCase();
    }
    if (!name.isEmpty()) {
      table.addStatement(new AttributeStatement(label, "NAME", new TextString(name)));
    }
    TextString interchangeFormat = null;
    TextString rows = new TextString(Integer.toString(tableDelimited.getRecords()));
    TextString columns = null;
    TextString rowBytes = null;
    TextString delimiter = null;
    interchangeFormat = new TextString("ASCII");
    if ("COMMA".equalsIgnoreCase(tableDelimited.getFieldDelimiter())) {
      delimiter = new TextString("COMMA");
    } else if ("HORIZONTAL TAB".equalsIgnoreCase(tableDelimited.getFieldDelimiter())) {
      delimiter = new TextString("TAB");
    } else if ("SEMICOLON".equalsIgnoreCase(tableDelimited.getFieldDelimiter())) {
      delimiter = new TextString("SEMICOLON");
    } else if ("VERTICAL BAR".equalsIgnoreCase(tableDelimited.getFieldDelimiter())) {
      delimiter = new TextString("VERTICAL_BAR");
    } else {
      throw new TransformException(
          "Unrecognized field delimiter value '"
              + tableDelimited.getFieldDelimiter() + "'.");
    }
    if (tableDelimited.getRecordDelimited() != null) {
      columns = new TextString(Integer.toString(
          tableDelimited.getRecordDelimited().getFields()));
      if (tableDelimited.getRecordDelimited().getMaximumRecordLength() != null) {
        rowBytes = new TextString(Integer.toString(
            tableDelimited.getRecordDelimited().getMaximumRecordLength().getValue()));
      } else {
        //Default to 10,000 for now since ROW_BYTES is required in PDS3
        rowBytes = new TextString("10000");
        log.log(new ToolsLogRecord(ToolsLevel.WARNING, 
            "No maximum_record_length element found. Setting ROW_BYTES to 10000.", pds4Label));
      }
    }
    table.addStatement(new AttributeStatement(label, "INTERCHANGE_FORMAT", interchangeFormat));
    table.addStatement(new AttributeStatement(label, "FIELD_DELIMITER", delimiter));
    table.addStatement(new AttributeStatement(label, "ROWS", rows));
    table.addStatement(new AttributeStatement(label, "ROW_BYTES", rowBytes));
    table.addStatement(new AttributeStatement(label, "FIELDS", columns));
    List<Object> fields = tableDelimited.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds();
    List<OrderedObjectStatement> columnObjects = mapFields(fields);
    for (OrderedObjectStatement colObject : columnObjects) {
      table.addStatement(colObject);
    }
    return table;
  }
  
  /**
   * Maps the given fields to its equivalent PDS3 column or container object.
   * 
   * @param fields A list of field objects to map.
   * 
   * @return A list of the transformed column and/or container objects.
   * 
   * @throws TransformException If an error occurred during transformation.
   */
  private List<OrderedObjectStatement> mapFields(List<Object> fields) throws TransformException {
    List<OrderedObjectStatement> columns = new ArrayList<OrderedObjectStatement>();
    for (Object fieldObject : fields) {
      if (fieldObject instanceof Field) {
        OrderedObjectStatement column = mapField((Field) fieldObject);
        columns.add(column);
      } else if (fieldObject instanceof Group) {
        OrderedObjectStatement container = mapGroup((Group) fieldObject); 
        columns.add(container);
      }
    }
    return columns;
  }
  
  /**
   * Map the given field to its equivalent PDS3 column object.
   * 
   * @param field The PDS4 field object.
   * @return An object statement of the PDS3 column or field.
   */
  private OrderedObjectStatement mapField(Field field) {
    OrderedObjectStatement column = null;
    if (field instanceof FieldDelimited) {
      column = new OrderedObjectStatement(label, "FIELD");
    } else {
      column = new OrderedObjectStatement(label, "COLUMN");
    }
    column.addStatement(new AttributeStatement(label, "NAME", 
        new TextString(field.getName().toUpperCase())));
    if (field.getFieldNumber() != null) {
      column.addStatement(new AttributeStatement(label, "COLUMN_NUMBER", 
          new TextString(field.getFieldNumber().toString())));
    }
    String dataType = "";
    TextString startByte = null;
    TextString bytes = null;
    PackedDataFields packedDataFields = null;
    if (field instanceof FieldCharacter) {
      FieldCharacter fc = (FieldCharacter) field;
      if (fc.getFieldLocation() != null) {
        startByte = new TextString(Integer.toString(fc.getFieldLocation().getValue()));
      }
      if (fc.getFieldLength() != null) {
        bytes = new TextString(Integer.toString(fc.getFieldLength().getValue()));
      }
      dataType = fc.getDataType();
    } else if (field instanceof FieldBinary) {
      FieldBinary fb = (FieldBinary) field;
      if (fb.getFieldLocation() != null) {
        startByte = new TextString(Integer.toString(fb.getFieldLocation().getValue()));
      }
      if (fb.getFieldLength() != null) {
        bytes = new TextString(Integer.toString(fb.getFieldLength().getValue()));
      }
      dataType = fb.getDataType();
      packedDataFields = fb.getPackedDataFields();
    } else if (field instanceof FieldDelimited) {
      FieldDelimited fd = (FieldDelimited) field;
      dataType = fd.getDataType();
      //FIXME: PDS3 FIELD objects require the BYTES keyword, which maps
      //to the PDS4 max field length, but this is an optional keyword.
      //Default to 256 bytes for now if the max field length is not defined.
      if (fd.getMaximumFieldLength() != null) {
        bytes = new TextString(Integer.toString(fd.getMaximumFieldLength().getValue())); 
      } else {
        bytes = new TextString("256");
        log.log(new ToolsLogRecord(ToolsLevel.WARNING, 
            "Missing 'maximum_field_length' element in the Field_Delimited area. Setting 'BYTES' to 256 in the 'FIELD' object.", pds4Label));
      }
    }
    List<AttributeStatement> dataTypeStmts = mapDataType(dataType, field);
    for (AttributeStatement a : dataTypeStmts) {
      column.addStatement(a);
    }
    if (startByte != null) {
      column.addStatement(new AttributeStatement(label, "START_BYTE", startByte));
    }
    if (bytes != null) {
      column.addStatement(new AttributeStatement(label, "BYTES", bytes));
    }
    if (packedDataFields != null) {
      List<OrderedObjectStatement> bitColumns = mapPackedDataFields(packedDataFields);
      for (OrderedObjectStatement bc : bitColumns) {
        column.addStatement(bc);
      }
    }
    return column;
  }
  
  /**
   * Maps the given Packed Data Field object to its equivalent PDS3 bit columns.
   * 
   * @param packedDataFields The Packed Data Field object to map.
   * 
   * @return A list of PDS3 bit column objects.
   */
  private List<OrderedObjectStatement> mapPackedDataFields(PackedDataFields packedDataFields) {
    List<OrderedObjectStatement> bitColumns = new ArrayList<OrderedObjectStatement>();
    for (FieldBit fieldBit : packedDataFields.getFieldBits()) {
      TextString name = new TextString(fieldBit.getName());
      TextString bitDataType = null;
      if ("SignedBitString".equalsIgnoreCase(fieldBit.getDataType())) {
        bitDataType = new TextString("MSB_INTEGER");
      } else if ("UnsignedBitString".equalsIgnoreCase(fieldBit.getDataType())) {
        //Should be UnsignedBitString
        bitDataType = new TextString("MSB_UNSIGNED_INTEGER");
      } else {
        log.log(new ToolsLogRecord(ToolsLevel.SEVERE, 
            "Field_Bit element has an invalid data type '" + fieldBit.getDataType(), pds4Label));
      }
      TextString startBit = new TextString(Integer.toString(fieldBit.getStartBit()));
      
      int bits = (fieldBit.getStopBit() - fieldBit.getStartBit()) + 1;
      OrderedObjectStatement bitColumn = new OrderedObjectStatement(label, "BIT_COLUMN");
      bitColumn.addStatement(new AttributeStatement(label, "NAME", name));
      bitColumn.addStatement(new AttributeStatement(label, "DATA_TYPE", bitDataType));
      bitColumn.addStatement(new AttributeStatement(label, "START_BIT", startBit));
      bitColumn.addStatement(new AttributeStatement(label, "BITS", new TextString(Integer.toString(bits))));
      bitColumns.add(bitColumn);
    }
    return bitColumns;
  }
  
  /**
   * Maps the given group field objects to a PDS3 container object.
   * 
   * @param group The group field object to map.
   * 
   * @return An object statement of the PDS3 container object.
   * 
   * @throws TransformException If an error occurred during transformation.
   */
  private OrderedObjectStatement mapGroup(Group group) throws TransformException {
    List<Object> childFields = new ArrayList<Object>();
    OrderedObjectStatement container = new OrderedObjectStatement(label, "CONTAINER");
    TextString repetitions = null;
    TextString startByte = null;
    TextString bytes = null;
    TextString name = null;
    if (group instanceof GroupFieldCharacter) {
      GroupFieldCharacter fc = (GroupFieldCharacter) group;
      if (fc.getGroupLocation() != null) {
        startByte = new TextString(Integer.toString(fc.getGroupLocation().getValue()));
      }
      if (fc.getGroupLength() != null && fc.getRepetitions() != 0) {
        bytes = new TextString(Integer.toString(fc.getGroupLength().getValue() / fc.getRepetitions()));
        repetitions = new TextString(Integer.toString(fc.getRepetitions()));
      }
      if (fc.getName() != null) {
        name = new TextString(fc.getName());
      }
      childFields = fc.getFieldCharactersAndGroupFieldCharacters();
    } else if (group instanceof GroupFieldBinary) {
      GroupFieldBinary fb = (GroupFieldBinary) group;
      if (fb.getGroupLocation() != null) {
        startByte = new TextString(Integer.toString(fb.getGroupLocation().getValue()));
      }
      if (fb.getGroupLength() != null && fb.getRepetitions() != 0) {
        bytes = new TextString(Integer.toString(fb.getGroupLength().getValue() / fb.getRepetitions()));
        repetitions = new TextString(Integer.toString(fb.getRepetitions()));
      }
      if (fb.getName() != null) {
        name = new TextString(fb.getName());
      }
      childFields = fb.getFieldBinariesAndGroupFieldBinaries();
    } else if (group instanceof GroupFieldDelimited) {
      //Issue here is that the model does not list 'group_length' as a valid element and it is needed
      //to calculate the "BYTES" value attribute for a PDS3 CONTAINER.
      throw new TransformException("Transformations of Group_Field_Delimited elements is not supported at this time.");
    }
    if (startByte != null) {
      container.addStatement(new AttributeStatement(label, "START_BYTE", startByte));
    }
    if (bytes != null) {
      container.addStatement(new AttributeStatement(label, "BYTES", bytes));
    }
    if (repetitions != null) {
      container.addStatement(new AttributeStatement(label, "REPETITIONS", repetitions));
    }
    if (name == null) {
      name = new TextString("TRANSFORMED_GROUP_CONTAINER");
    }
    container.addStatement(new AttributeStatement(label, "NAME", name));
    List<OrderedObjectStatement> childObjs = mapFields(childFields);
    for (OrderedObjectStatement childObj : childObjs) {
      container.addStatement(childObj);
    }
    return container;
  }
  
  /**
   * Maps the array to its equivalent PDS3 image object.
   * 
   * @param array The Array object to map.
   * 
   * @param objectName The name to give the PDS3 image object.
   * 
   * @return An object statement of the PDS3 image.
   * @throws TransformException 
   */
  private OrderedObjectStatement mapArray(Array array, String objectName) throws TransformException {
    OrderedObjectStatement image = new OrderedObjectStatement(label, objectName);
    String name = "";
    if (array.getName() != null) {
      name = array.getName().toUpperCase();
    } else if (array.getLocalIdentifier() != null) {
      name = array.getLocalIdentifier().toUpperCase();
    }
    if (!name.isEmpty()) {
      image.addStatement(new AttributeStatement(label, "NAME", new TextString(name)));
    }
    Integer lines = -1;
    Integer samples = -1;
    String dataType = null;
    TextString scalingFactor = null;
    TextString offset = null;
    TextString minimum = null;
    TextString maximum = null;
    
    if (array.getElementArray() != null) {
      dataType = array.getElementArray().getDataType();
      if (array.getElementArray().getScalingFactor() != null) {
        scalingFactor = new TextString(array.getElementArray().getScalingFactor().toString());
      }
      if (array.getElementArray().getValueOffset() != null) {
        offset = new TextString(array.getElementArray().getValueOffset().toString());
      }
    } else {
      throw new TransformException("Missing the 'Element_Array' area within the image object.");
    }
    
    if (array.getObjectStatistics() != null) {
      if (array.getObjectStatistics().getMinimum() != null) {
        minimum = new TextString(array.getObjectStatistics().getMinimum().toString());
      }
      if (array.getObjectStatistics().getMaximum() != null) {
        maximum = new TextString(array.getObjectStatistics().getMaximum().toString());
      }
    }
    
    if (array instanceof Array2DImage) {
      Array2DImage array2DImage = (Array2DImage) array;
      for (AxisArray axis : array2DImage.getAxisArraies()) {
        if ("Line".equalsIgnoreCase(axis.getAxisName())) {
          lines = axis.getElements();
        } else if ("Sample".equalsIgnoreCase(axis.getAxisName())) {
          samples = axis.getElements();
        }
      }
    } else if (array instanceof Array3DImage) {
      Array3DImage array3DImage = (Array3DImage) array;
      image.addStatement(new AttributeStatement(label, "BANDS", new TextString("3")));
      image.addStatement(new AttributeStatement(label, "BAND_STORAGE_TYPE", new TextString("BAND_SEQUENTIAL")));
      for (AxisArray axis : array3DImage.getAxisArraies()) {
        if ("Line".equalsIgnoreCase(axis.getAxisName())) {
          lines = axis.getElements();
        } else if ("Sample".equalsIgnoreCase(axis.getAxisName())) {
          samples = axis.getElements();
        }
      }
    } else if (array instanceof Array3DSpectrum) {
      Array3DSpectrum array3DSpectrum = (Array3DSpectrum) array;
      image.addStatement(new AttributeStatement(label, "BAND_STORAGE_TYPE", new TextString("BAND_SEQUENTIAL")));
      for (AxisArray axis : array3DSpectrum.getAxisArraies()) {
        if ("Line".equalsIgnoreCase(axis.getAxisName())) {
          lines = axis.getElements();
        } else if ("Sample".equalsIgnoreCase(axis.getAxisName())) {
          samples = axis.getElements();
        } else if ("Band".equalsIgnoreCase(axis.getAxisName())) {
          image.addStatement(new AttributeStatement(label, "BANDS", new TextString(Integer.toString(axis.getElements()))));
        }
      }
    }
    if (dataType == null || dataType.isEmpty()) {
      throw new TransformException("Missing the 'data_type' element for the image object.");
    } else {
      List<AttributeStatement> dataTypeStmts = mapDataType(dataType, array);
      for (AttributeStatement a : dataTypeStmts) {
        image.addStatement(a);
      }
    }
    if (scalingFactor != null) {
      image.addStatement(new AttributeStatement(label, "SCALING_FACTOR", scalingFactor));
    }
    if (offset != null) {
      image.addStatement(new AttributeStatement(label, "OFFSET", offset));
    }
    if (minimum != null) {
      image.addStatement(new AttributeStatement(label, "MINIMUM", minimum));
    }
    if (maximum != null) {
      image.addStatement(new AttributeStatement(label, "MAXIMUM", maximum));
    }
    image.addStatement(new AttributeStatement(label, "LINES", new TextString(lines.toString())));
    image.addStatement(new AttributeStatement(label, "LINE_SAMPLES", new TextString(samples.toString())));
    return image;
  }
  
  /**
   * Maps the PDS4 data type to its equivalent PDS3 data type.
   * 
   * @param dataType The PDS4 data type to map.
   * @param dataObject The data object associated with the given data type.
   * 
   * @return A list of statements that represents the equivalent PDS3 data type.
   */
  private List<AttributeStatement> mapDataType(String dataType, Object dataObject) {
    List<AttributeStatement> statements = new ArrayList<AttributeStatement>();
    Integer bits = null;
    TextString pds4Type = null;
    if ("UnsignedByte".equalsIgnoreCase(dataType) || "UnsignedMSB2".equalsIgnoreCase(dataType) || 
        "UnsignedMSB4".equalsIgnoreCase(dataType) || "UnsignedMSB8".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("MSB_UNSIGNED_INTEGER");
      try {
        bits = Integer.parseInt(dataType.substring(dataType.length() - 1)) * 8;
      } catch(NumberFormatException n) {
        bits = 8;
      }
      
    } else if ("SignedByte".equalsIgnoreCase(dataType) || "SignedMSB2".equalsIgnoreCase(dataType) || 
        "SignedMSB4".equalsIgnoreCase(dataType) || "SignedMSB8".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("MSB_INTEGER");
      try {
        bits = Integer.parseInt(dataType.substring(dataType.length() - 1)) * 8;
      } catch(NumberFormatException n) {
        bits = 8;
      }
    } else if ("UnsignedLSB2".equalsIgnoreCase(dataType) || "UnsignedLSB4".equalsIgnoreCase(dataType) ||
        "UnsignedLSB8".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("LSB_UNSIGNED_INTEGER");
      bits = Integer.parseInt(dataType.substring(dataType.length() - 1)) * 8;
    } else if ("SignedLSB2".equalsIgnoreCase(dataType) || "SignedLSB4".equalsIgnoreCase(dataType) ||
        "SignedLSB8".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("LSB_INTEGER");
      bits = Integer.parseInt(dataType.substring(dataType.length() - 1)) * 8;
    } else if ("IEEE754MSBSingle".equalsIgnoreCase(dataType) || "IEEE754MSBDouble".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("IEEE_REAL");
      if (dataType.endsWith("Single")) {
        bits = 4;
      } else if (dataType.endsWith("Double")) {
        bits = 8;
      }
    } else if ("IEEE754LSBSingle".equalsIgnoreCase(dataType) || "IEEE754LSBDouble".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("PC_REAL");
      if (dataType.endsWith("Single")) {
        bits = 4;
      } else if (dataType.endsWith("Double")) {
        bits = 8;
      }
    } else if ("ComplexMSB8".equalsIgnoreCase(dataType) || "ComplexMSB16".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("VAX_COMPLEX");
      if (dataType.endsWith("8")) {
        bits = 8;
      } else if (dataType.endsWith("16")) {
        bits = 16;
      }
    } else if ("ComplexLSB8".equalsIgnoreCase(dataType) || "ComplexLSB16".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("PC_COMPLEX");
      if (dataType.endsWith("8")) {
        bits = 8;
      } else if (dataType.endsWith("16")) {
        bits = 16;
      }
    } else if ("ASCII_Integer".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("ASCII_INTEGER");
    } else if ("ASCII_Real".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("ASCII_REAL");
    } else if ("ASCII_Date_DOY".equalsIgnoreCase(dataType) || "ASCII_Date_YMD".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("DATE");
    } else if ("ASCII_Time".equalsIgnoreCase(dataType) || "ASCII_Date_Time_DOY".equalsIgnoreCase(dataType) ||
        "ASCII_Date_Time_DOY_UTC".equalsIgnoreCase(dataType) || "ASCII_Date_Time_YMD".equalsIgnoreCase(dataType) ||
        "ASCII_Date_Time_YMD_UTC".equalsIgnoreCase(dataType)) {
      pds4Type = new TextString("TIME");
    } else {
      pds4Type = new TextString("CHARACTER");
    }
    if (dataObject instanceof Array) {
      statements.add(new AttributeStatement(label, "SAMPLE_TYPE", pds4Type));
      statements.add(new AttributeStatement(label, "SAMPLE_BITS", new TextString(bits.toString())));
    } else if (dataObject instanceof Field) {
      statements.add(new AttributeStatement(label, "DATA_TYPE", pds4Type));
    }
    return statements;
  }
  
  /**
   * Creates a pointer statement.
   * 
   * @param dataObject The data object.
   * @param counter A counter to uniquely name the pointer.
   * @param dataFile The data file to put into the pointer.
   * 
   * @return A pointer statement.
   */
  private PointerStatement createPointer(Object dataObject, Integer counter, String dataFile) {
    String identifier = "";
    Value value = null;
    String namePrefix = OBJECT_NAME_PREFIX + counter.toString();
    if (dataObject instanceof TableBase) {
      Integer offset = new Integer(((TableBase) dataObject).getOffset().getValue());
      if (offset != 0) {
        Sequence sequence = new Sequence();
        sequence.add(new TextString(dataFile));
        sequence.add(new Numeric(Integer.toString(offset.intValue()), "BYTES"));
        value = sequence;
      } else {
        value = new TextString(dataFile);
      }
      identifier = namePrefix + "_TABLE" ;
    } else if (dataObject instanceof TableDelimited) {
      Integer offset = new Integer(((TableDelimited) dataObject).getOffset().getValue());
      if (offset != 0) {
        Sequence sequence = new Sequence();
        sequence.add(new TextString(dataFile));
        sequence.add(new Numeric(Integer.toString(offset.intValue()), "BYTES"));
        value = sequence;
      } else {
        value = new TextString(dataFile);
      }
      identifier = namePrefix + "_SPREADSHEET" ;
    } else if (dataObject instanceof Array) {
      Integer offset = new Integer(((Array) dataObject).getOffset().getValue());
      if (offset != 0) {
        Sequence sequence = new Sequence();
        sequence.add(new TextString(dataFile));
        sequence.add(new Numeric(Integer.toString(offset.intValue()), "BYTES"));
        value = sequence;
      } else {
        value = new TextString(dataFile);
      }
      identifier = namePrefix + "_IMAGE";
    }
    return PointerStatementFactory.newInstance(label, -1, identifier, value);
  }
  
  /**
   * Gets the data file within the given FileArea.
   * 
   * @param fileArea The FileArea.
   * 
   * @return The data file name found within the given FileArea.
   */
  private String getDataFile(FileArea fileArea) {
    String result = "";
    if (fileArea instanceof FileAreaObservational) {
      result = ((FileAreaObservational) fileArea).getFile().getFileName();
    } else if (fileArea instanceof FileAreaSIPDeepArchive) {
      result = ((FileAreaSIPDeepArchive) fileArea).getFile().getFileName();
    } else if (fileArea instanceof FileAreaTransferManifest) {
      result = ((FileAreaTransferManifest) fileArea).getFile().getFileName();
    } else if (fileArea instanceof FileAreaInventory) {
      result = ((FileAreaInventory) fileArea).getFile().getFileName();
    } else if (fileArea instanceof FileAreaBrowse) {
      result = ((FileAreaBrowse) fileArea).getFile().getFileName();
    }
    return result;
  }
  
  /**
   * Gets the file areas from the various object providers.
   * 
   * @param target The PDS4 label file.
   * @param objectAccess To access the contents of the label.
   * 
   * @return A list of FileArea objects.
   * 
   * @throws ParseException If an error occurred while parsing the label.
   */
  private List<FileArea> getFileAreas(File target, ObjectProvider objectAccess) throws ParseException {
    List<FileArea> results = new ArrayList<FileArea>();
    Product product = objectAccess.getProduct(target, Product.class);
    if (product instanceof ProductObservational) {
      results.addAll(((ProductObservational) product).getFileAreaObservationals());
    } else if (product instanceof ProductSIPDeepArchive) {
      ProductSIPDeepArchive psda = (ProductSIPDeepArchive) product;
      if (psda.getInformationPackageComponentDeepArchive() != null && 
          psda.getInformationPackageComponentDeepArchive().getFileAreaSIPDeepArchive() != null) {
        results.add(psda.getInformationPackageComponentDeepArchive().getFileAreaSIPDeepArchive());
      }
    } else if (product instanceof ProductAIP) {
      ProductAIP pa = (ProductAIP) product;
      for ( InformationPackageComponent ipc : pa.getInformationPackageComponents()) {
        if (ipc.getFileAreaTransferManifest() != null) {
          results.add(ipc.getFileAreaTransferManifest());
        }
      }
    } else if (product instanceof ProductCollection) {
      ProductCollection pc = (ProductCollection) product;
      if (pc.getFileAreaInventory() != null &&
        pc.getFileAreaInventory().getInventory() != null) {
        results.add(pc.getFileAreaInventory());
      }
    } else if (product instanceof ProductBrowse) {
      results.addAll(((ProductBrowse) product).getFileAreaBrowses());
    }
    return results;
  }
}
