package com.ansaf.shouldiclickthis.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.AssertionErrors.assertEquals;


public class UrlUtilsServiceTest {

    private UrlUtils urlUtils;
    @BeforeEach
    public void setUp(){
        this.urlUtils = new UrlUtils();
    }

    @Test
    public void extractDomainReturnHostWithHttps(){
        String url = "https://google.com";
        String actual = urlUtils.extractDomain(url);
        assertEquals("Wrong domain found", "google.com", actual);
    }

    @Test
    public void extractDomainReturnHostWithHttp(){
        String url = "http://google.com";
        String actual = urlUtils.extractDomain(url);
        assertEquals("Wrong domain found", "google.com", actual);

    }

    @Test
    public void extractDomainReturnHostWithoutHttp(){
        String url = "google.com";
        String actual = urlUtils.extractDomain(url);
        assertEquals("Wrong domain found", "google.com", actual);

    }

}
