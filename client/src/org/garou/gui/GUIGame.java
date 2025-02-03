package org.garou.gui;


import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.garou.LoupGarou;
import org.garou.model.Role;
import org.garou.network.packet.packets.PacketAddVote;
import org.garou.network.packet.packets.PacketClairVoyant;
import org.garou.network.packet.packets.PacketCupidon;
import org.garou.network.packet.packets.PacketCupidon.Cupidon;
import org.garou.network.packet.packets.PacketHunter;
import org.garou.network.packet.packets.PacketRemoveVote;
import org.garou.network.packet.packets.PacketTimeEvent;
import org.garou.network.packet.packets.PacketWitch;
import org.garou.network.packet.packets.PacketWitch.Witch;

/*
 * game UI.
 */
public class GUIGame extends JPanel {

    
    
    private GUIChat chat; 
    private JPanel playersPanel;
    private GUIPlayer myself;
    private Map<String, GUIPlayer> players;

    private Image background, dayBackground, nightBackground;

    private GUIPlayer voted;
    private byte timeEvent;


    public GUIGame() {
        setOpaque(false);
        setLayout(new BorderLayout());

        playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(3, 6));
        playersPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        playersPanel.setOpaque(false);

        chat = new GUIChat();
        players = new HashMap<>();

        try {
            dayBackground = GUIResource.getImage("day.png");
            nightBackground = GUIResource.getImage("night.png");
        } catch (IOException e) {
            nightBackground = dayBackground = null;
        }

