package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.SuccessResponse;
import com.ansaf.shouldiclickthis.service.RateLimiterService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.TimeService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)

public class ApiControllerTest {
    @Mock
    private RedisService redisService;

    @Mock
    private TimeService timeService;

    @Mock
    private RateLimiterService rateLimiterService;

    @InjectMocks
    private ApiController apiController;

    private final String input = "domain.com";

    private final LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 1, 1,1);

    private final String localDateTimeString = "2024-01-01 01:01:01";


    @Test
    void domainControllerResponseIsStatusSuccess() throws Exception {
        given(redisService.urlContains(DOMAIN_SET, input)).willReturn(true);
        given(timeService.getNowTime()).willReturn(localDateTime);
        given(timeService.getIsoFormatString(eq(localDateTime))).willReturn(localDateTimeString);
        given(rateLimiterService.getPhishingDbBucket()).willReturn(Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        doNothing().when(rateLimiterService).runRateLimit(any(), eq(1), any());
        given(redisService.getString(anyString())).willReturn(localDateTimeString);


        SuccessResponse expected = SuccessResponse
                .builder()
                .url(input)
                .status(true)
                .responseTime(localDateTimeString)
                .lastUpdated(localDateTimeString)
                .build();
        SuccessResponse actual = apiController.domainSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void domainControllerResponseIsNotStatusSuccess() throws Exception {
        given(redisService.urlContains(DOMAIN_SET, input)).willReturn(false);
        given(timeService.getNowTime()).willReturn(localDateTime);
        given(timeService.getIsoFormatString(eq(localDateTime))).willReturn(localDateTimeString);
        given(rateLimiterService.getPhishingDbBucket()).willReturn(Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        doNothing().when(rateLimiterService).runRateLimit(any(), eq(1), any());
        given(redisService.getString(anyString())).willReturn(localDateTimeString);

        SuccessResponse expected = SuccessResponse
                .builder()
                .url(input)
                .status(false)
                .responseTime(localDateTimeString)
                .lastUpdated(localDateTimeString)
                .build();
        SuccessResponse actual = apiController.domainSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void linkControllerResponseIsStatusSuccess() throws TooManyRequestsException {
        given(redisService.urlContains(LINK_SET, input)).willReturn(true);
        given(timeService.getNowTime()).willReturn(localDateTime);
        given(timeService.getIsoFormatString(eq(localDateTime))).willReturn(localDateTimeString);
        given(rateLimiterService.getPhishingDbBucket()).willReturn(Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        doNothing().when(rateLimiterService).runRateLimit(any(), eq(1), any());
        given(redisService.getString(anyString())).willReturn(localDateTimeString);

        SuccessResponse expected = SuccessResponse
                .builder()
                .url(input)
                .status(true)
                .responseTime(localDateTimeString)
                .lastUpdated(localDateTimeString)
                .build();
        SuccessResponse actual = apiController.linkSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void linkControllerResponseIsNotStatusSuccess() throws TooManyRequestsException {
        given(redisService.urlContains(LINK_SET, input)).willReturn(false);
        given(timeService.getNowTime()).willReturn(localDateTime);
        given(timeService.getIsoFormatString(eq(localDateTime))).willReturn(localDateTimeString);
        given(rateLimiterService.getPhishingDbBucket()).willReturn(Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        doNothing().when(rateLimiterService).runRateLimit(any(), eq(1), any());
        given(redisService.getString(anyString())).willReturn(localDateTimeString);

        SuccessResponse expected = SuccessResponse
                .builder()
                .url(input)
                .status(false)
                .responseTime(localDateTimeString)
                .lastUpdated(localDateTimeString)
                .build();
        SuccessResponse actual = apiController.linkSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void openPhishControllerResponseIsStatusSuccess() throws TooManyRequestsException{
        given(redisService.urlContains(OPENPHISH_SET, input)).willReturn(true);
        given(timeService.getNowTime()).willReturn(localDateTime);
        given(timeService.getIsoFormatString(eq(localDateTime))).willReturn(localDateTimeString);
        given(rateLimiterService.getPhishingDbBucket()).willReturn(Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        doNothing().when(rateLimiterService).runRateLimit(any(), eq(1), any());
        given(redisService.getString(anyString())).willReturn(localDateTimeString);

        SuccessResponse expected = SuccessResponse
                .builder()
                .url(input)
                .status(true)
                .responseTime(localDateTimeString)
                .lastUpdated(localDateTimeString)
                .build();

        SuccessResponse actual = apiController.openPhishSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void openPhishControllerResponseIsStatusFailure() throws TooManyRequestsException{
        given(redisService.urlContains(OPENPHISH_SET, input)).willReturn(false);
        given(timeService.getNowTime()).willReturn(localDateTime);
        given(timeService.getIsoFormatString(eq(localDateTime))).willReturn(localDateTimeString);
        given(rateLimiterService.getPhishingDbBucket()).willReturn(Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        doNothing().when(rateLimiterService).runRateLimit(any(), eq(1), any());
        given(redisService.getString(anyString())).willReturn(localDateTimeString);

        SuccessResponse expected = SuccessResponse
                .builder()
                .url(input)
                .status(false)
                .responseTime(localDateTimeString)
                .lastUpdated(localDateTimeString)
                .build();

        SuccessResponse actual = apiController.openPhishSafety(input);

        assertSuccessResponse(expected, actual);
    }

    private void assertSuccessResponse(SuccessResponse expected, SuccessResponse actual){
        assertEquals("Url is not the same", expected.getUrl(), actual.getUrl());
        assertEquals("Status is not the same", expected.isStatus(), actual.isStatus());
        assertEquals("Response time is not the same", expected.getResponseTime(), actual.getResponseTime());
        assertEquals("LastUpdated is not the same", expected.getLastUpdated(), actual.getLastUpdated());

    }
}