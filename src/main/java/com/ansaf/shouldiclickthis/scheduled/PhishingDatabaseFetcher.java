package com.ansaf.shouldiclickthis.scheduled;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class PhishingDatabaseFetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 3600000) // 3600000 milliseconds = 1 hour
    public void fetchDomains() {
        try {
            // Download the .tar.gz file
            byte[] fileContent = loadFileContent("https://raw.githubusercontent.com/mitchellkrogza/Phishing.Database/master/ALL-phishing-domains.tar.gz");

            TarArchiveInputStream ti = unzipFolder(fileContent);

            TarArchiveEntry entry;
            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(extractRows(entry, ti, ".txt", "\n"));
            }
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
            byte[] fileContent = loadFileContent("https://raw.githubusercontent.com/mitchellkrogza/Phishing.Database/master/ALL-phishing-links.tar.gz");

            TarArchiveInputStream ti = unzipFolder(fileContent);

            TarArchiveEntry entry;

            List<String> rows = new ArrayList<>();

            while ((entry = ti.getNextEntry()) != null) {
                rows.addAll(extractRows(entry, ti, ".txt", "\n"));
            }
            log.info("Links loaded");

        } catch (IOException e) {
            log.error("Tar file was not unzipped");
        } catch (EmptyFileFileContentException e) {
            log.error("Tar file not loaded");
        } catch (Exception e) {
            log.error("Unknown error occured while loading phishing domains");
        }
    }

    private byte[] loadFileContent(String url) throws EmptyFileFileContentException {
        byte[] fileContent = restTemplate.execute(
                url,
                HttpMethod.GET,
                null,
                clientHttpResponse -> StreamUtils.copyToByteArray(clientHttpResponse.getBody())
        );

        if(fileContent == null){
            throw new EmptyFileFileContentException("Tar file was not loaded");
        }

        return fileContent;
    }

    private TarArchiveInputStream unzipFolder(byte[] fileContent) throws Exception{
            InputStream fi = new ByteArrayInputStream(fileContent);
            BufferedInputStream bi = new BufferedInputStream(fi);
            GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
            TarArchiveInputStream ti = new TarArchiveInputStream(gzi);
            return ti;
    }

    private List<String> extractRows(TarArchiveEntry entry, TarArchiveInputStream ti, String ext, String delimiter) throws IOException {
        List<String> urls = new ArrayList<>();
        if (!entry.isDirectory() && entry.getName().endsWith(ext)) {
            String content = new String(ti.readAllBytes());
            urls.addAll(Arrays.asList(content.split(delimiter)));
        }
        return urls;
    }

}
