package playground;

import playground.utils.Utils;

public class Message {

	//following attributes appear in the raw data sent on, the physical device

	//type of the message
	private int type = 0;
	// content of the message
	private byte[] content;
	//number of bytes of the message (header + conten)
	private int length = HEADER_SIZE;
	// cursor for reading/writing
	private int cursor = 0;
	// header size
	public static int HEADER_SIZE = 8;
	// default size of the content
	private static int BUFFER_SIZE = 256;

	/**
	 * Constructs an empty non specified message
	 */
	public Message(){
		this.content = new byte[BUFFER_SIZE];
	}
	
	/**
	 * Build a message from a given type, length and content
	 * @param type the type of the message
	 * @param length the length of the message
	 * @param content the content to put in the message
	 */
	public Message(int type, int length, byte[] content){
		this.type = type;
		this.length = length;
		this.content = new byte[content.length];
		System.arraycopy(content, 0, this.content, 0, (int) content.length);
		this.cursor = 0;
	}

	/**
	 * Reset the cursor
	 */
	public void resetCursor(){
		this.cursor = 0;
	}

	/**
	 * allows you to change the type of a message
	 * @param type the new type
	 */
	public void setType(int type){
		this.type = type;
	}

	/**
	 * returns the type of the message
	 * @return the type
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * Get the length of the message. This length includes the header size.
	 * @return the length of the message
	 */
	public int getLength(){
		return this.length;
	}

	/**
	 * returns a message as a byte array
	 * @return the message as a byte array
	 */
	public byte[] getBytes(){
		byte[] flow = new byte[content.length];
		Utils.writeInt(this.length, flow, 0);
		Utils.writeInt(this.type, flow, Utils.INT_SIZE);
		for(int i = 0 ; i < this.content.length - HEADER_SIZE; i++)
			flow[i + HEADER_SIZE] = this.content[i];
		return flow;
	}

	/**
	 * Enlarge the byte{} so that it can receive n more bytes.
	 * @param size number of additional bytes that this byte array should be
	 *        able to receive.
	 */
	private void checkForFreeSpace(int size) {
		int free = (int) (this.content.length - cursor);
		if(free < size){
			byte[] newData = new byte[(int) (this.content.length + (size - free))];
			System.arraycopy(this.content, 0, newData, 0, (int) length);
			this.content = newData;
		}
	}

	/**
	 * Puts a boolean in he content of the message
	 * @param b the boolean to put
	 */
	public void appendBoolean(boolean b) {
		checkForFreeSpace(Utils.BOOLEAN_SIZE);
		Utils.writeBoolean(b, content, cursor);
		cursor = cursor + Utils.BOOLEAN_SIZE;
		this.length = this.length + Utils.BOOLEAN_SIZE;
	}

	/**
	 * Reads a boolean in the content of the message
	 * @return the boolean read
	 */
	public boolean readBoolean() {
		Boolean b = Utils.readBoolean(content, cursor);
		cursor = cursor + Utils.BOOLEAN_SIZE;
		return b;
	}

	/**
	 * Puts an int in the content of the message
	 * @param i the int to put
	 */
	public void appendInt(int i) {
		checkForFreeSpace(Utils.INT_SIZE);
		Utils.writeInt(i, content, cursor);
		cursor = cursor + Utils.INT_SIZE;
		this.length = this.length + Utils.INT_SIZE;
	}

	/**
	 * Reads an int in the content of the message
	 * @return the int read
	 */
	public int readInt() {
		int i = Utils.readInt(content, cursor);
		cursor = cursor + Utils.INT_SIZE;
		return i;
	}

	/**
	 * Puts a float in the content of the message
	 * @param f the float read
	 */
	public void appendFloat(float f) {
		checkForFreeSpace(Utils.FLOAT_SIZE);
		Utils.writeFloat(f, content, cursor);
		cursor = cursor + Utils.FLOAT_SIZE;
		this.length = this.length + Utils.FLOAT_SIZE;
	}

	/**
	 * Reads a float in the content of the message
	 * @return the float read
	 */
	public float readFloat() {
		float f = Utils.readFloat(content,  cursor);
		cursor = cursor + Utils.FLOAT_SIZE;
		return f;
	}

