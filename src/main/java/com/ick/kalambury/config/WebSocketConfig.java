package com.ick.kalambury.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ick.kalambury.entities.ConnectionData;
import com.ick.kalambury.entities.ConnectionDataLegacy;
import com.ick.kalambury.websocket.GameWebSocketHandlerV2;
import com.ick.kalambury.websocket.LegacyGameWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocket
@Import(Parameters.class)
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConfig.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxSessionIdleTimeout(60000L);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(legacyGameSocketHandler(), "/v1/game/websocket")
                .addInterceptors(legacyQueryParamsInterceptor())
                .setAllowedOrigins("*");
        registry.addHandler(gameSocketHandlerV2(), "/v2/game/websocket")
                .addInterceptors(queryParamsInterceptorV2())
                .setAllowedOrigins("*");
    }

    @Bean
    public GameWebSocketHandlerV2 gameSocketHandlerV2() {
        return new GameWebSocketHandlerV2();
    }

    @Bean
    public LegacyGameWebSocketHandler legacyGameSocketHandler() {
        return new LegacyGameWebSocketHandler();
    }

    @Bean
    public HandshakeInterceptor legacyQueryParamsInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                String query = request.getURI().getQuery();
                if (!StringUtils.isEmpty(query)) {
                    attributes.putAll(Arrays.stream(query.split("&"))
                            .map(param -> param.split("="))
                            .collect(Collectors.toMap(param -> param[0], param -> param[1])));
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }

    @Bean
    public HandshakeInterceptor queryParamsInterceptorV2() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                ConnectionData data = parseConnectionData(request.getURI().getQuery());
                if (data != null) {
                    attributes.put(GameWebSocketHandlerV2.QUERY_PARAM_DATA, data);
                    return true;
                } else {
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    return false;
                }
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }

    private ConnectionData parseConnectionData(String query) {
        if (StringUtils.isEmpty(query)) {
            return null;
        }

        Map<String, Object> attributes = Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        if (!attributes.containsKey(GameWebSocketHandlerV2.QUERY_PARAM_DATA)) {
            return null;
        }

        String dataString = (String) attributes.get(GameWebSocketHandlerV2.QUERY_PARAM_DATA);
        try {
            return ConnectionData.parseFrom(Base64.getUrlDecoder().decode(dataString));
        } catch (InvalidProtocolBufferException e) {
            try {
                ConnectionDataLegacy dataLegacy = ConnectionDataLegacy.parse(objectMapper, dataString);
                return ConnectionData.newBuilder()
                        .setEndpoint(dataLegacy.getEndpoint())
                        .setNickname(dataLegacy.getNickname())
                        .setUuid(dataLegacy.getUuid())
                        .setVersion(dataLegacy.getVersion())
                        .build();
            } catch (IOException | IllegalArgumentException ex) {
                LOGGER.warn("Incorrect connection data in websocket request.", ex);
                return null;
            }
        }

    }
}
