package com.baitussalam.aifundraiser;

import com.baitussalam.aifundraiser.config.ScoringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(ScoringProperties.class)
@SpringBootApplication
public class AifundraiserApplication {

	public static void main(String[] args) {
		SpringApplication.run(AifundraiserApplication.class, args);
	}

}
