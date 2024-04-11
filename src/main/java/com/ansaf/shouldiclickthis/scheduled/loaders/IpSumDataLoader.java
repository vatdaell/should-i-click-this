package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.IpSumConfig;
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
public class IpSumDataLoader extends AbstractDataLoader {

  private final IpSumConfig ipSumConfig;

  public IpSumDataLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, IpSumConfig ipSumConfig) {
    super(timeService, redisService, restService, fileService, "IpSum", new ArrayList<>(),
        ipSumConfig.getInterval());
    this.ipSumConfig = ipSumConfig;
  }

  @Override
  protected void extractData() {
    try {
      byte[] fileContent = restService.loadFileContent(ipSumConfig.getUrl(), GET);
      rows = fileService.parseAndSkipLines(fileContent, ipSumConfig.getSkip(),
              ipSumConfig.getDelimiter()).stream().filter(r -> r.length > 0).map(r -> r[0])
          .toList();
      setUpdatedTime(IPSUM_UPDATED);
      log.info("File loaded from IPSum");
    } catch (EmptyFileFileContentException e) {
      log.error("IPSum file not loaded {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading IPSum file: {}", e.getMessage());
    }
  }

  @Override
  protected void saveData() {
    try {
      redisService.saveUrlsInChunks(IPSUM_SET, rows, ipSumConfig.getSplit());
    } catch (DataAccessException e) {
      log.error("Issues inserting IpSum into Redis: {}", e.getMessage());

    } catch (Exception e) {
      log.error("Unknown error occurred while loading IPSum file: {}", e.getMessage());
    }
  }
}
