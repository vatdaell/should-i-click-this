package com.ansaf.shouldiclickthis.service;

import java.util.ArrayList;
import java.util.List;
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

    public void setString(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public String getString(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
