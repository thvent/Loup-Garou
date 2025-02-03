package org.garou.network.packet.packets;

import java.nio.ByteBuffer;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobby;
import org.garou.gui.GUILobbyMenu;
import org.garou.model.SimpleLobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketOwnerLobby extends Packet implements PacketDeserilizer{

    public PacketOwnerLobby(byte id) {
        super(id);
    }

    public static class OwnerLobby {
        public UUID uuid;
        public String username;

        public OwnerLobby(UUID uuid, String username) {
            this.uuid = uuid;
            this.username = username;
        }
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new OwnerLobby(PacketUtils.getUUID(buf), PacketUtils.getString(buf));
    }

    @Override
    public void handle(Object obj) {
        OwnerLobby ownerLobby = (OwnerLobby) obj;
        GUILobbyMenu lobbyMenu = (GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu");
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                SimpleLobby simpleLobby = lobbyMenu.getLobby(ownerLobby.uuid);
                simpleLobby.setOwner(ownerLobby.username);

                if (LoupGarou.getInstance().getGUI().currentPanel().equals("lobby")) {
                    GUILobby lobby = (GUILobby) LoupGarou.getInstance().getGUI().getPanel("lobby");
                    if (lobby.getUUID().equals(ownerLobby.uuid)) {
                        lobby.setOwner(ownerLobby.username);
                    }
                }
                lobbyMenu.updateList();

            }
            
        });
    }
    
}
 