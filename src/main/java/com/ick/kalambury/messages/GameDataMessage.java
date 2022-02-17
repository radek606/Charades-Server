package com.ick.kalambury.messages;

import com.ick.kalambury.entities.GameDataProtos;
import com.ick.kalambury.entities.Player;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GameDataMessage {

    //server generated actions
    public static final int INITIAL_DATA       = (1 << 1);
    public static final int PLAYER_UPDATE      = (1 << 2);
    public static final int GAME_STATE_CHANGE  = (1 << 3);
    public static final int GAME_FINISH        = (1 << 4);

    //player generated actions
    public static final int PLAYER_READY       = (1 << 6);
    public static final int CHAT_MESSAGE       = (1 << 7);
    public static final int CLEAR_SCREEN       = (1 << 8);
    public static final int ADD_NEW_OBJECT     = (1 << 9);
    public static final int DELETE_LAST_OBJECT = (1 << 10);
    public static final int ABANDON_DRAWING    = (1 << 11);
    public static final int CONTINUE           = (1 << 12);
    public static final int QUIT_GAME          = (1 << 13);

    //table operator dedicated actions
    public static final int KICK_PLAYER        = (1 << 14);

    private final int actions;
    private final String actionData;
    private final GameConfigMessage gameConfig;
    private final GameStateMessage gameState;
    private final Map<String, Player> players;
    private final List<ChatMessage> messages;
    private final List<DrawableMessage> drawables;

    private GameDataMessage(Builder builder) {
        actions = builder.actions;
        actionData = builder.actionData;
        gameConfig = builder.gameConfig;
        gameState = builder.gameState;
        players = builder.players;
        messages = builder.messages.stream().filter(Objects::nonNull).collect(Collectors.toList());
        drawables = builder.drawables;
    }

    public static Builder newBuilder(int action) {
        return new Builder(action);
    }

    public static Builder newBuilder(GameDataProtos.GameData gameData) {
        return new Builder(gameData);
    }

    public static GameDataMessage fromProto(GameDataProtos.GameData gameData) {
        Builder builder = new Builder(gameData);

        if (gameData.hasActionData()) {
            builder.withActionData(gameData.getActionData());
        }

        if (gameData.hasGameState()) {
            builder.withGameState(GameStateMessage.fromProto(gameData.getGameState()));
        }

        gameData.getMessagesList().forEach(m -> builder.addChatMessage(ChatMessage.fromProto(m)));
        gameData.getDrawablesList().forEach(d -> builder.addDrawable(DrawableMessage.fromProto(d)));

        return builder.build();
    }

    public int getActions() {
        return actions;
    }

    public String getActionData() {
        return actionData;
    }

    public boolean hasAction(int action) {
        return (actions & action) == action;
    }

    public GameStateMessage getGameState() {
        return gameState;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public @Nullable ChatMessage getMessage(@NonNull GameDataProtos.ChatMessage.Type type) {
        return messages.stream()
                .filter(m -> m.getType() == type)
                .findFirst()
                .orElse(null);
    }

    public List<DrawableMessage> getDrawables() {
        return drawables;
    }

    public GameDataProtos.GameData toProto() {
        GameDataProtos.GameData.Builder builder = GameDataProtos.GameData.newBuilder();
        builder.setAction(actions);

        if (StringUtils.hasLength(actionData)) {
            builder.setActionData(actionData);
        }

        if (gameConfig != null) {
            builder.setConfig(gameConfig.toProto());
        }

        if (gameState != null) {
            builder.setGameState(gameState.toProto());
        }

        players.forEach((k, p) -> builder.putPlayers(k, p.toProto()));
        messages.forEach(m -> builder.addMessages(m.toProto()));
        drawables.forEach(d -> builder.addDrawables(d.toProto()));

        return builder.build();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("GameData{actions=[");

        if (hasAction(INITIAL_DATA)) {
            builder.append("INITIAL_DATA,");
        }
        if (hasAction(PLAYER_UPDATE)) {
            builder.append("PLAYER_UPDATE,");
        }
        if (hasAction(GAME_STATE_CHANGE)) {
            builder.append("GAME_STATE_CHANGE,");
        }
        if (hasAction(GAME_FINISH)) {
            builder.append("GAME_FINISH,");
        }
        if (hasAction(CHAT_MESSAGE)) {
            builder.append("CHAT_MESSAGE,");
        }
        if (hasAction(PLAYER_READY)) {
            builder.append("PLAYER_READY,");
        }
        if (hasAction(CLEAR_SCREEN)) {
            builder.append("CLEAR_SCREEN,");
        }
        if (hasAction(ADD_NEW_OBJECT)) {
            builder.append("ADD_NEW_OBJECT,");
        }
        if (hasAction(DELETE_LAST_OBJECT)) {
            builder.append("DELETE_LAST_OBJECT,");
        }
        if (hasAction(ABANDON_DRAWING)) {
            builder.append("ABANDON_DRAWING,");
        }
        if (hasAction(CONTINUE)) {
            builder.append("AGREE_CONTINUE,");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("]}");

        return builder.toString();
    }

    public static final class Builder {

        private int actions;
        private String actionData;
        private GameConfigMessage gameConfig;
        private GameStateMessage gameState;
        private Map<String, Player> players = new HashMap<>();
        private List<ChatMessage> messages = new ArrayList<>();
        private List<DrawableMessage> drawables = new ArrayList<>();

        private Builder(int action) {
            this.actions = action;
        }

        private Builder(GameDataProtos.GameData gameData) {
            this.actions = gameData.getAction();
        }

        public Builder addAction(int action) {
            this.actions |= action;
            return this;
        }

        public Builder withActionData(String data) {
            this.actionData = data;
            return this;
        }

        public Builder withGameConfig(GameConfigMessage gameConfig) {
            this.gameConfig = gameConfig;
            return this;
        }

        public Builder withGameState(GameStateMessage gameState) {
            this.gameState = gameState;
            return this;
        }

        public Builder withPlayers(Map<String, Player> players) {
            this.players = new HashMap<>(players);
            return this;
        }

        public Builder putPlayer(Player player) {
            this.players.put(player.getId(), player);
            return this;
        }

        public Builder withChatMessages(List<ChatMessage> messages) {
            this.messages = messages;
            return this;
        }

        public Builder addChatMessage(ChatMessage message) {
            this.messages.add(message);
            return this;
        }

        public Builder withDrawables(List<DrawableMessage> drawables) {
            this.drawables = drawables;
            return this;
        }

        public Builder addDrawable(DrawableMessage line) {
            this.drawables.add(line);
            return this;
        }

        public GameDataMessage build() {
            return new GameDataMessage(this);
        }
    }
}
