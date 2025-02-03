package org.garou.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.garou.LoupGarou;
import org.garou.gui.utils.DisabledItemSelectionModel;
import org.garou.gui.utils.JTextFieldLimit;
import org.garou.network.packet.packets.PacketMessage;

public class GUIChat extends JPanel {

    // output
    private JList<String> list;
    private DefaultListModel<String> model;

    // input
    private JTextField input;
    private JButton sendButton;

    public GUIChat() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        model = new DefaultListModel<>();
        model.setSize(100);
        list = new JList<String>(model);
        list.setSelectionModel(new DisabledItemSelectionModel());
        list.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scroll = new JScrollPane(list);

        scroll.setMaximumSize(new Dimension(300, 300));;
        
        input = new JTextField();
        input.setDocument(new JTextFieldLimit(200));
        sendButton = new JButton("Send");
        
        ActionListener sendMessageAction = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!input.getText().trim().isEmpty()) {
                    addMessage(String.format("%s: %s", ((GUILobbyMenu) LoupGarou.getInstance().getGUI().getPanel("lobbymenu")).getUsername(), input.getText()));
                    sendMessage(input.getText());
                }
                input.setText("");
            }
            
        };
        
        input.addActionListener(sendMessageAction);
        sendButton.addActionListener(sendMessageAction);
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridwidth = 6;
        gbc.gridheight = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(scroll, gbc);

        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 5;

        add(input, gbc);

        gbc.gridx = 5;
        gbc.gridwidth = 1;
        add(sendButton, gbc);
    }

    public void addMessage(String msg) {
        msg = String.format("<html><b>%s</b></html>", msg);
        model.addElement(msg);
        int index = model.getSize() - 1;
        if (index >= 0) {
            list.ensureIndexIsVisible(index);
        }
    }

    public void clearMessages() {
        model.removeAllElements();
    }

    public void sendMessage(String msg) {
        LoupGarou.getInstance().getNetworkManager().sendPacket(PacketMessage.class, msg);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEditable(enabled);
        input.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }

}
