package org.garou.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.garou.LoupGarou;

/*
 * Root frame
 */
public class GUIMain extends JFrame {

    private InnerPanel mainPanel;
    private JPanel statusBar;
    private JLabel statusLabel;

    private Map<String, Container> windows;
    private String currentPanel;

    public GUIMain() {

        super("Loup Garou");
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 500));

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e1) {
        }

        addWindowListener(new WindowListener() {

            @Override
            public void windowActivated(WindowEvent arg0) {}

            @Override
            public void windowClosed(WindowEvent arg0) {
                try {
                    LoupGarou.getInstance().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void windowClosing(WindowEvent arg0) {}

            @Override
            public void windowDeactivated(WindowEvent arg0) {}

            @Override
            public void windowDeiconified(WindowEvent arg0) {}

            @Override
            public void windowIconified(WindowEvent arg0) {}

            @Override
            public void windowOpened(WindowEvent arg0) {}

        });

        
        statusBar = new JPanel();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(getWidth(), 16));
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusBar.add(statusLabel);
        
        windows = new HashMap<>();
        mainPanel = new InnerPanel();
        
        addPanel("servermenu", new GUIServerMenu());
        addPanel("lobby", new GUILobby());
        addPanel("game", new GUIGame());
        addPanel("lobbymenu", new GUILobbyMenu());

        add(statusBar, BorderLayout.SOUTH);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private class InnerPanel extends JPanel {

        private Image background;
        private CardLayout cardLayout;

        public InnerPanel() {
            super();
            cardLayout = new CardLayout();
            setLayout(cardLayout);

            try {
                background = GUIResource.getImage("background.jpg");
            } catch (IOException e) {
                background = null;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }

        public void switchPanel(String pane) {
            cardLayout.show(this, pane);
        }

        public void addPanel(String name, Container pane) {
            add(name, pane);
        }
    
    }
    
    private void addPanel(String name, Container pane) {
        windows.put(name, pane);
        mainPanel.addPanel(name, pane);
    }

    
    public Container getPanel(String name) {
        return windows.get(name);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void switchPanel(String pane) {
        mainPanel.switchPanel(pane);
        currentPanel = pane;
    }

    public String currentPanel() {
        return currentPanel;
    }

}