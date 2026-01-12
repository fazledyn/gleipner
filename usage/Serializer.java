package usage;

import java.io.*;

public class Serializer {

    public static void serializeToFile(Object o, String file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.close();
            fos.close();
            System.out.println("Serialized object to " + file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deserializeFromFile(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            ois.close();
            fis.close();
            System.out.println("Deserialized object from " + file);
            System.out.println("Object class: " + o.getClass().getName());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
