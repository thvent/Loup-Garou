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

/*
 * Start game
 */
public class PacketGameStart extends Packet implements PacketSerializer, PacketDeserilizer{

    public PacketGameStart(byte id) {
        super(id);
    }

    public static class GameStart {
        public Role role;
        public String[] players;

        public GameStart(Role role, String[] players) {
            this.role = role;
            this.players = players;
        }
    }

    @Override
    public Object deserialize(ByteBuffer buf) {
        return new GameStart(PacketUtils.getRole(buf), PacketUtils.getString(buf).split(","));
    }

    @Override
    public void handle(Object obj) {
        GameStart gameStart = (GameStart) obj;
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                //Game game = new Game(gameStart.role, gameStart.players);
                ((GUIGame) LoupGarou.getInstance().getGUI().getPanel("game")).setGame(gameStart.role, gameStart.players);
                LoupGarou.getInstance().getGUI().switchPanel("game");
            }
            
        });
    }

    @Override
    public void serialize(ByteBuffer buf, Object obj) {
        //
    }

    
}
