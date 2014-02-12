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
		//String port = args[0];
		Server s = new Server();
		//int _port = Integer.parseInt(port);
		s.start(12345);
		boolean run_server = true;
		while(run_server){
			if(s.getNumberOfMessages() > 0){
				InboundMessage im = s.receive();
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
					}catch(Exception e){
						//System.out.println("catched !");
						//e.printStackTrace();
						run = false;
					}
				}
			}
		}
		s.stop();
	}
}
