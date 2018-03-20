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
package gov.nasa.pds.transform.table;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.TableReader;
import gov.nasa.pds.transform.TransformException;

/**
 * Class that extracts table data from a PDS4 Table product.
 * 
 * @author mcayanan
 *
 */
public class TableExtractor {
  /**
   * Defines an enumeration for the different output formats.
   */
  private static enum OutputFormat {
    CSV, FIXED_WIDTH;
  }

  private OutputFormat format;
  private PrintWriter out;
  
  private String fieldSeparator;
  private String lineSeparator;
  private String[] requestedFields;
  
  /**
   * Constructor.
   * 
   */
  public TableExtractor() {
    this.format = OutputFormat.CSV;
    out = new PrintWriter(new OutputStreamWriter(System.out));
    fieldSeparator = ",";
    //lineSeparator = System.getProperty("line.separator");
    lineSeparator = "\r\n";
    requestedFields = null;    
  }
  
  /**
   * Extracts a table to the output file.
   *
   * @param reader the table reader to use for reading data
   * @throws TransformException
   */
  public void extractTable(TableReader reader, boolean displayHeaders)
      throws TransformException {
    FieldDescription[] fields = reader.getFields();
    if (fields.length == 0) {
      throw new TransformException("No fields found in the table.");
    }
    int[] displayFields = getSelectedFields(fields);

    int[] fieldLengths = getFieldLengths(fields, displayFields);

    if (displayHeaders) {
      displayHeaders(fields, displayFields, fieldLengths);
    }
    displayRows(reader, fields, displayFields, fieldLengths);
  }
  
  /**
   * Sets the output format. 
   * 
   * @param format The output format.
   */
  public void setFormat(String format) {
    if ("csv".equals(format.toLowerCase())) {
      this.format = OutputFormat.CSV;
    }
  }
  
  /**
   * Sets the output writer.
   * 
   * @param writer The PrintWriter.
   */
  public void setOutput(PrintWriter writer) {
    out = writer;
  }
  
  /**
   * Gets an array of field lengths to use for output.
   *
   * @param fields an array of field descriptions
   * @param displayFields an array of field indices to display
   * @return An array of field lengths.
   */
  private int[] getFieldLengths(FieldDescription[] fields, int[] displayFields) {
    int[] fieldLengths = new int[displayFields.length];
    for (int i=0; i < displayFields.length; ++i) {
      int fieldIndex = displayFields[i];
      if (format == OutputFormat.CSV) {
        fieldLengths[i] = 0;
      } else {
        fieldLengths[i] = Math.max(fields[fieldIndex].getName().length(),
            fields[fieldIndex].getLength());
      }
    }
    return fieldLengths;
  }

  /**
   * Displays the headers of the table.
   *
   * @param fields an array of field descriptions
   * @param displayFields an array of field indices to display
   * @param fieldLengths an array of field lengths to use for output
   */
  private void displayHeaders(FieldDescription[] fields, int[] displayFields, int[] fieldLengths) {
    for (int i=0; i < displayFields.length; ++i) {
      if (i > 0) {
        out.append(fieldSeparator);
      }
      FieldDescription field = fields[displayFields[i]];
      displayJustified(field.getName(), fieldLengths[i], field.getType().isRightJustified());
    }
    out.append(lineSeparator);
  }

  /**
   * Displays the rows from the table.
   *
   * @param reader the table reader for reading rows
   * @param fields an array of field descriptions
   * @param displayFields an array of field indices to display
   * @param fieldLengths an array of field lengths to use for output
   * @throws TransformException
   * @throws IOException
   */
  private void displayRows(TableReader reader, FieldDescription[] fields,
      int[] displayFields, int[] fieldLengths) throws TransformException {
    TableRecord record;
    try {
      while ((record = reader.readNext()) != null) {
        for (int i=0; i < displayFields.length; ++i) {
          if (i > 0) {
            out.append(fieldSeparator);
          }
          int index = displayFields[i];
          FieldDescription field = fields[index];
          try {
            displayJustified(record.getString(index+1).trim(), fieldLengths[i],
              field.getType().isRightJustified());
          } catch (Exception e) {
            throw new TransformException("Error while attempting to read field "
                + (i + 1) + ": " + e.getMessage());
          }
        }
        out.append(lineSeparator);
      }
    } catch (IOException e) {
      throw new TransformException("Cannot read the next table record: "
          + e.getMessage());
    }
  }

  /**
   * Gets an array of field indices to display. Uses the
   * field indices specified on the command line, if any,
   * otherwise all fields will be displayed.
   *
   * @param totalFields the total number of fields in the table
   * @return an array of fields to display
   * @throws TransformException
   */
  private int[] getSelectedFields(FieldDescription[] fields)
  throws TransformException {
    int[] displayFields;
    if (requestedFields == null) {
      displayFields = new int[fields.length];
      for (int i=0; i < fields.length; ++i) {
        displayFields[i] = i;
      }
    } else {
      displayFields = new int[requestedFields.length];
      for (int i=0; i < requestedFields.length; ++i) {
        displayFields[i] = findField(requestedFields[i], fields);
      }
    }
    return displayFields;
  }
  
  /**
   * Try to convert a field name or index into a field index.
   * Prints an error message and exits if the field is not
   * present in the table.
   *
   * @param nameOrIndex the string form of the name or index requested
   * @param fields the field descriptions for the table fields
   * @return the index of the requested field
   * @throws TransformException
   */
  private int findField(String nameOrIndex, FieldDescription[] fields)
  throws TransformException {
    // First try to convert as an integer.
    try {
      return Integer.parseInt(nameOrIndex) - 1;
    } catch (NumberFormatException ex) {
      // ignore
    }
    // Now try to find a matching field name, ignoring case.
    for (int i=0; i < fields.length; ++i) {
      if (nameOrIndex.equalsIgnoreCase(fields[i].getName())) {
        return i;
      }
    }
    // If we get here, then we couldn't find a matching field.
    throw new TransformException("Requested field not present in table: "
        + nameOrIndex);
  }

  /**
   * Displays a string, justified in a field.
   *
   * @param s the string to display
   * @param length the field length
   * @param isRightJustified true, if the value should be right-justified, else left-justified
   */
  private void displayJustified(String s, int length, boolean isRightJustified) {
    String quoteCharacter = "\"";
    Pattern quoteCharacterPattern = Pattern.compile("\\Q" + quoteCharacter + "\\E");
    // Double any quote characters.
    if (s.contains(quoteCharacter)) {
      Matcher matcher = quoteCharacterPattern.matcher(s);
      s = matcher.replaceAll(quoteCharacter + quoteCharacter);
    }

    // If the value is all whitespace or contains the field separator, quote the value.
    if (s.trim().isEmpty() || s.contains(fieldSeparator)) {
      s = quoteCharacter + s + quoteCharacter;
    }


    int padding = length - s.length();

    if (isRightJustified) {
      displayPadding(padding);
    }
    out.append(s);
    if (!isRightJustified) {
      displayPadding(padding);
    }
  }

  /**
   * Displays a number of padding spaces.
   *
   * @param n the number of spaces
   */
  private void displayPadding(int n) {
    for (int i=0; i < n; ++i) {
      out.append(' ');
    }
  }  
}
