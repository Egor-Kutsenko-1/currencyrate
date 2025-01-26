package com.yehor.kutsenko.currencyrate.client;


import com.yehor.kutsenko.currencyrate.dto.external.CurrencyConvertingExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;

public interface CurrencySource {

    CurrencyRatesExternalSourceResponse getExchangeRate(String source);

    CurrencyRatesExternalSourceResponse getRateForSpecificCurrencies(String currencyFrom, String currencyTo);

    CurrencyConvertingExternalSourceResponse convertCurrencyToAnother(String currencyFrom, String currencyTo, Double amount);

}
