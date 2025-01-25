package com.yehor.kutsenko.currencyrate.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class CurrencyRates {

    Instant timestamp;
    String source;
    Map<String, Double> rates;

}
