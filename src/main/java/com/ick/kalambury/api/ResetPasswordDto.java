package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class ResetPasswordDto {

    @NotBlank
    @JsonProperty
    private String resetToken;

    @NotBlank
    @JsonProperty
    private String password;

    @NotBlank
    @JsonProperty
    private String matchingPassword;

    public ResetPasswordDto() {
    }

    public String getResetToken() {
        return resetToken;
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
                "resetToken='" + resetToken + '\'' +
                ", password=[redacted]" +
                ", matchingPassword=[redacted]" +
                '}';
    }
}
