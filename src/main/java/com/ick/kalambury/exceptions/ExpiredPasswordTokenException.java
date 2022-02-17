package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class ExpiredPasswordTokenException extends RestClientException {

    public ExpiredPasswordTokenException() {
        super("Reset password token has expired.");
    }

}
