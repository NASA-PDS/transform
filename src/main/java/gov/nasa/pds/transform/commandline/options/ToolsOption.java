// Copyright 2006-2012, by the California Institute of Technology.
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

import org.apache.commons.cli.Option;

/**
 * Class that extends Apache's Option class. Provides a simpler interface to
 * build command-line option flags.
 *
 *
 * @author mcayanan
 *
 */
public class ToolsOption extends Option {
    /**
     * Constructor.
     *
     * @param opt Short name of the option.
     * @param longOpt Long name of the option. Can be set to 'null'.
     * @param description Description of the option.
     */
    public ToolsOption(final String opt, final String longOpt,
            final String description) {
        super(opt, longOpt, false, description);
    }

    /**
     * Constructor.
     *
     * @param flag An object representation of the command-line flag option.
     */
    public ToolsOption(final Flag flag) {
        this(flag.getShortName(), flag.getLongName(), flag.getDescription());
        if (flag.getArgType() != null) {
            if (flag.allowsMultipleArgs()) {
                hasArgs(flag.getArgName(), flag.getArgType());
            } else {
                hasArg(flag.getArgName(), flag.getArgType());
            }
        }
    }

    /**
     * Requires a single argument to follow the option.
     *
     * @param name Sets the display name of the argument value.
     * @param type Sets the data type allowed for this argument.
     */
    public void hasArg(final String name, final Object type) {
        hasArg(name, type, false);
    }

    /**
     * Allows a single argument to be passed into the option.
     *
     * @param name Sets the display name of the argument value.
     * @param type Sets the data type allowed for this argument.
     * @param isOptional Set to 'true' if the argument is optional,
     * 'false' otherwise.
     */
    public void hasArg(final String name, final Object type,
            final boolean isOptional) {
        final char nullChar = '\0';
        hasArgs(1, name, type, nullChar, isOptional);
    }

    /**
     * Requires an argument to follow the option. This method allows the
     * option to take in multiple arguments. Does not define a maximum
     * number of allowable arguments.
     *
     * The separator value is set to the space character ' '.
     *
     * @param name Sets the display name of the argument value.
     * @param type Sets the data type allowed for this argument.
     */
    public void hasArgs(final String name, final Object type) {
        final char argSeparator = ',';
        hasArgs(name, type, argSeparator, false);
    }

    /**
     * Requires an argument to follow the option. Allows multiple arguments
     * to be passed in to the option. Does not define a maximum number of
     * allowable arguments.
     *
     *
     * @param name Sets the display name of the argument value.
     * @param type Sets the data type allowed for this argument.
     * @param separator Sets the separator value allowed in between the
     * argument values being passed in.
     */
    public void hasArgs(final String name, final Object type,
            final char separator) {
        hasArgs(name, type, separator, false);
    }

    /**
     * Allows multiple arguments to be passed in to the option. Does not
     * define a maximum number of allowable arguments.
     *
     * @param name Sets the display name of the argument value.
     * @param type Sets the data type allowed for this argument.
     * @param separator Sets the separator value allowed in between the
     * argument values being passed in.
     * @param isOptional Set to 'true' if an argument is optional,
     * 'false' otherwise.
     */
    public void hasArgs(final String name, final Object type,
            final char separator, final boolean isOptional) {
        hasArgs(Option.UNLIMITED_VALUES, name, type, separator, isOptional);
    }

    /**
     * Defines an argument's "properties" for an option.
     *
     * @param numArgs Max number of arguments allowed.
     * @param name Sets the display name of the argument value.
     * @param type Sets the data type allowed for this argument.
     * @param separator Sets the separator value allowed in between the
     * argument values being passed in.
     * @param isOptional Set to 'true' if an argument is optional, 'false'
     * otherwise.
     */
    public void hasArgs(final int numArgs, final String name,
            final Object type, final char separator,
            final boolean isOptional) {
        setArgs(numArgs);
        setArgName(name);
        setType(type);
        setValueSeparator(separator);
        setOptionalArg(isOptional);
    }

}
