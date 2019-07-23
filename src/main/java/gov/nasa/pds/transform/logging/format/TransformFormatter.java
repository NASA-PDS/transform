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

package gov.nasa.pds.transform.logging.format;

import gov.nasa.pds.transform.logging.ToolsLevel;
import gov.nasa.pds.transform.logging.ToolsLogRecord;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class that formats the Harvest logging messages.
 *
 * @author mcayanan
 *
 */
public class TransformFormatter extends Formatter {
  private static String lineFeed = System.getProperty("line.separator", "\n");
  private static String doubleLineFeed = lineFeed + lineFeed;

  private StringBuffer config;
  private StringBuffer summary;

  private int numWarnings;

  private int numErrors;

  public TransformFormatter() {
    config = new StringBuffer("PDS Transform Tool Log" + doubleLineFeed);
    summary = new StringBuffer("Summary:" + doubleLineFeed);
    numWarnings = 0;
    numErrors = 0;
  }

  public String format(LogRecord record) {
    StringBuffer message = new StringBuffer();
    if (record instanceof ToolsLogRecord) {
      ToolsLogRecord tlr = (ToolsLogRecord) record;
      if (tlr.getLevel().intValue() == ToolsLevel.NOTIFICATION.intValue()) {
        return tlr.getMessage() + lineFeed;
      }
      if (tlr.getLevel().intValue() == ToolsLevel.WARNING.intValue()) {
        ++numWarnings;
      } else if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
        ++numErrors;
      }
      if (tlr.getLevel().intValue() != ToolsLevel.CONFIGURATION.intValue()) {
        if (tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
          message.append("ERROR");
        } else {
          message.append(tlr.getLevel().getName());
        }
        message.append(":   ");
      }
      if (tlr.getFilename() != null) {
        message.append("[" + tlr.getFilename() + "] ");
      }
      if (tlr.getLine() != -1) {
        message.append("line " + tlr.getLine() + ": ");
      }
      message.append(tlr.getMessage());
      message.append(lineFeed);

      return message.toString();
    } else {
      if (record.getLevel().intValue() == Level.WARNING.intValue()) {
        ++numWarnings;
      } else if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
        ++numErrors;
      }
      if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
        message.append("ERROR");
      } else {
        message.append(record.getLevel().getName());
      }
      message.append(":   ");
      String source = record.getSourceClassName();
      String tokens[] = source.split("\\.");
      message.append("[" + tokens[tokens.length-1] + ":" + record.getSourceMethodName() + "] ");
      message.append(record.getMessage());
      message.append(lineFeed);
      return message.toString();
    }
  }

  public String getTail(Handler handler) {
    StringBuffer report = new StringBuffer("\n");
    return report.toString();
  }
}
