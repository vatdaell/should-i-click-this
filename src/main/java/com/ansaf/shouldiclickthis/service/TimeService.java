package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ansaf.shouldiclickthis.constant.TimeConstant.ISO_DATE_TIME_FORMAT;

@Service
public class TimeService {
    @Autowired
    private TimeUtils timeUtils;

    public LocalDateTime getNowTime(){
        return timeUtils.now();
    }

    public String getIsoFormatString(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT);
        return localDateTime.format(formatter);
    }

}
