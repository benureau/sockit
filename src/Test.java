import java.io.IOException;

import com.sun.corba.se.pept.transport.InboundConnectionCache;

import sockit.InboundMessage;
import sockit.InputMessage;
import sockit.OutputMessage;
import sockit.Server;


public class Test {

	/**
	 * @param args
	 * @return 
	 */
	public static void main(String[] args) {
		/*System.out.println("Begin test...");
		System.out.println("Utils.test");
		Utils.test();
		System.out.println("Message.test");
		Message.test();
		System.out.println("...end of tests");*/
		/*System.out.println("OutputMessage.test");
		OutputMessage o = new OutputMessage();
		o.setType(32);
		o.appendBoolean(true);
		o.appendDouble(5.5);
		o.appendInt(789);
		o.appendString("Hello !");
		byte[] ob = o.getBytes();
		System.out.println("Size of OutputMessage : " + ob.length);
		InputMessage i = new InputMessage(ob);
		System.out.println("Type of InputMessage is " + i.getType());
		try {
			System.out.println("Boolean in InputMessage : " + i.readBoolean());
			System.out.println("Double in InputMessage : " + i.readDouble());
			System.out.println("Int in InputMessage : " + i.readInt());
			System.out.println("String in InputMessage : " + i.readString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("...end of tests");*/
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

}
