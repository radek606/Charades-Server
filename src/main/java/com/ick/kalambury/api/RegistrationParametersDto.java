package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ick.kalambury.validation.ValidEmail;

import javax.validation.constraints.NotBlank;

public class RegistrationParametersDto {

    @NotBlank
    @JsonProperty
    private String nickname;

    @NotBlank
    @JsonProperty
    private String password;

    @NotBlank
    @JsonProperty
    private String matchingPassword;

    @ValidEmail
    @JsonProperty
    private String email;

    public RegistrationParametersDto() { }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public String getEmail() {
        return email;
    }

}
