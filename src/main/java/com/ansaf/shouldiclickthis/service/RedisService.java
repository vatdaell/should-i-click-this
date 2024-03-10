package com.ansaf.shouldiclickthis.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveUrls(String key, List<String> urls) {
        log.info("Starting insertion of Url at set: " + key);
        if (urls != null && !urls.isEmpty()) {
            redisTemplate.delete(key);
            redisTemplate.opsForSet().add(key, urls.toArray(new String[0]));
            log.info("Completed insertion of Url at set: " + key);
        }
        else{
            log.warn("Empty list of urls provided to Redis");
        }
    }

    public boolean urlContains(String key, String value){
        log.info("Starting redis search for url: " + value + " in set: " + key);
        boolean inSet = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        log.info("Completed redis search");
        return inSet;
    }
}
