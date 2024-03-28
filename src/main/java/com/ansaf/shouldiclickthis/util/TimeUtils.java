package com.ansaf.shouldiclickthis.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ansaf.shouldiclickthis.constant.TimeConstant.ISO_DATE_TIME_FORMAT;

@Component
public class TimeUtils {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
