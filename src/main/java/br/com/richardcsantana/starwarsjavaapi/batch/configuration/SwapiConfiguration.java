package br.com.richardcsantana.starwarsjavaapi.batch.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SwapiConfiguration {

    @Value("${swapi.url}")
    private String baseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.create(baseUrl);
    }
}
