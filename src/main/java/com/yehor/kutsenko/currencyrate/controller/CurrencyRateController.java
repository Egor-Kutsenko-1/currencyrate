package com.yehor.kutsenko.currencyrate.controller;

import com.yehor.kutsenko.currencyrate.cache.RedisProvider;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyConversionMapper;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import com.yehor.kutsenko.currencyrate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.yehor.kutsenko.currencyrate.utils.JsonUtils.getJsonString;

@RestController
@RequestMapping("/api/v1/currencies/exchange")
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateController {

    private final CurrencyService currencyService;
    private final CurrencyRatesMapper currencyRatesMapper;
    private final CurrencyConversionMapper currencyConversionMapper;
    private final RedisProvider redisProvider;

    @Value("${application.redis.ttl}")
    private Duration redisTTL;

    @GetMapping(value = "/{currency}/rate")
    public ResponseEntity<String> getAllCurrencyRates(@PathVariable String currency) {
        log.info("Get currency rate for {}", currency);

        String key = redisProvider.buildKey(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                + "getAllCurrencyRates" + currency);

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyRates allExchangeRatesForCurrency = currencyService.getAllExchangeRatesForCurrency(currency, null);
            return getJsonString(currencyRatesMapper.toResponse(allExchangeRatesForCurrency));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @GetMapping(value = "/{currencyFrom}/rate/{currencyTo}")
    public ResponseEntity<String> getRateForSpecificCurrencies(
            @PathVariable String currencyFrom,
            @PathVariable String currencyTo) {

        log.info("Get currency rate for {} and {}", currencyFrom, currencyTo);

        String key = redisProvider.buildKey(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                + "getRateForSpecificCurrencies" + currencyFrom + currencyTo);

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyRates rate = currencyService.getRateForSpecificCurrencies(currencyFrom, currencyTo);
            return getJsonString(currencyRatesMapper.toResponse(rate));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @GetMapping(value = "/{currencyFrom}/convert/{currencyTo}")
    public ResponseEntity<String> convertCurrency(
            @PathVariable String currencyFrom,
            @PathVariable String currencyTo,
            @RequestParam Double amount) {

        log.info("Converting {} {} to {}", amount, currencyFrom, currencyTo);

        String key = redisProvider.buildKey(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                + "convertCurrency" + currencyFrom + currencyTo + amount);

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyConverting conversionResult = currencyService.convert(currencyFrom, currencyTo, amount);
            return getJsonString(currencyConversionMapper.toResponse(conversionResult));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @GetMapping(value = "/{currencyFrom}/convert")
    public ResponseEntity<String> convertMultipleCurrencies(
            @PathVariable String currencyFrom,
            @RequestParam List<String> currenciesTo,
            @RequestParam Double amount) {

        log.info("Converting {} {} to {}", amount, currencyFrom, currenciesTo);

        String key = redisProvider.buildKey(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                + "convertMultipleCurrencies" + currencyFrom + String.join("", currenciesTo) + amount);

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            List<CurrencyConverting> conversionResult = currencyService.convertMultiple(currencyFrom, currenciesTo, amount);
            return getJsonString(currencyConversionMapper.toResponse(conversionResult));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
}
