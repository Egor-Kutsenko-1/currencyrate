package com.yehor.kutsenko.currencyrate.client;


import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;

public interface CurrencySource {

    CurrencyRatesExternalSourceResponse getExchangeRate(String source);

}
