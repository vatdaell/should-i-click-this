package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.SuccessResponse;
import com.ansaf.shouldiclickthis.service.RateLimiterService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ansaf.shouldiclickthis.constant.ControllerConstant.DOMAIN_PARAM;
import static com.ansaf.shouldiclickthis.constant.ControllerConstant.LINK_PARAM;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.*;

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
    public SuccessResponse domainSafety(@RequestParam(DOMAIN_PARAM) String domain) throws TooManyRequestsException {
        rateLimiterService.runRateLimit(rateLimiterService.getPhishingDbBucket(), 1, "Too many requests on /api/domain");

        log.info("Domain verification request started");
        boolean status = redisService.urlContains(DOMAIN_SET, domain);
        String lastUpdated = redisService.getString(DOMAIN_UPDATED);
        String currentTime = timeService.getIsoFormatString(timeService.getNowTime());
        log.info("Domain verification request completed");
        return SuccessResponse
                .builder()
                .domain(domain)
                .status(status)
                .responseTime(currentTime)
                .lastUpdated(lastUpdated)
                .build();
    }

    @PostMapping("/link")
    public SuccessResponse linkSafety(@RequestParam(LINK_PARAM) String link) throws TooManyRequestsException {
        rateLimiterService.runRateLimit(rateLimiterService.getPhishingDbBucket(), 1, "Too many requests on /api/link");

        log.info("Link verification request started");
        boolean status = redisService.urlContains(LINK_SET, link);
        String currentTime = timeService.getIsoFormatString(timeService.getNowTime());
        String lastUpdated = redisService.getString(LINK_UPDATED);
        log.info("Link verification request completed");
        return SuccessResponse
                .builder()
                .link(link)
                .status(status)
                .responseTime(currentTime)
                .lastUpdated(lastUpdated)
                .build();
    }


}
