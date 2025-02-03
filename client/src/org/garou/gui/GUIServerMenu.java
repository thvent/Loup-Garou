package org.garou.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.garou.LoupGarou;

/*
 * Server selection UI
 */
public class GUIServerMenu extends JPanel {

    private DefaultListModel<InetAddress> serversModel;
    private JList<InetAddress> serversView;

    public GUIServerMenu() {

        setLayout(new GridLayout(1, 3));
        setOpaque(false);

        JPanel serverList = new JPanel();
        serverList.setOpaque(false);
        serverList.setLayout(new GridBagLayout());

        //serverList.setBorder(BorderFactory.create());

        serversModel = new DefaultListModel<>();
        serversView = new JList<>(serversModel);
        serversView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serversView.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scroll = new JScrollPane(serversView);
        
        JButton refresh = new JButton("Refresh"); 
        JButton quit = new JButton("Quit"); 
        JButton connect = new JButton("Connect");

        
        refresh.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                refreshServer();
            }
            
        });
        
        
        JPanel moi = this;
        quit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Window parent = SwingUtilities.getWindowAncestor(moi);
                parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
            }
            
        });
        
        connect.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                InetAddress addr = serversView.getSelectedValue();
                if (addr == null) {
                    LoupGarou.getInstance().statusBar("No server selected");
                }
                else {
                    
                    try {
                        LoupGarou.getInstance().getNetworkManager().connect(addr);
                        LoupGarou.getInstance().statusBar("Connected to " + addr);
                        LoupGarou.getInstance().getGUI().switchPanel("lobbymenu");
                    }
                    catch (Exception e) {
                        LoupGarou.getInstance().displayException(e);
                    }
                    
                }
            }
            
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        serverList.add(scroll, gbc);

        gbc.weighty = 0.0;
        gbc.gridy = 1;
        serverList.add(refresh, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        serverList.add(quit, gbc);
        gbc.gridx = 1;
        serverList.add(connect, gbc);
        

        add(serverList, BorderLayout.LINE_END);
    }
    
    public void refreshServer() {
        try {
            serversModel.removeAllElements();
            LoupGarou.getInstance().getNetworkManager().serverList((server) -> {
                serversModel.addElement(server);
            });
        } catch (IOException e) {
            LoupGarou.getInstance().displayException(e);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            refreshServer();
        }
        super.setVisible(visible);
    }
    
}
