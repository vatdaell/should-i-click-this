package com.ansaf.shouldiclickthis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveUrls(String key, List<String> urls) {
        // Assuming `key` is the name of the Redis set
        // and `urls` is the list of URLs to add to the set
        if (!urls.isEmpty()) {
            redisTemplate.opsForSet().add(key, urls.toArray(new String[0]));
        }
    }
}
