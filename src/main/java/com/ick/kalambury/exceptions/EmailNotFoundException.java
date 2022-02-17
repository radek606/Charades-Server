package com.ick.kalambury.exceptions;

import org.springframework.web.client.RestClientException;

public class EmailNotFoundException extends RestClientException {

    public EmailNotFoundException() {
        super("Email not found");
    }

}
