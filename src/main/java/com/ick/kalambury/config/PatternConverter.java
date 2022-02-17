package com.ick.kalambury.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@ConfigurationPropertiesBinding
public class PatternConverter implements Converter<String, Pattern> {

    @Override
    public Pattern convert(@NonNull String source) {
        return Pattern.compile(source);
    }

}
