package com.ick.kalambury.api.converters;

import com.ick.kalambury.api.TablesDto;
import com.ick.kalambury.service.Table;

import java.util.Collection;

public class TablesConverter implements BaseConverter<Collection<Table>, TablesDto> {

    @Override
    public TablesDto convert(Collection<Table> tables) {
        TablesDto.Builder builder = TablesDto.newBuilder();
        tables.forEach(t -> builder.addTable(new TableConverter().convert(t)));
        return builder.build();


//        Map<TableKind, List<TableDto>> dto = new HashMap<>();
//        for (TableKind kind : TableKind.values()) {
//            dto.put(kind, new ArrayList<>());
//        }
//        tables.forEach(t -> dto.get(t.getTableConfig().getKind()).add(new TableConverter().convert(t)));
//        return dto;
    }

}
