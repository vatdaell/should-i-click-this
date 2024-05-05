package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.PHISHING_DB_LINK_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.PHISHING_DB_LINK_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.PHISHING_DB_LINK_UPDATED;
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
public class PhishingDbLinkLoader extends AbstractDataLoader {

  private final PhishingDbConfig phishingDbConfig;

  public PhishingDbLinkLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, PhishingDbConfig phishingDbConfig) {
    super(timeService, redisService, restService, fileService, PHISHING_DB_LINK_LOADER_NAME,
        new ArrayList<>(),
        phishingDbConfig.getLinksInterval());
    this.phishingDbConfig = phishingDbConfig;
  }

  @Override
  protected void extractData() {
    extractTextFromZip(phishingDbConfig.getLinks(), GET, ".txt", "\n");
  }

  @Override
  protected void saveData() {
    saveTextFileToRedis(PHISHING_DB_LINK_SET, PHISHING_DB_LINK_UPDATED,
        phishingDbConfig.getLinksSplit());
  }
}
