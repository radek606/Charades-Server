package com.ick.kalambury.messages;

import com.ick.kalambury.entities.GameDataProtos;
import org.springframework.util.StringUtils;

public class ChatMessage {

    private GameDataProtos.ChatMessage.Type type;
    private String source;
    private String body;

    private ChatMessage(Builder builder) {
        type = builder.type;
        source = builder.source;
        body = builder.body;
    }

    public static Builder newBuilder(GameDataProtos.ChatMessage.Type val) {
        return new Builder(val);
    }

    public static ChatMessage playerJoin(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_JOIN)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage playerDraw(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_DRAW)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage playerGuess(String playerId, String word) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_GUESS)
                .setSource(playerId)
                .setBody(word)
                .build();
    }

    public static ChatMessage playerAbandon(String body) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_ABANDON)
                .setSource(body)
                .build();
    }

    public static ChatMessage playerInactive(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_INACTIVE)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage playerLeft(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_LEFT)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage playerAnswer(String playerId, String answer) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_ANSWER)
                .setSource(playerId)
                .setBody(answer)
                .build();
    }

    public static ChatMessage playerWrite(String playerId, String text) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_WRITE)
                .setSource(playerId)
                .setBody(text)
                .build();
    }

    public static ChatMessage playerWon(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_WON)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage playerKicked(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PLAYER_KICKED)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage waiting() {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.WAITING).build();
    }

    public static ChatMessage inactivityWarn() {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.INACTIVITY_WARN).build();
    }

    public static ChatMessage littleTimeWarn() {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.LITTLE_TIME_WARN).build();
    }

    public static ChatMessage timeIsUp() {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.TIME_IS_UP).build();
    }

    public static ChatMessage closeEnoughAnswer(String body) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.CLOSE_ENOUGH_ANSWER)
                .setBody(body)
                .build();
    }

    public static ChatMessage hint(String body) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.HINT)
                .setBody(body)
                .build();
    }

    public static ChatMessage word(String body) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.PASSWORD)
                .setBody(body)
                .build();
    }

    public static ChatMessage newOperator(String playerId) {
        return ChatMessage.newBuilder(GameDataProtos.ChatMessage.Type.NEW_OPERATOR)
                .setSource(playerId)
                .build();
    }

    public static ChatMessage fromProto(GameDataProtos.ChatMessage message) {
        return newBuilder(message.getType())
                .setSource(message.getSource())
                .setBody(message.getBody())
                .build();
    }

    public GameDataProtos.ChatMessage.Type getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getBody() {
        return body;
    }

    public GameDataProtos.ChatMessage toProto() {
        GameDataProtos.ChatMessage.Builder builder = GameDataProtos.ChatMessage.newBuilder();
        builder.setType(type);

        if (!StringUtils.isEmpty(source)) {
            builder.setSource(source);
        }

        if (!StringUtils.isEmpty(body)) {
            builder.setBody(body);
        }

        return builder.build();
    }

    public static final class Builder {
        private GameDataProtos.ChatMessage.Type type;
        private String source;
        private String body;

        private Builder(GameDataProtos.ChatMessage.Type val) {
            type = val;
        }

        public Builder setSource(String val) {
            source = val;
            return this;
        }

        public Builder setBody(String val) {
            body = val;
            return this;
        }

        public ChatMessage build() {
            return new ChatMessage(this);
        }
    }
}
