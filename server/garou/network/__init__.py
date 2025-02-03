import struct
import uuid

import garou

"""
Java ByteBuffer implementation in python.
"""
class ByteBuffer(bytearray):

    NULL_UUID = uuid.UUID(int=0)

    def __init__(self, *args, **kwargs):
        super(ByteBuffer, self).__init__(*args, **kwargs)
        self.view = None
        self.offset = 0
        self.limit = len(self)

    def flip(self):
        if self.view is None:           
            # read mode
            self.view = memoryview(self)
            self.limit = self.offset
            self.offset = 0
        else:
            # write mode
            self.view.release()
            self.view = None
            self.offset = self.limit
            self.limit = len(self)

    def get_byte(self):
        b = struct.unpack("!b", self.view[self.offset : self.offset + 1])
        self.offset += 1
        return b[0]

    def get_short(self):
        h = struct.unpack("!h", self.view[self.offset : self.offset + 2])
        self.offset += 2
        return h[0]

    def get_int(self):
        i = struct.unpack("!i", self.view[self.offset : self.offset + 4])
        self.offset += 4
        return i[0]

    def get_long(self):
        l = struct.unpack("!q", self.view[self.offset : self.offset + 8])
        self.offset += 8
        return l[0]

    def get_str(self):
        str_size = self.get_short()
        string = self[self.offset : self.offset + str_size].decode('utf-8')
        self.offset += str_size
        return string

    def get_uuid(self):
        try:
            u = uuid.UUID(bytes=bytes(self.view[self.offset : self.offset + 16]))
            if u == ByteBuffer.NULL_UUID:
                u = None
        except Exception as e:
            u = None
        self.offset += 16
        return u

    def get_ints(self):
        y = []
        for i in range(self.get_short()):
            y.append(self.get_int())
        return y


    def put_bytes(self, b):
        self[self.offset : self.offset + len(b)] = b
        self.offset += len(b)

    def put_byte(self, b):
        self[self.offset : self.offset + 1] = struct.pack("!b", b)
        self.offset += 1

    def put_short(self, h):
        self[self.offset : self.offset + 2] = struct.pack("!h", h)
        self.offset += 2

    def put_int(self, i):
        self[self.offset : self.offset + 4] = struct.pack("!i", i)
        self.offset += 4

    def put_long(self, l):
        self[self.offset : self.offset + 8] = struct.pack("!q", l)
        self.offset += 8

    def put_str(self, s):
        s = s.encode('utf-8')
        self.put_short(len(s))
        self[self.offset : self.offset + len(s)] = s
        self.offset += len(s)

    def put_ints(self, i):
        self.put_short(len(i))
        for y in i:
            self.put_int(y)
        
    def put_uuid(self, u):
        if u is None:
            u = uuid.UUID(int=0)
        self[self.offset : self.offset + len(u.bytes)] = u.bytes
        self.offset += len(u.bytes)

    def put_detailed_lobby(self, lobby):
        self.put_byte(0)
        self.put_uuid(lobby.uuid)
        self.put_str(lobby.owner.username)
        self.put_byte(lobby.max_player_count)
        self.put_str(','.join(list(map(lambda c: c.username, lobby.clients))))
        self.put_ints(lobby.options)

    def put_simple_lobby(self, lobby):
        self.put_byte(1)
        self.put_uuid(lobby.uuid)
        self.put_str(lobby.owner.username)
        self.put_byte(lobby.max_player_count)
        self.put_byte(len(lobby.clients))

    def reset(self):
        if self.view is not None:
            self.view.release()
            self.view = None
        self.offset = 0
        self.limit = len(self)

    def rewind(self):
        self.offset = 0

    def remaining(self):
        return self.limit - self.offset