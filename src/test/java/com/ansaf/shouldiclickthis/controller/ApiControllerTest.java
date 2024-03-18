package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.SuccessResponse;
import com.ansaf.shouldiclickthis.service.RedisService;
import com.ansaf.shouldiclickthis.service.TimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.ansaf.shouldiclickthis.constant.RedisConstant.DOMAIN_SET;
import static com.ansaf.shouldiclickthis.constant.RedisConstant.LINK_SET;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)

public class ApiControllerTest {
    @Mock
    private RedisService redisService;

    @Mock
    private TimeService timeService;

    @InjectMocks
    private ApiController apiController;

    private final String input = "domain.com";

    @Test
    void domainControllerResponseIsStatusSuccess() throws Exception {
        given(redisService.urlContains(DOMAIN_SET, input)).willReturn(true);
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);

        SuccessResponse expected = SuccessResponse
                                    .builder()
                                    .domain(input)
                                    .status(true)
                                    .responseTime(LocalDateTime.MAX)
                                    .build();
        SuccessResponse actual = apiController.domainSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void domainControllerResponseIsNotStatusSuccess() throws Exception {
        given(redisService.urlContains(DOMAIN_SET, input)).willReturn(false);
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);

        SuccessResponse expected = SuccessResponse
                .builder()
                .domain(input)
                .status(false)
                .responseTime(LocalDateTime.MAX)
                .build();
        SuccessResponse actual = apiController.domainSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void linkControllerResponseIsStatusSuccess() throws TooManyRequestsException {
        given(redisService.urlContains(LINK_SET, input)).willReturn(true);
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);

        SuccessResponse expected = SuccessResponse
                .builder()
                .link(input)
                .status(true)
                .responseTime(LocalDateTime.MAX)
                .build();
        SuccessResponse actual = apiController.linkSafety(input);

        assertSuccessResponse(expected, actual);
    }

    @Test
    void linkControllerResponseIsNotStatusSuccess() throws TooManyRequestsException {
        given(redisService.urlContains(LINK_SET, input)).willReturn(false);
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);

        SuccessResponse expected = SuccessResponse
                .builder()
                .link(input)
                .status(false)
                .responseTime(LocalDateTime.MAX)
                .build();
        SuccessResponse actual = apiController.linkSafety(input);

        assertSuccessResponse(expected, actual);
    }

    private void assertSuccessResponse(SuccessResponse expected, SuccessResponse actual){
        assertEquals("Domain is not the same", expected.getDomain(), actual.getDomain());
        assertEquals("Status is not the same", expected.isStatus(), actual.isStatus());
        assertEquals("Response time is not the same", expected.getResponseTime(), actual.getResponseTime());
        assertEquals("Link is not the same", expected.getLink(), actual.getLink());
    }
}
