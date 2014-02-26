import sockit

PORT = 12345

NO_PARTIAL_MESSAGE = 0;
PARTIAL_MESSAGE_SART = 1;
PARTIAL_MESSAGE_SEQ = 2;
PARTIAL_MESSAGE_END = 3;

def serverTest():
    server = sockit.Server(True)
    server.start('en1', PORT)
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
                size = message.read()
                if message.type == NO_PARTIAL_MESSAGE:
                    for i in range(0, size):
                        print("x : %f  --  y : %f" % (message.read(), message.read()))
                        print("timestamp : %i" % message.read())
                else:
                    print 'not implemented yet'
                    
    server.stop()

if __name__ == "__main__":
    serverTest()
