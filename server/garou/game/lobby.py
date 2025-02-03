import logging
import random
import uuid

import garou
import garou.game as game
import garou.network.client as c
import garou.network.packet.packets as ps


class Lobby:

    __logger = logging.getLogger(__name__)

    def __init__(self, owner):
        self.__uuid = uuid.uuid4()
        self.__owner = owner
        self.__clients = []
        self.__max_player_count = 18
        self.__game = None
        self.__options = [60, 60, 60 // 4, 60 // 4, 60 // 4, 60 // 4, 60 // 4, 3, 3]
        Lobby.__logger.info("created {}".format(repr(self)))

    @property
    def owner(self):
        return self.__owner

    @property
    def uuid(self):
        return self.__uuid

    @property
    def max_player_count(self):
        return self.__max_player_count

    @property
    def clients(self):
        return self.__clients

    @property
    def options(self):
        return self.__options

    def get_game(self):
        return self.__game

    def start_game(self, from_client):
        if from_client is self.__owner:
            if len(self.__clients) >= 0:
                self.__game = game.Game(self, self.__clients)
                Lobby.__logger.info("started game")
                self.__game.start()
            else:
                from_client.send_packet(ps.PacketError, {'error': 'Lobby don\'t have enough players'})
        else:
            from_client.send_packet(ps.PacketError, {'error': 'You are not the owner of this lobby'})

    def end_game(self, reason):
        self.__game.end(reason)
        self.__game = None

    def join(self, player):
        if len(self.__clients) < self.__max_player_count:
            self.__clients.append(player)
            c.Client.send_packet_to_all(self.__clients, ps.PacketJoinLobby, {
                'uuid': None,
                'username': player.username
            }, exclude=[player])
            return True
        else:
            player.send_packet(ps.PacketError, {'error': 'Lobby is complete'})
            return False


    def leave(self, player):
        if player in self.__clients:
            if self.__game:
                self.__game.kill_player(player)
                self.__game.win_condition()

            self.__clients.remove(player)

            self.send_to_all_clients(ps.PacketLeaveLobby,
            {
                'username': player.username
            })

            c.Client.send_packet_to_all(garou.instance.server.clients_not_in_lobby, ps.PacketUpdateLobby, {
                'uuid': self.uuid,
                'nbPlayers': len(self.__clients)
            })

        if self.__clients:
            if player is self.__owner:
                self.__owner = random.choice(self.__clients)
            
                self.send_to_all_clients(ps.PacketOwnerLobby, {
                    'uuid': self.uuid,
                    'username': self.__owner.username
                })
        else:
            garou.instance.remove_lobby(self.uuid)

    def send_to_all_clients(self, packet, obj, exclude=[]):
        c.Client.send_packet_to_all(self.__clients, packet, obj, exclude)

    def message(self, from_client, msg):
        if from_client in self.__clients:
            msg = '{}: {}'.format(from_client.username, msg)
            self.send_to_all_clients(ps.PacketMessage, {
                'msg': msg
            }, exclude=[from_client])

    def close(self):
        Lobby.__logger.info("closed {}".format(self))
        if self.__game is not None:
            self.__game.end('Plus de joueurs')
        for c in self.__clients:
            c.leave_lobby(self)

    def in_game(self):
        return self.__game is not None

    def __repr__(self):
        return '{}\'s lobby ({}/{})'.format(self.__owner.username, len(self.__clients), self.__max_player_count)

    def __str__(self):
        return '{}\'s lobby'.format(self.__owner.username)
