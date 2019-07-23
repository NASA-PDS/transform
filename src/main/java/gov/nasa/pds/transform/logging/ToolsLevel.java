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

package gov.nasa.pds.transform.logging;

import java.util.logging.Level;

/**
 * Class to hold the logging levels.
 *
 * @author mcayanan
 *
 */
public class ToolsLevel extends Level {
    public static final Level CONFIGURATION = new ToolsLevel("CONFIGURATION",
            Level.SEVERE.intValue() + 8);
    public static final Level SKIP = new ToolsLevel("SKIP",
            Level.SEVERE.intValue() + 6);
    public static final Level ERROR = new ToolsLevel("ERROR",
        Level.SEVERE.intValue() + 4);
    public static final Level SUCCESS = new ToolsLevel("SUCCESS",
            Level.SEVERE.intValue() + 3);
    public static final Level NOTIFICATION = new ToolsLevel("NOTIFICATION",
        Level.SEVERE.intValue() + 1);
    public static final Level DEBUG = new ToolsLevel("DEBUG",
        Level.CONFIG.intValue() + 1);
    

    protected ToolsLevel(String name, int value) {
        super(name, value);
    }
}
