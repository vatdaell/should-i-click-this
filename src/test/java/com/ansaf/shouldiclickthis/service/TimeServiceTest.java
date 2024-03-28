package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.util.TimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)
public class TimeServiceTest {
    @Mock
    private TimeUtils timeUtils;

    @InjectMocks
    private TimeService timeService;

    @Test
    void verifyIsoStringConversion(){
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 1, 1,1);
        String actual = timeService.getIsoFormatString(localDateTime);
        assertEquals("Date is not in proper iso format","2024-01-01 01:01:01", actual);
    }
}
