package com.ansaf.shouldiclickthis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openphish")
@Data
public class OpenPhishConfig {

  private int interval;
  private int split;
  private String url;
}
