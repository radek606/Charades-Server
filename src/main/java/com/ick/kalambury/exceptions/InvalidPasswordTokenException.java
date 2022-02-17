package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class InvalidPasswordTokenException extends RestClientException {

    public InvalidPasswordTokenException() {
        super("Reset password token is invalid.");
    }

}
