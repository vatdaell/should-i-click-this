package com.ansaf.shouldiclickthis.controller;

import static com.ansaf.shouldiclickthis.constant.ControllerConstant.DOMAIN_PARAM;
import static com.ansaf.shouldiclickthis.constant.ControllerConstant.LINK_PARAM;
import static com.ansaf.shouldiclickthis.constant.ControllerConstant.URL_PARAM;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_UPDATED;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.IPSUM_UPDATED;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_UPDATED;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.OPENPHISH_UPDATED;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.SuccessResponse;
import com.ansaf.shouldiclickthis.service.RateLimiterService;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.TimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${apiPrefix}")
@Slf4j
@AllArgsConstructor
public class ApiController {

    private final RedisService redisService;

    private final TimeService timeService;

    private final RateLimiterService rateLimiterService;

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
                .url(domain)
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
                .url(link)
                .status(status)
                .responseTime(currentTime)
                .lastUpdated(lastUpdated)
                .build();
    }

    @PostMapping("/openphish")
    public SuccessResponse openPhishSafety(@RequestParam(LINK_PARAM) String link) throws TooManyRequestsException {
        rateLimiterService.runRateLimit(rateLimiterService.getPhishingDbBucket(), 1, "Too many requests on /api/openphish");
        log.info("OpenPhish verification request started");
        boolean status = redisService.urlContains(OPENPHISH_SET, link);
        String currentTime = timeService.getIsoFormatString(timeService.getNowTime());
        String lastUpdated = redisService.getString(OPENPHISH_UPDATED);
        log.info("OpenPhish verification request completed");

        return SuccessResponse
                .builder()
                .url(link)
                .status(status)
                .responseTime(currentTime)
                .lastUpdated(lastUpdated)
                .build();
    }

    @PostMapping("/ipsum")
    public SuccessResponse ipSumSafety(@RequestParam(URL_PARAM) String ip)
        throws TooManyRequestsException {
        rateLimiterService.runRateLimit(rateLimiterService.getPhishingDbBucket(), 1,
            "Too many requests on /api/ipsum");
        log.info("IpSum verification request started");
        boolean status = redisService.urlContains(IPSUM_SET, ip);
        String currentTime = timeService.getIsoFormatString(timeService.getNowTime());
        String lastUpdated = redisService.getString(IPSUM_UPDATED);
        log.info("IpSum verification request completed");

        return SuccessResponse
            .builder()
            .url(ip)
            .status(status)
            .responseTime(currentTime)
            .lastUpdated(lastUpdated)
            .build();
    }

    @PostMapping("/consolidated")
    public SuccessResponse consolidated(@RequestParam(URL_PARAM) String url)
        throws TooManyRequestsException {
        rateLimiterService.runRateLimit(rateLimiterService.getPhishingDbBucket(), 1,
            "Too many requests on /api/consolidated");

        log.info("Consolidated verification request started");
        boolean status =
            redisService.urlContains(OPENPHISH_SET, url) || redisService.urlContains(DOMAIN_SET,
                url) || redisService.urlContains(LINK_SET, url) || redisService.urlContains(
                IPSUM_SET, url);
        String currentTime = timeService.getIsoFormatString(timeService.getNowTime());
        String lastUpdated = redisService.getString(OPENPHISH_UPDATED);
        log.info("Consolidated verification request completed");

        return SuccessResponse
            .builder()
            .url(url)
            .status(status)
            .responseTime(currentTime)
            .lastUpdated(lastUpdated)
            .build();
    }
}