	/**
	 * Reads a string in the content of the message
	 * @return the string read
	 */
	public String readString() {
		String s = Utils.readString(content, cursor);
		cursor = cursor + Utils.INT_SIZE + s.getBytes().length;
		return s;
	}

	/**
	 * Put a string in the content of the message
	 * @param s the string to put in the message
	 */
	public void appendString(String s) {
		checkForFreeSpace(s.getBytes().length + Utils.INT_SIZE);
		Utils.writeString(s, content, cursor);
		cursor = cursor + s.getBytes().length + Utils.INT_SIZE;
		this.length = this.length + s.getBytes().length + Utils.INT_SIZE;
	}

	/**
	 * Just a little test for the message class
	 */
	public static void test(){
		int num_it = 1024;
		Message message = new Message();

		// int test
		for(int i = 0 ; i < num_it ; i++){
			message.appendInt(i);
		}
		message.resetCursor();
		for(int i = 0 ; i < num_it ; i++){
			int r = message.readInt();
			if(r != i){
				System.out.println(String.valueOf(r) + " versus " + String.valueOf(i));
				System.out.println("Error : message.read/appendInt");
				System.exit(0);
			}
		}
		System.out.println("OK : message.read/appendInt");
		message.resetCursor();

		// float test
		for(int i = 0 ; i < num_it ; i++){
			message.appendFloat(((float) i) / ((float) 128.789456));
		}
		message.resetCursor();
		for(int i = 0 ; i < num_it ; i++){
			float r = message.readFloat();
			if(r != ((float) i) / ((float) 128.789456)){
				System.out.println(String.valueOf(r) + " versus " + String.valueOf(((float) i) / ((float) 128.789456)));
				System.out.println("Error : message.read/appendFloat");
				System.exit(0);
			}
		}
		System.out.println("OK : message.read/appendFloat");
		message.resetCursor();

		// boolean test
		for(int i = 0 ; i < num_it ; i++){
			message.appendBoolean(i % 2 == 0 ? true : false);
		}
		message.resetCursor();
		for(int i = 0 ; i < num_it ; i++){
			Boolean r = message.readBoolean();
			if(r != (i % 2 == 0 ? true : false)){
				System.out.println(String.valueOf(r) + " versus " + String.valueOf(i % 2 == 0 ? true : false));
				System.out.println("Error : message.read/appendBoolean");
				System.exit(0);
			}
		}
		System.out.println("OK : message.read/appendBoolean");
		message.resetCursor();
		
		message = new Message();
		
		// string test
		for(int i = 0 ; i < num_it ; i++){
			message.appendString(String.valueOf(i) + "azertyuiopqsdfghjklmwxcvbn");
		    //FIXME check content length
		}
		message.resetCursor();
		for(int i = 0 ; i < num_it ; i++){
			String r = message.readString();
			String t = String.valueOf(i) + "azertyuiopqsdfghjklmwxcvbn";
		    //FIXME check content length
			if(r.compareTo(t) != 0){
				System.out.println(r + " versus " + String.valueOf(i) + "azertyuiopqsdfghjklmwxcvbn");
				System.out.println("Error : message.read/appendString");
				System.exit(0);
			}
		}
		System.out.println("OK : message.read/appendString");
		message.resetCursor();

		message = new Message();
		
		// mixed
		for(int j = 0 ; j < 1024 ; j++){
			message.resetCursor();
			message.appendInt(8848);
			message.appendBoolean(true);
			message.appendFloat((float) 128.789456);
			message.appendString("azertyuiopmlkjhgfdsqwxcvbn");
			message.resetCursor();
			if(message.readInt() != 8848){
				System.out.println("Error in Int");
				System.exit(0);
			}
			if(message.readBoolean() != true){
				System.out.println("Error in Boolean");
				System.exit(0);
			}
			if(message.readFloat() != (float) 128.789456){
				System.out.println("Error in Float");
				System.exit(0);
			}
			if(message.readString().compareTo("azertyuiopmlkjhgfdsqwxcvbn") != 0){
				System.out.println("Error in String");
				System.exit(0);
			}
		}
		System.out.println("OK : mixed types");
	}
}
