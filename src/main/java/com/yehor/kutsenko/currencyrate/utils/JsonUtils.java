package com.yehor.kutsenko.currencyrate.utils;

import lombok.SneakyThrows;

import static com.yehor.kutsenko.currencyrate.config.Config.OBJECT_MAPPER;

public class JsonUtils {

    @SneakyThrows
    public static String getJsonString(Object object) {
        return OBJECT_MAPPER.writeValueAsString(object);
    }
}
