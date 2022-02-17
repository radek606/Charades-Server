package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerDto {

    @JsonProperty
    private String id;

    @JsonProperty
    private String nickname;

    @JsonProperty
    private String tableName;

    public PlayerDto() {}

    public PlayerDto(String id, String nickname) {
        this(id, nickname, null);
    }

    public PlayerDto(String id, String nickname, String tableName) {
        this.id = id;
        this.nickname = nickname;
        this.tableName = tableName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "PlayerDto{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
