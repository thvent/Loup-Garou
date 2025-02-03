
class UnsupportedDeserilizationException(Exception):
    pass

class UnsupportedSerilizationException(Exception):
    pass

class UndefinedPacketException(Exception):
    pass

class Packet:

    PACKET_SIZE = 1024

    def __init__(self, id):
        self.__id = id

    @property
    def id(self):
        return self.__id

    def serialize(self, buf, obj):
        raise UnsupportedSerilizationException("Serilization unsupported for " + self.__class__.__name__)

    def deserialize(self, buf):
        raise UnsupportedDeserilizationException("Deserilization unsupported for " + self.__class__.__name__)

    def handle(self, client, obj):
        raise UnsupportedDeserilizationException("Can't do anything with " + self.__class__.__name__)
