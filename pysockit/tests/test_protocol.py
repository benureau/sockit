import env
from sockit import protocol

assert 'abcd' == protocol.chars2str(protocol.str2chars('abcd'))