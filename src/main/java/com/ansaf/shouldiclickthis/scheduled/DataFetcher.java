package com.ansaf.shouldiclickthis.scheduled;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_UPDATED;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_UPDATED;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_UPDATED;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_UPDATED;
import static org.springframework.http.HttpMethod.GET;

import com.ansaf.shouldiclickthis.config.IpSumConfig;
import com.ansaf.shouldiclickthis.config.OpenPhishConfig;
import com.ansaf.shouldiclickthis.config.PhishingDbConfig;
import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.RestService;
import com.ansaf.shouldiclickthis.service.TimeService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class DataFetcher {

    private final RedisService redisService;
    private final FileService fileService;
    private final PhishingDbConfig phishingDbConfig;
    private final TimeService timeService;
    private final OpenPhishConfig openPhishConfig;
    private final RestService restService;
    private final IpSumConfig ipSumConfig;


    @Scheduled(fixedDelayString = "${phishing.db.domainsInterval}")
    public void fetchPhishingDbDomains() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = restService.loadFileContent(phishingDbConfig.getDomains(), GET);

            TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

            TarArchiveEntry entry;
            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(fileService.extractRowsFromZip(entry, ti, ".txt", "\n"));
            }
            redisService.saveUrlsInChunks(DOMAIN_SET, rows, phishingDbConfig.getDomainsSplit());
            log.info("Domains loaded in set: {}", DOMAIN_SET);
            setUpdatedTime(DOMAIN_UPDATED);

        } catch (IOException e) {
            log.error("Tar file was not unzipped: {}", e.getMessage());
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded: {}", e.getMessage());
        }
        catch (DataAccessException e) {
            log.error("Issues inserting domains into Redis: {}", e.getMessage());
        }
        catch (Exception e) {
            log.error("Unknown error occurred while loading phishing domains: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${phishing.db.linksInterval}")
    public void fetchPhishingDbLinks() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = restService.loadFileContent(phishingDbConfig.getLinks(), GET);

            TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

            TarArchiveEntry entry;

            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(fileService.extractRowsFromZip(entry, ti, ".txt", "\n"));
            }
            redisService.saveUrlsInChunks(LINK_SET, rows, phishingDbConfig.getLinksSplit());
            log.info("Links loaded in set: {}", LINK_SET);
            setUpdatedTime(LINK_UPDATED);

        } catch (IOException e) {
            log.error("Tar file was not unzipped: {}", e.getMessage());
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded: {}", e.getMessage());
        }
        catch (DataAccessException e) {
            log.error("Issues inserting links into Redis: {}", e.getMessage());
        }
        catch (Exception e) {
            log.error("Unknown error occurred while loading phishing domains: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${openphish.interval}")
    public void fetchOpenPhishLinks() {
        try {
            byte[] fileContent = restService.loadFileContent(openPhishConfig.getUrl(), GET);
            List<String> rows = fileService.extractRowFromString(fileContent, "\n");
            redisService.saveUrlsInChunks(OPENPHISH_SET, rows, openPhishConfig.getSplit());
            setUpdatedTime(OPENPHISH_UPDATED);
            log.info("File loaded from openphish");
        }
        catch (EmptyFileFileContentException e){
            log.error("OpenPhish file not loaded {}", e.getMessage());
        }
        catch (DataAccessException e) {
            log.error("Issues inserting links into Redis: {}", e.getMessage());
        }
        catch (Exception e) {
            log.error("Unknown error occurred while loading phishing domains: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${ipsum.interval}")
    public void fetchIpSumIps() {
        try {
            byte[] fileContent = restService.loadFileContent(ipSumConfig.getUrl(), GET);
            List<String> rows = fileService.parseAndSkipLines(fileContent, ipSumConfig.getSkip(),
                    ipSumConfig.getDelimiter()).stream().filter(r -> r.length > 0).map(r -> r[0])
                .toList();

            redisService.saveUrlsInChunks(IPSUM_SET, rows, ipSumConfig.getSplit());

            setUpdatedTime(IPSUM_UPDATED);
            log.info("File loaded from IPSum");
        } catch (EmptyFileFileContentException e) {
            log.error("IPSum file not loaded {}", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Issues inserting IPs into Redis: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unknown error occurred while loading IPs: {}", e.getMessage());
        }
    }

    private void setUpdatedTime(String key){
        String currentTimeInString = timeService.getIsoFormatString(timeService.getNowTime());
        redisService.setString(key, currentTimeInString);
    }

}
