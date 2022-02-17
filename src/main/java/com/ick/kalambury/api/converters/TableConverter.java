package com.ick.kalambury.api.converters;

import com.ick.kalambury.api.TableDto;
import com.ick.kalambury.service.Table;

public class TableConverter implements BaseConverter<Table, TableDto> {

    @Override
    public TableDto convert(Table from) {
        return TableDto.fromConfig(from.getTableConfig())
                .setId(from.getId())
                .setName(from.getName())
                .setOperatorName(from.getOperatorPlayerName())
                .setPlayersCount(from.getActivePlayersCount())
                .build();
    }

}
