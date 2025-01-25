package com.yehor.kutsenko.currencyrate.service.impl;


import com.yehor.kutsenko.currencyrate.client.CurrencySource;
import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import com.yehor.kutsenko.currencyrate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRatesMapper currencyRatesMapper;
    private final CurrencySource currencySource;

    public CurrencyRates getAllExchangeRatesForCurrency(String currency) {

        CurrencyRatesExternalSourceResponse exchangeRateResponse = currencySource.getExchangeRate(currency);

        CurrencyRates currencyRates = currencyRatesMapper.toModel(exchangeRateResponse);

        log.info("Rates for currency {}: \n {}", currency, currencyRates.getRates());

        return currencyRates;
    }
}
