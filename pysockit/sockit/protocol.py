import struct
import types


HEADER_SIZE = 8

headerStruct = struct.Struct("!ii")
boolStruct   = struct.Struct("!?")
charStruct   = struct.Struct("!c")
intStruct    = struct.Struct("!i")
longStruct   = struct.Struct("!l")
floatStruct  = struct.Struct("!f")
doubleStruct = struct.Struct("!d")

typedict = {bool : b'?',
            int  : b'i',
            float: b'd',
            str  : b's',
            list : b'T',
            tuple: b'T',
            dict : b'D'}

structdict = {b'?': boolStruct,
              b'i': intStruct,
              b'f': floatStruct,
              b'd': doubleStruct,
              b'l': longStruct,
              b'c': charStruct}


utfStruct   = struct.Struct("!H")

def str2UTF(s):
    utf8 = s.encode('utf-8')
    length = len(utf8)
    format = '!' + str(length) + 's'
    return utfStruct.pack(length) + struct.pack(format, utf8)

def UTF2str(s):
    return s[2:]