package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUIGame;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketAddVote extends Packet implements PacketSerializer, PacketDeserilizer {

    public PacketAddVote(byte id) {
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
                ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).addVote((String) obj);
            }
            
        });
        
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        PacketUtils.putString((String) obj, buf);
    }
    
}
