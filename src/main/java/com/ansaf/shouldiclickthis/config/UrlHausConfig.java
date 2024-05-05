package com.ansaf.shouldiclickthis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "urlhaus")
@Data
public class UrlHausConfig {

  private int linkInterval;
  private int linkSplit;
  private String linkUrl;
  private int linkSkipHeader;
  private String linkDelimiter;
  private int domainInterval;
  private int domainSplit;
  private String domainUrl;
  private int domainSkipHeader;
  private int domainSkipFooter;
  private String domainDelimiter;
}
