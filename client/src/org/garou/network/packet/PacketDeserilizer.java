package org.garou.network.packet;

import java.nio.ByteBuffer;

/*
 * Packet that can be received by the client.
 */
public interface PacketDeserilizer {

    public Object deserialize(ByteBuffer buf);

    public void handle(Object obj);
    
}
