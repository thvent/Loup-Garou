package org.garou.model;

import java.util.UUID;

public interface Lobby {

    public UUID getUUID();

    public int nbPlayers();

    public int nbMaxPlayers();

    public String getOwner();

    public void setOwner(String username);
    
}
