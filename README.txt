The Transform Tool project contains software for transforming PDS3 and PDS4 
products. The software is packaged in a JAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/2010/preparation/transform/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

This project depends on the JAR files from the pds4-tools project. Perform 
the following commands to install those JAR files locally:

% cd ../pds4-tools/superpom
% cd mvn clean install 
% cd ../package 
% mvn clean install 

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package
