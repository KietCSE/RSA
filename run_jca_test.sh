#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
echo "Compiling..."
javac -d bin *.java com/example/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    
    # Package into a JAR file to satisfy JCE's expectation of a file-based code source
    echo "Packaging into myrsa.jar..."
    jar cf myrsa.jar -C bin .

    echo "Running TestJCA with JAR..."
    echo "--------------------------------------------------"
    # Run using the JAR in classpath
    java -cp myrsa.jar com.example.TestJCA
else
    echo "Compilation failed."
fi
