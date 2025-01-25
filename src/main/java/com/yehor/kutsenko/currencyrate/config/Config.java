package com.yehor.kutsenko.currencyrate.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Config {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> log.info(requestTemplate.toString());
    }
}
