package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobby;
import org.garou.gui.GUILobbyMenu;
import org.garou.model.DetailedLobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketGetLobby extends Packet implements PacketDeserilizer{

    public PacketGetLobby(byte id) {
        super(id);
    }

    public static class  GetLobby {
        public DetailedLobby lobby;
        public String username;

        public GetLobby(DetailedLobby lobby, String username) {
            this.lobby = lobby;
            this.username = username;
        }
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new GetLobby((DetailedLobby) PacketUtils.getLobby(buf), PacketUtils.getString(buf));
    }

    @Override
    public void handle(Object obj) {
        GetLobby getLobby = (GetLobby) obj;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).setUsername(getLobby.username);
                ((GUILobby) LoupGarou.getInstance().getGUI().getPanel("lobby")).setLobby(getLobby.lobby);
                LoupGarou.getInstance().getGUI().switchPanel("lobby");
            }
            
        });
    }
    
}
