import sockit
import traceback
import sys

PORT = 1990
IP = '127.0.0.1'

def clientTest():

    c = sockit.Client(True)
    b = c.connect(IP, PORT)

    try:
        if b:
            print("CLIENT -> started")

            hello = sockit.OutboundMessage()
            hello.type = 1
            hello.append("Hello server !")
            c.send(hello)
                        
            for i in range(100):
                f = sockit.OutboundMessage()
                f.type = 2
                f.append(i)
                print("CLIENT -> sending ", i)
                a = c.sendAndReceive(f)
                if a is not None:
                    if a.type == 2:
                        print("CLIENT -> i = ", a.read())
                    else:
                        print("CLIENT -> error in protocol")
                        c.disconnect()
                        break
                else:
                    print("CLIENT -> error in exchange")
                    c.disconnect()
                    break
            bye = sockit.OutboundMessage()
            bye.type = 3
            bye.append("Bye Server !")
            c.send(bye)
            c.disconnect()
            print("CLIENT -> stopped")
        else:
            print("CLIENT -> error")
        return
    except:
        print ("CLIENT -> Exception")
        traceback.print_exc(file=sys.stdout)
        c.disconnect()

if __name__ == "__main__":
    clientTest()
