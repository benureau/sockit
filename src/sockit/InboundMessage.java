package sockit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;


public class InboundMessage {

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
    public InboundMessage(int type, byte[] content){
        this.type = type;
        this.length = InboundMessage.HEADER_SIZE + content.length;
        this.content = new ByteArrayInputStream(content);
        this.din = new DataInputStream(this.content);
    }

    /**
     * Test constructors which build an object from a byte array
     * @param bytes the byte array to build the object
     */
    public InboundMessage(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bis);
        try {
            this.length = dis.readInt();
            this.type = dis.readInt();
            byte[] content = new byte[length - InboundMessage.HEADER_SIZE];
            for(int i = 0 ; i < length - InboundMessage.HEADER_SIZE ; i++)
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
        return ((Boolean) getNextElement(MessageUtils.BOOL_TYPE)).booleanValue();
    }


    /**
     * Reads a string in the content of the message
     * @return the string read
     * @throws IOException if read fails
     */
    public String readString() throws IOException{
        return ((String) getNextElement(MessageUtils.STRING_TYPE)).toString();
    }

    /**
     * Reads a double in the content of the message
     * @return the double read
     * @throws IOException if read fails
     */
    public double readDouble() throws IOException{
        return ((Double) getNextElement(MessageUtils.DOUBLE_TYPE)).doubleValue();
    }

    /**
     * Reads a float in the content of the message
     * @return the float read
     * @throws IOException if read fails
     */
    public float readFloat() throws IOException{
        return ((Float) getNextElement(MessageUtils.FLOAT_TYPE)).floatValue();
    }

    /**
     * Reads an int in the content of the message
     * @return the int read
     * @throws IOException if read fails
     */
    public int readInt() throws IOException{
        return ((Integer) getNextElement(MessageUtils.INT_TYPE)).intValue();
    }

    /**
     * Reads a long in the content of the message
     * @return the long read
     * @throws IOException
     */
    public long readLong() throws IOException{
        return ((Long) getNextElement(MessageUtils.LONG_TYPE)).longValue();
    }

    /**
     * Read an ArrayList of Object in the message content
     * @param al the Array List to write
     * @throws IOException Oh yeah.
     */
    public ArrayList<?> readArrayList() throws IOException{
        return (ArrayList<?>) getNextElement(MessageUtils.LIST_TYPE);
    }
    
    /**
     * 
     * @return
     * @throws IOException 
     */
	public HashMap<?, ?> readMap() throws IOException {
		return (HashMap<?, ?>) getNextElement(MessageUtils.DICT_TYPE);
	}


    /**
     * Returns the next element as an object without testing is the type is wrong or right.
     * @return the element as an Object
     * @throws IOException if the type of the element is not supported.
     */
    public Object readElement() throws IOException {
        byte type = din.readByte();
        //System.out.println("type : "+type);
        MessageUtils.isTypeExist(type);
        return readElement(type);
    }

    /**
     * Returns the next element as an object
     * @param expected the expected type of the element
     * @return the element
     * @throws IOException if the element is of the wrong type
     */
    private Object getNextElement(byte expected) throws IOException {
        byte type = din.readByte();
        //System.out.println("type : "+type);
        if(type != expected)
            throw new IOException("The next element type ("+type+") is not the expected one ("+expected+").");
        return readElement(type);
    }


    /**
     * Read an element of a given type
     */
    private Object readElement(byte type) throws IOException {
        Object o;
        switch (type) {
        case MessageUtils.BOOL_TYPE:
            o = (Object) new Boolean(din.readBoolean());
            break;
        case MessageUtils.DOUBLE_TYPE:
            o = (Object) new Double(din.readDouble());
            break;
        case MessageUtils.FLOAT_TYPE:
            o = (Object) new Float(din.readFloat());
            break;
        case MessageUtils.INT_TYPE:
            o = (Object) new Integer(din.readInt());
            break;
        case MessageUtils.LONG_TYPE:
            o = (Object) new Long(din.readLong());
            break;
        case MessageUtils.STRING_TYPE:
            int size2 = din.readInt();
            int size_bytes = 2 * size2 + 2;
			byte[] b_y = new byte[size_bytes];
            for(int i = 0 ; i < size_bytes ; i++)
            	b_y[i] = din.readByte();
            o = (Object) new String(b_y, "UTF-16");
            System.out.println("STRING OF THE DEATH : " + new String(b_y, "UTF-16"));
            break;
        case MessageUtils.LIST_TYPE:
            int size = din.readInt();
            if(size > 0){
            	byte sub_type = din.readByte();
            	o = (Object) new ArrayList<Object>(this.readArrayList(sub_type, size));
            }
            else{
            	o = (Object) new ArrayList<Object>();
            }
            break;
        case MessageUtils.DICT_TYPE:
            int sized = din.readInt();
            o = (Object) this.readHashMap(sized);
        break;

        default:
            throw new IOException("This type is not supported in InboundMessage class.");
        }
        return o;
    }

    /**
     * 
     * @param size
     * @return
     * @throws IOException
     */
    private HashMap<String, Object> readHashMap(int size) throws IOException {
        HashMap<String, Object> d = new HashMap<String, Object>();
        for (int i = 0; i < size; i++) {
            String key   = (String) this.getNextElement(MessageUtils.STRING_TYPE);
            Object value = this.readElement();
            d.put(key, value);
        }
        return d;
    }


    /**
     *
     * @param sub_type
     * @param size
     * @return
     * @throws IOException
     */
    private ArrayList<?> readArrayList(byte sub_type, int size) throws IOException {
        MessageUtils.isTypeExist(sub_type);
        ArrayList<Object> ret = new ArrayList<Object>();
        if(sub_type == MessageUtils.HETERO_TYPE){
            //System.out.println("hetero");
            for(int i = 0; i < size; i++){
                ret.add(readElement());
            }
        }
        else{
            //System.out.println("homo");
            switch (sub_type) {
            case MessageUtils.BOOL_TYPE:
                for(int i = 0; i < size; i++){
                    ret.add((Object) new Boolean(din.readBoolean()));
                }
                break;
            case MessageUtils.DOUBLE_TYPE:
                for(int i = 0; i < size; i++){
                    ret.add((Object) new Double(din.readDouble()));
                }
                break;
            case MessageUtils.FLOAT_TYPE:
                for(int i = 0; i < size; i++){
                    ret.add((Object) new Float(din.readFloat()));
                }
                break;
            case MessageUtils.INT_TYPE:
                for(int i = 0; i < size; i++){
                    ret.add((Object) new Integer(din.readInt()));
                }
                break;
            case MessageUtils.LONG_TYPE:
                for(int i = 0; i < size; i++){
                    ret.add((Object) new Long(din.readLong()));
                }
                break;
            case MessageUtils.STRING_TYPE:
                for(int i = 0; i < size; i++){
                	int size2 = din.readInt();
                    ret.add((Object) new String(din.readUTF()));
                }
                break;
            case MessageUtils.LIST_TYPE:
                for(int i = 0; i < size; i++){
                	int sub_size = din.readInt();
                    byte sub_sub_type = din.readByte();
                    ret.add(this.readArrayList(sub_sub_type, sub_size));
                }
                break;
            default:
                throw new IOException("This type is not supported in InboundMessage class.");
            }
        }
        return ret;
    }
}
