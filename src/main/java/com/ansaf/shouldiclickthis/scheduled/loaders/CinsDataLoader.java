package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.CINS_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.CINS_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.CINS_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.CinsConfig;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class CinsDataLoader extends AbstractDataLoader {

  private final CinsConfig cinsConfig;

  public CinsDataLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, CinsConfig cinsConfig) {
    super(timeService, redisService, restService, fileService, CINS_LOADER_NAME, new ArrayList<>(),
        cinsConfig.getInterval());
    this.cinsConfig = cinsConfig;
  }

  @Override
  protected void extractData() {
    extractCsvTextFile(cinsConfig.getUrl(), 0, 0, "NULL", 0, GET);
  }

  @Override
  protected void saveData() {
    saveTextFileToRedis(CINS_SET, CINS_UPDATED, cinsConfig.getSplit());
  }
}
