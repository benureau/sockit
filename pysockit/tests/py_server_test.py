import sockit

PORT = 1990

def serverTest():
    server = sockit.Server(True)
    server.start('lo0', PORT)
    run = True
    client = False
    while run:
        if server.get_number_of_messages() > 0:
            message = server.receive()
            if message == None:
                print("Server stopped")
                server.stop()
                run = False
            else:
                if message.type == 1 and not client:
                    client = True
                    print ("New client")
                if message.type == 2 and client:
                    i = message.read()
                    ret = sockit.OutboundMessage()
                    ret.type = message.type
                    ret.append(i * 2)
                    server.send(ret)
                    print("Exchange %i -> %i" % (i, i * 2))
                if message.type == 3 and client:
                    client = False
                    print("End client")
                    run = False
    server.stop()

if __name__ == "__main__":
    serverTest()
