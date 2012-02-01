package sockit.utils;

public class Utils {
	
	public final static int INT_SIZE = 4;
	public final static int FLOAT_SIZE = 4;
	public final static int BOOLEAN_SIZE = 1;

	/**
	 * Write an int in an byte array at a specified index
	 * @param value the int to write
	 * @param content the byte array where to write
	 * @param index the index where to start to write in the byte array
	 */
	public static void writeInt(int value, byte[] content, int index){
		if(content != null && (content.length - index) >= INT_SIZE){
			content[0 + index] = (byte) ((value >> 24) & 0xff);
			content[1 + index] = (byte) ((value >> 16) & 0xff);
			content[2 + index] = (byte) ((value >> 8) & 0xff);
			content[3 + index] = (byte) ((value >> 0) & 0xff);
		}
	}

	/**
	 * Read an int in a byte array if this array is not null
	 * @param content the byte array where to read
	 * @param index the index where to start to read
	 * @return the int read or 0.
	 */
	public static int readInt(byte[] content, int index){
		if (content == null || (content.length - index) < INT_SIZE) return 0x0;
		return (int)(
				(0xff & content[0 + index]) << 24  |
				(0xff & content[1 + index]) << 16  |
				(0xff & content[2 + index]) << 8   |
				(0xff & content[3 + index]) << 0
				);
	}

	/**
	 * Write a float in an byte array at a specified index
	 * @param value the float to write
	 * @param content the byte array where to write
	 * @param index the index where to start to write in the byte array
	 */
	public static void writeFloat(float value, byte[] content, int index){
		writeInt(Float.floatToRawIntBits(value), content, index);
	}

	/**
	 * Read a float in a byte array if this array is not null
	 * @param content the byte array where to read
	 * @param index the index where to start to read
	 * @return the float read or 0.0.
	 */
	public static float readFloat(byte[] content, int index){
		return Float.intBitsToFloat(readInt(content, index));
	}

	/**
	 * Write a boolean in an byte array at a specified index
	 * @param value the boolean to write
	 * @param content the byte array where to write
	 * @param index the index where to start to write in the byte array
	 */
	public static void writeBoolean(boolean value, byte[] content, int index) {
		if(content != null && (content.length - index) >= BOOLEAN_SIZE)
			content[index] = (byte)(value ? 0x01 : 0x00);
	}

	/**
	 * Read a boolean in a byte array if this array is not null
	 * @param content the byte array where to read
	 * @param index the index where to start to read
	 * @return the boolean read or false.
	 */
	public static boolean readBoolean(byte[] content, int index){
		return (content == null || (content.length - index) < BOOLEAN_SIZE) ? false : content[0 + index] != 0x00;
	}

	/**
	 * Write a java string in an byte array at a specified index. Note that an int is written before to indicate the size of the string
	 * @param value the string to write
	 * @param content the byte array where to write
	 * @param index the index where to start to write in the byte array
	 */
	public static void writeString(String value, byte[] content, int index){
		if(content != null && ((content.length - index) >= (value.getBytes().length + INT_SIZE))){
			writeInt(value.getBytes().length, content, index);
			System.arraycopy(value.getBytes(), 0, content, index + INT_SIZE, value.getBytes().length);
		}
	}

	/**
	 * Read a string in a byte array if this array is not null. First, the size of the string is read
	 * @param content the byte array where to read
	 * @param index the index where to start to read
	 * @return the string read or "".
	 */
	public static String readString(byte[] content, int index){
		if(content != null){
			int size = readInt(content, index);
			byte[] flow = new byte[size];
			for(int i = 0 ; i < size ; i++)
				flow[i] = content[index + INT_SIZE + i];
			return new String(flow);
		}
		return new String("");
	}

	/**
	 * Little test
	 */
	public static void test(){
		byte[] content = new byte[8192];
		int index = 0;

		// int test
		for(int i = 0 ; i < 1024 ; i++){
			writeInt(i, content, index);
			int r = readInt(content, index);
			if(r != i){
				System.out.println("Error in Int");
				System.exit(0);
			}
			index++;
		}
		index = 0;
		System.out.println("OK : Integer");

		// boolean test
		for(int i = 0 ; i < 1024 ; i++){
			writeBoolean(true, content, index);
			boolean r = readBoolean(content, index);
			if(r != true){
				System.out.println("Error in Boolean");
				System.exit(0);
			}
			index++;
		}
		index = 0;
		System.out.println("OK : Boolean");

		// float test
		for(int i = 0 ; i < 1024 ; i++){
			float toto = ((float) i) / ((float) 128);
			writeFloat(toto, content, index);
			float t = readFloat(content, index);
			if(toto != t){
				System.out.println("Error in Float");
				System.exit(0);
			}
			index++;
		}
		index = 0;
		System.out.println("OK : Float");

		// string test
		for(int i = 0 ; i < 1024 ; i++){
			String t = String.valueOf(i);
			t = t + "abcdefghijklmnopqrstuvwxyz";
			writeString(t, content, index);
			String c = readString(content, index);
			if(c.compareTo(t) != 0){
				System.out.println("Error in String");
				System.exit(0);
			}
			index++;
		}
		index = 0;
		System.out.println("OK : String");

		// mixed
		for(int j = 0 ; j < 1024 ; j++){
			index = 0;
			writeInt(8848, content, index);
			index = index + 4;
			writeBoolean(true, content, index);
			index = index + 1;
			writeFloat((float) 128.789456, content, index);
			index = index + 4;
			writeString("azertyuiopmlkjhgfdsqwxcvbn", content, index);
			index = 0;
			if(readInt(content, index) != 8848){
				System.out.println("Error in Int");
				System.exit(0);
			}
			index = index + 4;
			if(readBoolean(content, index) != true){
				System.out.println("Error in Boolean");
				System.exit(0);
			}
			index = index + 1;
			if(readFloat(content, index) != (float) 128.789456){
				System.out.println("Error in Float");
				System.exit(0);
			}
			index = index + 4;
			if(readString(content, index).compareTo("azertyuiopmlkjhgfdsqwxcvbn") != 0){
				System.out.println("Error in String");
				System.exit(0);
			}
		}
		System.out.println("OK : mixed types");
	}
}
