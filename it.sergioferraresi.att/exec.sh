#!/bin/bash

if [ $# -eq 2 ]
then
  # Variables.
  netbeansProjectFolder=$1
  outputFolder=$2
  pkg="automatictestingtool"
  ATTPath="${outputFolder}ATT/"
  srcPath="${ATTPath}src/"
  pkgPath="${srcPath}$pkg/"
  buildPath="${ATTPath}build/"
  testsPath="${buildPath}$pkg/tests/"

  # Creates the ATT directory in the user's specified folder.
  echo ""
  echo "Creating (if necessary) the folder tree..."
  if [ ! -e "$ATTPath" ]
  then
    mkdir $ATTPath
    echo "The \"$ATTPath\" does not exist: folder created."
  else
    echo "The \"$ATTPath\" already exists: folder not created."
  fi
  if [ ! -e "$srcPath" ]
  then
    mkdir $srcPath
    echo "The \"$srcPath\" does not exist: folder created."
  else
    echo "The \"$srcPath\" already exists: folder not created."
  fi
  if [ ! -e "$pkgPath" ]
  then
    mkdir $pkgPath
    echo "The \"$pkgPath\" does not exist: folder created."
  else
    echo "The \"$pkgPath\" already exists: folder not created."
  fi
  if [ ! -e "$buildPath" ]
  then
    mkdir $buildPath
    echo "The \"$buildPath\" does not exist: folder created."
  else
    echo "The \"$buildPath\" already exists: folder not created."
    #echo "Deleting all \".class\" files..."
    #rm $buildPath$pkg/*.class
  fi
  #if [ ! -e "$buildPath$pkg" ]
  #then
  #  mkdir $buildPath$pkg
  #  echo "The \"$buildPath$pkg\" does not exist: folder created."
  #else
  #  echo "The \"$buildPath$pkg\" already exists: folder not created."
  #fi
  if [ ! -e $buildPath$pkg/programImages ]
  then
    mkdir "${buildPath}${pkg}/programImages"
    echo "The \"${buildPath}${pkg}/programImages\" does not exist: folder created."
  else
    echo "The \"${buildPath}${pkg}/programImages\" already exists: folder not created."
  fi
  if [ ! -e $buildPath$pkg/programUtilFiles ]
  then
    mkdir ${buildPath}${pkg}/programUtilFiles
    echo "The \"$buildPath$pkg/programUtilFiles\" does not exist: folder created."
  else
    echo "The \"$buildPath$pkg/programUtilFiles\" already exists: folder not created."
  fi
  if [ -e $buildPath$pkg/programUtilFiles/testXMLSchema.xsd ]
  then
    echo "Deleting the \"testXMLSchema.xsd\" file..."
    rm $buildPath$pkg/programUtilFiles/testXMLSchema.xsd
  else
    echo "The \"testXMLSchema.xsd\" file does not exist."
  fi
  if [ -e $buildPath$pkg/programUtilFiles/reportsXMLSchema.xsd ]
  then
    echo "Deleting the \"reportsXMLSchema.xsd\" file..."
    rm $buildPath$pkg/programUtilFiles/reportsXMLSchema.xsd
  else
    echo "The \"reportsXMLSchema.xsd\" file does not exist."
  fi

  # Copies the images in the programImages/ folder.
  echo ""
  echo "Copying the images from the \""${netbeansProjectFolder}"programImages\" folder to the \"$buildPath$pkg/programImages\" folder..."
  cp "${netbeansProjectFolder}"programImages/*.png "${buildPath}${pkg}"/programImages/

  # Copies the XML Schema files in the programUtilFiles/ folder.
  echo ""
  echo "Copying the XML Schema files from the \""${netbeansProjectFolder}"src/$pkg/\" folder to the \"$buildPath$pkg/tests\" folder..."
  cp "${netbeansProjectFolder}"src/$pkg/*.xsd ${buildPath}${pkg}/programUtilFiles

  # Copies *.java files in the ../src/automatictestingtool folder.
  echo ""
  echo "Copying java source file from \""${netbeansProjectFolder}"src/$pkg/\" to \"$pkgPath\"..."
  cp "${netbeansProjectFolder}"src/$pkg/*.java $pkgPath

  # Compiles all *.java files.
  echo ""
  echo "Transforming from \"Java Code\" to \"Byte Code\"..."
  cd $pkgPath
  javac -d $buildPath Main.java ConsoleManager.java SystemManagement.java TestCaseBuilder.java TestExecutor.java UserActionSimulator.java WindowManager.java ResultsValidator.java ScriptExecutor.java

  # Creates the jar file in the ATT folder.
  echo ""
  echo "Creating the jar file \"att.jar\" in the \"$ATTPath\" folder..."
  cd $buildPath
  jar -cvfme "${ATTPath}att.jar" "${netbeansProjectFolder}manifest.mf" automatictestingtool.Main $pkg/*.class $pkg/programUtilFiles/*.xsd $pkg/programImages/*.png

  # Executes the Main.
  echo ""
  echo "Executing the \"Automatic Testing Tool\" Program..."
  cd $ATTPath
  java -jar att.jar
  echo "Execution terminated."
  echo ""
else
  echo "Usage: $0 netbeansProjectABSOLUTEFolder outputABSOLUTEFolder"
fi
