package org.garou;

import java.io.IOException;
import java.net.SocketException;

import org.garou.gui.GUIMain;
import org.garou.network.NetworkManager;

public class LoupGarou {

    private static LoupGarou instance;

    public static LoupGarou getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Exception {
        instance = new LoupGarou();
        instance.run();
    }

    private GUIMain gui;
    private NetworkManager nm;

    private LoupGarou() throws SocketException, IOException {
        nm = new NetworkManager();
        gui = new GUIMain();
    }

    public void run() {
        gui.switchPanel("servermenu");
        gui.setVisible(true);
    }

    public NetworkManager getNetworkManager() {
        return nm;
    }

    public GUIMain getGUI() {
        return gui;
    }

    public void close() throws IOException {
        nm.close();
    }

    public void statusBar(String s) {
        gui.setStatus(s);
    }

    public void displayException(Exception e) {
        e.printStackTrace();
        statusBar(e.getMessage());
    }
}