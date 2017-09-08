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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import gov.nasa.pds.tools.containers.FileReference;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.Scalar;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Set;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.transform.product.pds3.OrderedObjectStatement;

/**
 * Class that writes the label to a file.
 * 
 * @author mcayanan
 *
 */
public class PDS3LabelWriter {
  private final String CRLF = "\r\n";
  
  /**
   * Write the given label to a file. It will use the file name
   * stored within the given Label object.
   * 
   * @param label The label.
   * 
   * @throws IOException If an error occurred while writing the
   * label to a file.
   */
  public void write(Label label) throws IOException {
    PrintWriter writer = new PrintWriter(new FileWriter(label.getLabelFile()));
    try {
      for (Statement statement : label.getStatements()) {
        printStatement(writer, statement, 0);
      }
      writer.print("END" + CRLF);
    } finally {
      writer.flush();
      writer.close();
    }
  }
  
  private void printStatement(PrintWriter writer, Statement statement, int indent) {
    String spaces = "";
    for (int i = 0; i < indent; i++) {
      spaces += " ";
    }
    writer.print(spaces);
    if (statement instanceof AttributeStatement) {
      AttributeStatement a = (AttributeStatement) statement;
      writer.print(a.getIdentifier() + " = " + toString(a.getValue()) + CRLF);
    } else if (statement instanceof PointerStatement) {
      PointerStatement p = (PointerStatement) statement;
      writer.print("^" + p.getIdentifier().toString() + " = " + toString(p.getFileRefs()) + CRLF);
    } else if (statement instanceof OrderedObjectStatement) {
      OrderedObjectStatement o = (OrderedObjectStatement) statement;
      writer.print("OBJECT = " + o.getIdentifier().toString() + CRLF);
      for (Statement child : o.getStatements()) {
        printStatement(writer, child, indent + 2);
      }
      writer.print(spaces + "END_OBJECT = " + o.getIdentifier().toString() + CRLF + CRLF);
    }
  }
  
  private String toString(List<FileReference> fileRefs) {
    String result = "";
    FileReference fileRef = fileRefs.get(0);
    if (fileRef.getStartPosition() == null || 
        (fileRef.getStartPosition() != null && fileRef.getStartPosition().getValue().equals("0"))) {
      result = "\"" + fileRef.getPath() + "\"";
    } else {
      result = "(\"" + fileRef.getPath() + "\", " + fileRef.getStartPosition().toString() + " <BYTES>)";
    }
    return result;
  }
  
  private String toString(Value value) {
    String result = "";
    if (value instanceof Scalar) {
      result = ((Scalar) value).toString();
    } else if (value instanceof Sequence) {
      Sequence sequence = (Sequence) value;
      result = sequence.toString();
    } else if (value instanceof Set) {
      Set set = (Set) value;
      result = set.toString();
    }
    return result;
  }
}
