package org.garou.network;


import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import org.garou.LoupGarou;
import org.garou.network.packet.Packet;
import org.garou.network.packet.PacketDeserilizer;
import org.garou.network.packet.PacketList;
import org.garou.network.packet.PacketSerializer;
import org.garou.network.packet.UnsupportedDeserializationException;
import org.garou.network.packet.UnsupportedSerializationException;

public class NetworkManager implements Closeable{

    public static final int PACKET_MAX_SIZE = 4096;

    public static final int DEFAULT_TCP_PORT = 1501;
    public static final int DEFAULT_UDP_PORT = 1500;

    private Socket tcpSocket;
    private DatagramSocket udpSocket;

    private static final byte[] UDP_BYTES = {12, 4, 94};

    private ReadableByteChannel tcpInput;
    private WritableByteChannel tcpOutput;
    private ByteBuffer bufIn, bufOut;

    private PacketList packets;

    private Thread thread;

    public NetworkManager() throws SocketException, IOException {
        udpSocket = new DatagramSocket();
        udpSocket.setBroadcast(true);
        udpSocket.setSoTimeout(50);
        udpSocket.setReuseAddress(true);
        bufIn = ByteBuffer.allocate(PACKET_MAX_SIZE);
        bufOut = ByteBuffer.allocate(PACKET_MAX_SIZE);
        packets = new PacketList();
    }

    public void sendPacket(Class<? extends Packet> packetClass, Object obj) {
        try {
            Packet packet = packets.getPacket(packetClass);
            if (packet instanceof PacketSerializer) {
                bufOut.put(packet.getId()); // put packet header (1 byte id)
                ((PacketSerializer) packet).serialize(bufOut, obj);
                bufOut.flip();
                tcpOutput.write(bufOut);
                bufOut.clear();
            } else {
                throw new UnsupportedSerializationException("Packet " + packet.getClass().getSimpleName() + " doesn't support serilization");
            }
        }
        catch (Exception e) {
            bufOut.clear();
            LoupGarou.getInstance().displayException(e);
            LoupGarou.getInstance().getGUI().switchPanel("servermenu");
        }
    }

    private void handlePacket(ByteBuffer buf) {
        try {
            Packet packet = packets.getPacket(buf.get());
            if (packet instanceof PacketDeserilizer) {
                PacketDeserilizer des = ((PacketDeserilizer) packet);
                des.handle(des.deserialize(buf));
            } else {
                throw new UnsupportedDeserializationException("Packet " + packet.getClass().getSimpleName() + " doesn't support deserilization");
            }
        }
        catch (Exception e) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    LoupGarou.getInstance().displayException(e);
                    LoupGarou.getInstance().getGUI().switchPanel("servermenu");
                }
                
            });
        }
    }

    public void connect(InetAddress addr) throws UnknownHostException, IOException {
        tcpSocket = new Socket();
        tcpSocket.setReuseAddress(true);
        tcpSocket.connect(new InetSocketAddress(addr, DEFAULT_TCP_PORT));

        tcpInput = Channels.newChannel(tcpSocket.getInputStream());
        tcpOutput = Channels.newChannel(tcpSocket.getOutputStream());

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                
                try {

                    for (;;) {
                        if (tcpInput.read(bufIn) == -1) {
                            bufIn.clear();
                            SwingUtilities.invokeLater(() -> {
                                LoupGarou.getInstance().statusBar("Server disconnected");
                                LoupGarou.getInstance().getGUI().switchPanel("servermenu");
                            });
                            return;
                        }
                        bufIn.flip();
                        handlePacket(bufIn);
                        bufIn.clear();
                    }
                
                } catch (Exception e) {
                    bufIn.clear();
                    SwingUtilities.invokeLater(() -> {
                        LoupGarou.getInstance().displayException(e);
                        LoupGarou.getInstance().getGUI().switchPanel("servermenu");
                    });
                }
                
            }
            
        });

        thread.start();
    }

    public void serverList(Consumer<InetAddress> cons) throws IOException {
        DatagramPacket datagram = new DatagramPacket(UDP_BYTES, UDP_BYTES.length, InetAddress.getByName("255.255.255.255"), DEFAULT_UDP_PORT);
        udpSocket.send(datagram);
        byte[] udp_bytes = new byte[UDP_BYTES.length];

        for (;;) {
            try {

                datagram = new DatagramPacket(udp_bytes, udp_bytes.length);
                udpSocket.receive(datagram);
                if (Arrays.equals(udp_bytes, UDP_BYTES)) {
                    cons.accept(datagram.getAddress());
                }
            } catch (SocketTimeoutException e) {
                break; // no response
            } catch (IOException e) {
                LoupGarou.getInstance().displayException(e);
            }
        }

    }

    public void disconnect() throws IOException {
        if (tcpSocket != null) {
            thread.interrupt();
            tcpSocket.close();
            tcpSocket = null;
        }
    }

    public void close() throws IOException {
        disconnect();
        udpSocket.close();
    }

    public static void printBuffer(ByteBuffer buf) {
        byte[] arr = buf.array();
        System.out.print("[ ");
        for (int i=0; i < arr.length; i++) {
            System.out.printf("0x%02X ", arr[i]);
        }
        System.out.println("]");
    }

}