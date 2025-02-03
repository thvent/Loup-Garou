package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobbyMenu;
import org.garou.model.SimpleLobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketNewLobby extends Packet implements PacketDeserilizer{

    public PacketNewLobby(byte id) {
        super(id);
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return PacketUtils.getLobby(buf);
    }

    @Override
    public void handle(Object obj) {
        SimpleLobby newLobby = ((SimpleLobby) obj);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).addLobby(newLobby);
            }
            
        });
    }
    
}
