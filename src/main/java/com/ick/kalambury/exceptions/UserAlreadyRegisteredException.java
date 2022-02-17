package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class UserAlreadyRegisteredException extends RestClientException {

    public UserAlreadyRegisteredException(String nickname) {
        super("User " + nickname + " already registered");
    }

}
