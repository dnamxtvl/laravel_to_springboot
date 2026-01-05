package com.example.hello_sring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HelloSringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSringBootApplication.class, args);
	}

}
