# Retrieving the code, compiling it and running the tests

1. **Download & install a JDK (version 8 or later)**<br>
   For instance: [Oracle JDK 15](https://www.oracle.com/java/technologies/javase-jdk15-downloads.html)<br>
   Open JDKs are also possibility.<br>

2. **Clone this repository**<br>

3. **In the directory where the code is cloned, open a terminal**<br>
   And create "bin" and "lib" folders, for instance on Linux or MacOS:<pre>
     mkdir -p lib
     mkdir -p bin
   </pre>
4. **Download JUnit5 console standalone Jar**<br>
   Put following Jar in "lib" subfolder:<br>
   [https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.5.2/junit-platform-console-standalone-1.5.2.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.5.2/junit-platform-console-standalone-1.5.2.jar)
5. **Compile the code**<br>
   In the console, run:<pre>
     javac -cp lib/junit-platform-console-standalone-1.5.2.jar -d bin @sources.txt
   </pre>
6. **Run the unittests**<br> 
   In the console, run:<pre>
     java -jar lib/junit-platform-console-standalone-1.5.2.jar -cp bin --scan-classpath
   </pre>
 
Remark that the file listing all source files was created as follow:<pre>
  find . -name "*.java" > sources.txt
</pre>

# Architecture & design

API entry point is the parking.api.Parking class in src/parking/api/Parking.java file.
Please read the javadoc for a complete for a complete architecture and design overview.