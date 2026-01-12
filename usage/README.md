# Gleipner Usage Examples

This directory contains examples of how to use the Gleipner gadget chains.

## Prerequisites

- Java Development Kit (JDK) installed (Java 8+ recommended).
- The project must be built so that the JAR files in `algorithms/_target/` are available.

## Structure

- **Serializer.java**: A helper class to handle object serialization and deserialization to/from files.
- **PayloadGenerator.java**: A tool that constructs a malicious serialized payload using a Gadget Chain (in this case, a basic chain).
- **VulnerableApp.java**: A simulated vulnerable application that deserializes untrusted data from a file.
- **run.sh**: A script to compile and run the example.

## Step-by-Step Walkthrough

This section details the entire process of creating and using a gadget chain exploit with Gleipner.

### 1. Constructing the Malicious Payload (`PayloadGenerator.java`)

First, we need to create a Java program that builds the "Gadget Chain". A gadget chain is a sequence of objects that, when deserialized, trigger a specific "Sink" method (like a command execution or, in this test case, a logging event).

In `PayloadGenerator.java`, we instantiate the specific classes provided by the Gleipner library (`algorithms/_target/gleipner.chains-1.0.jar`):

```java
// 1. Create the Sink Gadget (The final destination)
BasicSinkGadget sinkGadget = new BasicSinkGadget();
sinkGadget.sinkGadget = new SinkGadget();
// The 'tainted' field is critical; the sink checks for this specific value to confirm success.
sinkGadget.tainted = Controller.TAINT_MARKER; 

// 2. Create the Link Gadget (Connects the Trigger to the Sink)
BasicLinkGadget basicLinkGadget = new BasicLinkGadget();
// Link it to our sink
basicLinkGadget.basicSinkGadget = sinkGadget;

// 3. Create the Trigger Gadget (The entry point)
TriggerGadget triggerGadget = new TriggerGadget();
// Store our chain inside the trigger
triggerGadget.o = basicLinkGadget;

// 4. Serialize the entire chain to a file
Serializer.serializeToFile(triggerGadget, "payload.ser");
```

### 2. Simulating the Victim (`VulnerableApp.java`)

The victim application is simple. It accepts a file path, reads the bytes, and blindly deserializes them using `ObjectInputStream.readObject()`. This is a common vulnerability in Java applications.

```java
// usage/VulnerableApp.java snippet
String payloadFile = args[0];
// The deserialization process automatically invokes 'readObject' on the TriggerGadget,
// starting the chain reaction.
Serializer.deserializeFromFile(payloadFile);
```

### 3. Compilation

We must compile our usage code against the Gleipner library so the Java compiler knows about `BasicSinkGadget`, `TriggerGadget`, etc.

```bash
# We add the Gleipner JAR to the classpath (-cp)
javac -cp ../algorithms/_target/gleipner.chains-1.0.jar usage/*.java
```

### 4. Generating the Payload

Run the generator. This creates the `payload.ser` file containing our malicious object graph.

```bash
# Run the generator, ensuring the library is available on classpath
java -cp .:../algorithms/_target/gleipner.chains-1.0.jar usage.PayloadGenerator usage/payload.ser
```

*Output:*
```
Constructing Gadget Chain...
Serializing payload to usage/payload.ser
Serialized object to usage/payload.ser
```

### 5. Running the Exploit

Finally, we feed the payload to the vulnerable application.

```bash
java -cp .:../algorithms/_target/gleipner.chains-1.0.jar usage.VulnerableApp usage/payload.ser
```

*Output:*
```
Vulnerable App started.
Deserializing usage/payload.ser...
gleipner.core.Controller.invokeSink
gleipner.core.SinkGadget.sinkMethod
...
Deserialization complete.
```

The lines `gleipner.core.Controller.invokeSink` appearing in the output confirm that the execution flow successfully traveled from `readObject` (Trigger) through the `LinkGadget` and reached the `SinkGadget`, validating the vulnerability.

## Running the Example (Automated)

Simply execute the `run.sh` script to perform steps 3-5 automatically:

```bash
./run.sh
```