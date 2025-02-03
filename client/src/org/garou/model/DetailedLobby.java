package org.garou.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DetailedLobby implements Lobby{
 
    private final UUID uuid;
    private String owner;
    private int maxPlayersCount;
    private Set<String> players;
    private List<Integer> options;

    public DetailedLobby(UUID uuid, String owner, int maxPlayersCount) {
        this(uuid, owner, maxPlayersCount, new HashSet<>(), new ArrayList<>());
    }

    public DetailedLobby(UUID uuid, String owner, int maxPlayersCount, Set<String> players, List<Integer> options) {
        this.uuid = uuid;
        this.owner = owner;
        this.maxPlayersCount = maxPlayersCount;
        this.players = players;
        this.players.add(owner);
        this.options = options;
    }

    public void setOwner(String username) {
        if (players.contains(username)) {
            this.owner = username;  
        }
    }

    public String getOwner() {
        return owner;
    }

    public int getMaxPlayersCount() {
        return maxPlayersCount;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public Set<String> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    public boolean addPlayer(String username) {
        return players.add(username);
    }

    public boolean removePlayer(String username) {
        return players.remove(username);
    }

    public String toString() {
        return String.format("%s's lobby (%d/%d)", owner, players.size(), maxPlayersCount);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public int nbPlayers() {
        return players.size();
    }

    @Override
    public int nbMaxPlayers() {
        return maxPlayersCount;
    }

    public void addOption(int i) {
        options.add(i);
    }

    public List<Integer> getOptions() {
        return options;
    }

}