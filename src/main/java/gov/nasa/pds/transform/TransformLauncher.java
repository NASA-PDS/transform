// Copyright © 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
// 
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// • Redistributions of source code must retain the above copyright notice,
//   this list of conditions and the following disclaimer.
// • Redistributions must reproduce the above copyright notice, this list of
//   conditions and the following disclaimer in the documentation and/or other
//   materials provided with the distribution.
// • Neither the name of Caltech nor its operating division, the Jet Propulsion
//   Laboratory, nor the names of its contributors may be used to endorse or
//   promote products derived from this software without specific prior written
//   permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.transform;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import gov.nasa.pds.transform.commandline.options.Flag;
import gov.nasa.pds.transform.commandline.options.InvalidOptionException;
import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;
import gov.nasa.pds.transform.logging.format.TransformFormatter;
import gov.nasa.pds.transform.logging.handler.TransformStreamHandler;
import gov.nasa.pds.transform.product.Pds3ImageTransformer;
import gov.nasa.pds.transform.product.Pds3LabelTransformer;
import gov.nasa.pds.transform.product.Pds3TableTransformer;
import gov.nasa.pds.transform.product.Pds4ImageTransformer;
import gov.nasa.pds.transform.product.ProductTransformer;
import gov.nasa.pds.transform.product.ProductTransformerFactory;
import gov.nasa.pds.transform.util.ObjectsReport;
import gov.nasa.pds.transform.util.ToolInfo;
import gov.nasa.pds.transform.util.Utility;

/**
 * Front end class of the Transform Tool that handles the command-line
 * processing.
 *
 * @author mcayanan
 *
 */
