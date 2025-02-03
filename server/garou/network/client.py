import logging
import random
import threading

import garou
from garou.network import ByteBuffer
from garou.network.packet import (Packet, UndefinedPacketException,
                                  UnsupportedDeserilizationException,
                                  UnsupportedSerilizationException)


class Client:

    __logger = logging.getLogger(__name__)

    def __init__(self, server, socket):
        self.__server = server
        self.__socket = socket
        
        self.__lobby = None
        self.__username = None
        
        self.__client_thread = threading.Thread(name='Client {}'.format(repr(self)), target=self.__client_routine)

        self.__buffer_out = ByteBuffer(Packet.PACKET_SIZE)
        self.__buffer_in = ByteBuffer(Packet.PACKET_SIZE)


    @property
    def username(self):
        if self.__username is None or self.__username == '':
            self.__username = "Player{:03}".format(random.randrange(1, 10**3))
        return self.__username

    @username.setter
    def username(self, username):
        if username is not None and username != '' and not garou.instance.server.has_same_username(username):
            self.__username = username

    def leave_lobby(self):
        if self.__lobby is not None:
            Client.__logger.info("leaved " + str(self.__lobby))
            self.__lobby.leave(self)
            self.__lobby = None
            return True
        return False
    
    def join_lobby(self, lobby):
        self.leave_lobby()
        if lobby.join(self):
            self.__lobby = lobby
            Client.__logger.info("joined " + str(lobby))

    def get_lobby(self):
        return self.__lobby

    def in_lobby(self):
        return self.__lobby is not None

    def routine(self):
        self.__client_thread.start()

    def __client_routine(self):
        try:
            while True:
                buf = self.__socket.recv(Packet.PACKET_SIZE)
                if buf == -1:
                    break            
                self.__buffer_in.put_bytes(buf)
                self.__buffer_in.flip()
                self.__server.handle_packet(self.__buffer_in, self)
                self.__buffer_in.reset()
        except OSError:
            pass
        except Exception as e:
            garou.instance.print_stack_trace(e)
        self.__server.close_client(self)

    def close(self):
        Client.__logger.info("client closed")
        self.leave_lobby()
        self.__socket.close()

    def send_packet(self, packet_type, obj):
        try:
            packet = self.__server.packets.get_packet_by_type(packet_type)
            #print('packet {} {} sended to {}'.format(packet_type.__name__, obj, repr(self)))
            self.__buffer_out.put_byte(packet.id)
            packet.serialize(self.__buffer_out, obj)
            self.__socket.send(self.__buffer_out)
            self.__buffer_out.reset()
        except (UndefinedPacketException, UnsupportedDeserilizationException, UnsupportedSerilizationException) as e:
            garou.instance.print_stack_trace(e)
        except OSError:
            pass

    def send_packet_to_all(clients, packet_type, obj, exclude=[]):
        for c in [c for c in clients if c not in exclude]:
            c.send_packet(packet_type, obj(c) if callable(obj) else obj)

    def __repr__(self):
        return '{} ({})'.format(self.__socket.getpeername()[0], self.username)
