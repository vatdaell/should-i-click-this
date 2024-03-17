package com.ansaf.shouldiclickthis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveUrlsInChunks(String key, List<String> urls, int chunkSize) {
        List<List<String>> chunks = splitIntoChunks(urls, chunkSize);
        redisTemplate.delete(key);

        for (int i=0; i<chunks.size(); i++){
            List<String> chunk = chunks.get(i);
            if(!chunk.isEmpty()){
                log.info("Starting insertion of Url at chunk {}/{}", (i+1), chunkSize);
                redisTemplate.opsForSet().add(key, chunk.toArray(new String[0]));
                log.info("Completed insertion of Url at chunk {}/{}", (i+1), chunkSize);
            }
        }
    }

    public boolean urlContains(String key, String value){
        log.info("Starting redis search for url: {} in set: {}", value, key);
        boolean inSet = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
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
}
