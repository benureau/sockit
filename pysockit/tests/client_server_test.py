#!/usr/bin/env python
# encoding: utf-8
"""
ClientServerTest.py

Created by Paul Fudal on 2013-10-24.
Copyright (c) 2013 __MyCompanyName__. All rights reserved.
"""

import sys
import os
import socket
import subprocess
import random
import time
import sockit
import signal
import traceback

zoo = [('$xz', 'n2"SqL3{', '2k3+Wb2\x7fDn!y\x0eyyA\x00c', -448146),
    (),
    (False, False, '_;\x00\x18N\x08%\x15KR1-\x17[_(BAd\x15', '\x1c~( &E'),
    ('\x00\x00Ew2:', -8592670.641777493, '?.'),
    ('\x11T\x05\x0byZk4:o\\\x00RN', 97576, -961919, -665894),
    (-6688899.80099246, 'R\x02\x00Gi\x06)\x00sIBuI\\\x0c\x18', True, -147184, True)]

def find_port():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
    sock.bind(('', 0))
    sock.listen(socket.SOMAXCONN)
    ipaddr, port = sock.getsockname()
    sock.close()
    return port

def launch_server(port, output):
    """Launch a subprocess for the Java sockit server"""

    server_file = os.path.dirname(os.path.realpath(__file__)) + '/' + 'ServerTest.jar'
    assert os.path.exists(server_file), "The file {} does not exist. Did you build the java code ?".format(server_file)

    cmd = "java -jar {} {}".format(server_file, port)
    if output:
        DEVNULL = None
    else:
        DEVNULL = open(os.devnull, 'wb')
    
    proc = subprocess.Popen(cmd, stdout=DEVNULL,
                            shell=True, preexec_fn=os.setsid)

    time.sleep(5)
    return proc

def randomstruct(max_depth=3):
    dice = random.randint(0, 4)
    if dice == 0:
        dice2 = random.randint(0, 1)
        return bool(dice2)
    elif dice == 1:
        return random.randint(-1000000, 100000)
    elif dice == 2:
        return random.uniform(-10000000.0, 100000.0)
    elif dice == 3:
        length = random.randint(0, 20)
        return ''.join(chr(random.randint(65, 90)) for _ in range(length))
    elif dice == 4:
        if max_depth > 0:
            length = random.randint(0, 5)
            return tuple(randomstruct(max_depth=max_depth-1) for _ in range(length))
        else:
            return random.randint(-1000000, 100000)
            
    else:
        # dict
        assert False

def clientTest(port):

    c = sockit.Client()
    b = c.connect("127.0.0.1", port)
    count = 1000;
    try:
        if b:
            print("CLIENT -> started")
            while count > 0:
                print "----------------------------------"
                st = randomstruct(1)
                print st
                if not st in zoo:
                    hello = sockit.OutboundMessage(port)
                    hello.append(st)
                    rep = c.sendAndReceive(hello)
                    st_t = rep.read()
                    if st != st_t:
                        print "MOTHER FUCKING LORDS OF GOD"
                    count -= 1
            c.disconnect()
            print("CLIENT -> stopped")
        else:
            print("CLIENT -> error")
        return
    except:
        traceback.print_exc()
        c.disconnect()

def main():
    port = find_port()
    j = launch_server(port, True)
    clientTest(port)
    os.killpg(j.pid, signal.SIGTERM)


if __name__ == '__main__':
    main()
