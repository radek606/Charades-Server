package com.ick.kalambury.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ick.kalambury.config.Parameters;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

public class AbstractGameWebSocketHandler extends BinaryWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGameWebSocketHandler.class);

    @Autowired
    Parameters config;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GameService gameService;

    protected void addConnection(WebSocketSession session, User user, String endpoint) {
        Parameters.ServerConfig serverConfig = config.getServerConfig();

        ConcurrentWebSocketSessionDecorator decoratedSession = new ConcurrentWebSocketSessionDecorator(session,
                serverConfig.getSendTimeLimitSeconds(), serverConfig.getMessageBufferSize(),
                ConcurrentWebSocketSessionDecorator.OverflowStrategy.DROP);
        decoratedSession.setBinaryMessageSizeLimit(serverConfig.getMessageSize());

        gameService.addConnection(decoratedSession, user, endpoint);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        gameService.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOGGER.info("Closed connection: " + session.getId());
        gameService.removeConnection(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOGGER.warn("Transport error for session: " + session.getId() + ", " + exception.toString());
    }

}
