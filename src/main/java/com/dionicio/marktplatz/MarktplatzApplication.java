package com.dionicio.marktplatz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MarktplatzApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarktplatzApplication.class, args);
	}

}
