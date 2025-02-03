package org.garou.network.packet.packets;

import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.gui.GUIGame;
import org.garou.gui.GUILobby;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.PacketUtils;

public class PacketMessage extends Packet implements PacketDeserilizer, PacketSerializer{

    public PacketMessage(byte id) {
        super(id);
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        PacketUtils.putString((String) obj, buf);
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
                if (LoupGarou.getInstance().getGUI().currentPanel().equals("game")) {
                    ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).getChat().addMessage((String) obj);
                } else if (LoupGarou.getInstance().getGUI().currentPanel().equals("lobby")) {
                    ((GUILobby) LoupGarou.getInstance().getGUI().getPanel("lobby")).getChat().addMessage((String) obj);
                }
            }
            
        });
    }


    
}
