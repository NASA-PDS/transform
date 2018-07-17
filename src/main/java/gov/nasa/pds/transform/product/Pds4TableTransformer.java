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
package gov.nasa.pds.transform.product;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.ParseException;
import gov.nasa.pds.objectAccess.TableReader;
import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.product.label.TableLabelTransformer;
import gov.nasa.pds.transform.product.label.TableLabelTransformerFactory;
import gov.nasa.pds.transform.table.TableExtractor;
import gov.nasa.pds.transform.util.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

/**
 * Class that transforms PDS4 tables into CSV formatted-files.
 *
 * @author mcayanan
 *
 */
public class Pds4TableTransformer extends DefaultTransformer {

	protected URL dataFileBasePath;

	protected TableExtractor tableExtractor;

	protected boolean displayHeaders;

	/**
	 * Constructor to set the flag to overwrite outputs.
	 *
	 * @param overwrite Set to true to overwrite outputs, false otherwise.
	 */
	public Pds4TableTransformer(boolean overwrite) {
		super(overwrite);
		dataFileBasePath = null;
		this.tableExtractor = new TableExtractor();
		displayHeaders = true;
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
	/*
  public List<File> transform(File target, File outputDir, String format,
      String dataFileName, int index)
  throws TransformException {
    try {
      return transform(target.toURI().toURL(), outputDir, format, dataFileName, index);
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }

    tableExtractor.setFormat(format);
    File result = null;
    try {
      ObjectAccess objectAccess = new ObjectAccess(
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
            break;
          } else {
            if (fa.getFile().getFileName().equals(dataFileName)) {
              fileArea = fa;
              break;
            }
          }
        }
      }
      if (fileArea != null) {
        URL base = null;
        if (dataFileBasePath != null) {
          base = dataFileBasePath;
        } else {
          base = target.getParentFile().toURI().toURL();
        }
        URL dataFile = new URL(base, dataFileName);
        File outputFile = Utility.createOutputFile(new File(dataFileName),
            outputDir, format);
        Object tableObject = process(target.toURI().toURL(), dataFile, outputFile, objectAccess, fileArea, index);
        // fix for PDS-353 (PDS4_TO_CSV)
        result = outputFile;
        Map<File, Object> outputsToTables = new LinkedHashMap<File, Object>();
        outputsToTables.put(outputFile, tableObject);
        create_delimited_table_label(objectAccess, target.toURI().toURL(), outputsToTables, outputDir);
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
    } catch (URISyntaxException e) {
      throw new TransformException(e.getMessage());
    } catch (Exception e) {
      throw new TransformException(e.getMessage());
    }

  }
	 */

	public List<File> transform(URL url, File outputDir, String format,
			String dataFileName, int index) throws TransformException, URISyntaxException, Exception {
		tableExtractor.setFormat(format);
		List<File> results = new ArrayList<File>();
		try {
			ObjectAccess objectAccess = new ObjectAccess(url);
			List<FileAreaObservational> fileAreas = Utility.getFileAreas(url);
			FileAreaObservational fileArea = null;
			if (fileAreas.isEmpty()) {
				throw new TransformException("Cannot find File_Area_Observational "
						+ "area in the label: " + url.toString());
			} else {
				for (FileAreaObservational fa : fileAreas) {
					if (dataFileName.isEmpty()) {
						fileArea = fileAreas.get(0);
						dataFileName = fileArea.getFile().getFileName();
						break;
					} else {
						if (fa.getFile().getFileName().equals(dataFileName)) {
							fileArea = fa;
							break;
						}
					}
				}
			}
			
			if (fileArea != null) {
				URL base = null;
				if (dataFileBasePath != null) {
					base = dataFileBasePath;
				} else {
					base = Utility.getParent(url);
				}
				URL dataFile = new URL(base, dataFileName);
				
				// PDS-540 & PDS-550
				if (!exists(dataFile)) {
					boolean hasUppercase = !dataFileName.equals(dataFileName.toLowerCase());
					boolean hasLowercase = !dataFileName.equals(dataFileName.toUpperCase());
					if (hasUppercase)
						dataFileName = dataFileName.toLowerCase();
					if (hasLowercase)
						dataFileName = dataFileName.toUpperCase();								
					dataFile = new URL(base, dataFileName);

					if (!exists(dataFile)) {				
						throw new TransformException("No table object '" + index + "' is found in the label. Please check the label.");	
					}
				}
					
				File outputFile = Utility.createOutputFile(new File(dataFileName),
						outputDir, format);

				Object tableObject = process(url, dataFile, outputFile, objectAccess, fileArea, index);
				// fix for PDS-353 (PDS4_TO_CSV)
				results.add(outputFile);
				Map<File, Object> outputsToTables = new LinkedHashMap<File, Object>();
				if (tableObject != null) {
					outputsToTables.put(outputFile, tableObject);
				}
				try {
					if (!outputsToTables.isEmpty()) {
						results.add(create_delimited_table_label(objectAccess, url, outputsToTables, outputDir));
					}
				} catch (Exception e) {
					throw new TransformException("Error while creating Table_Delimited label: " + e.getMessage());
				}
			} else {
				String message = "";
				if (dataFileName.isEmpty()) {
					message = "No data file(s) found in label.";
				} else {
					message = "No data file '" + dataFileName + "' found in label.";
				}
				throw new TransformException(message);
			}
			return results;
		} catch (ParseException p) {
			throw new TransformException("Error occurred while parsing label '"
					+ url.toString() + "': " + p.getMessage());
		} catch (IOException i) {
			throw new TransformException("Error occurred while resolving the path "
					+ "of the label: " + i.getMessage());
		} catch (URISyntaxException e) {
			throw new TransformException(e.getMessage());
		}
	}
	
  //PDS-540
	private static boolean exists(URL aURL) {
		boolean result = false;
		try {
			InputStream input = aURL.openStream();
			result = true;
		} catch (Exception ex) {
		} 
		return result;
	}
	
	@Override
	public List<File> transformAll(URL url, File outputDir, String format)
			throws TransformException {
		tableExtractor.setFormat(format);
		List<File> outputs = new ArrayList<File>();
		try {
			ObjectAccess objectAccess = new ObjectAccess(url);
			List<FileAreaObservational> fileAreas = Utility.getFileAreas(url);
			if (fileAreas.isEmpty()) {
				throw new TransformException("Cannot find File_Area_Observational "
						+ "area in the label: " + url.toString());
			} else {
				Map<File, Object> outputsToTables = new LinkedHashMap<File, Object>();
				for (FileAreaObservational fileArea : fileAreas) {
					List<Object> tables = objectAccess.getTableObjects(fileArea);
					if (!tables.isEmpty()) {
						int numTables = tables.size();
						String dataFilename = fileArea.getFile().getFileName();
						URL base = null;
						if (dataFileBasePath != null) {
							base = dataFileBasePath;
						} else {
							base = Utility.getParent(url);
						}
						URL dataFile = new URL(base, dataFilename);
					  
						for (int i = 0; i < numTables; i++) {
							File outputFile = null;
							if (objectAccess.getTableObjects(fileArea).size() > 1) {
								outputFile = Utility.createOutputFile(new File(dataFilename),
										outputDir, format, (i+1));
							} else {
								outputFile = Utility.createOutputFile(new File(dataFilename),
										outputDir, format);
							}
							
						  // PDS-540 & PDS-550
							if (!exists(dataFile)) {
								boolean hasUppercase = !dataFilename.equals(dataFilename.toLowerCase());
								boolean hasLowercase = !dataFilename.equals(dataFilename.toUpperCase());
								if (hasUppercase)
									dataFilename = dataFilename.toLowerCase();
								if (hasLowercase)
									dataFilename = dataFilename.toUpperCase();
								dataFile = new URL(base, dataFilename);

								if (!exists(dataFile)) {
									log.log(new ToolsLogRecord(ToolsLevel.ERROR,
											"No table object '" + i + "' is found in the label. Please check the label.", url.toString()));
									continue;
								}
							}

							try {
								Object tableObject = process(url, dataFile, outputFile, objectAccess, fileArea,
										(i+1));
								outputs.add(outputFile);
								if (tableObject != null) {
									outputsToTables.put(outputFile, tableObject);
								}
							} catch (Exception e) {
								log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
										url.toString()));
							}
						}
					} else {
						log.log(new ToolsLogRecord(ToolsLevel.INFO,
								"No table objects are found in the label.", url.toString()));
					}
				}
				try {
					if (!outputsToTables.isEmpty()) {
						outputs.add(create_delimited_table_label(
								objectAccess, url, outputsToTables, outputDir));
					}
				} catch (Exception e) {
					throw new TransformException("Error while creating Table_Delimited label: "
							+ e.getMessage());
				}
			}
			return outputs;
		} catch (ParseException p) {
			throw new TransformException("Error occurred while parsing label '"
					+ url.toString() + "': " + p.getMessage());
		} catch (IOException i) {
			throw new TransformException("Error occurred while resolving the path "
					+ "of the label: " + i.getMessage());
		} catch (URISyntaxException ue) {
			throw new TransformException(ue.getMessage());
		}
	}

	protected Object process(URL target, URL dataFile, File outputFile,
			ObjectProvider objectAccess, FileAreaObservational fileArea, int index)
					throws TransformException {
		Object tableObject = null;
		if ((outputFile.exists() && outputFile.length() != 0)
				&& !overwriteOutput) {
			log.log(new ToolsLogRecord(ToolsLevel.INFO,
					"Output file already exists. No transformation will occur: "
							+ outputFile.toString(), target.toString()));
		} else {
			List<Object> tableObjects = objectAccess.getTableObjects(fileArea);
			if (objectAccess.getTableObjects(fileArea).isEmpty()) {
				log.log(new ToolsLogRecord(ToolsLevel.INFO,
						"No table objects are found in the label.", target.toString()));
			} else {
				try {
					tableObject = tableObjects.get(index-1);
				} catch (IndexOutOfBoundsException ie) {
					throw new TransformException("Index '" + index
							+ "' is greater than the maximum number of tables found '"
							+ "for data file '" + fileArea.getFile().getFileName() + "': "
							+ tableObjects.size());
				} catch (IllegalArgumentException ae) {

				}
				PrintWriter fileWriter = null;
				try {
					fileWriter = new PrintWriter(new FileWriter(outputFile));
					tableExtractor.setOutput(fileWriter);
					log.log(new ToolsLogRecord(ToolsLevel.INFO,
							"Transforming table '" + index + "' of file: "
									+ dataFile.toString(), target.toString()));

					TableReader reader = null;
					URL url = objectAccess.getRoot();
					if (url.getProtocol().startsWith("http")) {
						String urlStr = url.toString(); 
						String urlLocation = urlStr.substring(0, urlStr.lastIndexOf('/'));
						//	URL datafileUrl = new URL(urlLocation+"/"+dataFile.getName());
						//	reader = ExporterFactory.getTableReader(tableObject, datafileUrl);
						reader = ExporterFactory.getTableReader(tableObject, dataFile);
					}
					else 
						reader = ExporterFactory.getTableReader(tableObject, dataFile);

					tableExtractor.extractTable(reader, false);
				} catch (IOException io) {
					throw new TransformException("Cannot open output file \'"
							+ outputFile.toString() + "': " + io.getMessage());
				} catch (Exception e) {
					throw new TransformException(
							"Error occurred while reading table '" + index
							+ "' of file '" + dataFile.toString() + "': "
							+ e.getMessage());
				} finally {
					fileWriter.close();
				}
				log.log(new ToolsLogRecord(ToolsLevel.INFO,
						"Successfully transformed table '" + index
						+ "' to the following output: " + outputFile.toString(),
						target.toString()));
			}
		}
		return tableObject;
	}

	/*
  @Override
  public List<File> transformAll(File target, File outputDir, String format)
      throws TransformException {
    try {
      return transformAll(target.toURI().toURL(), outputDir, format);
    } catch (MalformedURLException u) {
      throw new TransformException(u.getMessage());
    }
  }
	 */

	public void setDataFileBasePath(String base) 
			throws MalformedURLException {
		try {
			URL b = new URL(base);
			this.dataFileBasePath = b;
		} catch (MalformedURLException e) {
			File file = new File(base);
			this.dataFileBasePath = file.toURI().normalize().toURL(); 
		}
	}

	public void setDisplayHeaders(boolean flag) {
		this.displayHeaders = flag;
	}

	/**
	 * Creates the Table_Delimited label.
	 * 
	 * @param objectAccess The ObjectAccess object.
	 * @param target The target label.
	 * @param outputsToTables A mapping of the outputs to table objects.
	 * @param outputDir The directory where to write the label.
	 * 
	 * @return A PDS4 Table_Delimited product label.
	 * 
	 * @throws Exception If an error occurred during the label creation
	 * process.
	 */
	private File create_delimited_table_label(ObjectAccess objectAccess, 
			URL target, Map<File, Object> outputsToTables, File outputDir)
					throws Exception {
		ProductObservational product = objectAccess.getObservationalProduct(
				FilenameUtils.getName(target.toString()));
		product.getFileAreaObservationals().clear();
		TableLabelTransformerFactory factory = TableLabelTransformerFactory.getInstance();
		for (Map.Entry<File, Object> entry : outputsToTables.entrySet()) {
			File output = entry.getKey();
			Object table = entry.getValue();
			TableLabelTransformer<?> labelTransformer = factory.newInstance(table);
			TableDelimited tableDelimited = labelTransformer.toTableDelimited(table);
			FileAreaObservational fao = new FileAreaObservational();
			fao.getDataObjects().add(tableDelimited);
			gov.nasa.arc.pds.xml.generated.File file = 
					new gov.nasa.arc.pds.xml.generated.File();
			file.setFileName(FilenameUtils.getName(output.toString()));
			fao.setFile(file);
			product.getFileAreaObservationals().add(fao);
		}
		// transform the label
		String labelname = FilenameUtils.getBaseName(target.toString())
				+ "_delimited_csv.xml";
		ObjectAccess outputProduct = new ObjectAccess(outputDir.toString());
		outputProduct.setObservationalProduct(labelname.toString(), product, 
				objectAccess.getXMLLabelContext());
		File delimitedLabel = new File(outputDir, labelname);
		log.log(new ToolsLogRecord(ToolsLevel.INFO,
				"Successfully created Table_Delimited label: "
						+ delimitedLabel.toString(), target.toString()));
		return delimitedLabel;
	}
}
