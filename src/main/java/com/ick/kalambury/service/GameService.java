package com.ick.kalambury.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ick.kalambury.api.TableIdDto;
import com.ick.kalambury.config.Parameters;
import com.ick.kalambury.entities.GameDataProtos;
import com.ick.kalambury.entities.Player;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.messages.GameDataMessage;
import com.ick.kalambury.util.TableNameProvider;
import com.ick.kalambury.websocket.WebSocketConnection;
import com.ick.kalambury.words.WordsManager;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameService implements TableActionsCallbacks {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    private final AtomicInteger playersCounter;

    private final Map<String, WebSocketConnection> sessions; //<session_id, session>
    private final Map<String, WebSocketConnection> users;    //<user_id, session>
    private final Map<String, Table> tables;                 //<table_id, Table>

    private final Parameters parameters;
    private final WordsManager wordsManager;
    private final TableNameProvider tableNameProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public GameService(Parameters parameters, WordsManager wordsManager,
                       TableNameProvider tableNameProvider, ObjectMapper objectMapper, MeterRegistry registry) {
        this.parameters = parameters;
        this.wordsManager = wordsManager;
        this.tableNameProvider = tableNameProvider;
        this.objectMapper = objectMapper;
        this.playersCounter = registry.gauge("players.count", new AtomicInteger(0));
        this.sessions = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.tables = new ConcurrentHashMap<>();
    }

    @PostConstruct
    private void init() throws IOException {
        TypeReference<List<TableConfig>> categoriesTypeRef = new TypeReference<>() {};
        ClassPathResource categoriesRes = new ClassPathResource(parameters.getGameConfig().getDefaultTablesConfigPath());
        List<TableConfig> defaultTables = objectMapper.readValue(categoriesRes.getInputStream(), categoriesTypeRef);

        for (TableConfig tableConfig : defaultTables) {
            initTable(tableConfig.getId(), tableNameProvider.acquireName(tableConfig.getKind()), tableConfig);
        }
    }

    public synchronized TableIdDto createTable(User user, TableConfig tableConfig) {
        String tableId = user.getTableId();
        if (StringUtils.isEmpty(tableId)) {
            tableId = generateRandomId();
        }
        String name = tableNameProvider.acquireName(tableConfig.getKind());
        initTable(tableId, name, tableConfig);
        return new TableIdDto(tableId, name);
    }

    private void initTable(String tableId, String tableName, TableConfig tableConfig) {
        Table table = new Table(tableId, tableName, tableConfig, parameters.getGameConfig(), wordsManager, this);
        tables.put(tableId, table);
        wordsManager.registerTable(tableId, tableConfig.getCategories());

        LOGGER.info("Created " + tableConfig.getKind() + " table: " + tableName);
    }

    public void addConnection(WebSocketSession session, User user, String tableId) {
        if (!tables.containsKey(tableId)) {
            try {
                LOGGER.warn("User: " + user.getId() + " tried to join non-existing table: " + tableId + ". Terminating...");
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("103"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (tables.get(tableId).getActivePlayersCount() >= tables.get(tableId).getTableConfig().getMaxPlayers()) {
            try {
                LOGGER.warn("User: " + user.getId() + " tried to join to already full table: " + tableId + ". Terminating...");
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("102"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        synchronized (this) {
            WebSocketConnection connection = new WebSocketConnection(session, user, tableId);
            sessions.put(session.getId(), connection);
            users.put(user.getId(), connection);
            tables.get(tableId).handleEvent(GameEvent.connected(connection.getUser()));

            playersCounter.incrementAndGet();
        }
    }

    public void handleMessage(WebSocketSession session, BinaryMessage message) {
        try {
            WebSocketConnection connection = sessions.get(session.getId());
            Table table = tables.get(connection.getTableId());

            if (table != null) {
                try {
                    GameDataProtos.Envelope envelope = GameDataProtos.Envelope.parseFrom(message.getPayload());
                    switch (envelope.getType()) {
                        case KEEPALIVE:
                            LOGGER.info("Keep-alive from: " + connection.getSessionId());
                            break;
                        case CONTENT:
                            table.handleEvent(GameEvent.gameData(connection.getUser(), decodeMessage(envelope)));
                            break;
                    }
                } catch (InvalidProtocolBufferException ex) {
                    session.close(CloseStatus.BAD_DATA);
                    removeConnection(session, CloseStatus.BAD_DATA);
                }
            } else {
                session.close(CloseStatus.NORMAL);
                removeConnection(session, CloseStatus.NORMAL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeConnection(WebSocketSession session, CloseStatus status) {
        if (!sessions.containsKey(session.getId())) {
            LOGGER.warn("Tried to remove non-existing connection.");
            return;
        }

        synchronized (this) {
            WebSocketConnection connection = sessions.remove(session.getId());
            users.remove(connection.getUser().getId());
            Table table = tables.get(connection.getTableId());
            table.handleEvent(GameEvent.disconnected(connection.getUser(),
                    status == CloseStatus.POLICY_VIOLATION ? GameEvent.DisconnectReason.KICKED : GameEvent.DisconnectReason.LEFT));

            playersCounter.decrementAndGet();
        }
    }

    private void clearTable(Table table) {
        switch (table.getTableConfig().getKind()) {
            case DEFAULT: {
                wordsManager.saveTable(table.getId());
                table.reset();
                break;
            }
            case PUBLIC:
            case PRIVATE: {
                tables.remove(table.getId());
                wordsManager.saveTable(table.getId());
                tableNameProvider.releaseName(table.getTableConfig().getKind(), table.getName());
                table.destroy();
                break;
            }
        }
        LOGGER.info("Cleared " + table.getTableConfig().getKind() + " table: " + table.getName());
    }

    private void clearSessions(Table table) {
        for (Player player : table.getPlayers()) {
            WebSocketConnection connection = users.remove(player.getId());
            if (connection != null) {
                sessions.remove(connection.getSessionId());
            }
        }
    }

    @Override
    public void sendMessage(GameDataMessage message, List<String> recipientIds) {
        for (String id : recipientIds) {
            if (users.containsKey(id)) {
                users.get(id).sendMessage(encodeMessage(message));
            }
        }
    }

    @Override
    public void endPlayerSession(String userId, CloseStatus status) {
        try {
            WebSocketConnection connection = users.remove(userId);
            if (connection != null) {
                connection.close(status);
            } else {
                LOGGER.warn("Trying to close non-existent user session.");
            }
        } catch (IOException e) {
            LOGGER.error("Error during closing user session.", e);
        }
    }

    @Override
    public void clearTable(String tableId) {
        Table table = tables.get(tableId);
        if (table != null && table.getActivePlayersCount() == 0) {
            clearSessions(table);
            clearTable(table);
        }
    }

    private GameDataMessage decodeMessage(GameDataProtos.Envelope envelope) throws InvalidProtocolBufferException {
        return GameDataMessage.fromProto(GameDataProtos.Content.parseFrom(envelope.getContent()).getData());
    }

    private BinaryMessage encodeMessage(GameDataMessage message) {
        return new BinaryMessage(GameDataProtos.Envelope.newBuilder()
                .setContent(GameDataProtos.Content.newBuilder()
                        .setData(message.toProto())
                        .build().toByteString())
                .build().toByteArray());
    }

    public Collection<Table> getTables() {
        List<Table> tables = new LinkedList<>(this.tables.values());
        tables.sort(Comparator.comparing(Table::getName));
        return tables;
    }

    @PreDestroy
    private void deinit() {
        for (WebSocketConnection connection : sessions.values()) {
            try {
                connection.close(CloseStatus.GOING_AWAY);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessions.clear();
        tables.clear();
        users.clear();
    }

    private String generateRandomId() {
        return UUID.randomUUID().toString().split("-")[0];
    }

}
