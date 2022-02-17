package com.ick.kalambury.websocket;

import com.ick.kalambury.entities.User;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebSocketConnection {

    private final ExecutorService executor;

    private final WebSocketSession session;
    private final User user;
    private final String tableId;

    public WebSocketConnection(WebSocketSession session, User user, String tableId) {
        this.executor = Executors.newSingleThreadExecutor();
        this.session = session;
        this.user = user;
        this.tableId = tableId;
    }

    public Future<Boolean> sendMessage(BinaryMessage message) {
        return executor.submit(() -> {
            if (session.isOpen()) {
                session.sendMessage(message);
                return true;
            } else {
                return false;
            }
        });
    }

    public String getSessionId() {
        return session.getId();
    }

    public User getUser() {
        return user;
    }

    public String getTableId() {
        return tableId;
    }

    public void close(CloseStatus status) throws IOException {
        executor.shutdownNow();
        session.close(status);
    }
}
