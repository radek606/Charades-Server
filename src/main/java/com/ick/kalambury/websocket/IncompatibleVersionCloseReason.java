package com.ick.kalambury.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IncompatibleVersionCloseReason {

    @JsonProperty
    private int minVersionCode;

    @JsonProperty
    private String minVersionName;

    public IncompatibleVersionCloseReason() {
    }

    public IncompatibleVersionCloseReason(int minVersionCode, String minVersionName) {
        this.minVersionCode = minVersionCode;
        this.minVersionName = minVersionName;
    }

    public void setMinVersionCode(int minVersionCode) {
        this.minVersionCode = minVersionCode;
    }

    public void setMinVersionName(String minVersionName) {
        this.minVersionName = minVersionName;
    }

}
