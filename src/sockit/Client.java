package sockit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;


public class Client {

    // socket used to communicate with kernel module
    private Socket socket;
    // port and address used to connect to server
    private int port;
    // ip address of the server
    private String ip;
    // boolean indicated if network is connected or
    private boolean isConnected = false;
    // lock because thread access to network
    private ReentrantLock socketLock;
    // input and output stream
    private OutputStream out;
    private InputStream in;

    /**
     * Constructor of the object CLient
     */
    public Client(){
        this.port = 0;
        this.ip = "";
        this.socketLock = new ReentrantLock();
    }

    /**
     * Checks if an string is an IPv4 address or not
     * @param ip an IP address as a string
     * @return true if it's a valid IP address, or false if not.
     */
    private boolean checkIP(String ip){
        StringTokenizer st = new StringTokenizer(ip, ".");
        int n = 0;
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            int robid = Integer.parseInt(tok);
            if(robid < 0 || robid > 255){
                return false;
            }
            else{
                n++;
            }
        }
        if (n == 4)
            return true;
        return false;
    }

    /**
     * Connect to a server at a specified ip address and port
     * @param ip the ip of the server
     * @param port the port where to connect
     * @return true if connection is ok, false otherwise
     */
    public boolean connect(String ip, int port) {
        // check server address and port
        this.socketLock.lock();
        boolean b = unprotectedConnect(ip, port);
        this.socketLock.unlock();
        return b;
    }

    /**
     * Try to reconnect using the previous ip and port
     * @return true if connect ok, false, otherwise
     */
    public boolean reConnect() {
        if(this.port != -1 && this.ip.compareTo("") != 0)
            return this.connect(this.ip, this.port);
        return false;
    }

    /**
     * Accessor of the member isConnected
     * @return true or false
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Disconnects the client from the server
     * @return true if disconnect ok, false otherwise (i think it couldn't append)
     */
    public boolean disconnect() {
        this.socketLock.lock();
        boolean b = unprotectedDisconnect();
        this.socketLock.unlock();
        return b;
    }

    /**
     * Sends a message through the socket
     * @param message the message to send
     * @return true if the message is sent, false otherwise. If an error occurs during, the socket will be closed.
     */
    public boolean send(OutputMessage message) {
        this.socketLock.lock();
        boolean ret = this.unprotectedSend(message);
        if(ret == false)
            this.unprotectedDisconnect();
        this.socketLock.unlock();
        return ret;
    }

    /**
     * This sends a message and wait for the answer
     * @param message the message to send
     * @return the answer message received
     */
    public InputMessage sendAndReceive(OutputMessage message) {
        InputMessage answer = null;
        this.socketLock.lock();
        if(this.unprotectedSend(message) == true){
            answer = this.unprotectedReceive();
        }
        if(answer == null)
            this.unprotectedDisconnect();
        this.socketLock.unlock();
        return answer;
    }
    
    /**
     * Send a message without controlling if the socket is used
     * @param message to send
     * @return true or false if send fails
     */
    private boolean unprotectedSend(OutputMessage message){
        if(isConnected){
            try {
                System.out.println("J'envoie " + message.getBytes().length + " bytes");
                out.write(message.getBytes(), 0, (int) message.getBytes().length);
                out.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Receive a message without controlling if the socket is already used
     * @return the message received or null
     */
    private InputMessage unprotectedReceive(){
        if(isConnected == true){
            try{
                byte[] header = new byte[InputMessage.HEADER_SIZE];
                int len = in.read(header, 0, InputMessage.HEADER_SIZE);
                if(len != InputMessage.HEADER_SIZE)
                    return null;
                ByteArrayInputStream bis = new ByteArrayInputStream(header);
                DataInputStream dis = new DataInputStream(bis);
                int length = dis.readInt();
                int type = dis.readInt();
                byte[] content = new byte[length - InputMessage.HEADER_SIZE];
                len = in.read(content, 0, length - InputMessage.HEADER_SIZE);
                if(len != length - InputMessage.HEADER_SIZE)
                    return null;
                InputMessage message = new InputMessage(type, content);
                return message;
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Connect to a server at a specified ip address and port without controlling the lock
     * @param ip the ip of the server
     * @param port the port where to connect
     * @return true if connection is ok, false otherwise
     */
    public boolean unprotectedConnect(String ip, int port) {
        // check server address and port
        this.port = port;
        this.ip = ip;
        if(checkIP(ip)){
            if(!isConnected){
                try {
                    socket = new Socket(ip, this.port);
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                    this.isConnected = true;
                    return true;
                } catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }
        return this.isConnected;
    }
    
    /**
     * Disconnects the client from the server with controlling the lock
     * @return true if disconnect ok, false otherwise (i think it couldn't append)
     */
    public boolean unprotectedDisconnect() {
        if(isConnected){
            this.isConnected = false;
            try {
                socket.close();
                in.close();
                out.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
