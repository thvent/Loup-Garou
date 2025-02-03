import garou
import garou.game.lobby as l
import garou.network.client as c
import garou.network.packet as p


class PacketList:

    def __init__(self):
        self.__packets_map = {}
        self.__packets_id = [None] * 128

        self.__add_packet(PacketGameStart(1))
        self.__add_packet(PacketJoinLobby(2))
        self.__add_packet(PacketNewLobby(3))
        self.__add_packet(PacketGetAllLobby(4))
        self.__add_packet(PacketGetLobby(5))
        self.__add_packet(PacketLeaveLobby(6))
        self.__add_packet(PacketUpdateLobby(7))
        self.__add_packet(PacketOwnerLobby(8))
        self.__add_packet(PacketAddVote(9))
        self.__add_packet(PacketRemoveVote(10))
        self.__add_packet(PacketKill(11))
        self.__add_packet(PacketError(12))
        self.__add_packet(PacketTimeEvent(13))
        self.__add_packet(PacketMessage(14))
        self.__add_packet(PacketGameEnd(15))
        self.__add_packet(PacketClairVoyant(16))
        self.__add_packet(PacketCupidon(17))
        self.__add_packet(PacketWitch(18))
        self.__add_packet(PacketHunter(19))

    def get_packet_by_id(self, id):
        packet = self.__packets_id[id]
        if packet is None:
            raise p.UndefinedPacketException("Undefined packet(id: {})".format(id))
        return packet

    def get_packet_by_type(self, type):
        packet = self.__packets_map.get(type)
        if packet is None:
            raise p.UndefinedPacketException("Undefined packet(class: {})".format(type.__name__))
        return packet
        
    def __add_packet(self, packet):
        assert self.__packets_id[packet.id] is None
        self.__packets_id[packet.id] = packet
        self.__packets_map[type(packet)] = packet

class PacketNewLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_simple_lobby(obj['lobby'])

class PacketJoinLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_uuid(obj['uuid'])
        buf.put_str(obj['username'])

    def deserialize(self, buf):
        return {'uuid': buf.get_uuid(), 'username': buf.get_str()}

    def handle(self, client, obj):
        client.username = obj['username']
        lobby = garou.instance.get_lobby(obj['uuid'])

        if lobby is None:
            # client create new lobby
            lobby = l.Lobby(client)
            garou.instance.add_lobby(lobby)

            client.send_packet(PacketGetLobby, {
                'username': client.username,
                'lobby': lobby
            })
        else:
            client.join_lobby(lobby)

            client.send_packet(PacketGetLobby, {
                'username': client.username,
                'lobby': lobby
            })


class PacketGameStart(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_byte(obj['role'])
        buf.put_str(','.join(obj['players']))

    def deserialize(self, buf):
        pass

    def handle(self, client, obj):
        if client.in_lobby():
            client.get_lobby().start_game(client)
        
class PacketGetAllLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        lobbies = obj['lobbies']
        buf.put_byte(len(lobbies))
        for lobby in lobbies:
            buf.put_simple_lobby(lobby)

    def deserialize(self, buf):
        pass

    def handle(self, client, obj):
        client.send_packet(PacketGetAllLobby, {
            'lobbies': garou.instance.lobbies,
        })

class PacketGetLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_detailed_lobby(obj['lobby'])
        buf.put_str(obj['username'])


class PacketLeaveLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['username'])

    def deserialize(self, buf):
        pass

    def handle(self, client, obj):
        if client.in_lobby():
            client.leave_lobby()

class PacketUpdateLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_uuid(obj['uuid'])
        buf.put_byte(obj['nbPlayers'])

class PacketOwnerLobby(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_uuid(obj['uuid'])
        buf.put_str(obj['username'])

class PacketAddVote(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['who'])

    def deserialize(self, buf):
        return {'who': buf.get_str()}

    def handle(self, client, obj):
        if client.in_lobby() and client.get_lobby().in_game():
            client.get_lobby().get_game().add_vote_player(client, obj['who'])

class PacketRemoveVote(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['who'])

    def deserialize(self, buf):
        pass

    def handle(self, client, obj):
        if client.in_lobby() and client.get_lobby().in_game():
            client.get_lobby().get_game().remove_vote_player(client)

class PacketTimeEvent(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_byte(obj['event'])

class PacketKill(p.Packet):
    
    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['who'])
        buf.put_byte(obj['role'])


class PacketError(p.Packet):
    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['error'])

class PacketMessage(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['msg'])

    def deserialize(self, buf):
        return {'msg': buf.get_str()}

    def handle(self, client, obj):
        if client.in_lobby():
            if client.get_lobby().in_game():
                client.get_lobby().get_game().message(client, obj['msg'])
            else:
                client.get_lobby().message(client, obj['msg'])

class PacketGameEnd(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['msg'])

class PacketClairVoyant(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def serialize(self, buf, obj):
        buf.put_str(obj['who'])
        buf.put_byte(obj['role'])

    def deserialize(self, buf):
        return {'who': buf.get_str()}

    def handle(self, client, obj):
        if client.in_lobby() and client.get_lobby().in_game():
            client.get_lobby().get_game().clairvoyant(client, obj['who'])

class PacketCupidon(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def deserialize(self, buf):
        return {
            'kind': buf.get_byte(),
            'who': buf.get_str()
        }

    def handle(self, client, obj):
        if client.in_lobby() and client.get_lobby().in_game():
            client.get_lobby().get_game().cupidon(client, obj['who'], obj['kind'])


class PacketWitch(p.Packet):

    def __init__(self, id):
        super().__init__(id)


    def deserialize(self, buf):
        return {
            'who': buf.get_str(),
            'action': buf.get_byte()
        }

    def handle(self, client, obj):
        if client.in_lobby() and client.get_lobby().in_game():
            client.get_lobby().get_game().witch(client, obj['who'], obj['action'])


class PacketHunter(p.Packet):

    def __init__(self, id):
        super().__init__(id)

    def deserialize(self, buf):
        return {'who': buf.get_str()}

    def handle(self, client, obj):
        if client.in_lobby() and client.get_lobby().in_game():
            client.get_lobby().get_game().hunter(client, obj['who'])