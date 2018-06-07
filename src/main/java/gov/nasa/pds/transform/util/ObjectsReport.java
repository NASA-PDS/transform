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
package gov.nasa.pds.transform.util;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.AxisArray;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.RecordBinary;
import gov.nasa.arc.pds.xml.generated.RecordCharacter;
import gov.nasa.arc.pds.xml.generated.RecordDelimited;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.objectAccess.DataType;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.objectAccess.TableReader;
import gov.nasa.pds.tools.containers.FileReference;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Class that reports the table and image objects currently supported
 * by the Transform Tool.
 *
 * @author mcayanan
 *
 */
public class ObjectsReport {
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

	/** The output stream that will contain the report. */
	private PrintWriter writer;

	/**
	 * Constructor.
	 */
	public ObjectsReport() {
		this.writer = new PrintWriter(new OutputStreamWriter(System.out));
	}

	/**
	 * Lists the table and image objects currently supported by the Transform
	 * Tool. Report defaults to standard out.
	 *
	 * @param label The label to examine.
	 *
	 * @throws Exception If there was an error while parsing the label.
	 */
	public void list(File label) throws Exception {
		//System.out.println("label = " + label.getName());
		if ("xml".equalsIgnoreCase(FilenameUtils.getExtension(label.getName()))) {
			List<FileAreaObservational> fileAreas =
					new ArrayList<FileAreaObservational>();
			try {

				fileAreas = Utility.getFileAreas(label);  
			} catch (ParseException pe) {
				throw new Exception("Exception occurred while parsing label: "
						+ pe.getMessage());
			}
			ObjectProvider objectAccess = new ObjectAccess(
					label.getCanonicalFile().getParent());
			list(objectAccess, fileAreas, null);
		} else {
			Label pds3Label = null;
			try {
				//System.out.println("in ObejctsReport.list().... before calling Utility.parsePds3Label....");
				pds3Label = Utility.parsePds3Label(label);
			} catch (Exception e) {
				throw new Exception("Exception occurred while parsing label: "
						+ e.getMessage());
			}
			list(pds3Label);
		}
	}

	public void list(URL url, File outputDir) throws Exception {
		if ("xml".equalsIgnoreCase(FilenameUtils.getExtension(url.toString()))) {
			List<FileAreaObservational> fileAreas =
					new ArrayList<FileAreaObservational>();
			try {
				fileAreas = Utility.getFileAreas(url);
			} catch (ParseException pe) {
				throw new Exception("Exception occurred while parsing label: "
						+ pe.getMessage());
			}
			ObjectProvider objectAccess = new ObjectAccess(url);
			list(objectAccess, fileAreas, outputDir);
		} else {
			Label pds3Label = null;
			try {
				pds3Label = Utility.parsePds3Label(url);
			} catch (Exception e) {
				throw new Exception("Exception occurred while parsing label: "
						+ e.getMessage());
			}
			list(pds3Label);
		}
	}

