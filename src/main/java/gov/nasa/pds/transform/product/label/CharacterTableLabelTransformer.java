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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.GroupFieldCharacter;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.RecordCharacter;
import gov.nasa.arc.pds.xml.generated.RecordDelimited;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.table.DelimiterType;

/**
 * Class to transform a Table_Character area to Table_Delimited.
 * 
 * @author mcayanan
 *
 */
public class CharacterTableLabelTransformer implements TableLabelTransformer<TableCharacter> {

  @Override
  public TableDelimited toTableDelimited(Object table) {
    return toTableDelimited(table, DelimiterType.COMMA);
  }
  
  public TableDelimited toTableDelimited(Object table, DelimiterType type) {
    return toTableDelimited((TableCharacter) table, type);
  }
  
  
  public TableDelimited toTableDelimited(TableCharacter tableCharacter) {
    return toTableDelimited(tableCharacter, DelimiterType.COMMA);
  }
  
  @Override
  public TableDelimited toTableDelimited(TableCharacter table, DelimiterType type) {
    TableDelimited tableDelimited = new TableDelimited();
    tableDelimited.setDescription(table.getDescription());
    tableDelimited.setRecordDelimiter("Carriage-Return Line-Feed");
    tableDelimited.setFieldDelimiter(type.getXmlType());
    tableDelimited.setLocalIdentifier(table.getLocalIdentifier());
    tableDelimited.setName(table.getName());
    //Offset should always be set to 0
    tableDelimited.setOffset(table.getOffset());
    tableDelimited.getOffset().setValue(BigInteger.valueOf(0));
    tableDelimited.setParsingStandardId("PDS DSV 1");
    tableDelimited.setRecords(table.getRecords());
    tableDelimited.setUniformlySampled(table.getUniformlySampled());
    tableDelimited.setRecordDelimited(toRecordDelimited(table.getRecordCharacter()));
    return tableDelimited;
  }

  /**
   * Transforms the Record_Character object into a Record_Delimited object.
   * 
   * @param recordCharacter The Record_Character object to convert.
   * 
   * @return The converted Record_Delimited object.
   */
  public RecordDelimited toRecordDelimited(RecordCharacter recordCharacter) {
    RecordDelimited recordDelimited = new RecordDelimited();
    recordDelimited.setFields(recordCharacter.getFields());
    recordDelimited.setGroups(recordCharacter.getGroups());
    recordDelimited.getFieldDelimitedsAndGroupFieldDelimiteds().addAll(
        toFieldDelimitedAndGroupFieldDelimiteds(
            recordCharacter.getFieldCharactersAndGroupFieldCharacters()));
    return recordDelimited;
  }
  
  /**
   * Transforms the fields and group fields.
   * 
   * @param fieldCharacters A list of fields and group fields to convert.
   * 
   * @return A list of Field_Delimited and Group_Field_Delimited objects.
   */
  public List<Object> toFieldDelimitedAndGroupFieldDelimiteds(
      List<Object> fieldCharacters) {
    List<Object> fieldDelimiteds = new ArrayList<Object>();
    for (Object fieldCharacter : fieldCharacters) {
      if (fieldCharacter instanceof FieldCharacter) {
        fieldDelimiteds.add(toFieldDelimited((FieldCharacter) fieldCharacter));
      } else if (fieldCharacter instanceof GroupFieldCharacter) {
        fieldDelimiteds.add(toGroupFieldDelimited((GroupFieldCharacter) fieldCharacter));
      }
    }
    return fieldDelimiteds;
  }
  
  /**
   * Converts Field_Character objects to Field_Delimited objects.
   * 
   * @param fieldCharacter The Field_Character object to convert.
   * 
   * @return The converted Field_Delimited object.
   */
  public FieldDelimited toFieldDelimited(FieldCharacter fieldCharacter) {
    FieldDelimited fieldDelimited = new FieldDelimited();
    fieldDelimited.setDataType(fieldCharacter.getDataType());
    fieldDelimited.setDescription(fieldCharacter.getDescription());
    fieldDelimited.setFieldFormat(fieldCharacter.getFieldFormat());
    fieldDelimited.setFieldStatistics(fieldCharacter.getFieldStatistics());
    fieldDelimited.setName(fieldCharacter.getName());
    fieldDelimited.setScalingFactor(fieldCharacter.getScalingFactor());
    fieldDelimited.setSpecialConstants(fieldCharacter.getSpecialConstants());
    fieldDelimited.setUnit(fieldCharacter.getUnit());
    fieldDelimited.setValueOffset(fieldCharacter.getValueOffset());
    return fieldDelimited;
  }
  
  /**
   * Converts the Group_Field_Character object into a Group_Field_Delimited
   * object.
   * 
   * @param groupFieldCharacter The Group_Field_Character object to convert.
   * 
   * @return The converted Group_Field_Delimited object.
   */
  public GroupFieldDelimited toGroupFieldDelimited(GroupFieldCharacter groupFieldCharacter) {
    GroupFieldDelimited groupFieldDelimited = new GroupFieldDelimited();
    groupFieldDelimited.setDescription(groupFieldCharacter.getDescription());
    groupFieldDelimited.setFields(groupFieldCharacter.getFields());
    groupFieldDelimited.setGroups(groupFieldCharacter.getGroups());
    groupFieldDelimited.setName(groupFieldCharacter.getName());
    groupFieldDelimited.setRepetitions(groupFieldCharacter.getRepetitions());
    groupFieldDelimited.getFieldDelimitedsAndGroupFieldDelimiteds().addAll(
        toFieldDelimitedAndGroupFieldDelimiteds(
            groupFieldCharacter.getFieldCharactersAndGroupFieldCharacters()));
    return groupFieldDelimited;
  }
}
