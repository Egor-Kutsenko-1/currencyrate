package com.yehor.kutsenko.currencyrate.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrencyConvertingMultiple {
    List<CurrencyConverting> conversionResult;
    String success;
    Error error;

    @Data
    @Builder
    @FieldDefaults(level = PRIVATE)
    public static class Error {
        Integer code;
        String info;
    }
}
