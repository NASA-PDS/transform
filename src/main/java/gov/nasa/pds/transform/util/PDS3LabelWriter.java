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
