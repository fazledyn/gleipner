package gleipner.core;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SinkGadget implements Serializable {

    public SinkGadget() {
    }

    public void sinkMethod(String taint) {        
        Controller.invokeSink(taint);
    }

}
