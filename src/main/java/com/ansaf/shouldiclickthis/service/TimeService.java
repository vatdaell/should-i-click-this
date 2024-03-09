package com.ansaf.shouldiclickthis.service;

import com.ansaf.shouldiclickthis.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeService {
    @Autowired
    TimeUtils timeUtils;

    public LocalDateTime getNowTime(){
        return timeUtils.now();
    }

}
