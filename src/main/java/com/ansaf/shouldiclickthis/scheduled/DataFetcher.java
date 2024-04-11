package com.ansaf.shouldiclickthis.scheduled;

import com.ansaf.shouldiclickthis.scheduled.loaders.AbstractDataLoader;
import com.ansaf.shouldiclickthis.scheduled.loaders.IpSumDataLoader;
import com.ansaf.shouldiclickthis.scheduled.loaders.OpenPhishDataLoader;
import com.ansaf.shouldiclickthis.scheduled.loaders.PhishingDbDomainDataLoader;
import com.ansaf.shouldiclickthis.scheduled.loaders.PhishingDbLinkLoader;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataFetcher {

  private final TaskScheduler taskScheduler;

  public DataFetcher(TaskScheduler taskScheduler,
      PhishingDbDomainDataLoader phishingDbDomainDataLoader,
      PhishingDbLinkLoader phishingDbLinkLoader, OpenPhishDataLoader openPhishDataLoader,
      IpSumDataLoader ipSumDataLoader) {
    this.taskScheduler = taskScheduler;
    scheduleTasks(List.of(phishingDbDomainDataLoader, phishingDbLinkLoader, openPhishDataLoader,
        ipSumDataLoader));
  }

  private void scheduleTasks(List<AbstractDataLoader> loaders) {
    loaders.forEach(this::scheduleTask);
  }

  private void scheduleTask(AbstractDataLoader loader) {
    taskScheduler.scheduleWithFixedDelay(loader::process, Duration.ofSeconds(
        loader.getFixedTimeString()));
    }

}
