from __future__ import print_function, division
import sys, os
import random

import sys, os
sys.path += [os.path.join(os.path.dirname(__file__), '../')]

import msg

num_it = 1024

raise DeprecationWarning("test is not up-to-date with code")

def test_message_int():
    """Test read and write ints."""
    result = True

    message = msg.Message()
    for i in range(num_it):
        message.appendInt(i)
        if message.length != msg.HEADER_SIZE + (i+1)*msg.intStruct.size:
            print("Size is ", message.length, " but should be ", msg.HEADER_SIZE + (i+1)*msg.intStruct.size)
            print("Error : message.appendInt")
            result = False

    message.resetCursor()
    for i in range(num_it):
        r = message.readInt()
        if r != i:
            print(r, " vs ", i)
            print("Error : message.read/appendInt")
            result = False

    return result

def test_message_float():
    """Test read and write floats."""
    result = True

    message = msg.Message()
    for i in range(num_it):
        message.appendFloat(i/128.789456)
        if message.length != msg.HEADER_SIZE + (i+1)*msg.floatStruct.size:
            print("Size is ", message.length, " but should be ", msg.HEADER_SIZE + (i+1)*msg.floatStruct.size)
            print("Error : message.appendFloat")
            result = False

    message.resetCursor()
    for i in range(num_it):
        r = message.readFloat()
        if abs(r - i/128.789456) > 0.000001:
            print(r, " vs ", i/128.789456)
            print("Error : message.read/appendFloat")
            result = False

    return result

def test_message_boolean():
    """Test read and write booleans."""
    result = True

    message = msg.Message()
    for i in range(num_it):
        message.appendBoolean(True if i % 2 == 0 else False)
        if message.length != msg.HEADER_SIZE + (i+1)*msg.boolStruct.size:
            print("Size is ", message.length, " but should be ", msg.HEADER_SIZE + (i+1)*msg.boolStruct.size)
            print("Error : message.appendBoolean")
            result = False

    message.resetCursor()
    for i in range(num_it):
        r = message.readBoolean()
        if r != (True if i % 2 == 0 else False):
            print(r, " vs ", (True if i % 2 == 0 else False))
            print("Error : message.read/appendBoolean")
            result = False

    return result


def test_message_string():
    """Test read and write strings."""
    result = True

    message = msg.Message()
    size = 0
    for i in range(num_it):
        message.appendString(str(i) + "azertyuiopqsdfghjklmwxcvbn")
        size += len(str(i) + "azertyuiopqsdfghjklmwxcvbn")
        if message.length != msg.HEADER_SIZE + (i+1)*msg.intStruct.size + size:
            print("Size is ", message.length, " but should be ", msg.HEADER_SIZE + (i+1)*msg.intStruct.size + size)
            print("Error : message.appendString")
            result = False

    message.resetCursor()
    for i in range(num_it):
        r = message.readString()
        if r != str(i) + "azertyuiopqsdfghjklmwxcvbn":
            print(r, " vs ", str(i) + "azertyuiopqsdfghjklmwxcvbn")
            print("Error : message.read/appendString")
            result = False

    return result


def test_message_mixed():
    """Test read and write mixed datatypes."""
    result = True

    message = msg.Message()
    size = 0
    for i in range(num_it):
        message.appendInt(8848)
        message.appendBoolean(True)
        message.appendFloat(128.789456)
        message.appendString(str(i) + "azertyuiopmlkjhgfdsqwxcvbn")

        size += msg.intStruct.size + msg.boolStruct.size + msg.floatStruct.size + msg.intStruct.size + len(str(i) + "azertyuiopqsdfghjklmwxcvbn")
        if message.length != msg.HEADER_SIZE + size:
            print("Size is ", message.length, " but should be ", msg.HEADER_SIZE + size)
            print("Error : message.appendMixed")
            result = False

    message.resetCursor()
    for i in range(num_it):
        a = message.readInt()
        b = message.readBoolean()
        c = message.readFloat()
        d = message.readString()
        if a != 8848:
            print("Error in int", i, a)
            result = False
        if not b is True:
            print("Errro in boolean", i, b)
            result = False
        if abs(c- 128.789456) > 0.00001:
            print("Error in float", i, c)
            result = False
        if d !=  str(i) + "azertyuiopmlkjhgfdsqwxcvbn":
            print("Error in string", i, d)
            result = False

    return result


        # // mixed
        # message = new Message();
        #       for(int j = 0 ; j < 1024 ; j++){
        #     message.resetCursor();
        #     message.appendInt(8848);
        #     message.appendBoolean(true);
        #     message.appendFloat((float) 128.789456);
        #     message.appendString("azertyuiopmlkjhgfdsqwxcvbn");
        #     message.resetCursor();
        #     if(message.readInt() != 8848){
        #         System.out.println("Error in Int");
        #         System.exit(0);
        #     }
        #     if(message.readBoolean() != true){
        #         System.out.println("Error in Boolean");
        #         System.exit(0);
        #     }
        #     if(message.readFloat() != (float) 128.789456){
        #         System.out.println("Error in Float");
        #         System.exit(0);
        #     }
        #     if(message.readString().compareTo("azertyuiopmlkjhgfdsqwxcvbn") != 0){
        #         System.out.println("Error in String");
        #         System.exit(0);
        #     }
        # }
        # System.out.println("OK : mixed types");


tests = [test_message_int,
         test_message_float,
         test_message_boolean,
         test_message_string,
         test_message_mixed,
        ]

if __name__ == "__main__":
    print("\033[1m%s\033[0m" % (__file__,))
    for t in tests:
        print('%s %s' % ('\033[1;32mPASS\033[0m' if t() else
                         '\033[1;31mFAIL\033[0m', t.__doc__))
