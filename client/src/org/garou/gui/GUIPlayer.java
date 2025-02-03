package org.garou.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.garou.model.Role;

public class GUIPlayer extends JPanel {

    private JLabel icon;
    private Image image;

    private JLabel label;
    private String constantLabel;

    private final String username;
    private Role role;
    private int vote;
    private boolean alive = true;

    public GUIPlayer(String username) {
        this(username, Role.INCONNU);
    }

    public GUIPlayer(String username, Role role) {
        this.username = username;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //setMinimumSize(getSize());

        icon = new JLabel();
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                icon.setIcon(new ImageIcon(scaledImage()));
            }
        });

        
        constantLabel = username;
        label = new JLabel(username);
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(16.0f));
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        reveal(role);

        add(icon);
        add(label);
    }

    public void addVote() {
        vote++;
        setLabel("vote: " + vote);  
    }

    public void removeVote() {
        vote--;
        if (vote > 0) {
            setLabel("vote: " + vote);
        } else {
            clearLabel();
        }
    }

    public void clearVote() {
        vote = 0;
        clearLabel();
    }

    public void setLabel(String str) {
        label.setText(constantLabel + " " + str);
    }

    public void clearLabel() {
        label.setText(constantLabel);
    }

    public void setConstantLabel(String str) {
        constantLabel += " " + str;
        clearLabel();
    }

    public Image scaledImage() {
        int width = this.getWidth();
        int height = this.getHeight() - label.getHeight();

        int size = height > width ? width : height;

        if (size <= 0) {
            size = 1;
        }

        return image.getScaledInstance(size, size,  Image.SCALE_FAST);
    }

    public void reveal(Role role) {
        this.role = role;
        try {
            image = GUIResource.getRolePicture(role);
            icon.setIcon(new ImageIcon(scaledImage()));
        } catch (IOException e) {
            image = null;
            System.err.println("can't find " + role.getName() + " resource, falling back to text");
            setConstantLabel("role: " + role.getFullName());
        }
    }

    public Role getRole() {
        return role;
    }

    public void kill(Role role) {
        reveal(role);
        kill();
    }

    public void kill() {
        alive = false;
        if (image == null) {
            setConstantLabel("state: dead");
        } else {
            image = GrayFilter.createDisabledImage(image);
            icon.setIcon(new ImageIcon(scaledImage()));
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean isAlive() {
        return alive;
    }

}
