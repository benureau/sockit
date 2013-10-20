""" Python port of the message Java class Message.
    For consistency, the API is as similar as possible to the Java ones.
    However, some methods are useless in the Python one (such as checkFreeSpace())
"""

import types
import struct

from .protocol import *


class InboundMessage(object):

    def __init__(self, msg_type = None, content = ""):
        """ Create a message from a given type,

            :arg msg_type:  the type of the message
            :arg content:   the content to put in the message
        """
        self.type = msg_type
        self.length = len(content)
        self.content = content
        self.cursor = 0

    def resetReadCursor(self):
        """ Restart the reading at the beginning of the message """
        self.cursor = 0

    def getBytes(self):
        """ Return a copy of the header + content of the message """
        header  = headerStruct.pack(self.length, self.type)
        return header + content

    def checkForOverflow(self, size):
        """ Check if the content is up-to-date for a read of <size> bytes.

        :arg size:         the number of bytes that would be read
        :raise ValueError: if the read exceed the content range.
        """
        if len(self.content) < self.cursor + size:
            raise ValueError("Read exceed content range.")

    def _read(self, type_str):
        """ Read an element, provided a format"""
        type_struct = structdict[type_str]
        self.checkForOverflow(type_struct.size)
        e = type_struct.unpack_from(self.content, self.cursor)[0]
        self.cursor  += type_struct.size
        return e

    def read(self):
        """ Read an element of the message """
        type_str = self._read('c')
        return InboundMessage._readdict[type_str](self, type_str)

    def _readString(self, *args):
        """ Reads a string in the content of the message """
        size = self._read('i')
        self.checkForOverflow(size)
        s = self.content[self.cursor:self.cursor+size]
        self.cursor  += size
        return UTF2str(s)

    def _readList(self, *args):
        """ Reads a tuple of elements in the content of the message"""
        list_size = self._read('i')
        type_str  = self._read('c')
        if type_str == 'x':
            return tuple(self.read() for _ in range(list_size))
        else:
            f_read = InboundMessage._readdict[type_str]
            return tuple(f_read(self, type_str) for _ in range(list_size))

    def _readDict(self, *args):
        """ Reads a dict with string keys"""
        d = {}
        size  = self._read('i')
        for i in range(size):
            key = self.read()
            value = self.read()
            d[key] = value
        return d

    _readdict = {'?': _read,
                 'i': _read,
                 'f': _read,
                 'd': _read,
                 'l': _read,
                 's': _readString,
                 'T': _readList,
                 'D': _readDict}

