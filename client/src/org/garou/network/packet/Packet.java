package org.garou.network.packet;

/*
 * Packet base class that every packets extends from.
 */

public abstract class Packet {

    private byte id;

    public Packet(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

}