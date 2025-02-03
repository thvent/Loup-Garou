package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUILobbyMenu;
import org.garou.model.SimpleLobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

/*
 * Get all lobbies present in server.
 */
public class PacketGetAllLobby extends Packet implements PacketDeserilizer, PacketSerializer{

    public PacketGetAllLobby(byte id) {
        super(id);
    }

    /*
     * Request all lobbies availible in server.
     */
    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        // 
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        byte length = buf.get();
        SimpleLobby[] lobbies = new SimpleLobby[length];
        for (int i=0; i < length; i++) {
            lobbies[i] = (SimpleLobby) PacketUtils.getLobby(buf);
        }
        return lobbies;
    }

    @Override
    public void handle(Object obj) {
        SimpleLobby[] lobbies = ((SimpleLobby[]) obj);
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                GUILobbyMenu guiLobbyMenu = ((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu"));
                for (SimpleLobby lobby : lobbies) {
                    guiLobbyMenu.addLobby(lobby);
                }
            }
            
        });
    }
    
}
