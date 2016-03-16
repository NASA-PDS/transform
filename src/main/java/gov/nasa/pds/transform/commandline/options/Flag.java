// Copyright 2006-2016, by the California Institute of Technology.
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
package gov.nasa.pds.transform.commandline.options;

import gov.nasa.pds.transform.constants.Constants;

import org.apache.commons.cli.Options;

/**
 * Class that holds the command-line option flags.
 *
 * @author mcayanan
 *
 */
public enum Flag {
  /**
   * Flag to specify the band strengths for the red, green, and blue components.
   * 
   */
/*  
  BANDS("b", "bands", "band list", int.class, true, "For transformations of "
      + "PDS4 Array_3D_Spectrum products, specify this flag to "
      + "indicate the bands to use for the red, green and blue "
      + "channels (i.e. -b 1,2,3) in order to get a 3-color RGB "
      + "representative image. Default is to use band 1."),
*/  
  /** Flag to specify a configuration file to configure the tool behavior.
   */
  CONFIG("c", "config", "file", String.class, "Specify a configuration "
      + "file to set the tool behavior."),

  /**
   * Flag to specify file patterns to look for when validating a target
   * directory.
   */
  REGEXP("e", "regexp", "patterns", String.class, true, "Specify file patterns "
      + "to look for when validating a directory. Each pattern should "
      + "be surrounded by quotes. (i.e. -e \"*.xml\")"),

  /**
   * Flag to display the help.
   */
  HELP("h", "help", "Display usage."),

  /**
   * Flag to specify a report file name.
   */
  REPORT("r", "report-file", "file name", String.class, "Specify the "
      + "report file name. Default is standard out."),

  /**
   * Flag to explicitly specify the target to validate.
   */
  TARGET("t", "target", "files", String.class, true, "Explicitly specify "
      + "the targets to transform. The targets can be "
      + "specified implicitly as well. "
      + "(example: transform array2DImage.xml)"),

  OUTPUTDIR("o", "output-dir", "file", String.class,
      "Specify an output directory."),

  FORMAT("f", "format-type", "type", String.class,
      "Specify the transformation format type to perform on the target. "
      + "Valid format types are the following: "
      + Constants.COMMON_VALID_FORMATS + ". "
      + "For PDS4 products, the following additional formats are "
      + "available: " + Constants.PDS4_ONLY_VALID_FORMATS
      + ". For PDS3 products, the following additional formats are "
      + "available: " + Constants.PDS3_ONLY_VALID_FORMATS),

  /**
   * Flag to specify the severity level and above to include in the report.
   */
  VERBOSE("v", "verbose", "1|2|3", short.class, "Specify the severity "
      + "level and above to include in the human-readable report: "
      + "(1=Info, 2=Warning, 3=Error). Default is Warning and above. "),

  /**
   * Flag that disables recursion when traversing a target directory.
   */
  LOCAL("L", "local", "Validate files only in the target directory rather "
      + "than recursively traversing down the subdirectories."),

  /**
   * Displays the tool version.
   */
  VERSION("V", "version", "Display application version."),

  /**
   * Flag to specify the index of the image or table to transform.
   */
  INDEX("n", "index", "value", String.class, "Specify the index of the image "
      + "or table to transform. Default is set to 1 (the first one). "),

  /**
   * Flag to specify the data file(s) to transform.
   */
  DATAFILE("d", "datafile", "file", String.class, "Specify the data "
      + "file to transform. The default is to transform the first data "
      + "file found."),

  ALL("a", "all", "Specify to transform all data files found in the given "
      + "label."),

  /**
   * Flag to display a list of images and tables found within a given label.
   * Additionally,
   */
  OBJECTS("O", "list-objects", "List the table and image objects found within "
      + "a given label that are currently supported by the tool.");

  /** The short name of the flag. */
  private final String shortName;

  /** The long name of the flag. */
  private final String longName;

  /** An argument name for the flag, if it accepts argument values. */
  private final String argName;

  /** The type of argument values the flag accepts. */
  private final Object argType;

  /** A boolean value indicating if the flag accepts more than one
   * argument.
   */
  private final boolean allowsMultipleArgs;

  /** The flag description. */
  private final String description;

  /** A list of Option objects for command-line processing. */
  private static Options options;

  /**
   * Constructor.
   *
   * @param shortName The short name.
   * @param longName The long name.
   * @param description A description of the flag.
   */
  private Flag(final String shortName, final String longName,
      final String description) {
    this(shortName, longName, null, null, description);
  }

  /**
   * Constructor for flags that can take arguments.
   *
   * @param shortName The short name.
   * @param longName The long name.
   * @param argName The argument name.
   * @param argType The argument type.
   * @param description A description of the flag.
   */
  private Flag(final String shortName, final String longName,
      final String argName, final Object argType,
      final String description) {
    this(shortName, longName, argName, argType, false, description);
  }

  /**
   * Constructor for flags that can take arguments.
   *
   * @param shortName The short name.
   * @param longName The long name.
   * @param argName The argument name.
   * @param argType The argument type.
   * @param allowsMultipleArgs Flag to indicate if multiple arguments are
   * allowed.
   * @param description A description of the flag.
   */
  private Flag(final String shortName, final String longName,
      final String argName, final Object argType,
      final boolean allowsMultipleArgs, final String description) {
    this.shortName = shortName;
    this.longName = longName;
    this.argName = argName;
    this.argType = argType;
    this.allowsMultipleArgs = allowsMultipleArgs;
    this.description = description;
  }

  /**
   * Get the short name of the flag.
   *
   * @return The short name.
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Get the long name of the flag.
   *
   * @return The long name.
   */
  public String getLongName() {
    return longName;
  }

  /**
   * Get the argument name of the flag.
   *
   * @return The argument name.
   */
  public String getArgName() {
    return argName;
  }

  /**
   * Find out if the flag can handle multiple arguments.
   *
   * @return 'true' if yes.
   */
  public boolean allowsMultipleArgs() {
    return allowsMultipleArgs;
  }

  /**
   * Get the argument type of the flag.
   *
   * @return The argument type.
   */
  public Object getArgType() {
    return argType;
  }

  /**
   * Get the flag description.
   *
   * @return The description.
   */
  public String getDescription() {
    return description;
  }

  static {
    options = new Options();
    
    options.addOption(new ToolsOption(ALL));
//    options.addOption(new ToolsOption(BANDS));
    options.addOption(new ToolsOption(DATAFILE));
    options.addOption(new ToolsOption(FORMAT));
    options.addOption(new ToolsOption(HELP));
    options.addOption(new ToolsOption(INDEX));
    options.addOption(new ToolsOption(OBJECTS));
    options.addOption(new ToolsOption(OUTPUTDIR));
    options.addOption(new ToolsOption(TARGET));
    options.addOption(new ToolsOption(VERSION));
  }

  /**
   * Get the command-line options.
   *
   * @return A class representation of the command-line options.
   */
  public static Options getOptions() {
    return options;
  }
}
