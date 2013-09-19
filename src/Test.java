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

	public static void testArrayListInMessage(){
		System.out.println("Begin test...");

		int size = 1000;
		ArrayList<Double> al = new ArrayList<Double>();		
		for(int i = 0 ; i < size ; i++){
			al.add(new Double(i * Math.PI));
		}
		OutboundMessage om = new OutboundMessage();
		om.appendArrayList(al);
		InboundMessage im = new InboundMessage(om.getBytes());
		ArrayList<Double> alout;
		try {
			alout = (ArrayList<Double>) im.readArrayList();
			for(Double s : alout){
				System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("...end of tests");
	}
	
	public static void testMessages(){
		System.out.println("Begin test...");
		OutboundMessage o = new OutboundMessage();
		o.setType(32);
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
	}

	/**
	 * @param args
	 * @return 
	 */
	public static void main(String[] args) {
		Test.testArrayListInMessage();
	}

}
