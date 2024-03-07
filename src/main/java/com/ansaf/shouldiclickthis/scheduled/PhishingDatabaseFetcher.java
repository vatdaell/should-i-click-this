package com.ansaf.shouldiclickthis.scheduled;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PhishingDatabaseFetcher {

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;


    @Scheduled(fixedRate = 3600000) // 3600000 milliseconds = 1 hour
    public void fetchDomains() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = fileService.loadFileContent("https://raw.githubusercontent.com/mitchellkrogza/Phishing.Database/master/ALL-phishing-domains.tar.gz");

            TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

            TarArchiveEntry entry;
            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(fileService.extractRows(entry, ti, ".txt", "\n"));
            }
            redisService.saveUrls("domainsSet",rows);
            log.info("Domains loaded");

        } catch (IOException e) {
            log.error("Tar file was not unzipped");
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded");
        } catch (Exception e) {
            log.error("Unknown error occured while loading phishing domains");
        }
    }

    @Scheduled(fixedRate = 3600000) // 3600000 milliseconds = 1 hour
    public void fetchLinks() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = fileService.loadFileContent("https://raw.githubusercontent.com/mitchellkrogza/Phishing.Database/master/ALL-phishing-links.tar.gz");

            TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

            TarArchiveEntry entry;

            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(fileService.extractRows(entry, ti, ".txt", "\n"));
            }
            redisService.saveUrls("linksSet",rows);
            log.info("Links loaded");

        } catch (IOException e) {
            log.error("Tar file was not unzipped");
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded");
        } catch (Exception e) {
            log.error("Unknown error occured while loading phishing domains");
        }
    }





}
