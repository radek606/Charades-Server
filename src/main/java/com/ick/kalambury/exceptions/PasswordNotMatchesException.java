package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class PasswordNotMatchesException extends RestClientException {

    public PasswordNotMatchesException() {
        super("Password doesn't match.");
    }
}
