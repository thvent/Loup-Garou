package org.garou.network.packet;

import java.nio.ByteBuffer;

/*
 * Packet that can be sended by the client.
 */
public interface PacketSerializer {
    
    public void serialize(ByteBuffer buf, Object obj);

}
