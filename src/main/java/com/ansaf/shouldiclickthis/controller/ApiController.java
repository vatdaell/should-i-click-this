package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.model.SuccessResponse;
import com.ansaf.shouldiclickthis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_SET;

@RestController
public class ApiController {
    @Autowired
    private RedisService redisService;

    @GetMapping("/")
    public String index(){
        return "test";
    }

    @PostMapping("/domain")
    public SuccessResponse domainSafety(@RequestParam("domain") String domain){
        boolean status = redisService.urlContains(DOMAIN_SET, domain);
        LocalDateTime currentTime = LocalDateTime.now();
        return  SuccessResponse
                .builder()
                .domain(domain)
                .status(status)
                .responseTime(currentTime)
                .build();
    }
}
