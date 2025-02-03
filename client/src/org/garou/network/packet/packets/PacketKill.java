package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUIGame;
import org.garou.model.Role;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketUtils;

public class PacketKill extends Packet implements PacketDeserilizer{

    public PacketKill(byte id) {
        super(id);
    }

    public static class Kill {

        public String username;
        public Role role;

        public Kill(String username, Role role) {
            this.username = username;
            this.role = role;
        }
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new Kill(PacketUtils.getString(buf), PacketUtils.getRole(buf));
    }

    @Override
    public void handle(Object obj) {
        Kill kill = (Kill) obj;

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).kill(kill.username, kill.role);
            }
            
        });
    }
    
}
