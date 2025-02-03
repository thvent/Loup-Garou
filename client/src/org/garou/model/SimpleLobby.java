package org.garou.model;

import java.util.UUID;

public class SimpleLobby implements Lobby{

    private final UUID uuid;
    private String owner;
    private int playersCount, maxPlayersCount;

    public SimpleLobby(UUID uuid, String owner, int maxPlayersCount, int playersCount) {
        this.uuid = uuid;
        this.owner = owner;
        this.playersCount = playersCount;
        this.maxPlayersCount = maxPlayersCount;
    }

    @Override
    public int nbPlayers() {
        return playersCount;
    }

    public void setNbPlayers(int playersCount) {
        this.playersCount = playersCount;
    }

    @Override
    public int nbMaxPlayers() {
        return maxPlayersCount;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setOwner(String username) {
        this.owner = username;
    }

    public String toString() {
        return String.format("%s's lobby (%d/%d)", owner, playersCount, maxPlayersCount);
    }
    
}
