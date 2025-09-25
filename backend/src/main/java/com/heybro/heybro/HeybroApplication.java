package com.heybro.heybro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HeybroApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeybroApplication.class, args);
	}

}
