package com.yehor.kutsenko.currencyrate.service.impl;


import com.yehor.kutsenko.currencyrate.client.CurrencySource;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyConversionMapper;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import com.yehor.kutsenko.currencyrate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRatesMapper currencyRatesMapper;
    private final CurrencyConversionMapper currencyConversionMapper;
    private final CurrencySource currencySource;

    public CurrencyRates getAllExchangeRatesForCurrency(String baseCurrency, String currenciesToRate) {


        var exchangeRateResponse = currencySource.getExchangeRate(baseCurrency, currenciesToRate);

        var currencyRates = currencyRatesMapper.toModel(exchangeRateResponse);

        log.info("Rates for baseCurrency {}: \n {}", baseCurrency, currencyRates.getRates());

        return currencyRates;
    }

    @Override
    public CurrencyRates getRateForSpecificCurrencies(String currencyFrom, String currencyTo) {

        var exchangeRateResponse = currencySource.getRateForSpecificCurrencies(currencyFrom, currencyTo);

        var currencyRates = currencyRatesMapper.toModel(exchangeRateResponse);

        log.info("Rate between {} and {}: \n {}", currencyFrom, currencyTo, currencyRates.getRates());

        return currencyRates;
    }

    @Override
    public CurrencyConverting convert(String currencyFrom, String currencyTo, Double amount) {


        var exchangeRateResponse = currencySource.convertCurrencyToAnother(currencyFrom, currencyTo, amount);

        var conversionResult = currencyConversionMapper.toModel(exchangeRateResponse);

        log.info("The result of converting {} {} to {} is {} {}",
                amount, currencyFrom, currencyTo, conversionResult.getResult(), currencyTo);

        return conversionResult;
    }

    @Override
    public List<CurrencyConverting> convertMultiple(String currencyFrom, List<String> currenciesToRate, Double amount) {


        CurrencyRates rates = getRates(currencyFrom, currenciesToRate);
        return convertAll(rates, amount);
    }


    private CurrencyRates getRates(String baseCurrency, List<String> currenciesToRate) {
        if (currenciesToRate != null) {
            String stringCurrencies = String.join(",", currenciesToRate);
            return getAllExchangeRatesForCurrency(baseCurrency, stringCurrencies);
        } else {
            return null;
        }
    }

    private List<CurrencyConverting> convertAll(CurrencyRates rates, Double amount) {
        return rates.getRates().entrySet().stream()
                .map(entry -> buildCurrencyConverting(entry.getKey(), entry.getValue(), amount))
                .sorted(Comparator.comparing(c -> c.getQuery().getTo()))
                .toList();
    }

    private CurrencyConverting buildCurrencyConverting(String currencies, Double rate, Double amount) {
        Instant now = Instant.now();

        return CurrencyConverting.builder()
                .info(CurrencyConverting.Info.builder()
                        .timestamp(now)
                        .quote(rate)
                        .build())
                .result(rate * amount)
                .query(CurrencyConverting.Query.builder()
                        .from(currencies.substring(0, 3))
                        .to(currencies.substring(3))
                        .amount(amount)
                        .build())
                .build();
    }
}
