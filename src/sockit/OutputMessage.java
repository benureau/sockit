package sockit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OutputMessage {

	//type of the message
	private int type = 0;
	// OutoutStream used to write data
	private ByteArrayOutputStream content;
	// DataInputstream used to write data using a machine-independent way
	private DataOutputStream dout;

	/**
	 * Constructs an empty non specified message
	 */
	public OutputMessage() {
		content = new ByteArrayOutputStream();
		dout = new DataOutputStream(content);
	}	
	
	/**
	 * Constructs a message with a specified type
	 * @param type the type of the message
	 */
	public OutputMessage(int type) {
		content = new ByteArrayOutputStream();
		dout = new DataOutputStream(content);
		this.type = type;
	}

	/**
	 * Gets the type of the message
	 * @return the type of the message
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * Sets the type of the message
	 * @param type the new type of the message
	 */
	public void setType(int type){
		this.type = type;
	}
	
	/**
	 * returns a message as a bytes array
	 * @return the message as a bytes array
	 */
	public byte[] getBytes(){
		byte[] header = getHeaderAsByteArray();
		if(header == null)
			return null;
		byte[] content = getContentAsByteArray();
		if(content == null)
			return null;
		byte[] message = new byte[header.length + content.length];
		System.arraycopy(header, 0, message, 0, header.length);
		System.arraycopy(message, 0, message, header.length, content.length);
		return message;
	}
	
	/**
	 * Returns the header as a bytes array
	 * @return the header of the message as a byte array
	 */
	private byte[] getHeaderAsByteArray(){
		ByteArrayOutputStream header = new ByteArrayOutputStream();
		DataOutputStream dheader = new DataOutputStream(header);
		try {
			dheader.write(type);
			int header_size = dheader.size() * 2;
			dheader.write(content.size() + header_size);
			// very important
			dheader.flush();
			return header.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the content of the message as a byte array
	 * @return
	 */
	private byte[] getContentAsByteArray(){
		try {
			content.flush();
			return content.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Writes a Boolean in the message content
	 * @param b the boolean to write
	 */
	void writeBoolean(Boolean b){
		try {
			dout.writeBoolean(b);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write a string in the message content as a sequence of characters
	 * @param s the string to write
	 */
	void writeChars(String s){
		try {
			dout.writeChars(s);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write a double in the message content
	 * @param d the double to write
	 */
	void writeDouble(double d){
		try {
			dout.writeDouble(d);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write a float in the content of the message
	 * @param f the float to write
	 */
	void writeFloat(float f){
		try {
			dout.writeFloat(f);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes an int in the message content
	 * @param i the it to write
	 */
	void writeInt(int i){
		try {
			dout.writeInt(i);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a long in the message content
	 * @param l the long to write
	 */
	void writeLong(long l){
		try {
			dout.writeLong(l);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Just a simple test
	 */
	public static void test(){
		OutputMessage m = new OutputMessage();
		m.setType(126);
		m.writeInt(25);
		m.writeBoolean(true);
		m.writeDouble(5.5);
		m.writeFloat((float) 9.8);
		m.writeLong(798);
		m.writeChars("hello !");
		byte[] mb = m.getBytes();
		System.out.println("Message size is " + mb.length);
	}
}
