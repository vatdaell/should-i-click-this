package com.ansaf.shouldiclickthis.scheduled.loaders;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
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
    log.info("Starting data loader: {}", loaderName);
    extractData();
    if (rows.isEmpty()) {
      log.warn("No rows were extracted for loader {}", loaderName);
    } else {
      saveData();
    }
    log.info("Ending data loader: {}", loaderName);
  }

  private void setUpdatedTime(String key) {
    String currentTimeInString = timeService.getIsoFormatString(timeService.getNowTime());
    redisService.setString(key, currentTimeInString);
  }

  protected void saveTextFileToRedis(String setName, String updatedName, int split) {
    try {
      redisService.saveValuesInChunks(setName, rows, split);
      setUpdatedTime(updatedName);
    } catch (DataAccessException e) {
      log.error("Issues inserting {} into Redis: {}", loaderName, e.getMessage());

    } catch (Exception e) {
      log.error("Unknown error occurred while loading {} file: {}", loaderName, e.getMessage());
    }
  }

  protected void extractCsvTextFile(String url, int skipLines, int skipFooter, String delimiter,
      int col,
      HttpMethod httpMethod) {
    try {
      byte[] fileContent = restService.loadFileContent(url, httpMethod);
      rows = fileService.parseAndSkipLines(fileContent, skipLines,
              skipFooter, delimiter).stream().filter(r -> r.length > 0).map(r -> r[col])
          .toList();
      log.info("File loaded from {}", loaderName);
    } catch (EmptyFileFileContentException e) {
      log.error("{} not loaded {}", loaderName, e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading {} file: {}", loaderName, e.getMessage());
    }
  }

  protected void extractTextFromZip(String url, HttpMethod httpMethod, String ext,
      String lineBreak) {
    try {
      byte[] fileContent = restService.loadFileContent(url, httpMethod);
      TarArchiveInputStream ti = fileService.unzipFolder(fileContent);
      TarArchiveEntry entry;
      while ((entry = ti.getNextEntry()) != null) {
        rows.addAll(fileService.extractRowsFromZip(entry, ti, ext, lineBreak));
      }
    } catch (IOException e) {
      log.error("Tar file was not unzipped for {}: {}", loaderName, e.getMessage());
    } catch (EmptyFileFileContentException e) {
      log.error("Tar file not loaded for {}: {}", loaderName, e.getMessage());
    } catch (Exception e) {
      log.error("Unknown error occurred while loading {}: {}", loaderName, e.getMessage());
    }
  }
}
