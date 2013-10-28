"""
Python port of the message Java class Message.
For consistency, the API is as similar as possible to the Java ones.
However, some methods are useless in the Python one (such as checkFreeSpace())
"""

from .protocol import *


class OutboundMessage(object):
    """ Outbound messages class. """

    @classmethod
    def from_msg(cls, msg):
        """ Copy a message (inbound or outbound) as an OutboundMessage. """
        return cls.from_bytes(msg.getBytes)

    def __init__(self, type_msg = None, content = None):
        """ Create a message from a given type and content.

            :arg type_msg:     the type of the message
            :arg content:  the content to put in the message
        """
        self.type = type_msg
        self.length = headerStruct.size
        self.content = []
        if content is not None:
            for c in content:
                self.append(c)

    def getBytes(self, header = True):
        """ Returns a message as a bytes array """
        #print(self.type, self.length, len(''.join(self.content)), self.content)
        if self.type is None:
            raise ValueError
        content_array = ''.join(self.content)
        assert self.length == len(content_array) + headerStruct.size
        if header:
            header_bytes  = headerStruct.pack(self.length, self.type)
            return header_bytes + content_array
        else:
            return content_array

    def append(self, v):
        type_str = typedict[type(v)]
        self.content.append(charStruct.pack(type_str))
        self.length  += charStruct.size
        self._append(v)

    def _append(self, v):
        return OutboundMessage._appenddict[typedict[type(v)]](self, v)

    def _appendElement(self, v):
        type_str = typedict[type(v)]
        type_struct = structdict[type_str]
        self.content.append(type_struct.pack(v))
        self.length  += type_struct.size

    def _appendString(self, s):
        """ Add a string in the content of the message """
        utfs = str2chars(s)
        self.content.append(intStruct.pack(len(utfs)))
        self.content.append(utfs)
        self.length  += intStruct.size + len(utfs)

    def _appendList(self, a):
        """ Add a list in the content of the message """
        self.content.append(intStruct.pack(len(a)))
        if len(a) > 0:
            try:
                self._appendHomogeneousList(a)
            except ValueError:
                self._appendHeterogeneousList(a)
        else:
            self.length += intStruct.size

    def _appendHomogeneousList(self, a):
        firsttype = type(a[0])
        if not all(firsttype == type(e) for e in a):
            raise ValueError("not all type are the same")
        self.content.append(charStruct.pack(typedict[firsttype]))
        self.length  += intStruct.size + charStruct.size
        for e in a:
            self._append(e)

    def _appendHeterogeneousList(self, a):
        self.content.append(charStruct.pack(b'x'))
        self.length  += intStruct.size + charStruct.size
        for e in a:
            self.append(e)

    def _appendDict(self, d):
        self._append(len(d))
        for k, v in d.items():
            self.append(k)
            self.append(v)


    _appenddict = {b'?': _appendElement,
                   b'i': _appendElement,
                   b'f': _appendElement,
                   b'd': _appendElement,
                   b's': _appendString,
                   b'T': _appendList,
                   b'D': _appendDict}

