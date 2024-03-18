package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.config.RateLimiterConfig;
import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Data
public class RateLimiterService {

    private final Bucket phishingDbBucket;
    private final RateLimiterConfig rateLimiterConfig;

    @Autowired
    public RateLimiterService(RateLimiterConfig rateLimiterConfig) {
        this.rateLimiterConfig = rateLimiterConfig;
        this.phishingDbBucket =  createBucket(rateLimiterConfig.getCapacity(), rateLimiterConfig.getRefill(), rateLimiterConfig.getToken());
    }

    public void runRateLimit(Bucket bucket, int token,String message) throws TooManyRequestsException {
        if(!bucket.tryConsume(token))
            throw new TooManyRequestsException(message);
    }

    private Bucket createBucket(int capacity, int refill, int durationInMinutes){
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(refill, Duration.ofMinutes(durationInMinutes))))
                .build();

    }
}
