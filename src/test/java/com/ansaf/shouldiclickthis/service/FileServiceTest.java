package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FileService fileService;
    @BeforeEach
    void setUp(){
        fileService = new FileService(restTemplate);
    }

    private byte[] createGzippedContent(String content) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(content.length());
        try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
            zipStream.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return byteStream.toByteArray();
    }

    private TarArchiveInputStream createArchiveInputStream(String content){
        ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
        return new TarArchiveInputStream(bis);
    }

    @Test
    void unzipFolderShouldReturnTarArchiveInputStream() {

        String testContent = "Test content";
        byte[] gzippedContent;

        try {
            gzippedContent = createGzippedContent(testContent);
            TarArchiveInputStream resultStream = fileService.unzipFolder(gzippedContent);

            // Verify the result is a TarArchiveInputStream
            assertNotNull(resultStream);

            // Optionally, read from the resultStream to further assert correct behavior

        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testLoadFileContent() throws Exception {
        // Given
        String url = "http://example.com/file.tar.gz";
        byte[] expectedContent = "File content".getBytes();
        given(restTemplate.execute(
                eq(url), eq(HttpMethod.GET), any(), any()))
                .willReturn(expectedContent);

        // When
        byte[] actualContent = fileService.loadFileContent(url);

        assertEquals("Wrong actual content is loaded", new String(expectedContent), new String(actualContent));

    }

    @Test
    void testLoadFileContentThrowsEmptyFileException() throws Exception {
        // Given
        String url = "http://example.com/file.tar.gz";
        given(restTemplate.execute(
                eq(url), eq(HttpMethod.GET), any(), any()))
                .willReturn(null);

        assertThrows(EmptyFileFileContentException.class, () -> fileService.loadFileContent(url));

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
