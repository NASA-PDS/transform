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

/**
 * Exception class that is called upon errors found during command-line
 * option processing.
 *
 *
 * @author mcayanan
 *
 */
public class InvalidOptionException extends Exception {
    /** Generated serial ID. */
    private static final long serialVersionUID = -5439038812448365813L;

    /**
     * Constructor.
     *
     * @param msg An exception message.
     */
    public InvalidOptionException(String msg) {
        super(msg);
    }

}
