package com.yehor.kutsenko.currencyrate.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrencyConvertingResponse {

    String success;
    Info info;
    Double result;
    Query query;
    Error error;

    @Data
    @Builder
    @FieldDefaults(level = PRIVATE)
    public static class Info {
        Instant timestamp;
        Double quote;
    }

    @Data
    @Builder
    @FieldDefaults(level = PRIVATE)
    public static class Query {
        String from;
        String to;
        Double amount;
    }

    @Data
    @Builder
    @FieldDefaults(level = PRIVATE)
    public static class Error {
        Integer code;
        String info;
    }
}
