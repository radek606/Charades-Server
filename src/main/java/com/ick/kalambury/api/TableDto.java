package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ick.kalambury.service.TableConfig;
import com.ick.kalambury.service.TableKind;

public class TableDto {

    @JsonProperty
    private String id;

    @JsonProperty
    private TableKind kind;

    @JsonProperty
    private String name;

    @JsonProperty
    private int pointsLimit;

    @JsonProperty
    private int maxPlayers;

    @JsonProperty
    private int playersCount;

    @JsonProperty
    private int roundTime;

    @JsonProperty
    private String language;

    @JsonProperty
    private String operatorName;

    public TableDto() { }

    private TableDto(Builder builder) {
        id = builder.id;
        kind = builder.kind;
        name = builder.name;
        pointsLimit = builder.pointsLimit;
        maxPlayers = builder.maxPlayers;
        playersCount = builder.playersCount;
        roundTime = builder.roundTime;
        language = builder.language;
        operatorName = builder.operatorName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder fromConfig(TableConfig config) {
        return newBuilder()
                .setId(config.getId())
                .setKind(config.getKind())
                .setPointsLimit(config.getPointsLimit())
                .setMaxPlayers(config.getMaxPlayers())
                .setRoundTime(config.getRoundTime())
                .setLanguage(config.getLanguage());
    }

    public String getId() {
        return id;
    }

    public TableKind getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public int getPointsLimit() {
        return pointsLimit;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public String getLanguage() {
        return language;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public static final class Builder {
        private String id;
        private TableKind kind;
        private String name;
        private int pointsLimit;
        private int maxPlayers;
        private int playersCount;
        private int roundTime;
        private String language;
        private String operatorName;

        private Builder() {
        }

        public Builder setId(String val) {
            id = val;
            return this;
        }

        public Builder setKind(TableKind val) {
            kind = val;
            return this;
        }

        public Builder setName(String val) {
            name = val;
            return this;
        }

        public Builder setPointsLimit(int val) {
            pointsLimit = val;
            return this;
        }

        public Builder setMaxPlayers(int val) {
            maxPlayers = val;
            return this;
        }

        public Builder setPlayersCount(int val) {
            playersCount = val;
            return this;
        }

        public Builder setRoundTime(int val) {
            roundTime = val;
            return this;
        }

        public Builder setLanguage(String val) {
            language = val;
            return this;
        }

        public Builder setOperatorName(String val) {
            operatorName = val;
            return this;
        }

        public TableDto build() {
            return new TableDto(this);
        }
    }

    @Override
    public String toString() {
        return "TableDto{" +
                "id='" + id + '\'' +
                ", kind=" + kind +
                ", name='" + name + '\'' +
                ", pointsLimit=" + pointsLimit +
                ", maxPlayers=" + maxPlayers +
                ", playersCount=" + playersCount +
                ", roundTime=" + roundTime +
                ", language='" + language + '\'' +
                ", operatorName='" + operatorName + '\'' +
                '}';
    }
}
