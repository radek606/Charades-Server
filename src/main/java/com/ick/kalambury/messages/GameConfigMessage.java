package com.ick.kalambury.messages;

import com.ick.kalambury.entities.GameDataProtos;
import com.ick.kalambury.service.PlayerChooseMethod;
import com.ick.kalambury.service.TableConfig;

public class GameConfigMessage {

    private final String name;
    private final int roundTime;
    private final int pointsLimit;
    private final PlayerChooseMethod playerChooseMethod;
    private final String language;

    public static GameConfigMessage.Builder newBuilder() {
        return new Builder();
    }

    public static GameConfigMessage.Builder fromTableConfig(TableConfig config) {
        return newBuilder()
                .setRoundTime(config.getRoundTime())
                .setPointsLimit(config.getPointsLimit())
                .setLanguage(config.getLanguage())
                .setPlayerChooseMethod(config.getPlayerChooseMethod());
    }

    public GameDataProtos.GameConfig toProto() {
        return GameDataProtos.GameConfig.newBuilder()
                .setPointsLimit(pointsLimit)
                .setRoundTime(roundTime)
                .setLanguage(language)
                .setChooseMethod(GameDataProtos.GameConfig.PlayerChooseMethod.valueOf(playerChooseMethod.name()))
                .setName(name)
                .build();
    }

    private GameConfigMessage(Builder builder) {
        language = builder.language;
        roundTime = builder.roundTime;
        name = builder.name;
        playerChooseMethod = builder.playerChooseMethod;
        pointsLimit = builder.pointsLimit;
    }

    public static final class Builder {
        private String name;
        private int roundTime;
        private int pointsLimit;
        private PlayerChooseMethod playerChooseMethod;
        private String language;

        private Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setRoundTime(int roundTime) {
            this.roundTime = roundTime;
            return this;
        }

        public Builder setPointsLimit(int pointsLimit) {
            this.pointsLimit = pointsLimit;
            return this;
        }

        public Builder setPlayerChooseMethod(PlayerChooseMethod playerChooseMethod) {
            this.playerChooseMethod = playerChooseMethod;
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public GameConfigMessage build() {
            return new GameConfigMessage(this);
        }
    }
}
