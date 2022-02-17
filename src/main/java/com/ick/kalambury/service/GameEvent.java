package com.ick.kalambury.service;

import com.ick.kalambury.entities.User;
import com.ick.kalambury.messages.GameDataMessage;

public class GameEvent {

    public enum Type {
        PLAYER_CONNECTED, GAME_DATA, PLAYER_DISCONNECTED
    }

    public enum DisconnectReason {
        LEFT, KICKED, FAILURE
    }

    private final Type type;
    private final User user;
    private final GameDataMessage gameData;
    private final DisconnectReason reason;

    public static GameEvent connected(User user) {
        return new GameEvent(Type.PLAYER_CONNECTED, user, null, null);
    }

    public static GameEvent gameData(User user, GameDataMessage message) {
        return new GameEvent(Type.GAME_DATA, user, message, null);
    }

    public static GameEvent disconnected(User user, DisconnectReason reason) {
        return new GameEvent(Type.PLAYER_DISCONNECTED, user, null, reason);
    }

    private GameEvent(Type type, User user, GameDataMessage gameData, DisconnectReason reason) {
        this.type = type;
        this.user = user;
        this.gameData = gameData;
        this.reason = reason;
    }

    public Type getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public GameDataMessage getGameData() {
        return gameData;
    }

    public DisconnectReason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "GameEvent{" +
                "type=" + type +
                ", user=" + user +
                ", gameData=" + gameData +
                ", reason=" + reason +
                '}';
    }
}
