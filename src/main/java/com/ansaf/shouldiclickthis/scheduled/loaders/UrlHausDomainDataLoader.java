package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.URL_HAUS_DOMAIN_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.URL_HAUS_DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.URL_HAUS_DOMAIN_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.UrlHausConfig;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UrlHausDomainDataLoader extends AbstractDataLoader {

  private final UrlHausConfig urlHausConfig;

  public UrlHausDomainDataLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, UrlHausConfig urlHausConfig) {
    super(timeService, redisService, restService, fileService, URL_HAUS_DOMAIN_LOADER_NAME,
        new ArrayList<>(), urlHausConfig.getDomainInterval());
    this.urlHausConfig = urlHausConfig;
  }

  @Override
  protected void extractData() {
    extractCsvTextFile(urlHausConfig.getDomainUrl(), urlHausConfig.getDomainSkipHeader(),
        urlHausConfig.getDomainSkipFooter(), urlHausConfig.getDomainDelimiter(), 1, GET);
  }

  @Override
  protected void saveData() {
    saveTextFileToRedis(URL_HAUS_DOMAIN_SET, URL_HAUS_DOMAIN_UPDATED,
        urlHausConfig.getDomainSplit());
  }
}
