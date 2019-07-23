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

package gov.nasa.pds.transform.logging.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Class to filter logging messages that are coming from the underlying
 * framework.
 *
 * @author mcayanan
 *
 */
public class ToolsLogFilter implements Filter {
  /**
   * Method that checks if a log record is loggable.
   *
   * @param record The LogRecord.
   *
   * @return true if the record can be logged by the handler.
   *
   */
  @Override
  public boolean isLoggable(LogRecord record) {
//    String arcPds = "gov.nasa.arc";
    String jaxb = "javax.xml.bind";
    String comSunXmlInternal = "com.sun.xml.internal.bind";
    String comSunXmlBind = "com.sun.xml.bind";
    if ((record.getLoggerName() != null)
        && (record.getLoggerName().contains(jaxb)
            || record.getLoggerName().contains(comSunXmlInternal)
            || record.getLoggerName().contains(comSunXmlBind)
            )) {
      return false;
    } else {
      return true;
    }
  }
}
