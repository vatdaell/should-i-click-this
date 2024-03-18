package com.ansaf.shouldiclickthis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate.limiter")
@Data
public class RateLimiterConfig {
    private int capacity;
    private int token;
    private int refill;
}