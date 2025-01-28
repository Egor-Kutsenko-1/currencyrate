package com.yehor.kutsenko.currencyrate.mapper;

import com.yehor.kutsenko.currencyrate.dto.external.CurrencyRatesExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.dto.response.CurrencyRatesResponse;
import com.yehor.kutsenko.currencyrate.model.CurrencyRates;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CurrencyRatesMapper {

    @Mapping(source = "quotes", target = "rates")
    CurrencyRates toModel(CurrencyRatesExternalSourceResponse response);

    CurrencyRatesResponse toResponse(CurrencyRates model);

}