        add(playersPanel, BorderLayout.CENTER);
        add(chat, BorderLayout.PAGE_END);
    }

    private void clearVote() {
        voted = null;
        players.values().forEach(p -> p.clearVote());
    }

    public void setGame(Role role, String[] players) {
        String myUsername = ((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).getUsername();
        GUIPlayer player;

        if (myself != null) {
            playersPanel.removeAll();
            remove(myself);
        }

        // fill GUI with players
        for (String s : players) {
            if (s.equals(myUsername)) {
                player = new GUIPlayer(myUsername, role);
                myself = player;
                add(myself, BorderLayout.LINE_START);
            } else {
                player = new GUIPlayer(s);
                playersPanel.add(player);
            }
            this.players.put(s, player);
        }

        // add events for each of them
        this.players.values().forEach(e -> e.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent arg0) {
                GUIPlayer player = (GUIPlayer) arg0.getSource();

                if (myself.isAlive() && player.isAlive()) {

                    if (timeEvent == PacketTimeEvent.DAY || (timeEvent == Role.LOUP.ordinal() && myself.getRole() == Role.LOUP)) {

                        if (voted == null) {
                            voted = player;
                            voted.addVote();
                            LoupGarou.getInstance().getNetworkManager().sendPacket(PacketAddVote.class, voted.getUsername());
                            chat.addMessage(String.format("Vous avez voté pour %s.", voted.getUsername()));
                        } else if (voted == arg0.getSource()) {
                            voted.removeVote();
                            LoupGarou.getInstance().getNetworkManager().sendPacket(PacketRemoveVote.class, voted.getUsername());
                            chat.addMessage(String.format("Vous avez retiré votre vote pour %s.", voted.getUsername()));
                            voted = null;
                        } else {
                            voted.removeVote();
                            voted = player;
                            voted.addVote();
                            LoupGarou.getInstance().getNetworkManager().sendPacket(PacketAddVote.class, voted.getUsername());
                            chat.addMessage(String.format("Vous avez voté pour %s.", voted.getUsername()));
                        }

                    } else if (timeEvent == Role.VOYANTE.ordinal() && myself.getRole() == Role.VOYANTE && player.getRole() == Role.INCONNU) {

                        LoupGarou.getInstance().getNetworkManager().sendPacket(PacketClairVoyant.class, player.getUsername());

                    } else if (timeEvent == Role.CUPIDON.ordinal() && myself.getRole() == Role.CUPIDON) {
                        
                        switch (arg0.getButton()) {
                            case MouseEvent.BUTTON1: // left click
                                LoupGarou.getInstance().getNetworkManager().sendPacket(PacketCupidon.class, new Cupidon(
                                    (byte) 0, player.getUsername()
                                ));
                                chat.addMessage(String.format("Vous avez choisi %s comme premier joueur.", player.getUsername()));
                            break;
                            case MouseEvent.BUTTON3: // right click
                            LoupGarou.getInstance().getNetworkManager().sendPacket(PacketCupidon.class, new Cupidon(
                                    (byte) 1, player.getUsername()
                                ));
                                chat.addMessage(String.format("Vous avez choisi %s comme deuxième joueur.", player.getUsername()));
                            break;
                        }

                    } else if (timeEvent == Role.SORCIERE.ordinal() && myself.getRole() == Role.SORCIERE) {

                        switch (arg0.getButton()) {
                            case MouseEvent.BUTTON1: // left click
                                LoupGarou.getInstance().getNetworkManager().sendPacket(PacketWitch.class, new Witch(
                                    player.getUsername(), (byte) 0 
                                ));
                            break;
                            case MouseEvent.BUTTON3: // right click
                            LoupGarou.getInstance().getNetworkManager().sendPacket(PacketWitch.class, new Witch(
                                    player.getUsername(), (byte) 1
                                ));
                            break;
                        }

                    } else if (timeEvent == Role.CHASSEUR.ordinal()  && myself.getRole() == Role.CHASSEUR) {
                        
                        LoupGarou.getInstance().getNetworkManager().sendPacket(PacketHunter.class, player.getUsername());

                    }

                }
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {}

            @Override
            public void mouseExited(MouseEvent arg0) {}

            @Override
            public void mousePressed(MouseEvent arg0) {}

            @Override
            public void mouseReleased(MouseEvent arg0) {}
            
        }));

    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            chat.clearMessages();
            if (myself.getRole() == Role.VOYANTE) {
                chat.addMessage("Vous êtes voyante!");
                chat.addMessage("Cliquez sur un joueur pour découvrir son rôle (à la fin de votre tour).");
            }
            else if (myself.getRole() == Role.SORCIERE) {
                chat.addMessage("Vous êtes sorcière!");
                chat.addMessage("Faites un clic gauche sur un joueur pour le tuer.");
                chat.addMessage("Faites un clic droit sur le joueur tué par les loups pour le sauver.");
            }
            else if (myself.getRole() == Role.CUPIDON) {
                chat.addMessage("Vous êtes cupidon!");
                chat.addMessage("Faites un clic gauche sur un premier joueur");
                chat.addMessage("puis faites un clic droit sur un second pour les faire tomber amoureux.");
            } else if (myself.getRole() == Role.CHASSEUR) {
                chat.addMessage("Vous êtes chasseur!");
                chat.addMessage("Si vous êtes tué, cliquez sur un joueur pour le tuer également.");
            }
        }
        super.setVisible(visible);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }

    public void addVote(String who) {
        players.get(who).addVote();
    }

    public void removeVote(String who) {
        players.get(who).removeVote();
    }

    public void reveal(String who, Role role) {
        players.get(who).reveal(role);
        chat.addMessage(String.format("%s à le role %s.", who, role.getFullName()));
    }

    public void kill(String username, Role role) {
        if (username.equals(myself.getUsername())) {
            myself.kill();
            chat.setEnabled(false);
            chat.addMessage(String.format("Vous êtes mort.", username));
        } else {
            players.get(username).kill(role);
            chat.addMessage(String.format("%s est mort.", username));
        }
    }

    public void setEnabledChat(boolean enabled) {
        if (myself.isAlive()) {
            chat.setEnabled(enabled);
        }
    }

    public void timeEvent(byte event) {
        clearVote();
        timeEvent = event;

        if (PacketTimeEvent.DAY == event) {
            setEnabledChat(true);
            background = dayBackground;
            chat.addMessage("Le jour se lève.");
            repaint();
        }
        else if (event == PacketTimeEvent.NIGHT) {
            setEnabledChat(false);
            background = nightBackground;
            chat.addMessage("La nuit tombe.");
            repaint();
        }
        else if (event == (byte) Role.LOUP.ordinal()) {
            setEnabledChat(myself.getRole() == Role.LOUP);
            chat.addMessage("Le tour des loups.");
        }
        else if (event == (byte) Role.VOYANTE.ordinal()) {
            setEnabledChat(false);
            chat.addMessage("Le tour de la voyante.");
        }
        else if (event == (byte) Role.SORCIERE.ordinal()) {
            setEnabledChat(false);
            chat.addMessage("Le tour de la sorcière.");
        }
        else if (event == (byte) Role.CUPIDON.ordinal()) {
            setEnabledChat(false);
            chat.addMessage("Le tour de cupidon.");
        }
        else if (event == (byte) Role.CHASSEUR.ordinal()) {
            setEnabledChat(false);
            chat.addMessage("Le tour du chasseur.");
        }

    }

    public GUIChat getChat() {
        return chat;
    }
}
