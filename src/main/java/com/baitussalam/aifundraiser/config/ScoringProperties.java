package com.baitussalam.aifundraiser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "scoring")
public class ScoringProperties {
    private Map<String, Double> weights;
}
