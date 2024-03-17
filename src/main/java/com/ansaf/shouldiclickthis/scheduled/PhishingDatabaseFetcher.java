package com.ansaf.shouldiclickthis.scheduled;

import com.ansaf.shouldiclickthis.config.PhishingDbConfig;
import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import com.ansaf.shouldiclickthis.service.FileService;
import com.ansaf.shouldiclickthis.service.RedisService;
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
public class PhishingDatabaseFetcher {

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PhishingDbConfig phishingDbConfig;


    @Scheduled(fixedDelayString = "${phishing.db.domains.interval}")
    public void fetchDomains() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = fileService.loadFileContent(phishingDbConfig.getDomains());

            TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

            TarArchiveEntry entry;
            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(fileService.extractRows(entry, ti, ".txt", "\n"));
            }
            redisService.saveUrlsInChunks(DOMAIN_SET,rows,15);
            log.info("Domains loaded");

        } catch (IOException e) {
            log.error("Tar file was not unzipped");
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded");
        }
        catch (DataAccessException e) {
            log.error("Issues inserting domains into Redis: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Unknown error occured while loading phishing domains");
        }
    }

    @Scheduled(fixedDelayString = "${phishing.db.links.interval}")
    public void fetchLinks() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = fileService.loadFileContent(phishingDbConfig.getLinks());

            TarArchiveInputStream ti = fileService.unzipFolder(fileContent);

            TarArchiveEntry entry;

            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(fileService.extractRows(entry, ti, ".txt", "\n"));
            }
            redisService.saveUrlsInChunks(LINK_SET,rows,20);
            log.info("Links loaded");

        } catch (IOException e) {
            log.error("Tar file was not unzipped");
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded:" + e.getMessage());
        }
        catch (DataAccessException e) {
            log.error("Issues inserting links into Redis:" + e.getMessage());
        }
        catch (Exception e) {
            log.error("Unknown error occured while loading phishing domains:");
        }
    }

}
