package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FileService {

    private final RestTemplate restTemplate;

    public TarArchiveInputStream unzipFolder(byte[] fileContent) throws Exception{
        InputStream fi = new ByteArrayInputStream(fileContent);
        BufferedInputStream bi = new BufferedInputStream(fi);
        GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
        return new TarArchiveInputStream(gzi);
    }

    public byte[] loadFileContent(String url) throws EmptyFileFileContentException {
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

    public List<String> extractRowsFromZip(TarArchiveEntry entry, TarArchiveInputStream ti, String ext, String delimiter) throws IOException {
        List<String> urls = new ArrayList<>();
        if (!entry.isDirectory() && entry.getName().endsWith(ext)) {
            String content = new String(ti.readAllBytes());
            urls.addAll(Arrays.asList(content.split(delimiter)));
        }
        return urls;
    }

    public List<String> extractRowFromString(byte[] fileContent, String delimiter){
        String fileText = new String(fileContent, StandardCharsets.UTF_8);
        return Arrays.asList(fileText.split(delimiter));
    }
}
