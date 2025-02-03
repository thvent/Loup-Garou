package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUIGame;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;

public class PacketTimeEvent extends Packet implements PacketDeserilizer {

    
    public PacketTimeEvent(byte id) {
        super(id);
    }

    public static final byte DAY = -1;
    public static final byte NIGHT = -2;

    @Override
    public Object deserialize(ByteBuffer buf) {
        return buf.get();
    }

    @Override
    public void handle(Object obj) {
        byte event = (byte) obj;

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).timeEvent(event);
            }
            
        });
    }

}
