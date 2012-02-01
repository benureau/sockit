package sockit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import sockit.utils.Utils;


public class Server {

	// the main thread of the server
	ServerThread thread;
	// a lock just for fun
	private ReentrantLock lock;
	
	public Server(){
		this.lock = new ReentrantLock();
		this.thread = null;
	}

	/**
	 * Starts the server listening on a specified port number
	 * @param port the port where to listen
	 * @return true if the server starts correctly, false otherwise
	 */
	public boolean start(int port) {
		this.lock.lock();
		if(this.thread != null){
			this.lock.unlock();
			return false;
		}
		this.thread = new ServerThread(port);
		this.thread.start();
		this.lock.unlock();
		return true;
	}

	/**
	 * Stops the server
	 * @return true if the server is correctly stopped
	 */
	public boolean stop() {
		this.lock.lock();
		if(this.thread == null){
			this.lock.unlock();
			return false;
		}
		this.thread.close();
		this.thread = null;
		this.lock.unlock();
		return true;
	}

	/**
	 * Sends a message to the connected client
	 * @param message the message to send
	 * @return true if the message is sent, false otherwise
	 */
	public boolean send(Message message) {
		this.lock.lock();
		if(this.thread == null){
			this.lock.unlock();
			return false;
		}
		boolean b = this.thread.send(message);
		this.lock.unlock();
		return b;
	}

	/**
	 * Returns a message received. If no message are received in background, the method wait for a new one...
	 * @return the message received or null
	 */
	public Message receive() {
		this.lock.lock();
		if(this.thread == null){
			this.lock.unlock();
			return null;
		}
		Message r = this.thread.receive();
		this.lock.unlock();
		return r;
	}
	
	/**
	 * Returns the number of messages received
	 * @return the number of messages received
	 */
	public int getNumberOfMessages(){
		this.lock.lock();
		int n = thread.getNumberOfMessages();
		this.lock.unlock();
		return n;
	}
	
	/**
	 * This private class implement the server of the thread witch receive in background all the message
	 * @author pfudal
	 *
	 */
	private class ServerThread extends Thread {
		
		// number of client at the same time
		private static final int BACKLOG = 1;
		
		// indicates if listening or not
		private boolean isReady = false;
		// the port of the server
		private int port = 0;
		// the server socket
		private ServerSocket server = null;
		// lock to protect the queue containing messages received
		private ReentrantLock queueLock = null;
		// the queue containing received message
		private Vector<Message> queue;
		// input and output stream
		private InputStream in = null;
		private OutputStream out = null;
		
		/**
		 * Constructors of the server thread
		 */
		public ServerThread(int port){
			this.port = port;
			this.queueLock = new ReentrantLock();
			this.queue = new Vector<Message>();
		}
		
		/**
		 * Extract the first message in the queue
		 * @return the message or null if the queue is empty
		 */
		public Message receive() {
			if(isReady == false)
				return null;
			this.queueLock.lock();
			int size = this.queue.size();
			if(size == 0){
				this.queueLock.unlock();
				return null;
			}
			else{
				Message answer = this.queue.firstElement();
				this.queue.remove(0);
				this.queueLock.unlock();
				return answer;
			}
		}
		
		/**
		 * Returns the number of message in the queue
		 * @return the size of the queue
		 */
		public int getNumberOfMessages(){
			this.queueLock.lock();
			int s = this.queue.size();
			this.queueLock.unlock();
			return s;
		}

		/**
		 * Send a message through the socket
		 * @param message the message to send
		 * @return
		 */
		public boolean send(Message message) {
			if(isReady == false)
				return false;
			try {
				out.write(message.getBytes(), 0, (int) message.getLength());
				out.flush();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		/**
		 * Closes the socket. It is useful to do that before an interrupt
		 */
		public void close(){
			try {
				this.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.interrupt();
		}
		
		/**
		 * The main method of the thread (call when starts)
		 */
		public void run() {
			Socket s = null;
			try {
				// creating the socket (bind and listen included)
				this.server = new ServerSocket(this.port, BACKLOG);
				s = server.accept();
				this.in = s.getInputStream();
				this.out = s.getOutputStream();
				this.isReady = true;
				boolean running = true;
				while(running){
					byte[] header = new byte[Message.HEADER_SIZE];
					int len = in.read(header, 0, Message.HEADER_SIZE);
					if(len == Message.HEADER_SIZE){
						int length = Utils.readInt(header, 0);
						int type = Utils.readInt(header, Utils.INT_SIZE);
						byte[] content = new byte[length - Message.HEADER_SIZE];
						len = in.read(content, 0, length - Message.HEADER_SIZE);
						if(len == length - Message.HEADER_SIZE){
							Message message = new Message(type, length, content);
							this.queueLock.lock();
							this.queue.add(message);
							this.queueLock.unlock();
						}
						else{
							running = false;
						}
					}
					else{
						running = false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.isReady = false;
			try {
				this.in.close();
				this.out.close();
				s.close();
				this.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
}