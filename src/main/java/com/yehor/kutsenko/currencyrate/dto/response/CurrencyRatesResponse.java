package com.yehor.kutsenko.currencyrate.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrencyRatesResponse {

    Instant timestamp;
    String source;
    Map<String, Double> rates;

}
