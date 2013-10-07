package sockit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;

public class OutboundMessage {

	//type of the message
	private int type = 0;
	// OutoutStream used to write data
	private ByteArrayOutputStream content;
	// DataInputstream used to write data using a machine-independent way
	private DataOutputStream dout;
	// header size
	public static int HEADER_SIZE = 8;

	/**
	 * Constructs an empty non specified message
	 */
	public OutboundMessage() {
		content = new ByteArrayOutputStream();
		dout = new DataOutputStream(content);
	}    

	/**
	 * Constructs a message with a specified type
	 * @param type the type of the message
	 */
	public OutboundMessage(int type) {
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
		System.arraycopy(content, 0, message, header.length, content.length);
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
			dheader.writeInt(InboundMessage.HEADER_SIZE + this.content.size());
			dheader.writeInt(type);
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
	 * @throws IOException 
	 */
	public void appendBoolean(Boolean b) throws IOException{
		dout.writeByte(MessageUtils.BOOL_TYPE);
		dout.writeBoolean(b);
		dout.flush();
	}

	/**
	 * Write a string in the message content as a sequence of characters
	 * @param s the string to write
	 * @throws IOException 
	 */
	public void appendString(String s) throws IOException{
		dout.writeByte(MessageUtils.STRING_TYPE);
		dout.writeUTF(s);
		dout.flush();
	}

	/**
	 * Write a double in the message content
	 * @param d the double to write
	 * @throws IOException 
	 */
	public void appendDouble(double d) throws IOException{
		dout.writeByte(MessageUtils.DOUBLE_TYPE);
		dout.writeDouble(d);
		dout.flush();
	}

	/**
	 * Write a float in the content of the message
	 * @param f the float to write
	 * @throws IOException 
	 */
	public void appendFloat(float f) throws IOException{
		dout.writeByte(MessageUtils.FLOAT_TYPE);
		dout.writeFloat(f);
		dout.flush();
	}

	/**
	 * Writes an int in the message content
	 * @param i the it to write
	 * @throws IOException 
	 */
	public void appendInt(int i) throws IOException{
		dout.writeByte(MessageUtils.INT_TYPE);
		dout.writeInt(i);
		dout.flush();
	}

	/**
	 * Writes a long in the message content
	 * @param l the long to write
	 * @throws IOException 
	 */
	public void appendLong(long l) throws IOException{
		dout.writeByte(MessageUtils.LONG_TYPE);
		dout.writeLong(l);
		dout.flush();
	}

	/**
	 * Write an ArrayList of Object in the message content
	 * @param al the Array List to write
	 * @throws IOException 
	 */
	public void appendArrayList(ArrayList<?> al) throws IOException{
		dout.writeByte(MessageUtils.LIST_TYPE);
		this.appendInt(al.size());
		if(al.size() == 0)
			return;
		if(this.isHeterogeneousList(al)){
			dout.writeByte(MessageUtils.HETERO_TYPE);
			for (Object o : al) {
				if(MessageUtils.getTypeOfClass(o) == MessageUtils.LONG_TYPE)
					this.appendLong(((Long) o).longValue());
				else if(MessageUtils.getTypeOfClass(o) == MessageUtils.DOUBLE_TYPE)
					this.appendDouble(((Double) o).doubleValue());
				else if(MessageUtils.getTypeOfClass(o) == MessageUtils.INT_TYPE)
					this.appendInt(((Integer) o).intValue());
				else if(MessageUtils.getTypeOfClass(o) == MessageUtils.FLOAT_TYPE)
					this.appendFloat(((Float) o).floatValue());
				else if(MessageUtils.getTypeOfClass(o) == MessageUtils.BOOL_TYPE)
					this.appendBoolean(((Boolean) o).booleanValue());
				else if(MessageUtils.getTypeOfClass(o) == MessageUtils.LIST_TYPE)
					this.appendArrayList((ArrayList) o);
				else if(MessageUtils.getTypeOfClass(o) == MessageUtils.STRING_TYPE)
					this.appendString(((String) o).toString());
				else
					throw new IOException("This type is not supported yet.");
			}
		}
		else{
			byte type = MessageUtils.getTypeOfClass(al.get(0));
			dout.writeByte(type);
			switch (type) {
			case MessageUtils.BOOL_TYPE:
				for (Object o : al){
					dout.writeBoolean(((Boolean) o).booleanValue());
				}
				break;
			case MessageUtils.DOUBLE_TYPE:
				for (Object o : al){
					dout.writeDouble(((Double) o).doubleValue());
				}
				break;	
			case MessageUtils.FLOAT_TYPE:
				for (Object o : al){
					dout.writeFloat(((Float) o).floatValue());;
				}
				break;
			case MessageUtils.INT_TYPE:
				for (Object o : al){
					dout.writeInt(((Integer) o).intValue());
				}
				break;	
			case MessageUtils.LONG_TYPE:
				for (Object o : al){
					dout.writeLong(((Long) o).longValue());
				}
				break;
			case MessageUtils.STRING_TYPE:
				for (Object o : al){
					dout.writeUTF(((String) o).toString());
				}
				break;
			case MessageUtils.LIST_TYPE:
				;
				for (Object o : al){
					this.appendArrayList((ArrayList) o);
				}
				break;	
			default:
				throw new IOException("This type is not supported in InboundMessage class.");
			}
		}
	}


	/**
	 * Returns true if an ArrayList of contains heterogeneous Objects 
	 * @param al the ArrayList to test
	 * @return true or false
	 * @throws IOException 
	 */
	private boolean isHeterogeneousList(ArrayList<?> al) throws IOException{
		if(al.size() == 0)
			throw new IOException("The list is empty and cannot be written.");
		if(al.size() == 1)
			return false;
		Object o = al.get(0);
		for(int i = 1; i < al.size() ; i++){
			if(al.get(i).getClass().equals(o.getClass()) == false)
				return true;
		}
		return false;
	}
}
