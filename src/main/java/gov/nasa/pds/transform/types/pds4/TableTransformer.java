// Copyright 2006-2013, by the California Institute of Technology.
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
package gov.nasa.pds.transform.types.pds4;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.objectAccess.TableReader;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;


/**
 * Class that transforms PDS4 tables into CSV formatted-files.
 *
 * @author mcayanan
 *
 */
public class TableTransformer {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      TableTransformer.class.getName());

  /**
   * Defines an enumeration for the different table types
   * that can be extracted. Holds a readable description of
   * the table type.
   */
  private static enum TableType {
    /** A fixed-width binary table. */
    FIXED_BINARY("fixed-width binary table"),

    /** A fixed-width text table. */
    FIXED_TEXT("fixed-width character table"),

    /** A delimited table. */
    DELIMITED("delimited table");

    private String readableType;

    private TableType(String readableType) {
        this.readableType = readableType;
    }

    /**
     * Gets the readable name for the table type.
     *
     * @return the name of the table type
     */
    public String getReadableType() {
        return readableType;
    }
  }

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
   * Default constructor.
   *
   */
  public TableTransformer() {
    format = OutputFormat.CSV;
    out = new PrintWriter(new OutputStreamWriter(System.out));
    fieldSeparator = ",";
    lineSeparator = System.getProperty("line.separator");
    requestedFields = null;
  }

  /**
   * Transform the given label.
   *
   * @param labelFile The PDS4 xml label file.
   * @param outputFile The output file. If none is provided, the output
   * will go to standard out.
   *
   * @throws TransformException If an error occurred during transformation.
   * @throws ParseException If there were errors parsing the given label.
   */
  public void transform(File labelFile, File outputFile) throws TransformException,
  ParseException {
    if (!labelFile.isFile() || !labelFile.canRead()) {
      throw new TransformException("Cannot read label file: " + labelFile);
    }
    try {
      if (outputFile != null) {
        try {
          out = new PrintWriter(new FileWriter(outputFile));
        } catch (IOException io) {
          throw new TransformException("Cannot open output file \'"
              + outputFile.toString() + "': " + io.getMessage());
        }
      }
      ObjectProvider objectAccess = new ObjectAccess();
      ProductObservational product = objectAccess.getProduct(labelFile,
          ProductObservational.class);
      if (product.getFileAreaObservationals() == null ||
          product.getFileAreaObservationals().isEmpty()) {
        throw new TransformException("Missing File_Area_Observational area in "
            + "the label.");
      }
      FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
      String filename = fileArea.getFile().getFileName();
      File dataFile = new File(labelFile.getParent(), filename);
      int currentIndex = 1;
      if (objectAccess.getTableObjects(fileArea).isEmpty()) {
        throw new TransformException("No table objects are found in the label.");
      }
      for (Object object : objectAccess.getTableObjects(fileArea)) {
        TableType tableType = TableType.FIXED_BINARY;
        if (object instanceof TableCharacter) {
          tableType = TableType.FIXED_TEXT;
        } else if (object instanceof TableDelimited) {
          tableType = TableType.DELIMITED;
        }
        try {
          log.log(new ToolsLogRecord(ToolsLevel.INFO, "Transforming table file: "
              + dataFile.toString(), labelFile));
          TableReader reader = ExporterFactory.getTableReader(object, dataFile);
          extractTable(reader);
        } catch (Exception e) {
          throw new TransformException("Cannot create a table reader: "
              + e.getMessage());
        }
        ++currentIndex;
      }
    } finally {
      out.close();
    }
  }

  /**
   * Extracts a table to the output file.
   *
   * @param reader the table reader to use for reading data
   * @throws TransformException
   */
  private void extractTable(TableReader reader) throws TransformException {
    FieldDescription[] fields = reader.getFields();
    int[] displayFields = getSelectedFields(fields);

    int[] fieldLengths = getFieldLengths(fields, displayFields);

    displayHeaders(fields, displayFields, fieldLengths);
    displayRows(reader, fields, displayFields, fieldLengths);
  }

  /**
   * Gets an array of field lengths to use for output.
   *
   * @param fields an array of field descriptions
   * @param displayFields an array of field indices to display
   * @return
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
          displayJustified(record.getString(index+1).trim(), fieldLengths[i],
              field.getType().isRightJustified());
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
