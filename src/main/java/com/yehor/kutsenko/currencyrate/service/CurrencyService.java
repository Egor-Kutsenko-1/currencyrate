package com.yehor.kutsenko.currencyrate.service;

import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;

import java.util.List;

public interface CurrencyService {

    CurrencyRates getAllExchangeRatesForCurrency(String currency, String currenciesToRate);

    CurrencyRates getRateForSpecificCurrencies(String currencyFrom, String currencyTo);

    CurrencyConverting convert (String currencyFrom, String currencyTo, Double amount);

    List<CurrencyConverting> convertMultiple(String currencyFrom, List<String> currenciesTo, Double amount);
}
