package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TableIdDto {

    @JsonProperty
    private String tableId;

    @JsonProperty
    private String tableName;

    public TableIdDto() { }

    public TableIdDto(String tableId, String tableName) {
        this.tableId = tableId;
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "TableIdDto{" +
                "tableId='" + tableId + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }

}
