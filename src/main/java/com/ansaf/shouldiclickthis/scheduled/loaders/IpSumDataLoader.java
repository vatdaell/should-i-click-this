package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.IPSUM_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.IpSumConfig;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IpSumDataLoader extends AbstractDataLoader {

  private final IpSumConfig ipSumConfig;

  public IpSumDataLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, IpSumConfig ipSumConfig) {
    super(timeService, redisService, restService, fileService, IPSUM_LOADER_NAME, new ArrayList<>(),
        ipSumConfig.getInterval());
    this.ipSumConfig = ipSumConfig;
  }

  @Override
  protected void extractData() {
    extractCsvTextFile(ipSumConfig.getUrl(), ipSumConfig.getSkip(), 0, ipSumConfig.getDelimiter(),
        0, GET);
  }

  @Override
  protected void saveData() {
    saveTextFileToRedis(IPSUM_SET, IPSUM_UPDATED, ipSumConfig.getSplit());
    try {
      redisService.saveValuesInChunks(IPSUM_SET, rows, ipSumConfig.getSplit());
      setUpdatedTime(IPSUM_UPDATED);
    } catch (DataAccessException e) {
      log.error("Issues inserting IpSum into Redis: {}", e.getMessage());

    } catch (Exception e) {
      log.error("Unknown error occurred while loading IPSum file: {}", e.getMessage());
    }
  }
}
