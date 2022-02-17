package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountDto {

    @JsonProperty
    private String nickname;

    @JsonProperty
    private String password;

    public AccountDto() {
    }

    public AccountDto(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "nickname='" + nickname + '\'' +
                ", password=[redacted]" +
                '}';
    }
}
