package com.ick.kalambury.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.Date;

public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    @JsonProperty
    private int code;

    @JsonProperty
    private String status;

    @JsonProperty
    private String message;

    public ErrorResponse(Date timestamp, HttpStatus status, String message) {
        this.timestamp = timestamp;
        this.code = status.value();
        this.status = status.getReasonPhrase();
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
