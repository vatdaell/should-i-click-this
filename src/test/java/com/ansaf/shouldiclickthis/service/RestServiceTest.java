package com.ansaf.shouldiclickthis.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class RestServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private RestService restService;

  @Test
  void testLoadFileContent() throws Exception {
    // Given
    String url = "http://example.com/file.tar.gz";
    byte[] expectedContent = "File content".getBytes();
    given(restTemplate.execute(
        eq(url), eq(GET), any(), any()))
        .willReturn(expectedContent);

    // When
    byte[] actualContent = restService.loadFileContent(url, GET);

    assertEquals("Wrong actual content is loaded", new String(expectedContent),
        new String(actualContent));

  }

  @Test
  void testLoadFileContentThrowsEmptyFileException() {
    // Given
    String url = "http://example.com/file.tar.gz";
    given(restTemplate.execute(
        eq(url), eq(GET), any(), any()))
        .willReturn(null);

    assertThrows(EmptyFileFileContentException.class, () -> restService.loadFileContent(url, GET));

  }

}
