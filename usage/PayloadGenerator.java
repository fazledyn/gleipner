package usage;

import gleipner.chains.basic.BasicLinkGadget;
import gleipner.chains.basic.BasicSinkGadget;
import gleipner.core.Controller;
import gleipner.core.SinkGadget;
import gleipner.core.TriggerGadget;

public class PayloadGenerator {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java usage.PayloadGenerator <output_file>");
            System.exit(1);
        }
        String outputFile = args[0];

        // Setup gadget chain
        // TriggerGadget -> BasicLinkGadget -> BasicSinkGadget -> SinkGadget

        System.out.println("Constructing Gadget Chain...");
        BasicSinkGadget sinkGadget = new BasicSinkGadget();
        sinkGadget.sinkGadget = new SinkGadget();
        sinkGadget.tainted = Controller.TAINT_MARKER;

        BasicLinkGadget basicLinkGadget = new BasicLinkGadget();
        basicLinkGadget.basicSinkGadget = sinkGadget;

        TriggerGadget triggerGadget = new TriggerGadget();
        triggerGadget.o = basicLinkGadget;

        System.out.println("Serializing payload to " + outputFile);
        Serializer.serializeToFile(triggerGadget, outputFile);
    }
}
