package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketWitch extends Packet implements PacketSerializer  {

    public PacketWitch(byte id) {
        super(id);
    }

    public static class Witch {
        public byte action;
        public String who;

        public Witch(String who, byte kill) {
            this.who = who;
            this.action = kill;
        }
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        Witch w = (Witch) obj;
        PacketUtils.putString(w.who, buf);
        buf.put(w.action);
    }
    
}
