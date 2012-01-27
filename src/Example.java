import playground.Client;
import playground.Message;
import playground.Server;


public class Example {
	
	// the port number to use
	public static final int PORT = 1984;
	// the local ip
	public static final String ip = "127.0.0.1";
	
	/** PROTOCOL **/
	public static final int HELLO_TYPE = 0;// a string in the message to the server, no answer
	public static final int BYE_TYPE = 1;// a string in the message to the server, empty answer
	public static final int FIBO_TYPE = 2;// a int in the message to the server, an int in the answer
	
	// this class allows to run the client in a thread
	public class ClientThread extends Thread{
		public void run(){
			ClientTest();
		}
	}
	
	// this class allows to run the server in a thread
	public class ServerThread extends Thread{
		public void run(){
			ServerTest();
		}
	}
	
	// the main function wich launch the server and the client and waits for the end of both
	public static void main(String[] args) {
		Example e = new Example();
		ClientThread c = e.new ClientThread();
		ServerThread s = e.new ServerThread();
		s.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		c.start();
		try {
			c.join();
			s.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	// server code
	public static void ServerTest(){
		Server s = new Server();
		s.start(PORT);
		System.out.println("SERVER -> started");
		boolean run = true;
		boolean count = false;
		while(run == true){
			int n = s.getNumberOfMessages();
			if(n > 0){
				Message in = s.receive();
				int type = in.getType();
				switch(type){
				case HELLO_TYPE:
					{
						System.out.println("SERVER -> receives : " + in.readString());
						count = true;
						break;
					}
				case BYE_TYPE:
					{
						System.out.println("SERVER -> receives : " + in.readString());
						Message a = new Message();
						a.setType(BYE_TYPE);
						s.send(a);
						count = false;
						run = false;
						break;
					}
				case FIBO_TYPE:
					{
						if(count == true){
							int f = in.readInt();
							System.out.println("SERVER -> computes fibo(" + f + ")");
							int t = fibo(f);
							Message a = new Message();
							a.setType(FIBO_TYPE);
							a.appendInt(t);
							s.send(a);
						}
						else{
							System.out.println("SERVER -> client is rude ! No answer !");
							run = false;
						}
						break;
					}
				}
			}
		}
		s.stop();
		System.out.println("SERVER -> stopped");
		return;
	}
	
	// client code
	public static void ClientTest(){
		Client c = new Client();
		boolean b = c.connect("127.0.0.1", PORT);
		if(b == true){
			System.out.println("CLIENT -> started");
			Message hello = new Message();
			hello.setType(HELLO_TYPE);
			hello.appendString("Hello server !");
			c.send(hello);
			for(int i = 1 ; i < 41 ; i++){
				Message f = new Message();
				f.setType(FIBO_TYPE);
				f.appendInt(i);
				System.out.println("CLIENT -> asking for fibo(" + i + ")");
				Message a = c.sendAndReceive(f);
				if(a != null){
					if(a.getType() == FIBO_TYPE){
						System.out.println("CLIENT -> fibo(" + i + ") = " + a.readInt());
					}
					else{
						System.out.println("CLIENT -> error in protocol");
						c.disconnect();
						break;
					}
				}
				else{
					System.out.println("CLIENT -> error in exchange");
					c.disconnect();
					break;
				}
			}
			Message bye = new Message();
			bye.setType(BYE_TYPE);
			bye.appendString("Bye Server !");
			c.send(bye);
			c.disconnect();
			System.out.println("CLIENT -> stopped");
		}
		else{
			System.out.println("CLIENT -> error");
		}
		return;
	}
	
	// recursive version of fibo
	public static int fibo(int n) {
        if (n <= 1) return n;
        else return fibo(n-1) + fibo(n-2);
    }
}