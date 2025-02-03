package org.garou.network.packet.packets;

import java.nio.ByteBuffer;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobby;
import org.garou.gui.GUIMain;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

/*
 * A player join a lobby.
 */
public class PacketJoinLobby extends Packet implements PacketSerializer, PacketDeserilizer{

    public PacketJoinLobby(byte id) {
        super(id);
    }

    public static class JoinLobby {
        public UUID uuid;
        public String username;

        public JoinLobby(UUID uuid, String username) {
            this.uuid = uuid;
            this.username = username;
        }

        public String toString() {
            return String.format("Player %s join lobby %s", username, uuid == null ? "null uuid" : uuid.toString());
        }
    }

    /*
     * Client join a lobby.
     * uuid point to lobby on server.
     * if null, server create a new lobby.
     */
    public void serialize(ByteBuffer buf, Object obj) {
        JoinLobby joinLobby = ((JoinLobby) obj);
        PacketUtils.putUUID(joinLobby.uuid, buf);
        PacketUtils.putString(joinLobby.username, buf);
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new JoinLobby(
            PacketUtils.getUUID(buf),
            PacketUtils.getString(buf)
        );
    }

    @Override
    public void handle(Object obj) {
        JoinLobby joinLobby = ((JoinLobby) obj);
        GUIMain guiMain = LoupGarou.getInstance().getGUI();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {         
                ((GUILobby) guiMain.getPanel("lobby")).addPlayer(joinLobby.username);
            }
            
        });
    }
    
}
