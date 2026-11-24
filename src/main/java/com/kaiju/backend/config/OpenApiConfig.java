package com.kaiju.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(
            @Value("${app.public-base-url:http://184.192.5.101:8080}") String publicBaseUrl) {
        return new OpenAPI()
            .info(new Info()
                .title("Kaiju Backend API")
                .version("1.0.0")
                .description("API REST de inventario, sucursales, ventas y movimientos.")
                .contact(new Contact().name("Kaiju Backend")))
            .servers(List.of(
                new Server().url(publicBaseUrl).description("EC2 Elastic IP"),
                new Server().url("http://localhost:8080").description("Servidor local")
            ));
    }
}
