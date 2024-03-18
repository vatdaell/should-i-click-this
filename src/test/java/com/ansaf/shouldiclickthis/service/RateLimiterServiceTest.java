package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.config.RateLimiterConfig;
import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class RateLimiterServiceTest {
    private Bucket bucket;
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp(){
        RateLimiterConfig rateLimiterConfig = new RateLimiterConfig();
        rateLimiterConfig.setRefill(20);
        rateLimiterConfig.setCapacity(20);
        rateLimiterConfig.setToken(1);
        rateLimiterService = new RateLimiterService(rateLimiterConfig);
        bucket = mock(Bucket.class);
    }

    @Test
    void verifyRunRateLimit() {
        given(bucket.tryConsume(eq(1L))).willReturn(true);
        assertAll(() -> rateLimiterService.runRateLimit(bucket, 1, "too many requests"));
    }

    @Test
    void verifyRunRateLimitException() {
        given(bucket.tryConsume(eq(1L))).willReturn(false);
        assertThrows(TooManyRequestsException.class,() -> rateLimiterService.runRateLimit(bucket, 1, "too many requests"));
    }

}
