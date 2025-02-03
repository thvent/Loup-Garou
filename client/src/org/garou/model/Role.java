package org.garou.model;

public enum Role {

    INCONNU("inconnu", "inconnu"),
    VILLAGEOIS("villageois", "villageois"),
    CHASSEUR("chasseur", "chasseur"),
    CUPIDON("cupidon", "cupidon"),
    FILLE("fille", "petite fille"),
    LOUP("loup", "loup-garou"),
    MAIRE("maire", "maire"),
    SORCIERE("sorciere", "sorcière"),
    VOLEUR("voleur", "voleur"),
    VOYANTE("voyante", "voyante");
    
    private String name;
    private String fullName;

    private Role(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

}