package sockit;

import java.util.ArrayList;

public class MessageUtils {
	// type int
	static final int AL_TYPE_INT = 0;
	// type int
	static final int AL_TYPE_STRING = 1;
	// type double
	static final int AL_TYPE_DOUBLE = 2;
	// type float
	static final int AL_TYPE_FLOAT = 3;
	// type bool
	static final int AL_TYPE_BOOL = 4;
	// type long
	static final int AL_TYPE_LONG = 5;
	
	/**
	 * Return the type of the first object inside the ArrayList
	 * @param al the ArrayList to test
	 * @return the type as an int
	 */
	public static int determineTypeInArrayList(ArrayList<?> al){
		int type = -1;
		if(al.size() > 0){
			// test the first one only
			Object o = al.get(0);
			if (o.getClass().equals(Integer.class)) {
				type = 0;
			}
			else if (o.getClass().equals(String.class)) {
				type = 1;
			}
			else if (o.getClass().equals(Double.class)) {
				type = 2;
			}
			else if (o.getClass().equals(Float.class)) {
				type = 3;
			}
			else if (o.getClass().equals(Boolean.class)) {
				type = 4;
			}
			else if (o.getClass().equals(Long.class)) {
				type = 5;
			}
		}
		return type;
	}
}
