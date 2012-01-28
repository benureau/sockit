"""
Python port of the message Java class Message.
For consistency, the API is as similar as possible to the Java ones.
However, some methods are useless in the Python one (such as checkFreeSpace())
"""

import types
import struct

HEADER_SIZE = 8
BUFFER_SIZE = 256 # Not used.

boolStruct   = struct.Struct("!?")
intStruct    = struct.Struct("!i")
floatStruct  = struct.Struct("!f")
headerStruct = struct.Struct("!ii")        

class Message(object):
            
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
        self.contentList = []        

    def resetCursor(self):
        """Reset the cursor"""
        self.softCursor = 0
        
    def getBytes(self):
        """Return a copy of the header + content of the message"""
        header  = self.headerStruct.pack(self.length, self.type)
        return header + ''.join(self.contentList)
        
    def checkForFreeSpace(self, size):
        """Does nothing. Here for consistency with Java API"""
        pass
        
    def appendBoolean(self, b):
        """
        Puts a boolean in he content of the message
        @param b the boolean to put
        """
        assert type(b) is types.BooleanType
        self.contentList.append(boolStruct.pack(b))
        self.length  += boolStruct.size
        
    def appendInt(self, i):
        """
        Puts an int in the content of the message
        @param i   the int to put
        """
        assert type(i) is types.IntType
        self.contentList.append(intStruct.pack(i))
        self.length  += intStruct.size
        
    def appendFloat(self, f):
        """
        Puts a float in the content of the message
        @param f   the float read
        """
        assert type(f) is types.FloatType
        self.contentList.append(floatStruct.pack(f))
        self.length  += floatStruct.size

    def appendString(self, s):
        """
        Put a string in the content of the message
        @param s   the string to put in the message
        """
        assert type(s) is types.StringType
        self.contentList.append(intStruct.pack(len(s)))
        self.contentList.append(s)
        self.length  += intStruct.size + len(s)
        
    def checkForOverflow(self, size):
        """
        Check if the content is up-to-date for a read of <size> bytes.
        Raise ValueError if the read exceed the content range.
        @param size  the number of bytes to read
        """
        if len(self.content) < self.cursor + size:
            if self.length - HEADER_SIZE < self.cursor + size:
                raise ValueError, "Read exceed content range."
            else:
                self.content = ''.join(self.contentList)
        
    def readBoolean(self):
        """
        Reads a float in the content of the message
        @return the float read
        """
        self.checkForOverflow(boolStruct.size)
        b = boolStruct.unpack_from(self.content, self.cursor)
        self.cursor  += boolStruct.size
        return b[0]
        
    def readInt(self):
        """
        Reads an int in the content of the message
        @return the int read
        """
        self.checkForOverflow(intStruct.size)
        i = intStruct.unpack_from(self.content, self.cursor)
        self.cursor  += intStruct.size
        return i[0]
        
    def readFloat(self):
        """
        Reads a float in the content of the message
        @return the float read
        """
        self.checkForOverflow(floatStruct.size)
        f = floatStruct.unpack_from(self.content, self.cursor)
        self.cursor  += floatStruct.size
        return f[0]

    def readString(self):
        """
        Reads a string in the content of the message
        @return the string read
        """
        self.checkForOverflow(intStruct.size)
        i = intStruct.unpack_from(self.content, self.cursor)
        self.cursor  += intStruct.size
        self.checkForOverflow(i[0])
        s = self.content[self.cursor:self.cursor+i[0]]
        self.cursor  += i[0]
        return s
