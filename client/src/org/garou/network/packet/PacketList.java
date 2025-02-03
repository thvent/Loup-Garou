package org.garou.network.packet;

import java.util.HashMap;
import java.util.Map;

import org.garou.network.packet.packets.PacketAddVote;
import org.garou.network.packet.packets.PacketClairVoyant;
import org.garou.network.packet.packets.PacketCupidon;
import org.garou.network.packet.packets.PacketError;
import org.garou.network.packet.packets.PacketGameEnd;
import org.garou.network.packet.packets.PacketGameStart;
import org.garou.network.packet.packets.PacketGetAllLobby;
import org.garou.network.packet.packets.PacketGetLobby;
import org.garou.network.packet.packets.PacketHunter;
import org.garou.network.packet.packets.PacketJoinLobby;
import org.garou.network.packet.packets.PacketKill;
import org.garou.network.packet.packets.PacketLeaveLobby;
import org.garou.network.packet.packets.PacketMessage;
import org.garou.network.packet.packets.PacketNewLobby;
import org.garou.network.packet.packets.PacketOwnerLobby;
import org.garou.network.packet.packets.PacketRemoveVote;
import org.garou.network.packet.packets.PacketTimeEvent;
import org.garou.network.packet.packets.PacketUpdateLobby;
import org.garou.network.packet.packets.PacketWitch;

public class PacketList {
    
    private Map<Class<? extends Packet>, Packet> PACKETS_BY_CLASS = new HashMap<>(Byte.MAX_VALUE);
    private Packet[] PACKETS_BY_ID = new Packet[Byte.MAX_VALUE];

    public Packet getPacket(Class<? extends Packet> clazz) throws UndefinedPacketException {
        Packet packet = PACKETS_BY_CLASS.get(clazz);
        if (packet == null) throw new UndefinedPacketException("Undefined packet(class: " + clazz.getSimpleName() + ")");
        return packet;
    }

    public Packet getPacket(byte id) throws UndefinedPacketException {
        Packet packet = PACKETS_BY_ID[id];
        if (packet == null) throw new UndefinedPacketException("Undefined packet(id: " + id + ")");
        return packet;
    }

    private void addPacket(Packet packet) {
        assert(PACKETS_BY_ID[packet.getId()] == null);
        PACKETS_BY_ID[packet.getId()] = packet;
        PACKETS_BY_CLASS.put(packet.getClass(), packet);
    }

    public PacketList() {
        addPacket(new PacketGameStart((byte) 1));
        addPacket(new PacketJoinLobby((byte) 2));
        addPacket(new PacketNewLobby((byte) 3));
        addPacket(new PacketGetAllLobby((byte) 4));
        addPacket(new PacketGetLobby((byte) 5));
        addPacket(new PacketLeaveLobby((byte) 6));
        addPacket(new PacketUpdateLobby((byte) 7));
        addPacket(new PacketOwnerLobby((byte) 8));
        addPacket(new PacketAddVote((byte) 9));
        addPacket(new PacketRemoveVote((byte) 10));
        addPacket(new PacketKill((byte) 11));
        addPacket(new PacketError((byte) 12));
        addPacket(new PacketTimeEvent((byte) 13));
        addPacket(new PacketMessage((byte) 14));
        addPacket(new PacketGameEnd((byte) 15));
        addPacket(new PacketClairVoyant((byte) 16));
        addPacket(new PacketCupidon((byte) 17));
        addPacket(new PacketWitch((byte) 18));
        addPacket(new PacketHunter((byte) 19));
    }

}
