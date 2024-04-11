package com.ansaf.shouldiclickthis.scheduled.loaders;

import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract class AbstractDataLoader {

  protected final TimeService timeService;
  protected final RedisService redisService;
  protected final RestService restService;
  protected final FileService fileService;
  protected String loaderName;
  protected List<String> rows;
  protected int fixedTimeString;

  protected abstract void extractData();

  protected abstract void saveData();

  public void process() {
    extractData();
    saveData();
  }

  protected void setUpdatedTime(String key) {
    String currentTimeInString = timeService.getIsoFormatString(timeService.getNowTime());
    redisService.setString(key, currentTimeInString);
  }
}
