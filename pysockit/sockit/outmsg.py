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

class OutboundMessage(object):
    """ Outbound messages class. """

    @classmethod
    def from_msg(cls, msg):
        """ Copy a message (inbound or outbound) as an OutboundMessage. """
        return cls.from_bytes(msg.getBytes)

    @classmethod
    def from_bytes(cls, bytes):
        """ Classmethod to create a message from a string, interpreted as a bytes array.

            Probably not really often useful.
            :arg bytes:   the string holding the encoded message
        """
        msg_type = intStruct.unpack_from(bytes, 0)[0]
        length   = intStruct.unpack_from(bytes, 4)[0]
        if len(bytes) < length:
            raise ValueError, "Bytes array's encoded length does not fit the given ."
        content  = bytes[8:]

        msg = cls(msg_type, content)
        return msg

    def __init__(self, type_msg = None, content = None):
        """ Create a message from a given type and content.

            :arg type_msg:     the type of the message
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
        """ Returns a message as a bytes array """
        if self.type is None:
            raise ValueError
        header  = headerStruct.pack(self.length, self.type)
        return header + ''.join(self.content)

    def append(self, v):
        """ Add a value in he content of the message

            :arg v:  the value to add. Type is automatically detected.
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
        """ Add a boolean in he content of the message """
        assert type(b) is types.BooleanType
        self.content.append(boolStruct.pack(b))
        self.length  += boolStruct.size

    def appendInt(self, i):
        """ Add an int in the content of the message """
        assert type(i) is types.IntType
        self.content.append(intStruct.pack(i))
        self.length  += intStruct.size

    def appendLong(self, l):
        """ Add a (C) long in the content of the message """
        assert type(l) is types.IntType or type(l) is types.LongType
        self.content.append(longStruct.pack(l))
        self.length  += longStruct.size

    def appendDouble(self, d):
        """ Add a double in the content of the message """
        assert type(d) is types.FloatType
        self.content.append(doubleStruct.pack(d))
        self.length  += doubleStruct.size

    def appendString(self, s):
        """ Add a string in the content of the message """
        assert type(s) is types.StringType
        self.content.append(intStruct.pack(len(s)))
        self.content.append(s)
        self.length  += intStruct.size + len(s)
