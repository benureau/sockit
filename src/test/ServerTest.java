package test;

import java.util.ArrayList;

import sockit.InboundMessage;
import sockit.OutboundMessage;
import sockit.Server;

public class ServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String port = args[0];
		Server s = new Server();
		int _port = Integer.parseInt(port);
		s.start(_port);
		boolean run_server = true;
		while(run_server){
			if(s.getNumberOfMessages() > 0){
				InboundMessage im = s.receive();
				System.out.println("Reading a message...");
				OutboundMessage om = new OutboundMessage();
				om.setType(im.getType());
				boolean run = true;
				while(run){
					try{
						Object o = im.readElement();
						System.out.println("SERVER : reading -> " + o.getClass() + " : " + o);
						if(o.getClass() == ArrayList.class){
							for (Object obj : (ArrayList<?>) o) {
								System.out.println("       : reading -> " + obj.getClass());
							}
						}
						om.appendElement(o);
					}catch(Exception e){
						//System.out.println("catched !");
						//e.printStackTrace();
						run = false;
					}
				}
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				if(!s.send(om)){
					run_server = false;
					System.out.println("Error while sending. Quit...");
				}
			}
		}
		s.stop();
	}
}