public class TransformLauncher {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      TransformLauncher.class.getName());

  /** The output directory. */
  private File outputDir;

  /** A list of targets to transform. */
  private List<URL> targets;

  private String dataFileName;

  /** A format type for the transformation. */
  private String formatType;

  /**
   * The index of the image or table to transform.
   */
  private int index;

  /**
   * Flag to indicate to transform all images or tables
   * found within a given label.
   */
  private boolean transformAll;

  /**
   * Flag to display image and table objects found within
   * a given label.
   */
  private boolean listObjects;

  private List<Integer> bands;

  private List<String> includePaths;
  
  /**
   * Constructor.
   * @throws IOException
   */
  public TransformLauncher() throws IOException {
    outputDir = new File(".").getCanonicalFile();
    targets = new ArrayList<URL>();
    formatType = "";
    index = 1;
    transformAll = false;
    dataFileName = "";
    listObjects = false;
    bands = new ArrayList<Integer>();
    includePaths = new ArrayList<String>();
  }

  /**
   * A method to parse the command-line arguments.
   *
   * @param args The command-line arguments
   * @return A class representation of the command-line arguments
   *
   * @throws ParseException If there was an error during parsing.
   */
  public final CommandLine parse(final String[] args)
  throws ParseException {
    CommandLineParser parser = new GnuParser();
    return parser.parse(Flag.getOptions(), args);
  }
  /**
   * Examines the command-line arguments passed into the Harvest Tool
   * and takes the appropriate action based on what flags were set.
   *
   * @param line A class representation of the command-line arguments.
   *
   * @throws Exception If there was an error while querying the options
   * that were set on the command-line.
   */
  public final void query(final CommandLine line)
  throws InvalidOptionException, IOException {
    List<String> targetList = new ArrayList<String>();
    for (Iterator<String> i = line.getArgList().iterator(); i.hasNext();) {
      String[] values = i.next().split(",");
      for (int index = 0; index < values.length; index++) {
        targetList.add(values[index].trim());
      }
    }
    List<Option> processedOptions = Arrays.asList(line.getOptions());
    for (Option o : processedOptions) {
      if (o.getOpt().equals(Flag.HELP.getShortName())) {
        displayHelp();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
        displayVersion();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.TARGET.getShortName())) {
        targetList.addAll(o.getValuesList());
      } else if (o.getOpt().equals(Flag.OUTPUTDIR.getShortName())) {
        setOutputDir(o.getValue());
      } else if (o.getOpt().equals(Flag.FORMAT.getShortName())) {
        setFormatType(o.getValue().toLowerCase());
      } else if (o.getOpt().equals(Flag.INDEX.getShortName())) {
        try {
        setIndex(o.getValue());
        } catch (Exception e) {
          throw new InvalidOptionException(e.getMessage());
        }
      } else if (o.getOpt().equals(Flag.DATAFILE.getShortName())) {
        setDataFileName(o.getValue());
      } else if (o.getOpt().equals(Flag.ALL.getShortName())) {
        transformAll = true;
      } else if (o.getOpt().equals(Flag.OBJECTS.getShortName())) {
        listObjects = true;
      } else if (o.getOpt().equals(Flag.INCLUDES.getShortName())) {
        setIncludePaths(o.getValuesList());
      } /* else if (o.getOpt().equals(Flag.BANDS.getShortName())) {
        setBands(o.getValuesList());
      }*/
    }
    setLogger();
    if (!targetList.isEmpty()) {
      setTargets(targetList);
    } else {
      throw new InvalidOptionException("No target specified.");
    }
    if (formatType.isEmpty() && (!listObjects)) {
      throw new InvalidOptionException("-f flag option is required.");
    }
    if (transformAll && (!dataFileName.isEmpty() || index != 1)) {
      throw new InvalidOptionException("Cannot specify the '-a' flag "
          + "option with the '-d' flag and/or '-n' flag option");
    }
  }

  /**
   * Displays tool usage.
   *
   */
  public final void displayHelp() {
    int maxWidth = 80;
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(maxWidth, "transform <options>",
        null, Flag.getOptions(), null);
  }

  /**
   * Displays the current version and disclaimer notice.
   *
   */
  public final void displayVersion() {
    System.err.println("\n" + ToolInfo.getName());
    System.err.println(ToolInfo.getVersion());
    System.err.println("Release Date: " + ToolInfo.getReleaseDate());
    System.err.println(ToolInfo.getCopyright() + "\n");
  }
  
  /**
   * Set the target.
   *
   * @param targets A list of targets.
   * @throws MalformedURLException
   */
  public void setTargets(List<String> targets)
  throws MalformedURLException {
    this.targets.clear();
    while (targets.remove(""));
    for (String t : targets) {
      URL url = null;
      try {
        url = new URL(t);
        this.targets.add(url);
      } catch (MalformedURLException u) {
        File file = new File(t);
        this.targets.add(file.toURI().normalize().toURL());
      }
    }
  }
  
  private void setBands(List<String> bands) throws IOException {
    while (bands.remove(""));
    if (bands.size() != 3) {
      throw new IOException("Must specify 3 positive integer values "
          + "when using the '-b' flag option (i.e. -b 1,2,3).");
    }
    for (String b : bands) {
      try {
        Integer i = new Integer(b);
        if (i.intValue() < 0) {
          throw new NumberFormatException("Must enter a positive integer");
        }
        this.bands.add(i);
      } catch (NumberFormatException e) {
        throw new IOException("Invalid value entered for '-b' flag option '"
            + b + "'. Must be a positive integer.");
      }
    }
  }

  /**
   * Set the paths to search for files referenced by pointers.
   * <p>
   * Default is to always look first in the same directory
   * as the label, then search specified directories.
   * @param i List of paths
   */
  public void setIncludePaths(List<String> i) {
    this.includePaths = new ArrayList<String> (i);
    while(this.includePaths.remove(""));
  }
  
  /**
   * Set the output directory.
   *
   * @param output A directory.
   */
  private void setOutputDir(String outputDir) {
    this.outputDir = new File(outputDir);
    if (!this.outputDir.exists()) {
      this.outputDir.mkdirs();
    }
  }

  /**
   * Set the format type to use for the transformation.
   *
   * @param formatType A format type.
   */
  private void setFormatType(String formatType) {
    this.formatType = formatType;
  }

  private void setIndex(String value) throws Exception {
    try {
      index = Integer.parseInt(value);
      if (index < 0) {
        throw new Exception("Index value must be a positive integer.");
      }
    } catch (NumberFormatException ne) {
        throw new Exception("Index value not a valid number: " + value);
    }
  }

  private void setDataFileName(String value) {
    this.dataFileName = value;
  }

  /**
   * Sets the appropriate handlers for the logging.
   *
   * @throws IOException If a log file was specified and could not
   * be read.
   */
  private void setLogger() throws IOException {
    Logger logger = Logger.getLogger("");
    logger.setLevel(Level.INFO);
    Handler []handler = logger.getHandlers();
    for (int i = 0; i < logger.getHandlers().length; i++) {
      logger.removeHandler(handler[i]);
    }
    logger.addHandler(new TransformStreamHandler(System.out,
        new TransformFormatter()));
  }

  /**
   * Log the report header.
   *
   */
  private void logHeader() {
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "PDS Transform Tool Log\n"));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Version                     " + ToolInfo.getVersion()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Time                        " + Utility.getDateTime()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Target                      " + targets.toString()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Output Directory            " + outputDir.toString()));
    if (transformAll) {
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Transform All               true"));
    } else {
      if (!dataFileName.isEmpty()) {
        log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
          "Data File                   " + dataFileName));
      }
      log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
          "Index                       " + index));
    }
    if (!bands.isEmpty()) {
      log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
          "Selected Bands              " + bands.toString()));
    }
    if (!includePaths.isEmpty()) {
      log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Include Paths                  " + includePaths.toString()));
    } 
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Format Type                 " + formatType + "\n"));   
  }

  /**
   *
   * Do the transformation.
   */
  private List<File> doTransformation() throws Exception {
    ProductTransformerFactory factory = ProductTransformerFactory.getInstance();
    List<File> results = new ArrayList<File>();
    try {
      ProductTransformer pt = factory.newInstance(targets.get(0), formatType);      
      if (pt instanceof Pds3ImageTransformer) {
      	//System.out.println("Pds3ImageTransformer.....");
        Pds3ImageTransformer pds3Transformer = (Pds3ImageTransformer) pt;
        if (transformAll) {
        	// need to write in Pds3ImageTransformer
          results = pds3Transformer.transformAll(targets, outputDir, formatType);
        } else {
        	for (URL target: targets) {
        		try {
        			if (target.getProtocol().startsWith("http")) {
        				results.addAll(pds3Transformer.transform(target, outputDir, formatType,
        						dataFileName, index));
        			}
        			else {
        				results.addAll(pds3Transformer.transform(new File(target.toURI()), outputDir,
        						formatType, dataFileName, index));
        			}
        		} catch (TransformException t) {
        			log.log(new ToolsLogRecord(ToolsLevel.SEVERE, t.getMessage(), target.toString()));
        		}
        	}
        }
      } else {
        if (pt instanceof Pds4ImageTransformer) {
        	//System.out.println("Pds3ImageTransformer.....");
          if (!bands.isEmpty()) {
            ((Pds4ImageTransformer) pt).setBands(bands);
          }
        }
        if (pt instanceof Pds3TableTransformer) {
        	//System.out.println("Pds3TableTransformer.....");
          ((Pds3TableTransformer) pt).setIncludePaths(includePaths);
        }
        if (pt instanceof Pds3LabelTransformer) {
        	//System.out.println("Pds3LabelTransformer.....");
          ((Pds3LabelTransformer) pt).setIncludePaths(includePaths);
        }
        if (transformAll) {
        	// write in Pds4ImageTransformer, Pds3TableTransformer, Pds3LabelTransformer
        	results = pt.transformAll(targets, outputDir, formatType);      	
        } else {
        	for (URL target: targets) {
        		try {
        			if (target.getProtocol().startsWith("http")) {
        				results.addAll(pt.transform(target, outputDir, formatType,
        					dataFileName, index));
        			}
        			else {
        				results.addAll(pt.transform(new File(target.toURI()), outputDir, formatType,
            					dataFileName, index));
        			}
        		} catch (TransformException t) {
        			log.log(new ToolsLogRecord(ToolsLevel.SEVERE, t.getMessage(), target.toString()));
        		}
        	}
        }
      }
    } catch (TransformException t) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, t.getMessage()));
    } 
    catch (Exception ex) {
      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, ex.getMessage()));
    }
    return results;
  }

  private List<File> processMain(String[] args) {
	List<File> results = null;
    try {
      CommandLine cmdLine = parse(args);
      query(cmdLine);
      if (listObjects) {
        ObjectsReport objects = new ObjectsReport();
        for (URL url: targets) {
        	if (url.getProtocol().startsWith("http"))
        		objects.list(url, outputDir);
        	else
        		objects.list(new File(url.toURI()));
        }
      } else {
        logHeader();
        results = doTransformation();
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    return results;
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("\nType 'transform -h' for usage");
      System.exit(0);
    }
    List<File> results = new TransformLauncher().processMain(args);
    
    // TODO TODO TODO: how to send output info when executing the transform tool via Tool-Service
    if (results != null)
    	System.out.println("outputs = " + results.toString());
  }
}
