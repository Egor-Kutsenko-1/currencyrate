package com.yehor.kutsenko.currencyrate.integration.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.yehor.kutsenko.currencyrate.CurrencyRateApplicationTests;
import com.yehor.kutsenko.currencyrate.dto.external.CurrencyConvertingExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CurrencyExchangeRateControllerIntegrationTest extends CurrencyRateApplicationTests {

    private static final String ACCESS_KEY = "test";
    private static final String QUERY_PARAM_ACCESS_KEY = "access_key";
    private static final String QUERY_PARAM_SOURCE = "source";
    private static final String QUERY_PARAM_CURRENCY_FROM = "from";
    private static final String QUERY_PARAM_CURRENCY_TO = "to";
    private static final String QUERY_PARAM_AMOUNT = "amount";

    @Test
    void whenSearchRateForSpecificCurrency_thenShouldReturnCorrectResponse() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        String jsonResponse = objectMapper.writeValueAsString(
                CurrencyRatesExternalSourceResponse.builder()
                        .timestamp(Instant.now())
                        .quotes(Map.of(
                                "UAHPLN", 5.31,
                                "UAHEUR", 41.57,
                                "UAHGPB", 0.89
                        ))
                        .build()
        );

        EXTERNAL_CURRENCY_SOURCE.stubFor(WireMock.get(WireMock.urlPathEqualTo("/live"))
                .withQueryParam(QUERY_PARAM_ACCESS_KEY, WireMock.equalTo(ACCESS_KEY))
                .withQueryParam(QUERY_PARAM_SOURCE, WireMock.equalTo("UAH"))
                .willReturn(WireMock.okJson(jsonResponse)));

        mockMvc.perform(get(BASE_URL + "/currencies/exchange/UAH/rate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rates.size()", is(3)))
                .andExpect(jsonPath("$.rates.UAHPLN", is(5.31)))
                .andExpect(jsonPath("$.rates.UAHEUR", is(41.57)))
                .andExpect(jsonPath("$.rates.UAHGPB", is(0.89)));
    }

    @Test
    void whenSearchRateBetweenTwoCurrencies_thenShouldReturnCorrectResponse() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        String jsonResponse = objectMapper.writeValueAsString(
                CurrencyRatesExternalSourceResponse.builder()
                        .timestamp(Instant.now())
                        .quotes(Map.of(
                                "USDEUR", 0.91
                        ))
                        .build()
        );

        EXTERNAL_CURRENCY_SOURCE.stubFor(WireMock.get(WireMock.urlPathEqualTo("/live"))
                .withQueryParam(QUERY_PARAM_ACCESS_KEY, WireMock.equalTo(ACCESS_KEY))
                .withQueryParam(QUERY_PARAM_SOURCE, WireMock.equalTo("USD"))
                .withQueryParam("currencies", WireMock.equalTo("EUR"))
                .willReturn(WireMock.okJson(jsonResponse)));

        mockMvc.perform(get(BASE_URL + "/currencies/exchange/USD/rate/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rates.size()", is(1)))
                .andExpect(jsonPath("$.rates.USDEUR", is(0.91)));
    }

    @Test
    void whenConvertCurrencyToAnother_thenShouldReturnCorrectResponse() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        Instant now = Instant.now();
        String jsonResponse = objectMapper.writeValueAsString(
                CurrencyConvertingExternalSourceResponse.builder()
                        .result(0.23903)
                        .info(CurrencyConvertingExternalSourceResponse.Info.builder()
                                .timestamp(now)
                                .quote(0.023903)
                                .build())
                        .build()
        );

        EXTERNAL_CURRENCY_SOURCE.stubFor(WireMock.get(WireMock.urlPathEqualTo("/convert"))
                .withQueryParam(QUERY_PARAM_ACCESS_KEY, WireMock.equalTo(ACCESS_KEY))
                .withQueryParam(QUERY_PARAM_CURRENCY_FROM, WireMock.equalTo("UAH"))
                .withQueryParam(QUERY_PARAM_CURRENCY_TO, WireMock.equalTo("USD"))
                .withQueryParam(QUERY_PARAM_AMOUNT, WireMock.equalTo("10.0"))
                .willReturn(WireMock.okJson(jsonResponse)));

        mockMvc.perform(get(BASE_URL + "/currencies/exchange/UAH/convert/USD?amount=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(0.23903)))
                .andExpect(jsonPath("$.info.quote", is(0.023903)));
    }


    @Test
    void whenConvertCurrencyToMultipleCurrencies_thenShouldReturnCorrectConversion() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        String jsonResponse = objectMapper.writeValueAsString(
                CurrencyRatesExternalSourceResponse.builder()
                        .success("true")
                        .timestamp(Instant.now())
                        .quotes(Map.of(
                                "UAHUSD", 0.025,
                                "UAHEUR", 0.020
                        ))
                        .build()
        );

        EXTERNAL_CURRENCY_SOURCE.stubFor(WireMock.get(WireMock.anyUrl())
                .willReturn(WireMock.okJson(jsonResponse)));

        mockMvc.perform(get(BASE_URL + "/currencies/exchange/UAH/convert?currenciesTo=EUR,USD&amount=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversionResult.size()", is(2)))
                .andExpect(jsonPath("$.conversionResult.[0].info.quote", is(0.02)))
                .andExpect(jsonPath("$.conversionResult.[0].result", is(2.0)))
                .andExpect(jsonPath("$.conversionResult.[0].query.from", is("UAH")))
                .andExpect(jsonPath("$.conversionResult.[0].query.to", is("EUR")))
                .andExpect(jsonPath("$.conversionResult.[1].info.quote", is(0.025)))
                .andExpect(jsonPath("$.conversionResult.[1].result", is(2.5)))
                .andExpect(jsonPath("$.conversionResult.[1].query.from", is("UAH")))
                .andExpect(jsonPath("$.conversionResult.[1].query.to", is("USD")));
    }
}
