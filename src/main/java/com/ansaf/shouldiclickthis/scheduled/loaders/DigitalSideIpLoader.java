package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.DIGITAL_SIDE_IP_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DIGITAL_SIDE_IPS_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DIGITAL_SIDE_IPS_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.DigitalSideConfig;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class DigitalSideIpLoader extends AbstractDataLoader {

  private final DigitalSideConfig digitalSideConfig;

  public DigitalSideIpLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, DigitalSideConfig digitalSideConfig) {
    super(timeService, redisService, restService, fileService, DIGITAL_SIDE_IP_LOADER_NAME,
        new ArrayList<>(),
        digitalSideConfig.getIpInterval());
    this.digitalSideConfig = digitalSideConfig;
  }

  @Override
  protected void extractData() {
    extractCsvTextFile(digitalSideConfig.getIpUrl(), digitalSideConfig.getIpSkip(), 0,
        " ", 0, GET);
  }

  @Override
  protected void saveData() {
    saveTextFileToRedis(DIGITAL_SIDE_IPS_SET, DIGITAL_SIDE_IPS_UPDATED,
        digitalSideConfig.getIpSplit());
  }

}
