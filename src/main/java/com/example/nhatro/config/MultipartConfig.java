package com.example.nhatro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        // Set maximum file size and request size directly
        return new MultipartConfigElement(
            null,
            DataSize.ofMegabytes(50).toBytes(),
            DataSize.ofMegabytes(100).toBytes(),
            0
        );
    }
}
