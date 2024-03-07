package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
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

}
