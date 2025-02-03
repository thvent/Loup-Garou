package org.garou.network.packet.packets;

import java.nio.ByteBuffer;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobbyMenu;
import org.garou.model.SimpleLobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketUpdateLobby extends Packet implements PacketDeserilizer{

    public PacketUpdateLobby(byte id) {
        super(id);
    }

    public static class LobbyUpdate {
        public UUID uuid;
        public byte nbPlayers;

        public LobbyUpdate(UUID uuid, byte nbPlayers) {
            this.uuid = uuid;
            this.nbPlayers = nbPlayers;
        }

    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new LobbyUpdate(PacketUtils.getUUID(buf), buf.get());
    }

    @Override
    public void handle(Object obj) {
        LobbyUpdate lobbyUpdate = (LobbyUpdate) obj;
        GUILobbyMenu lobbyMenu = (GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (lobbyUpdate.nbPlayers <= 0) {
                    lobbyMenu.removeLobby(lobbyUpdate.uuid);
                } else {
                    SimpleLobby simpleLobby = lobbyMenu.getLobby(lobbyUpdate.uuid);
                    simpleLobby.setNbPlayers(lobbyUpdate.nbPlayers);
                    lobbyMenu.updateList();
                }
            }
            
        });
    }
    
    

}