	/**
	 * Lists the PDS3 objects supported by the tool.
	 *
	 * @param label The PDS3 label.
	 */
	private void list(Label label) {
		List<PointerStatement> pointers = label.getPointers();
		Collections.sort(pointers);
		writer.println("Supported Images: \n");
		Map<String, List<PointerStatement>> pointerMap =
				new LinkedHashMap<String, List<PointerStatement>>();
		for (PointerStatement pointer : pointers) {
			if (pointer.getIdentifier().getId().endsWith("IMAGE")) {
				List<FileReference> refs = pointer.getFileRefs();
				if (!refs.isEmpty()) {
					FileReference ref = refs.get(0);
					List<PointerStatement> ptrs = pointerMap.get(ref.getPath());
					if (ptrs == null) {
						ptrs = new ArrayList<PointerStatement>();
						ptrs.add(pointer);
						pointerMap.put(ref.getPath(), ptrs);
					} else {
						ptrs.add(pointer);
					}
				}
			}
		}
		int index = 1;
		boolean hasImages = false;
		for (String datafile : pointerMap.keySet()) {
			String extension = FilenameUtils.getExtension(datafile);
			boolean printHeader = true;
			for (PointerStatement pointer : pointerMap.get(datafile)) {
				// TODO: Remove this check if the Transcoder can support
				// multiple, non-FITS PDS3 image transformations
				if (index != 1 && !("fits".equalsIgnoreCase(extension) ||
						"fit".equalsIgnoreCase(extension)) ) {
					break;
				}
				List<ObjectStatement> objects = label.getObjects(
						pointer.getIdentifier().getId());
				if (!objects.isEmpty()) {
					// Assume only 1 object was returned for that identifier
					ObjectStatement object = objects.get(0);
					// TODO: Check number of bands. Transcoder currently does not
					// support transformation of multi banded images.
					int numBands = 1;
					if (object.getAttribute("BANDS") != null) {
						numBands = Integer.parseInt(
								object.getAttribute("BANDS").getValue().toString());
					}
					if (numBands == 1) {
						hasImages = true;
						if (printHeader) {
							writer.println("  Data file: " + datafile + "\n");
							printHeader = false;
						}
						printImageInfo(object, index);
					}
				}
				index++;

			}
		}
		if (!hasImages) {
			writer.println("  None Found\n");
		}
		writer.println("Supported Tables: \n");
		pointerMap.clear();
		for (PointerStatement pointer : pointers) {
			if (pointer.getIdentifier().getId().endsWith("TABLE") ||
					pointer.getIdentifier().getId().endsWith("SPREADSHEET") ||
					pointer.getIdentifier().getId().endsWith("SERIES") 
					) {
				List<FileReference> refs = pointer.getFileRefs();
				if (!refs.isEmpty()) {
					FileReference ref = refs.get(0);
					List<PointerStatement> ptrs = pointerMap.get(ref.getPath());
					if (ptrs == null) {
						ptrs = new ArrayList<PointerStatement>();
						ptrs.add(pointer);
						pointerMap.put(ref.getPath(), ptrs);
					} else {
						ptrs.add(pointer);
					}
				}
			}
		}

		// for PDS-539)
		Map<String, List<ObjectStatement>> objMap =
				new LinkedHashMap<String, List<ObjectStatement>>();
		List<ObjectStatement> fileObjects = label.getObjects("FILE");
		for (ObjectStatement obj: fileObjects) {
			List<PointerStatement> filePointers = obj.getPointers();
			for (PointerStatement filepointer : filePointers) {
				if (filepointer.getIdentifier().getId().endsWith("TABLE") ||
						filepointer.getIdentifier().getId().endsWith("SPREADSHEET") ||
						filepointer.getIdentifier().getId().endsWith("SERIES") 
						) {
					List<FileReference> refs = filepointer.getFileRefs();
					if (!refs.isEmpty()) {
						FileReference ref = refs.get(0);
						List<PointerStatement> ptrs = pointerMap.get(ref.getPath());
						if (ptrs == null) {
							ptrs = new ArrayList<PointerStatement>();
							ptrs.add(filepointer);
							pointerMap.put(ref.getPath(), ptrs);
							objMap.put(ref.getPath(), obj.getObjects());
						} else {
							ptrs.add(filepointer);
						}
					}
				}
			}
		}
		index = 1;
		boolean hasTables = false;
		for (String datafile : pointerMap.keySet()) {
			boolean printHeader = true;
			for (PointerStatement pointer : pointerMap.get(datafile)) {
				//System.out.println("pointer.getIdentifier().getId() = " + pointer.getIdentifier().getId() + "   datafile = " + datafile);
				List<ObjectStatement> objects = label.getObjects(
						pointer.getIdentifier().getId());		
        // PDS-540 check for null objects
				if (objects!=null) {
					if (!objects.isEmpty()) {
						// Assume only 1 object was returned for that identifier
						ObjectStatement object = objects.get(0);
						hasTables = true;
						if (printHeader) {
							writer.println("  Data file: " + datafile + "\n");
							printHeader = false;
						}
						printTableInfo(object, index);
					}
					else { // for PDS-539 (to handle nested object and pointers)
						objects = objMap.get(datafile);
						// PDS-540 (to handle unfound data objects)
						if (objects==null) {
							writer.println("  Error(s) with Table: " + pointer.getIdentifier().getId() + "\n");
						} 
						else {
							if (!objects.isEmpty()) {
								for (ObjectStatement obj: objects) {
									if (obj.getIdentifier().getId().endsWith("SPREADSHEET") ||
											obj.getIdentifier().getId().endsWith("TABLE") ||
											obj.getIdentifier().getId().endsWith("SERIES")) {
										hasTables = true;
										if (printHeader) {
											writer.println("  Data file: " + datafile + "\n");
											printHeader = false;
										}
										printTableInfo(obj, index);
									}
								} // end for
							} // end if (!objects.isEmpty())
						} // end else
					}
					index++;
				}
			}
		}
		if (!hasTables) {
			writer.println("  None Found\n");
		}
		writer.flush();
	}

