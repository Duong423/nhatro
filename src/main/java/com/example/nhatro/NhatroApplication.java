package com.example.nhatro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NhatroApplication {

	public static void main(String[] args) {
		SpringApplication.run(NhatroApplication.class, args);
	}

}
