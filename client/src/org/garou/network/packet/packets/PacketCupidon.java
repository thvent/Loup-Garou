package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketCupidon extends Packet implements PacketSerializer {

    public PacketCupidon(byte id) {
        super(id);
    }

    public static class Cupidon {
        public byte type;
        public String who;

        public Cupidon(byte type, String who) {
            this.type = type;
            this.who = who;
        }
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        Cupidon c = (Cupidon) obj;
        buf.put(c.type);
        PacketUtils.putString(c.who, buf);
    }
    
}
