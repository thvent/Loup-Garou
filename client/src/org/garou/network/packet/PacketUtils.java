package org.garou.network.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.garou.model.DetailedLobby;
import org.garou.model.Lobby;
import org.garou.model.Role;
import org.garou.model.SimpleLobby;

public class PacketUtils {

    private static final UUID NULL_UUID = new UUID(0, 0);

    private PacketUtils() {}

    private static Charset UTF8 = Charset.forName("UTF-8");

    public static UUID getUUID(ByteBuffer buf) {
        UUID uuid = new UUID(buf.getLong(), buf.getLong());
        return uuid.equals(NULL_UUID) ? null : uuid;
    }

    public static void putUUID(UUID uuid, ByteBuffer buf) {
        if (uuid == null) {
            buf.putLong(0);
            buf.putLong(0);
        } else {
            buf.putLong(uuid.getMostSignificantBits());
            buf.putLong(uuid.getLeastSignificantBits());
        }
    }

    public static String getString(ByteBuffer buf) {
        int limit = buf.limit();
        short h = buf.getShort();
        buf.limit(buf.position() + h);
        String str = UTF8.decode(buf).toString();
        buf.limit(limit);
        return str;
    }

    public static void putString(String str, ByteBuffer buf) {
        if (str == null) {
            buf.putShort((short) 0);
        } else {
            ByteBuffer buf2 = UTF8.encode(str);
            //System.out.println(buf2.position() + " " + buf2.limit() + " " + buf2.capacity());
            buf.putShort((short) buf2.limit());
            buf.put(buf2);
        }
    }

    public static void putLobby(Lobby lobby, ByteBuffer buf) {
        if (lobby instanceof DetailedLobby) {
            DetailedLobby detailedLobby = (DetailedLobby) lobby;
            buf.put((byte) 0);
            PacketUtils.putUUID(detailedLobby.getUUID(), buf);
            PacketUtils.putString(detailedLobby.getOwner(), buf);
            buf.put((byte) detailedLobby.nbMaxPlayers());
            PacketUtils.putString(String.join(",", detailedLobby.getPlayers()), buf);
            PacketUtils.putInts(buf, detailedLobby.getOptions());
        } else if (lobby instanceof SimpleLobby) {
            SimpleLobby simpleLobby = (SimpleLobby) lobby;
            buf.put((byte) 1);
            PacketUtils.putUUID(simpleLobby.getUUID(), buf);
            PacketUtils.putString(simpleLobby.getOwner(), buf);
            buf.put((byte) simpleLobby.nbMaxPlayers());
            buf.put((byte) simpleLobby.nbPlayers());
        }
    }

    public static Lobby getLobby(ByteBuffer buf) {
        byte typeLobby = buf.get();
        if (typeLobby == 0) {
            return new DetailedLobby(
                PacketUtils.getUUID(buf),
                PacketUtils.getString(buf),
                buf.get(),
                new HashSet<>(Arrays.asList(PacketUtils.getString(buf).split(","))),
                PacketUtils.getInts(buf)
            );
        } else if (typeLobby == 1) {
            return new SimpleLobby(
                PacketUtils.getUUID(buf),
                PacketUtils.getString(buf),
                buf.get(),
                buf.get()
            );
        }
        new Exception(String.format("Lobby type unrecognize (%d)", typeLobby)).printStackTrace();
        return null;
    }

    public static List<Integer> getInts(ByteBuffer buf) {
        List<Integer> list = new ArrayList<>();
        short size = buf.getShort();
        for (short i=0; i < size; i++) {
            list.add(buf.getInt());
        }
        return list;
    }

    public static void putInts(ByteBuffer buf, List<Integer> list) {
        buf.putShort((short) list.size());
        list.forEach(i -> buf.putInt(i));
    }

    public static Role getRole(ByteBuffer buf) {
        return Role.values()[buf.get()];
    }

    public static void putRole(Role role, ByteBuffer buf) {
        buf.put((byte) role.ordinal());
    }




}
