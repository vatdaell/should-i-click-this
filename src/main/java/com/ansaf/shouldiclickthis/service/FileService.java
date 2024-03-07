package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    public FileService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TarArchiveInputStream unzipFolder(byte[] fileContent) throws Exception{
        InputStream fi = new ByteArrayInputStream(fileContent);
        BufferedInputStream bi = new BufferedInputStream(fi);
        GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
        TarArchiveInputStream ti = new TarArchiveInputStream(gzi);
        return ti;
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

    public List<String> extractRows(TarArchiveEntry entry, TarArchiveInputStream ti, String ext, String delimiter) throws IOException {
        List<String> urls = new ArrayList<>();
        if (!entry.isDirectory() && entry.getName().endsWith(ext)) {
            String content = new String(ti.readAllBytes());
            urls.addAll(Arrays.asList(content.split(delimiter)));
        }
        return urls;
    }
}
