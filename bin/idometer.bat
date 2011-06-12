set LIB=..\lib\

set BASEPATH=%LIB%idometer-0.0.2.jar;%LIB%scala-library.jar
set LIBPATH=%LIB%scala-swing-2.9.0.jar;%LIB%grizzled-slf4j_2.9.0-0.5.jar;%LIB%log4j-1.2.16.jar;%LIB%slf4j-api-1.6.1.jar;%LIB%slf4j-log4j12-1.6.1.jar

java -cp %BASEPATH%;%LIBPATH% de.velopmind.idometer.swing.IdometerGui
