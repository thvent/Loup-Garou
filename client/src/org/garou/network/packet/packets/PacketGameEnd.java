package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketGameEnd extends Packet implements PacketDeserilizer {

    public PacketGameEnd(byte id) {
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
                LoupGarou.getInstance().getGUI().switchPanel("lobby");
                ((GUILobby) LoupGarou.getInstance().getGUI().getPanel("lobby")).getChat().addMessage((String) obj);
            }
            
        });
    }
    
}
