import java.security.AccessController;
import java.security.PrivilegedAction;


public class Evil implements PrivilegedAction {

	public static void disable() {
		AccessController.doPrivileged(new Evil());
	}

	@Override
	public Object run() {
		System.setSecurityManager(null);
		System.out.println("disabled security manager");
		return null;
	}
}
