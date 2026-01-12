package usage;

public class VulnerableApp {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java usage.VulnerableApp <payload_file>");
            System.exit(1);
        }
        String payloadFile = args[0];

        System.out.println("Vulnerable App started.");
        System.out.println("Deserializing " + payloadFile + "...");
        
        // This simulates receiving a payload and deserializing it
        Serializer.deserializeFromFile(payloadFile);
        System.out.println("Deserialization complete.");
    }
}
