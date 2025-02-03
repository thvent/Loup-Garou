package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketError extends Packet implements PacketDeserilizer {

    public PacketError(byte id) {
        super(id);
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return PacketUtils.getString(buf);
    }

    @Override
    public void handle(Object obj) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                LoupGarou.getInstance().statusBar((String) obj);
            }
            
        });
    }
    
}
