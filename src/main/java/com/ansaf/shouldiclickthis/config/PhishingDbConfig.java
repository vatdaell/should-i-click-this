package com.ansaf.shouldiclickthis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "phishing.db")
@Data
public class PhishingDbConfig {
    private String links;
    private String domains;
    private int linksInterval;
    private int domainsInterval;
    private int linksSplit;
    private int domainsSplit;
}
