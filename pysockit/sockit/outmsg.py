"""
Python port of the message Java class Message.
For consistency, the API is as similar as possible to the Java ones.
However, some methods are useless in the Python one (such as checkFreeSpace())
"""

import types
import struct

HEADER_SIZE = 8

headerStruct = struct.Struct("!ii")
boolStruct   = struct.Struct("!?")
intStruct    = struct.Struct("!i")
longStruct   = struct.Struct("!l")
floatStruct  = struct.Struct("!f")
doubleStruct = struct.Struct("!d")

class OutputMessage(object):

    def __init__(self, type_msg = None, content = None):
        """ Build a message from a given type, content

            :arg type:     the type of the message
            :arg content:  the content to put in the message
        """
        self.type = type_msg
        self.length = HEADER_SIZE
        self.content = []
        if content is not None:
            self.addContent(contentList)

    def addContent(self, content):
        for a in content:
            if   type(a) == types.BooleanType:
                self.appendBoolean(a)
            elif type(a) == types.IntType:
                self.appendInt(a)
            elif type(a) == types.LongType:
                self.appendLong(a)
            elif type(a) == types.FloatType:
                self.appendFloat(a)
            elif type(a) == types.StringType:
                self.appendString(a)


    def getBytes(self):
        """ Returns a message as a bytes array

            :return: the message as a bytes array
        """
        if self.type is None:
            raise ValueError
        header  = headerStruct.pack(self.length, self.type)
        return header + ''.join(self.content)

    def append(self, v):
        """ Add a value in he content of the message

            :arg v:  the value to write. Type is automatically detected.
        """
        if type(v) == int:
            self.appendInt(v)
        elif type(v) == float:
            self.appendDouble(v)
        elif type(v) == str:
            self.appendString(v)
        elif type(v) == bool:
            self.appendBoolean(v)

    def appendBoolean(self, b):
        """ Add a boolean in he content of the message

            :arg b:  the boolean to write
        """
        assert type(b) is types.BooleanType
        self.content.append(boolStruct.pack(b))
        self.length  += boolStruct.size

    def appendInt(self, i):
        """ Add an int in the content of the message

            :arg i:  the int to write
        """
        assert type(i) is types.IntType
        self.content.append(intStruct.pack(i))
        self.length  += intStruct.size

    def appendLong(self, l):
        """ Add a (C) long in the content of the message

            :arg l:  the long to write
        """
        assert type(l) is types.IntType or type(l) is types.LongType
        self.content.append(longStruct.pack(l))
        self.length  += longStruct.size

    def appendDouble(self, d):
        """ Add a double in the content of the message

            :arg d:  the double to write
        """
        assert type(d) is types.FloatType
        self.content.append(doubleStruct.pack(d))
        self.length  += doubleStruct.size

    def appendString(self, s):
        """ Add a string in the content of the message

            :arg s:  the string to put in the message
        """
        assert type(s) is types.StringType
        self.content.append(intStruct.pack(len(s)))
        self.content.append(s)
        self.length  += intStruct.size + len(s)
