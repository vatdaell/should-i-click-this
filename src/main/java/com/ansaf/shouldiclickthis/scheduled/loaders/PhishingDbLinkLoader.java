package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.PhishingDbConfig;
import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhishingDbLinkLoader extends AbstractDataLoader {

  private final PhishingDbConfig phishingDbConfig;

  public PhishingDbLinkLoader(TimeService timeService,
      RedisService redisService,
      RestService restService,
      FileService fileService, PhishingDbConfig phishingDbConfig) {
    super(timeService, redisService, restService, fileService, "PhishingDbDomainLoader",
        new ArrayList<>(),
        phishingDbConfig.getLinksInterval());
    this.phishingDbConfig = phishingDbConfig;
  }

  @Override
  protected void extractData() {
    try {
      byte[] fileContent = restService.loadFileContent(phishingDbConfig.getLinks(), GET);

      TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

      TarArchiveEntry entry;
      while ((entry = ti.getNextEntry()) != null) {
        rows.addAll(fileService.extractRowsFromZip(entry, ti, ".txt", "\n"));
      }
    } catch (IOException e) {
      log.error("Tar file was not unzipped for Phishing.db Links: {}", e.getMessage());
    } catch (EmptyFileFileContentException e) {
      log.error("Tar file not loaded for Phishing.db Links: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading Phishing.db Links: {}", e.getMessage());
    }

  }

  @Override
  protected void saveData() {
    if (rows.isEmpty()) {
      log.warn("No data for Phishing.db Links was found");
    } else {
      try {
        redisService.saveUrlsInChunks(LINK_SET, rows, phishingDbConfig.getLinksSplit());
        log.info("Phishing.db Links loaded in set: {}", LINK_SET);
        setUpdatedTime(LINK_UPDATED);
      } catch (DataAccessException e) {
        log.error("Issues inserting Phishing.db Links into Redis: {}", e.getMessage());
      } catch (Exception e) {
        log.error("Unknown error occurred while loading Phishing.db Links: {}", e.getMessage());
      }
    }
  }
}
