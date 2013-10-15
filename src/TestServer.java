import sockit.InboundMessage;
import sockit.Server;


public class TestServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server s = new Server();
		s.start(1985);
		while (true) {
			while(s.getNumberOfMessages() > 0){
				InboundMessage m = s.receive();
				System.out.println("### : received message of type " + m.getType() + " and length " + m.getLength());
			}
		}
	}

}
