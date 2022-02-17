package com.ick.kalambury.messages;

import com.ick.kalambury.entities.GameDataProtos;
import org.springframework.util.StringUtils;

public class GameStateMessage {

    private final GameDataProtos.GameState.State state;
    private final String operatorPlayerId;
    private final String drawingPlayerId;
    private final String winnerPlayerId;
    private final String wordToGuess;
    private final String category;
    private final int timeLeft;

    private GameStateMessage(Builder builder) {
        state = builder.state;
        operatorPlayerId = builder.operatorPlayerId;
        drawingPlayerId = builder.drawingPlayerId;
        winnerPlayerId = builder.winnerPlayerId;
        wordToGuess = builder.wordToGuess;
        category = builder.category;
        timeLeft = builder.timeLeft;
    }

    public static GameStateMessage.Builder newBuilder(GameDataProtos.GameState.State state) {
        return new Builder(state);
    }

    public static GameStateMessage fromProto(GameDataProtos.GameState gameState) {
        return newBuilder(gameState.getState())
                .setOperatorPlayerId(gameState.getOperatorPlayerId())
                .setDrawingPlayerId(gameState.getDrawingPlayerId())
                .setWinnerPlayerId(gameState.getWinnerPlayerId())
                .setWordToGuess(gameState.getWordToGuess())
                .setCategory(gameState.getCategory())
                .setTimeLeft(gameState.getTimeLeft())
                .build();
    }

    public GameDataProtos.GameState.State getState() {
        return state;
    }

    public String getOperatorPlayerId() {
        return operatorPlayerId;
    }

    public String getDrawingPlayerId() {
        return drawingPlayerId;
    }

    public String getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public String getCategory() {
        return category;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public GameDataProtos.GameState toProto() {
        GameDataProtos.GameState.Builder builder = GameDataProtos.GameState.newBuilder();
        builder.setState(state);
        builder.setTimeLeft(timeLeft);

        if (StringUtils.hasLength(operatorPlayerId)) {
            builder.setOperatorPlayerId(operatorPlayerId);
        }

        if (StringUtils.hasLength(drawingPlayerId)) {
            builder.setDrawingPlayerId(drawingPlayerId);
        }

        if (StringUtils.hasLength(winnerPlayerId)) {
            builder.setWinnerPlayerId(winnerPlayerId);
        }

        if (StringUtils.hasLength(wordToGuess)) {
            builder.setWordToGuess(wordToGuess);
        }

        if (StringUtils.hasLength(category)) {
            builder.setCategory(category);
        }

        return builder.build();
    }

    public static final class Builder {
        private final GameDataProtos.GameState.State state;
        private String operatorPlayerId;
        private String drawingPlayerId;
        private String winnerPlayerId;
        private String wordToGuess;
        private String category;
        private int timeLeft;

        public Builder(GameDataProtos.GameState.State val) {
            state = val;
        }

        public Builder setOperatorPlayerId(String val) {
            operatorPlayerId = val;
            return this;
        }

        public Builder setDrawingPlayerId(String val) {
            drawingPlayerId = val;
            return this;
        }

        public Builder setWinnerPlayerId(String winnerPlayerId) {
            this.winnerPlayerId = winnerPlayerId;
            return this;
        }

        public Builder setWordToGuess(String wordToGuess) {
            this.wordToGuess = wordToGuess;
            return this;
        }

        public Builder setCategory(String val) {
            category = val;
            return this;
        }

        public Builder setTimeLeft(int val) {
            timeLeft = val;
            return this;
        }

        public GameStateMessage build() {
            return new GameStateMessage(this);
        }
    }
}
