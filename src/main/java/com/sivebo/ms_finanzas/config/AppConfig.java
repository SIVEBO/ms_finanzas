package com.sivebo.ms_finanzas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class AppConfig {

        @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
