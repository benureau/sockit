package sockit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


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
		dout.writeInt(s.length()+2);
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
	 * 
	 * @param obj
	 * @throws IOException
	 */
	private void appendElement(Object obj) throws IOException {
		if(obj instanceof Long)
			this.appendLong(((Long) obj).longValue());
		else if(obj instanceof Double)
			this.appendDouble(((Double) obj).doubleValue());
		else if(obj instanceof Integer)
			this.appendInt(((Integer) obj).intValue());
		else if(obj instanceof Float)
			this.appendFloat(((Float) obj).floatValue());
		else if(obj instanceof Boolean)
			this.appendBoolean(((Boolean) obj).booleanValue());
		else if(obj instanceof String)
			this.appendString(((String) obj).toString());
		else if(obj instanceof List)
			this.appendList((List) obj);
		else if(obj instanceof Map)
			this.appendMap((Map) obj);
		else
			throw new IOException("This type is not supported yet.");	
	}


	/**
	 * Write an ArrayList of Object in the message content
	 * @param al the Array List to write
	 * @throws IOException
	 */
	public void appendList(List<?> al) throws IOException{
		dout.writeByte(MessageUtils.LIST_TYPE);
		appendListUntyped(al);
	}

	/**
	 * 
	 * @param al
	 * @throws IOException
	 */
	private void appendListUntyped(List<?> al) throws IOException {
		dout.writeInt(al.size());
		if(al.size() == 0)
			return;
		if(this.isHeterogeneousList(al)){
			dout.writeByte(MessageUtils.HETERO_TYPE);
			for (Object o : al) {
				this.appendElement(o);
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
					dout.writeInt(((String) o).length());
					dout.writeUTF(((String) o).toString());
				}
				break;
			case MessageUtils.LIST_TYPE:
				for (Object o : al){
					this.appendListUntyped((List) o);
				}
				break;
			case MessageUtils.DICT_TYPE:
				for(Object o : al)
					this.appendMap((Map) o);

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
	private boolean isHeterogeneousList(List<?> al) throws IOException{
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

	/**
	 * 
	 * @param map
	 * @throws IOException
	 */
	public void appendMap(Map<String, Object> map) throws IOException{
		dout.writeByte(MessageUtils.DICT_TYPE);
		dout.writeInt(map.size());
		for (Map.Entry<String, Object> entry : map.entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			this.appendString(key);
			this.appendElement(value);			
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getLength() {
		return OutboundMessage.HEADER_SIZE + dout.size();
	}
}
