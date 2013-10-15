import env
from sockit import protocol

assert 'abcd' == protocol.UTF2str(protocol.str2UTF('abcd'))