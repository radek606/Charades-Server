package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class UserNotFoundException extends RestClientException {

    public UserNotFoundException() {
        super("User not found");
    }

}
