package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.PHISHING_DB_DOMAIN_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.PHISHING_DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.PHISHING_DOMAIN_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.PhishingDbConfig;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PhishingDbDomainDataLoader extends AbstractDataLoader {

  private final PhishingDbConfig phishingDbConfig;

  public PhishingDbDomainDataLoader(TimeService timeService,
      RedisService redisService, RestService restService, FileService fileService,
      PhishingDbConfig phishingDbConfig) {
    super(timeService, redisService, restService, fileService, PHISHING_DB_DOMAIN_LOADER_NAME,
        new ArrayList<>(), phishingDbConfig.getDomainsInterval());
    this.phishingDbConfig = phishingDbConfig;
  }

  @Override
  public void extractData() {
    extractTextFromZip(phishingDbConfig.getDomains(), GET, ".txt", "\n");
  }

  @Override
  public void saveData() {
    saveTextFileToRedis(PHISHING_DOMAIN_SET, PHISHING_DOMAIN_UPDATED,
        phishingDbConfig.getDomainsSplit());
  }

}
