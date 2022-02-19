package com.ick.kalambury.service;

import com.ick.kalambury.config.Parameters;
import com.ick.kalambury.entities.GameDataProtos;
import com.ick.kalambury.entities.Player;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.messages.*;
import com.ick.kalambury.util.CountDownTimer;
import com.ick.kalambury.words.Word;
import com.ick.kalambury.words.WordMatcher;
import com.ick.kalambury.words.WordMatchingResult;
import com.ick.kalambury.words.WordsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {

    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private final String id;
    private final String name;
    private final TableConfig tableConfig;

    private final ExecutorService eventExecutor;
    private final Random random;

    private final Parameters.GameConfig gameConfig;
    private WordsManager wordsManager;
    private TableActionsCallbacks callbacks;

    private GameDataProtos.GameState.State state;

    private Map<String, Player> players;
    private List<DrawableMessage> drawableObjects;

    private String firstPlayerId, drawingPlayerId, winnerPlayerId, operatorPlayerId;

    private Word currentWord;

    private CountDownTimer gameTimer;
    private int currentTimeLeft;
    private int inactivitySeconds;

    Table(String id, String name, TableConfig tableConfig, Parameters.GameConfig gameConfig, WordsManager wordsManager, TableActionsCallbacks callbacks) {
        this.id = id;
        this.name = name;
        this.tableConfig = tableConfig;
        this.gameConfig = gameConfig;
        this.wordsManager = wordsManager;
        this.callbacks = callbacks;
        this.players = new HashMap<>();
        this.drawableObjects = new ArrayList<>();
        this.random = new Random();
        this.state = GameDataProtos.GameState.State.NO_PLAYERS;
        this.eventExecutor = Executors.newSingleThreadExecutor();
    }

    public void handleEvent(final GameEvent event) {
        eventExecutor.execute(() -> handleEventInternal(event));
    }

    private void handleEventInternal(GameEvent event) {
        try {
            validateGameEvent(event);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error on table " + name + ": " + e.getMessage());
            return;
        }

        LOGGER.debug("New event on table " + name + ": " + event.toString());

        switch (event.getType()) {
            case PLAYER_CONNECTED:
                onNewPlayer(event.getUser());
                break;
            case GAME_DATA:
                onMessage(event.getUser(), event.getGameData());
                break;
            case PLAYER_DISCONNECTED:
                onPlayerDisconnected(event.getUser(), event.getReason());
                break;
        }
    }

    private void validateGameEvent(GameEvent event) {
        if (event.getType() == null) {
            throw new IllegalArgumentException("Missing event type");
        }

        if (event.getUser() == null) {
            throw new IllegalArgumentException("Missing user for event type: " + event.getType());
        }

        if (event.getType() == GameEvent.Type.GAME_DATA && event.getGameData() == null) {
            throw new IllegalArgumentException("Missing game data for event type: " + event.getType());
        }

        if (event.getType() == GameEvent.Type.PLAYER_DISCONNECTED && event.getReason() == null) {
            throw new IllegalArgumentException("Missing reason for event type: " + event.getType());
        }
    }

    private void onNewPlayer(User user) {
        Player player = players.getOrDefault(user.getId(), new Player(user));
        if (player.getStatus() == Player.Status.KICKED) {
            callbacks.endPlayerSession(user.getId(), CloseStatus.POLICY_VIOLATION.withReason("kicked"));
            return;
        }
        player.setStatus(Player.Status.CONNECTED);

        if (state == GameDataProtos.GameState.State.NO_PLAYERS) {
            firstPlayerId = player.getId();
            operatorPlayerId = assignOperator(player);
            winnerPlayerId = null;
            state = GameDataProtos.GameState.State.WAITING;
        }

        players.put(user.getId(), player);

        callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.INITIAL_DATA)
                .withGameState(getGameStateMessage(0))
                .withGameConfig(GameConfigMessage.fromTableConfig(tableConfig).setName(name).build())
                .withPlayers(players)
                .build(), Collections.singletonList(player.getId()));
    }

    private void onPlayerDisconnected(User user, GameEvent.DisconnectReason reason) {
        if (!players.containsKey(user.getId())) {
            LOGGER.warn("onPlayerDisconnected() - No player for user: " + user);
            callbacks.clearTable(id);
            return;
        }

        Player player = players.get(user.getId());
        if (player.getStatus() == Player.Status.IN_GAME) {
            player.setStatus(Player.Status.DISCONNECTED);
            player.setOperator(false);
        }

        if (getActivePlayersCount() >= 2) {
            GameDataMessage.Builder builder = GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE | GameDataMessage.PLAYER_UPDATE);
            builder.addChatMessage(getPlayerLeftMessage(player.getUsername(), reason));
            if (state == GameDataProtos.GameState.State.IN_GAME) {
                if (drawingPlayerId.equals(player.getId())) {
                    updateRounds();
                    drawingPlayerId = getNextPlayerIdToDraw();
                    currentWord = getNextWordToGuess();
                    builder.addAction(GameDataMessage.GAME_STATE_CHANGE);
                    builder.withGameState(getGameStateMessage(tableConfig.getRoundTime()));
                    builder.addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()));
                    drawableObjects.clear();
                    setGameTimer(tableConfig.getRoundTime());
                }
                if (player.getId().equals(operatorPlayerId)) {
                    List<Player> activePlayers = getActivePlayersStream().collect(Collectors.toList());
                    Player newOperator = activePlayers.get(random.nextInt(activePlayers.size()));
                    newOperator.setOperator(true);
                    operatorPlayerId = newOperator.getId();
                    builder.addAction(GameDataMessage.GAME_STATE_CHANGE);
                    builder.withGameState(getGameStateMessage(currentTimeLeft));
                    builder.addChatMessage(ChatMessage.newOperator(newOperator.getUsername()));
                }
            }
            builder.withPlayers(players);
            callbacks.sendMessage(builder.build(), Recipients.all(players.values()));
        } else if (getActivePlayersCount() == 1) {
            state = GameDataProtos.GameState.State.WAITING;
            if(gameTimer != null) {
                gameTimer.cancel();
            }
            drawableObjects.clear();
            drawingPlayerId = null;

            Player lastPlayer = getActivePlayersStream().findFirst().get();
            lastPlayer.reset();
            firstPlayerId = lastPlayer.getId();
            operatorPlayerId = assignOperator(lastPlayer);

            players.clear();
            players.put(lastPlayer.getId(), lastPlayer);

            callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE
                    | GameDataMessage.PLAYER_UPDATE | GameDataMessage.CHAT_MESSAGE)
                    .withGameState(getGameStateMessage(0))
                    .withPlayers(players)
                    .addChatMessage(ChatMessage.playerLeft(player.getUsername()))
                    .addChatMessage(StringUtils.isEmpty(operatorPlayerId) ? null : ChatMessage.newOperator(lastPlayer.getUsername()))
                    .addChatMessage(ChatMessage.waiting())
                    .build(), Recipients.all(players.values()));
        } else if (getActivePlayersCount() == 0) {
            callbacks.clearTable(id);
        }
    }

    private ChatMessage getPlayerLeftMessage(String playerId, GameEvent.DisconnectReason reason) {
        if (reason == GameEvent.DisconnectReason.KICKED) {
            return ChatMessage.playerKicked(playerId);
        } else {
            return ChatMessage.playerLeft(playerId);
        }
    }

    private void onMessage(User user, GameDataMessage gameData) {
        if (!players.containsKey(user.getId())) {
            LOGGER.warn("onMessage() - No player for user: " + user);
            return;
        }

        Player player = players.get(user.getId());

        if (gameData.hasAction(GameDataMessage.PLAYER_READY)) {
            player.setStatus(Player.Status.IN_GAME);
            if (getActivePlayersCount() >= 2) {
                GameDataMessage.Builder builder = GameDataMessage.newBuilder(GameDataMessage.PLAYER_UPDATE
                        | GameDataMessage.CHAT_MESSAGE)
                        .withPlayers(players)
                        .addChatMessage(ChatMessage.playerJoin(player.getUsername()));
                if (StringUtils.isEmpty(drawingPlayerId)) {
                    drawingPlayerId = firstPlayerId;
                    currentWord = getNextWordToGuess();
                    state = GameDataProtos.GameState.State.IN_GAME;
                    builder.addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()));
                    builder.addAction(GameDataMessage.GAME_STATE_CHANGE);
                    builder.withGameState(getGameStateMessage(tableConfig.getRoundTime()));
                    drawableObjects.clear();
                    setGameTimer(tableConfig.getRoundTime());
                } else {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE
                            | GameDataMessage.ADD_NEW_OBJECT | GameDataMessage.CHAT_MESSAGE)
                            .withGameState(getGameStateMessage(currentTimeLeft))
                            .withDrawables(drawableObjects)
                            .withPlayers(players)
                            .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
                            .build(), Recipients.one(player));
                }
                callbacks.sendMessage(builder.build(), Recipients.all(players.values()));
            } else {
                callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE | GameDataMessage.CHAT_MESSAGE)
                        .withGameState(getGameStateMessage(0))
                        .addChatMessage(StringUtils.isEmpty(operatorPlayerId) ? null : ChatMessage.newOperator(player.getUsername()))
                        .addChatMessage(ChatMessage.waiting())
                        .build(), Recipients.one(player));
            }
        } else if (gameData.hasAction(GameDataMessage.ADD_NEW_OBJECT)) {
            callbacks.sendMessage(gameData, Recipients.allExcept(players.values(), player));
            drawableObjects.addAll(gameData.getDrawables());
            inactivitySeconds = 0;
        } else if (gameData.hasAction(GameDataMessage.DELETE_LAST_OBJECT)) {
            callbacks.sendMessage(gameData, Recipients.allExcept(players.values(), player));
            if(!drawableObjects.isEmpty()) {
                drawableObjects.remove(drawableObjects.size() - 1);
            }
            inactivitySeconds = 0;
        } else if (gameData.hasAction(GameDataMessage.CLEAR_SCREEN)) {
            callbacks.sendMessage(gameData, Recipients.allExcept(players.values(), player));
            drawableObjects.clear();
            inactivitySeconds = 0;
        } else if (gameData.hasAction(GameDataMessage.CHAT_MESSAGE)) {
            ChatMessage answer = gameData.getMessages().get(0);
            if (answer.getType() == GameDataProtos.ChatMessage.Type.PLAYER_ANSWER) {
                WordMatchingResult matchingResult = wordsManager.matchWord(currentWord, answer.getBody());
                if (matchingResult.isMatch()) {
                    player.updatePoints(1);
                    if (player.getPoints() == tableConfig.getPointsLimit()) {
                        setGameTimer(0);
                        player.setWinner(true);
                        winnerPlayerId = player.getId();
                        state = GameDataProtos.GameState.State.FINISHED;
                        callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_FINISH
                                | GameDataMessage.CHAT_MESSAGE | GameDataMessage.PLAYER_UPDATE)
                                .withGameState(getGameStateMessage(0))
                                .addChatMessage(ChatMessage.playerAnswer(player.getUsername(), answer.getBody()))
                                .addChatMessage(ChatMessage.playerGuess(player.getUsername(), currentWord.getWord()))
                                .addChatMessage(ChatMessage.playerWon(player.getUsername()))
                                .withPlayers(players).build(), Recipients.all(players.values()));
                        resetTable();
                    } else {
                        updateRounds();
                        String lastWord = currentWord.getWord();
                        drawingPlayerId = getNextPlayerIdToDraw(player.getId());
                        currentWord = getNextWordToGuess();
                        callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE
                                        | GameDataMessage.CHAT_MESSAGE | GameDataMessage.PLAYER_UPDATE)
                                .withPlayers(players)
                                .withGameState(getGameStateMessage(tableConfig.getRoundTime()))
                                .addChatMessage(ChatMessage.playerAnswer(player.getUsername(), answer.getBody()))
                                .addChatMessage(ChatMessage.playerGuess(player.getUsername(), lastWord))
                                .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
                                .build(), Recipients.all(players.values()));
                        drawableObjects.clear();
                        setGameTimer(tableConfig.getRoundTime());
                    }
                } else if (matchingResult.isCloseEnough()) {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.playerAnswer(player.getUsername(), answer.getBody()))
                            .addChatMessage(ChatMessage.closeEnoughAnswer(answer.getBody()))
                            .build(), Recipients.all(players.values()));
                } else {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.playerAnswer(player.getUsername(), answer.getBody()))
                            .build(), Recipients.allExcept(players.values(), player));
                }
            } else if (answer.getType() == GameDataProtos.ChatMessage.Type.PLAYER_WRITE) {
                callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                        .addChatMessage(ChatMessage.playerWrite(player.getUsername(), answer.getBody()))
                        .build(), Recipients.allExcept(players.values(), player));
            }
        } else if (gameData.hasAction(GameDataMessage.ABANDON_DRAWING)) {
            player.updatePoints(-1);
            updateRounds();
            String lastPlayer = drawingPlayerId;
            drawingPlayerId = getNextPlayerIdToDraw();
            currentWord = getNextWordToGuess();
            callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE
                    | GameDataMessage.PLAYER_UPDATE | GameDataMessage.CHAT_MESSAGE)
                    .addChatMessage(ChatMessage.playerAbandon(players.get(lastPlayer).getUsername()))
                    .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
                    .withPlayers(players)
                    .withGameState(getGameStateMessage(tableConfig.getRoundTime()))
                    .build(), Recipients.all(players.values()));
            drawableObjects.clear();
            setGameTimer(tableConfig.getRoundTime());
        } else if (gameData.hasAction(GameDataMessage.KICK_PLAYER)) {
            String playerIdToKick = gameData.getActionData();
            if (players.containsKey(playerIdToKick)) {
                players.get(playerIdToKick).setStatus(Player.Status.KICKED);
                callbacks.endPlayerSession(playerIdToKick, CloseStatus.POLICY_VIOLATION.withReason("kicked"));
            }
        }
