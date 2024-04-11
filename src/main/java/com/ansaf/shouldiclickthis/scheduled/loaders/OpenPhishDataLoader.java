package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.OpenPhishConfig;
import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
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
public class OpenPhishDataLoader extends AbstractDataLoader {

  private final OpenPhishConfig openPhishConfig;

  public OpenPhishDataLoader(TimeService timeService,
      RedisService redisService, RestService restService, FileService fileService,
      OpenPhishConfig openPhishConfig) {
    super(timeService, redisService, restService, fileService, "OpenPhishDataLoader",
        new ArrayList<>(),
        openPhishConfig.getInterval());
    this.openPhishConfig = openPhishConfig;
  }

  @Override
  protected void extractData() {
    try {
      byte[] fileContent = restService.loadFileContent(openPhishConfig.getUrl(), GET);
      rows = fileService.extractRowFromString(fileContent, "\n");

    } catch (EmptyFileFileContentException e) {
      log.error("OpenPhish file not loaded {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading OpenPhish file: {}", e.getMessage());
    }
  }

  @Override
  protected void saveData() {
    try {
      redisService.saveUrlsInChunks(OPENPHISH_SET, rows, openPhishConfig.getSplit());
      setUpdatedTime(OPENPHISH_UPDATED);
      log.info("File loaded from OpenPhish");
    } catch (DataAccessException e) {
      log.error("Issues inserting OpenPhish into Redis: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading OpenPhish file: {}", e.getMessage());
    }
  }
}
