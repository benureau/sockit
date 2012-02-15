package sockit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class InputMessage {
	
	//type of the message
	private int type = 0;
	//type of the message
	private int length = 0;
	// OutoutStream used to write data
	private ByteArrayInputStream content;
	// DataInputstream used to write data using a machine-independent way
	private DataInputStream din;
	// header size
	public static int HEADER_SIZE = 8;
	
	/**
	 * Build a input message from a given type
	 * @param type the type of the message
	 * @param content the content to put in the message
	 */
	public InputMessage(int type, byte[] content){
		this.type = type;
		this.length = InputMessage.HEADER_SIZE + content.length;
		this.content = new ByteArrayInputStream(content);
		this.din = new DataInputStream(this.content);
	}
	
	/**
	 * Test constructors which build an object from a byte array
	 * @param bytes the byte array to build the object
	 */
	public InputMessage(byte[] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);
		try {
			this.type = dis.readInt();
			int length = dis.readInt();
			byte[] content = new byte[length - InputMessage.HEADER_SIZE];
			for(int i = 0 ; i < length - InputMessage.HEADER_SIZE ; i++)
				content[i] = dis.readByte();
			this.content = new ByteArrayInputStream(content);
			this.din = new DataInputStream(this.content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reset the cursor
	 */
	public void resetCursor(){
    	this.din = new DataInputStream(this.content);
	}
	
	/**
	 * Returns the type of the input message
	 * @return the type of the input message
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * Returns the length of the input message
	 * @return the length of the input message
	 */
	public int getLength(){
		return this.length;
	}
	
	/**
	 * Reads a boolean in the message content
	 * @return the boolean read
	 * @throws IOException if readBoolean fails
	 */
	public boolean readBoolean() throws IOException{
		return din.readBoolean();
	}
	
	
	/**
	 * Reads a string in the content of the message
	 * @return the string read
	 * @throws IOException if read fails
	 */
	public String readString() throws IOException{
		return din.readUTF();
	}
	
	/**
	 * Reads a double in the content of the message
	 * @return the double read
	 * @throws IOException if read fails
	 */
	public double readDouble() throws IOException{
		return din.readDouble();
	}
	
	/**
	 * Reads a float in the content of the message
	 * @return the float read
	 * @throws IOException if read fails
	 */
	public float readFloat() throws IOException{
		return din.readFloat();
	}
	
	/**
	 * Reads an int in the content of the message
	 * @return the int read
	 * @throws IOException if read fails
	 */
	public int readInt() throws IOException{
		return din.readInt();
	}
	
	/**
	 * Reads a long in the content of the message
	 * @return the long read
	 * @throws IOException 
	 */
	public long readLong() throws IOException{
		return din.readLong();
	}
}