//        else if (gameData.hasAction(GameDataMessage.CONTINUE)) {
//            player.setStatus(Player.Status.IN_GAME);
//
//            if (getActivePlayersStream().allMatch(p -> p.getStatus() == Player.Status.IN_GAME)) {
//                state = GameDataProtos.GameState.State.IN_GAME;
//                players.entrySet().removeIf(e -> !e.getValue().isActive());
//                players.values().forEach(Player::reset);
//
//                winnerPlayerId = null;
//                drawingPlayerId = getNextPlayerIdToDraw();
//                currentWord = getNextWordToGuess();
//                drawableObjects.clear();
//                setGameTimer(tableConfig.getRoundTime());
//
//                callbacks.sendMessage( GameDataMessage.newBuilder(GameDataMessage.PLAYER_UPDATE
//                        | GameDataMessage.CHAT_MESSAGE | GameDataMessage.GAME_STATE_CHANGE)
//                        .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
//                        .withGameState(getGameStateMessage())
//                        .withPlayers(players).build(), Recipients.all(players.values()));
//            }
//        }
    }

    private void resetTable() {
        if (getActivePlayersStream().allMatch(p -> p.getStatus() == Player.Status.IN_GAME)) {
            state = GameDataProtos.GameState.State.IN_GAME;
            players.entrySet().removeIf(e -> !e.getValue().isActive());
            players.values().forEach(Player::reset);

            winnerPlayerId = null;
            drawingPlayerId = getNextPlayerIdToDraw();
            currentWord = getNextWordToGuess();
            drawableObjects.clear();
            setGameTimer(tableConfig.getRoundTime());

            callbacks.sendMessage( GameDataMessage.newBuilder(GameDataMessage.PLAYER_UPDATE
                    | GameDataMessage.CHAT_MESSAGE | GameDataMessage.GAME_STATE_CHANGE)
                    .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
                    .withGameState(getGameStateMessage(tableConfig.getRoundTime()))
                    .withPlayers(players).build(), Recipients.all(players.values()));
        }
    }

    private void setGameTimer(final int seconds) {
        inactivitySeconds = 0;
        if(gameTimer != null) {
            gameTimer.cancel();
        }
        if (seconds <= 0) {
            return;
        }
        gameTimer = new CountDownTimer((seconds + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentTimeLeft = (int) (millisUntilFinished / 1000);
                inactivitySeconds++;
                if (currentTimeLeft == seconds / 2) {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.hint(currentWord.getWord().substring(0, 1)))
                            .build(), Recipients.all(players.values()));
                }
                if (currentTimeLeft == seconds / 4) {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.hint(currentWord.getWord().substring(0, 2)))
                            .build(), Recipients.all(players.values()));
                }
                if (currentTimeLeft == seconds / 8) {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.littleTimeWarn())
                            .build(), Recipients.one(players.get(drawingPlayerId)));
                }
                int inactivityLimit = gameConfig.getDrawingPlayerInactivityLimitSeconds();
                if (inactivitySeconds == (inactivityLimit / 3) * 2) {
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.inactivityWarn())
                            .build(), Recipients.one(players.get(drawingPlayerId)));
                }
                if (inactivitySeconds >= inactivityLimit) {
                    updateRounds();
                    String lastPlayer = drawingPlayerId;
                    drawingPlayerId = getNextPlayerIdToDraw();
                    currentWord = getNextWordToGuess();
                    callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE
                            | GameDataMessage.CHAT_MESSAGE)
                            .addChatMessage(ChatMessage.playerInactive(players.get(lastPlayer).getUsername()))
                            .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
                            .withGameState(getGameStateMessage(tableConfig.getRoundTime()))
                            .build(), Recipients.all(players.values()));
                    setGameTimer(getTableConfig().getRoundTime());
                    drawableObjects.clear();
                }
            }

            @Override
            public void onFinish() {
                updateRounds();
                String lastWord = currentWord.getWord();
                drawingPlayerId = getNextPlayerIdToDraw();
                currentWord = getNextWordToGuess();
                callbacks.sendMessage(GameDataMessage.newBuilder(GameDataMessage.GAME_STATE_CHANGE
                        | GameDataMessage.CHAT_MESSAGE)
                        .addChatMessage(ChatMessage.timeIsUp())
                        .addChatMessage(ChatMessage.word(lastWord))
                        .addChatMessage(ChatMessage.playerDraw(players.get(drawingPlayerId).getUsername()))
                        .withGameState(getGameStateMessage(tableConfig.getRoundTime()))
                        .build(), Recipients.all(players.values()));
                setGameTimer(getTableConfig().getRoundTime());
                drawableObjects.clear();
            }
        };
        gameTimer.start();
    }

    private String getNextPlayerIdToDraw(@NonNull String guessingPlayerId) {
        inactivitySeconds = 0;
        if (tableConfig.getPlayerChooseMethod() == PlayerChooseMethod.GUESSING_PLAYER) {
            return guessingPlayerId;
        } else {
            return getNextPlayerIdToDraw();
        }
    }

    private String getNextPlayerIdToDraw() {
        Stream<Player> stream = getActivePlayersStream().filter(p -> !p.getId().equals(drawingPlayerId));

        inactivitySeconds = 0;
        switch (tableConfig.getPlayerChooseMethod()) {
            case RANDOM_PLAYER:
                List<Player> tempPlayers = stream.collect(Collectors.toList());
                return tempPlayers.get(random.nextInt(tempPlayers.size())).getId();
            case GUESSING_PLAYER:
            case LONGEST_WAITING_PLAYER:
                return stream.max(Comparator.comparing(Player::getRoundsSinceLastDraw)).get().getId();
            default:
                throw new IllegalArgumentException("Unknown player choose method.");
        }
    }

    private Stream<Player> getActivePlayersStream() {
        return players.values().stream().filter(Player::isActive);
    }

    public int getActivePlayersCount() {
        return (int) getActivePlayersStream().count();
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    private void updateRounds() {
        for(Player p : players.values()) {
            p.updateRoundsSinceLastDraw(1);
        }
        players.get(drawingPlayerId).setRoundsSinceLastDraw(0);
    }

    private Word getNextWordToGuess() {
        return wordsManager.drawWord(id);
    }

    private GameStateMessage getGameStateMessage(int currentTimeLeft) {
        return GameStateMessage.newBuilder(state)
                .setDrawingPlayerId(drawingPlayerId)
                .setOperatorPlayerId(operatorPlayerId)
                .setWinnerPlayerId(winnerPlayerId)
                .setWordToGuess(currentWord != null ? currentWord.getWord() : null)
                .setCategory(currentWord != null ? currentWord.getSetName() : null)
                .setTimeLeft(currentTimeLeft)
                .setRoundTime(tableConfig.getRoundTime())
                .setPointsLimit(tableConfig.getPointsLimit())
                .build();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    private String assignOperator(Player player) {
        if (tableConfig.getKind() != TableKind.DEFAULT) {
            player.setOperator(true);
            return player.getId();
        }
        return null;
    }

    public String getOperatorPlayerName() {
        if (!StringUtils.isEmpty(operatorPlayerId) && players.containsKey(operatorPlayerId)) {
            return players.get(operatorPlayerId).getUser().getNickname();
        } else {
            return null;
        }
    }

    void reset() {
        state = GameDataProtos.GameState.State.NO_PLAYERS;
        drawingPlayerId = null;
        operatorPlayerId = null;
        winnerPlayerId = null;
        if(gameTimer != null) {
            gameTimer.cancel();
        }
        players.clear();
        drawableObjects.clear();
        currentWord = null;
    }

    void destroy() {
        eventExecutor.shutdownNow();
        if(gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }
        players.clear();
        players = null;
        drawableObjects.clear();
        drawableObjects = null;
        currentWord = null;
        wordsManager = null;
        callbacks = null;
    }
}
