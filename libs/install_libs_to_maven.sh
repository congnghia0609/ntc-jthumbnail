#!/bin/sh

echo ">>>>> Install library aperture-core-1.5.0.jar to maven local..."
mvn install:install-file -Dfile=./aperture-core-1.5.0.jar -DgroupId=org.aperture -DartifactId=aperture-core -Dversion=1.5.0 -Dpackaging=jar

#<dependency>
#    <groupId>org.aperture</groupId>
#    <artifactId>aperture-core</artifactId>
#    <version>1.5.0</version>
#</dependency>


echo ">>>>> Install library jodconverter-core-3.0-beta-3_r220.jar to maven local..."
mvn install:install-file -Dfile=./jodconverter-core-3.0-beta-3_r220.jar -DgroupId=org.artofsolving.jodconverter -DartifactId=jodconverter-core -Dversion=3.0-beta-4-jahia2 -Dpackaging=jar

#<dependency>
#    <groupId>org.artofsolving.jodconverter</groupId>
#    <artifactId>jodconverter-core</artifactId>
#    <version>3.0-beta-4-jahia2</version>
#</dependency>


echo ">>>>> Install library ScratchApplet.jar to maven local..."
mvn install:install-file -Dfile=./ScratchApplet.jar -DgroupId=edu.mit.scratch -DartifactId=scratch -Dversion=1.0.0 -Dpackaging=jar

#<dependency>
#    <groupId>edu.mit.scratch</groupId>
#    <artifactId>scratch</artifactId>
#    <version>1.0.0</version>
#</dependency>


















