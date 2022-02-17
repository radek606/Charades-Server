package com.ick.kalambury.api.converters;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
interface BaseConverter<F, T> {

    T convert(F from);

    default List<T> convertAll(Collection<F> elements){
        return elements.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

}
