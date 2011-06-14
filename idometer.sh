#! /bin/sh

LIB=lib_managed/scala_2.9.0/compile/

BASEPATH=target/scala_2.9.0/idometer_2.9.0-0.0.3.jar:project/boot/scala-2.9.0/lib/scala-library.jar
LIBPATH=${LIB}scala-swing-2.9.0.jar:${LIB}grizzled-slf4j_2.9.0-0.5.jar:${LIB}log4j-1.2.16.jar:${LIB}slf4j-api-1.6.1.jar:${LIB}slf4j-log4j12-1.6.1.jar

java -cp $BASEPATH:$LIBPATH de.velopmind.idometer.swing.IdometerGui
