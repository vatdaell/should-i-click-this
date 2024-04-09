package com.ansaf.shouldiclickthis.service;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    private FileService fileService;
    @BeforeEach
    void setUp(){
        fileService = new FileService();
    }

    private byte[] createGzippedContent(String content) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(content.length());
        try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
            zipStream.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return byteStream.toByteArray();
    }

    @Test
    void unzipFolderShouldReturnTarArchiveInputStream() {

        String testContent = "Test content";
        byte[] gzippedContent;

        try {
            gzippedContent = createGzippedContent(testContent);
            TarArchiveInputStream resultStream = fileService.unzipFolder(gzippedContent);
            assertNotNull(resultStream);

        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }



    @Test
    void testExtractRowsFromZip() throws IOException {
        ByteArrayOutputStream byteOutStream = getByteArrayOutputStream("test.txt", "url1.com\nurl2.com\nurl3.com");

        try (ByteArrayInputStream bin = new ByteArrayInputStream(byteOutStream.toByteArray());
             TarArchiveInputStream tin = new TarArchiveInputStream(new GzipCompressorInputStream(bin))) {

            TarArchiveEntry entry = tin.getNextEntry();
            List<String> urls = fileService.extractRowsFromZip(entry, tin, ".txt", "\n");

            assertEquals("Should extract three URLs.",3, urls.size());
            assertEquals("First URL should match.","url1.com", urls.get(0));
            assertEquals("Second URL should match.", "url2.com", urls.get(1));
            assertEquals("Third URL should match.", "url3.com", urls.get(2));
        }
    }

    @Test
    void testExtractRowsFromZipWithDifferentDelimeter() throws IOException {
        ByteArrayOutputStream byteOutStream = getByteArrayOutputStream("test.txt", "url1.com,url2.com,url3.com");

        try (ByteArrayInputStream bin = new ByteArrayInputStream(byteOutStream.toByteArray());
             TarArchiveInputStream tin = new TarArchiveInputStream(new GzipCompressorInputStream(bin))) {

            TarArchiveEntry entry = tin.getNextEntry();
            List<String> urls = fileService.extractRowsFromZip(entry, tin, ".txt", ",");

            assertEquals("Should extract three URLs.",3, urls.size());
            assertEquals("First URL should match.","url1.com", urls.get(0));
            assertEquals("Second URL should match.", "url2.com", urls.get(1));
            assertEquals("Third URL should match.", "url3.com", urls.get(2));
        }
    }

    @Test
    void testExtractRowsFromZipWithDifferentFilename() throws IOException {
        ByteArrayOutputStream byteOutStream = getByteArrayOutputStream("test.file", "url1.com,url2.com,url3.com");
        try (ByteArrayInputStream bin = new ByteArrayInputStream(byteOutStream.toByteArray());
             TarArchiveInputStream tin = new TarArchiveInputStream(new GzipCompressorInputStream(bin))) {

            TarArchiveEntry entry = tin.getNextEntry();
            List<String> urls = fileService.extractRowsFromZip(entry, tin, ".file", ",");

            assertEquals("Should extract three URLs.",3, urls.size());
            assertEquals("First URL should match.","url1.com", urls.get(0));
            assertEquals("Second URL should match.", "url2.com", urls.get(1));
            assertEquals("Third URL should match.", "url3.com", urls.get(2));
        }
    }

    @Test
    void testExtractRows() {
        byte[] file = "url1.com\nurl2.com\nurl3.com".getBytes(StandardCharsets.UTF_8);
        List<String> urls = fileService.extractRowFromString(file, "\n");
        assertEquals("Should extract three URLs.",3, urls.size());
        assertEquals("First URL should match.","url1.com", urls.get(0));
        assertEquals("Second URL should match.", "url2.com", urls.get(1));
        assertEquals("Third URL should match.", "url3.com", urls.get(2));
    }

    @Test
    void testExtractRowsWithDifferentDelimiter() {
        byte[] file = "url1.com,url2.com,url3.com".getBytes(StandardCharsets.UTF_8);
        List<String> urls = fileService.extractRowFromString(file, ",");
        assertEquals("Should extract three URLs.",3, urls.size());
        assertEquals("First URL should match.","url1.com", urls.get(0));
        assertEquals("Second URL should match.", "url2.com", urls.get(1));
        assertEquals("Third URL should match.", "url3.com", urls.get(2));
    }

    @Test
    void testRowParsing() throws IOException {
        String csvString = """
            # IPsum Threat Intelligence Feed
            # (https://github.com/stamparm/ipsum)
            #
            # Last update: Tue, 09 Apr 2024 03:11:09 +0200
            #
            # IP\tnumber of (black)lists
            #
            185.224.128.34\t11
            82.200.65.218\t8
            186.96.145.241\t8
            178.20.55.16\t8""";

        byte[] csvByte = csvString.getBytes(StandardCharsets.UTF_8);
        List<String[]> actual = fileService.parseAndSkipLines(csvByte, 7, "\t");
        List<String[]> expected = Arrays.asList(
            new String[]{"185.224.128.34", "11"},
            new String[]{"82.200.65.218", "8"},
            new String[]{"186.96.145.241", "8"},
            new String[]{"178.20.55.16", "8"}
        );
        assertEquals("Parsed Length must be correct", expected.size(), actual.size());

        AtomicInteger idx = new AtomicInteger();
        expected.forEach(e -> {
            String[] currentActual = expected.get(idx.get());
            assertEquals("IP address must be correct", e[0], currentActual[0]);
            assertEquals("Blacklist amount must be correct", e[1], currentActual[1]);
            idx.getAndIncrement();
        });
    }

    private ByteArrayOutputStream getByteArrayOutputStream(String filename, String input) throws IOException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(new GzipCompressorOutputStream(byteOutStream))) {
            TarArchiveEntry entry = new TarArchiveEntry(filename);
            byte[] data = input.getBytes(StandardCharsets.UTF_8);
            entry.setSize(data.length);
            tarOut.putArchiveEntry(entry);
            tarOut.write(data);
            tarOut.closeArchiveEntry();
        }
        return byteOutStream;
    }

}
