import logging
import socket
import threading

import garou
import garou.network.client as client
import garou.network.packet.packets as packets


class Server:

    __logger = logging.getLogger(__name__)

    DEFAULT_TCP_PORT = 1501
    DEFAULT_UDP_PORT = 1500

    UDP_BYTES = bytearray([12, 4, 94])

    def __init__(self):
        Server.__logger.info('starting server...')

        self.__tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__udp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

        self.__tcp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.__udp.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        self.__tcp.bind(('', Server.DEFAULT_TCP_PORT))
        self.__udp.bind(('', Server.DEFAULT_UDP_PORT))

        self.__tcp_thread = threading.Thread(name='TCP Server', target=self.__tcp_routine)
        self.__udp_thread = threading.Thread(name='UDP Server', target=self.__udp_routine)

        self.__packets = packets.PacketList()
        self.__clients = []

    def __udp_routine(self):
        try:
            while True:
                udp_bytes, addr = self.__udp.recvfrom(len(Server.UDP_BYTES))
                if udp_bytes == Server.UDP_BYTES:
                    self.__udp.sendto(Server.UDP_BYTES, addr)
        except OSError:
            pass
        except Exception as e:
            garou.instance.print_stack_trace(e)

    def __tcp_routine(self):
        try:
            self.__tcp.listen(18)
            while True:
                client_socket, _ = self.__tcp.accept()
                socket = client.Client(self, client_socket)
                self.__clients.append(socket)
                Server.__logger.info("new client from {}".format(client_socket.getpeername()))
                socket.routine()
        except OSError:
            pass
        except Exception as e:
            garou.instance.print_stack_trace(e)
    
    def routine(self):
        Server.__logger.info('server running')
        try:
            self.__tcp_thread.start()
            self.__udp_thread.start()
            self.__tcp_thread.join()
            self.__udp_thread.join()
        except:
            pass
        self.close()

    @property
    def packets(self):
        return self.__packets

    def close(self):
        Server.__logger.info('closing server...')
        for c in self.__clients:
            c.close()
        self.__tcp.close()
        self.__udp.close()

    def close_client(self, client):
        self.__clients.remove(client)
        client.close()

    def handle_packet(self, buf, client):
        packet = self.__packets.get_packet_by_id(buf.get_byte())
        obj = packet.deserialize(buf)
        #print('packet {} {} received from {}'.format(type(packet).__name__, obj, repr(client)))
        packet.handle(client, obj)

    def has_same_username(self, username):
        for c in self.__clients:
            if c.username == username:
                return True
        return False

    @property
    def clients(self):
        return self.__clients

    @property
    def clients_not_in_lobby(self):
        return [x for x in self.__clients if not x.in_lobby()]