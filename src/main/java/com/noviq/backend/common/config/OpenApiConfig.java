package com.noviq.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI noviqOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Noviq API")
                        .description("REST API for Noviq")
                        .version("1.0.0"));
    }
}