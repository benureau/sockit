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
            
    def __init__(self, type = None):
        """
        Build a message from a given type, length and content
        @param type    the type of the message
        @param length   the length of the message
        @param content  the content to put in the message
        """
        self.type = type
        self.length = HEADER_SIZE
        self.content = []    
        
    def getBytes(self):
        """
        Returns a message as a bytes array
        @return the message as a bytes array
        """
        if self.type is None:
            raise ValueError
        header  = headerStruct.pack(self.length, self.type)
        return header + ''.join(self.content)
        
    def appendBoolean(self, b):
        """
        Puts a boolean in he content of the message
        @param b the boolean to write
        """
        assert type(b) is types.BooleanType
        self.content.append(boolStruct.pack(b))
        self.length  += boolStruct.size
        
    def appendInt(self, i):
        """
        Puts an int in the content of the message
        @param i   the int to write
        """
        assert type(i) is types.IntType
        self.content.append(intStruct.pack(i))
        self.length  += intStruct.size

    def appendLong(self, l):
        """
        Puts a (C) long in the content of the message
        @param l   the long to write
        """
        assert type(l) is types.IntType or type(l) is types.LongType
        self.content.append(longStruct.pack(l))
        self.length  += longStruct.size
        
    def appendFloat(self, f):
        """
        Puts a float in the content of the message
        @param f   the float to write
        """
        assert type(f) is types.FloatType
        self.content.append(floatStruct.pack(f))
        self.length  += floatStruct.size

    def appendDouble(self, d):
        """
        Puts a double in the content of the message
        @param d   the double to write
        """
        assert type(d) is types.FloatType
        self.content.append(doubleStruct.pack(f))
        self.length  += doubleStruct.size

    def appendString(self, s):
        """
        Put a string in the content of the message
        @param s   the string to put in the message
        """
        assert type(s) is types.StringType
        self.content.append(intStruct.pack(len(s)))
        self.content.append(s)
        self.length  += intStruct.size + len(s)
        