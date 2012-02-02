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

class InputMessage(object):
            
    def __init__(self, type = 0, length = HEADER_SIZE, content = ""):
        """
        Build a message from a given type, length and content
        @param type    the type of the message
        @param length   the length of the message
        @param content  the content to put in the message
        """
        self.type = type
        assert length == HEADER_SIZE + len(content)
        self.length = length
        self.content = content
        self.cursor = 0
        
    def resetCursor(self):
        """Reset the cursor"""
        self.cursor = 0
        
    def getBytes(self):
        """Return a copy of the header + content of the message"""
        header  = headerStruct.pack(self.length, self.type)
        return header + content
                        
    def checkForOverflow(self, size):
        """
        Check if the content is up-to-date for a read of <size> bytes.
        Raise ValueError if the read exceed the content range.
        @param size  the number of bytes to read
        """
        if len(self.content) < self.cursor + size:
            raise ValueError, "Read exceed content range."
            
    def readBoolean(self):
        """
        Reads a float in the content of the message
        @return the float read
        """
        self.checkForOverflow(boolStruct.size)
        b = boolStruct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += boolStruct.size
        return b
        
    def readInt(self):
        """
        Reads an int in the content of the message
        @return the int read
        """
        self.checkForOverflow(intStruct.size)
        i = intStruct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += intStruct.size
        return i
        
    def readFloat(self):
        """
        Reads a float in the content of the message
        @return the float read
        """
        self.checkForOverflow(floatStruct.size)
        f = floatStruct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += floatStruct.size
        return f

    def readDouble(self):
        """
        Reads a double in the content of the message
        @return the double read
        """
        self.checkForOverflow(doubleStruct.size)
        d = doubleStruct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += doubleStruct.size
        return d

    def readString(self):
        """
        Reads a string in the content of the message
        @return the string read
        """
        self.checkForOverflow(intStruct.size)
        i = intStruct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += intStruct.size
        self.checkForOverflow(i)
        s = self.content[self.cursor:self.cursor+i]
        self.cursor  += i
        return s
