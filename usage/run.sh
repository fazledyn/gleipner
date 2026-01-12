#!/bin/bash

# Get the directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
# Project root is the parent directory
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
# Path to the library jar
TARGET_JAR="$PROJECT_ROOT/algorithms/_target/gleipner.chains-1.0.jar"

# Check if JAR exists
if [ ! -f "$TARGET_JAR" ]; then
    echo "Error: Gadget Chains JAR not found at $TARGET_JAR"
    # Fallback to check specific jar just in case
    echo "Checking for basic jar..."
    TARGET_JAR="$PROJECT_ROOT/algorithms/_target/gleipner.chains-1.0-basic.jar"
    if [ ! -f "$TARGET_JAR" ]; then
         echo "Error: Basic Gadget Chains JAR also not found."
         exit 1
    fi
fi

echo "Using JAR: $TARGET_JAR"

# Output directory for compiled classes
# We will compile into the usage directory itself for simplicity in this example
# But strictly speaking, it's better to keep source and class files separate.
# However, for a simple usage example, compiling in-place is often clearer for users to see what's happening.

echo "Compiling Java files..."
# We include TARGET_JAR in classpath.
# We also include PROJECT_ROOT because our source files are in 'package usage;', 
# so 'javac' needs to know where the root of the source tree is if we were referencing other local packages.
# Here we are just compiling single files.
javac -cp "$TARGET_JAR" "$SCRIPT_DIR/Serializer.java" "$SCRIPT_DIR/PayloadGenerator.java" "$SCRIPT_DIR/VulnerableApp.java"

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

PAYLOAD_FILE="$SCRIPT_DIR/payload.ser"

echo "----------------------------------------------------------------"
echo "Running PayloadGenerator..."
echo "Generating payload to: $PAYLOAD_FILE"
# Run java. Classpath must include:
# 1. The PROJECT_ROOT directory (so that 'usage.PayloadGenerator' can be found)
# 2. The TARGET_JAR (for the gadget chain classes)
java -cp "$PROJECT_ROOT:$TARGET_JAR" usage.PayloadGenerator "$PAYLOAD_FILE"

if [ $? -ne 0 ]; then
    echo "Payload generation failed."
    exit 1
fi

echo "----------------------------------------------------------------"
echo "Running VulnerableApp..."
echo "Feeding payload: $PAYLOAD_FILE"
# Run VulnerableApp
java -cp "$PROJECT_ROOT:$TARGET_JAR" usage.VulnerableApp "$PAYLOAD_FILE"

if [ $? -ne 0 ]; then
    echo "VulnerableApp execution failed."
    exit 1
fi

echo "----------------------------------------------------------------"
echo "Demonstration Complete."
