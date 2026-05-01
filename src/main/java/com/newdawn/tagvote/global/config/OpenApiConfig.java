package com.newdawn.tagvote.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tagvoteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tagvote API")
                        .version("v1")
                        .description("Tagvote backend API documentation")
                        .contact(new Contact()
                                .name("Newdawn SOI")
                                .url("https://vote.newdawnsoi.site/")));
    }
}
