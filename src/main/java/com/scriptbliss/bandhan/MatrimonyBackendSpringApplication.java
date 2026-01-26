package com.scriptbliss.bandhan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MatrimonyBackendSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatrimonyBackendSpringApplication.class, args);
	}

}
