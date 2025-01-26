package com.yehor.kutsenko.currencyrate.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrencyConverting {

    Info info;
    Double result;

    @Data
    @Builder
    @FieldDefaults(level = PRIVATE)
    public static class Info {
        Instant timestamp;
        Double quote;
    }
}
