package com.ick.kalambury.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ick.kalambury.service.TableKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablesDto {

    @JsonProperty
    private Map<TableKind, List<TableDto>> tables;

    public TablesDto() { }

    private TablesDto(Builder builder) {
        tables = builder.tables;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Map<TableKind, List<TableDto>> tables;

        private Builder() {
            tables = new HashMap<>();
            for (TableKind kind : TableKind.values()) {
                tables.put(kind, new ArrayList<>());
            }
        }

        public Builder addTable(TableDto table) {
            this.tables.get(table.getKind()).add(table);
            return this;
        }

        public TablesDto build() {
            return new TablesDto(this);
        }
    }

    @Override
    public String toString() {
        return "TablesDto{" +
                "tables=" + tables +
                '}';
    }
}
