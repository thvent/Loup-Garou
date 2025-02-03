package org.garou.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.garou.LoupGarou;
import org.garou.model.SimpleLobby;
import org.garou.network.packet.packets.PacketGetAllLobby;
import org.garou.network.packet.packets.PacketJoinLobby;
import org.garou.network.packet.packets.PacketJoinLobby.JoinLobby;

/*
 * Lobby selection UI
 */
public class GUILobbyMenu extends JPanel{

    private JTextField usernameField;

    private JList<SimpleLobby> lobbiesView;
    private DefaultListModel <SimpleLobby> lobbiesModel;

    public GUILobbyMenu() {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel sub = new JPanel();
        sub.setOpaque(false);
        sub.setLayout(new GridBagLayout());
        sub.setBorder(new CompoundBorder(new TitledBorder("Lobby List"), new EmptyBorder(0, 0, 0, 0)));

        lobbiesModel = new DefaultListModel<>();
        lobbiesView = new JList<>(lobbiesModel);
        lobbiesView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lobbiesView.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scroll = new JScrollPane(lobbiesView);
        
        JButton join = new JButton("Join");
        JButton createLobby = new JButton("Create");
        
        JPanel disconnectPanel = new JPanel();
        disconnectPanel.setOpaque(false);
        disconnectPanel.setLayout(new GridBagLayout());
        JButton disconnect = new JButton("Disconnect");

        JPanel usernamePanel = new JPanel();
        usernamePanel.setOpaque(false);
        usernamePanel.setLayout(new GridBagLayout());
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField();
        
        disconnect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                try {
                    LoupGarou.getInstance().getNetworkManager().disconnect();
                } catch (IOException e) {
                    LoupGarou.getInstance().displayException(e);
                }
                
                LoupGarou.getInstance().getGUI().switchPanel("servermenu");
                
            }
            
        });
        
        join.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                SimpleLobby lobby = lobbiesView.getSelectedValue();

                if (lobby != null) {
                    LoupGarou.getInstance().getNetworkManager().sendPacket(
                        PacketJoinLobby.class, 
                        new JoinLobby(lobby.getUUID(), getUsername())
                    );
                } else {
                    LoupGarou.getInstance().statusBar("No lobby selected");
                }
            }
            
        });

        createLobby.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                LoupGarou.getInstance().getNetworkManager().sendPacket(
                    PacketJoinLobby.class,
                    new JoinLobby(null, getUsername())
                );
            }
            
        });

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);
        
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;

        disconnectPanel.add(disconnect, gbc);

        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        
        usernamePanel.add(usernameLabel, gbc);
        
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        usernamePanel.add(usernameField, gbc);

        
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        

        gbc.gridx = 0;
        gbc.gridy = 0;

        sub.add(scroll, gbc);

        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        sub.add(join, gbc);
        gbc.gridx = 1;
        sub.add(createLobby, gbc);
        
        add(usernamePanel, BorderLayout.PAGE_START);
        add(disconnectPanel, BorderLayout.LINE_START);
        add(sub, BorderLayout.CENTER);
    }
    
    public void addLobby(SimpleLobby lobby) {
        lobbiesModel.addElement(lobby);
    }

    public SimpleLobby getLobby(UUID uuid) {
        for (int i=0; i < lobbiesModel.size(); i++) {
            if (lobbiesModel.get(i).getUUID().equals(uuid)) {
                return lobbiesModel.get(i);
            }
        }
        return null;
    }

    public void updateList() {
        lobbiesView.setModel(lobbiesModel);
    }

    public void removeLobby(UUID uuid) {
        for (int i=0; i < lobbiesModel.size(); i++) {
            if (lobbiesModel.get(i).getUUID().equals(uuid)) {
                lobbiesModel.remove(i);
                return;
            }
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            lobbiesModel.removeAllElements();
            LoupGarou.getInstance().getNetworkManager().sendPacket(PacketGetAllLobby.class, null);
        }
        super.setVisible(visible);
    }

    public void setUsername(String username) {
        usernameField.setText(username);
    }

    public String getUsername() {
        return usernameField.getText();
    }

}