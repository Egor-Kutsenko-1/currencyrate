package com.yehor.kutsenko.currencyrate.controller;

import com.yehor.kutsenko.currencyrate.cache.RedisProvider;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyConversionMapper;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyConvertingMultiple;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import com.yehor.kutsenko.currencyrate.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Currency Exchange", description = "Endpoints for fetching and converting currency exchange rates.")
public class CurrencyRateController {

    private final CurrencyService currencyService;
    private final CurrencyRatesMapper currencyRatesMapper;
    private final CurrencyConversionMapper currencyConversionMapper;
    private final RedisProvider redisProvider;

    @Value("${application.redis.ttl}")
    private Duration redisTTL;

    @Operation(
            summary = "Get all currency rates for the base currency",
            description = """
            Fetches currency exchange rates from an external source for the specified base currency.
            Results may be cached to reduce external API calls.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of all currency rates",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", description = "JSON string with currency rates"))
            )
    })
    @GetMapping(value = "/{currency}/rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAllCurrencyRates(
            @Parameter(description = "Three-letter currency code, e.g. 'USD'")
            @PathVariable String currency) {

        log.info("Get currency rate for {}", currency);

        String key = redisProvider.buildKey(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) + "getAllCurrencyRates" + currency
        );

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyRates allExchangeRatesForCurrency = currencyService.getAllExchangeRatesForCurrency(currency, null);
            return getJsonString(currencyRatesMapper.toResponse(allExchangeRatesForCurrency));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @Operation(
            summary = "Get rate between two specific currencies",
            description = """
            Returns the exchange rate between the given currencyFrom and currencyTo.
            The result may be cached to reduce external API calls.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of specific currency rate",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", description = "JSON string with specific currency rate"))
            )
    })
    @GetMapping(value = "/{currencyFrom}/rate/{currencyTo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRateForSpecificCurrencies(
            @Parameter(description = "Base currency code, e.g. 'USD'")
            @PathVariable String currencyFrom,
            @Parameter(description = "Target currency code, e.g. 'EUR'")
            @PathVariable String currencyTo) {

        log.info("Get currency rate for {} and {}", currencyFrom, currencyTo);

        String key = redisProvider.buildKey(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) + "getRateForSpecificCurrencies" + currencyFrom + currencyTo
        );

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyRates rate = currencyService.getRateForSpecificCurrencies(currencyFrom, currencyTo);
            return getJsonString(currencyRatesMapper.toResponse(rate));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @Operation(
            summary = "Convert from one currency to another",
            description = """
            Converts the specified amount from 'currencyFrom' to 'currencyTo'.
            The result may be cached to reduce external API calls.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful conversion from one currency to another",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", description = "JSON string with conversion result"))
            )
    })
    @GetMapping(value = "/{currencyFrom}/convert/{currencyTo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> convertCurrency(
            @Parameter(description = "Currency to convert from, e.g. 'USD'")
            @PathVariable String currencyFrom,
            @Parameter(description = "Currency to convert to, e.g. 'EUR'")
            @PathVariable String currencyTo,
            @Parameter(description = "Amount to convert, e.g. '100.5'")
            @RequestParam Double amount) {

        log.info("Converting {} {} to {}", amount, currencyFrom, currencyTo);

        String key = redisProvider.buildKey(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) + "convertCurrency" + currencyFrom + currencyTo + amount
        );

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyConverting conversionResult = currencyService.convert(currencyFrom, currencyTo, amount);
            return getJsonString(currencyConversionMapper.toResponse(conversionResult));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    @Operation(
            summary = "Convert a list of currencies from one base currency",
            description = """
            Converts the specified amount from 'currencyFrom' to multiple target currencies ('currenciesTo').
            The result may be cached to reduce external API calls.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful conversion to multiple target currencies",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", description = "JSON string with multiple conversion results"))
            )
    })
    @GetMapping(value = "/{currencyFrom}/convert", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> convertMultipleCurrencies(
            @Parameter(description = "Currency to convert from, e.g. 'USD'")
            @PathVariable String currencyFrom,
            @Parameter(description = "List of target currency codes, e.g. ['EUR', 'UAH']")
            @RequestParam List<String> currenciesTo,
            @Parameter(description = "Amount to convert, e.g. '100.5'")
            @RequestParam Double amount) {

        log.info("Converting {} {} to {}", amount, currencyFrom, currenciesTo);

        String key = redisProvider.buildKey(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) + "convertMultipleCurrencies"
                        + currencyFrom + String.join("", currenciesTo) + amount
        );

        String result = redisProvider.getOrAdd(key, redisTTL, () -> {
            CurrencyConvertingMultiple conversionResult = currencyService.convertMultiple(currencyFrom, currenciesTo, amount);
            return getJsonString(currencyConversionMapper.toResponse(conversionResult));
        });

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }
}
