import logging
import random
import threading
import time
from enum import IntEnum

import garou.network.client as c
import garou.network.packet.packets as ps


class Role(IntEnum):
    ICONNU = 0
    VILLAGEOIS = 1
    CHASSEUR = 2
    CUPIDON = 3
    FILLE = 4
    LOUP = 5
    MAIRE = 6
    SORCIERE = 7
    VOLEUR = 8
    VOYANTE = 9

class TimeEvent(IntEnum):
    DAY = -1
    NIGHT = -2

class Game:
    
    __logger = logging.getLogger(__name__)

    REPARTS_ROLE = [
        [Role.LOUP] * 0 + [Role.VOYANTE] * 0 + [Role.CUPIDON] * 0 + [Role.VILLAGEOIS] * 0 + [Role.SORCIERE] * 0 + [Role.CHASSEUR] * 0,
        [Role.LOUP] * 0 + [Role.VOYANTE] * 0 + [Role.CUPIDON] * 0 + [Role.VILLAGEOIS] * 1 + [Role.SORCIERE] * 0 + [Role.CHASSEUR] * 0,
        [Role.LOUP] * 1 + [Role.VOYANTE] * 0 + [Role.CUPIDON] * 0 + [Role.VILLAGEOIS] * 0 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 0,

        [Role.LOUP] * 1 + [Role.VOYANTE] * 0 + [Role.CUPIDON] * 0 + [Role.VILLAGEOIS] * 1 + [Role.SORCIERE] * 0 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 1 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 0 + [Role.VILLAGEOIS] * 2 + [Role.SORCIERE] * 0 + [Role.CHASSEUR] * 0,
        [Role.LOUP] * 1 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 0 + [Role.VILLAGEOIS] * 3 + [Role.SORCIERE] * 0 + [Role.CHASSEUR] * 0,
        [Role.LOUP] * 1 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 2 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 0,
        [Role.LOUP] * 2 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 2 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 0,
        [Role.LOUP] * 2 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 2 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 2 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 3 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 2 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 4 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 2 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 5 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 5 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 6 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 7 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 8 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 9 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 10 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
        [Role.LOUP] * 3 + [Role.VOYANTE] * 1 + [Role.CUPIDON] * 1 + [Role.VILLAGEOIS] * 11 + [Role.SORCIERE] * 1 + [Role.CHASSEUR] * 1,
    ]

    def __init__(self, lobby, clients):

        self.__lobby = lobby
        self.__game_thread = threading.Thread(target=self.__game_routine)
        self.__exit = threading.Event()
        self.__skip = False

        self.__players = {} # all players, even dead
        self.__teams = (  # players by teams
            ('villageois', []),
            ('loups', [])
        )

        self.__player_by_roles = [[] for i in range(len(Role))]
        self.__can_vote = []

        self.__running = True
        self.__killed = []

        self.__day = 1
        self.__time_event = None # event of the game (DAY, NIGHT, TURNS...)

        self.__init_players(clients)

        

    def __init_players(self, clients):
        roles = Game.REPARTS_ROLE[len(clients)].copy()
        assert len(roles) == len(clients)

        for c in clients:
            c.alive = True
            c.role = random.choice(roles)
            c.vote = 0
            c.voted = None
            c.lover = None

            if c.role == Role.CUPIDON:
                c.lover1 = None
                c.lover2 = None
            elif c.role == Role.SORCIERE:
                c.has_health_potion = True
                c.has_poison = True

            self.__players[c.username] = c
            self.__teams[1 if c.role == Role.LOUP else 0][1].append(c)
            self.__player_by_roles[c.role].append(c)
            roles.remove(c.role)

    def add_vote_player(self, voter, who):

        if self.__running and voter in self.__players.values() and voter.alive and who in self.__players and self.__players[who].alive and voter in self.__can_vote:
            self.remove_vote_player(voter)
            voter.voted = self.__players[who]
            voter.voted.vote += 1
            
            c.Client.send_packet_to_all(self.__can_vote, ps.PacketAddVote, {
                'who': who
            }, exclude=[voter])

    def remove_vote_player(self, voter):
        if self.__running and voter in self.__players.values() and voter.alive and voter in self.__can_vote and voter.voted is not None:
            voter.voted.vote -= 1
            voted = voter.voted
            voter.voted = None

            c.Client.send_packet_to_all(self.__can_vote, ps.PacketRemoveVote, {
                'who': voted.username
            }, exclude=[voter])

    def kill_player(self, who):
        if self.__running and who in self.__players.values() and who.alive:

            if who.lover is not None:
                who.lover.lover = None
                self.kill_player(who.lover)

            if who.role == Role.CHASSEUR:
                self.__timeevent(Role.CHASSEUR, 15)

            who.alive = False
            self.__teams[1 if who.role == Role.LOUP else 0][1].remove(who)
            self.__player_by_roles[who.role].remove(who)



            self.send_to_all_players(ps.PacketKill, {
                'who': who.username,
                'role': who.role
            })

            Game.__logger.info('killed {}'.format(who.username))


        

    def start(self):
        self.__game_thread.start()
        Game.__logger.info('game started')

    def end(self, msg):
        self.__running = False
        self.__exit.set()

        self.send_to_all_players(ps.PacketGameEnd, {
            'msg': msg
        })
        Game.__logger.info('game ended')

    def send_to_all_players(self, packet, obj, exclude=[]):
        c.Client.send_packet_to_all(self.__players.values(), packet, obj, exclude)

    def message(self, from_client, msg):
        if self.__running and from_client in self.__players.values() and from_client.alive:
            msg = '{}: {}'.format(from_client.username, msg)
            if self.__time_event == TimeEvent.DAY:
                self.send_to_all_players(ps.PacketMessage, {
                    'msg': msg
                }, exclude=[from_client])
            elif self.__time_event == Role.LOUP and from_client.role == Role.LOUP:
                c.Client.send_packet_to_all(self.__teams[1][1], ps.PacketMessage, {
                    'msg': msg
                }, exclude=[from_client])
    
    def clairvoyant(self, client, who):
        if self.__running and client in self.__players.values() and client.alive and client.role == Role.VOYANTE and who in self.__players and self.__players[who].alive and self.__time_event == Role.VOYANTE:
            client.send_packet(ps.PacketClairVoyant, {
                'who': who,
                'role': self.__players[who].role
            })
            self.__skip_timeevent()

    def cupidon(self, client, who, kind):
        if (self.__running and client in self.__players.values() and client.alive and client.role == Role.CUPIDON
            and who in self.__players and self.__players[who].alive
            and self.__time_event == Role.CUPIDON
            and (client.lover1 is None or client.lover2 is None)
            and client.lover1 != self.__players[who]
            and client.lover2 != self.__players[who]):

            if kind == 0:
                client.lover1 = self.__players[who]
            elif kind == 1:
                client.lover2 = self.__players[who]

            if client.lover1 is not None and client.lover2 is not None:
                client.lover1.lover = client.lover2
                client.lover2.lover = client.lover1
                self.__skip_timeevent()


    def witch(self, client, who, action):
        if (self.__running and client in self.__players.values() and client.alive and client.role == Role.SORCIERE
            and who in self.__players and self.__players[who].alive
            and self.__time_event == Role.SORCIERE):

            print(action, client.has_poison, client.has_health_potion)

            if action == 0 and client.has_poison:
                client.has_poison = False
                self.__killed.append(self.__players[who])
                self.__skip_timeevent()
                
            elif action == 1 and self.__players[who] in self.__killed and client.has_health_potion:
                client.has_health_potion = False
                self.__killed.remove(self.__players[who])
                self.__skip_timeevent()

            

    def hunter(self, client, who):
        if (self.__running and client in self.__players.values() and client.alive and client.role == Role.CHASSEUR
            and who in self.__players and self.__players[who].alive
            and self.__time_event == Role.CHASSEUR):

            self.__killed.append(self.__players[who])
            self.__skip_timeevent()

    def __skip_timeevent(self):
        self.__skip = True
        self.__exit.set()

    def count_vote(self):
        if self.__running:
            voted = max(list(filter(lambda x: x.alive, self.__players.values())), key=lambda x: x.vote)
            if len(list(filter(lambda x: x.alive and x.vote == voted.vote, self.__players.values()))) == 1:
                self.__killed.append(voted)

        # empty vote count
        for i in self.__teams:
            for y in i[1]:
                y.vote = 0
                y.voted = None

    def kill_players(self):
        if self.__running:
            if self.__killed:
                for i in self.__killed:
                    self.kill_player(i)
                self.__killed = []

                self.__exit.wait(1)
                self.win_condition()
                
            else:
                self.send_to_all_players(ps.PacketMessage, {
                    'msg': 'Personne n\'est mort.'
                })

    def win_condition(self):
        # check win/lose condition
        survivors = []
        for i in self.__teams:
            if i[1]:
                survivors.append(i[0])

        if len(survivors) == 1:
            self.__lobby.end_game('Les {} ont gagnés'.format(survivors[0]))
        elif len(survivors) == 0:
            self.__lobby.end_game('Tout le monde est mort')

        self.__day += 1
        




    def __timeevent(self, event, wait):
        if self.__running and (isinstance(event, TimeEvent) or len(self.__player_by_roles[event])):

            if event == Role.SORCIERE:
                if not any(list(map(lambda w: w.has_poison or w.has_health_potion, self.__player_by_roles[Role.SORCIERE]))):
                    return
                if self.__killed:
                    c.Client.send_packet_to_all(self.__player_by_roles[Role.SORCIERE], ps.PacketMessage, {
                        'msg': 'Les joueurs {} sont menacés.'.format(','.join(list(map(lambda x: x.username, self.__killed))))
                    })
                else:
                    c.Client.send_packet_to_all(self.__player_by_roles[Role.SORCIERE], ps.PacketMessage, {
                        'msg': 'Aucun joueur n\'est menacé'
                    })
            elif event == TimeEvent.DAY:
                self.__can_vote = self.__players.values()
            elif event == Role.LOUP:
                self.__can_vote = self.__teams[1][1]
            else:
                self.__can_vote = []
            self.__time_event = event
            self.send_to_all_players(ps.PacketTimeEvent, {
                'event': event
            })

            self.__exit.wait(wait)
            if self.__skip:
                self.__exit = threading.Event()
                self.__skip = False

    def __game_routine(self):
        self.send_to_all_players(ps.PacketGameStart, lambda c:{
            'role': c.role,
            'players': self.__players.keys()
        })
        
        self.__exit.wait(1)

        # first night
        self.__timeevent(TimeEvent.NIGHT, 2)
        self.__timeevent(Role.CUPIDON, 30)
        self.__timeevent(Role.VOYANTE, 15)

        while self.__running:
            
            # day
            self.__timeevent(TimeEvent.DAY, 60)
            self.count_vote()

            self.kill_players()

            # night
            self.__timeevent(TimeEvent.NIGHT, 2)
            self.__timeevent(Role.VOYANTE, 15)
            self.__timeevent(Role.LOUP, 30)
            self.count_vote()

            self.__timeevent(Role.SORCIERE, 15)

            self.kill_players()

            