import java.io.IOException;
import java.util.ArrayList;

import sockit.InboundMessage;
import sockit.OutboundMessage;
import sockit.Server;

public class Test {
	public static void testServerJonasse(){
		Server s = new Server();
		if(!s.start(1234))
			return;
		while(true){
			if(s.getNumberOfMessages() > 0){
				InboundMessage m = s.receive();
				if(m.getType() == 1){
					String res = "";
					try{
						int n = m.readInt();
						for(int i = 0 ; i < n ; i++){
							float x = m.readFloat();
							float y = m.readFloat();
							long t = m.readLong();
							res += "[" + x + ", " + y + " : " + t + "]";
						}
					}catch (Exception e){
						System.out.println(e.getLocalizedMessage());
					}
					System.out.println(res);
				}
			}
		}
	}

	public static void testArrayListInMessage() throws IOException{
		System.out.println("Begin test...");

		// 1
		ArrayList<Object> al1 = new ArrayList<Object>();
		ArrayList<Integer> al2 = new ArrayList<Integer>();
		for(int j = 0; j < 2 ; j++)
			al2.add(new Integer(j));
		al1.add(al2);
		
		ArrayList<Integer> al3 = new ArrayList<Integer>();
		for(int j = 0; j < 3 ; j++)
			al3.add(new Integer(j));
		al1.add(al3);
		
		ArrayList<Integer> al4 = new ArrayList<Integer>();
		for(int j = 0; j < 4 ; j++)
			al4.add(new Integer(j));
		al1.add(al4);
		
		// 2
		OutboundMessage om = new OutboundMessage();
		om.appendList(al1);
		
		// 3
		InboundMessage im = new InboundMessage(om.getBytes());
		
		// 4
		ArrayList<?> al5 = im.readArrayList();
		for (Object p : al5){
			System.out.println("-- begin list");
			for (Object o : ((ArrayList<Object>)p)) {
				System.out.println(((Integer) o).intValue());
			}
			System.out.println("-- end list");
		}
		
		System.out.println("...end of tests");
	}
	
	public static void testMessages(){
		System.out.println("Begin test...");
		OutboundMessage o = new OutboundMessage();
		o.setType(32);
		try {
			o.appendBoolean(true);
			o.appendDouble(5.5);
			o.appendInt(789);
			o.appendString("Hello !");
			byte[] ob = o.getBytes();
			System.out.println("Size of OutboundMessage : " + ob.length);
			InboundMessage i = new InboundMessage(ob);
			System.out.println("Type of InboundMessage is " + i.getType());
			try {
				System.out.println("Boolean in InboundMessage : " + i.readBoolean());
				System.out.println("Double in InboundMessage : " + i.readDouble());
				System.out.println("Int in InboundMessage : " + i.readInt());
				System.out.println("String in InboundMessage : " + i.readString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("...end of tests");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	/**	
	 * @param args
	 * @return 
	 */
	public static void main(String[] args) {
		try {
			Test.testArrayListInMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
