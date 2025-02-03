package org.garou.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;

import org.garou.LoupGarou;
import org.garou.gui.utils.DisabledItemSelectionModel;
import org.garou.gui.utils.JTextFieldInteger;
import org.garou.model.DetailedLobby;
import org.garou.network.packet.packets.PacketGameStart;
import org.garou.network.packet.packets.PacketLeaveLobby;


public class GUILobby extends JPanel {

    private UUID uuid;
    private String owner;
    private int maxPlayers;

    private JLabel lobbyName;
    private GUIChat chat;

    private JList<String> playerListView;
    private DefaultListModel<String> playerListModel;
    private JButton start;
    private List<JTextField> optionsList;
    private JPanel options;

    public GUILobby() {
        optionsList = new ArrayList<>();
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel menus = new JPanel();
        menus.setLayout(new BoxLayout(menus, BoxLayout.LINE_AXIS));

        options = new JPanel();
        options.setLayout(new GridLayout(5, 4));
        
        lobbyName = new JLabel();
        lobbyName.setForeground(Color.WHITE);
        lobbyName.setFont(lobbyName.getFont().deriveFont(32.0f));
        lobbyName.setBorder(new EmptyBorder(20, 20, 20, 20));
        lobbyName.setAlignmentX(Component.CENTER_ALIGNMENT);
        //lobbyName.setVerticalAlignment(SwingConstants.CENTER);
        //lobbyName.setHorizontalAlignment(SwingConstants.CENTER);

        
        playerListModel = new DefaultListModel<>();
        playerListView = new JList<>(playerListModel);
        playerListView.setSelectionModel(new DisabledItemSelectionModel());
        playerListView.setLayoutOrientation(JList.VERTICAL_WRAP);
        JScrollPane scroll = new JScrollPane(playerListView);
        
        chat = new GUIChat();
        
        JButton leave = new JButton("Leave");
        start = new JButton("Start");

        leave.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                LoupGarou.getInstance().getNetworkManager().sendPacket(PacketLeaveLobby.class,
                    ((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).getUsername()
                );
                LoupGarou.getInstance().getGUI().switchPanel("lobbymenu");
            }
            
        });

        start.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (owner.equals(((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).getUsername())) {
                    LoupGarou.getInstance().getNetworkManager().sendPacket(PacketGameStart.class, start);
                } else {
                    LoupGarou.getInstance().statusBar("You are not the owner of this lobby");
                }
            }
            
        });

        add(lobbyName);

        addOption("Temps jour (secondes):");
        addOption("Temps nuit (secondes):");
        addOption("Temps loups (secondes):");
        addOption("Temps voyante (secondes):");
        addOption("Temps sorcière (secondes):");
        addOption("Temps cupidon (secondes):");
        addOption("Temps chasseur (secondes):");
        addOption("Nombre villageois:");
        addOption("Nombre loups:");
        
        options.add(leave);
        options.add(start);

        menus.add(scroll);
        menus.add(options);

        add(menus);
        add(chat);

    }

    private void addOption(String name) {
        JTextField field = new JTextField();
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new JTextFieldInteger());
        optionsList.add(field);
        options.add(new JLabel(name));
        options.add(field);
    }
    
    public void setLobby(DetailedLobby lobby) {
        uuid = lobby.getUUID();
        owner = lobby.getOwner();
        chat.clearMessages();
        playerListModel.removeAllElements();
        maxPlayers = lobby.getMaxPlayersCount();
        playerListModel.addAll(lobby.getPlayers());
        IntStream.range(0, lobby.getOptions().size()).forEach(
            i -> optionsList.get(i).setText(String.valueOf(lobby.getOptions().get(i)))
        );
        
        setEnabled(owner.equals(((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).getUsername()));
        update();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        start.setEnabled(enabled);
        optionsList.forEach(f -> {
            f.setEditable(enabled);
            f.setEnabled(enabled);
        });
    }

    private void update() {
        lobbyName.setText(String.format("%s's lobby (%d/%d)", owner, playerListModel.size(), maxPlayers));
    }

    public void addPlayer(String username) {
        playerListModel.addElement(username);
        chat.addMessage(String.format("Le joueur %s a rejoint le lobby.", username));
        update();
    }

    public void removePlayer(String username) {
        playerListModel.removeElement(username);
        chat.addMessage(String.format("Le joueur %s a quitté le lobby.", username));
        update();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            chat.clearMessages();
            update();
        }
        super.setVisible(visible);
    }

    public GUIChat getChat() {
        return chat;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        update();
    }

    public UUID getUUID() {
        return uuid;
    }

}
