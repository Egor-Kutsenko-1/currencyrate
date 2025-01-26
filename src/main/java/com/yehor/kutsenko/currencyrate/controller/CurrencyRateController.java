package com.yehor.kutsenko.currencyrate.controller;

import com.yehor.kutsenko.currencyrate.dto.response.CurrencyConvertingResponse;
import com.yehor.kutsenko.currencyrate.dto.response.CurrencyRatesResponse;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyConversionMapper;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import com.yehor.kutsenko.currencyrate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currencies/exchange")
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateController {

    private final CurrencyService currencyService;
    private final CurrencyRatesMapper currencyRatesMapper;
    private final CurrencyConversionMapper currencyConversionMapper;

    @GetMapping(value = "/{currency}/rate")
    public ResponseEntity<CurrencyRatesResponse> getAllCurrencyRates(@PathVariable String currency) {
        log.info("Get currency rate for {}", currency);

        CurrencyRates allExchangeRatesForCurrency = currencyService.getAllExchangeRatesForCurrency(currency);
        CurrencyRatesResponse currencyRatesResponse = currencyRatesMapper.toResponse(allExchangeRatesForCurrency);

        return currencyRatesResponse.getRates().isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(currencyRatesResponse);
    }

    @GetMapping(value = "/{currencyFrom}/rate/{currencyTo}")
    public ResponseEntity<CurrencyRatesResponse> getRateForSpecificCurrencies(
            @PathVariable String currencyFrom,
            @PathVariable String currencyTo) {

        log.info("Get currency rate for {} and {}", currencyFrom, currencyTo);

        CurrencyRates rate = currencyService.getRateForSpecificCurrencies(currencyFrom, currencyTo);
        CurrencyRatesResponse currencyRateResponse = currencyRatesMapper.toResponse(rate);

        return currencyRateResponse.getRates().isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(currencyRateResponse);
    }

    @GetMapping(value = "/{currencyFrom}/convert/{currencyTo}")
    public ResponseEntity<CurrencyConvertingResponse> convertCurrency(
            @PathVariable String currencyFrom,
            @PathVariable String currencyTo,
            @RequestParam Double amount) {

        log.info("Converting {} {} to {}", amount, currencyFrom, currencyTo);

        CurrencyConverting conversionResult = currencyService.convert(currencyFrom, currencyTo, amount);
        CurrencyConvertingResponse currencyConversionResponse = currencyConversionMapper.toResponse(conversionResult);

        return currencyConversionResponse.getInfo() == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(currencyConversionResponse);
    }
}
