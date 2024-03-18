package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.SuccessResponse;
import com.ansaf.shouldiclickthis.service.RateLimiterService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.TimeService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.ansaf.shouldiclickthis.constant.ControllerConstant.DOMAIN_PARAM;
import static com.ansaf.shouldiclickthis.constant.ControllerConstant.LINK_PARAM;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_SET;

@RestController
@RequestMapping(path = "${apiPrefix}")
@Slf4j
public class ApiController {
    @Autowired
    private RedisService redisService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @PostMapping("/domain")
    public SuccessResponse domainSafety(@RequestParam(DOMAIN_PARAM) String domain) throws Exception {
        if(rateLimiterService.getPhishingDbBucket().tryConsume(1)) {
            log.info("Domain verification request started");
            boolean status = redisService.urlContains(DOMAIN_SET, domain);
            LocalDateTime currentTime = timeService.getNowTime();
            log.info("Domain verification request completed");
            return SuccessResponse
                    .builder()
                    .domain(domain)
                    .status(status)
                    .responseTime(currentTime)
                    .build();
        }

        throw new TooManyRequestsException("Too many requests on /api/domain");
    }

    @PostMapping("/link")
    public SuccessResponse linkSafety(@RequestParam(LINK_PARAM) String link) throws TooManyRequestsException {
        if(rateLimiterService.getPhishingDbBucket().tryConsume(1)) {
            boolean status = redisService.urlContains(LINK_SET, link);
            LocalDateTime currentTime = timeService.getNowTime();
            return SuccessResponse
                    .builder()
                    .link(link)
                    .status(status)
                    .responseTime(currentTime)
                    .build();
        }
        throw new TooManyRequestsException("Too many requests on /api/link");
    }
}
