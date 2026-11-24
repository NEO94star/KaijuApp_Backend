package com.kaiju.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KaijuBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(KaijuBackendApplication.class, args);
    }
}
