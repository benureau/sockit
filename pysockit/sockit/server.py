"""
Python port of the Java Server class.
"""

import sys
import socket
import traceback
import struct
import select
import threading

import netifaces as ni
from . import inmsg

class _ServerThread(threading.Thread):
    
    def __init__(self, iface, port, debug = False):
        super(_ServerThread, self).__init__()
        self.BACKLOG = 1
        self.isReady = False
        self.port = port
        self.socket = None
        self.queueLock = threading.Lock()
        self.queue = []
        self.debug = debug
        self.client = None
        self.runServer = True
        self.iface = iface
        
    def receive(self):
        m = None
        if self.isReady:
            self.queueLock.acquire()
            size = len(self.queue)
            if size == 0:
                if self.debug:
                    print 'No message in queue (len(self.queue) = 0)'
            else:
                m = self.queue[0]
                del(self.queue[0])                
            self.queueLock.release()
        else:
            if self.debug:
                print 'Server not ready (sel.isReady = False)'
        return m
            
    def get_number_of_messages(self):
        self.queueLock.acquire()
        size = len(self.queue)
        self.queueLock.release()
        return size
        
    def send(self, message):
        if self.isReady:
            try:
                self.client.send(message.getBytes())
                return True;
            except:
                if self.debug:
                    traceback.print_exc(file=sys.stdout)
        return False
        
    def close(self):
        self.runServer = False
        try:
            self.socket.close()
        except:
            if self.debug:
                traceback.print_exc(file=sys.stdout)
        
    def run (self):
        try:
            iface = ni.ifaddresses(self.iface)
            while self.runServer:
                self.socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
                self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
                self.socket.bind((iface[2][0]['addr'], self.port))
                self.socket.listen(self.BACKLOG)
                if not self.isReady:
                    if self.debug :
                        print('SATUS : accepting new connections on port ' + str(self.port))
                    self.client, addr = self.socket.accept()
                    self.isReady = True
                    if self.debug :
                        print 'SATUS : now accepting messages'
                running = True
                while running:
                    header = self.client.recv(inmsg.HEADER_SIZE)
                    if len(header) == inmsg.HEADER_SIZE:
                        data_len  = struct.unpack("!i", header[:4])[0]
                        #print "received a header announcing of lenght %i" % data_len
                        data_type = struct.unpack("!i", header[4:])[0]

                        #print "fetching %i more bytes" % (data_len - inmsg.HEADER_SIZE)
                        content = ""
                        while len(content) < data_len - inmsg.HEADER_SIZE:
                            content += self.client.recv(data_len - inmsg.HEADER_SIZE - len(content))
                        #print "received a message of lenght %i" % data_len
                        if len(content) == data_len - inmsg.HEADER_SIZE:
                            if self.debug:
                                print("SATUS : received message of type %i and length %i)" % (data_type, data_len))
                            m = inmsg.InboundMessage(data_type, content)
                            self.queueLock.acquire()
                            self.queue.append(m)
                            self.queueLock.release()
                        else:
                            if self.debug:
                                print("Content has the wrong size (%i != %i)") % len(content), data_len - inmsg.HEADER_SIZE
                            running = False
                            self.isReady = False
                            self.client.close()
                            self.socket.close()
                            self.client = None
                    else:
                        if self.debug:
                            print("STATUS : header size was %i; it seems connection was closed.") % len(header)
                        running = False
                        self.isReady = False
                        self.client.close()
                        self.client = None
                        self.socket.close()
        except:
            if self.debug:
                traceback.print_exc(file=sys.stdout)

class Server(object):

    def __init__(self, debug = False):
        self.debug = debug
        self.lock = threading.Lock()
        self.serverThread = None
        
    def start(self, iface, port):
        self.lock.acquire()
        if self.serverThread != None:
            self.lock.release()
            return False
        self.serverThread = _ServerThread(iface, port, self.debug)
        self.serverThread.daemon = True
        self.serverThread.start()
        self.lock.release()
        return True
        
    def stop(self):
        self.lock.acquire()
        if self.serverThread == None:
            self.lock.release()
            return False
        self.serverThread.close()
        self.serverThread.join()
        self.serverThread = None
        self.lock.release()
        return True
        
    def send(self, message):
        self.lock.acquire()
        if self.serverThread == None:
            self.lock.release()
            return False
        ret = self.serverThread.send(message)
        self.lock.release()
        return ret
        
    def receive(self):
        self.lock.acquire()
        if self.serverThread == None:
            self.lock.release()
            return None
        message = self.serverThread.receive()
        self.lock.release()
        return message
        
    def get_number_of_messages(self):
        self.lock.acquire()
        if self.serverThread == None:
            self.lock.release()
            return -1
        size = self.serverThread.get_number_of_messages()
        self.lock.release()
        return size
        