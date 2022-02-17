package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class ChangedPasswordDto {

    @NotBlank
    @JsonProperty
    private String oldPassword;

    @NotBlank
    @JsonProperty
    private String password;

    @NotBlank
    @JsonProperty
    private String matchingPassword;

    public ChangedPasswordDto() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    @Override
    public String toString() {
        return "PasswordDto{" +
                "oldPassword=[redacted]" +
                ", password=[redacted]" +
                ", matchingPassword=[redacted]" +
                '}';
    }
}