	/**
	 * Lists the PDS4 objects supported by the tool.
	 *
	 * @param objectAccess Object representation of the PDS4 label.
	 * @param fileAreas A list of object representations of the file area
	 * element or elements within a given PDS4 label.
	 * @throws Exception
	 */
	private void list(ObjectProvider objectAccess,
			List<FileAreaObservational> fileAreas, File outputDir) throws Exception {
		writer.println("Supported Images: \n");
		boolean hasImages = false;
		for (FileAreaObservational fileArea : fileAreas) {
			List<Array> arrays = objectAccess.getArrays(fileArea);
			arrays = Utility.getSupportedImages(arrays);
			if (!arrays.isEmpty()) {
				boolean printHeader = true;
				hasImages = true;
				int index = 1;
				for (Array array : arrays) {
					String extension = FilenameUtils.getExtension(
							fileArea.getFile().getFileName());
					String dataType = array.getElementArray().getDataType();
					boolean supportedType = false;
					for (DataType.NumericDataType type : DataType.NumericDataType.values()) {
						if (type.toString().equalsIgnoreCase(dataType)) {
							supportedType = true;
							break;
						}
					}
					if (("fits".equalsIgnoreCase(extension) ||
							"fit".equalsIgnoreCase(extension)) || supportedType) {
						if (printHeader) {
							writer.println("  Data file: "
									+ fileArea.getFile().getFileName() + "\n");
							printHeader = false;
						}
						printImageInfo(array, index);
						index++;
					}
				}
			}
		}
		if (!hasImages) {
			writer.println("  None Found\n");
		}
		writer.println("Supported Tables: \n");
		boolean hasTables = false;
		for (FileAreaObservational fileArea : fileAreas) {
			List<Object> tables = objectAccess.getTableObjects(fileArea);
			if (!tables.isEmpty()) {
				hasTables = true;
				writer.println("  Data file: "
						+ fileArea.getFile().getFileName() + "\n");

				String datafileName = null;
				int index = 1;
				for (int i = 0; i < tables.size(); i++) {
					TableReader reader = null;
					try {
						URL url= objectAccess.getRoot();
						if (url.getProtocol().startsWith("http")) {
							String urlStr = url.toString(); 
							String urlLocation = urlStr.substring(0, urlStr.lastIndexOf('/'));
							URL datafileUrl = new URL(urlLocation+"/"+fileArea.getFile().getFileName());
							reader = ExporterFactory.getTableReader(tables.get(i), datafileUrl);
							datafileName = datafileUrl.toString();
						}
						else {
							File datafile = new File(FileUtils.toFile(objectAccess.getRoot()), fileArea.getFile().getFileName());
							reader = ExporterFactory.getTableReader(tables.get(i), datafile);
							datafileName = datafile.toString();
						}
					} catch (Exception ex) {
						throw new Exception("Error reading table in file '" + datafileName
								+ "': " + ex.getMessage());
					}
					printTableInfo(tables.get(i), index, reader.getFields());
					index++;
				}
			}
		}
		if (!hasTables) {
			writer.println("  None Found\n");
		}
		writer.flush();
	}

	/**
	 * Prints the PDS4 image information.
	 *
	 * @param image Object representation of a PDS4 Array 2D image.
	 * @param index The index of the image.
	 */
	private void printImageInfo(Array array, int index) {
		writer.println("    index = " + index);
		XmlType xmlType = array.getClass().getAnnotation(XmlType.class);
		writer.println("    object type = " + xmlType.name());
		writer.println("    name = " + array.getName());
		writer.println("    local identifier = " + array.getLocalIdentifier());
		writer.println("    data type = " + array.getElementArray().getDataType());
		for( AxisArray axisArray : array.getAxisArraies()) {
			if ("Line".equalsIgnoreCase(axisArray.getAxisName())) {
				writer.println("    lines = " + axisArray.getElements());
			} else if ("Sample".equalsIgnoreCase(axisArray.getAxisName())) {
				writer.println("    samples = " + axisArray.getElements());
			}
		}
		writer.println("");
	}

