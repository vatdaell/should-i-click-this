package com.ansaf.shouldiclickthis.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

    public TarArchiveInputStream unzipFolder(byte[] fileContent) throws Exception{
        InputStream fi = new ByteArrayInputStream(fileContent);
        BufferedInputStream bi = new BufferedInputStream(fi);
        GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
        return new TarArchiveInputStream(gzi);
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

    public List<String[]> parseAndSkipLines(byte[] fileContent, int skipHeader, int skipFooter,
        String delimiter)
        throws IOException {
        List<String[]> result = new ArrayList<>();
        String contentAsString = new String(fileContent, StandardCharsets.UTF_8);

        try (BufferedReader reader = new BufferedReader(new StringReader(contentAsString))) {
            for (int i = 0; i < skipHeader; i++) {
                reader.readLine();
            }
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(delimiter);
                result.add(parts);
            }

            for (int j = 0; j < skipFooter; j++) {
                result.remove(result.size() - 1);
            }
        }

        return result;
    }
}
