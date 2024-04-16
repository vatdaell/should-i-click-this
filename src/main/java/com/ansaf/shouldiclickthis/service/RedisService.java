package com.ansaf.shouldiclickthis.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void saveValuesInChunks(String key, List<String> values, int chunkSize) {
        List<List<String>> chunks = splitIntoChunks(values, chunkSize);
        redisTemplate.delete(key);

        for (int i=0; i<chunks.size(); i++){
            List<String> chunk = chunks.get(i);
            if(!chunk.isEmpty()){
                log.info("Starting insertion of values at chunk {}/{}", (i + 1), chunkSize);
                redisTemplate.opsForSet().add(key, chunk.toArray(new String[0]));
                log.info("Completed insertion of values at chunk {}/{}", (i + 1), chunkSize);
            }
        }
    }

    public boolean setContains(String key, String value) {
        log.info("Starting redis search for value: {} in set: {}", value, key);
        boolean inSet = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        if (inSet) {
            log.info("Found value: {} in set: {}", value, key);
        }
        log.info("Completed redis search");
        return inSet;
    }

    private <T> List<List<T>> splitIntoChunks(List<T> list, int chunks) {
        List<List<T>> chunkedList = new ArrayList<>();
        int chunkSize = (int)Math.ceil((double)list.size() / chunks);

        for (int i = 0; i < list.size(); i += chunkSize) {
            chunkedList.add(new ArrayList<>(list.subList(i, Math.min(list.size(), i + chunkSize))));
        }

        return chunkedList;
    }

    public void setValueWithExpiry(String key, boolean value, int min) {
        String stringValue = Boolean.toString(value);
        log.info("Setting value for key: {} without immediate expiry", key);

        redisTemplate.opsForValue().set(key, stringValue);
        redisTemplate.expire(key, min, TimeUnit.MINUTES);

        log.info("Expiry for key: {} set to 10 minutes", key);
    }

    public Optional<Boolean> getValueAsBoolean(String key) {
        log.info("Retrieving value for key: {}", key);

        // Retrieve the value as String
        String value = redisTemplate.opsForValue().get(key);

        if (value != null) {
            log.info("Retrieved value for key: {} is [{}]", key, value);
            // Convert the String value to boolean and return
            return Optional.of(Boolean.parseBoolean(value));
        } else {
            log.info("No value found for key: {}", key);
            // Return Optional.empty() if no value found
            return Optional.empty();
        }
    }


    public void setSetString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getSetString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
