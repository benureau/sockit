"""
Python port of the Java Client class.
Contrary to the later, not yet thread-safe.
"""

import sys
import socket
import traceback
import struct
import select

import inmsg

class Client(object):

    def __init__(self, debug = False):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.isConnected = False
        self.output = None # output stream
        self.input  = None # input stream

        self.port = 0
        self.ip = "127.0.0.1"
        self.socketLock = None

        self.debug = debug

    def checkIP(self, ip):
        """ Checks if an string is an IPv4 address or not

            :arg ip:  an IP address as a string
            :return:  True if it's a valid IP address, or False if not.
        """
        return True
        tokens = ip.split(".")
        if len(tokens) != 4:
            return False
        for tk in tokens:
            if not (0 <= int(tk) <= 255):
                return False
        return True

    def connect(self, ip, port = 0):
        """ Connect to a server at a specified ip address and port

            :arg ip:   the ip of the server
            :arg port: the port where to connect
            :return:  True if connection is ok, False otherwise
        """
        #FIXME socketLock lock
        b = self.unprotectedConnect(ip, port)
        #FIXME socketLock unlock
        self.port = self.socket.getsockname()[1]
        return b

    def reConnect(self):
        """ Try to reconnect using the previous ip and port

            :return:  True if connect ok, False otherwise
        """
        if self.port != -1 and self.ip != "":
            return self.connect(ip, port)
        return False

    def disconnect(self):
        """ Disconnects the client from the server

            :return:  True if disconnect ok, False otherwise (i think it couldn't append)
        """
        #FIXME socketLock lock
        b = self.unprotectedDisconnect()
        #FIXME socketLock unlock
        return b

    def send(self, message):
        """ Sends a message through the socket

            :arg message: the message to send
            :return:      True if the message is sent, False otherwise. If an error occurs during, the socket will be closed.
        """
        #FIXME socketLock lock
        ret = self.unprotectedSend(message)
        if not ret:
            self.unprotectedDisconnect()
        #FIXME socketLock unlock
        return ret

    def sendAndReceive(self, message, timeout = 100):
        """ This sends a message and wait for the answer

            :arg   message the message to send
            :return:  the answer message received
        """
        answer = None
        #FIXME socketLock lock
        if self.unprotectedSend(message):
            answer = self.unprotectedReceive(timeout)
        #FIXME socketLock unlock
        return answer

    def receive(self, timeout = float('inf')):
        answer = self.unprotectedReceive(timeout)
        return answer

    def unprotectedSend(self, message):
        """ Send a message without controlling if the socket is used

            :arg   message to send
            :return:  true or false if send fails
        """
        if self.isConnected:
            try:
                self.socket.send(message.getBytes())
                return True;
            except:
                if self.debug:
                    traceback.print_exc(file=sys.stdout)
        return False;

    def unprotectedReceive(self, timeout = 1.0):
        """ Receive a message without controlling if the socket is already used

            :return:  the message received or null
        """
        if self.isConnected:
            try:
                ready = select.select([self.socket], [], [], timeout)
                if ready[0]:
                    header = self.socket.recv(inmsg.HEADER_SIZE)
                    if len(header) != inmsg.HEADER_SIZE:
                        print("Header has the wrong size (%i != %i)" % (len(header), inmsg.HEADER_SIZE))
                        return None
                    data_len  = struct.unpack("!i", header[:4])[0]
                    #print "received a header announcing of lenght %i" % data_len
                    data_type = struct.unpack("!i", header[4:])[0]

                    #print "fetching %i more bytes" % (data_len - inmsg.HEADER_SIZE)
                    content = ""
                    while len(content) < data_len - inmsg.HEADER_SIZE:
                        content += self.socket.recv(data_len - inmsg.HEADER_SIZE - len(content))
                    #print "received a message of lenght %i" % data_len

                    if len(content) != data_len - inmsg.HEADER_SIZE:
                        print("Content has the wrong size (%i != %i)" % (len(content), data_len - inmsg.HEADER_SIZE))
                        return None
                    m = inmsg.InboundMessage(data_type, content)
                    return m
                return None
            except:
                traceback.print_exc(file=sys.stdout)
        return None

    def unprotectedConnect(self, ip, port):
        """ Connect to a server at a specified ip address and port without controlling the lock

            :arg ip: the ip of the server
            :arg port: the port where to connect
            :return: true if connection is ok, false otherwise
        """
        # check server address and port
        self.port = port;
        self.ip = ip;
        if not self.isConnected:
            if self.checkIP(ip):
                try:
                    self.socket.connect((ip, self.port))
                    self.isConnected = True
                except:
                    try:
                        self.socket.close()
                    except:
                        if self.debug:
                            traceback.print_exc(file=sys.stdout)
                    # FIXME: probably a double of previous stack trace.
                    if self.debug:
                        traceback.print_exc(file=sys.stdout)
            else:
                print("Not an ip adress %s" % (ip,))
        return self.isConnected

    def unprotectedDisconnect(self):
        """ Disconnects the client from the server with controlling the lock
            :return:  True if disconnect ok, False otherwise (i think it couldn't append)
        """
        if self.isConnected:
            self.isConnected = False;
            try:
                self.socket.close()
                return True
            except:
                traceback.print_exc(file=sys.stdout)
        return False
