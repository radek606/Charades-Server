package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public final class InvalidOldPasswordException extends RestClientException {

    public InvalidOldPasswordException() {
        super("Incorrect old password");
    }

}