	/**
	 * Prints the PDS3 image information.
	 *
	 * @param image Object representation of a PDS3 image object.
	 * @param index The index of the image.
	 */
	private void printImageInfo(ObjectStatement image, int index) {
		writer.println("    index = " + index);
		writer.println("    name = " + image.getIdentifier().getId());
		if (image.getAttribute("LINES") != null) {
			writer.println("    lines = "
					+ image.getAttribute("LINES").getValue().toString());
		}
		if (image.getAttribute("LINE_SAMPLES") != null) {
			writer.println("    samples = "
					+ image.getAttribute("LINE_SAMPLES").getValue().toString());
		}
		if (image.getAttribute("SAMPLE_TYPE") != null) {
			writer.println("    sample type = "
					+ image.getAttribute("SAMPLE_TYPE").getValue().toString());
		}
		if (image.getAttribute("SAMPLE_BITS") != null) {
			writer.println("    sample bits = "
					+ image.getAttribute("SAMPLE_BITS").getValue().toString());
		}
		writer.println("");
	}

	/**
	 * Prints the PDS3 image information.
	 *
	 * @param image Object representation of a PDS3 image object.
	 * @param index The index of the image.
	 */
	private void printTableInfo(ObjectStatement table, int index) {
		writer.println("    index = " + index);
		writer.println("    name = " + table.getIdentifier().getId());
		if (table.getAttribute("COLUMNS") != null) {
			writer.println("    columns = "
					+ table.getAttribute("COLUMNS").getValue().toString());
		}
		if (table.getAttribute("ROWS") != null) {
			writer.println("    rows = "
					+ table.getAttribute("ROWS").getValue().toString());
		}
		if (table.getAttribute("ROW_BYTES") != null) {
			writer.println("    row bytes = "
					+ table.getAttribute("ROW_BYTES").getValue().toString());
		}
		if (table.getAttribute("INTERCHANGE_FORMAT") != null) {
			writer.println("    interchange format = "
					+ table.getAttribute("INTERCHANGE_FORMAT").getValue().toString());
		}
		writer.println("");
	}


	/**
	 * Prints the PDS4 table information.
	 *
	 * @param table Object representation of a PDS4 table.
	 * @param index The index of the table.
	 * @param fields A list of fields associated with the given table.
	 */
	private void printTableInfo(Object table, int index,
			FieldDescription[] fields) {
		if (table instanceof TableBinary) {
			TableBinary tableBinary = (TableBinary) table;
			writer.println("    index = " + index);
			XmlType xmlType = tableBinary.getClass().getAnnotation(XmlType.class);
			writer.println("    object type = " + xmlType.name());
			writer.println("    name = " + tableBinary.getName());
			writer.println("    local identifier = "
					+ tableBinary.getLocalIdentifier());
			writer.println("    records = " + tableBinary.getRecords());
			RecordBinary record = tableBinary.getRecordBinary();
			if (record != null) {
				if (record.getRecordLength() != null) {
					writer.println("    record length = "
							+ record.getRecordLength().getValue());
				}
				writer.println("    groups = " + record.getGroups());
				writer.println("    fields = " + record.getFields());
			}
		} else if (table instanceof TableCharacter) {
			TableCharacter tableChar = (TableCharacter) table;
			writer.println("    index = " + index);
			XmlType xmlType = tableChar.getClass().getAnnotation(XmlType.class);
			writer.println("    object type = " + xmlType.name());
			writer.println("    name = " + tableChar.getName());
			writer.println("    local identifier = "
					+ tableChar.getLocalIdentifier());
			writer.println("    records = " + tableChar.getRecords());
			RecordCharacter record = tableChar.getRecordCharacter();
			if (record != null) {
				if (record.getRecordLength() != null) {
					writer.println("    record length = "
							+ record.getRecordLength().getValue());
				}
				writer.println("    groups = " + record.getGroups());
				writer.println("    fields = " + record.getFields());
			}
		} else if (table instanceof TableDelimited) {
			TableDelimited tableDelim = (TableDelimited) table;
			writer.println("    index = " + index);
			XmlType xmlType = tableDelim.getClass().getAnnotation(XmlType.class);
			writer.println("    object type = " + xmlType.name());
			writer.println("    name = " + tableDelim.getName());
			writer.println("    local identifier = "
					+ tableDelim.getLocalIdentifier());
			writer.println("    records = " + tableDelim.getRecords());
			RecordDelimited record = tableDelim.getRecordDelimited();
			if (record != null) {
				if (record.getMaximumRecordLength() != null) {
					writer.println("    max record length = "
							+ record.getMaximumRecordLength().getValue());
				}
				writer.println("    groups = " + record.getGroups());
				writer.println("    fields = " + record.getFields());
			}
		}
		writer.println("");
	}
}
