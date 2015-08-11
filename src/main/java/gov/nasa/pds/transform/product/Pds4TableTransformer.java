// Copyright 2006-2015, by the California Institute of Technology.
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

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
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
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class that transforms PDS4 tables into CSV formatted-files.
 *
 * @author mcayanan
 *
 */
public class Pds4TableTransformer extends DefaultTransformer {

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
   * Constructor to set the flag to overwrite outputs.
   *
   * @param overwrite Set to true to overwrite outputs, false otherwise.
   */
  public Pds4TableTransformer(boolean overwrite) {
    super(overwrite);
    format = OutputFormat.CSV;
    out = new PrintWriter(new OutputStreamWriter(System.out));
    fieldSeparator = ",";
    lineSeparator = System.getProperty("line.separator");
    requestedFields = null;
  }

  /**
   * Transform the given label.
   *
   * @param target The PDS4 xml label file.
   * @param outputDir The output directory.
   * @param format the format type.
   *
   * @throws TransformException If an error occurred during transformation.
   * @throws ParseException If there were errors parsing the given label.
   */
  public File transform(File target, File outputDir, String format,
      String dataFileName, int index)
  throws TransformException {
    setFormat(format);
    File result = null;
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      List<FileAreaObservational> fileAreas = Utility.getFileAreas(target);
      FileAreaObservational fileArea = null;
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational "
            + "area in the label: " + target.toString());
      } else {
        for (FileAreaObservational fa : fileAreas) {
          if (dataFileName.isEmpty()) {
            fileArea = fileAreas.get(0);
            dataFileName = fileArea.getFile().getFileName();
          }
          if (fa.getFile().getFileName().equals(dataFileName)) {
            fileArea = fa;
          }
        }
      }
      if (fileArea != null) {
        File dataFile = new File(target.getParent(), dataFileName);
        File outputFile = Utility.createOutputFile(new File(dataFileName),
            outputDir, format);
        process(target, dataFile, outputFile, objectAccess, fileArea, index);
        // fix for PDS-353 (PDS4_TO_CSV)
        result = outputFile;
      } else {
        String message = "";
        if (dataFileName.isEmpty()) {
          message = "No data file(s) found in label.";
        } else {
          message = "No data file '" + dataFileName + "' found in label.";
        }
        throw new TransformException(message);
      }
      return result;
    } catch (ParseException p) {
      throw new TransformException("Error occurred while parsing label '"
          + target.toString() + "': " + p.getMessage());
    } catch (IOException i) {
      throw new TransformException("Error occurred while resolving the path "
          + "of the label: " + i.getMessage());
    }
  }

  private void process(File target, File dataFile, File outputFile,
      ObjectProvider objectAccess, FileAreaObservational fileArea, int index)
          throws TransformException {
    if ((outputFile.exists() && outputFile.length() != 0)
        && !overwriteOutput) {
      log.log(new ToolsLogRecord(ToolsLevel.INFO,
          "Output file already exists. No transformation will occur: "
          + outputFile.toString(), target));
    } else {
      List<Object> tableObjects = objectAccess.getTableObjects(fileArea);
      if (objectAccess.getTableObjects(fileArea).isEmpty()) {
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "No table objects are found in the label.", target));
      } else {
        Object tableObject = null;
        try {
          tableObject = tableObjects.get(index-1);
        } catch (IndexOutOfBoundsException ie) {
          throw new TransformException("Index '" + index
              + "' is greater than the maximum number of tables found '"
              + "for data file '" + fileArea.getFile().getFileName() + "': "
              + tableObjects.size());
        }
        try {
        out = new PrintWriter(new FileWriter(outputFile));
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "Transforming table '" + index + "' of file: "
            + dataFile.toString(), target));
        TableReader reader = ExporterFactory.getTableReader(tableObject,
            dataFile);
        extractTable(reader);
        } catch (IOException io) {
          throw new TransformException("Cannot open output file \'"
              + outputFile.toString() + "': " + io.getMessage());
        } catch (Exception e) {
          throw new TransformException(
              "Error occurred while reading table '" + index
              + "' of file '" + dataFile.toString() + "': "
              + e.getMessage());
        } finally {
          out.close();
        }
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
            "Successfully transformed table '" + index
              + "' to the following output: " + outputFile.toString(),
              target));
      }
    }
  }

  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    setFormat(format);
    List<File> outputs = new ArrayList<File>();
    try {
      ObjectProvider objectAccess = new ObjectAccess(
          target.getCanonicalFile().getParent());
      List<FileAreaObservational> fileAreas = Utility.getFileAreas(target);
      if (fileAreas.isEmpty()) {
        throw new TransformException("Cannot find File_Area_Observational "
            + "area in the label: " + target.toString());
      } else {
        for (FileAreaObservational fileArea : fileAreas) {
          List<Object> tables = objectAccess.getTableObjects(fileArea);
          if (!tables.isEmpty()) {
            int numTables = tables.size();
            String dataFilename = fileArea.getFile().getFileName();
            File dataFile = new File(target.getParent(), dataFilename);
            for (int i = 0; i < numTables; i++) {
              File outputFile = null;
              if (objectAccess.getTableObjects(fileArea).size() > 1) {
                outputFile = Utility.createOutputFile(new File(dataFilename),
                    outputDir, format, (i+1));
              } else {
                outputFile = Utility.createOutputFile(new File(dataFilename),
                    outputDir, format);
              }
              try {
              process(target, dataFile, outputFile, objectAccess, fileArea,
                  (i+1));
              outputs.add(outputFile);
              } catch (Exception e) {
                log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
                    target));
              }
            }
          } else {
            log.log(new ToolsLogRecord(ToolsLevel.INFO,
                "No table objects are found in the label.", target));
          }
        }
      }
      return outputs;
    } catch (ParseException p) {
      throw new TransformException("Error occurred while parsing label '"
          + target.toString() + "': " + p.getMessage());
    } catch (IOException i) {
      throw new TransformException("Error occurred while resolving the path "
          + "of the label: " + i.getMessage());
    }
  }

  private void setFormat(String format) {
    if ("csv".equals(format.toLowerCase())) {
      this.format = OutputFormat.CSV;
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
