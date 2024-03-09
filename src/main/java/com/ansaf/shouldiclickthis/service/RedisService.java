package com.ansaf.shouldiclickthis.service;

import lombok.extern.slf4j.Slf4j;
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
            if (!urls.isEmpty()) {
                redisTemplate.delete(key);
                redisTemplate.opsForSet().add(key, urls.toArray(new String[0]));
            }
    }

    public boolean urlContains(String key, String value){
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }
}
