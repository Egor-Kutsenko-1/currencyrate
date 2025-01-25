package com.yehor.kutsenko.currencyrate.client;

import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@ConditionalOnProperty(prefix = "application", name = "source", havingValue = "exchangerate")
@FeignClient(
        name = "exchangerate",
        url = "${api.exchangerate.url}")
public interface ExchangeRateClient extends CurrencySource {

    @GetMapping(value = "/live?access_key=${api.exchangerate.key}")
    CurrencyRatesExternalSourceResponse getExchangeRate(@RequestParam("source") String source);

    @GetMapping(value = "/live?access_key=${api.exchangerate.key}")
    CurrencyRatesExternalSourceResponse getRateForSpecificCurrencies(
            @RequestParam("source") String currencyFrom,
            @RequestParam("currencies") String currencyTo);

}
