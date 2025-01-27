package com.yehor.kutsenko.currencyrate.service.impl;


import com.yehor.kutsenko.currencyrate.client.CurrencySource;
import com.yehor.kutsenko.currencyrate.dto.external.CurrencyConvertingExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyConversionMapper;
import com.yehor.kutsenko.currencyrate.mapper.CurrencyRatesMapper;
import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRatesMapper currencyRatesMapper;
    @Mock
    private CurrencyConversionMapper currencyConversionMapper;
    @Mock
    private CurrencySource currencySource;
    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    void testGetAllExchangeRatesForCurrency() {

        String baseCurrency = "USD";
        String currenciesToRate = "EUR,UAH";
        CurrencyRatesExternalSourceResponse mockResponse = new CurrencyRatesExternalSourceResponse();
        CurrencyRates mockRates = new CurrencyRates();
        mockRates.setRates(Map.of("USDEUR", 0.92, "USDUAH", 37.0));

        when(currencySource.getExchangeRate(baseCurrency, currenciesToRate)).thenReturn(mockResponse);
        when(currencyRatesMapper.toModel(mockResponse)).thenReturn(mockRates);

        CurrencyRates result = currencyService.getAllExchangeRatesForCurrency(baseCurrency, currenciesToRate);

        verify(currencySource).getExchangeRate(baseCurrency, currenciesToRate);
        verify(currencyRatesMapper).toModel(mockResponse);
        assertNotNull(result);
        assertEquals(2, result.getRates().size());
        assertEquals(37.0, result.getRates().get("USDUAH"));
    }

    @Test
    void testGetRateForSpecificCurrencies() {
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        CurrencyRatesExternalSourceResponse mockResponse = new CurrencyRatesExternalSourceResponse();
        CurrencyRates mockRates = new CurrencyRates();
        mockRates.setRates(Map.of("USDEUR", 0.92));

        when(currencySource.getRateForSpecificCurrencies(currencyFrom, currencyTo)).thenReturn(mockResponse);
        when(currencyRatesMapper.toModel(mockResponse)).thenReturn(mockRates);

        CurrencyRates result = currencyService.getRateForSpecificCurrencies(currencyFrom, currencyTo);

        verify(currencySource).getRateForSpecificCurrencies(currencyFrom, currencyTo);
        verify(currencyRatesMapper).toModel(mockResponse);
        assertNotNull(result);
        assertEquals(1, result.getRates().size());
        assertEquals(0.92, result.getRates().get("USDEUR"));
    }

    @Test
    void testConvert() {
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        double amount = 100.0;

        CurrencyConvertingExternalSourceResponse mockResponse = new CurrencyConvertingExternalSourceResponse();
        CurrencyConverting mockConversion = CurrencyConverting.builder()
                .result(92.0)
                .build();

        when(currencySource.convertCurrencyToAnother(currencyFrom, currencyTo, amount)).thenReturn(mockResponse);
        when(currencyConversionMapper.toModel(mockResponse)).thenReturn(mockConversion);

        CurrencyConverting result = currencyService.convert(currencyFrom, currencyTo, amount);

        verify(currencySource).convertCurrencyToAnother(currencyFrom, currencyTo, amount);
        verify(currencyConversionMapper).toModel(mockResponse);
        assertNotNull(result);
        assertEquals(92.0, result.getResult());
    }

    @Test
    void testConvertMultiple_HappyPath() {
        String currencyFrom = "USD";
        List<String> currenciesToRate = List.of("EUR", "UAH");
        Double amount = 100.0;

        CurrencyRatesExternalSourceResponse mockExternalResponse = new CurrencyRatesExternalSourceResponse();  // placeholder
        CurrencyRates mockRates = CurrencyRates.builder()
                .timestamp(Instant.now())
                .source("USD")
                .rates(Map.of("USDEUR", 0.92, "USDUAH", 37.0))
                .build();

        when(currencySource.getExchangeRate(eq(currencyFrom), eq("EUR,UAH")))
                .thenReturn(mockExternalResponse);
        when(currencyRatesMapper.toModel(mockExternalResponse)).thenReturn(mockRates);

        List<CurrencyConverting> result = currencyService.convertMultiple(currencyFrom, currenciesToRate, amount);

        verify(currencySource, times(1)).getExchangeRate(eq("USD"), eq("EUR,UAH"));
        verify(currencyRatesMapper, times(1)).toModel(mockExternalResponse);

        assertNotNull(result);
        assertEquals(2, result.size());

        CurrencyConverting first = result.get(0);
        CurrencyConverting second = result.get(1);

        assertEquals("USD", first.getQuery().getFrom());
        assertEquals("EUR", first.getQuery().getTo());
        assertEquals(amount, first.getQuery().getAmount());
        assertEquals(92.0, first.getResult());

        assertEquals("USD", second.getQuery().getFrom());
        assertEquals("UAH", second.getQuery().getTo());
        assertEquals(3700.0, second.getResult());
    }

    @Test
    void testConvertMultiple_EmptyCurrenciesList() {
        String currencyFrom = "USD";
        List<String> currenciesToRate = Collections.emptyList();
        Double amount = 100.0;

        CurrencyRatesExternalSourceResponse mockExternalResponse = new CurrencyRatesExternalSourceResponse();
        CurrencyRates mockRates = CurrencyRates.builder()
                .timestamp(Instant.now())
                .source("USD")
                .rates(Map.of())
                .build();

        when(currencySource.getExchangeRate(eq(currencyFrom), eq("")))
                .thenReturn(mockExternalResponse);
        when(currencyRatesMapper.toModel(mockExternalResponse)).thenReturn(mockRates);

        List<CurrencyConverting> result = currencyService.convertMultiple(currencyFrom, currenciesToRate, amount);

        verify(currencySource, times(1)).getExchangeRate(eq("USD"), eq(""));
        verify(currencyRatesMapper, times(1)).toModel(mockExternalResponse);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertMultiple_NoRates() {
        String currencyFrom = "USD";
        List<String> currenciesToRate = List.of("EUR", "UAH");
        Double amount = 100.0;

        CurrencyRatesExternalSourceResponse mockExternalResponse = new CurrencyRatesExternalSourceResponse();
        CurrencyRates mockRates = CurrencyRates.builder()
                .timestamp(Instant.now())
                .source("USD")
                .rates(Collections.emptyMap())
                .build();

        when(currencySource.getExchangeRate(eq(currencyFrom), eq("EUR,UAH")))
                .thenReturn(mockExternalResponse);
        when(currencyRatesMapper.toModel(mockExternalResponse)).thenReturn(mockRates);
        List<CurrencyConverting> result = currencyService.convertMultiple(currencyFrom, currenciesToRate, amount);
        verify(currencySource).getExchangeRate(eq(currencyFrom), eq("EUR,UAH"));
        verify(currencyRatesMapper).toModel(mockExternalResponse);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertMultiple_ZeroAmount() {
        String currencyFrom = "USD";
        List<String> currenciesToRate = List.of("EUR");
        Double amount = 0.0;

        CurrencyRatesExternalSourceResponse mockExternalResponse = new CurrencyRatesExternalSourceResponse();
        CurrencyRates mockRates = CurrencyRates.builder()
                .timestamp(Instant.now())
                .source("USD")
                .rates(Map.of("USDEUR", 0.92))
                .build();

        when(currencySource.getExchangeRate(eq("USD"), eq("EUR")))
                .thenReturn(mockExternalResponse);
        when(currencyRatesMapper.toModel(mockExternalResponse)).thenReturn(mockRates);

        List<CurrencyConverting> result = currencyService.convertMultiple(currencyFrom, currenciesToRate, amount);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).getResult());
    }
}