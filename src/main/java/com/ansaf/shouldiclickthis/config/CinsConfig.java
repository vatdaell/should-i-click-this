package com.ansaf.shouldiclickthis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cins")
@Data
public class CinsConfig {

  private int interval;
  private int split;
  private String url;
}
