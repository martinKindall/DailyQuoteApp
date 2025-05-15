package org.morsaprogramando.app_quotes.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperConfig {

    public ObjectMapper create() {
        return new ObjectMapper();
    }
}
