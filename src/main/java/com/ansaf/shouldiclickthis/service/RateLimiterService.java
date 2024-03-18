package com.ansaf.shouldiclickthis.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Data
public class RateLimiterService {

    private final Bucket phishingDbBucket;

    public RateLimiterService() {
        this.phishingDbBucket =  createBucket(20,20,1);
    }

    private Bucket createBucket(int capacity, int refill, int durationInMinutes){
        return Bucket.builder()
                .addLimit(Bandwidth.classic(capacity, Refill.greedy(refill, Duration.ofMinutes(durationInMinutes))))
                .build();

    }
}
