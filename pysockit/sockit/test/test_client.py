# Test if the Python client is compatible with the Java server

from __future__ import print_function, division
import sys, os
sys.path += [os.path.join(os.path.dirname(__file__), '../')]

import client
from outmsg import OutboundMessage

# Socket adress
PORT = 1984
IP = '127.0.0.1'

# Protocol
HELLO_TYPE = 0
BYE_TYPE   = 1
FIBO_TYPE  = 2

def clientTest():

    c = client.Client()
    b = c.connect(IP, PORT)

    try:
        if b:
            print("CLIENT -> started")

            hello = OutboundMessage()
            hello.type = HELLO_TYPE
            hello.appendString("Hello server !")
            c.send(hello)

            for i in range(41):
                f = OutboundMessage()
                f.type = FIBO_TYPE
                f.appendInt(i)
                print("CLIENT -> asking for fibo(", i ,")")
                a = c.sendAndReceive(f)
                if a is not None:
                    if a.type == FIBO_TYPE:
                        print("CLIENT -> fibo(", i, ") = ", a.readInt())
                    else:
                        print("CLIENT -> error in protocol")
                        c.disconnect()
                        break
                else:
                    print("CLIENT -> error in exchange")
                    c.disconnect()
                    break
            bye = OutboundMessage()
            bye.type = BYE_TYPE
            bye.appendString("Bye Server !")
            c.send(bye)
            c.disconnect()
            print("CLIENT -> stopped")
        else:
            print("CLIENT -> error")
        return
    except:
        c.disconnect()

if __name__ == "__main__":
    clientTest()
        