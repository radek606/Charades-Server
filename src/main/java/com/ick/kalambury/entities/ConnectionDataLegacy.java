package com.ick.kalambury.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;

public class ConnectionDataLegacy {

    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String uuid;

    @JsonProperty
    private String nickname;

    @JsonProperty
    private int version;

    public static ConnectionDataLegacy parse(ObjectMapper mapper, String encoded) throws IOException, IllegalArgumentException {
        return mapper.readValue(Base64.getDecoder().decode(encoded), ConnectionDataLegacy.class);
    }

    public ConnectionDataLegacy() { }

    public String getEndpoint() {
        return endpoint;
    }

    public String getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public int getVersion() {
        return version;
    }

}
