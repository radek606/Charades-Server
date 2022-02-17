package com.ick.kalambury.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class TableConfig {

    @JsonProperty
    private String id;

    @JsonProperty
    private TableKind kind;

    @JsonProperty
    private PlayerChooseMethod playerChooseMethod;

    @JsonProperty
    private int pointsLimit;

    @JsonProperty
    private int maxPlayers = 10;

    @JsonProperty
    private int roundTime;

    @JsonProperty
    private String language;

    @JsonProperty
    private String ownerId;

    @JsonProperty
    private Set<String> categories;

    public TableConfig() { }

    public String getId() {
        return id;
    }

    public TableKind getKind() {
        return kind;
    }

    public PlayerChooseMethod getPlayerChooseMethod() {
        return playerChooseMethod;
    }

    public int getPointsLimit() {
        return pointsLimit;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public String getLanguage() {
        return language;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Set<String> getCategories() {
        return categories;
    }

}
