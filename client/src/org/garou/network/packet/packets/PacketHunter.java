package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketHunter extends Packet implements PacketSerializer {

    public PacketHunter(byte id) {
        super(id);
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        PacketUtils.putString((String) obj, buf);
    }
    
}
