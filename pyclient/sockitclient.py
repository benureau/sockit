"""
Python port of the Java Client class.
Contrary to the later, not yet thread-safe.
"""

import socket
import traceback
import struct

import msg

class Client(object):
    
    def __init__(self):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.isConnected = False
        self.output = None # output stream
        self.input  = None # input stream
        
        self.port = 0
        self.ip = ""
        self.socketLock = None
        
    def checkIP(ip):
    	"""
        Checks  if an string is an IPv4 address or not
        @param   ip an IP address as a string
    	@return  true if it's a valid IP address, or false if not.
    	"""
        tokens = ip.split(".")
        if len(tokens) != 4:
            return False
        for tk in tokens:
            if not (0 <= int(tk) <= 255):
                return False
        return True
        
    def connect(self, ip, port):
    	"""
    	Connect to a server at a specified ip address and port
    	@param   ip the ip of the server
    	@param   port the port where to connect
    	@return  True if connection is ok, False otherwise
    	"""
        #FIXME socketLock lock
        b = self.unprotectedConnect(ip, port)
        #FIXME socketLock unlock
        return b
        
    def reConnect(self):
    	"""
    	Try to reconnect using the previous ip and port
    	@return  True if connect ok, False otherwise
    	"""
        if self.port != -1 and self.ip != "":
            return self.connect(ip, port)
        return False
        
    def disconnect(self):
    	"""
    	Disconnects the client from the server
    	@return  True if disconnect ok, False otherwise (i think it couldn't append)
    	"""
        #FIXME socketLock lock
        b = self.unprotectedDisconnect()
        #FIXME socketLock unlock
        return b
        
    def send(message):
        """
	    Sends a message through the socket
	    @param   message the message to send
	    @return  True if the message is sent, False otherwise. If an error occurs during, the socket will be closed.
	    """
        #FIXME socketLock lock
        ret = self.unprotectedSend()
        if not ret:
            this.unprotectedDisconnect()
        #FIXME socketLock unlock
        return ret
        
	def sendAndReceive(message):
    	"""
    	This sends a message and wait for the answer
    	@param   message the message to send
    	@return  the answer message received
    	"""
		answer = None
        #FIXME socketLock lock
		if self.unprotectedSend(message):
			answer = self.unprotectedReceive
		}
		if answer is None:
			self.unprotectedDisconnect()
        #FIXME socketLock unlock
		return answer
        
	def unprotectedSend(message):
    	"""
    	Send a message without controlling if the socket is used
    	@param   message to send
    	@return  true or false if send fails
    	"""
		if self.isConnected:
			try:
				self.socket.send(message.getBytes())
				return True;
			except:
                traceback.print_exc(file=sys.stdout)
		return False;
	
	def unprotectedReceive():
    	"""
    	Receive a message without controlling if the socket is already used
    	@return  the message received or null
        """
		if self.isConnected:
			try:
				header = self.socket.recv(msg.HEADER_SIZE)
				if len(header) != msg.HEADER_SIZE:
                    return None
                data_len  = struct.unpack("!i", header[:4])
				data_type = struct.unpack("!i", header[4:])
                
                content = self.socket.recv(data_len - msg.HEADER_SIZE)
                if len(content) != msg.HEADER_SIZE:
				    return None
                m = msg.Message(data_type, data_len, content)
                return m
			except:
                traceback.print_exc(file=sys.stdout)
		return None
        
	def unprotectedConnect(self, ip, port):
        """
    	Connect to a server at a specified ip address and port without controlling the lock
    	
        @param ip the ip of the server
    	@param port the port where to connect
    	@return true if connection is ok, false otherwise
    	"""
		# check server address and port
		self.port = port;
		self.ip = ip;
		if checkIP(ip) and not self.isConnected:
			try:
			    self.socket.connect(ip, self.port)
				self.isConnected = True
			except:
				try:
					socket.close()
			    except:
                    traceback.print_exc(file=sys.stdout)
				# FIXME: probably a double of previous stack trace.
                traceback.print_exc(file=sys.stdout)
		return self.isConnected
    
	def unprotectedDisconnect(self):
        """
    	Disconnects the client from the server with controlling the lock
    	@return  True if disconnect ok, False otherwise (i think it couldn't append)
    	"""
	    if self.isConnected:
			self.isConnected = False;
			try:
				socket.close()
				return True
			except:
				traceback.print_exc(file=sys.stdout)
		return False
    