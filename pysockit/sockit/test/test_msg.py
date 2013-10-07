from __future__ import print_function, division

import sys, os
import random

import env
import inmsg
import outmsg
import protocol

num_it = 1024

#raise DeprecationWarning("test is not up-to-date with code")

def test_message_int():
    """Test read and write ints."""
    result = True

    message = outmsg.OutboundMessage(0)
    for i in range(num_it):
        message.append(i)
        correct_size = protocol.headerStruct.size + (i+1)*protocol.intStruct.size
        if message.length != correct_size:
            print("Size is ", message.length, " but should be ", correct_size)
            result = False

    message = inmsg.InboundMessage(0, content = message.getBytes(header = False))
    for i in range(num_it):
        r = message.read()
        if r != i:
            print(r, " vs ", i)
            result = False

    return result

def test_message_string():
    """Test read and write strings."""
    result = True

    message = outmsg.OutboundMessage(0)
    correct_size = protocol.headerStruct.size
    for i in range(num_it):
        s = str(i) + "azertyuiopqsdfghjklmwxcvbn"
        message.append(s)
        correct_size += protocol.intStruct.size + len(s)
        if message.length != correct_size:
            print("Size is ", message.length, " but should be ", correct_size)
            result = False

    message = inmsg.InboundMessage(0, content = message.getBytes(header = False))
    for i in range(num_it):
        r = message.read()
        if r != str(i) + "azertyuiopqsdfghjklmwxcvbn":
            print(r, " vs ", str(i) + "azertyuiopqsdfghjklmwxcvbn")
            result = False

    return result

def test_message_list():
    """Test read and write homogeneous list."""
    result = True

    message = outmsg.OutboundMessage(0)
    message.append(list(range(10)))

    message = inmsg.InboundMessage(0, content = message.getBytes(header = False))

    return message.read() == tuple(range(10))


def test_message_list2():
    """Test read and write heterogeneous list."""
    result = True

    l = [1, 2.0, 'abc', False, 2]
    message = outmsg.OutboundMessage(0)
    message.append(l)

    message = inmsg.InboundMessage(0, content = message.getBytes(header = False))

    return message.read() == tuple(l)


def test_message_list3():
    """Test read and write nested list."""
    result = True

    l = [1, 2.0, 'abc', ('abc', True, 3.1), False, 2, (1, 2)]
    message = outmsg.OutboundMessage(0)
    message.append(l)

    message = inmsg.InboundMessage(0, content = message.getBytes(header = False))

    l2 = message.read()
    return l2 == tuple(l)



tests = [test_message_int,
         test_message_string,
         test_message_list,
         test_message_list2,
         test_message_list3,
        ]

if __name__ == "__main__":
    print("\033[1m%s\033[0m" % (__file__,))
    for t in tests:
        print('%s %s' % ('\033[1;32mPASS\033[0m' if t() else
                         '\033[1;31mFAIL\033[0m', t.__doc__))
