package com.yehor.kutsenko.currencyrate.integration.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.yehor.kutsenko.currencyrate.CurrencyRateApplicationTests;
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
    private static final String QUERY_PARAM_ACCESS_SOURCE = "source";

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
                .withQueryParam(QUERY_PARAM_ACCESS_SOURCE, WireMock.equalTo("UAH"))
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
                .withQueryParam(QUERY_PARAM_ACCESS_SOURCE, WireMock.equalTo("USD"))
                .withQueryParam("currencies", WireMock.equalTo("EUR"))
                .willReturn(WireMock.okJson(jsonResponse)));

        mockMvc.perform(get(BASE_URL + "/currencies/exchange/USD/rate/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rates.size()", is(1)))
                .andExpect(jsonPath("$.rates.USDEUR", is(0.91)));
    }
}
