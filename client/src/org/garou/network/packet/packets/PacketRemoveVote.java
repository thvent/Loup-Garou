package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUIGame;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketRemoveVote extends Packet implements PacketDeserilizer, PacketSerializer {

    public PacketRemoveVote(byte id) {
        super(id);
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        //
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
                ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).removeVote((String) obj);
            }
            
        });
    }
    
}
