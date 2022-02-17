package com.ick.kalambury.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ick.kalambury.storage.RedisSimpleEntity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class PasswordResetToken implements RedisSimpleEntity {

    private static final int EXPIRATION = 60 * 24;

    @JsonProperty
    private String token;

    @JsonProperty
    private String username;

    @JsonProperty
    private Date expiryDate;

    public PasswordResetToken(String token, String username) {
        this.token = token;
        this.username = username;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    @Override
    @JsonIgnore
    public String getId() {
        return getToken();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        return new Date(LocalDateTime.now()
                .plus(expiryTimeInMinutes, ChronoUnit.MINUTES)
                .toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
