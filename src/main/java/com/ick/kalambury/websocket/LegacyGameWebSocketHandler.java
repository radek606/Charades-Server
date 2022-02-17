package com.ick.kalambury.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public class LegacyGameWebSocketHandler extends AbstractGameWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyGameWebSocketHandler.class);

    public static final String QUERY_PARAM_USER_ID = "user_id";
    public static final String QUERY_PARAM_USERNAME = "username";
    public static final String QUERY_PARAM_TABLE_ID = "table_id";
    public static final String QUERY_PARAM_CLIENT_VERSION = "version";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Established connection: " + session.getId());

        Map<String, Object> attributes = session.getAttributes();

        try {
            validateQueryParams(attributes);
        } catch (IllegalArgumentException e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(e.getMessage()));
            return;
        }

        String userId = (String) attributes.get(QUERY_PARAM_USER_ID);
        String username = (String) attributes.get(QUERY_PARAM_USERNAME);
        String tableId = (String) attributes.get(QUERY_PARAM_TABLE_ID);

        User user = User.newBuilder(null)
                .setUserId(userId)
                .setNickname(username + "_" + userId.split("-")[0])
                .setRole(Role.GUEST)
                .build();

        addConnection(session, user, tableId);
    }

    private void validateQueryParams(Map<String, Object> attributes) throws IllegalArgumentException, JsonProcessingException {
        if (!attributes.containsKey(QUERY_PARAM_USER_ID) || !attributes.containsKey(QUERY_PARAM_USERNAME)
                || !attributes.containsKey(QUERY_PARAM_TABLE_ID) || !attributes.containsKey(QUERY_PARAM_CLIENT_VERSION)) {
            LOGGER.warn("Closing connection with missing query params");
            throw new IllegalArgumentException(objectMapper.writeValueAsString(new IncompatibleVersionCloseReason(
                    config.getClientConfig().getMinSupportedVersion(), config.getClientConfig().getMinSupportedVersionName())));
        }

        int clientVersion = Integer.parseInt((String) attributes.get(QUERY_PARAM_CLIENT_VERSION));
        if (clientVersion < config.getClientConfig().getMinSupportedVersion()) {
            LOGGER.warn("Closing connection with incompatible version");
            throw new IllegalArgumentException(objectMapper.writeValueAsString(new IncompatibleVersionCloseReason(
                    config.getClientConfig().getMinSupportedVersion(), config.getClientConfig().getMinSupportedVersionName())));
        }
    }

}
