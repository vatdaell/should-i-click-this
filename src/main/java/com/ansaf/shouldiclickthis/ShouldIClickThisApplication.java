package com.ansaf.shouldiclickthis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShouldIClickThisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShouldIClickThisApplication.class, args);
	}

}
