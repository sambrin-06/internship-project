package com.ict.internal_controls_testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class InternalControlsTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternalControlsTestingApplication.class, args);
	}

}
