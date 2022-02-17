package com.ick.kalambury.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ick.kalambury.security.Role;
import com.ick.kalambury.storage.RedisSimpleEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

public class User implements RedisSimpleEntity {

    @JsonProperty
    private String nickname;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String password;

    @JsonProperty
    private String email;

    @JsonProperty
    private Role role;

    @JsonProperty
    private String tableId;

    public User() {}

    @JsonCreator
    public User(@JsonProperty("nickname") String nickname,
                @JsonProperty("userId") String userId,
                @JsonProperty("password") String password,
                @JsonProperty("email") String email,
                @JsonProperty("role") Role role,
                @JsonProperty("tableId") String tableId) {
        this.nickname = nickname;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.role = role;
        this.tableId = tableId;
    }

    private User(Builder builder) {
        this.nickname = builder.nickname;
        this.userId = builder.userId;
        this.password = builder.password;
        this.email = builder.email;
        this.role = builder.role;
        this.tableId = builder.tableId;
    }

    public static Builder newBuilder(PasswordEncoder encoder) {
        return new Builder(encoder);
    }

    public Builder toBuilder(PasswordEncoder encoder) {
        Builder builder = new Builder(encoder);
        builder.userId = getUserId();
        builder.nickname = getNickname();
        builder.password = getPassword();
        builder.email = getEmail();
        builder.role = getRole();
        builder.tableId = getTableId();
        return builder;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return getUserId();
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public Role getRole() {
        return role;
    }

    public String getTableId() {
        return tableId;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", userId='" + userId + '\'' +
                ", password=[redacted]" +
                ", email=[redacted]" +
                ", role=" + role +
                ", tableId='" + tableId + '\'' +
                '}';
    }

    public static final class Builder {

        private final PasswordEncoder encoder;

        private String nickname;
        private String userId;
        private String password;
        private String email;
        private Role role;
        private String tableId;

        private Builder(PasswordEncoder encoder) {
            this.encoder = encoder;
        }

        public Builder setNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setPassword(String password) {
            if (encoder == null) throw new RuntimeException("password encoder is null!");

            this.password = encoder.encode(password);
            return this;
        }

        public Builder setEmail(String email) {
            if (encoder == null) throw new RuntimeException("password encoder is null!");

            this.email = encoder.encode(email);
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setTableId(String tableId) {
            this.tableId = tableId;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

}
