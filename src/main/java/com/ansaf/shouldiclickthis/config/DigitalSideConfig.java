package com.ansaf.shouldiclickthis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "digitalside")
@Data
public class DigitalSideConfig {

  private int linkInterval;
  private int linkSplit;
  private String linkUrl;
  private int linkSkip;
  private int ipInterval;
  private int ipSplit;
  private String ipUrl;
  private int ipSkip;
  private int domainInterval;
  private int domainSplit;
  private String domainUrl;
  private int domainSkip;
}
