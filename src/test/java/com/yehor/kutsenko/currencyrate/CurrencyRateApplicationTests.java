package com.yehor.kutsenko.currencyrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = CurrencyRateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith({SpringExtension.class})
@AutoConfigureMockMvc
public class CurrencyRateApplicationTests {


    protected static final String BASE_URL = "/api/v1";
    @Autowired
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();
    @RegisterExtension
    protected static WireMockExtension EXTERNAL_CURRENCY_SOURCE = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9080))
            .build();

    @Test
    void contextLoads() {
    }

}
