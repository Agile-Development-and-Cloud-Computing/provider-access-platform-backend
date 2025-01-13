package com.fuas.providers_access_platform.config;

import com.fuas.providers_access_platform.service.ApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    CommandLineRunner run(ApiService apiService) {
        return args -> {
            System.out.println("Fetching and inserting agreements...");
            apiService.fetchAndInsertAgreements();
        };
    }

}
