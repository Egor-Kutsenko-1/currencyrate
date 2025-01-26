package com.yehor.kutsenko.currencyrate.service;

import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;

public interface CurrencyService {

    CurrencyRates getAllExchangeRatesForCurrency(String currency);

    CurrencyRates getRateForSpecificCurrencies(String currencyFrom, String currencyTo);

    CurrencyConverting convert (String currencyFrom, String currencyTo, Double amount);

}
