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

typedict = {types.BooleanType: '?',
            types.IntType:     'i',
            types.FloatType:   'd',
            types.LongType:    'l',
            types.StringType:  's',
            types.ListType:    'T',
            types.TupleType:   'T',
            types.DictType:    'D'}

structdict = {'?': boolStruct,
              'i': intStruct,
              'f': floatStruct,
              'd': doubleStruct,
              'l': longStruct,
              'c': charStruct}


utfStruct   = struct.Struct("!H")

def str2UTF(s):
    utf8 = s.encode('utf-8')
    length = len(utf8)
    format = '!' + str(length) + 's'
    return utfStruct.pack(length) + struct.pack(format, utf8)

def UTF2str(s):
    return s[2:]