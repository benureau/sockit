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
		al1.add(new String("Je porte un string en léopard."));
		al1.add(new Double(Math.PI));
		al1.add(new Boolean(true));
		ArrayList<Double> al2 = new ArrayList<Double>();
		for(int i = 0; i < 100 ; i++)
			al2.add(new Double(i * Math.PI));
		al1.add(al2);
		
		// 2
		OutboundMessage om = new OutboundMessage();
		om.appendArrayList(al1);
		om.appendInt(1984);
		
		// 3
		InboundMessage im = new InboundMessage(om.getBytes());
		
		// 4
		ArrayList<?> al3 = im.readArrayList();
		System.out.println(al3.get(0));
		System.out.println(al3.get(1));
		System.out.println(al3.get(2));
		ArrayList<?> al4 = (ArrayList<?>) al3.get(3);
		for (Object o : al4) {
			System.out.println(((Double) o).doubleValue());
		}
		int a = im.readInt();
		System.out.println(a);
		
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
