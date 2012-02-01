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
		byte[] content = getContentAsByteArray();
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
		return content.toByteArray();
	}
}
