package com.yehor.kutsenko.currencyrate.mapper;

import com.yehor.kutsenko.currencyrate.dto.external.CurrencyConvertingExternalSourceResponse;
import com.yehor.kutsenko.currencyrate.dto.response.CurrencyConvertingResponse;
import com.yehor.kutsenko.currencyrate.model.CurrencyConverting;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CurrencyConversionMapper {

    CurrencyConverting toModel(CurrencyConvertingExternalSourceResponse response);

    CurrencyConvertingResponse toResponse(CurrencyConverting model);

    List<CurrencyConvertingResponse> toResponse(List<CurrencyConverting> model);

}
