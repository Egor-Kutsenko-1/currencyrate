package com.yehor.kutsenko.currencyrate.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrencyConvertingMultipleResponse {

    String success;
    List<CurrencyConvertingResponse> conversionResult;
    Error error;

    @Data
    @Builder
    @FieldDefaults(level = PRIVATE)
    public static class Error {
        Integer code;
        String info;
    }
}
