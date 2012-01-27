"""
Python port of the message Java class Message.
For consistency, the API is as similar as possible to the Java ones.
However, some methods are useless in the Python one (such as checkFreeSpace())
"""


HEADER_SIZE = 8
BUFFER_SIZE = 256

class Message(object):
    
    def __init__(self):
        self.type = 0
        self.length = HEADER_SIZE
        self.content = ""
        
    def __init__(self, type, length, content):
        self.type = type
        self.length = length
        self.content = content
        
    def resetCursor(self):
        