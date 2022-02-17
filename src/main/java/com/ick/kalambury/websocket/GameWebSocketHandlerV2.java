package com.ick.kalambury.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ick.kalambury.entities.ConnectionData;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public class GameWebSocketHandlerV2 extends AbstractGameWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameWebSocketHandlerV2.class);

    public static final String QUERY_PARAM_DATA = "data";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Established connection: " + session.getId());

        ConnectionData data = (ConnectionData) session.getAttributes().get(QUERY_PARAM_DATA);

        try {
            validateQueryParams(data);
        } catch (IllegalArgumentException e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(e.getMessage()));
            return;
        }

        User user = User.newBuilder(null)
                .setUserId(data.getUuid())
                .setNickname(data.getNickname() + "_" + data.getUuid().split("-")[0])
                .setRole(Role.GUEST)
                .build();

        addConnection(session, user, data.getEndpoint());
    }

    private void validateQueryParams(ConnectionData data) throws IllegalArgumentException, JsonProcessingException {
        if (StringUtils.isEmpty(data.getEndpoint()) || StringUtils.isEmpty(data.getNickname())
                || StringUtils.isEmpty(data.getUuid()) || data.getVersion() == 0) {
            LOGGER.warn("Closing connection with missing query params");
            throw new IllegalArgumentException(objectMapper.writeValueAsString(new IncompatibleVersionCloseReason(
                    config.getClientConfig().getMinSupportedVersion(), config.getClientConfig().getMinSupportedVersionName())));
        }

        if (data.getVersion() < config.getClientConfig().getMinSupportedVersion()) {
            LOGGER.warn("Closing connection with incompatible version");
            throw new IllegalArgumentException(objectMapper.writeValueAsString(new IncompatibleVersionCloseReason(
                    config.getClientConfig().getMinSupportedVersion(), config.getClientConfig().getMinSupportedVersionName())));
        }
    }

}
