package com.ansaf.shouldiclickthis.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeUtils {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
