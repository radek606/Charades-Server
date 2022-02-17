package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class InvalidEmailException extends RestClientException {

    public InvalidEmailException(String email) {
        super("Invalid email: " + email);
    }

}
