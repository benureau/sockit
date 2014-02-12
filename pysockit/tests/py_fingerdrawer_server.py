import sockit

PORT = 12345

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
                for i in range(0, size):
                    print("x : %f  --  y : %f" % (message.read(), message.read()))
                    print("timestamp : %i" % message.read())
                    
    server.stop()

if __name__ == "__main__":
    serverTest()
