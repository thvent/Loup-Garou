package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUIGame;
import org.garou.model.Role;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketClairVoyant extends Packet implements PacketDeserilizer, PacketSerializer{

    public PacketClairVoyant(byte id) {
        super(id);
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        PacketUtils.putString((String) obj, buf);
    }

    public static class Reveal {
        public String username;
        public Role role;

        public Reveal(String username, Role role) {
            this.username = username;
            this.role = role;
        }
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new Reveal(PacketUtils.getString(buf), PacketUtils.getRole(buf));
    }

    @Override
    public void handle(Object obj) {
        Reveal reveal = (Reveal) obj;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).reveal(reveal.username, reveal.role);
            }
            
        });
    }
    
}
