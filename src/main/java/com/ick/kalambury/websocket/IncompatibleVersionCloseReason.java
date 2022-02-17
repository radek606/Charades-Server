package com.ick.kalambury.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IncompatibleVersionCloseReason {

    @JsonProperty
    private int minVersion;

    @JsonProperty
    private String minVersionName;

    public IncompatibleVersionCloseReason() {
    }

    public IncompatibleVersionCloseReason(int minVersion, String minVersionName) {
        this.minVersion = minVersion;
        this.minVersionName = minVersionName;
    }

    public void setMinVersion(int minVersion) {
        this.minVersion = minVersion;
    }

    public void setMinVersionName(String minVersionName) {
        this.minVersionName = minVersionName;
    }

}
