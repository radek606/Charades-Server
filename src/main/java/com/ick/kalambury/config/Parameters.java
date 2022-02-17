package com.ick.kalambury.config;

import com.ick.kalambury.service.TableKind;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "parameters")
public class Parameters {

    private AdminUser adminUser;
    private ServerConfig serverConfig;
    private ClientConfig clientConfig;
    private GameConfig gameConfig;
    private LogStorage logStorage;
    private WordsStorage wordsStorage;

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public LogStorage getLogStorage() {
        return logStorage;
    }

    public void setLogStorage(LogStorage logStorage) {
        this.logStorage = logStorage;
    }

    public WordsStorage getWordsStorage() {
        return wordsStorage;
    }

    public void setWordsStorage(WordsStorage wordsStorage) {
        this.wordsStorage = wordsStorage;
    }

    public static class AdminUser {

        private String login;
        private String password;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ServerConfig {

        private int sendTimeLimitSeconds;
        private int messageSize;
        private int messageBufferSize;

        public int getSendTimeLimitSeconds() {
            return sendTimeLimitSeconds;
        }

        public void setSendTimeLimitSeconds(int sendTimeLimitSeconds) {
            this.sendTimeLimitSeconds = sendTimeLimitSeconds;
        }

        public int getMessageBufferSize() {
            return messageBufferSize;
        }

        public void setMessageBufferSize(int messageBufferSize) {
            this.messageBufferSize = messageBufferSize;
        }

        public int getMessageSize() {
            return messageSize;
        }

        public void setMessageSize(int messageSize) {
            this.messageSize = messageSize;
        }
    }

    public static class ClientConfig {

        private int minSupportedVersion;
        private String minSupportedVersionName;

        public int getMinSupportedVersion() {
            return minSupportedVersion;
        }

        public void setMinSupportedVersion(int minSupportedVersion) {
            this.minSupportedVersion = minSupportedVersion;
        }

        public String getMinSupportedVersionName() {
            return minSupportedVersionName;
        }

        public void setMinSupportedVersionName(String minSupportedVersionName) {
            this.minSupportedVersionName = minSupportedVersionName;
        }

    }

    public static class GameConfig {

        private String defaultTablesConfigPath;
        private int drawingPlayerInactivityLimitSeconds;
        private float wordsPairLengthDiffFactor;
        private float closeEnoughAnswerDistanceFactor;
        private Map<TableKind, TableNameConfig> tableNameConfig;

        public String getDefaultTablesConfigPath() {
            return defaultTablesConfigPath;
        }

        public void setDefaultTablesConfigPath(String defaultTablesConfigPath) {
            this.defaultTablesConfigPath = defaultTablesConfigPath;
        }

        public int getDrawingPlayerInactivityLimitSeconds() {
            return drawingPlayerInactivityLimitSeconds;
        }

        public void setDrawingPlayerInactivityLimitSeconds(int drawingPlayerInactivityLimitSeconds) {
            this.drawingPlayerInactivityLimitSeconds = drawingPlayerInactivityLimitSeconds;
        }

        public float getWordsPairLengthDiffFactor() {
            return wordsPairLengthDiffFactor;
        }

        public void setWordsPairLengthDiffFactor(float wordsPairLengthDiffFactor) {
            this.wordsPairLengthDiffFactor = wordsPairLengthDiffFactor;
        }

        public float getCloseEnoughAnswerDistanceFactor() {
            return closeEnoughAnswerDistanceFactor;
        }

        public void setCloseEnoughAnswerDistanceFactor(float closeEnoughAnswerDistanceFactor) {
            this.closeEnoughAnswerDistanceFactor = closeEnoughAnswerDistanceFactor;
        }

        public Map<TableKind, TableNameConfig> getTableNameConfig() {
            return tableNameConfig;
        }

        public void setTableNameConfig(Map<TableKind, TableNameConfig> tableNameConfig) {
            this.tableNameConfig = tableNameConfig;
        }

        public static class TableNameConfig {

            private String prefix;
            private int baseNumber;

            public String getPrefix() {
                return prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }

            public int getBaseNumber() {
                return baseNumber;
            }

            public void setBaseNumber(int baseNumber) {
                this.baseNumber = baseNumber;
            }
        }

    }

    public static class LogStorage {

        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

    }

    public static class WordsStorage {

        private String path;
        private String plaintextFile;
        private String encryptedFile;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPlaintextFile() {
            return plaintextFile;
        }

        public void setPlaintextFile(String plaintextFile) {
            this.plaintextFile = plaintextFile;
        }

        public String getEncryptedFile() {
            return encryptedFile;
        }

        public void setEncryptedFile(String encryptedFile) {
            this.encryptedFile = encryptedFile;
        }
    }

}
