package com.example.TTMS.config.swagger;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${com.custom.swagger-path}")
    private String path;

    @Value("${com.custom.swagger-live-path}")
    private String livePath;

    SecurityScheme securityScheme() {
        return new SecurityScheme().bearerFormat("JWT").type(Type.HTTP).scheme("bearer").name("Bearer Authentication");
    }

    @Bean
    public OpenAPI ttmsOpenAPI() {
        // Set server context path
        Server server = new Server();
        server.setUrl("/ttms");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", securityScheme()))
                .servers(List.of(server))
                .info(new Info()
                        .title("TTMS API")
                        .description("API documentation for TTMS (Transport Ticket Management System)")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("TTMS Support Team")
                                .email("support@hepl.com")));
    }
}