package com.ansaf.shouldiclickthis.scheduled;

import com.ansaf.shouldiclickthis.config.OpenPhishConfig;
import com.ansaf.shouldiclickthis.config.PhishingDbConfig;
import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.*;
@Component
@Slf4j
public class DataFetcher {

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PhishingDbConfig phishingDbConfig;

    @Autowired
    private TimeService timeService;

    @Autowired
    private OpenPhishConfig openPhishConfig;


    @Scheduled(fixedDelayString = "${phishing.db.domainsInterval}")
    public void fetchPhishingDbDomains() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = fileService.loadFileContent(phishingDbConfig.getDomains());

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
            log.error("Unknown error occured while loading phishing domains: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${phishing.db.linksInterval}")
    public void fetchPhishingDbLinks() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = fileService.loadFileContent(phishingDbConfig.getLinks());

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
            log.error("Unknown error occured while loading phishing domains: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${openphish.interval}")
    public void fetchOpenPhishLinks() {
        try {
            byte[] fileContent = fileService.loadFileContent(openPhishConfig.getUrl());
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
            log.error("Unknown error occured while loading phishing domains: {}", e.getMessage());
        }
    }

    private void setUpdatedTime(String key){
        String currentTimeInString = timeService.getIsoFormatString(timeService.getNowTime());
        redisService.setString(key, currentTimeInString);
    }

}
