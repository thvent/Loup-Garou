import logging
import traceback

import garou.network.client as c
import garou.network.packet.packets as ps
import garou.network.server as s

logging.basicConfig(format='%(asctime)s %(levelname)s (%(threadName)s): %(message)s', level=logging.INFO, datefmt='%Y-%m-%d')

class LoupGarou:

    __logger = logging.getLogger(__name__)

    def __init__(self):
        self.__server = s.Server()
        self.__lobbies = {}

    def run(self):
        try:
            self.__server.routine()
        except:
            self.close()

    @property
    def server(self):
        return self.__server

    @property
    def lobbies(self):
        return self.__lobbies.values()

    def add_lobby(self, lobby):
        self.__lobbies[lobby.uuid] = lobby
        lobby.owner.join_lobby(lobby)
        
        c.Client.send_packet_to_all(self.__server.clients_not_in_lobby, ps.PacketNewLobby, {
            'lobby': lobby
        })

    def remove_lobby(self, uuid):
        lobby = self.get_lobby(uuid)
        if lobby is not None:
            lobby.close()
            self.__lobbies.pop(uuid)

    def get_lobby(self, uuid):
        return self.__lobbies.get(uuid)

    def close(self):
        self.__server.close()
        for l in list(self.__lobbies.values()):
            self.remove_lobby(l.uuid)

    def print_stack_trace(self, e):
        traceback.print_exception(type(e), e, e.__traceback__)

instance = LoupGarou()