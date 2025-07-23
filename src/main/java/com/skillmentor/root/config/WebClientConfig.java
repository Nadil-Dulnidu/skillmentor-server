package com.skillmentor.root.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${clerk.api.key}")
    private String clerkApiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.clerk.dev/v1/users")
                .defaultHeader("Authorization", "Bearer " + clerkApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}


