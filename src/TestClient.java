import java.io.IOException;

import sockit.Client;
import sockit.OutboundMessage;

import com.sun.source.tree.NewClassTree;


public class TestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client c = new Client();
		c.connect("127.0.0.1",  1985);
		for(int i = 10000 ; i < 1000000 ; i++){
			System.out.println("### : message with " + i + " integers.");
			OutboundMessage m = new OutboundMessage();
			m.setType(i);
			for(int j = 0 ; j < i ; j++){
				try {
					m.appendInt(i);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			c.send(m);
		}
		c.disconnect();
	}

}
