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

import java.util.ArrayList;
import java.util.List;


import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.RecordBinary;
import gov.nasa.arc.pds.xml.generated.RecordDelimited;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.table.DelimiterType;

/**
 * Class to transform Table_Binary objects into Table_Delimited objects.
 * 
 * @author mcayanan
 *
 */
public class BinaryTableLabelTransformer implements 
TableLabelTransformer<TableBinary> {
  /** Keeps track of the number of fields. */
  private int numFields;
  
  /**
   * Constructor.
   * 
   */
  public BinaryTableLabelTransformer() {
    numFields = 0;
  }
  
  public TableDelimited toTableDelimited(Object tableBinary) {
    return toTableDelimited(tableBinary, DelimiterType.COMMA);
  }
  
  public TableDelimited toTableDelimited(Object tableBinary, DelimiterType type) {
    return toTableDelimited((TableBinary) tableBinary, type);
  }
  
  
  public TableDelimited toTableDelimited(TableBinary tableBinary) {
    return toTableDelimited(tableBinary, DelimiterType.COMMA);
  }
  
  public TableDelimited toTableDelimited(TableBinary tableBinary, DelimiterType type) {
    TableDelimited tableDelimited = new TableDelimited();
    tableDelimited.setDescription(tableBinary.getDescription());
    tableDelimited.setRecordDelimiter("Carriage-Return Line-Feed");
    tableDelimited.setFieldDelimiter(type.getXmlType());
    tableDelimited.setLocalIdentifier(tableBinary.getLocalIdentifier());
    tableDelimited.setName(tableBinary.getName());
    //Offset should always be set to 0
    tableDelimited.setOffset(tableBinary.getOffset());
    tableDelimited.getOffset().setValue(0);
    tableDelimited.setParsingStandardId("PDS DSV 1");
    tableDelimited.setRecords(tableBinary.getRecords());
    tableDelimited.setUniformlySampled(tableBinary.getUniformlySampled());
    tableDelimited.setRecordDelimited(toRecordDelimited(tableBinary.getRecordBinary()));
    return tableDelimited;
  }
  
  /**
   * Transforms the Record_Binary object into a Record_Delimited object.
   * 
   * @param recordBinary The Record_Binary object to convert.
   * 
   * @return The converted Record_Delimited object.
   */
  public RecordDelimited toRecordDelimited(RecordBinary recordBinary) {
    RecordDelimited recordDelimited = new RecordDelimited();
    numFields = recordBinary.getFields();
    recordDelimited.setGroups(recordBinary.getGroups());
    recordDelimited.getFieldDelimitedsAndGroupFieldDelimiteds().addAll(
        toFieldDelimitedAndGroupFieldDelimiteds(
            recordBinary.getFieldBinariesAndGroupFieldBinaries()));
    //Should we manually count number of Field_Delimited objects???
    recordDelimited.setFields(numFields);
    return recordDelimited;
  }
  
  /**
   * Transforms the fields and group fields. The top-level field objects of
   * ones containing packed fields are ignored as the Field_Bit objects
   * will be converted to Field_Delimited objects.
   * 
   * @param fieldBinaries A list of fields and group fields to convert.
   * 
   * @return A list of Field_Delimited and Group_Field_Delimited objects.
   */
  public List<Object> toFieldDelimitedAndGroupFieldDelimiteds(
      List<Object> fieldBinaries) {
    List<Object> fieldDelimiteds = new ArrayList<Object>();
    for (Object fieldBinary : fieldBinaries) {
      if (fieldBinary instanceof FieldBinary) {
        // If the field contains packed data, we should ignore these field
        // characteristics and process the packed data fields instead.
        FieldBinary fb = (FieldBinary) fieldBinary;
        if (fb.getPackedDataFields() != null) {
          --numFields;
          fieldDelimiteds.addAll(toFieldDelimiteds(fb.getPackedDataFields()));
        } else {
          fieldDelimiteds.add(toFieldDelimited(fb));
        }
      } else if (fieldBinary instanceof GroupFieldBinary) {
        fieldDelimiteds.add(toGroupFieldDelimited((GroupFieldBinary) fieldBinary));
      }
    }
    return fieldDelimiteds;
  }
  
  /**
   * Converts Field_Binary objects to Field_Delimited objects.
   * 
   * @param fieldBinary The Field_Binary object to convert.
   * 
   * @return The converted Field_Delimited object.
   */
  public FieldDelimited toFieldDelimited(FieldBinary fieldBinary) {
    FieldDelimited fieldDelimited = new FieldDelimited();
    fieldDelimited.setDataType(toAsciiDataType(fieldBinary.getDataType()));
    fieldDelimited.setDescription(fieldBinary.getDescription());
    fieldDelimited.setFieldFormat(fieldBinary.getFieldFormat());
    fieldDelimited.setFieldStatistics(fieldBinary.getFieldStatistics());
    fieldDelimited.setName(fieldBinary.getName());
    fieldDelimited.setScalingFactor(fieldBinary.getScalingFactor());
    fieldDelimited.setSpecialConstants(fieldBinary.getSpecialConstants());
    fieldDelimited.setUnit(fieldBinary.getUnit());
    fieldDelimited.setValueOffset(fieldBinary.getValueOffset());
    
    return fieldDelimited;
  }
  
  /**
   * Converts the Field_Bits within the Packed_Data_Fields to Field_Delimited
   * objects.
   * 
   * @param packedDataFields The Packed_Data_Fields to convert.
   * 
   * @return A list of converted Field_Delimited objects.
   */
  public List<FieldDelimited> toFieldDelimiteds(PackedDataFields packedDataFields) {
    List<FieldDelimited> results = new ArrayList<FieldDelimited>();
    for (FieldBit fieldBit : packedDataFields.getFieldBits()) {
      FieldDelimited fieldDelimited = new FieldDelimited();
      fieldDelimited.setDataType(toAsciiDataType(fieldBit.getDataType()));
      fieldDelimited.setName(fieldBit.getName());
      fieldDelimited.setScalingFactor(fieldBit.getScalingFactor());
      fieldDelimited.setSpecialConstants(fieldBit.getSpecialConstants());
      fieldDelimited.setUnit(fieldBit.getUnit());
      fieldDelimited.setValueOffset(fieldBit.getValueOffset());
      results.add(fieldDelimited);
      ++numFields;
    }
    return results;
  }
  
  /**
   * Converts the Group_Field_Binary object into a Group_Field_Delimited
   * object.
   * 
   * @param groupFieldBinary The Group_Field_Binary object to convert.
   * 
   * @return The converted Group_Field_Delimited object.
   */
  public GroupFieldDelimited toGroupFieldDelimited(GroupFieldBinary groupFieldBinary) {
    GroupFieldDelimited groupFieldDelimited = new GroupFieldDelimited();
    groupFieldDelimited.setDescription(groupFieldBinary.getDescription());
    groupFieldDelimited.setFields(groupFieldBinary.getFields());
    groupFieldDelimited.setGroups(groupFieldBinary.getGroups());
    groupFieldDelimited.setName(groupFieldBinary.getName());
    groupFieldDelimited.setRepetitions(groupFieldBinary.getRepetitions());
    groupFieldDelimited.getFieldDelimitedsAndGroupFieldDelimiteds().addAll(
        toFieldDelimitedAndGroupFieldDelimiteds(
            groupFieldBinary.getFieldBinariesAndGroupFieldBinaries()));
    return groupFieldDelimited;
  }
  
  /**
   * Converts a binary data type to an ascii data type.
   * 
   * @param binaryType The data type to convert.
   * 
   * @return An ascii data type.
   */
  private String toAsciiDataType(String binaryType) {
    String type = "";
    if (binaryType.startsWith("IEEE") || binaryType.startsWith("Complex")) {
      type = "ASCII_Real";
    } else if ("UnsignedBitString".equalsIgnoreCase(binaryType) ||
               "SignedBitString".equalsIgnoreCase(binaryType)) {
      type = "ASCII_String";
    } else if (binaryType.startsWith("Unsigned") || binaryType.startsWith("Signed")) {
      type = "ASCII_Integer";
    } else {
      type = binaryType;
    }
    return type;
  }
}
