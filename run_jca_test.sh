#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files (both default package and com.example package)
# We compile everything together to ensure dependencies are resolved
echo "Compiling..."
javac -d bin *.java com/example/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful."
    echo "Running TestJCA..."
    echo "--------------------------------------------------"
    # Run the TestJCA class, ensuring bin is in the classpath
    java -cp bin com.example.TestJCA
else
    echo "Compilation failed."
fi
