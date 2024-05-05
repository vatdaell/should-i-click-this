package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.OPENPHISH_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.OpenPhishConfig;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OpenPhishDataLoader extends AbstractDataLoader {

  private final OpenPhishConfig openPhishConfig;

  public OpenPhishDataLoader(TimeService timeService,
      RedisService redisService, RestService restService, FileService fileService,
      OpenPhishConfig openPhishConfig) {
    super(timeService, redisService, restService, fileService, OPENPHISH_LOADER_NAME,
        new ArrayList<>(),
        openPhishConfig.getInterval());
    this.openPhishConfig = openPhishConfig;
  }

  @Override
  protected void extractData() {
    extractCsvTextFile(openPhishConfig.getUrl(), 0, 0, "", 0, GET);
  }

  @Override
  protected void saveData() {
    saveTextFileToRedis(OPENPHISH_SET, OPENPHISH_UPDATED, openPhishConfig.getSplit());
  }
}
