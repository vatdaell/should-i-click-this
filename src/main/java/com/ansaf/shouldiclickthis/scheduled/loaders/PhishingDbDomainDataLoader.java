package com.ansaf.shouldiclickthis.scheduled.loaders;

import static com.ansaf.shouldiclickthis.constant.LoaderConstant.PHISHING_DB_DOMAIN_LOADER_NAME;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_UPDATED;
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
public class PhishingDbDomainDataLoader extends AbstractDataLoader {

  private final PhishingDbConfig phishingDbConfig;

  public PhishingDbDomainDataLoader(TimeService timeService,
      RedisService redisService, RestService restService, FileService fileService,
      PhishingDbConfig phishingDbConfig) {
    super(timeService, redisService, restService, fileService, PHISHING_DB_DOMAIN_LOADER_NAME,
        new ArrayList<>(), phishingDbConfig.getDomainsInterval());
    this.phishingDbConfig = phishingDbConfig;
  }

  @Override
  public void extractData() {
    try {
      byte[] fileContent = restService.loadFileContent(phishingDbConfig.getDomains(), GET);
      TarArchiveInputStream ti = fileService.unzipFolder(fileContent);
      TarArchiveEntry entry;
      while ((entry = ti.getNextEntry()) != null) {
        rows.addAll(fileService.extractRowsFromZip(entry, ti, ".txt", "\n"));
      }
    } catch (IOException e) {
      log.error("Tar file was not unzipped for Phishing.db Domains: {}", e.getMessage());
    } catch (EmptyFileFileContentException e) {
      log.error("Tar file not loaded for Phishing.db Domains: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading Phishing.db Domains: {}", e.getMessage());
    }

  }

  @Override
  public void saveData() {
    if (rows.isEmpty()) {
      log.warn("No data for Phishing.db Domains was found");
    } else {
      try {
        redisService.saveValuesInChunks(DOMAIN_SET, rows, phishingDbConfig.getDomainsSplit());
        log.info("Phishing.db Domains loaded in set: {}", DOMAIN_SET);
        setUpdatedTime(DOMAIN_UPDATED);
      } catch (DataAccessException e) {
        log.error("Issues inserting Phishing.db Domains into Redis: {}", e.getMessage());
      } catch (Exception e) {
        log.error("Unknown error occurred while loading Phishing.db Domains: {}", e.getMessage());
      }
    }
  }

}
