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
        
    @classmethod
    def from_bytes(cls, bytes):
        """
        Classmethod to create a message from a string, interpreted
        as a bytes array. Probably not really often useful.
        @bytes   the string holding the encoded message
        @return  the message created
        """
        msg_type = intStruct.unpack_from(bytes, 0)[0]
        length   = intStruct.unpack_from(bytes, 4)[0]
        if len(bytes) < length:
            raise ValueError, "Bytes array's encoded length does not fit the given ."
        content  = bytes[8:]
        
        msg = cls(msg_type, content)
        return msg

    def __init__(self, msg_type = None, content = ""):
        """
        Build a message from a given type,  content
        @param msg_type  the type of the message
        @param content   the content to put in the message
        """
        self.type = msg_type
        self.length = len(content)
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

    def readLong(self):
        """
        Reads a long in the content of the message
        @return the long read
        """
        self.checkForOverflow(longStruct.size)
        l = longStruct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += longStruct.size
        return l
        
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
