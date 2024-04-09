package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.exception.EmptyFileFileContentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RestService {

  private final RestTemplate restTemplate;

  public byte[] loadFileContent(String url, HttpMethod httpMethod)
      throws EmptyFileFileContentException {
    byte[] fileContent = restTemplate.execute(
        url,
        httpMethod,
        null,
        clientHttpResponse -> StreamUtils.copyToByteArray(clientHttpResponse.getBody())
    );

    if (fileContent == null) {
      throw new EmptyFileFileContentException("Content was not loaded");
    }

    return fileContent;
  }
}
