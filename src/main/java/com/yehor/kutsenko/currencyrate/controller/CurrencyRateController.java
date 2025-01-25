package com.yehor.kutsenko.currencyrate.controller;

import com.yehor.kutsenko.currencyrate.dto.response.CurrencyRatesResponse;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import com.yehor.kutsenko.currencyrate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currencies/exchange")
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateController {

    private final CurrencyService currencyService;
    private final CurrencyRatesMapper currencyRatesMapper;

    @GetMapping(value = "/{currency}/rate")
    public ResponseEntity<CurrencyRatesResponse> getAllCurrencyRates(@PathVariable String currency) {
        log.info("Get currency rate for {}", currency);

        CurrencyRates allExchangeRatesForCurrency = currencyService.getAllExchangeRatesForCurrency(currency);
        CurrencyRatesResponse currencyRatesResponse = currencyRatesMapper.toResponse(allExchangeRatesForCurrency);

        return currencyRatesResponse.getRates().isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(currencyRatesResponse);
    }
}
